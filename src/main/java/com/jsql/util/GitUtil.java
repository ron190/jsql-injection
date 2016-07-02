package com.jsql.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;

public class GitUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(GitUtil.class);
    
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
            String pageSource = ConnectionUtil.getSource("https://raw.githubusercontent.com/ron190/jsql-injection/master/.version");
            
            Float gitVersion = Float.parseFloat(pageSource);
            if (gitVersion > Float.parseFloat(InjectionModel.VERSION_JSQL)) {
                LOGGER.warn(I18n.get("UPDATE_NEW_VERSION_AVAILABLE"));
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn(I18n.get("UPDATE_EXCEPTION"));
            LOGGER.error(e, e);
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
            + "Strategy: "+( MediatorModel.model().getStrategy() != null ? MediatorModel.model().getStrategy().getValue().getName() : "undefined" )+"\n"
            + "Db engine: "+ MediatorModel.model().currentVendor.toString() +"\n"
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
    
    public static void sendReport(String reportBody, ShowOnConsole showOnConsole, String reportTitle) {
        // Test if proxy is available then apply settings
        if (ProxyUtil.isUsingProxy && !"".equals(ProxyUtil.proxyAddress) && !"".equals(ProxyUtil.proxyPort)) {
            try {
                if (showOnConsole == ShowOnConsole.YES) {
                    LOGGER.info("Testing proxy...");
                }
                
                new Socket(ProxyUtil.proxyAddress, Integer.parseInt(ProxyUtil.proxyPort)).close();
            } catch (Exception e) {
                if (showOnConsole == ShowOnConsole.YES) {
                    LOGGER.warn(
                        "Proxy connection failed: " 
                        + ProxyUtil.proxyAddress + ":" + ProxyUtil.proxyPort
                        + ". Please check your proxy informations or disable proxy setting.",
                        e
                    );
                }
                return;
            }
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.trace("Proxy is responding.");
            }
        }

        HttpURLConnection connection = null;
        try {
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.info("Sending report...");
            }
            
            URL githubUrl = new URL("https://api.github.com/repos/ron190/jsql-injection/issues");
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
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
            dataOut.writeBytes(
                new JSONObject()
                    .put("title", reportTitle)
                    .put("body", new String(reportBody.getBytes("UTF-8")))
                    .toString()
            );
            dataOut.flush();
            dataOut.close();
            
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.debug("Report sent successfully.");
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                while (reader.readLine() != null) {
                    // nothing
                }
            } catch (IOException e) {
                if (showOnConsole == ShowOnConsole.YES) {
                    LOGGER.warn("Read error " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.warn("Error during Git report connection " + e.getMessage(), e);
            }
        }
    }
}

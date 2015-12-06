package com.jsql.tool;

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
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;

public class GitTools {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(GitTools.class);
    
    public enum DisplayLog {YES, NO}; 

    /**
     * Utility class.
     */
    private GitTools() {
        //not called
    }

    public static void checkVersion() {
        try {
            URLConnection con = new URL("https://raw.githubusercontent.com/ron190/jsql-injection/master/.version").openConnection();
            con.setReadTimeout(60000);
            con.setConnectTimeout(60000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();
            
            Float gitVersion = Float.parseFloat(pageSource);
            if (gitVersion > Float.parseFloat(InjectionModel.JSQLVERSION)) {
                LOGGER.warn(I18n.UPDATE_NEW_VERSION_AVAILABLE);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn(I18n.UPDATE_EXCEPTION);
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.warn(I18n.UPDATE_EXCEPTION);
            LOGGER.error(e, e);
        }
    }
    
    public static void sendUnhandledException(String tname, Throwable thrown) {
        String clientDescription = 
              "```\n"
            + "jSQL version: " + InjectionModel.JSQLVERSION +"\n"
            + "Java version: " + System.getProperty("java.version").substring(0, 3) +"\n"
            + "Operating system: " + System.getProperty("os.name") +"\n"
            + "Strategy: " + MediatorModel.model().injectionStrategy.getName() +"\n"
            + "Db engine: " + MediatorModel.model().sqlStrategy.getDbLabel() +"\n"
            + "```\n"
            + "```\n"
            + "Exception on " + tname +"\n"
            + ExceptionUtils.getStackTrace(thrown).trim() +"\n"
            + "```";
        
        clientDescription = clientDescription.replaceAll("(http://[.a-zA-Z_0-9]*)+", "");
          
        GitTools.sendReport(clientDescription, DisplayLog.NO);
    }
    
    public static void sendReport(String reportBody) {
        GitTools.sendReport(reportBody, DisplayLog.YES);
    }
    
    public static void sendReport(String reportBody, DisplayLog shouldDisplayLog) {
        // Test if proxy is available then apply settings
        if (MediatorModel.model().isProxyfied && !"".equals(MediatorModel.model().proxyAddress) && !"".equals(MediatorModel.model().proxyPort)) {
            try {
                if (shouldDisplayLog == DisplayLog.YES) {
                    LOGGER.info("Testing proxy...");
                }
                
                new Socket(MediatorModel.model().proxyAddress, Integer.parseInt(MediatorModel.model().proxyPort)).close();
            } catch (Exception e) {
                if (shouldDisplayLog == DisplayLog.YES) {
                    LOGGER.warn("Proxy connection failed: " 
                        + MediatorModel.model().proxyAddress + ":" + MediatorModel.model().proxyPort
                        + "\nVerify your proxy informations or disable proxy setting.", e);
                }
                return;
            }
            if (shouldDisplayLog == DisplayLog.YES) {
                LOGGER.trace("Proxy is responding.");
            }
        }

        HttpURLConnection connection = null;
        try {
            if (shouldDisplayLog == DisplayLog.YES) {
                LOGGER.info("Sending report...");
            }
            
            URL githubUrl = new URL("https://api.github.com/repos/ron190/jsql-injection/issues");
//            URL githubUrl = new URL("https://api.github.com/repos/ron190/test-issues/issues");
            connection = (HttpURLConnection) githubUrl.openConnection();
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "token " + StringUtils.newStringUtf8(Base64.decodeBase64("NGQ1YzdkYWE1NDQwYzdkNTk1YTZlODQzYzFlODlkZmMzNzQ1NDhlNg==")));
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
            dataOut.writeBytes(
                new JSONObject()
                    .put("title", "Report")
                    .put("body", new String(reportBody.getBytes("UTF-8")))
                    .toString()
            );
            dataOut.flush();
            dataOut.close();
            
            if (shouldDisplayLog == DisplayLog.YES) {
                LOGGER.debug("Report sent successfully.");
            }
            
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while (reader.readLine() != null);
                reader.close();
//                System.out.println(pageSource);
            } catch (IOException e) {
                if (shouldDisplayLog == DisplayLog.YES) {
                    LOGGER.warn("Read error " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            if (shouldDisplayLog == DisplayLog.YES) {
                LOGGER.warn("Error during Git report connection " + e.getMessage(), e);
            }
        }
    }
}

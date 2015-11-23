package com.jsql;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.swing.JFrameGUI;
import com.jsql.view.swing.MediatorGUI;

public class MainApplication {
    /**
     * Using default log4j.properties from root /
     */
    public static final Logger LOGGER = Logger.getLogger(MainApplication.class);
    
    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        
        public void handle(Throwable thrown) {
            // for EDT exceptions
            handleException(Thread.currentThread().getName(), thrown);
        }
        
        public void uncaughtException(Thread thread, Throwable thrown) {
            // for other uncaught exceptions
            handleException(thread.getName(), thrown);
        }
        
        protected void handleException(String tname, Throwable thrown) {
            LOGGER.error("Exception on " + tname, thrown);
//            
//            if (thrown != null && thrown..getThrowableStrRep() != null) {
//                for (String rep: throwableInformation.getThrowableStrRep()) {
//                    javaConsole.append(rep, ERROR);
//                    msgComplete += rep;
//                }
//            }
            
            
            if (MediatorGUI.model().reportBugs) {
             // Test if proxy is available then apply settings
                if (MediatorGUI.model().isProxyfied && !"".equals(MediatorGUI.model().proxyAddress) && !"".equals(MediatorGUI.model().proxyPort)) {
                    try {
    //                    LOGGER.info("Testing proxy...");
                        new Socket(MediatorGUI.model().proxyAddress, Integer.parseInt(MediatorGUI.model().proxyPort)).close();
                    } catch (Exception e) {
    //                    LOGGER.warn("Proxy connection failed: " 
    //                            + MediatorGUI.model().proxyAddress + ":" + MediatorGUI.model().proxyPort
    //                            + "\nVerify your proxy informations or disable proxy setting.", e);
                        return;
                    }
    //                LOGGER.trace("Proxy is responding.");
                }
    
                HttpURLConnection connection = null;
                try {
    //                LOGGER.info("Sending report...");
                    URL githubUrl = new URL("https://api.github.com/repos/ron190/jsql-injection/issues");
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
                        "{\"title\": \"Report\", \"body\": \""+ StringEscapeUtils.escapeJson(
                            ("Exception on " + tname +"\n"+ ExceptionUtils.getStackTrace(thrown))
                                .replaceAll("(http://[.a-zA-Z_0-9]*)+", "")
                                .replaceAll("(\\r|\\n)+", "\\\\n")
                    ) +"\"}");
                    dataOut.flush();
                    dataOut.close();
                } catch (IOException e) {
    //                LOGGER.warn("Error during JSON connection " + e.getMessage(), e);
                }
    
                // Request the web page to the server
                String line, pageSource = "";
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        pageSource += line + "\r\n";
                    }
                    reader.close();
                    
    //                LOGGER.debug("Report sent successfully.");
//                    System.out.println(pageSource);
                } catch (MalformedURLException e) {
    //                LOGGER.warn("Malformed URL " + e.getMessage(), e);
                } catch (IOException e) {
                    /* lot of timeout in local use */
    //                LOGGER.warn("Read error " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     */
    public static void main(String[] args) {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
                System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
                
                InjectionModel model = new InjectionModel();
                MediatorModel.register(model);
                MediatorGUI.register(model);
                MediatorGUI.register(new JFrameGUI());
                model.instanciationDone();
            }
        });
        
    }
}

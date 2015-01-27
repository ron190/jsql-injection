package com.jsql;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

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

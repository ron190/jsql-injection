package com.jsql.util;

import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * SSL certificates are used by https connection. This utility class
 * gets rid of malformed certification chains from bad configured websites
 * in order to ignore connection exception in that specific case.
 */
public class CertificateUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    // Utility class
    private CertificateUtil() {
        // not called
    }
    
    /**
     * Configure a fake SSL context in order to ignore malformed certificate.
     */
    public static void ignoreCertificationChain() {
        // Create a trust manager that does not validate certificate chains
        // and ignore exception PKIX path building failed: unable to find valid certification path to requested target
        TrustManager[] trustAllCerts = new TrustManager[] {
                
            new X509TrustManager() {
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                
                @Override
                public void checkClientTrusted(
                    X509Certificate[] certs, String authType
                ) {
                    // Ignore
                }
                
                @Override
                public void checkServerTrusted(
                    X509Certificate[] certs, String authType
                ) {
                    // Ignore
                }
                
            }
            
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LOGGER.warn("Error ignoring untrusted SSL: "+ e.getMessage(), e);
        }
        
        // Ignore CertificateException: No subject alternative names present
        HttpsURLConnection.setDefaultHostnameVerifier((String hostname, SSLSession sslSession) -> true);
    }
    
}

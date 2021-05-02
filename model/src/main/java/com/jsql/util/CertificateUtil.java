package com.jsql.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SSL certificates are used by https connection. This utility class
 * gets rid of malformed certification chains from bad configured websites
 * in order to ignore connection exception in that specific case.
 */
public class CertificateUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private SSLContext sslContext = null;
    
    // Utility class
    public CertificateUtil() {
        
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        
        // Create a trust manager that does not validate certificate chains
        // and ignore exception PKIX path building failed: unable to find valid certification path to requested target
        var trustAllCerts = new TrustManager[] {
                
            new X509TrustManager() {
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    
                    return new X509Certificate[0];
                }
                
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    
                    if (StringUtils.EMPTY.equals(StringUtils.SPACE)) {
                        
                        throw new IllegalArgumentException();
                    }
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    
                    if (StringUtils.EMPTY.equals(StringUtils.SPACE)) {
                        
                        throw new IllegalArgumentException();
                    }
                }
            }
        };
        
        try {
            this.sslContext = SSLContext.getInstance("TLSv1.2");
            this.sslContext.init(null, trustAllCerts, new SecureRandom());
            
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Error ignoring untrusted SSL: %s", e.getMessage()),
                e
            );
        }
    }
    
    public SSLContext getSslContext() {
        
        return this.sslContext;
    }
}

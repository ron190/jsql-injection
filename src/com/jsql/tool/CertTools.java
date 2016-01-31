package com.jsql.tool;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CertTools {
    /**
     * Utility class.
     */
    private CertTools() {
        //not called
    }
    
    public static void ignoreCertificationChain() {
        // Create a trust manager that does not validate certificate chains
        // and ignore exception PKIX path building failed: unable to find valid certification path to requested target
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                    // nothing
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                    // nothing
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            // nothing
        }
        
        // Ignore CertificateException: No subject alternative names present
        HttpsURLConnection.setDefaultHostnameVerifier(
            new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession sslSession) {
                    return true;
                }
            }
        );
    }
}

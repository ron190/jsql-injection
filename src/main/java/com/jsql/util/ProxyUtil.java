package com.jsql.util;

import java.net.Socket;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.PreparationException;

public class ProxyUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ProxyUtil.class);
    
    /**
     * Proxy address.
     */
    public static String proxyAddress;
    
    /**
     * Proxy port.
     */
    public static String proxyPort;
    
    /**
     * True if connection is proxified.
     */
    public static boolean isUsingProxy = false;
    
    /**
     * Utility class.
     */
    private ProxyUtil() {
        //not called
    }
    
    /**
     * Save configuration into preferences.
     * @param isUsingProxy
     * @param proxyAddress
     * @param proxyPort
     */
    public static void set(boolean isUsingProxy, String proxyAddress, String proxyPort) {
        // Define proxy settings
        ProxyUtil.isUsingProxy = isUsingProxy;
        ProxyUtil.proxyAddress = proxyAddress;
        ProxyUtil.proxyPort = proxyPort;

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("isUsingProxy", ProxyUtil.isUsingProxy);
        prefs.put("proxyAddress", ProxyUtil.proxyAddress);
        prefs.put("proxyPort", ProxyUtil.proxyPort);

        if (ProxyUtil.isUsingProxy) {
            System.setProperty("http.proxyHost", ProxyUtil.proxyAddress);
            System.setProperty("http.proxyPort", ProxyUtil.proxyPort);
        } else {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        }
    }
    
    /**
     * Initialize proxy information from previously saved configuration.
     */
    public static void setProxy() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        ProxyUtil.isUsingProxy = prefs.getBoolean("isUsingProxy", false);

        // Default TOR config
        ProxyUtil.proxyAddress = prefs.get("proxyAddress", "127.0.0.1");
        ProxyUtil.proxyPort = prefs.get("proxyPort", "8118");
        
        if (ProxyUtil.isUsingProxy) {
            System.setProperty("http.proxyHost", ProxyUtil.proxyAddress);
            System.setProperty("http.proxyPort", ProxyUtil.proxyPort);
        }
    }
    
    public static void testProxy() throws PreparationException {
        // Test if proxy is available then apply settings
        if (
            ProxyUtil.isUsingProxy && 
            !"".equals(ProxyUtil.proxyAddress) && 
            !"".equals(ProxyUtil.proxyPort)
        ) {
            try {
                LOGGER.info("Testing proxy...");
                new Socket(ProxyUtil.proxyAddress, Integer.parseInt(ProxyUtil.proxyPort)).close();
            } catch (Exception e) {
                /**
                 * TODO Preparation Proxy Exception
                 */
                throw new PreparationException(
                    "Proxy connection failed: "
                    + ProxyUtil.proxyAddress + ":" + ProxyUtil.proxyPort + ". "
                    + "Please check your proxy informations or disable proxy setting."
                );
            }
            LOGGER.debug("Proxy is responding.");
        }
    }
    
}

package com.jsql.util;

import java.net.Socket;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;

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
    public static boolean useProxy = false;
    
    /**
     * Utility class.
     */
    private ProxyUtil() {
        //not called
    }
    
    /**
     * Save configuration into preferences.
     * @param useProxy
     * @param proxyAddress
     * @param proxyPort
     */
    public static void set(boolean useProxy, String proxyAddress, String proxyPort) {
        // Define proxy settings
        ProxyUtil.useProxy = useProxy;
        ProxyUtil.proxyAddress = proxyAddress;
        ProxyUtil.proxyPort = proxyPort;

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("useProxy", ProxyUtil.useProxy);
        prefs.put("proxyAddress", ProxyUtil.proxyAddress);
        prefs.put("proxyPort", ProxyUtil.proxyPort);

        if (ProxyUtil.useProxy) {
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
    public static void initializeProxy() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        ProxyUtil.useProxy = prefs.getBoolean("useProxy", false);

        // Default TOR config
        ProxyUtil.proxyAddress = prefs.get("proxyAddress", "127.0.0.1");
        ProxyUtil.proxyPort = prefs.get("proxyPort", "8118");
        

        if (ProxyUtil.useProxy) {
            System.setProperty("http.proxyHost", ProxyUtil.proxyAddress);
            System.setProperty("http.proxyPort", ProxyUtil.proxyPort);
        }
    }
    
    public static void check() throws PreparationException {
        // Test if proxy is available then apply settings
        if (useProxy && !"".equals(proxyAddress) && !"".equals(proxyPort)) {
            try {
                LOGGER.info("Testing proxy...");
                new Socket(proxyAddress, Integer.parseInt(proxyPort)).close();
            } catch (Exception e) {
                /**
                 * TODO Preparation Proxy Exception
                 */
                throw new PreparationException("Proxy connection failed: " + proxyAddress + ":" + proxyPort
                        + ". Please check your proxy informations or disable proxy setting.");
            }
            LOGGER.debug("Proxy is responding.");
        }
    }
    
}

package com.jsql.util;

import java.net.Socket;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.util.GitUtil.ShowOnConsole;

public class ProxyUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ProxyUtil.class);
    
    /**
     * Proxy address.
     */
    private static String proxyAddress;

    /**
     * Proxy port.
     */
    private static String proxyPort;
    
    /**
     * True if connection is proxified.
     */
    private static boolean isUsingProxy = false;
    
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
        ProxyUtil.setUsingProxy(isUsingProxy);
        ProxyUtil.setProxyAddress(proxyAddress);
        ProxyUtil.setProxyPort(proxyPort);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("isUsingProxy", ProxyUtil.isUsingProxy());
        prefs.put("proxyAddress", ProxyUtil.getProxyAddress());
        prefs.put("proxyPort", ProxyUtil.getProxyPort());

        if (ProxyUtil.isUsingProxy()) {
            System.setProperty("http.proxyHost", ProxyUtil.getProxyAddress());
            System.setProperty("http.proxyPort", ProxyUtil.getProxyPort());
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
        ProxyUtil.setUsingProxy(prefs.getBoolean("isUsingProxy", false));

        // Default TOR config
        ProxyUtil.setProxyAddress(prefs.get("proxyAddress", "127.0.0.1"));
        ProxyUtil.setProxyPort(prefs.get("proxyPort", "8118"));
        
        if (ProxyUtil.isUsingProxy()) {
            System.setProperty("http.proxyHost", ProxyUtil.getProxyAddress());
            System.setProperty("http.proxyPort", ProxyUtil.getProxyPort());
        }
    }
    
    public static boolean proxyIsResponding() {
        return ProxyUtil.proxyIsResponding(ShowOnConsole.YES);
    }
    
    public static boolean proxyIsResponding(ShowOnConsole isErrorDisplayed) {
        boolean proxyIsResponding = true;
        
        if (
            ProxyUtil.isUsingProxy() && 
            !"".equals(ProxyUtil.getProxyAddress()) && 
            !"".equals(ProxyUtil.getProxyPort())
        ) {
            try {
                new Socket(ProxyUtil.getProxyAddress(), Integer.parseInt(ProxyUtil.getProxyPort())).close();
            } catch (Exception e) {
                proxyIsResponding = false;
                
                if (isErrorDisplayed == ShowOnConsole.YES) {
                    LOGGER.warn(
                        "Connection to proxy "+ ProxyUtil.toStr() +" failed: "+ e +". Verify your proxy settings"
                    );
                }
            }
        }
        
        return proxyIsResponding;
    }
    
    public static String getProxyAddress() {
        return proxyAddress;
    }

    public static void setProxyAddress(String proxyAddress) {
        ProxyUtil.proxyAddress = proxyAddress;
    }

    public static String getProxyPort() {
        return proxyPort;
    }

    public static void setProxyPort(String proxyPort) {
        ProxyUtil.proxyPort = proxyPort;
    }

    public static boolean isUsingProxy() {
        return isUsingProxy;
    }

    public static void setUsingProxy(boolean isUsingProxy) {
        ProxyUtil.isUsingProxy = isUsingProxy;
    }    
    
    private static String toStr() {
        return ProxyUtil.getProxyAddress() + ":" + ProxyUtil.getProxyPort();
    }
}

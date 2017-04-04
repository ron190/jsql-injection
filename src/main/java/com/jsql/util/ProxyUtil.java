package com.jsql.util;

import java.net.Socket;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.util.GitUtil.ShowOnConsole;

/**
 * Utility class managing proxy settings.
 * The proxy configuration is saved as preferences and applied to the JVM.
 */
public class ProxyUtil {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Proxy IP address or name.
     */
    private static String proxyAddress;

    /**
     * Proxy port number.
     */
    private static String proxyPort;
    
    /**
     * True if connection is proxified.
     */
    private static boolean isUsingProxy = false;
    
    // Utility class
    private ProxyUtil() {
        // not called
    }
    
    /**
     * Save proxy configuration into the JVM preferences.
     * @param isUsingProxy wether the connection is using a proxy
     * @param proxyAddress IP address or name of the proxy
     * @param proxyPort port number of proxy
     */
    public static void set(boolean isUsingProxy, String proxyAddress, String proxyPort) {
    	
        // Set the application proxy settings
        ProxyUtil.setUsingProxy(isUsingProxy);
        ProxyUtil.setProxyAddress(proxyAddress);
        ProxyUtil.setProxyPort(proxyPort);

        // Save the settings in the JVM
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("isUsingProxy", ProxyUtil.isUsingProxy());
        prefs.put("proxyAddress", ProxyUtil.getProxyAddress());
        prefs.put("proxyPort", ProxyUtil.getProxyPort());

        // Change the JVM configuration
        if (ProxyUtil.isUsingProxy()) {
            System.setProperty("http.proxyHost", ProxyUtil.getProxyAddress());
            System.setProperty("http.proxyPort", ProxyUtil.getProxyPort());
        } else {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        }
        
    }
    
    /**
     * Initialize proxy information from JVM already saved preferences.
     */
    public static void setProxy() {
    	
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        ProxyUtil.setUsingProxy(prefs.getBoolean("isUsingProxy", false));

        // Default TOR config
        ProxyUtil.setProxyAddress(prefs.get("proxyAddress", "localhost"));
        ProxyUtil.setProxyPort(prefs.get("proxyPort", "8118"));
        
        // Change the JVM configuration
        if (ProxyUtil.isUsingProxy()) {
            System.setProperty("http.proxyHost", ProxyUtil.getProxyAddress());
            System.setProperty("http.proxyPort", ProxyUtil.getProxyPort());
        }
        
    }
    
    /**
     * Check if the proxy is up.
     * @param showOnConsole wether the message should be presented to the user
     * @return true if the proxy is up
     */
    public static boolean proxyIsResponding(ShowOnConsole showOnConsole) {
    	
        boolean proxyIsResponding = true;
        
        if (
            ProxyUtil.isUsingProxy() && 
            !"".equals(ProxyUtil.getProxyAddress()) && 
            !"".equals(ProxyUtil.getProxyPort())
        ) {
            try {
            	Socket socket = new Socket(ProxyUtil.getProxyAddress(), Integer.parseInt(ProxyUtil.getProxyPort()));
            	socket.close();
            } catch (Exception e) {
                proxyIsResponding = false;
                
                if (showOnConsole == ShowOnConsole.YES) {
                    LOGGER.warn(
                        "Connection to proxy "+ ProxyUtil.getProxyAddress() +":"+ ProxyUtil.getProxyPort() +" failed: "+ e +". Verify your proxy settings.", e
                    );
                }
            }
        }
        
        return proxyIsResponding;
        
    }
    
    // Getters and setters
    
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
    
}

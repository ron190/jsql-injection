package com.jsql.util;

import java.net.Socket;
import java.util.Optional;
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
    
    private static final String HTTP_PROXY_DEFAULT_ADDRESS = PropertiesUtil.getInstance().getProperties().getProperty("http.proxy.default.ip");
    private static final String HTTP_PROXY_DEFAULT_PORT = PropertiesUtil.getInstance().getProperties().getProperty("http.proxy.default.port");
    private static final String HTTPS_PROXY_DEFAULT_ADDRESS = PropertiesUtil.getInstance().getProperties().getProperty("https.proxy.default.ip");
    private static final String HTTPS_PROXY_DEFAULT_PORT = PropertiesUtil.getInstance().getProperties().getProperty("https.proxy.default.port");
    
    /**
     * Proxy IP address or name.
     */
    private static String proxyAddress;
    private static String proxyAddressHttps;

    /**
     * Proxy port number.
     */
    private static String proxyPort;
    private static String proxyPortHttps;
    
    /**
     * True if connection is proxified.
     */
    private static boolean isUsingProxy = false;
    private static boolean isUsingProxyHttps = false;
    
    private static final String PROPERTIES_HTTP_PROXY_HOST = "http.proxyHost";
    private static final String PROPERTIES_HTTP_PROXY_PORT = "http.proxyPort";
    private static final String PROPERTIES_HTTPS_PROXY_HOST = "https.proxyHost";
    private static final String PROPERTIES_HTTPS_PROXY_PORT = "https.proxyPort";
    
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
    public static void set(
        boolean isUsingProxy,
        String proxyAddress,
        String proxyPort,
        boolean isUsingProxyHttps,
        String proxyAddressHttps,
        String proxyPortHttps
    ) {
    	
        // Set the application proxy settings
        ProxyUtil.setUsingProxy(isUsingProxy);
        ProxyUtil.setProxyAddress(proxyAddress);
        ProxyUtil.setProxyPort(proxyPort);
        
        ProxyUtil.setUsingProxyHttps(isUsingProxyHttps);
        ProxyUtil.setProxyAddressHttps(proxyAddressHttps);
        ProxyUtil.setProxyPortHttps(proxyPortHttps);

        // Save the settings in the JVM
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("isUsingProxy", ProxyUtil.isUsingProxy());
        prefs.put("proxyAddress", ProxyUtil.getProxyAddress());
        prefs.put("proxyPort", ProxyUtil.getProxyPort());
        
        prefs.putBoolean("isUsingProxyHttps", ProxyUtil.isUsingProxyHttps());
        prefs.put("proxyAddressHttps", ProxyUtil.getProxyAddressHttps());
        prefs.put("proxyPortHttps", ProxyUtil.getProxyPortHttps());

        // Change the JVM configuration
        if (ProxyUtil.isUsingProxy()) {
            System.setProperty(PROPERTIES_HTTP_PROXY_HOST, ProxyUtil.getProxyAddress());
            System.setProperty(PROPERTIES_HTTP_PROXY_PORT, ProxyUtil.getProxyPort());
        } else {
            System.setProperty(PROPERTIES_HTTP_PROXY_HOST, "");
            System.setProperty(PROPERTIES_HTTP_PROXY_PORT, "");
        }
        
        if (ProxyUtil.isUsingProxyHttps()) {
            System.setProperty(PROPERTIES_HTTPS_PROXY_HOST, ProxyUtil.getProxyAddressHttps());
            System.setProperty(PROPERTIES_HTTPS_PROXY_PORT, ProxyUtil.getProxyPortHttps());
        } else {
            System.setProperty(PROPERTIES_HTTPS_PROXY_HOST, "");
            System.setProperty(PROPERTIES_HTTPS_PROXY_PORT, "");
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
        
        ProxyUtil.setUsingProxyHttps(prefs.getBoolean("isUsingProxyHttps", false));

        // Default TOR config
        ProxyUtil.setProxyAddress(prefs.get("proxyAddress", HTTP_PROXY_DEFAULT_ADDRESS));
        ProxyUtil.setProxyPort(prefs.get("proxyPort", HTTP_PROXY_DEFAULT_PORT));
        
        ProxyUtil.setProxyAddressHttps(prefs.get("proxyAddressHttps", HTTPS_PROXY_DEFAULT_ADDRESS));
        ProxyUtil.setProxyPortHttps(prefs.get("proxyPortHttps", HTTPS_PROXY_DEFAULT_PORT));
        
        // Change the JVM configuration
        if (ProxyUtil.isUsingProxy()) {
            System.setProperty(PROPERTIES_HTTP_PROXY_HOST, ProxyUtil.getProxyAddress());
            System.setProperty(PROPERTIES_HTTP_PROXY_PORT, ProxyUtil.getProxyPort());
        }
        
        if (ProxyUtil.isUsingProxyHttps()) {
            System.setProperty(PROPERTIES_HTTPS_PROXY_HOST, ProxyUtil.getProxyAddressHttps());
            System.setProperty(PROPERTIES_HTTPS_PROXY_PORT, ProxyUtil.getProxyPortHttps());
        }
        
    }
    
    /**
     * Check if the proxy is up.
     * @param showOnConsole wether the message should be presented to the user
     * @return true if the proxy is up
     */
    public static boolean isLive(ShowOnConsole showOnConsole) {
    	
        boolean proxyIsChecked = true;
        
        if (
            ProxyUtil.isUsingProxy()
            && !"".equals(ProxyUtil.getProxyAddress())
            && !"".equals(ProxyUtil.getProxyPort())
        ) {
            try {
            	Socket socket = new Socket(ProxyUtil.getProxyAddress(), Integer.parseInt(ProxyUtil.getProxyPort()));
            	socket.close();
            	
            	if (showOnConsole == ShowOnConsole.YES) {
            	    LOGGER.debug("Connection to HTTP proxy "+ ProxyUtil.getProxyAddress() +":"+ ProxyUtil.getProxyPort() +" successful");
            	}
            } catch (Exception e) {
                proxyIsChecked = false;
                
                if (showOnConsole == ShowOnConsole.YES) {
                    String message = Optional.ofNullable(e.getMessage()).orElse("");
                    LOGGER.warn(
                        "Connection to HTTP proxy "
                        + ProxyUtil.getProxyAddress() +":"
                        + ProxyUtil.getProxyPort()
                        +" failed with error \""+ message.replace(e.getClass().getName() +": ", "") +"\", verify your proxy settings for HTTP protocol",
                        e
                    );
                }
            }
        }

        if (
            ProxyUtil.isUsingProxyHttps()
            && !"".equals(ProxyUtil.getProxyAddressHttps())
            && !"".equals(ProxyUtil.getProxyPortHttps())
        ) {
            try {
                Socket socket = new Socket(ProxyUtil.getProxyAddressHttps(), Integer.parseInt(ProxyUtil.getProxyPortHttps()));
                socket.close();
                
                if (showOnConsole == ShowOnConsole.YES) {
                    LOGGER.debug("Connection to HTTPS proxy "+ ProxyUtil.getProxyAddressHttps() +":"+ ProxyUtil.getProxyPortHttps() +" successful");
                }
            } catch (Exception e) {
                proxyIsChecked = false;
                
                if (showOnConsole == ShowOnConsole.YES) {
                    String message = Optional.ofNullable(e.getMessage()).orElse("");
                    LOGGER.warn(
                        "Connection to HTTPS proxy "+ ProxyUtil.getProxyAddressHttps() +":"+ ProxyUtil.getProxyPortHttps() +" failed: "+ message.replace(e.getClass().getName() +": ", "") +", verify your proxy settings for HTTPS protocol", e
                    );
                }
            }
        }
        
        return proxyIsChecked;
        
    }
    
    // Getters and setters
    
    public static String getProxyAddress() {
        return ProxyUtil.proxyAddress;
    }

    public static void setProxyAddress(String proxyAddress) {
        ProxyUtil.proxyAddress = proxyAddress;
    }

    public static String getProxyPort() {
        return ProxyUtil.proxyPort;
    }

    public static void setProxyPort(String proxyPort) {
        ProxyUtil.proxyPort = proxyPort;
    }

    public static boolean isUsingProxy() {
        return ProxyUtil.isUsingProxy;
    }

    public static void setUsingProxy(boolean isUsingProxy) {
        ProxyUtil.isUsingProxy = isUsingProxy;
    }

    public static String getProxyAddressHttps() {
        return ProxyUtil.proxyAddressHttps;
    }

    public static void setProxyAddressHttps(String proxyAddressHttps) {
        ProxyUtil.proxyAddressHttps = proxyAddressHttps;
    }

    public static String getProxyPortHttps() {
        return ProxyUtil.proxyPortHttps;
    }

    public static void setProxyPortHttps(String proxyPortHttps) {
        ProxyUtil.proxyPortHttps = proxyPortHttps;
    }

    public static boolean isUsingProxyHttps() {
        return ProxyUtil.isUsingProxyHttps;
    }

    public static void setUsingProxyHttps(boolean isUsingProxyHttps) {
        ProxyUtil.isUsingProxyHttps = isUsingProxyHttps;
    }
    
}

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
    
    private final String httpProxyDefaultAddress;
    private final String httpProxyDefaultPort;
    private final String httpsProxyDefaultAddress;
    private final String httpsProxyDefaultPort;
    
    /**
     * Proxy IP address or name.
     */
    private String proxyAddress;
    private String proxyAddressHttps;

    /**
     * Proxy port number.
     */
    private String proxyPort;
    private String proxyPortHttps;
    
    /**
     * True if connection is proxified.
     */
    private boolean isUsingProxy = false;
    private boolean isUsingProxyHttps = false;
    
    private static final String PROPERTIES_HTTP_PROXY_HOST = "http.proxyHost";
    private static final String PROPERTIES_HTTP_PROXY_PORT = "http.proxyPort";
    private static final String PROPERTIES_HTTPS_PROXY_HOST = "https.proxyHost";
    private static final String PROPERTIES_HTTPS_PROXY_PORT = "https.proxyPort";
    
    // Utility class
    public ProxyUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        this.httpProxyDefaultAddress = injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("http.proxy.default.ip");
        this.httpProxyDefaultPort = injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("http.proxy.default.port");
        this.httpsProxyDefaultAddress = injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("https.proxy.default.ip");
        this.httpsProxyDefaultPort = injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("https.proxy.default.port");
    }
    InjectionModel injectionModel;
    
    /**
     * Save proxy configuration into the JVM preferences.
     * @param isUsingProxy wether the connection is using a proxy
     * @param proxyAddress IP address or name of the proxy
     * @param proxyPort port number of proxy
     */
    // TODO Spock test
    public void set(
        boolean isUsingProxy,
        String proxyAddress,
        String proxyPort,
        boolean isUsingProxyHttps,
        String proxyAddressHttps,
        String proxyPortHttps
    ) {
        
        // Set the application proxy settings
        this.setUsingProxy(isUsingProxy);
        this.setProxyAddress(proxyAddress);
        this.setProxyPort(proxyPort);
        
        this.setUsingProxyHttps(isUsingProxyHttps);
        this.setProxyAddressHttps(proxyAddressHttps);
        this.setProxyPortHttps(proxyPortHttps);

        // Save the settings in the JVM
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("isUsingProxy", this.isUsingProxy());
        prefs.put("proxyAddress", this.getProxyAddress());
        prefs.put("proxyPort", this.getProxyPort());
        
        prefs.putBoolean("isUsingProxyHttps", this.isUsingProxyHttps());
        prefs.put("proxyAddressHttps", this.getProxyAddressHttps());
        prefs.put("proxyPortHttps", this.getProxyPortHttps());

        // Change the JVM configuration
        if (this.isUsingProxy()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_HOST, this.getProxyAddress());
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_PORT, this.getProxyPort());
        } else {
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_HOST, "");
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_PORT, "");
        }
        
        if (this.isUsingProxyHttps()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_HOST, this.getProxyAddressHttps());
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_PORT, this.getProxyPortHttps());
        } else {
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_HOST, "");
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_PORT, "");
        }
        
    }
    
    /**
     * Initialize proxy information from JVM already saved preferences.
     */
    public void setProxy() {
        
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        this.setUsingProxy(prefs.getBoolean("isUsingProxy", false));
        
        this.setUsingProxyHttps(prefs.getBoolean("isUsingProxyHttps", false));

        // Default TOR config
        this.setProxyAddress(prefs.get("proxyAddress", this.httpProxyDefaultAddress));
        this.setProxyPort(prefs.get("proxyPort", this.httpProxyDefaultPort));
        
        this.setProxyAddressHttps(prefs.get("proxyAddressHttps", this.httpsProxyDefaultAddress));
        this.setProxyPortHttps(prefs.get("proxyPortHttps", this.httpsProxyDefaultPort));
        
        // Change the JVM configuration
        if (this.isUsingProxy()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_HOST, this.getProxyAddress());
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_PORT, this.getProxyPort());
        }
        
        if (this.isUsingProxyHttps()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_HOST, this.getProxyAddressHttps());
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_PORT, this.getProxyPortHttps());
        }
        
    }
    
    /**
     * Check if the proxy is up.
     * @param showOnConsole wether the message should be presented to the user
     * @return true if the proxy is up
     */
    public boolean isLive(ShowOnConsole showOnConsole) {
        
        boolean proxyIsChecked = true;
        
        if (
            this.isUsingProxy()
            && !"".equals(this.getProxyAddress())
            && !"".equals(this.getProxyPort())
        ) {
            try {
                Socket socket = new Socket(this.getProxyAddress(), Integer.parseInt(this.getProxyPort()));
                socket.close();
                
                if (showOnConsole == ShowOnConsole.YES) {
                    ProxyUtil.LOGGER.debug("Connection to HTTP proxy "+ this.getProxyAddress() +":"+ this.getProxyPort() +" successful");
                }
            } catch (Exception e) {
                proxyIsChecked = false;
                
                if (showOnConsole == ShowOnConsole.YES) {
                    String message = Optional.ofNullable(e.getMessage()).orElse("");
                    ProxyUtil.LOGGER.warn(
                        "Connection to HTTP proxy "
                        + this.getProxyAddress() +":"
                        + this.getProxyPort()
                        +" failed with error \""+ message.replace(e.getClass().getName() +": ", "") +"\", verify your proxy settings for HTTP protocol",
                        e
                    );
                }
            }
        }

        if (
            this.isUsingProxyHttps()
            && !"".equals(this.getProxyAddressHttps())
            && !"".equals(this.getProxyPortHttps())
        ) {
            try {
                Socket socket = new Socket(this.getProxyAddressHttps(), Integer.parseInt(this.getProxyPortHttps()));
                socket.close();
                
                if (showOnConsole == ShowOnConsole.YES) {
                    ProxyUtil.LOGGER.debug("Connection to HTTPS proxy "+ this.getProxyAddressHttps() +":"+ this.getProxyPortHttps() +" successful");
                }
            } catch (Exception e) {
                proxyIsChecked = false;
                
                if (showOnConsole == ShowOnConsole.YES) {
                    String message = Optional.ofNullable(e.getMessage()).orElse("");
                    ProxyUtil.LOGGER.warn(
                        "Connection to HTTPS proxy "+ this.getProxyAddressHttps() +":"+ this.getProxyPortHttps() +" failed: "+ message.replace(e.getClass().getName() +": ", "") +", verify your proxy settings for HTTPS protocol", e
                    );
                }
            }
        }
        
        return proxyIsChecked;
        
    }
    
    // Getters and setters
    
    public String getProxyAddress() {
        return this.proxyAddress;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    public String getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isUsingProxy() {
        return this.isUsingProxy;
    }

    public void setUsingProxy(boolean isUsingProxy) {
        this.isUsingProxy = isUsingProxy;
    }

    public String getProxyAddressHttps() {
        return this.proxyAddressHttps;
    }

    public void setProxyAddressHttps(String proxyAddressHttps) {
        this.proxyAddressHttps = proxyAddressHttps;
    }

    public String getProxyPortHttps() {
        return this.proxyPortHttps;
    }

    public void setProxyPortHttps(String proxyPortHttps) {
        this.proxyPortHttps = proxyPortHttps;
    }

    public boolean isUsingProxyHttps() {
        return this.isUsingProxyHttps;
    }

    public void setUsingProxyHttps(boolean isUsingProxyHttps) {
        this.isUsingProxyHttps = isUsingProxyHttps;
    }
    
}

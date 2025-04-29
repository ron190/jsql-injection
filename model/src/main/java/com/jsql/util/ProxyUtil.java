package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.util.GitUtil.ShowOnConsole;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Utility class managing proxy settings.
 * The proxy configuration is saved as preferences and applied to the JVM.
 */
public class ProxyUtil {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Proxy IP address or name.
     */
    private String proxyAddressHttp;
    private String proxyAddressHttps;

    /**
     * Proxy port number.
     */
    private String proxyPortHttp;
    private String proxyPortHttps;
    
    /**
     * True if connection is proxified.
     */
    private boolean isUsingProxyHttp = false;
    private boolean isUsingProxyHttps = false;
    
    private static final String PROPERTIES_HTTP_PROXY_HOST = "http.proxyHost";
    private static final String PROPERTIES_HTTP_PROXY_PORT = "http.proxyPort";
    private static final String PROPERTIES_HTTPS_PROXY_HOST = "https.proxyHost";
    private static final String PROPERTIES_HTTPS_PROXY_PORT = "https.proxyPort";
    
    /**
     * Save proxy configuration into the JVM preferences.
     * @param isUsingProxyHttp whether the connection is using a proxy
     * @param proxyAddressHttp IP address or name of the proxy
     * @param proxyPortHttp port number of proxy
     */
    public void setPreferences(
        boolean isUsingProxyHttp, String proxyAddressHttp, String proxyPortHttp,
        boolean isUsingProxyHttps, String proxyAddressHttps, String proxyPortHttps
    ) {
        // Set the application proxy settings
        this.setUsingProxyHttp(isUsingProxyHttp);
        this.setProxyAddressHttp(proxyAddressHttp);
        this.setProxyPortHttp(proxyPortHttp);
        
        this.setUsingProxyHttps(isUsingProxyHttps);
        this.setProxyAddressHttps(proxyAddressHttps);
        this.setProxyPortHttps(proxyPortHttps);

        // Save the settings in the JVM
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("isUsingProxy", this.isUsingProxyHttp());
        prefs.put("proxyAddress", this.getProxyAddressHttp());
        prefs.put("proxyPort", this.getProxyPortHttp());
        
        prefs.putBoolean("isUsingProxyHttps", this.isUsingProxyHttps());
        prefs.put("proxyAddressHttps", this.getProxyAddressHttps());
        prefs.put("proxyPortHttps", this.getProxyPortHttps());

        // Change the JVM configuration
        if (this.isUsingProxyHttp()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_HOST, this.getProxyAddressHttp());
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_PORT, this.getProxyPortHttp());
        } else {
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_HOST, StringUtils.EMPTY);
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_PORT, StringUtils.EMPTY);
        }
        if (this.isUsingProxyHttps()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_HOST, this.getProxyAddressHttps());
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_PORT, this.getProxyPortHttps());
        } else {
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_HOST, StringUtils.EMPTY);
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_PORT, StringUtils.EMPTY);
        }
    }
    
    /**
     * Initialize proxy information from JVM already saved preferences.
     */
    public void initProxy() {
        // Use Preferences API to persist proxy configuration
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        this.setUsingProxyHttp(preferences.getBoolean("isUsingProxy", false));
        this.setUsingProxyHttps(preferences.getBoolean("isUsingProxyHttps", false));

        // Default proxy config
        this.setProxyAddressHttp(preferences.get("proxyAddress", "127.0.0.1"));
        this.setProxyPortHttp(preferences.get("proxyPort", "8118"));
        
        this.setProxyAddressHttps(preferences.get("proxyAddressHttps", "127.0.0.1"));
        this.setProxyPortHttps(preferences.get("proxyPortHttps", "8118"));
        
        // Change the JVM configuration
        if (this.isUsingProxyHttp()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_HOST, this.getProxyAddressHttp());
            System.setProperty(ProxyUtil.PROPERTIES_HTTP_PROXY_PORT, this.getProxyPortHttp());
        }
        if (this.isUsingProxyHttps()) {
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_HOST, this.getProxyAddressHttps());
            System.setProperty(ProxyUtil.PROPERTIES_HTTPS_PROXY_PORT, this.getProxyPortHttps());
        }
    }
    
    /**
     * Check if enabled proxies are up when application starts,
     * injection begins,- checking IP, sending reports.
     * Display logs except when sending unhandled exception.
     * @param showOnConsole whether the message should be presented to the user
     * @return true if enabled proxies are up
     */
    public boolean isNotLive(ShowOnConsole showOnConsole) {
        var isLive = true;
        if (
            this.isUsingProxyHttp()
            && StringUtils.isNotEmpty(this.getProxyAddressHttp())
            && StringUtils.isNotEmpty(this.getProxyPortHttp())
        ) {
            isLive = this.isSocketOn(showOnConsole, this.getProxyAddressHttp(), this.getProxyPortHttp(), "HTTP");
        }
        if (
            this.isUsingProxyHttps()
            && StringUtils.isNotEmpty(this.getProxyAddressHttps())
            && StringUtils.isNotEmpty(this.getProxyPortHttps())
        ) {
            isLive = this.isSocketOn(showOnConsole, this.getProxyAddressHttps(), this.getProxyPortHttps(), "HTTPS");
        }
        return !isLive;
    }
    
    private boolean isSocketOn(ShowOnConsole showOnConsole, String address, String port, String protocol) {
        var isSocketOn = true;
        try {
            var socket = new Socket(address, Integer.parseInt(port));
            socket.close();
            this.logStatus(showOnConsole, address, port, protocol);
        } catch (Exception e) {
            isSocketOn = false;
            this.logStatus(showOnConsole, address, port, protocol, e);
        }
        return isSocketOn;
    }
    
    private void logStatus(ShowOnConsole showOnConsole, String address, String port, String protocol) {
        if (showOnConsole == ShowOnConsole.YES) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "Connection successful to {} proxy {}:{}",
                () -> protocol,
                () -> address,
                () ->port
            );
        }
    }
    
    private void logStatus(ShowOnConsole showOnConsole, String address, String port, String protocol, Exception e) {
        if (showOnConsole == ShowOnConsole.YES) {
            String message = Optional.ofNullable(e.getMessage()).orElse(StringUtils.EMPTY);
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                () -> String.format(
                    "Connection to %s proxy %s:%s failed, verify your proxy settings: %s",
                    protocol,
                    address,
                    port,
                    message.replace(e.getClass().getName() +": ", StringUtils.EMPTY)
                )
            );
        }
    }
    
    
    // Getters and setters
    
    public String getProxyAddressHttp() {
        return this.proxyAddressHttp;
    }

    public void setProxyAddressHttp(String proxyAddressHttp) {
        this.proxyAddressHttp = proxyAddressHttp;
    }

    public String getProxyPortHttp() {
        return this.proxyPortHttp;
    }

    public void setProxyPortHttp(String proxyPortHttp) {
        this.proxyPortHttp = proxyPortHttp;
    }

    public boolean isUsingProxyHttp() {
        return this.isUsingProxyHttp;
    }

    public void setUsingProxyHttp(boolean isUsingProxyHttp) {
        this.isUsingProxyHttp = isUsingProxyHttp;
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

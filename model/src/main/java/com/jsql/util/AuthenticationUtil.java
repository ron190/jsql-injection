package com.jsql.util;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

/**
 * Manage authentication protocols Basic, Digest, NTLM and Kerberos.
 * Java class Authenticator processes Basic, Digest and NTLM, library spnego
 * processes kerberos. Library jcifs eases the configuration by providing
 * a way to define authentication directly in the URL and it's also compatible
 * with protocol Negotiate.
 */
public class AuthenticationUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String STR_JAVA_PROTO_HDL_PKGS = "java.protocol.handler.pkgs";
    
    /**
     * True if standard authentication Basic, Digest, NTLM is activated.
     */
    private boolean isAuthEnabled = false;

    /**
     * Login for standard authentication.
     */
    private String usernameAuthentication;

    /**
     * Pass for standard authentication.
     */
    private String passwordAuthentication;
    
    /**
     * True if kerberos authentication is activated.
     */
    private boolean isKerberos = false;

    /**
     * Path to the kerberos file login.
     */
    private String pathKerberosLogin;

    /**
     * Path to the kerberos file krb5.
     */
    private String pathKerberosKrb5;
    
    private InjectionModel injectionModel;
    
    public AuthenticationUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    /**
     * Get new authentication settings from the view, update the utility class,
     * persist settings to the JVM and apply changes to the system.
     * @param isAuthentication true if non-kerberos authentication is activated
     * @param usernameAuthentication login for standard authentication
     * @param passwordAuthentication pass for standard authentication
     * @param isKerberos true if krb authentication is activated
     * @param kerberosKrb5Conf path to the file krb5
     * @param kerberosLoginConf path to the file login
     */
    public boolean set(
        boolean isAuthentication,
        String usernameAuthentication,
        String passwordAuthentication,
        boolean isKerberos,
        String kerberosKrb5Conf,
        String kerberosLoginConf
    ) {

        boolean isRestartRequired = this.initializeKerberos(isKerberos, kerberosKrb5Conf, kerberosLoginConf);
        
        this.initializeSimpleAuthorization(isAuthentication, usernameAuthentication, passwordAuthentication);
        
        this.setAuthentication();
        
        return isRestartRequired;
    }

    public void initializeSimpleAuthorization(boolean isAuthentication, String usernameAuthentication, String passwordAuthentication) {
        
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        
        preferences.putBoolean("isDigestAuthentication", this.isAuthEnabled);
        preferences.put("usernameDigest", this.usernameAuthentication);
        preferences.put("passwordDigest", this.passwordAuthentication);
        
        // Define proxy settings
        this.isAuthEnabled = isAuthentication;
        this.usernameAuthentication = usernameAuthentication;
        this.passwordAuthentication = passwordAuthentication;
    }

    private boolean initializeKerberos(boolean isKerberos, String kerberosKrb5Conf, String kerberosLoginConf) {
        
        // Persist to JVM
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        
        this.isKerberos = isKerberos;
        this.pathKerberosKrb5 = kerberosKrb5Conf;
        this.pathKerberosLogin = kerberosLoginConf;
        
        // Check if krb file has change
        boolean isRestartRequired =
            this.isKerberos
            && !new File(this.pathKerberosKrb5).exists()
            && !kerberosKrb5Conf.equals(this.pathKerberosKrb5);
        
        preferences.putBoolean("enableKerberos", this.isKerberos);
        preferences.put("kerberosKrb5Conf", this.pathKerberosKrb5);
        preferences.put("kerberosLoginConf", this.pathKerberosLogin);
        
        // Check krb integrity
        if (this.isKerberos) {
            
            // Fix #23877: NoClassDefFoundError on java/nio/file/Paths
            if (!new File(this.pathKerberosKrb5).exists()) {
                
                LOGGER.warn("Krb5 file not found: " + this.pathKerberosKrb5);
            }
            
            if (!new File(this.pathKerberosLogin).exists()) {
                
                LOGGER.warn("Login file not found: " + this.pathKerberosLogin);
            }
        }
        
        return isRestartRequired;
    }
    
    /**
     * Initialize the utility class with preferences from the JVM
     * and apply environment settings.
     */
    public void setKerberosCifs() {
        
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        this.isAuthEnabled = prefs.getBoolean("isDigestAuthentication", false);

        // Default TOR config
        this.usernameAuthentication = prefs.get("usernameDigest", StringUtils.EMPTY);
        this.passwordAuthentication = prefs.get("passwordDigest", StringUtils.EMPTY);
        
        this.isKerberos = prefs.getBoolean("enableKerberos", false);
        this.pathKerberosKrb5 = prefs.get("kerberosKrb5Conf", StringUtils.EMPTY);
        this.pathKerberosLogin = prefs.get("kerberosLoginConf", StringUtils.EMPTY);

        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        Authenticator.setDefault(null);
        
        if (this.isAuthEnabled) {
            
            AuthCacheValue.setAuthCache(new AuthCacheImpl());
            Authenticator.setDefault(new Authenticator() {
                
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    
                    return new PasswordAuthentication (
                        AuthenticationUtil.this.usernameAuthentication,
                        AuthenticationUtil.this.passwordAuthentication.toCharArray()
                    );
                }
            });
        }
        
        this.setAuthentication();
    }
    
    /**
     * Apply jcifs or kerberos authentication to the JVM.
     * In case of jcifs, which is the default connection processing, it also defines
     * standard timeout configuration.
     */
    public void setAuthentication() {
        
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        Authenticator.setDefault(null);

        if (this.isAuthEnabled) {
            
            AuthCacheValue.setAuthCache(new AuthCacheImpl());
            Authenticator.setDefault(new Authenticator() {
                
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    
                    return new PasswordAuthentication (
                        AuthenticationUtil.this.usernameAuthentication,
                        AuthenticationUtil.this.passwordAuthentication.toCharArray()
                    );
                }
            });
            
        } else {
            
            AuthCacheValue.setAuthCache(new AuthCacheImpl());
            Authenticator.setDefault(null);
        }
        
        if (this.isKerberos) {
            
            if (System.getProperty(STR_JAVA_PROTO_HDL_PKGS) != null) {
                
                System.setProperty(
                    STR_JAVA_PROTO_HDL_PKGS,
                    System
                    .getProperty(STR_JAVA_PROTO_HDL_PKGS)
                    .replace("|jcifs", StringUtils.EMPTY)
                    .replace("jcifs", StringUtils.EMPTY)
                );
            }
            
            System.setProperty("java.security.krb5.conf", this.pathKerberosKrb5);
            System.setProperty("java.security.auth.login.config", this.pathKerberosLogin);
            System.setProperty("spnego.krb5.conf", this.pathKerberosKrb5);
            System.setProperty("spnego.login.conf", this.pathKerberosLogin);
            
        } else {
            
            System.setProperty(STR_JAVA_PROTO_HDL_PKGS, StringUtils.EMPTY);
            System.setProperty("java.security.krb5.conf", StringUtils.EMPTY);
            System.setProperty("java.security.auth.login.config", StringUtils.EMPTY);
            System.setProperty("spnego.krb5.conf", StringUtils.EMPTY);
            System.setProperty("spnego.login.conf", StringUtils.EMPTY);
            
            System.setProperty("jcifs.smb.client.responseTimeout", this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout().toString());
            System.setProperty("jcifs.smb.client.soTimeout", this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout().toString());
            jcifs.Config.setProperty("jcifs.smb.client.responseTimeout", this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout().toString());
            jcifs.Config.setProperty("jcifs.smb.client.soTimeout", this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout().toString());
            
            jcifs.Config.registerSmbURLHandler();
        }
    }
    
    
    // Getters and setters

    public String getUsernameDigest() {
        return this.usernameAuthentication;
    }

    public String getPasswordDigest() {
        return this.passwordAuthentication;
    }

    public boolean isAuthentEnabled() {
        return this.isAuthEnabled;
    }

    public String getPathKerberosLogin() {
        return this.pathKerberosLogin;
    }

    public String getPathKerberosKrb5() {
        return this.pathKerberosKrb5;
    }

    public boolean isKerberos() {
        return this.isKerberos;
    }

    public void setIsAuthentEnabled(boolean isAuthentEnabled) {
        this.isAuthEnabled = isAuthentEnabled;
    }

    public void setUsernameAuthentication(String usernameAuthentication) {
        this.usernameAuthentication = usernameAuthentication;
    }

    public void setPasswordAuthentication(String passwordAuthentication) {
        this.passwordAuthentication = passwordAuthentication;
    }
    
    
    // Builder
    
    public AuthenticationUtil withAuthentEnabled() {
        this.isAuthEnabled = true;
        return this;
    }
    
    public AuthenticationUtil withUsernameAuthentication(String usernameAuthentication) {
        this.usernameAuthentication = usernameAuthentication;
        return this;
    }
    
    public AuthenticationUtil withPasswordAuthentication(String passwordAuthentication) {
        this.passwordAuthentication = passwordAuthentication;
        return this;
    }
}

package com.jsql.util;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;

/**
 * Manage authentication protocols Basic, Digest, NTLM and Kerberos.
 * Java class Authenticator processes Basic, Digest and NTLM, library spnego
 * processes kerberos.
 */
public class AuthenticationUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * True if standard authentication Basic, Digest, NTLM is activated.
     */
    private boolean isAuthentication = false;

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
        
        // TODO Move to Preferences
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        
        preferences.putBoolean("isAuthentication", isAuthentication);
        preferences.put("usernameAuthentication", usernameAuthentication);
        preferences.put("passwordAuthentication", passwordAuthentication);
        
        // Define proxy settings
        this.isAuthentication = isAuthentication;
        this.usernameAuthentication = usernameAuthentication;
        this.passwordAuthentication = passwordAuthentication;
    }

    private boolean initializeKerberos(boolean isKerberos, String kerberosKrb5Conf, String kerberosLoginConf) {
        
        // Persist to JVM
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        
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
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Krb5 file not found: {}", this.pathKerberosKrb5);
            }
            
            if (!new File(this.pathKerberosLogin).exists()) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Login file not found: {}", this.pathKerberosLogin);
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
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        this.isAuthentication = preferences.getBoolean("isAuthentication", false);

        // Default TOR config
        this.usernameAuthentication = preferences.get("usernameAuthentication", StringUtils.EMPTY);
        this.passwordAuthentication = preferences.get("passwordAuthentication", StringUtils.EMPTY);
        
        this.isKerberos = preferences.getBoolean("enableKerberos", false);
        this.pathKerberosKrb5 = preferences.get("kerberosKrb5Conf", StringUtils.EMPTY);
        this.pathKerberosLogin = preferences.get("kerberosLoginConf", StringUtils.EMPTY);
        
        this.setAuthentication();
    }
    
    /**
     * Apply kerberos authentication to the JVM.
     */
    public void setAuthentication() {
        
        Authenticator.setDefault(null);

        if (this.isAuthentication) {
            
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
            
            Authenticator.setDefault(null);
        }
        
        if (this.isKerberos) {
            
            System.setProperty("java.security.krb5.conf", this.pathKerberosKrb5);
            System.setProperty("java.security.auth.login.config", this.pathKerberosLogin);
            System.setProperty("spnego.krb5.conf", this.pathKerberosKrb5);
            System.setProperty("spnego.login.conf", this.pathKerberosLogin);
            
        } else {
            
            System.setProperty("java.security.krb5.conf", StringUtils.EMPTY);
            System.setProperty("java.security.auth.login.config", StringUtils.EMPTY);
            System.setProperty("spnego.krb5.conf", StringUtils.EMPTY);
            System.setProperty("spnego.login.conf", StringUtils.EMPTY);
        }
    }
    
    
    // Getters and setters

    public boolean isAuthentEnabled() {
        return this.isAuthentication;
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

    public String getUsernameAuthentication() {
        return this.usernameAuthentication;
    }

    public String getPasswordAuthentication() {
        return this.passwordAuthentication;
    }
    
    
    // Builder
    
    public AuthenticationUtil withAuthentEnabled() {
        this.isAuthentication = true;
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

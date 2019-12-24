package com.jsql.util;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;

/**
 * Manage authentication protocols Basic, Digest, NTLM and Kerberos.
 * Java class Authenticator processes Basic, Digest and NTLM, library spnego
 * processes kerberos. Library jcifs eases the configuration by providing
 * a way to define authent directly in the URL and it's also compatible
 * with protocol Negotiate.
 */
public class AuthenticationUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * True if standard authent Basic, Digest, NTLM is activated.
     */
    private boolean isAuthentication = false;

    /**
     * Login for standard authent.
     */
    private String usernameAuthentication;

    /**
     * Pass for standard authent.
     */
    private String passwordAuthentication;
    
    /**
     * True if kerberos authent is activated.
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
     * @param isAuthentication true if non-kerberos authent is activated
     * @param usernameAuthentication login for standard authent
     * @param passwordAuthentication pass for standard authent
     * @param isKerberos true if krb authent is activated
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
        
        // Check if krb file has change
        boolean isRestartRequired = false;
        if (
            this.isKerberos
            && !new File(this.pathKerberosKrb5).exists()
            && !kerberosKrb5Conf.equals(this.pathKerberosKrb5)
        ) {
            isRestartRequired = true;
        }
        
        // Define proxy settings
        this.isAuthentication = isAuthentication;
        this.usernameAuthentication = usernameAuthentication;
        this.passwordAuthentication = passwordAuthentication;
        
        this.isKerberos = isKerberos;
        this.pathKerberosKrb5 = kerberosKrb5Conf;
        this.pathKerberosLogin = kerberosLoginConf;

        // Persist to JVM
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        preferences.putBoolean("isDigestAuthentication", this.isAuthentication);
        preferences.put("usernameDigest", this.usernameAuthentication);
        preferences.put("passwordDigest", this.passwordAuthentication);
        
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
        
        // Activate standard authentication
        // TODO: java.lang.IllegalAccessError: class com.jsql.tool.AuthenticationTools (in unnamed module @0x266d09)
        // cannot access class sun.net.www.protocol.http.AuthCacheImpl (in module java.base) because module java.base
        // does not export sun.net.www.protocol.http to unnamed module @0x266d09
        // Use Authenticator.setDefault(null); or a bad Authenticator
//        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
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
        
        this.setAuthentication();
        
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
        this.isAuthentication = prefs.getBoolean("isDigestAuthentication", false);

        // Default TOR config
        this.usernameAuthentication = prefs.get("usernameDigest", "");
        this.passwordAuthentication = prefs.get("passwordDigest", "");
        
        this.isKerberos = prefs.getBoolean("enableKerberos", false);
        this.pathKerberosKrb5 = prefs.get("kerberosKrb5Conf", "");
        this.pathKerberosLogin = prefs.get("kerberosLoginConf", "");

//        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
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
        }
        
        this.setAuthentication();
        
    }
    
    /**
     * Apply jcifs or kerberos authentication to the JVM.
     * In case of jcifs, which is the default connection processing, it also defines
     * standard timeout configuration.
     */
    private void setAuthentication() {
        
        if (this.isKerberos) {
            if (System.getProperty("java.protocol.handler.pkgs") != null) {
                System.setProperty(
                    "java.protocol.handler.pkgs",
                    System.getProperty("java.protocol.handler.pkgs")
                        .replace("|jcifs", "")
                        .replace("jcifs", "")
                );
            }
            System.setProperty("java.security.krb5.conf", this.pathKerberosKrb5);
            System.setProperty("java.security.auth.login.config", this.pathKerberosLogin);
            System.setProperty("spnego.krb5.conf", this.pathKerberosKrb5);
            System.setProperty("spnego.login.conf", this.pathKerberosLogin);
        } else {
            System.setProperty("java.protocol.handler.pkgs", "");
            System.setProperty("java.security.krb5.conf", "");
            System.setProperty("java.security.auth.login.config", "");
            System.setProperty("spnego.krb5.conf", "");
            System.setProperty("spnego.login.conf", "");
            
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

    public boolean isDigestAuthentication() {
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
    
}

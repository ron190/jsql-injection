package com.jsql.util;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.ActionNewWindow;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

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
    private static boolean isAuthentication = false;

    /**
     * Login for standard authent.
     */
    private static String usernameAuthentication;

    /**
     * Pass for standard authent.
     */
    private static String passwordAuthentication;
    
    /**
     * True if kerberos authent is activated.
     */
    private static boolean isKerberos = false;

    /**
     * Path to the kerberos file login.
     */
    private static String pathKerberosLogin;

    /**
     * Path to the kerberos file krb5.
     */
    private static String pathKerberosKrb5;
    
    // Utility class
    private AuthenticationUtil() {
        // not called
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
    public static void set(
        boolean isAuthentication, String usernameAuthentication, String passwordAuthentication,
        boolean isKerberos, String kerberosKrb5Conf, String kerberosLoginConf
    ) {
        
    	// Check if krb file has change
        boolean isRestartRequired = false;
        if (
            AuthenticationUtil.isKerberos
            && !new File(AuthenticationUtil.pathKerberosKrb5).exists()
            && !kerberosKrb5Conf.equals(AuthenticationUtil.pathKerberosKrb5)
        ) {
            isRestartRequired = true;
        }
        
        // Define proxy settings
        AuthenticationUtil.isAuthentication = isAuthentication;
        AuthenticationUtil.usernameAuthentication = usernameAuthentication;
        AuthenticationUtil.passwordAuthentication = passwordAuthentication;
        
        AuthenticationUtil.isKerberos = isKerberos;
        AuthenticationUtil.pathKerberosKrb5 = kerberosKrb5Conf;
        AuthenticationUtil.pathKerberosLogin = kerberosLoginConf;

        // Persist to JVM
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        preferences.putBoolean("isDigestAuthentication", AuthenticationUtil.isAuthentication);
        preferences.put("usernameDigest", AuthenticationUtil.usernameAuthentication);
        preferences.put("passwordDigest", AuthenticationUtil.passwordAuthentication);
        
        preferences.putBoolean("enableKerberos", AuthenticationUtil.isKerberos);
        preferences.put("kerberosKrb5Conf", AuthenticationUtil.pathKerberosKrb5);
        preferences.put("kerberosLoginConf", AuthenticationUtil.pathKerberosLogin);
        
        // Check krb integrity
        if (AuthenticationUtil.isKerberos) {
            // Fix #23877: NoClassDefFoundError on java/nio/file/Paths
        	if (!new File(AuthenticationUtil.pathKerberosKrb5).exists()) {
        		LOGGER.warn("Krb5 file not found: " + AuthenticationUtil.pathKerberosKrb5);
        	}
        	if (!new File(AuthenticationUtil.pathKerberosLogin).exists()) {
        		LOGGER.warn("Login file not found: " + AuthenticationUtil.pathKerberosLogin);
        	}
        }
        
        // Activate standard authentication
        // TODO: java.lang.IllegalAccessError: class com.jsql.tool.AuthenticationTools (in unnamed module @0x266d09)
        // cannot access class sun.net.www.protocol.http.AuthCacheImpl (in module java.base) because module java.base
        // does not export sun.net.www.protocol.http to unnamed module @0x266d09
        // Use Authenticator.setDefault(null); or a bad Authenticator
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (AuthenticationUtil.isAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication (
                        AuthenticationUtil.usernameAuthentication,
                        AuthenticationUtil.passwordAuthentication.toCharArray()
                    );
                }
            });
        } else {
            Authenticator.setDefault(null);
        }
        
        AuthenticationUtil.setAuthentication();
        
        // Manage the restart of application if required
        // TODO Remove from model
        if (
            isRestartRequired &&
            JOptionPane.showConfirmDialog(
                MediatorGui.frame(),
                "File krb5.conf has changed, please restart.",
                "Restart",
                JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION
        ) {
            new ActionNewWindow().actionPerformed(null);
        }
        
    }
    
    /**
     * Initialize the utility class with preferences from the JVM
     * and apply environment settings.
     */
    public static void setKerberosCifs() {
    	
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        AuthenticationUtil.isAuthentication = prefs.getBoolean("isDigestAuthentication", false);

        // Default TOR config
        AuthenticationUtil.usernameAuthentication = prefs.get("usernameDigest", "");
        AuthenticationUtil.passwordAuthentication = prefs.get("passwordDigest", "");
        
        AuthenticationUtil.isKerberos = prefs.getBoolean("enableKerberos", false);
        AuthenticationUtil.pathKerberosKrb5 = prefs.get("kerberosKrb5Conf", "");
        AuthenticationUtil.pathKerberosLogin = prefs.get("kerberosLoginConf", "");

        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (AuthenticationUtil.isAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication (
                        AuthenticationUtil.usernameAuthentication,
                        AuthenticationUtil.passwordAuthentication.toCharArray()
                    );
                }
            });
        }
        
        AuthenticationUtil.setAuthentication();
        
    }
    
    /**
     * Apply jcifs or kerberos authentication to the JVM.
     * In case of jcifs, which is the default connection processing, it also defines
     * standard timeout configuration.
     */
    private static void setAuthentication() {
    	
        if (AuthenticationUtil.isKerberos) {
            if (System.getProperty("java.protocol.handler.pkgs") != null) {
                System.setProperty(
                    "java.protocol.handler.pkgs",
                    System.getProperty("java.protocol.handler.pkgs")
                        .replace("|jcifs", "")
                        .replace("jcifs", "")
                );
            }
            System.setProperty("java.security.krb5.conf", AuthenticationUtil.pathKerberosKrb5);
            System.setProperty("java.security.auth.login.config", AuthenticationUtil.pathKerberosLogin);
            System.setProperty("spnego.krb5.conf", AuthenticationUtil.pathKerberosKrb5);
            System.setProperty("spnego.login.conf", AuthenticationUtil.pathKerberosLogin);
        } else {
            System.setProperty("java.protocol.handler.pkgs", "");
            System.setProperty("java.security.krb5.conf", "");
            System.setProperty("java.security.auth.login.config", "");
            System.setProperty("spnego.krb5.conf", "");
            System.setProperty("spnego.login.conf", "");
            
            System.setProperty("jcifs.smb.client.responseTimeout", ConnectionUtil.TIMEOUT.toString());
            System.setProperty("jcifs.smb.client.soTimeout", ConnectionUtil.TIMEOUT.toString());
            jcifs.Config.setProperty("jcifs.smb.client.responseTimeout", ConnectionUtil.TIMEOUT.toString());
            jcifs.Config.setProperty("jcifs.smb.client.soTimeout", ConnectionUtil.TIMEOUT.toString());
            
            jcifs.Config.registerSmbURLHandler();
        }
        
    }
    
    // Getters and setters

    public static String getUsernameDigest() {
        return usernameAuthentication;
    }

    public static String getPasswordDigest() {
        return passwordAuthentication;
    }

    public static boolean isDigestAuthentication() {
        return isAuthentication;
    }

    public static String getPathKerberosLogin() {
        return pathKerberosLogin;
    }

    public static String getPathKerberosKrb5() {
        return pathKerberosKrb5;
    }

    public static boolean isKerberos() {
        return isKerberos;
    }
    
}

package com.jsql.util;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.ActionNewWindow;

public class AuthenticationUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationUtil.class);

    public static String usernameDigest;

    public static String passwordDigest;

    public static boolean isDigestAuthentication = false;

    public static String pathKerberosLogin;

    public static String pathKerberosKrb5;

    public static boolean isKerberos = false;
    
    /**
     * Utility class.
     */
    private AuthenticationUtil() {
        //not called
    }
    
    public static void set(
        boolean isDigestAuthentication, String usernameDigest, String passwordDigest,
        boolean isKerberos, String kerberosKrb5Conf, String kerberosLoginConf
    ) {
        
        boolean isRestartRequired = false;
        if (
            AuthenticationUtil.isKerberos
            && !new File(AuthenticationUtil.pathKerberosKrb5).exists()
            && !kerberosKrb5Conf.equals(AuthenticationUtil.pathKerberosKrb5)
        ) {
            isRestartRequired = true;
        }
        
        // Define proxy settings
        AuthenticationUtil.isDigestAuthentication = isDigestAuthentication;
        AuthenticationUtil.usernameDigest = usernameDigest;
        AuthenticationUtil.passwordDigest = passwordDigest;
        
        AuthenticationUtil.isKerberos = isKerberos;
        AuthenticationUtil.pathKerberosKrb5 = kerberosKrb5Conf;
        AuthenticationUtil.pathKerberosLogin = kerberosLoginConf;

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        preferences.putBoolean("isDigestAuthentication", AuthenticationUtil.isDigestAuthentication);
        preferences.put("usernameDigest", AuthenticationUtil.usernameDigest);
        preferences.put("passwordDigest", AuthenticationUtil.passwordDigest);
        
        preferences.putBoolean("enableKerberos", AuthenticationUtil.isKerberos);
        preferences.put("kerberosKrb5Conf", AuthenticationUtil.pathKerberosKrb5);
        preferences.put("kerberosLoginConf", AuthenticationUtil.pathKerberosLogin);
        
        if (
            AuthenticationUtil.isKerberos && 
            !new File(AuthenticationUtil.pathKerberosKrb5).exists()
        ) {
            LOGGER.warn("Krb5 file not found: " + AuthenticationUtil.pathKerberosKrb5);
        }
        if (
            AuthenticationUtil.isKerberos && 
            !new File(AuthenticationUtil.pathKerberosLogin).exists()
        ) {
            LOGGER.warn("Login file not found: " + AuthenticationUtil.pathKerberosLogin);
        }
        
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (AuthenticationUtil.isDigestAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication pa = new PasswordAuthentication (
                        AuthenticationUtil.usernameDigest, 
                        AuthenticationUtil.passwordDigest.toCharArray()
                    );
                    return pa;
                }
            });
        } else {
            Authenticator.setDefault(null);
        }
        
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
            
            jcifs.Config.registerSmbURLHandler();
        }
        
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
    
    public static void initialize() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        AuthenticationUtil.isDigestAuthentication = prefs.getBoolean("isDigestAuthentication", false);

        // Default TOR config
        AuthenticationUtil.usernameDigest = prefs.get("usernameDigest", "");
        AuthenticationUtil.passwordDigest = prefs.get("passwordDigest", "");
        
        AuthenticationUtil.isKerberos = prefs.getBoolean("enableKerberos", false);
        AuthenticationUtil.pathKerberosKrb5 = prefs.get("kerberosKrb5Conf", "");
        AuthenticationUtil.pathKerberosLogin = prefs.get("kerberosLoginConf", "");

        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (AuthenticationUtil.isDigestAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication pa = new PasswordAuthentication (
                        AuthenticationUtil.usernameDigest, 
                        AuthenticationUtil.passwordDigest.toCharArray()
                    );
                    return pa;
                }
            });
        }
        
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
            
            jcifs.Config.registerSmbURLHandler();
        }
    }
}

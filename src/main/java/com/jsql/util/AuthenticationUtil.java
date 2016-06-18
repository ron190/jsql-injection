package com.jsql.util;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

import com.jsql.model.injection.InjectionModel;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.action.ActionNewWindow;

@SuppressWarnings("restriction")
public class AuthenticationUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationUtil.class);

    public static String digestUsername;

    public static String digestPassword;

    public static boolean enableDigestAuthentication = false;

    public static String kerberosLoginConf;

    public static String kerberosKrb5Conf;

    public static boolean enableKerberos = false;
    
    /**
     * Utility class.
     */
    private AuthenticationUtil() {
        //not called
    }
    
    public static void set(boolean enableDigestAuthentication, String digestUsername, String digestPassword,
            boolean enableKerberos, String kerberosKrb5Conf, String kerberosLoginConf) {
        
        boolean shouldRestart = false;
        if (AuthenticationUtil.enableKerberos
                && !new File(AuthenticationUtil.kerberosKrb5Conf).exists()
                && !kerberosKrb5Conf.equals(AuthenticationUtil.kerberosKrb5Conf)) {
            shouldRestart = true;
        }
        
        // Define proxy settings
        AuthenticationUtil.enableDigestAuthentication = enableDigestAuthentication;
        AuthenticationUtil.digestUsername = digestUsername;
        AuthenticationUtil.digestPassword = digestPassword;
        
        AuthenticationUtil.enableKerberos = enableKerberos;
        AuthenticationUtil.kerberosKrb5Conf = kerberosKrb5Conf;
        AuthenticationUtil.kerberosLoginConf = kerberosLoginConf;

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("enableDigestAuthentication", AuthenticationUtil.enableDigestAuthentication);
        prefs.put("digestUsername", AuthenticationUtil.digestUsername);
        prefs.put("digestPassword", AuthenticationUtil.digestPassword);
        
        prefs.putBoolean("enableKerberos", AuthenticationUtil.enableKerberos);
        prefs.put("kerberosKrb5Conf", AuthenticationUtil.kerberosKrb5Conf);
        prefs.put("kerberosLoginConf", AuthenticationUtil.kerberosLoginConf);
        
        if (AuthenticationUtil.enableKerberos && !new File(AuthenticationUtil.kerberosKrb5Conf).exists()) {
            LOGGER.warn("Krb5 file not found: " + AuthenticationUtil.kerberosKrb5Conf);
        }
        if (AuthenticationUtil.enableKerberos && !new File(AuthenticationUtil.kerberosLoginConf).exists()) {
            LOGGER.warn("Login file not found: " + AuthenticationUtil.kerberosLoginConf);
        }
        
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (AuthenticationUtil.enableDigestAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication pa = new PasswordAuthentication (
                        AuthenticationUtil.digestUsername, 
                        AuthenticationUtil.digestPassword.toCharArray()
                    );
                    return pa;
                }
            });
        } else {
            Authenticator.setDefault(null);
        }
        
        if (AuthenticationUtil.enableKerberos) {
            if (System.getProperty("java.protocol.handler.pkgs") != null) {
                System.setProperty("java.protocol.handler.pkgs", 
                        System.getProperty("java.protocol.handler.pkgs").replace("|jcifs", "").replace("jcifs", ""));
            }
            System.setProperty("java.security.krb5.conf", AuthenticationUtil.kerberosKrb5Conf);
            System.setProperty("java.security.auth.login.config", AuthenticationUtil.kerberosLoginConf);
            System.setProperty("spnego.krb5.conf", AuthenticationUtil.kerberosKrb5Conf);
            System.setProperty("spnego.login.conf", AuthenticationUtil.kerberosLoginConf);
        } else {
            System.setProperty("java.protocol.handler.pkgs", "");
            System.setProperty("java.security.krb5.conf", "");
            System.setProperty("java.security.auth.login.config", "");
            System.setProperty("spnego.krb5.conf", "");
            System.setProperty("spnego.login.conf", "");
            
            jcifs.Config.registerSmbURLHandler();
        }
        
        if (shouldRestart && JOptionPane.showConfirmDialog(MediatorGUI.jFrame(), "File krb5.conf has changed, please restart.", "Restart", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new ActionNewWindow().actionPerformed(null);
        }
    }
    
    public static void initializeProtocol() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        AuthenticationUtil.enableDigestAuthentication = prefs.getBoolean("enableDigestAuthentication", false);

        // Default TOR config
        AuthenticationUtil.digestUsername = prefs.get("digestUsername", "");
        AuthenticationUtil.digestPassword = prefs.get("digestPassword", "");
        
        AuthenticationUtil.enableKerberos = prefs.getBoolean("enableKerberos", false);
        AuthenticationUtil.kerberosKrb5Conf = prefs.get("kerberosKrb5Conf", "");
        AuthenticationUtil.kerberosLoginConf = prefs.get("kerberosLoginConf", "");

        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (AuthenticationUtil.enableDigestAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication pa = new PasswordAuthentication (
                        AuthenticationUtil.digestUsername, 
                        AuthenticationUtil.digestPassword.toCharArray()
                    );
                    return pa;
                }
            });
        }
        
        if (AuthenticationUtil.enableKerberos) {
            if (System.getProperty("java.protocol.handler.pkgs") != null) {
                System.setProperty("java.protocol.handler.pkgs", 
                        System.getProperty("java.protocol.handler.pkgs").replace("|jcifs", "").replace("jcifs", ""));
            }
            System.setProperty("java.security.krb5.conf", AuthenticationUtil.kerberosKrb5Conf);
            System.setProperty("java.security.auth.login.config", AuthenticationUtil.kerberosLoginConf);
            System.setProperty("spnego.krb5.conf", AuthenticationUtil.kerberosKrb5Conf);
            System.setProperty("spnego.login.conf", AuthenticationUtil.kerberosLoginConf);
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

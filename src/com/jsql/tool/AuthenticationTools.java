package com.jsql.tool;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.action.ActionNewWindow;

@SuppressWarnings("restriction")
public class AuthenticationTools {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationTools.class);

    /**
     * Utility class.
     */
    private AuthenticationTools() {
        //not called
    }
    
    public static void set(boolean enableDigestAuthentication, String digestUsername, String digestPassword,
            boolean enableKerberos, String kerberosKrb5Conf, String kerberosLoginConf) {
        
        boolean shouldRestart = false;
        if (MediatorModel.model().enableKerberos
                && !new File(MediatorModel.model().kerberosKrb5Conf).exists()
                && !kerberosKrb5Conf.equals(MediatorModel.model().kerberosKrb5Conf)) {
            shouldRestart = true;
        }
        
        // Define proxy settings
        MediatorModel.model().enableDigestAuthentication = enableDigestAuthentication;
        MediatorModel.model().digestUsername = digestUsername;
        MediatorModel.model().digestPassword = digestPassword;
        
        MediatorModel.model().enableKerberos = enableKerberos;
        MediatorModel.model().kerberosKrb5Conf = kerberosKrb5Conf;
        MediatorModel.model().kerberosLoginConf = kerberosLoginConf;

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("enableDigestAuthentication", MediatorModel.model().enableDigestAuthentication);
        prefs.put("digestUsername", MediatorModel.model().digestUsername);
        prefs.put("digestPassword", MediatorModel.model().digestPassword);
        
        prefs.putBoolean("enableKerberos", MediatorModel.model().enableKerberos);
        prefs.put("kerberosKrb5Conf", MediatorModel.model().kerberosKrb5Conf);
        prefs.put("kerberosLoginConf", MediatorModel.model().kerberosLoginConf);
        
        if (MediatorModel.model().enableKerberos && !new File(MediatorModel.model().kerberosKrb5Conf).exists()) {
            LOGGER.warn("Krb5 file not found: " + MediatorModel.model().kerberosKrb5Conf);
        }
        if (MediatorModel.model().enableKerberos && !new File(MediatorModel.model().kerberosLoginConf).exists()) {
            LOGGER.warn("Login file not found: " + MediatorModel.model().kerberosLoginConf);
        }
        
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (MediatorModel.model().enableDigestAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication pa = new PasswordAuthentication (
                        MediatorModel.model().digestUsername, 
                        MediatorModel.model().digestPassword.toCharArray()
                    );
                    return pa;
                }
            });
        } else {
            Authenticator.setDefault(null);
        }
        
        if (MediatorModel.model().enableKerberos) {
            if (System.getProperty("java.protocol.handler.pkgs") != null) {
                System.setProperty("java.protocol.handler.pkgs", 
                        System.getProperty("java.protocol.handler.pkgs").replace("|jcifs", "").replace("jcifs", ""));
            }
            System.setProperty("java.security.krb5.conf", MediatorModel.model().kerberosKrb5Conf);
            System.setProperty("java.security.auth.login.config", MediatorModel.model().kerberosLoginConf);
            System.setProperty("spnego.krb5.conf", MediatorModel.model().kerberosKrb5Conf);
            System.setProperty("spnego.login.conf", MediatorModel.model().kerberosLoginConf);
        } else {
            System.setProperty("java.protocol.handler.pkgs", "");
            System.setProperty("java.security.krb5.conf", "");
            System.setProperty("java.security.auth.login.config", "");
            System.setProperty("spnego.krb5.conf", "");
            System.setProperty("spnego.login.conf", "");
            
            jcifs.Config.registerSmbURLHandler();
        }
        
        if (shouldRestart && JOptionPane.showConfirmDialog(MediatorGUI.gui(), "File krb5.conf has changed, please restart.", "Restart", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new ActionNewWindow().actionPerformed(null);
        }
    }
    
    public static void init() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        MediatorModel.model().enableDigestAuthentication = prefs.getBoolean("enableDigestAuthentication", false);

        // Default TOR config
        MediatorModel.model().digestUsername = prefs.get("digestUsername", "");
        MediatorModel.model().digestPassword = prefs.get("digestPassword", "");
        
        MediatorModel.model().enableKerberos = prefs.getBoolean("enableKerberos", false);
        MediatorModel.model().kerberosKrb5Conf = prefs.get("kerberosKrb5Conf", "");
        MediatorModel.model().kerberosLoginConf = prefs.get("kerberosLoginConf", "");

        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        
        if (MediatorModel.model().enableDigestAuthentication) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    PasswordAuthentication pa = new PasswordAuthentication (
                        MediatorModel.model().digestUsername, 
                        MediatorModel.model().digestPassword.toCharArray()
                    );
                    return pa;
                }
            });
        }
        
        if (MediatorModel.model().enableKerberos) {
            if (System.getProperty("java.protocol.handler.pkgs") != null) {
                System.setProperty("java.protocol.handler.pkgs", 
                        System.getProperty("java.protocol.handler.pkgs").replace("|jcifs", "").replace("jcifs", ""));
            }
            System.setProperty("java.security.krb5.conf", MediatorModel.model().kerberosKrb5Conf);
            System.setProperty("java.security.auth.login.config", MediatorModel.model().kerberosLoginConf);
            System.setProperty("spnego.krb5.conf", MediatorModel.model().kerberosKrb5Conf);
            System.setProperty("spnego.login.conf", MediatorModel.model().kerberosLoginConf);
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

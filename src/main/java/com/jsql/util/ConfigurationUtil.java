package com.jsql.util;

import java.util.prefs.Preferences;

import com.jsql.model.injection.InjectionModel;

public class ConfigurationUtil {

    /**
     * File path saved in preference.
     */
    public static String prefPathFile;
    
    /**
     * True if connection is proxified.
     */
    public static boolean checkUpdateAtStartup = true;
    
    /**
     * True if evasion techniques should be used.
     */
    public static boolean enableEvasion = false;

    /**
     * True to follow HTTP 302 redirection.
     */
    public static boolean followRedirection = false;
    
    /**
     * True if connection is proxified.
     */
    public static boolean reportBugs = true;
    
    public static void initializePreferences() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        ConfigurationUtil.checkUpdateAtStartup = prefs.getBoolean("updateAtStartup", true);
        ConfigurationUtil.reportBugs = prefs.getBoolean("reportBugs", true);
        ConfigurationUtil.enableEvasion = prefs.getBoolean("enableEvasion", false);
        ConfigurationUtil.followRedirection = prefs.getBoolean("followRedirection", false);
        ConfigurationUtil.prefPathFile = prefs.get("pathFile", System.getProperty("user.dir"));
    }
    
    public static void setPath(String path) {
        ConfigurationUtil.prefPathFile = path;
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", ConfigurationUtil.prefPathFile);
    }
    
    public static void set(boolean checkUpdateAtStartup, boolean reportBugs, boolean enableEvasion, boolean followRedirection) {
        ConfigurationUtil.checkUpdateAtStartup = checkUpdateAtStartup;
        ConfigurationUtil.reportBugs = reportBugs;
        ConfigurationUtil.enableEvasion = enableEvasion;
        ConfigurationUtil.followRedirection = followRedirection;

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        prefs.putBoolean("updateAtStartup", ConfigurationUtil.checkUpdateAtStartup);
        prefs.putBoolean("reportBugs", ConfigurationUtil.reportBugs);
        prefs.putBoolean("enableEvasion", ConfigurationUtil.enableEvasion);
        prefs.putBoolean("followRedirection", ConfigurationUtil.followRedirection);
    }
    
}

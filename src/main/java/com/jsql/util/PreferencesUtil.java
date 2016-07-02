package com.jsql.util;

import java.util.prefs.Preferences;

import com.jsql.model.InjectionModel;

public class PreferencesUtil {

    /**
     * File path saved in preference.
     */
    public static String pathFile;
    
    /**
     * True if connection is proxified.
     */
    public static boolean isCheckingUpdate = true;
    
    /**
     * True if evasion techniques should be used.
     */
    public static boolean isEvading = false;

    /**
     * True to follow HTTP 302 redirection.
     */
    public static boolean isFollowingRedirection = false;
    
    /**
     * True if connection is proxified.
     */
    public static boolean isReportingBugs = true;
    
    public static void initialize() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        PreferencesUtil.isCheckingUpdate = prefs.getBoolean("isCheckingUpdate", true);
        PreferencesUtil.isReportingBugs = prefs.getBoolean("isReportingBugs", true);
        PreferencesUtil.isEvading = prefs.getBoolean("isEvading", false);
        PreferencesUtil.isFollowingRedirection = prefs.getBoolean("isFollowingRedirection", false);
        
        PreferencesUtil.pathFile = prefs.get("pathFile", System.getProperty("user.dir"));
    }
    
    public static void setPath(String path) {
        PreferencesUtil.pathFile = path;
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", PreferencesUtil.pathFile);
    }
    
    public static void set(boolean isCheckingUpdate, boolean isReportingBugs, boolean isEvading, boolean isFollowingRedirection) {
        PreferencesUtil.isCheckingUpdate = isCheckingUpdate;
        PreferencesUtil.isReportingBugs = isReportingBugs;
        PreferencesUtil.isEvading = isEvading;
        PreferencesUtil.isFollowingRedirection = isFollowingRedirection;

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.isCheckingUpdate);
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs);
        preferences.putBoolean("isEvading", PreferencesUtil.isEvading);
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection);
    }
    
}

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
    public static boolean isCheckUpdateActivated = true;
    
    /**
     * True if evasion techniques should be used.
     */
    public static boolean evasionIsEnabled = false;

    /**
     * True to follow HTTP 302 redirection.
     */
    public static boolean isFollowingRedirection = false;
    
    /**
     * True if connection is proxified.
     */
    public static boolean isReportingBugs = true;
    
    public static void loadSavedPreferences() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        PreferencesUtil.isCheckUpdateActivated = prefs.getBoolean("isCheckingUpdate", true);
        PreferencesUtil.isReportingBugs = prefs.getBoolean("isReportingBugs", true);
        PreferencesUtil.evasionIsEnabled = prefs.getBoolean("isEvading", false);
        PreferencesUtil.isFollowingRedirection = prefs.getBoolean("isFollowingRedirection", false);
        
        PreferencesUtil.pathFile = prefs.get("pathFile", System.getProperty("user.dir"));
    }
    
    public static void setPath(String path) {
        PreferencesUtil.pathFile = path;
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", PreferencesUtil.pathFile);
    }
    
    public static void set(boolean isCheckingUpdate, boolean isReportingBugs, boolean isEvading, boolean isFollowingRedirection) {
        PreferencesUtil.isCheckUpdateActivated = isCheckingUpdate;
        PreferencesUtil.isReportingBugs = isReportingBugs;
        PreferencesUtil.evasionIsEnabled = isEvading;
        PreferencesUtil.isFollowingRedirection = isFollowingRedirection;

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.isCheckUpdateActivated);
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs);
        preferences.putBoolean("isEvading", PreferencesUtil.evasionIsEnabled);
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection);
    }
    
}

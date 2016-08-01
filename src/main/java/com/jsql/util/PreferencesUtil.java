package com.jsql.util;

import java.net.HttpURLConnection;
import java.util.prefs.Preferences;

import com.jsql.model.InjectionModel;

public class PreferencesUtil {

    /**
     * File path saved in preference.
     */
    private static String pathFile;

    /**
     * True if connection is proxified.
     */
    private static boolean isCheckUpdateActivated = true;

    /**
     * True if evasion techniques should be used.
     */
    private static boolean evasionIsEnabled = false;

    /**
     * True to follow HTTP 302 redirection.
     */
    private static boolean isFollowingRedirection = false;

    /**
     * True if connection is proxified.
     */
    private static boolean isReportingBugs = true;

    private PreferencesUtil() {
        // Utility class
    }
    
    public static void loadSavedPreferences() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        PreferencesUtil.setCheckUpdateActivated(prefs.getBoolean("isCheckingUpdate", true));
        PreferencesUtil.setReportingBugs(prefs.getBoolean("isReportingBugs", true));
        PreferencesUtil.setEvasionIsEnabled(prefs.getBoolean("isEvading", false));
        PreferencesUtil.setFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        
        PreferencesUtil.pathFile = prefs.get("pathFile", System.getProperty("user.dir"));
        
        HttpURLConnection.setFollowRedirects(PreferencesUtil.isFollowingRedirection());
    }
    
    public static void setPath(String path) {
        PreferencesUtil.pathFile = path;
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", PreferencesUtil.pathFile);
    }
    
    public static void set(boolean isCheckingUpdate, boolean isReportingBugs, boolean isEvading, boolean isFollowingRedirection) {
        PreferencesUtil.setCheckUpdateActivated(isCheckingUpdate);
        PreferencesUtil.setReportingBugs(isReportingBugs);
        PreferencesUtil.setEvasionIsEnabled(isEvading);
        PreferencesUtil.setFollowingRedirection(isFollowingRedirection);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.checkUpdateIsActivated());
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs());
        preferences.putBoolean("isEvading", PreferencesUtil.isEvasionIsEnabled());
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection());
        
        HttpURLConnection.setFollowRedirects(PreferencesUtil.isFollowingRedirection());
    }

    public static String getPathFile() {
        return pathFile;
    }
    
    public static boolean checkUpdateIsActivated() {
        return isCheckUpdateActivated;
    }

    public static void setCheckUpdateActivated(boolean isCheckUpdateActivated) {
        PreferencesUtil.isCheckUpdateActivated = isCheckUpdateActivated;
    }

    public static boolean isEvasionIsEnabled() {
        return evasionIsEnabled;
    }

    public static void setEvasionIsEnabled(boolean evasionIsEnabled) {
        PreferencesUtil.evasionIsEnabled = evasionIsEnabled;
    }
    
    public static boolean isFollowingRedirection() {
        return isFollowingRedirection;
    }

    public static void setFollowingRedirection(boolean isFollowingRedirection) {
        PreferencesUtil.isFollowingRedirection = isFollowingRedirection;
    }
    
    public static boolean isReportingBugs() {
        return isReportingBugs;
    }

    public static void setReportingBugs(boolean isReportingBugs) {
        PreferencesUtil.isReportingBugs = isReportingBugs;
    }    
}

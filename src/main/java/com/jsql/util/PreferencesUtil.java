package com.jsql.util;

import java.net.HttpURLConnection;
import java.util.prefs.Preferences;

import com.jsql.model.InjectionModel;

/**
 * Utility class to manage JVM preferences previously saved into the system.
 * Only general settings are processed by this utility, other specific preferences
 * like those for proxy are defined from specific utility classes.
 */
public class PreferencesUtil {

    /**
     * File path saved in preference.
     */
    private static String pathFile;

    /**
     * True if updates are checked on startup.
     */
    private static boolean isCheckUpdateActivated = true;

    /**
     * True if evasion techniques should be used.
     */
    private static boolean isEvasionEnabled = false;

    /**
     * True if HTTP 302 redirection are followed to the new URL.
     */
    private static boolean isFollowingRedirection = false;

    /**
     * True if bugs are sent to Github.
     */
    private static boolean isReportingBugs = true;

    // Utility class
    private PreferencesUtil() {
        // not called
    }
    
    /**
     * Initialize the utility class with previously saved JVM preferences and apply
     * loaded settings to the system.
     */
    public static void loadSavedPreferences() {
    	
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        PreferencesUtil.setIsCheckUpdateActivated(prefs.getBoolean("isCheckingUpdate", true));
        PreferencesUtil.setReportingBugs(prefs.getBoolean("isReportingBugs", true));
        PreferencesUtil.setEvasionIsEnabled(prefs.getBoolean("isEvading", false));
        PreferencesUtil.setFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        PreferencesUtil.setPathFile(prefs.get("pathFile", System.getProperty("user.dir")));
        
        HttpURLConnection.setFollowRedirects(PreferencesUtil.isFollowingRedirection());
        
    }
    
    /**
     * Set the general file path to the utility class and persist to JVM preferences.
     * @param path folder path to persist
     */
    public static void set(String path) {
    	
        PreferencesUtil.setPathFile(path);
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", PreferencesUtil.pathFile);
        
    }
    
    /**
     * Initialize the utility class, persist preferences and
     * apply change to the JVM.
     * @param isCheckingUpdate true if it checks to updates
     * @param isReportingBugs true if it reports issues
     * @param isEvading true if evasion is activated
     * @param isFollowingRedirection true if redirection are followed to new URL destination
     */
    public static void set(boolean isCheckingUpdate, boolean isReportingBugs, boolean isEvading, boolean isFollowingRedirection) {
    	
        PreferencesUtil.setIsCheckUpdateActivated(isCheckingUpdate);
        PreferencesUtil.setReportingBugs(isReportingBugs);
        PreferencesUtil.setEvasionIsEnabled(isEvading);
        PreferencesUtil.setFollowingRedirection(isFollowingRedirection);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.isCheckUpdateActivated());
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs());
        preferences.putBoolean("isEvading", PreferencesUtil.isEvasionEnabled());
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection());
        
        HttpURLConnection.setFollowRedirects(PreferencesUtil.isFollowingRedirection());
        
    }

    // Getters and setters
    
    public static String getPathFile() {
        return PreferencesUtil.pathFile;
    }
    
    public static void setPathFile(String pathFile) {
    	PreferencesUtil.pathFile = pathFile;
    }
    
    public static boolean isCheckUpdateActivated() {
        return PreferencesUtil.isCheckUpdateActivated;
    }

    public static void setIsCheckUpdateActivated(boolean isCheckUpdateActivated) {
        PreferencesUtil.isCheckUpdateActivated = isCheckUpdateActivated;
    }

    public static boolean isEvasionEnabled() {
        return PreferencesUtil.isEvasionEnabled;
    }

    public static void setEvasionIsEnabled(boolean isEvasionEnabled) {
        PreferencesUtil.isEvasionEnabled = isEvasionEnabled;
    }
    
    public static boolean isFollowingRedirection() {
        return PreferencesUtil.isFollowingRedirection;
    }

    public static void setFollowingRedirection(boolean isFollowingRedirection) {
        PreferencesUtil.isFollowingRedirection = isFollowingRedirection;
    }
    
    public static boolean isReportingBugs() {
        return PreferencesUtil.isReportingBugs;
    }

    public static void setReportingBugs(boolean isReportingBugs) {
        PreferencesUtil.isReportingBugs = isReportingBugs;
    }
    
}

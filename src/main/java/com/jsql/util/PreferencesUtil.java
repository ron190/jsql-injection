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
    
    private static boolean isInjectingMetadata = true;
    
    private static boolean isCheckingAllParam = false;
    private static boolean isCheckingAllURLParam = false;
    private static boolean isCheckingAllRequestParam = false;
    private static boolean isCheckingAllHeaderParam = false;
    private static boolean isCheckingAllJSONParam = false;
    private static boolean isCheckingAllCookieParam = false;
    
    private static boolean isParsingForm = false;
    private static boolean isNotTestingConnection = false;
    private static boolean isProcessingCookies = false;
    private static boolean isProcessingCsrf = false;

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
        
        PreferencesUtil.setPathFile(prefs.get("pathFile", System.getProperty("user.dir")));
        
        PreferencesUtil.setIsCheckUpdateActivated(prefs.getBoolean("isCheckingUpdate", true));
        PreferencesUtil.setReportingBugs(prefs.getBoolean("isReportingBugs", true));
        PreferencesUtil.setEvasionIsEnabled(prefs.getBoolean("isEvading", false));
        PreferencesUtil.setFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        PreferencesUtil.setInjectingMetadata(prefs.getBoolean("isInjectingMetadata", true));
        
        PreferencesUtil.setCheckingAllParam(prefs.getBoolean("isCheckingAllParam", false));
        PreferencesUtil.setCheckingAllURLParam(prefs.getBoolean("isCheckingAllURLParam", false));
        PreferencesUtil.setCheckingAllRequestParam(prefs.getBoolean("isCheckingAllRequestParam", false));
        PreferencesUtil.setCheckingAllHeaderParam(prefs.getBoolean("isCheckingAllHeaderParam", false));
        PreferencesUtil.setCheckingAllJSONParam(prefs.getBoolean("isCheckingAllJSONParam", false));
        PreferencesUtil.setCheckingAllCookieParam(prefs.getBoolean("isCheckingAllCookieParam", false));
        
        PreferencesUtil.setParsingForm(prefs.getBoolean("isParsingForm", false));
        PreferencesUtil.setNotTestingConnection(prefs.getBoolean("isNotTestingConnection", false));
        PreferencesUtil.setProcessingCookies(prefs.getBoolean("isProcessingCookies", false));
        PreferencesUtil.setProcessingCsrf(prefs.getBoolean("isProcessingCsrf", false));
        
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
    public static void set(
        boolean isCheckingUpdate,
        boolean isReportingBugs,
        boolean isEvading,
        boolean isFollowingRedirection,
        boolean isInjectingMetadata,
        
        boolean isCheckingAllParam,
        boolean isCheckingAllURLParam,
        boolean isCheckingAllRequestParam,
        boolean isCheckingAllHeaderParam,
        boolean isCheckingAllJSONParam,
        boolean isCheckingAllCookieParam,
        
        boolean isParsingForm,
        boolean isNotTestingConnection,
        boolean isProcessingCookies,
        boolean isProcessingCsrf
    ) {
    	
        PreferencesUtil.setIsCheckUpdateActivated(isCheckingUpdate);
        PreferencesUtil.setReportingBugs(isReportingBugs);
        PreferencesUtil.setEvasionIsEnabled(isEvading);
        PreferencesUtil.setFollowingRedirection(isFollowingRedirection);
        PreferencesUtil.setInjectingMetadata(isInjectingMetadata);
        
        PreferencesUtil.setCheckingAllParam(isCheckingAllParam);
        PreferencesUtil.setCheckingAllURLParam(isCheckingAllURLParam);
        PreferencesUtil.setCheckingAllRequestParam(isCheckingAllRequestParam);
        PreferencesUtil.setCheckingAllHeaderParam(isCheckingAllHeaderParam);
        PreferencesUtil.setCheckingAllJSONParam(isCheckingAllJSONParam);
        PreferencesUtil.setCheckingAllCookieParam(isCheckingAllCookieParam);
        
        PreferencesUtil.setParsingForm(isParsingForm);
        PreferencesUtil.setNotTestingConnection(isNotTestingConnection);
        
        PreferencesUtil.setProcessingCookies(isProcessingCookies);
        PreferencesUtil.setProcessingCsrf(isProcessingCsrf);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.isCheckUpdateActivated());
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs());
        preferences.putBoolean("isEvading", PreferencesUtil.isEvasionEnabled());
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection());
        preferences.putBoolean("isInjectingMetadata", PreferencesUtil.isInjectingMetadata());
        
        preferences.putBoolean("isCheckingAllParam", PreferencesUtil.isCheckingAllParam());
        preferences.putBoolean("isCheckingAllURLParam", PreferencesUtil.isCheckingAllURLParam());
        preferences.putBoolean("isCheckingAllRequestParam", PreferencesUtil.isCheckingAllRequestParam());
        preferences.putBoolean("isCheckingAllHeaderParam", PreferencesUtil.isCheckingAllHeaderParam());
        preferences.putBoolean("isCheckingAllJSONParam", PreferencesUtil.isCheckingAllJSONParam());
        preferences.putBoolean("isCheckingAllCookieParam", PreferencesUtil.isCheckingAllCookieParam());
        
        preferences.putBoolean("isParsingForm", PreferencesUtil.isParsingForm());
        preferences.putBoolean("isNotTestingConnection", PreferencesUtil.isNotTestingConnection());
        
        preferences.putBoolean("isProcessingCookies", PreferencesUtil.isProcessingCookies());
        preferences.putBoolean("isProcessingCsrf", PreferencesUtil.isProcessingCsrf());
        
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

    public static boolean isInjectingMetadata() {
        return PreferencesUtil.isInjectingMetadata;
    }

    public static void setInjectingMetadata(boolean isInjectingMetadata) {
        PreferencesUtil.isInjectingMetadata = isInjectingMetadata;
    }

    public static boolean isCheckingAllURLParam() {
        return isCheckingAllURLParam;
    }

    public static void setCheckingAllURLParam(boolean isCheckingAllURLParam) {
        PreferencesUtil.isCheckingAllURLParam = isCheckingAllURLParam;
    }

    public static boolean isCheckingAllRequestParam() {
        return isCheckingAllRequestParam;
    }

    public static void setCheckingAllRequestParam(boolean isCheckingAllRequestParam) {
        PreferencesUtil.isCheckingAllRequestParam = isCheckingAllRequestParam;
    }

    public static boolean isCheckingAllHeaderParam() {
        return isCheckingAllHeaderParam;
    }

    public static void setCheckingAllHeaderParam(boolean isCheckingAllHeaderParam) {
        PreferencesUtil.isCheckingAllHeaderParam = isCheckingAllHeaderParam;
    }

    public static boolean isCheckingAllJSONParam() {
        return isCheckingAllJSONParam;
    }

    public static void setCheckingAllJSONParam(boolean isCheckingAllJSONParam) {
        PreferencesUtil.isCheckingAllJSONParam = isCheckingAllJSONParam;
    }

    public static boolean isParsingForm() {
        return isParsingForm;
    }

    public static void setParsingForm(boolean isParsingForm) {
        PreferencesUtil.isParsingForm = isParsingForm;
    }

    public static boolean isNotTestingConnection() {
        return isNotTestingConnection;
    }

    public static void setNotTestingConnection(boolean isNotTestingConnection) {
        PreferencesUtil.isNotTestingConnection = isNotTestingConnection;
    }

    public static void setCheckUpdateActivated(boolean isCheckUpdateActivated) {
        PreferencesUtil.isCheckUpdateActivated = isCheckUpdateActivated;
    }

    public static void setEvasionEnabled(boolean isEvasionEnabled) {
        PreferencesUtil.isEvasionEnabled = isEvasionEnabled;
    }

    public static boolean isProcessingCookies() {
        return isProcessingCookies;
    }

    public static void setProcessingCookies(boolean isProcessingCookies) {
        PreferencesUtil.isProcessingCookies = isProcessingCookies;
    }

    public static boolean isCheckingAllParam() {
        return isCheckingAllParam;
    }

    public static void setCheckingAllParam(boolean isCheckingAllParam) {
        PreferencesUtil.isCheckingAllParam = isCheckingAllParam;
    }

    public static boolean isProcessingCsrf() {
        return isProcessingCsrf;
    }

    public static void setProcessingCsrf(boolean isProcessingCsrf) {
        PreferencesUtil.isProcessingCsrf = isProcessingCsrf;
    }

    public static boolean isCheckingAllCookieParam() {
        return isCheckingAllCookieParam;
    }

    public static void setCheckingAllCookieParam(boolean isCheckingAllCookieParam) {
        PreferencesUtil.isCheckingAllCookieParam = isCheckingAllCookieParam;
    }
    
}

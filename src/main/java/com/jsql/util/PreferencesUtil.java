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
    
    private static boolean isNotInjectingMetadata = false;
    
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
    
    private static boolean isTamperingBase64 = false;
    private static boolean isTamperingFunctionComment = false;
    private static boolean isTamperingEqualToLike = false;
    private static boolean isTamperingRandomCase = false;
    private static boolean isTamperingSpaceToMultlineComment = false;
    private static boolean isTamperingSpaceToDashComment = false;
    private static boolean isTamperingSpaceToSharpComment = false;

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
        PreferencesUtil.setNotInjectingMetadata(prefs.getBoolean("isNotInjectingMetadata", false));
        
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
        
        PreferencesUtil.setTamperingBase64(prefs.getBoolean("isTamperingBase64", false));
        PreferencesUtil.setTamperingEqualToLike(prefs.getBoolean("isTamperingEqualToLike", false));
        PreferencesUtil.setTamperingFunctionComment(prefs.getBoolean("isTamperingFunctionComment", false));
        PreferencesUtil.setTamperingRandomCase(prefs.getBoolean("isTamperingRandomCase", false));
        PreferencesUtil.setTamperingSpaceToDashComment(prefs.getBoolean("isTamperingSpaceToDashComment", false));
        PreferencesUtil.setTamperingSpaceToMultlineComment(prefs.getBoolean("isTamperingSpaceToMultlineComment", false));
        PreferencesUtil.setTamperingSpaceToSharpComment(prefs.getBoolean("isTamperingSpaceToSharpComment", false));
        
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
        boolean isNotInjectingMetadata,
        
        boolean isCheckingAllParam,
        boolean isCheckingAllURLParam,
        boolean isCheckingAllRequestParam,
        boolean isCheckingAllHeaderParam,
        boolean isCheckingAllJSONParam,
        boolean isCheckingAllCookieParam,
        
        boolean isParsingForm,
        boolean isNotTestingConnection,
        boolean isProcessingCookies,
        boolean isProcessingCsrf,
        
        boolean isTamperingBase64,
        boolean isTamperingEqualToLike,
        boolean isTamperingFunctionComment,
        boolean isTamperingRandomCase,
        boolean isTamperingSpaceToDashComment,
        boolean isTamperingSpaceToMultlineComment,
        boolean isTamperingSpaceToSharpComment
    ) {
    	
        PreferencesUtil.setIsCheckUpdateActivated(isCheckingUpdate);
        PreferencesUtil.setReportingBugs(isReportingBugs);
        PreferencesUtil.setEvasionIsEnabled(isEvading);
        PreferencesUtil.setFollowingRedirection(isFollowingRedirection);
        PreferencesUtil.setNotInjectingMetadata(isNotInjectingMetadata);
        
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
        
        PreferencesUtil.setTamperingBase64(isTamperingBase64);
        PreferencesUtil.setTamperingEqualToLike(isTamperingEqualToLike);
        PreferencesUtil.setTamperingFunctionComment(isTamperingFunctionComment);
        PreferencesUtil.setTamperingRandomCase(isTamperingRandomCase);
        PreferencesUtil.setTamperingSpaceToDashComment(isTamperingSpaceToDashComment);
        PreferencesUtil.setTamperingSpaceToMultlineComment(isTamperingSpaceToMultlineComment);
        PreferencesUtil.setTamperingSpaceToSharpComment(isTamperingSpaceToSharpComment);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.isCheckUpdateActivated());
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs());
        preferences.putBoolean("isEvading", PreferencesUtil.isEvasionEnabled());
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection());
        preferences.putBoolean("isNotInjectingMetadata", PreferencesUtil.isNotInjectingMetadata());
        
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
        
        preferences.putBoolean("isTamperingBase64", PreferencesUtil.isTamperingBase64());
        preferences.putBoolean("isTamperingEqualToLike", PreferencesUtil.isTamperingEqualToLike());
        preferences.putBoolean("isTamperingFunctionComment", PreferencesUtil.isTamperingFunctionComment());
        preferences.putBoolean("isTamperingRandomCase", PreferencesUtil.isTamperingRandomCase());
        preferences.putBoolean("isTamperingSpaceToDashComment", PreferencesUtil.isTamperingSpaceToDashComment());
        preferences.putBoolean("isTamperingSpaceToMultlineComment", PreferencesUtil.isTamperingSpaceToMultlineComment());
        preferences.putBoolean("isTamperingSpaceToSharpComment", PreferencesUtil.isTamperingSpaceToSharpComment());
        
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

    public static boolean isNotInjectingMetadata() {
        return PreferencesUtil.isNotInjectingMetadata;
    }

    public static void setNotInjectingMetadata(boolean isNotInjectingMetadata) {
        PreferencesUtil.isNotInjectingMetadata = isNotInjectingMetadata;
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

    public static boolean isTamperingBase64() {
        return isTamperingBase64;
    }

    public static void setTamperingBase64(boolean isTamperingBase64) {
        PreferencesUtil.isTamperingBase64 = isTamperingBase64;
    }

    public static boolean isTamperingFunctionComment() {
        return isTamperingFunctionComment;
    }

    public static void setTamperingFunctionComment(boolean isTamperingFunctionComment) {
        PreferencesUtil.isTamperingFunctionComment = isTamperingFunctionComment;
    }

    public static boolean isTamperingEqualToLike() {
        return isTamperingEqualToLike;
    }

    public static void setTamperingEqualToLike(boolean isTamperingEqualToLike) {
        PreferencesUtil.isTamperingEqualToLike = isTamperingEqualToLike;
    }

    public static boolean isTamperingRandomCase() {
        return isTamperingRandomCase;
    }

    public static void setTamperingRandomCase(boolean isTamperingRandomCase) {
        PreferencesUtil.isTamperingRandomCase = isTamperingRandomCase;
    }

    public static boolean isTamperingSpaceToMultlineComment() {
        return isTamperingSpaceToMultlineComment;
    }

    public static void setTamperingSpaceToMultlineComment(boolean isTamperingSpaceToMultlineComment) {
        PreferencesUtil.isTamperingSpaceToMultlineComment = isTamperingSpaceToMultlineComment;
    }

    public static boolean isTamperingSpaceToDashComment() {
        return isTamperingSpaceToDashComment;
    }

    public static void setTamperingSpaceToDashComment(boolean isTamperingSpaceToDashComment) {
        PreferencesUtil.isTamperingSpaceToDashComment = isTamperingSpaceToDashComment;
    }

    public static boolean isTamperingSpaceToSharpComment() {
        return isTamperingSpaceToSharpComment;
    }

    public static void setTamperingSpaceToSharpComment(boolean isTamperingSpaceToSharpComment) {
        PreferencesUtil.isTamperingSpaceToSharpComment = isTamperingSpaceToSharpComment;
    }
    
}

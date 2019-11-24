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
    private static boolean isCheckingAllSOAPParam = false;
    
    private static boolean isParsingForm = false;
    private static boolean isNotTestingConnection = false;
    private static boolean isProcessingCookies = false;
    private static boolean isProcessingCsrf = false;
    
    private static boolean isTamperingBase64 = false;
    private static boolean isTamperingFunctionComment = false;
    private static boolean isTamperingVersionComment = false;
    private static boolean isTamperingEqualToLike = false;
    private static boolean isTamperingRandomCase = false;
    private static boolean isTamperingEval = false;
    private static boolean isTamperingSpaceToMultlineComment = false;
    private static boolean isTamperingSpaceToDashComment = false;
    private static boolean isTamperingSpaceToSharpComment = false;

    /**
     * True if bugs are sent to Github.
     */
    private static boolean isReportingBugs = true;
    private static boolean is4K = true;

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
        
        PreferencesUtil.setFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        PreferencesUtil.setNotInjectingMetadata(prefs.getBoolean("isNotInjectingMetadata", false));
        
        PreferencesUtil.setCheckingAllParam(prefs.getBoolean("isCheckingAllParam", false));
        PreferencesUtil.setCheckingAllURLParam(prefs.getBoolean("isCheckingAllURLParam", false));
        PreferencesUtil.setCheckingAllRequestParam(prefs.getBoolean("isCheckingAllRequestParam", false));
        PreferencesUtil.setCheckingAllHeaderParam(prefs.getBoolean("isCheckingAllHeaderParam", false));
        PreferencesUtil.setCheckingAllJSONParam(prefs.getBoolean("isCheckingAllJSONParam", false));
        PreferencesUtil.setCheckingAllCookieParam(prefs.getBoolean("isCheckingAllCookieParam", false));
        PreferencesUtil.setCheckingAllSOAPParam(prefs.getBoolean("isCheckingAllSOAPParam", false));
        
        PreferencesUtil.setParsingForm(prefs.getBoolean("isParsingForm", false));
        PreferencesUtil.setNotTestingConnection(prefs.getBoolean("isNotTestingConnection", false));
        PreferencesUtil.setProcessingCookies(prefs.getBoolean("isProcessingCookies", false));
        PreferencesUtil.setProcessingCsrf(prefs.getBoolean("isProcessingCsrf", false));
        
        PreferencesUtil.setTamperingBase64(prefs.getBoolean("isTamperingBase64", false));
        PreferencesUtil.setTamperingEqualToLike(prefs.getBoolean("isTamperingEqualToLike", false));
        PreferencesUtil.setTamperingFunctionComment(prefs.getBoolean("isTamperingFunctionComment", false));
        PreferencesUtil.setTamperingVersionComment(prefs.getBoolean("isTamperingVersionComment", false));
        PreferencesUtil.setTamperingRandomCase(prefs.getBoolean("isTamperingRandomCase", false));
        PreferencesUtil.setTamperingEval(prefs.getBoolean("isTamperingEval", false));
        PreferencesUtil.setTamperingSpaceToDashComment(prefs.getBoolean("isTamperingSpaceToDashComment", false));
        PreferencesUtil.setTamperingSpaceToMultlineComment(prefs.getBoolean("isTamperingSpaceToMultlineComment", false));
        PreferencesUtil.setTamperingSpaceToSharpComment(prefs.getBoolean("isTamperingSpaceToSharpComment", false));
        
        PreferencesUtil.setIs4K(prefs.getBoolean("is4K", false));
        
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
     * @param isFollowingRedirection true if redirection are followed to new URL destination
     */
    public static void set(
        boolean isCheckingUpdate,
        boolean isReportingBugs,
        boolean isFollowingRedirection,
        boolean isNotInjectingMetadata,
        
        boolean isCheckingAllParam,
        boolean isCheckingAllURLParam,
        boolean isCheckingAllRequestParam,
        boolean isCheckingAllHeaderParam,
        boolean isCheckingAllJSONParam,
        boolean isCheckingAllCookieParam,
        boolean isCheckingAllSOAPParam,
        
        boolean isParsingForm,
        boolean isNotTestingConnection,
        boolean isProcessingCookies,
        boolean isProcessingCsrf,
        
        boolean isTamperingBase64,
        boolean isTamperingEqualToLike,
        boolean isTamperingFunctionComment,
        boolean isTamperingVersionComment,
        boolean isTamperingRandomCase,
        boolean isTamperingEval,
        boolean isTamperingSpaceToDashComment,
        boolean isTamperingSpaceToMultlineComment,
        boolean isTamperingSpaceToSharpComment,
        
        boolean is4K
    ) {
    	
        PreferencesUtil.setIsCheckUpdateActivated(isCheckingUpdate);
        PreferencesUtil.setReportingBugs(isReportingBugs);
        PreferencesUtil.setFollowingRedirection(isFollowingRedirection);
        PreferencesUtil.setNotInjectingMetadata(isNotInjectingMetadata);
        
        PreferencesUtil.setCheckingAllParam(isCheckingAllParam);
        PreferencesUtil.setCheckingAllURLParam(isCheckingAllURLParam);
        PreferencesUtil.setCheckingAllRequestParam(isCheckingAllRequestParam);
        PreferencesUtil.setCheckingAllHeaderParam(isCheckingAllHeaderParam);
        PreferencesUtil.setCheckingAllJSONParam(isCheckingAllJSONParam);
        PreferencesUtil.setCheckingAllCookieParam(isCheckingAllCookieParam);
        PreferencesUtil.setCheckingAllSOAPParam(isCheckingAllSOAPParam);
        
        PreferencesUtil.setParsingForm(isParsingForm);
        PreferencesUtil.setNotTestingConnection(isNotTestingConnection);
        
        PreferencesUtil.setProcessingCookies(isProcessingCookies);
        PreferencesUtil.setProcessingCsrf(isProcessingCsrf);
        
        PreferencesUtil.setTamperingBase64(isTamperingBase64);
        PreferencesUtil.setTamperingEqualToLike(isTamperingEqualToLike);
        PreferencesUtil.setTamperingFunctionComment(isTamperingFunctionComment);
        PreferencesUtil.setTamperingVersionComment(isTamperingVersionComment);
        PreferencesUtil.setTamperingRandomCase(isTamperingRandomCase);
        PreferencesUtil.setTamperingEval(isTamperingEval);
        PreferencesUtil.setTamperingSpaceToDashComment(isTamperingSpaceToDashComment);
        PreferencesUtil.setTamperingSpaceToMultlineComment(isTamperingSpaceToMultlineComment);
        PreferencesUtil.setTamperingSpaceToSharpComment(isTamperingSpaceToSharpComment);
        
        PreferencesUtil.setIs4K(is4K);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", PreferencesUtil.isCheckUpdateActivated());
        preferences.putBoolean("isReportingBugs", PreferencesUtil.isReportingBugs());
        preferences.putBoolean("isFollowingRedirection", PreferencesUtil.isFollowingRedirection());
        preferences.putBoolean("isNotInjectingMetadata", PreferencesUtil.isNotInjectingMetadata());
        
        preferences.putBoolean("isCheckingAllParam", PreferencesUtil.isCheckingAllParam());
        preferences.putBoolean("isCheckingAllURLParam", PreferencesUtil.isCheckingAllURLParam());
        preferences.putBoolean("isCheckingAllRequestParam", PreferencesUtil.isCheckingAllRequestParam());
        preferences.putBoolean("isCheckingAllHeaderParam", PreferencesUtil.isCheckingAllHeaderParam());
        preferences.putBoolean("isCheckingAllJSONParam", PreferencesUtil.isCheckingAllJSONParam());
        preferences.putBoolean("isCheckingAllCookieParam", PreferencesUtil.isCheckingAllCookieParam());
        preferences.putBoolean("isCheckingAllSOAPParam", PreferencesUtil.isCheckingAllSOAPParam());
        
        preferences.putBoolean("isParsingForm", PreferencesUtil.isParsingForm());
        preferences.putBoolean("isNotTestingConnection", PreferencesUtil.isNotTestingConnection());
        
        preferences.putBoolean("isProcessingCookies", PreferencesUtil.isProcessingCookies());
        preferences.putBoolean("isProcessingCsrf", PreferencesUtil.isProcessingCsrf());
        
        preferences.putBoolean("isTamperingBase64", PreferencesUtil.isTamperingBase64());
        preferences.putBoolean("isTamperingEqualToLike", PreferencesUtil.isTamperingEqualToLike());
        preferences.putBoolean("isTamperingVersionComment", PreferencesUtil.isTamperingVersionComment());
        preferences.putBoolean("isTamperingFunctionComment", PreferencesUtil.isTamperingFunctionComment());
        preferences.putBoolean("isTamperingRandomCase", PreferencesUtil.isTamperingRandomCase());
        preferences.putBoolean("isTamperingEval", PreferencesUtil.isTamperingEval());
        preferences.putBoolean("isTamperingSpaceToDashComment", PreferencesUtil.isTamperingSpaceToDashComment());
        preferences.putBoolean("isTamperingSpaceToMultlineComment", PreferencesUtil.isTamperingSpaceToMultlineComment());
        preferences.putBoolean("isTamperingSpaceToSharpComment", PreferencesUtil.isTamperingSpaceToSharpComment());
        
        preferences.putBoolean("is4K", PreferencesUtil.is4K());
        
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

    public static boolean isTamperingVersionComment() {
        return isTamperingVersionComment;
    }

    public static void setTamperingVersionComment(boolean isTamperingVersionComment) {
        PreferencesUtil.isTamperingVersionComment = isTamperingVersionComment;
    }

    public static boolean isTamperingEval() {
        return isTamperingEval;
    }

    public static void setTamperingEval(boolean isTamperingEval) {
        PreferencesUtil.isTamperingEval = isTamperingEval;
    }

    public static boolean isCheckingAllSOAPParam() {
        return isCheckingAllSOAPParam;
    }

    public static void setCheckingAllSOAPParam(boolean isCheckingAllSOAPParam) {
        PreferencesUtil.isCheckingAllSOAPParam = isCheckingAllSOAPParam;
    }

    public static boolean is4K() {
        return is4K;
    }

    public static void setIs4K(boolean is4k) {
        is4K = is4k;
    }
    
}

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
    private String pathFile;

    /**
     * True if updates are checked on startup.
     */
    private boolean isCheckUpdateActivated = true;

    /**
     * True if HTTP 302 redirection are followed to the new URL.
     */
    private boolean isFollowingRedirection = false;
    
    private boolean isNotInjectingMetadata = false;
    
    private boolean isCheckingAllParam = false;
    private boolean isCheckingAllURLParam = false;
    private boolean isCheckingAllRequestParam = false;
    private boolean isCheckingAllHeaderParam = false;
    private boolean isCheckingAllJSONParam = false;
    private boolean isCheckingAllCookieParam = false;
    private boolean isCheckingAllSOAPParam = false;
    
    private boolean isParsingForm = false;
    private boolean isNotTestingConnection = false;
    private boolean isProcessingCookies = false;
    private boolean isProcessingCsrf = false;
    
    private boolean isTamperingBase64 = false;
    private boolean isTamperingFunctionComment = false;
    private boolean isTamperingVersionComment = false;
    private boolean isTamperingEqualToLike = false;
    private boolean isTamperingRandomCase = false;
    private boolean isTamperingEval = false;
    private boolean isTamperingSpaceToMultlineComment = false;
    private boolean isTamperingSpaceToDashComment = false;
    private boolean isTamperingSpaceToSharpComment = false;

    /**
     * True if bugs are sent to Github.
     */
    private boolean isReportingBugs = true;
    private boolean is4K = true;

    // Utility class
    private PreferencesUtil() {
        // not called
    }
    
    public PreferencesUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Initialize the utility class with previously saved JVM preferences and apply
     * loaded settings to the system.
     */
    public void loadSavedPreferences() {
    	
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        setPathFile(prefs.get("pathFile", System.getProperty("user.dir")));
        
        setIsCheckUpdateActivated(prefs.getBoolean("isCheckingUpdate", true));
        setReportingBugs(prefs.getBoolean("isReportingBugs", true));
        
        setFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        setNotInjectingMetadata(prefs.getBoolean("isNotInjectingMetadata", false));
        
        setCheckingAllParam(prefs.getBoolean("isCheckingAllParam", false));
        setCheckingAllURLParam(prefs.getBoolean("isCheckingAllURLParam", false));
        setCheckingAllRequestParam(prefs.getBoolean("isCheckingAllRequestParam", false));
        setCheckingAllHeaderParam(prefs.getBoolean("isCheckingAllHeaderParam", false));
        setCheckingAllJSONParam(prefs.getBoolean("isCheckingAllJSONParam", false));
        setCheckingAllCookieParam(prefs.getBoolean("isCheckingAllCookieParam", false));
        setCheckingAllSOAPParam(prefs.getBoolean("isCheckingAllSOAPParam", false));
        
        setParsingForm(prefs.getBoolean("isParsingForm", false));
        setNotTestingConnection(prefs.getBoolean("isNotTestingConnection", false));
        setProcessingCookies(prefs.getBoolean("isProcessingCookies", false));
        setProcessingCsrf(prefs.getBoolean("isProcessingCsrf", false));
        
        setTamperingBase64(prefs.getBoolean("isTamperingBase64", false));
        setTamperingEqualToLike(prefs.getBoolean("isTamperingEqualToLike", false));
        setTamperingFunctionComment(prefs.getBoolean("isTamperingFunctionComment", false));
        setTamperingVersionComment(prefs.getBoolean("isTamperingVersionComment", false));
        setTamperingRandomCase(prefs.getBoolean("isTamperingRandomCase", false));
        setTamperingEval(prefs.getBoolean("isTamperingEval", false));
        setTamperingSpaceToDashComment(prefs.getBoolean("isTamperingSpaceToDashComment", false));
        setTamperingSpaceToMultlineComment(prefs.getBoolean("isTamperingSpaceToMultlineComment", false));
        setTamperingSpaceToSharpComment(prefs.getBoolean("isTamperingSpaceToSharpComment", false));
        
        setIs4K(prefs.getBoolean("is4K", false));
        
        HttpURLConnection.setFollowRedirects(isFollowingRedirection());
        
    }
    
    /**
     * Set the general file path to the utility class and persist to JVM preferences.
     * @param path folder path to persist
     */
    public void set(String path) {
    	
        setPathFile(path);
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", pathFile);
        
    }
    
    /**
     * Initialize the utility class, persist preferences and
     * apply change to the JVM.
     * @param isCheckingUpdate true if it checks to updates
     * @param isReportingBugs true if it reports issues
     * @param isFollowingRedirection true if redirection are followed to new URL destination
     */
    public void set(
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
    	
        setIsCheckUpdateActivated(isCheckingUpdate);
        setReportingBugs(isReportingBugs);
        setFollowingRedirection(isFollowingRedirection);
        setNotInjectingMetadata(isNotInjectingMetadata);
        
        setCheckingAllParam(isCheckingAllParam);
        setCheckingAllURLParam(isCheckingAllURLParam);
        setCheckingAllRequestParam(isCheckingAllRequestParam);
        setCheckingAllHeaderParam(isCheckingAllHeaderParam);
        setCheckingAllJSONParam(isCheckingAllJSONParam);
        setCheckingAllCookieParam(isCheckingAllCookieParam);
        setCheckingAllSOAPParam(isCheckingAllSOAPParam);
        
        setParsingForm(isParsingForm);
        setNotTestingConnection(isNotTestingConnection);
        
        setProcessingCookies(isProcessingCookies);
        setProcessingCsrf(isProcessingCsrf);
        
        setTamperingBase64(isTamperingBase64);
        setTamperingEqualToLike(isTamperingEqualToLike);
        setTamperingFunctionComment(isTamperingFunctionComment);
        setTamperingVersionComment(isTamperingVersionComment);
        setTamperingRandomCase(isTamperingRandomCase);
        setTamperingEval(isTamperingEval);
        setTamperingSpaceToDashComment(isTamperingSpaceToDashComment);
        setTamperingSpaceToMultlineComment(isTamperingSpaceToMultlineComment);
        setTamperingSpaceToSharpComment(isTamperingSpaceToSharpComment);
        
        setIs4K(is4K);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", isCheckUpdateActivated());
        preferences.putBoolean("isReportingBugs", isReportingBugs());
        preferences.putBoolean("isFollowingRedirection", isFollowingRedirection());
        preferences.putBoolean("isNotInjectingMetadata", isNotInjectingMetadata());
        
        preferences.putBoolean("isCheckingAllParam", isCheckingAllParam());
        preferences.putBoolean("isCheckingAllURLParam", isCheckingAllURLParam());
        preferences.putBoolean("isCheckingAllRequestParam", isCheckingAllRequestParam());
        preferences.putBoolean("isCheckingAllHeaderParam", isCheckingAllHeaderParam());
        preferences.putBoolean("isCheckingAllJSONParam", isCheckingAllJSONParam());
        preferences.putBoolean("isCheckingAllCookieParam", isCheckingAllCookieParam());
        preferences.putBoolean("isCheckingAllSOAPParam", isCheckingAllSOAPParam());
        
        preferences.putBoolean("isParsingForm", isParsingForm());
        preferences.putBoolean("isNotTestingConnection", isNotTestingConnection());
        
        preferences.putBoolean("isProcessingCookies", isProcessingCookies());
        preferences.putBoolean("isProcessingCsrf", isProcessingCsrf());
        
        preferences.putBoolean("isTamperingBase64", isTamperingBase64());
        preferences.putBoolean("isTamperingEqualToLike", isTamperingEqualToLike());
        preferences.putBoolean("isTamperingVersionComment", isTamperingVersionComment());
        preferences.putBoolean("isTamperingFunctionComment", isTamperingFunctionComment());
        preferences.putBoolean("isTamperingRandomCase", isTamperingRandomCase());
        preferences.putBoolean("isTamperingEval", isTamperingEval());
        preferences.putBoolean("isTamperingSpaceToDashComment", isTamperingSpaceToDashComment());
        preferences.putBoolean("isTamperingSpaceToMultlineComment", isTamperingSpaceToMultlineComment());
        preferences.putBoolean("isTamperingSpaceToSharpComment", isTamperingSpaceToSharpComment());
        
        preferences.putBoolean("is4K", is4K());
        
        HttpURLConnection.setFollowRedirects(isFollowingRedirection());
        
    }

    // Getters and setters
    
    public String getPathFile() {
        return pathFile;
    }
    
    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }
    
    public boolean isCheckUpdateActivated() {
        return isCheckUpdateActivated;
    }

    public void setIsCheckUpdateActivated(boolean isCheckUpdateActivated) {
        this.isCheckUpdateActivated = isCheckUpdateActivated;
    }
    
    public boolean isFollowingRedirection() {
        return isFollowingRedirection;
    }

    public void setFollowingRedirection(boolean isFollowingRedirection) {
        this.isFollowingRedirection = isFollowingRedirection;
    }
    
    public boolean isReportingBugs() {
        return isReportingBugs;
    }

    public void setReportingBugs(boolean isReportingBugs) {
        this.isReportingBugs = isReportingBugs;
    }

    public boolean isNotInjectingMetadata() {
        return isNotInjectingMetadata;
    }

    public void setNotInjectingMetadata(boolean isNotInjectingMetadata) {
        this.isNotInjectingMetadata = isNotInjectingMetadata;
    }

    public boolean isCheckingAllURLParam() {
        return isCheckingAllURLParam;
    }

    public void setCheckingAllURLParam(boolean isCheckingAllURLParam) {
        this.isCheckingAllURLParam = isCheckingAllURLParam;
    }

    public boolean isCheckingAllRequestParam() {
        return isCheckingAllRequestParam;
    }

    public void setCheckingAllRequestParam(boolean isCheckingAllRequestParam) {
        this.isCheckingAllRequestParam = isCheckingAllRequestParam;
    }

    public boolean isCheckingAllHeaderParam() {
        return isCheckingAllHeaderParam;
    }

    public void setCheckingAllHeaderParam(boolean isCheckingAllHeaderParam) {
        this.isCheckingAllHeaderParam = isCheckingAllHeaderParam;
    }

    public boolean isCheckingAllJSONParam() {
        return isCheckingAllJSONParam;
    }

    public void setCheckingAllJSONParam(boolean isCheckingAllJSONParam) {
        this.isCheckingAllJSONParam = isCheckingAllJSONParam;
    }

    public boolean isParsingForm() {
        return isParsingForm;
    }

    public void setParsingForm(boolean isParsingForm) {
        this.isParsingForm = isParsingForm;
    }

    public boolean isNotTestingConnection() {
        return isNotTestingConnection;
    }

    public void setNotTestingConnection(boolean isNotTestingConnection) {
        this.isNotTestingConnection = isNotTestingConnection;
    }

    public void setCheckUpdateActivated(boolean isCheckUpdateActivated) {
        this.isCheckUpdateActivated = isCheckUpdateActivated;
    }

    public boolean isProcessingCookies() {
        return isProcessingCookies;
    }

    public void setProcessingCookies(boolean isProcessingCookies) {
        this.isProcessingCookies = isProcessingCookies;
    }

    public boolean isCheckingAllParam() {
        return isCheckingAllParam;
    }

    public void setCheckingAllParam(boolean isCheckingAllParam) {
        this.isCheckingAllParam = isCheckingAllParam;
    }

    public boolean isProcessingCsrf() {
        return isProcessingCsrf;
    }

    public void setProcessingCsrf(boolean isProcessingCsrf) {
        this.isProcessingCsrf = isProcessingCsrf;
    }

    public boolean isCheckingAllCookieParam() {
        return isCheckingAllCookieParam;
    }

    public void setCheckingAllCookieParam(boolean isCheckingAllCookieParam) {
        this.isCheckingAllCookieParam = isCheckingAllCookieParam;
    }

    public boolean isTamperingBase64() {
        return isTamperingBase64;
    }

    public void setTamperingBase64(boolean isTamperingBase64) {
        this.isTamperingBase64 = isTamperingBase64;
    }

    public boolean isTamperingFunctionComment() {
        return isTamperingFunctionComment;
    }

    public void setTamperingFunctionComment(boolean isTamperingFunctionComment) {
        this.isTamperingFunctionComment = isTamperingFunctionComment;
    }

    public boolean isTamperingEqualToLike() {
        return isTamperingEqualToLike;
    }

    public void setTamperingEqualToLike(boolean isTamperingEqualToLike) {
        this.isTamperingEqualToLike = isTamperingEqualToLike;
    }

    public boolean isTamperingRandomCase() {
        return isTamperingRandomCase;
    }

    public void setTamperingRandomCase(boolean isTamperingRandomCase) {
        this.isTamperingRandomCase = isTamperingRandomCase;
    }

    public boolean isTamperingSpaceToMultlineComment() {
        return isTamperingSpaceToMultlineComment;
    }

    public void setTamperingSpaceToMultlineComment(boolean isTamperingSpaceToMultlineComment) {
        this.isTamperingSpaceToMultlineComment = isTamperingSpaceToMultlineComment;
    }

    public boolean isTamperingSpaceToDashComment() {
        return isTamperingSpaceToDashComment;
    }

    public void setTamperingSpaceToDashComment(boolean isTamperingSpaceToDashComment) {
        this.isTamperingSpaceToDashComment = isTamperingSpaceToDashComment;
    }

    public boolean isTamperingSpaceToSharpComment() {
        return isTamperingSpaceToSharpComment;
    }

    public void setTamperingSpaceToSharpComment(boolean isTamperingSpaceToSharpComment) {
        this.isTamperingSpaceToSharpComment = isTamperingSpaceToSharpComment;
    }

    public boolean isTamperingVersionComment() {
        return isTamperingVersionComment;
    }

    public void setTamperingVersionComment(boolean isTamperingVersionComment) {
        this.isTamperingVersionComment = isTamperingVersionComment;
    }

    public boolean isTamperingEval() {
        return isTamperingEval;
    }

    public void setTamperingEval(boolean isTamperingEval) {
        this.isTamperingEval = isTamperingEval;
    }

    public boolean isCheckingAllSOAPParam() {
        return isCheckingAllSOAPParam;
    }

    public void setCheckingAllSOAPParam(boolean isCheckingAllSOAPParam) {
        this.isCheckingAllSOAPParam = isCheckingAllSOAPParam;
    }

    public boolean is4K() {
        return is4K;
    }

    public void setIs4K(boolean is4k) {
        is4K = is4k;
    }
    
}

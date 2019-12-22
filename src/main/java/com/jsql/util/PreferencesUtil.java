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

    public PreferencesUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Initialize the utility class with previously saved JVM preferences and apply
     * loaded settings to the system.
     */
    // TODO Spock test
    public void loadSavedPreferences() {
    	
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        this.setPathFile(prefs.get("pathFile", System.getProperty("user.dir")));
        
        this.setIsCheckingUpdate(prefs.getBoolean("isCheckingUpdate", true));
        this.setReportingBugs(prefs.getBoolean("isReportingBugs", true));
        
        this.setFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        this.setNotInjectingMetadata(prefs.getBoolean("isNotInjectingMetadata", false));
        
        this.setCheckingAllParam(prefs.getBoolean("isCheckingAllParam", false));
        this.setCheckingAllURLParam(prefs.getBoolean("isCheckingAllURLParam", false));
        this.setCheckingAllRequestParam(prefs.getBoolean("isCheckingAllRequestParam", false));
        this.setCheckingAllHeaderParam(prefs.getBoolean("isCheckingAllHeaderParam", false));
        this.setCheckingAllJSONParam(prefs.getBoolean("isCheckingAllJSONParam", false));
        this.setCheckingAllCookieParam(prefs.getBoolean("isCheckingAllCookieParam", false));
        this.setCheckingAllSOAPParam(prefs.getBoolean("isCheckingAllSOAPParam", false));
        
        this.setParsingForm(prefs.getBoolean("isParsingForm", false));
        this.setNotTestingConnection(prefs.getBoolean("isNotTestingConnection", false));
        this.setProcessingCookies(prefs.getBoolean("isProcessingCookies", false));
        this.setProcessingCsrf(prefs.getBoolean("isProcessingCsrf", false));
        
        this.setTamperingBase64(prefs.getBoolean("isTamperingBase64", false));
        this.setTamperingEqualToLike(prefs.getBoolean("isTamperingEqualToLike", false));
        this.setTamperingFunctionComment(prefs.getBoolean("isTamperingFunctionComment", false));
        this.setTamperingVersionComment(prefs.getBoolean("isTamperingVersionComment", false));
        this.setTamperingRandomCase(prefs.getBoolean("isTamperingRandomCase", false));
        this.setTamperingEval(prefs.getBoolean("isTamperingEval", false));
        this.setTamperingSpaceToDashComment(prefs.getBoolean("isTamperingSpaceToDashComment", false));
        this.setTamperingSpaceToMultlineComment(prefs.getBoolean("isTamperingSpaceToMultlineComment", false));
        this.setTamperingSpaceToSharpComment(prefs.getBoolean("isTamperingSpaceToSharpComment", false));
        
        this.setIs4K(prefs.getBoolean("is4K", false));
        
        HttpURLConnection.setFollowRedirects(this.isFollowingRedirection());
        
    }
    
    /**
     * Set the general file path to the utility class and persist to JVM preferences.
     * @param path folder path to persist
     */
    public void set(String path) {
    	
        this.setPathFile(path);
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.put("pathFile", this.pathFile);
        
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
    	
        this.setIsCheckingUpdate(isCheckingUpdate);
        this.setReportingBugs(isReportingBugs);
        this.setFollowingRedirection(isFollowingRedirection);
        this.setNotInjectingMetadata(isNotInjectingMetadata);
        
        this.setCheckingAllParam(isCheckingAllParam);
        this.setCheckingAllURLParam(isCheckingAllURLParam);
        this.setCheckingAllRequestParam(isCheckingAllRequestParam);
        this.setCheckingAllHeaderParam(isCheckingAllHeaderParam);
        this.setCheckingAllJSONParam(isCheckingAllJSONParam);
        this.setCheckingAllCookieParam(isCheckingAllCookieParam);
        this.setCheckingAllSOAPParam(isCheckingAllSOAPParam);
        
        this.setParsingForm(isParsingForm);
        this.setNotTestingConnection(isNotTestingConnection);
        
        this.setProcessingCookies(isProcessingCookies);
        this.setProcessingCsrf(isProcessingCsrf);
        
        this.setTamperingBase64(isTamperingBase64);
        this.setTamperingEqualToLike(isTamperingEqualToLike);
        this.setTamperingFunctionComment(isTamperingFunctionComment);
        this.setTamperingVersionComment(isTamperingVersionComment);
        this.setTamperingRandomCase(isTamperingRandomCase);
        this.setTamperingEval(isTamperingEval);
        this.setTamperingSpaceToDashComment(isTamperingSpaceToDashComment);
        this.setTamperingSpaceToMultlineComment(isTamperingSpaceToMultlineComment);
        this.setTamperingSpaceToSharpComment(isTamperingSpaceToSharpComment);
        
        this.setIs4K(is4K);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", this.isCheckUpdateActivated());
        preferences.putBoolean("isReportingBugs", this.isReportingBugs());
        preferences.putBoolean("isFollowingRedirection", this.isFollowingRedirection());
        preferences.putBoolean("isNotInjectingMetadata", this.isNotInjectingMetadata());
        
        preferences.putBoolean("isCheckingAllParam", this.isCheckingAllParam());
        preferences.putBoolean("isCheckingAllURLParam", this.isCheckingAllURLParam());
        preferences.putBoolean("isCheckingAllRequestParam", this.isCheckingAllRequestParam());
        preferences.putBoolean("isCheckingAllHeaderParam", this.isCheckingAllHeaderParam());
        preferences.putBoolean("isCheckingAllJSONParam", this.isCheckingAllJSONParam());
        preferences.putBoolean("isCheckingAllCookieParam", this.isCheckingAllCookieParam());
        preferences.putBoolean("isCheckingAllSOAPParam", this.isCheckingAllSOAPParam());
        
        preferences.putBoolean("isParsingForm", this.isParsingForm());
        preferences.putBoolean("isNotTestingConnection", this.isNotTestingConnection());
        
        preferences.putBoolean("isProcessingCookies", this.isProcessingCookies());
        preferences.putBoolean("isProcessingCsrf", this.isProcessingCsrf());
        
        preferences.putBoolean("isTamperingBase64", this.isTamperingBase64());
        preferences.putBoolean("isTamperingEqualToLike", this.isTamperingEqualToLike());
        preferences.putBoolean("isTamperingVersionComment", this.isTamperingVersionComment());
        preferences.putBoolean("isTamperingFunctionComment", this.isTamperingFunctionComment());
        preferences.putBoolean("isTamperingRandomCase", this.isTamperingRandomCase());
        preferences.putBoolean("isTamperingEval", this.isTamperingEval());
        preferences.putBoolean("isTamperingSpaceToDashComment", this.isTamperingSpaceToDashComment());
        preferences.putBoolean("isTamperingSpaceToMultlineComment", this.isTamperingSpaceToMultlineComment());
        preferences.putBoolean("isTamperingSpaceToSharpComment", this.isTamperingSpaceToSharpComment());
        
        preferences.putBoolean("is4K", this.is4K());
        
        HttpURLConnection.setFollowRedirects(this.isFollowingRedirection());
        
    }

    // Getters and setters
    
    public String getPathFile() {
        return this.pathFile;
    }
    
    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }
    
    public boolean isCheckUpdateActivated() {
        return this.isCheckUpdateActivated;
    }

    public void setIsCheckingUpdate(boolean isCheckUpdateActivated) {
        this.isCheckUpdateActivated = isCheckUpdateActivated;
    }
    
    public boolean isFollowingRedirection() {
        return this.isFollowingRedirection;
    }

    public void setFollowingRedirection(boolean isFollowingRedirection) {
        this.isFollowingRedirection = isFollowingRedirection;
    }
    
    public boolean isReportingBugs() {
        return this.isReportingBugs;
    }

    public void setReportingBugs(boolean isReportingBugs) {
        this.isReportingBugs = isReportingBugs;
    }

    public boolean isNotInjectingMetadata() {
        return this.isNotInjectingMetadata;
    }

    public void setNotInjectingMetadata(boolean isNotInjectingMetadata) {
        this.isNotInjectingMetadata = isNotInjectingMetadata;
    }

    public boolean isCheckingAllURLParam() {
        return this.isCheckingAllURLParam;
    }

    public void setCheckingAllURLParam(boolean isCheckingAllURLParam) {
        this.isCheckingAllURLParam = isCheckingAllURLParam;
    }

    public boolean isCheckingAllRequestParam() {
        return this.isCheckingAllRequestParam;
    }

    public void setCheckingAllRequestParam(boolean isCheckingAllRequestParam) {
        this.isCheckingAllRequestParam = isCheckingAllRequestParam;
    }

    public boolean isCheckingAllHeaderParam() {
        return this.isCheckingAllHeaderParam;
    }

    public void setCheckingAllHeaderParam(boolean isCheckingAllHeaderParam) {
        this.isCheckingAllHeaderParam = isCheckingAllHeaderParam;
    }

    public boolean isCheckingAllJSONParam() {
        return this.isCheckingAllJSONParam;
    }

    public void setCheckingAllJSONParam(boolean isCheckingAllJSONParam) {
        this.isCheckingAllJSONParam = isCheckingAllJSONParam;
    }

    public boolean isParsingForm() {
        return this.isParsingForm;
    }

    public void setParsingForm(boolean isParsingForm) {
        this.isParsingForm = isParsingForm;
    }

    public boolean isNotTestingConnection() {
        return this.isNotTestingConnection;
    }

    public void setNotTestingConnection(boolean isNotTestingConnection) {
        this.isNotTestingConnection = isNotTestingConnection;
    }

    public boolean isProcessingCookies() {
        return this.isProcessingCookies;
    }

    public void setProcessingCookies(boolean isProcessingCookies) {
        this.isProcessingCookies = isProcessingCookies;
    }

    public boolean isCheckingAllParam() {
        return this.isCheckingAllParam;
    }

    public void setCheckingAllParam(boolean isCheckingAllParam) {
        this.isCheckingAllParam = isCheckingAllParam;
    }

    public boolean isProcessingCsrf() {
        return this.isProcessingCsrf;
    }

    public void setProcessingCsrf(boolean isProcessingCsrf) {
        this.isProcessingCsrf = isProcessingCsrf;
    }

    // TODO
    public boolean isCheckingAllCookieParam() {
        return this.isCheckingAllCookieParam;
    }

    public void setCheckingAllCookieParam(boolean isCheckingAllCookieParam) {
        this.isCheckingAllCookieParam = isCheckingAllCookieParam;
    }

    public boolean isTamperingBase64() {
        return this.isTamperingBase64;
    }

    public void setTamperingBase64(boolean isTamperingBase64) {
        this.isTamperingBase64 = isTamperingBase64;
    }

    public boolean isTamperingFunctionComment() {
        return this.isTamperingFunctionComment;
    }

    public void setTamperingFunctionComment(boolean isTamperingFunctionComment) {
        this.isTamperingFunctionComment = isTamperingFunctionComment;
    }

    public boolean isTamperingEqualToLike() {
        return this.isTamperingEqualToLike;
    }

    public void setTamperingEqualToLike(boolean isTamperingEqualToLike) {
        this.isTamperingEqualToLike = isTamperingEqualToLike;
    }

    public boolean isTamperingRandomCase() {
        return this.isTamperingRandomCase;
    }

    public void setTamperingRandomCase(boolean isTamperingRandomCase) {
        this.isTamperingRandomCase = isTamperingRandomCase;
    }

    public boolean isTamperingSpaceToMultlineComment() {
        return this.isTamperingSpaceToMultlineComment;
    }

    public void setTamperingSpaceToMultlineComment(boolean isTamperingSpaceToMultlineComment) {
        this.isTamperingSpaceToMultlineComment = isTamperingSpaceToMultlineComment;
    }

    public boolean isTamperingSpaceToDashComment() {
        return this.isTamperingSpaceToDashComment;
    }

    public void setTamperingSpaceToDashComment(boolean isTamperingSpaceToDashComment) {
        this.isTamperingSpaceToDashComment = isTamperingSpaceToDashComment;
    }

    public boolean isTamperingSpaceToSharpComment() {
        return this.isTamperingSpaceToSharpComment;
    }

    public void setTamperingSpaceToSharpComment(boolean isTamperingSpaceToSharpComment) {
        this.isTamperingSpaceToSharpComment = isTamperingSpaceToSharpComment;
    }

    public boolean isTamperingVersionComment() {
        return this.isTamperingVersionComment;
    }

    public void setTamperingVersionComment(boolean isTamperingVersionComment) {
        this.isTamperingVersionComment = isTamperingVersionComment;
    }

    public boolean isTamperingEval() {
        return this.isTamperingEval;
    }

    public void setTamperingEval(boolean isTamperingEval) {
        this.isTamperingEval = isTamperingEval;
    }

    public boolean isCheckingAllSOAPParam() {
        return this.isCheckingAllSOAPParam;
    }

    public void setCheckingAllSOAPParam(boolean isCheckingAllSOAPParam) {
        this.isCheckingAllSOAPParam = isCheckingAllSOAPParam;
    }

    public boolean is4K() {
        return this.is4K;
    }

    public void setIs4K(boolean is4k) {
        this.is4K = is4k;
    }
    
}

package com.jsql.util;

import java.net.HttpURLConnection;
import java.util.prefs.Preferences;

import javax.swing.Icon;

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
    private boolean isCheckingUpdate = true;

    /**
     * True if bugs are sent to Github.
     */
    private boolean isReportingBugs = true;
    
    private boolean is4K = true;
    
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

    private String blindTag = "";
    private boolean isBlindTag = false;
    private boolean isLimitingThreads = false;
    private int countLimitingThreads;

    /**
     * Initialize the utility class with previously saved JVM preferences and apply
     * loaded settings to the system.
     */
    public void loadSavedPreferences() {
        
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        this.setPathFile(prefs.get("pathFile", System.getProperty("user.dir")));
        
        this.setIsCheckingUpdate(prefs.getBoolean("isCheckingUpdate", true));
        this.setIsReportingBugs(prefs.getBoolean("isReportingBugs", true));
        
        this.setIsFollowingRedirection(prefs.getBoolean("isFollowingRedirection", false));
        this.setIsNotInjectingMetadata(prefs.getBoolean("isNotInjectingMetadata", false));
        
        this.setIsCheckingAllParam(prefs.getBoolean("isCheckingAllParam", false));
        this.setIsCheckingAllURLParam(prefs.getBoolean("isCheckingAllURLParam", false));
        this.setIsCheckingAllRequestParam(prefs.getBoolean("isCheckingAllRequestParam", false));
        this.setIsCheckingAllHeaderParam(prefs.getBoolean("isCheckingAllHeaderParam", false));
        this.setIsCheckingAllJSONParam(prefs.getBoolean("isCheckingAllJSONParam", false));
        this.setIsCheckingAllCookieParam(prefs.getBoolean("isCheckingAllCookieParam", false));
        this.setIsCheckingAllSoapParam(prefs.getBoolean("isCheckingAllSOAPParam", false));
        
        this.setIsParsingForm(prefs.getBoolean("isParsingForm", false));
        this.setIsNotTestingConnection(prefs.getBoolean("isNotTestingConnection", false));
        this.setIsProcessingCookies(prefs.getBoolean("isProcessingCookies", false));
        this.setIsProcessingCsrf(prefs.getBoolean("isProcessingCsrf", false));
        
        this.setIsTamperingBase64(prefs.getBoolean("isTamperingBase64", false));
        this.setIsTamperingEqualToLike(prefs.getBoolean("isTamperingEqualToLike", false));
        this.setIsTamperingFunctionComment(prefs.getBoolean("isTamperingFunctionComment", false));
        this.setIsTamperingVersionComment(prefs.getBoolean("isTamperingVersionComment", false));
        this.setIsTamperingRandomCase(prefs.getBoolean("isTamperingRandomCase", false));
        this.setIsTamperingEval(prefs.getBoolean("isTamperingEval", false));
        this.setIsTamperingSpaceToDashComment(prefs.getBoolean("isTamperingSpaceToDashComment", false));
        this.setIsTamperingSpaceToMultlineComment(prefs.getBoolean("isTamperingSpaceToMultlineComment", false));
        this.setIsTamperingSpaceToSharpComment(prefs.getBoolean("isTamperingSpaceToSharpComment", false));
        
        this.setIs4K(prefs.getBoolean("is4K", false));
        this.setIsBlindTag(prefs.getBoolean("isBlindTag", false));
        this.setBlindTag(prefs.get("blindTag", ""));
        this.setIsLimitingThreads(prefs.getBoolean("isLimitingThreads", false));
        this.setCountLimitingThreads(prefs.getInt("countLimitingThreads", 10));
        
        HttpURLConnection.setFollowRedirects(this.isFollowingRedirection);
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
    
    // TODO builder
    /**
     * Initialize the utility class, persist preferences and
     * apply change to the JVM.
     * @param isCheckingUpdate true if it checks to updates
     * @param isReportingBugs true if it reports issues
     * @param isFollowingRedirection true if redirection are followed to new URL destination
     * @param string 
     * @param b 
     * @param object 
     */
    public void set(
        boolean isCheckingUpdate,
        boolean isReportingBugs,
        boolean is4K,
        
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
        boolean isLimitingThreads, 
        int countLimitingThreads, 
        boolean isBlindTag, 
        String blindTag
    ) {
        
        this.setIsCheckingUpdate(isCheckingUpdate);
        this.setIsReportingBugs(isReportingBugs);
        this.setIs4K(is4K);
        this.setIsLimitingThreads(isLimitingThreads);
        this.setCountLimitingThreads(countLimitingThreads);
        this.setIsBlindTag(isBlindTag);
        this.setBlindTag(blindTag);
        
        this.setIsFollowingRedirection(isFollowingRedirection);
        this.setIsNotInjectingMetadata(isNotInjectingMetadata);
        this.setIsCheckingAllParam(isCheckingAllParam);
        this.setIsCheckingAllURLParam(isCheckingAllURLParam);
        this.setIsCheckingAllRequestParam(isCheckingAllRequestParam);
        this.setIsCheckingAllHeaderParam(isCheckingAllHeaderParam);
        this.setIsCheckingAllJSONParam(isCheckingAllJSONParam);
        this.setIsCheckingAllCookieParam(isCheckingAllCookieParam);
        this.setIsCheckingAllSoapParam(isCheckingAllSOAPParam);
        this.setIsParsingForm(isParsingForm);
        this.setIsNotTestingConnection(isNotTestingConnection);
        this.setIsProcessingCookies(isProcessingCookies);
        this.setIsProcessingCsrf(isProcessingCsrf);
        
        this.setIsTamperingBase64(isTamperingBase64);
        this.setIsTamperingEqualToLike(isTamperingEqualToLike);
        this.setIsTamperingFunctionComment(isTamperingFunctionComment);
        this.setIsTamperingVersionComment(isTamperingVersionComment);
        this.setIsTamperingRandomCase(isTamperingRandomCase);
        this.setIsTamperingEval(isTamperingEval);
        this.setIsTamperingSpaceToDashComment(isTamperingSpaceToDashComment);
        this.setIsTamperingSpaceToMultlineComment(isTamperingSpaceToMultlineComment);
        this.setIsTamperingSpaceToSharpComment(isTamperingSpaceToSharpComment);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", this.isCheckingUpdate);
        preferences.putBoolean("isReportingBugs", this.isReportingBugs);
        preferences.putBoolean("is4K", this.is4K);
        preferences.putBoolean("isLimitingThreads", this.isLimitingThreads);
        preferences.putInt("countLimitingThreads", this.countLimitingThreads);
        preferences.putBoolean("isBlindTag", this.isBlindTag);
        preferences.put("blindTag", this.blindTag);
        
        preferences.putBoolean("isFollowingRedirection", this.isFollowingRedirection);
        preferences.putBoolean("isNotInjectingMetadata", this.isNotInjectingMetadata);
        preferences.putBoolean("isCheckingAllParam", this.isCheckingAllParam);
        preferences.putBoolean("isCheckingAllURLParam", this.isCheckingAllURLParam);
        preferences.putBoolean("isCheckingAllRequestParam", this.isCheckingAllRequestParam);
        preferences.putBoolean("isCheckingAllHeaderParam", this.isCheckingAllHeaderParam);
        preferences.putBoolean("isCheckingAllJSONParam", this.isCheckingAllJSONParam);
        preferences.putBoolean("isCheckingAllCookieParam", this.isCheckingAllCookieParam);
        preferences.putBoolean("isCheckingAllSOAPParam", this.isCheckingAllSOAPParam);
        preferences.putBoolean("isParsingForm", this.isParsingForm);
        preferences.putBoolean("isNotTestingConnection", this.isNotTestingConnection);
        preferences.putBoolean("isProcessingCookies", this.isProcessingCookies);
        preferences.putBoolean("isProcessingCsrf", this.isProcessingCsrf);
        
        preferences.putBoolean("isTamperingBase64", this.isTamperingBase64);
        preferences.putBoolean("isTamperingEqualToLike", this.isTamperingEqualToLike);
        preferences.putBoolean("isTamperingVersionComment", this.isTamperingVersionComment);
        preferences.putBoolean("isTamperingFunctionComment", this.isTamperingFunctionComment);
        preferences.putBoolean("isTamperingRandomCase", this.isTamperingRandomCase);
        preferences.putBoolean("isTamperingEval", this.isTamperingEval);
        preferences.putBoolean("isTamperingSpaceToDashComment", this.isTamperingSpaceToDashComment);
        preferences.putBoolean("isTamperingSpaceToMultlineComment", this.isTamperingSpaceToMultlineComment);
        preferences.putBoolean("isTamperingSpaceToSharpComment", this.isTamperingSpaceToSharpComment);
        
        HttpURLConnection.setFollowRedirects(this.isFollowingRedirection);
    }
    
    
    // Builder

    public PreferencesUtil withNotTestingConnection() {
        this.isNotTestingConnection = true;
        return this;
    }
    
    public PreferencesUtil withCheckingAllHeaderParam() {
        this.isCheckingAllHeaderParam = true;
        return this;
    }
    
    public PreferencesUtil withProcessingCookies() {
        this.isProcessingCookies = true;
        return this;
    }
    
    public PreferencesUtil withProcessingCsrf() {
        this.isProcessingCsrf = true;
        return this;
    }
    
    public PreferencesUtil withCheckingAllURLParam() {
        this.isCheckingAllURLParam = true;
        return this;
    }
    
    public PreferencesUtil withCheckingAllJSONParam() {
        this.isCheckingAllJSONParam = true;
        return this;
    }
    
    
    // Getters and setters
    
    public String getPathFile() {
        return this.pathFile;
    }
    
    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }
    
    public boolean isCheckingUpdate() {
        return this.isCheckingUpdate;
    }

    public void setIsCheckingUpdate(boolean isCheckingUpdate) {
        this.isCheckingUpdate = isCheckingUpdate;
    }
    
    public boolean isFollowingRedirection() {
        return this.isFollowingRedirection;
    }

    public void setIsFollowingRedirection(boolean isFollowingRedirection) {
        this.isFollowingRedirection = isFollowingRedirection;
    }
    
    public boolean isReportingBugs() {
        return this.isReportingBugs;
    }

    public void setIsReportingBugs(boolean isReportingBugs) {
        this.isReportingBugs = isReportingBugs;
    }

    public boolean isNotInjectingMetadata() {
        return this.isNotInjectingMetadata;
    }

    public void setIsNotInjectingMetadata(boolean isNotInjectingMetadata) {
        this.isNotInjectingMetadata = isNotInjectingMetadata;
    }

    public boolean isCheckingAllURLParam() {
        return this.isCheckingAllURLParam;
    }

    public void setIsCheckingAllURLParam(boolean isCheckingAllURLParam) {
        this.isCheckingAllURLParam = isCheckingAllURLParam;
    }

    public boolean isCheckingAllRequestParam() {
        return this.isCheckingAllRequestParam;
    }

    public void setIsCheckingAllRequestParam(boolean isCheckingAllRequestParam) {
        this.isCheckingAllRequestParam = isCheckingAllRequestParam;
    }

    public boolean isCheckingAllHeaderParam() {
        return this.isCheckingAllHeaderParam;
    }

    public void setIsCheckingAllHeaderParam(boolean isCheckingAllHeaderParam) {
        this.isCheckingAllHeaderParam = isCheckingAllHeaderParam;
    }

    public boolean isCheckingAllJsonParam() {
        return this.isCheckingAllJSONParam;
    }

    public void setIsCheckingAllJSONParam(boolean isCheckingAllJSONParam) {
        this.isCheckingAllJSONParam = isCheckingAllJSONParam;
    }

    public boolean isParsingForm() {
        return this.isParsingForm;
    }

    public void setIsParsingForm(boolean isParsingForm) {
        this.isParsingForm = isParsingForm;
    }

    public boolean isNotTestingConnection() {
        return this.isNotTestingConnection;
    }

    public void setIsNotTestingConnection(boolean isNotTestingConnection) {
        this.isNotTestingConnection = isNotTestingConnection;
    }

    public boolean isProcessingCookies() {
        return this.isProcessingCookies;
    }

    public void setIsProcessingCookies(boolean isProcessingCookies) {
        this.isProcessingCookies = isProcessingCookies;
    }

    public boolean isCheckingAllParam() {
        return this.isCheckingAllParam;
    }

    public void setIsCheckingAllParam(boolean isCheckingAllParam) {
        this.isCheckingAllParam = isCheckingAllParam;
    }

    public boolean isProcessingCsrf() {
        return this.isProcessingCsrf;
    }

    public void setIsProcessingCsrf(boolean isProcessingCsrf) {
        this.isProcessingCsrf = isProcessingCsrf;
    }

    public boolean isCheckingAllCookieParam() {
        return this.isCheckingAllCookieParam;
    }

    public void setIsCheckingAllCookieParam(boolean isCheckingAllCookieParam) {
        this.isCheckingAllCookieParam = isCheckingAllCookieParam;
    }

    public boolean isTamperingBase64() {
        return this.isTamperingBase64;
    }

    public void setIsTamperingBase64(boolean isTamperingBase64) {
        this.isTamperingBase64 = isTamperingBase64;
    }

    public boolean isTamperingFunctionComment() {
        return this.isTamperingFunctionComment;
    }

    public void setIsTamperingFunctionComment(boolean isTamperingFunctionComment) {
        this.isTamperingFunctionComment = isTamperingFunctionComment;
    }

    public boolean isTamperingEqualToLike() {
        return this.isTamperingEqualToLike;
    }

    public void setIsTamperingEqualToLike(boolean isTamperingEqualToLike) {
        this.isTamperingEqualToLike = isTamperingEqualToLike;
    }

    public boolean isTamperingRandomCase() {
        return this.isTamperingRandomCase;
    }

    public void setIsTamperingRandomCase(boolean isTamperingRandomCase) {
        this.isTamperingRandomCase = isTamperingRandomCase;
    }

    public boolean isTamperingSpaceToMultlineComment() {
        return this.isTamperingSpaceToMultlineComment;
    }

    public void setIsTamperingSpaceToMultlineComment(boolean isTamperingSpaceToMultlineComment) {
        this.isTamperingSpaceToMultlineComment = isTamperingSpaceToMultlineComment;
    }

    public boolean isTamperingSpaceToDashComment() {
        return this.isTamperingSpaceToDashComment;
    }

    public void setIsTamperingSpaceToDashComment(boolean isTamperingSpaceToDashComment) {
        this.isTamperingSpaceToDashComment = isTamperingSpaceToDashComment;
    }

    public boolean isTamperingSpaceToSharpComment() {
        return this.isTamperingSpaceToSharpComment;
    }

    public void setIsTamperingSpaceToSharpComment(boolean isTamperingSpaceToSharpComment) {
        this.isTamperingSpaceToSharpComment = isTamperingSpaceToSharpComment;
    }

    public boolean isTamperingVersionComment() {
        return this.isTamperingVersionComment;
    }

    public void setIsTamperingVersionComment(boolean isTamperingVersionComment) {
        this.isTamperingVersionComment = isTamperingVersionComment;
    }

    public boolean isTamperingEval() {
        return this.isTamperingEval;
    }

    public void setIsTamperingEval(boolean isTamperingEval) {
        this.isTamperingEval = isTamperingEval;
    }

    public boolean isCheckingAllSOAPParam() {
        return this.isCheckingAllSOAPParam;
    }

    public void setIsCheckingAllSoapParam(boolean isCheckingAllSOAPParam) {
        this.isCheckingAllSOAPParam = isCheckingAllSOAPParam;
    }

    public boolean is4K() {
        return this.is4K;
    }

    public void setIs4K(boolean is4k) {
        this.is4K = is4k;
    }

    public boolean isLimitingThreads() {
        return isLimitingThreads ;
    }
    
    public void setIsLimitingThreads(boolean isLimitingThreads) {
        this.isLimitingThreads = isLimitingThreads;
    }
    
    public int countLimitingThreads() {
        return countLimitingThreads ;
    }
    
    public void setCountLimitingThreads(int countLimitingThreads) {
        this.countLimitingThreads = countLimitingThreads;
    }
    
    public boolean isBlindTag() {
        return isBlindTag;
    }
    
    public void setIsBlindTag(boolean isBlindTag) {
        this.isBlindTag = isBlindTag;
    }
    
    public String blindTag() {
        return blindTag;
    }
    
    public void setBlindTag(String blindTag) {
        this.blindTag = blindTag;
    }
}

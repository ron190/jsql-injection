package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.util.reverse.ModelReverse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Utility class to manage JVM preferences previously saved into the system.
 * Only general settings are processed by this utility, other specific preferences
 * like those for proxy are defined from specific utility classes.
 */
public class PreferencesUtil {

    public static final String EW_SPLIT = "verticalSplitter";
    public static final String NS_SPLIT = "horizontalSplitter";
    public static final String CHUNK_VISIBLE = "chunk_visible";
    public static final String BINARY_VISIBLE = "binary_visible";
    public static final String NETWORK_VISIBLE = "header_visible";
    public static final String JAVA_VISIBLE = "java_visible";
    public static final String IS_MAXIMIZED = "is_maximized";

    // File path saved in preference.
    private String pathFile;

    private boolean isCheckingUpdate = true;

    // True if bugs are sent to GitHub.
    private boolean isReportingBugs = true;
    
    private boolean is4K = false;
    
    private boolean isFollowingRedirection = false;
    private boolean isHttp2Disabled = false;
    
    private boolean isNotInjectingMetadata = false;
    private boolean isNotSearchingCharInsertion = false;
    private boolean isNotShowingVulnReport = false;

    private boolean isCheckingAllParam = false;
    private boolean isCheckingAllURLParam = false;
    private boolean isCheckingAllRequestParam = false;
    private boolean isCheckingAllHeaderParam = false;
    private boolean isCheckingAllBase64Param = false;
    private boolean isCheckingAllJsonParam = false;
    private boolean isCheckingAllCookieParam = false;
    private boolean isCheckingAllSoapParam = false;
    
    private boolean isPerfIndexDisabled = false;
    private boolean isDefaultStrategy = false;
    private boolean isZipStrategy = false;
    private boolean isDiosStrategy = false;
    private boolean isUrlEncodingDisabled = false;
    private boolean isUrlRandomSuffixDisabled = false;

    private boolean isParsingForm = false;
    private boolean isNotTestingConnection = false;
    private boolean isNotProcessingCookies = false;
    private boolean isProcessingCsrf = false;
    
    private boolean isTamperingBase64 = false;
    private boolean isTamperingFunctionComment = false;
    private boolean isTamperingVersionComment = false;
    private boolean isTamperingEqualToLike = false;
    private boolean isTamperingRandomCase = false;
    private boolean isTamperingEval = false;
    private boolean isTamperingSpaceToMultilineComment = false;
    private boolean isTamperingSpaceToDashComment = false;
    private boolean isTamperingSpaceToSharpComment = false;

    private String csrfUserTag = StringUtils.EMPTY;
    private String csrfUserTagOutput = StringUtils.EMPTY;
    private boolean isCsrfUserTag = false;
    private boolean isLimitingThreads = true;
    private int countLimitingThreads = 5;
    private boolean isConnectionTimeout = false;
    private int countConnectionTimeout = 15;
    private boolean isUnicodeDecodeDisabled = false;
    private boolean isUrlDecodeDisabled = false;

    private boolean isStrategyTimeDisabled = false;
    private boolean isStrategyBlindBitDisabled = false;
    private boolean isStrategyBlindBinDisabled = false;
    private boolean isStrategyMultibitDisabled = false;
    private boolean isStrategyStackDisabled = false;
    private boolean isStrategyErrorDisabled = false;
    private boolean isStrategyUnionDisabled = false;

    private boolean isLimitingUnionIndex = false;
    private int countUnionIndex = 50;
    private boolean isLimitingSleepTimeStrategy = false;
    private int countSleepTimeStrategy = 5;

    private String themeFlatLafName = StringUtils.EMPTY;
    private String languageTag = StringUtils.EMPTY;
    private boolean isUserAgentRandom = false;
    private boolean isUrlDecodeNetworkTab = false;

    private final Yaml yaml;
    private String commandsReverseYaml;
    private List<ModelReverse> commandsReverse;

    public PreferencesUtil() {
        var loaderOptions = new LoaderOptions();
        loaderOptions.setWarnOnDuplicateKeys(false);  // required to prevent snakeyaml logs
        this.yaml = new Yaml(loaderOptions);
        try {
            this.parseReverseCommands(StringUtil.fromBase64Zip(StringUtil.getFile("exploit/reverse.yml").trim()));
        } catch (IOException e) {
            throw new JSqlRuntimeException(e);
        }
    }

    public void parseReverseCommands(String commandsReverseYaml) {
        List<Map<String, String>> commandsReverseMap = this.yaml.load(commandsReverseYaml);
        this.commandsReverse = commandsReverseMap.stream()
            .map(map -> new ModelReverse(
                map.get("name"),
                map.get("command").replaceAll("\\n\\s*", StringUtils.EMPTY)
            ))
            .collect(Collectors.toList());
        this.commandsReverseYaml = commandsReverseYaml;
    }

    /**
     * Initialize the utility class with previously saved JVM preferences and apply
     * loaded settings to the system.
     */
    public void loadSavedPreferences() {
        
        // Use Preferences API to persist proxy configuration
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        
        this.pathFile = preferences.get("pathFile", SystemUtils.USER_DIR);
        
        this.isCheckingUpdate = preferences.getBoolean("isCheckingUpdate", true);
        this.isReportingBugs = preferences.getBoolean("isReportingBugs", true);
        
        this.isFollowingRedirection = preferences.getBoolean("isFollowingRedirection", false);
        this.isHttp2Disabled = preferences.getBoolean("isHttp2Disabled", false);
        this.isNotInjectingMetadata = preferences.getBoolean("isNotInjectingMetadata", false);
        this.isNotSearchingCharInsertion = preferences.getBoolean("isNotSearchingCharInsertion", false);
        this.isNotShowingVulnReport = preferences.getBoolean("isNotShowingVulnReport", false);

        this.isCheckingAllParam = preferences.getBoolean("isCheckingAllParam", false);
        this.isCheckingAllURLParam = preferences.getBoolean("isCheckingAllURLParam", false);
        this.isCheckingAllRequestParam = preferences.getBoolean("isCheckingAllRequestParam", false);
        this.isCheckingAllHeaderParam = preferences.getBoolean("isCheckingAllHeaderParam", false);
        this.isCheckingAllBase64Param = preferences.getBoolean("isCheckingAllBase64Param", false);
        this.isCheckingAllJsonParam = preferences.getBoolean("isCheckingAllJsonParam", false);
        this.isCheckingAllCookieParam = preferences.getBoolean("isCheckingAllCookieParam", false);
        this.isCheckingAllSoapParam = preferences.getBoolean("isCheckingAllSoapParam", false);
        
        this.isPerfIndexDisabled = preferences.getBoolean("isPerfIndexDisabled", false);
        this.isDefaultStrategy = preferences.getBoolean("isDefaultStrategy", false);
        this.isZipStrategy = preferences.getBoolean("isZipStrategy", false);
        this.isDiosStrategy = preferences.getBoolean("isDiosStrategy", false);
        this.isUrlEncodingDisabled = preferences.getBoolean("isUrlEncodingDisabled", false);
        this.isUrlRandomSuffixDisabled = preferences.getBoolean("isUrlRandomSuffixDisabled", false);

        this.isParsingForm = preferences.getBoolean("isParsingForm", false);
        this.isNotTestingConnection = preferences.getBoolean("isNotTestingConnection", false);
        this.isNotProcessingCookies = preferences.getBoolean("isNotProcessingCookies", false);
        this.isProcessingCsrf = preferences.getBoolean("isProcessingCsrf", false);
        
        this.isTamperingBase64 = preferences.getBoolean("isTamperingBase64", false);
        this.isTamperingEqualToLike = preferences.getBoolean("isTamperingEqualToLike", false);
        this.isTamperingFunctionComment = preferences.getBoolean("isTamperingFunctionComment", false);
        this.isTamperingVersionComment = preferences.getBoolean("isTamperingVersionComment", false);
        this.isTamperingRandomCase = preferences.getBoolean("isTamperingRandomCase", false);
        this.isTamperingEval = preferences.getBoolean("isTamperingEval", false);
        this.isTamperingSpaceToDashComment = preferences.getBoolean("isTamperingSpaceToDashComment", false);
        this.isTamperingSpaceToMultilineComment = preferences.getBoolean("isTamperingSpaceToMultilineComment", false);
        this.isTamperingSpaceToSharpComment = preferences.getBoolean("isTamperingSpaceToSharpComment", false);
        
        this.is4K = preferences.getBoolean("is4K", false);
        this.isCsrfUserTag = preferences.getBoolean("isCsrfUserTag", false);
        this.csrfUserTag = preferences.get("csrfUserTag", StringUtils.EMPTY);
        this.csrfUserTagOutput = preferences.get("csrfUserTagOutput", StringUtils.EMPTY);
        this.isLimitingThreads = preferences.getBoolean("isLimitingThreads", true);
        this.countLimitingThreads = preferences.getInt("countLimitingThreads", 5);
        this.isConnectionTimeout = preferences.getBoolean("isConnectionTimeout", false);
        this.countConnectionTimeout = preferences.getInt("countConnectionTimeout", 15);
        this.isUnicodeDecodeDisabled = preferences.getBoolean("isUnicodeDecodeDisabled", false);
        this.isUrlDecodeDisabled = preferences.getBoolean("isUrlDecodeDisabled", false);
        this.countUnionIndex = preferences.getInt("countUnionIndex", 50);
        this.isLimitingUnionIndex = preferences.getBoolean("isLimitingUnionIndex", false);
        this.countSleepTimeStrategy = preferences.getInt("countSleepTimeStrategy", 5);
        this.isLimitingSleepTimeStrategy = preferences.getBoolean("isLimitingSleepTimeStrategy", false);

        this.isStrategyTimeDisabled = preferences.getBoolean("isStrategyTimeDisabled", false);
        this.isStrategyBlindBitDisabled = preferences.getBoolean("isStrategyBlindBitDisabled", false);
        this.isStrategyBlindBinDisabled = preferences.getBoolean("isStrategyBlindBinDisabled", false);
        this.isStrategyMultibitDisabled = preferences.getBoolean("isStrategyMultibitDisabled", false);
        this.isStrategyStackDisabled = preferences.getBoolean("isStrategyStackDisabled", false);
        this.isStrategyErrorDisabled = preferences.getBoolean("isStrategyErrorDisabled", false);
        this.isStrategyUnionDisabled = preferences.getBoolean("isStrategyUnionDisabled", false);

        this.isUserAgentRandom = preferences.getBoolean("isUserAgentRandom", false);

        this.themeFlatLafName = preferences.get("themeFlatLafName", StringUtils.EMPTY);
        this.languageTag = preferences.get("languageTag", StringUtils.EMPTY);
        this.isUrlDecodeNetworkTab = preferences.getBoolean("isUrlDecodeNetworkTab", false);
    }
    
    /**
     * Initialize the utility class, persist preferences and
     * apply change to the JVM.
     */
    public void persist() {
        
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        preferences.putBoolean("isCheckingUpdate", this.isCheckingUpdate);
        preferences.putBoolean("isReportingBugs", this.isReportingBugs);
        preferences.putBoolean("is4K", this.is4K);
        preferences.putBoolean("isUnicodeDecodeDisabled", this.isUnicodeDecodeDisabled);
        preferences.putBoolean("isUrlDecodeDisabled", this.isUrlDecodeDisabled);
        preferences.putBoolean("isLimitingThreads", this.isLimitingThreads);
        preferences.putInt("countLimitingThreads", this.countLimitingThreads);
        preferences.putBoolean("isConnectionTimeout", this.isConnectionTimeout);
        preferences.putInt("countConnectionTimeout", this.countConnectionTimeout);
        preferences.putBoolean("isLimitingUnionIndex", this.isLimitingUnionIndex);
        preferences.putInt("countUnionIndex", this.countUnionIndex);
        preferences.putBoolean("isLimitingSleepTimeStrategy", this.isLimitingSleepTimeStrategy);
        preferences.putInt("countSleepTimeStrategy", this.countSleepTimeStrategy);
        preferences.putBoolean("isCsrfUserTag", this.isCsrfUserTag);
        preferences.put("csrfUserTag", this.csrfUserTag);
        preferences.put("csrfUserTagOutput", this.csrfUserTagOutput);
        
        preferences.putBoolean("isFollowingRedirection", this.isFollowingRedirection);
        preferences.putBoolean("isHttp2Disabled", this.isHttp2Disabled);
        preferences.putBoolean("isNotInjectingMetadata", this.isNotInjectingMetadata);
        preferences.putBoolean("isNotSearchingCharInsertion", this.isNotSearchingCharInsertion);
        preferences.putBoolean("isNotShowingVulnReport", this.isNotShowingVulnReport);
        preferences.putBoolean("isCheckingAllParam", this.isCheckingAllParam);
        preferences.putBoolean("isCheckingAllURLParam", this.isCheckingAllURLParam);
        preferences.putBoolean("isCheckingAllRequestParam", this.isCheckingAllRequestParam);
        preferences.putBoolean("isCheckingAllHeaderParam", this.isCheckingAllHeaderParam);
        
        preferences.putBoolean("isCheckingAllBase64Param", this.isCheckingAllBase64Param);
        preferences.putBoolean("isCheckingAllJsonParam", this.isCheckingAllJsonParam);
        preferences.putBoolean("isCheckingAllCookieParam", this.isCheckingAllCookieParam);
        preferences.putBoolean("isCheckingAllSoapParam", this.isCheckingAllSoapParam);
        preferences.putBoolean("isParsingForm", this.isParsingForm);
        preferences.putBoolean("isNotTestingConnection", this.isNotTestingConnection);
        preferences.putBoolean("isNotProcessingCookies", this.isNotProcessingCookies);
        preferences.putBoolean("isProcessingCsrf", this.isProcessingCsrf);
        
        preferences.putBoolean("isPerfIndexDisabled", this.isPerfIndexDisabled);
        preferences.putBoolean("isDefaultStrategy", this.isDefaultStrategy);
        preferences.putBoolean("isZipStrategy", this.isZipStrategy);
        preferences.putBoolean("isDiosStrategy", this.isDiosStrategy);
        preferences.putBoolean("isUrlEncodingDisabled", this.isUrlEncodingDisabled);
        preferences.putBoolean("isUrlRandomSuffixDisabled", this.isUrlRandomSuffixDisabled);

        preferences.putBoolean("isTamperingBase64", this.isTamperingBase64);
        preferences.putBoolean("isTamperingEqualToLike", this.isTamperingEqualToLike);
        preferences.putBoolean("isTamperingVersionComment", this.isTamperingVersionComment);
        preferences.putBoolean("isTamperingFunctionComment", this.isTamperingFunctionComment);
        preferences.putBoolean("isTamperingRandomCase", this.isTamperingRandomCase);
        preferences.putBoolean("isTamperingEval", this.isTamperingEval);
        preferences.putBoolean("isTamperingSpaceToDashComment", this.isTamperingSpaceToDashComment);
        preferences.putBoolean("isTamperingSpaceToMultilineComment", this.isTamperingSpaceToMultilineComment);
        preferences.putBoolean("isTamperingSpaceToSharpComment", this.isTamperingSpaceToSharpComment);
        
        preferences.putBoolean("isStrategyTimeDisabled", this.isStrategyTimeDisabled);
        preferences.putBoolean("isStrategyBlindBitDisabled", this.isStrategyBlindBitDisabled);
        preferences.putBoolean("isStrategyBlindBinDisabled", this.isStrategyBlindBinDisabled);
        preferences.putBoolean("isStrategyMultibitDisabled", this.isStrategyMultibitDisabled);
        preferences.putBoolean("isStrategyStackDisabled", this.isStrategyStackDisabled);
        preferences.putBoolean("isStrategyErrorDisabled", this.isStrategyErrorDisabled);
        preferences.putBoolean("isStrategyUnionDisabled", this.isStrategyUnionDisabled);

        preferences.putBoolean("isUserAgentRandom", this.isUserAgentRandom);
        preferences.putBoolean("isUrlDecodeNetworkTab", this.isUrlDecodeNetworkTab);

        preferences.put("themeFlatLafName", this.themeFlatLafName);
        preferences.put("languageTag", this.languageTag);
    }
    
    /**
     * Set the general file path to the utility class and persist to JVM preferences.
     * @param path folder path to persist
     */
    public void set(String path) {
        this.pathFile = path;
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        preferences.put("pathFile", this.pathFile);
    }
    
    
    // Getters and setters
    
    public String getPathFile() {
        return this.pathFile;
    }
    
    public boolean isCheckingUpdate() {
        return this.isCheckingUpdate;
    }
    
    public boolean isFollowingRedirection() {
        return this.isFollowingRedirection;
    }
    
    public boolean isHttp2Disabled() {
        return this.isHttp2Disabled;
    }
    
    public boolean isReportingBugs() {
        return this.isReportingBugs;
    }

    public boolean isNotInjectingMetadata() {
        return this.isNotInjectingMetadata;
    }

    public boolean isNotSearchingCharInsertion() {
        return this.isNotSearchingCharInsertion;
    }

    public boolean isNotShowingVulnReport() {
        return this.isNotShowingVulnReport;
    }

    public boolean isCheckingAllURLParam() {
        return this.isCheckingAllURLParam;
    }

    public boolean isCheckingAllRequestParam() {
        return this.isCheckingAllRequestParam;
    }

    public boolean isCheckingAllHeaderParam() {
        return this.isCheckingAllHeaderParam;
    }

    public boolean isCheckingAllBase64Param() {
        return this.isCheckingAllBase64Param;
    }
    
    public boolean isCheckingAllJsonParam() {
        return this.isCheckingAllJsonParam;
    }

    public boolean isParsingForm() {
        return this.isParsingForm;
    }

    public boolean isNotTestingConnection() {
        return this.isNotTestingConnection;
    }

    public boolean isNotProcessingCookies() {
        return this.isNotProcessingCookies;
    }

    public boolean isCheckingAllParam() {
        return this.isCheckingAllParam;
    }

    public boolean isProcessingCsrf() {
        return this.isProcessingCsrf;
    }

    public boolean isCheckingAllCookieParam() {
        return this.isCheckingAllCookieParam;
    }

    public boolean isTamperingBase64() {
        return this.isTamperingBase64;
    }

    public boolean isTamperingFunctionComment() {
        return this.isTamperingFunctionComment;
    }

    public boolean isTamperingEqualToLike() {
        return this.isTamperingEqualToLike;
    }

    public boolean isTamperingRandomCase() {
        return this.isTamperingRandomCase;
    }

    public boolean isTamperingSpaceToMultilineComment() {
        return this.isTamperingSpaceToMultilineComment;
    }

    public boolean isTamperingSpaceToDashComment() {
        return this.isTamperingSpaceToDashComment;
    }

    public boolean isTamperingSpaceToSharpComment() {
        return this.isTamperingSpaceToSharpComment;
    }

    public boolean isTamperingVersionComment() {
        return this.isTamperingVersionComment;
    }

    public boolean isTamperingEval() {
        return this.isTamperingEval;
    }

    public boolean isCheckingAllSoapParam() {
        return this.isCheckingAllSoapParam;
    }

    public boolean is4K() {
        return this.is4K;
    }

    public boolean isLimitingThreads() {
        return this.isLimitingThreads;
    }
    
    public boolean isLimitingSleepTimeStrategy() {
        return this.isLimitingSleepTimeStrategy;
    }
    
    public boolean isConnectionTimeout() {
        return this.isConnectionTimeout;
    }
    
    public boolean isUnicodeDecodeDisabled() {
        return this.isUnicodeDecodeDisabled;
    }
    
    public boolean isUrlDecodeDisabled() {
        return this.isUrlDecodeDisabled;
    }
    
    public int countLimitingThreads() {
        return this.countLimitingThreads;
    }
    
    public int countConnectionTimeout() {
        return this.countConnectionTimeout;
    }
    
    public int countUnionIndex() {
        return this.countUnionIndex;
    }
    
    public int countSleepTimeStrategy() {
        return this.countSleepTimeStrategy;
    }
    
    public boolean isLimitingUnionIndex() {
        return this.isLimitingUnionIndex;
    }
    
    public boolean isCsrfUserTag() {
        return this.isCsrfUserTag;
    }
    
    public String csrfUserTag() {
        return this.csrfUserTag;
    }
    
    public String csrfUserTagOutput() {
        return this.csrfUserTagOutput;
    }
    
    public boolean isPerfIndexDisabled() {
        return this.isPerfIndexDisabled;
    }
    
    public boolean isZipStrategy() {
        return this.isZipStrategy;
    }
    
    public boolean isDefaultStrategy() {
        return this.isDefaultStrategy;
    }
    
    public boolean isDiosStrategy() {
        return this.isDiosStrategy;
    }
    
    public boolean isUrlEncodingDisabled() {
        return this.isUrlEncodingDisabled;
    }
    
    public boolean isUrlRandomSuffixDisabled() {
        return this.isUrlRandomSuffixDisabled;
    }

    public boolean isStrategyTimeDisabled() {
        return this.isStrategyTimeDisabled;
    }

    public boolean isStrategyBlindBitDisabled() {
        return this.isStrategyBlindBitDisabled;
    }

    public boolean isStrategyBlindBinDisabled() {
        return this.isStrategyBlindBinDisabled;
    }

    public boolean isStrategyMultibitDisabled() {
        return this.isStrategyMultibitDisabled;
    }

    public boolean isStrategyStackDisabled() {
        return this.isStrategyStackDisabled;
    }

    public boolean isStrategyErrorDisabled() {
        return this.isStrategyErrorDisabled;
    }

    public boolean isStrategyUnionDisabled() {
        return this.isStrategyUnionDisabled;
    }

    public boolean isUserAgentRandom() {
        return this.isUserAgentRandom;
    }

    public String getThemeFlatLafName() {
        return this.themeFlatLafName;
    }

    public String getLanguageTag() {
        return this.languageTag;
    }

    public boolean isUrlDecodeNetworkTab() {
        return this.isUrlDecodeNetworkTab;
    }

    public String getCommandsReverseYaml() {
        return this.commandsReverseYaml;
    }

    public List<ModelReverse> getCommandsReverse() {
        return this.commandsReverse;
    }

    // Builder

    public PreferencesUtil withIsCheckingUpdate(boolean isCheckingUpdate) {
        this.isCheckingUpdate = isCheckingUpdate;
        return this;
    }

    public PreferencesUtil withIsReportingBugs(boolean isReportingBugs) {
        this.isReportingBugs = isReportingBugs;
        return this;
    }

    public PreferencesUtil withIs4K(boolean is4K) {
        this.is4K = is4K;
        return this;
    }

    public PreferencesUtil withIsFollowingRedirection(boolean isFollowingRedirection) {
        this.isFollowingRedirection = isFollowingRedirection;
        return this;
    }
    
    public PreferencesUtil withIsHttp2Disabled(boolean isHttp2Disabled) {
        this.isHttp2Disabled = isHttp2Disabled;
        return this;
    }
    
    public PreferencesUtil withIsUnicodeDecodeDisabled(boolean isUnicodeDecodeDisabled) {
        this.isUnicodeDecodeDisabled = isUnicodeDecodeDisabled;
        return this;
    }
    
    public PreferencesUtil withIsUrlDecodeDisabled(boolean isUrlDecodeDisabled) {
        this.isUrlDecodeDisabled = isUrlDecodeDisabled;
        return this;
    }

    public PreferencesUtil withIsNotInjectingMetadata(boolean isNotInjectingMetadata) {
        this.isNotInjectingMetadata = isNotInjectingMetadata;
        return this;
    }

    public PreferencesUtil withIsNotSearchingCharInsertion(boolean isNotSearchingCharInsertion) {
        this.isNotSearchingCharInsertion = isNotSearchingCharInsertion;
        return this;
    }

    public PreferencesUtil withIsNotShowingVulnReport(boolean isNotShowingVulnReport) {
        this.isNotShowingVulnReport = isNotShowingVulnReport;
        return this;
    }

    public PreferencesUtil withIsCheckingAllParam(boolean isCheckingAllParam) {
        this.isCheckingAllParam = isCheckingAllParam;
        return this;
    }

    public PreferencesUtil withIsCheckingAllURLParam(boolean isCheckingAllURLParam) {
        this.isCheckingAllURLParam = isCheckingAllURLParam;
        return this;
    }

    public PreferencesUtil withIsCheckingAllRequestParam(boolean isCheckingAllRequestParam) {
        this.isCheckingAllRequestParam = isCheckingAllRequestParam;
        return this;
    }

    public PreferencesUtil withIsCheckingAllHeaderParam(boolean isCheckingAllHeaderParam) {
        this.isCheckingAllHeaderParam = isCheckingAllHeaderParam;
        return this;
    }

    public PreferencesUtil withIsCheckingAllBase64Param(boolean isCheckingAllBase64Param) {
        this.isCheckingAllBase64Param = isCheckingAllBase64Param;
        return this;
    }
    
    public PreferencesUtil withIsCheckingAllJsonParam(boolean isCheckingAllJSONParam) {
        this.isCheckingAllJsonParam = isCheckingAllJSONParam;
        return this;
    }

    public PreferencesUtil withIsCheckingAllCookieParam(boolean isCheckingAllCookieParam) {
        this.isCheckingAllCookieParam = isCheckingAllCookieParam;
        return this;
    }

    public PreferencesUtil withIsCheckingAllSoapParam(boolean isCheckingAllSOAPParam) {
        this.isCheckingAllSoapParam = isCheckingAllSOAPParam;
        return this;
    }

    public PreferencesUtil withIsParsingForm(boolean isParsingForm) {
        this.isParsingForm = isParsingForm;
        return this;
    }

    public PreferencesUtil withIsNotTestingConnection(boolean isNotTestingConnection) {
        this.isNotTestingConnection = isNotTestingConnection;
        return this;
    }

    public PreferencesUtil withIsNotProcessingCookies(boolean isNotProcessingCookies) {
        this.isNotProcessingCookies = isNotProcessingCookies;
        return this;
    }

    public PreferencesUtil withIsProcessingCsrf(boolean isProcessingCsrf) {
        this.isProcessingCsrf = isProcessingCsrf;
        return this;
    }

    public PreferencesUtil withIsTamperingBase64(boolean isTamperingBase64) {
        this.isTamperingBase64 = isTamperingBase64;
        return this;
    }

    public PreferencesUtil withIsTamperingFunctionComment(boolean isTamperingFunctionComment) {
        this.isTamperingFunctionComment = isTamperingFunctionComment;
        return this;
    }

    public PreferencesUtil withIsTamperingVersionComment(boolean isTamperingVersionComment) {
        this.isTamperingVersionComment = isTamperingVersionComment;
        return this;
    }

    public PreferencesUtil withIsTamperingEqualToLike(boolean isTamperingEqualToLike) {
        this.isTamperingEqualToLike = isTamperingEqualToLike;
        return this;
    }

    public PreferencesUtil withIsTamperingRandomCase(boolean isTamperingRandomCase) {
        this.isTamperingRandomCase = isTamperingRandomCase;
        return this;
    }

    public PreferencesUtil withIsTamperingEval(boolean isTamperingEval) {
        this.isTamperingEval = isTamperingEval;
        return this;
    }

    public PreferencesUtil withIsTamperingSpaceToMultilineComment(boolean isTamperingSpaceToMultilineComment) {
        this.isTamperingSpaceToMultilineComment = isTamperingSpaceToMultilineComment;
        return this;
    }

    public PreferencesUtil withIsTamperingSpaceToDashComment(boolean isTamperingSpaceToDashComment) {
        this.isTamperingSpaceToDashComment = isTamperingSpaceToDashComment;
        return this;
    }

    public PreferencesUtil withIsTamperingSpaceToSharpComment(boolean isTamperingSpaceToSharpComment) {
        this.isTamperingSpaceToSharpComment = isTamperingSpaceToSharpComment;
        return this;
    }

    public PreferencesUtil withCsrfUserTag(String csrfUserTag) {
        this.csrfUserTag = csrfUserTag;
        return this;
    }
    
    public PreferencesUtil withCsrfUserTagOutput(String csrfUserTagOutput) {
        this.csrfUserTagOutput = csrfUserTagOutput;
        return this;
    }

    public PreferencesUtil withIsCsrfUserTag(boolean isCsrfUserTag) {
        this.isCsrfUserTag = isCsrfUserTag;
        return this;
    }

    public PreferencesUtil withIsLimitingThreads(boolean isLimitingThreads) {
        this.isLimitingThreads = isLimitingThreads;
        return this;
    }
    
    public PreferencesUtil withIsConnectionTimeout(boolean isConnectionTimeout) {
        this.isConnectionTimeout = isConnectionTimeout;
        return this;
    }
    
    public PreferencesUtil withIsLimitingSleepTimeStrategy(boolean isLimitingSleepTimeStrategy) {
        this.isLimitingSleepTimeStrategy = isLimitingSleepTimeStrategy;
        return this;
    }

    public PreferencesUtil withCountLimitingThreads(int countLimitingThreads) {
        this.countLimitingThreads = countLimitingThreads;
        return this;
    }
    
    public PreferencesUtil withCountConnectionTimeout(int countConnectionTimeout) {
        this.countConnectionTimeout = countConnectionTimeout;
        return this;
    }
    
    public PreferencesUtil withCountSleepTimeStrategy(int countSleepTimeStrategy) {
        this.countSleepTimeStrategy = countSleepTimeStrategy;
        return this;
    }
    
    public PreferencesUtil withIsZipStrategy(boolean isZipStrategy) {
        this.isZipStrategy = isZipStrategy;
        return this;
    }
    
    public PreferencesUtil withIsDefaultStrategy(boolean isDefaultStrategy) {
        this.isDefaultStrategy = isDefaultStrategy;
        return this;
    }
    
    public PreferencesUtil withIsDiosStrategy(boolean isDiosStrategy) {
        this.isDiosStrategy = isDiosStrategy;
        return this;
    }
    
    public PreferencesUtil withIsPerfIndexDisabled(boolean isPerfIndexDisabled) {
        this.isPerfIndexDisabled = isPerfIndexDisabled;
        return this;
    }
    
    public PreferencesUtil withIsUrlEncodingDisabled(boolean isUrlEncodingDisabled) {
        this.isUrlEncodingDisabled = isUrlEncodingDisabled;
        return this;
    }

    public PreferencesUtil withIsUrlRandomSuffixDisabled(boolean isUrlRandomSuffixDisabled) {
        this.isUrlRandomSuffixDisabled = isUrlRandomSuffixDisabled;
        return this;
    }

    public PreferencesUtil withIsLimitingUnionIndex(boolean isLimitingUnionIndex) {
        this.isLimitingUnionIndex = isLimitingUnionIndex;
        return this;
    }

    public PreferencesUtil withCountUnionIndex(int countUnionIndex) {
        this.countUnionIndex = countUnionIndex;
        return this;
    }

    public PreferencesUtil withIsStrategyTimeDisabled(boolean isStrategyTimeDisabled) {
        this.isStrategyTimeDisabled = isStrategyTimeDisabled;
        return this;
    }

    public PreferencesUtil withIsStrategyBlindBitDisabled(boolean isStrategyBlindBitDisabled) {
        this.isStrategyBlindBitDisabled = isStrategyBlindBitDisabled;
        return this;
    }

    public PreferencesUtil withIsStrategyBlindBinDisabled(boolean isStrategyBlindBinDisabled) {
        this.isStrategyBlindBinDisabled = isStrategyBlindBinDisabled;
        return this;
    }

    public PreferencesUtil withIsStrategyMultibitDisabled(boolean isStrategyMultibitDisabled) {
        this.isStrategyMultibitDisabled = isStrategyMultibitDisabled;
        return this;
    }

    public PreferencesUtil withIsStrategyStackDisabled(boolean isStrategyStackDisabled) {
        this.isStrategyStackDisabled = isStrategyStackDisabled;
        return this;
    }

    public PreferencesUtil withIsStrategyErrorDisabled(boolean isStrategyErrorDisabled) {
        this.isStrategyErrorDisabled = isStrategyErrorDisabled;
        return this;
    }

    public PreferencesUtil withIsStrategyUnionDisabled(boolean isStrategyUnionDisabled) {
        this.isStrategyUnionDisabled = isStrategyUnionDisabled;
        return this;
    }

    public PreferencesUtil withThemeFlatLafName(String themeFlatLafName) {
        this.themeFlatLafName = themeFlatLafName;
        return this;
    }

    public PreferencesUtil withIsUrlDecodeNetworkTab(boolean isUrlDecodeNetworkTab) {
        this.isUrlDecodeNetworkTab = isUrlDecodeNetworkTab;
        return this;
    }

    public PreferencesUtil withLanguageTag(String languageTag) {
        this.languageTag = languageTag;
        return this;
    }

    public PreferencesUtil withIsUserAgentRandom(boolean selected) {
        this.isUserAgentRandom = selected;
        return this;
    }
}

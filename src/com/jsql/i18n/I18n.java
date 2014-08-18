package com.jsql.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private I18n() {
        // Disable constructor
    }
    
    static {
//        Locale.setDefault(Locale.FRENCH);
    }
    
    private static final ResourceBundle labels = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    
    public static final String SELECT_ALL = (String) labels.getObject("selectAll");
    public static final String COPY = (String) labels.getObject("copy");
    public static final String COPY_PAGE_URL = (String) labels.getObject("copyPageURL");
    
    public static final String NEW_WINDOW = (String) labels.getObject("newWindow");
    public static final String STARTING_NEW_WINDOW = (String) labels.getObject("startingNewWindow");
    public static final String ERROR_OPENING_NEW_WINDOW = (String) labels.getObject("errorOpeningNewWindow");
    
    public static final String CHECK_ALL = (String) labels.getObject("checkAll");
    public static final String UNCHECK_ALL = (String) labels.getObject("uncheckAll");
    
    public static final String LOAD_STOP = (String) labels.getObject("loadStop");
    public static final String PAUSE_RESUME = (String) labels.getObject("pauseResume");
    
    public static final String ENTER_ADDRESS = (String) labels.getObject("enterAddress");
    
    /**
     * Managers
     */
    public static final String NO_DATABASE = (String) labels.getObject("noDatabase");
    public static final String WEBSHELL = (String) labels.getObject("webshell");
    public static final String WEBSHELL_TOOLTIP = (String) labels.getObject("webshellTooltip");
    public static final String SQLSHELL = (String) labels.getObject("sqlshell");
    public static final String SQLSHELL_TOOLTIP = (String) labels.getObject("sqlshellTooltip");
    public static final String UPLOAD = (String) labels.getObject("upload");
    public static final String UPLOAD_TOOLTIP = (String) labels.getObject("uploadTooltip");
    public static final String BRUTEFORCE = (String) labels.getObject("bruteForce");
    public static final String BRUTEFORCE_TOOLTIP = (String) labels.getObject("bruteForceTooltip");
    public static final String CODER = (String) labels.getObject("coder");
    public static final String CODER_TOOLTIP = (String) labels.getObject("coderTooltip");
    public static final String DATABASE = (String) labels.getObject("database");
    public static final String DATABASE_TOOLTIP = (String) labels.getObject("databaseTooltip");
    public static final String ADMINPAGE = (String) labels.getObject("adminPage");
    public static final String ADMINPAGE_TOOLTIP = (String) labels.getObject("adminPageTooltip");
    public static final String FILE = (String) labels.getObject("file");
    public static final String FILE_TOOLTIP = (String) labels.getObject("fileTooltip");
    
    /**
     * Top panel
     */
    public static final String GET_TOOLTIP = (String) labels.getObject("getTooltip");
    public static final String POST_TOOLTIP = (String) labels.getObject("postTooltip");
    public static final String COOKIE_TOOLTIP = (String) labels.getObject("cookieTooltip");
    public static final String HEADER_TOOLTIP = (String) labels.getObject("headerTooltip");
    public static final String GET_METHOD = (String) labels.getObject("getMethod");
    public static final String POST_METHOD = (String) labels.getObject("postMethod");
    public static final String COOKIE_METHOD = (String) labels.getObject("cookieMethod");
    public static final String HEADER_METHOD = (String) labels.getObject("headerMethod");
    public static final String BUTTON_START_INJECTION = (String) labels.getObject("buttonStartInjection");
    public static final String BUTTON_ADVANCED = (String) labels.getObject("buttonAdvanced");
    public static final String DIALOG_NEW_INJECTION_TITLE = (String) labels.getObject("dialogNewInjectionTitle");
    public static final String DIALOG_NEW_INJECTION_TEXT = (String) labels.getObject("dialogNewInjectionText");
    
    /**
     * Statusbar
     */
    public static final String LABEL_TIMEBASED_TOOLTIP = (String) labels.getObject("labelTimeBasedToolTip");
    public static final String LABEL_BLIND_TOOLTIP = (String) labels.getObject("labelBlindToolTip");
    public static final String LABEL_ERRORBASED_TOOLTIP = (String) labels.getObject("labelErrorBasedToolTip");
    public static final String LABEL_NORMAL_TOOLTIP = (String) labels.getObject("labelNormalToolTipText");
    
    public static final String LABEL_NORMAL = (String) labels.getObject("labelNormal");
    public static final String LABEL_ERRORBASED = (String) labels.getObject("labelErrorBased");
    public static final String LABEL_BLIND = (String) labels.getObject("labelBlind");
    public static final String LABEL_TIMEBASED = (String) labels.getObject("labelTimeBased");
    
    public static final String TITLE_DATABASEVERSION = (String) labels.getObject("titleDatabaseVersion");
    public static final String TITLE_CURRENTDB = (String) labels.getObject("titleCurrentDB");
    public static final String TITLE_CURRENTUSER = (String) labels.getObject("titleCurrentUser");
    public static final String TITLE_AUTHENTICATEDUSER = (String) labels.getObject("titleAuthenticatedUser");
    
    /**
     * Panel bottom
     */
    public static final String JAVA_TAB_LABEL = (String) labels.getObject("javaTabLabel");
    public static final String JAVA_TAB_TOOLTIP = (String) labels.getObject("javaTabTooltip");
    public static final String NETWORK_TAB_LABEL = (String) labels.getObject("networkTabLabel");
    public static final String NETWORK_TAB_TOOLTIP = (String) labels.getObject("networkTabTooltip");
    public static final String BINARY_TAB_LABEL = (String) labels.getObject("binaryTabLabel");
    public static final String BINARY_TAB_TOOLTIP  = (String) labels.getObject("binaryTabTooltip");
    public static final String CHUNK_TAB_LABEL = (String) labels.getObject("chunkTabLabel");
    public static final String CHUNK_TAB_TOOLTIP = (String) labels.getObject("chunkTabTooltip");
    public static final String CONSOLE_TAB_LABEL = (String) labels.getObject("consoleTabLabel");
    public static final String CONSOLE_TAB_TOOLTIP = (String) labels.getObject("consoleTabTooltip");
    
    public static final String NETWORK_TAB_HEADERS_LABEL  = (String) labels.getObject("networkTabHeadersLabel");
    public static final String NETWORK_TAB_COOKIES_LABEL  = (String) labels.getObject("networkTabCookiesLabel");
    public static final String NETWORK_TAB_PARAMS_LABEL   = (String) labels.getObject("networkTabParamsLabel");
    public static final String NETWORK_TAB_RESPONSE_LABEL = (String) labels.getObject("networkTabResponseLabel");
    public static final String NETWORK_TAB_TIMING_LABEL   = (String) labels.getObject("networkTabTimingLabel");
    public static final String NETWORK_TAB_PREVIEW_LABEL  = (String) labels.getObject("networkTabPreviewLabel");
                                                                                                           
    public static final String NETWORK_TAB_METHOD_COLUMN  = (String) labels.getObject("networkTabMethodColumn");
    public static final String NETWORK_TAB_URL_COLUMN     = (String) labels.getObject("networkTabUrlColumn");
    public static final String NETWORK_TAB_SIZE_COLUMN    = (String) labels.getObject("networkTabSizeColumn");
    public static final String NETWORK_TAB_TYPE_COLUMN    = (String) labels.getObject("networkTabTypeColumn");
    
    /**
     * Menubar
     */
    public static final String ITEM_UPDATE = (String) labels.getObject("itemUpdate");
    public static final String ITEM_ABOUT = (String) labels.getObject("itemAbout");
    public static final String MENU_HELP = (String) labels.getObject("menuHelp");
    public static final String MENU_VIEW = (String) labels.getObject("menuView");
    public static final String MENU_PANEL = (String) labels.getObject("menuPanel");
    public static final String MENU_PREFERENCES = (String) labels.getObject("menuPreferences");
    public static final String MENU_WINDOWS = (String) labels.getObject("menuWindows");
    public static final String MENU_EDIT = (String) labels.getObject("menuEdit");
    public static final String ITEM_EXIT = (String) labels.getObject("itemExit");
    public static final String MENU_FILE = (String) labels.getObject("menuFile");
    
    /**
     * Update action
     */
    public static final String UPDATE_EXCEPTION = (String) labels.getObject("updateException");
    public static final String UPDATE_NEW_VERSION_AVAILABLE = (String) labels.getObject("updateNewVersionAvailable");
    public static final String UPDATE_UPTODATE = (String) labels.getObject("updateUpToDate");
    public static final String UPDATE_LOADING = (String) labels.getObject("updateLoading");
}

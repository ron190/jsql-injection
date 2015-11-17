package com.jsql.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
//    static {
//        Locale.setDefault(Locale.FRENCH);
//    }
    
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
    public static final String SCANLIST = (String) labels.getObject("scanList");
    public static final String SCANLIST_TOOLTIP = (String) labels.getObject("scanListTooltip");
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
    public static final String NETWORK_TAB_SOURCE_LABEL  = (String) labels.getObject("networkTabSourceLabel");
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
    
    /**
     * Webshell
     */
    public static final String PRIVILEGE_LABEL = (String) labels.getObject("privilegeLabel");
    public static final String PRIVILEGE_TOOLTIP = (String) labels.getObject("privilegeToolTip");
    public static final String SHELL_URL_LABEL = (String) labels.getObject("shellURLLabel");
    public static final String SHELL_URL_TOOLTIP = (String) labels.getObject("shellURLTooltip");
    public static final String SHELL_RUN_BUTTON = (String) labels.getObject("shellRunButton");
    public static final String SHELL_RUN_BUTTON_TOOLTIP = (String) labels.getObject("shellRunButtonTooltip");
    
    /**
     * Upload
     */
    public static final String UPLOAD_DIALOG_TEXT = (String) labels.getObject("uploadDialogText");
    public static final String UPLOAD_URL_LABEL = (String) labels.getObject("uploadURLLabel");
    public static final String UPLOAD_URL_TOOLTIP = (String) labels.getObject("uploadURLTooltip");
    public static final String UPLOAD_RUN_BUTTON = (String) labels.getObject("uploadRunButton");
    public static final String UPLOAD_RUN_BUTTON_TOOLTIP = (String) labels.getObject("uploadRunButtonTooltip");
    
    /**
     * SQL shell
     */
    public static final String SQL_SHELL_RUN_BUTTON = (String) labels.getObject("sqlShellRunButton");
    public static final String SQL_SHELL_USERNAME_LABEL = (String) labels.getObject("sqlShellUsernameLabel");
    public static final String SQL_SHELL_PASSWORD_LABEL = (String) labels.getObject("sqlShellPasswordLabel");
    public static final String SQL_SHELL_USERNAME_TOOLTIP = (String) labels.getObject("sqlShellUsernameTooltip");
    public static final String SQL_SHELL_PASSWORD_TOOLTIP = (String) labels.getObject("sqlShellPasswordTooltip");
    
    /**
     * File
     */
    public static final String FILE_RUN_BUTTON = (String) labels.getObject("fileRunButton");
    public static final String FILE_RUN_BUTTON_TOOLTIP = (String) labels.getObject("fileRunButtonTooltip");
    
    /**
     * Coder
     */
    public static final String CODER_RUN_BUTTON = (String) labels.getObject("coderRunButton");
    
    /**
     * Bruteforce
     */
    public static final String BRUTEFORCE_HASH = (String) labels.getObject("bruteForceHash");
    public static final String BRUTEFORCE_HASH_TOOLTIP = (String) labels.getObject("bruteForceHashTooltip");
    public static final String BRUTEFORCE_HASH_TYPE_TOOLTIP = (String) labels.getObject("bruteForceHashTypeTooltip");
    public static final String BRUTEFORCE_LCASE_TOOLTIP = (String) labels.getObject("bruteForceLCaseTooltip");
    public static final String BRUTEFORCE_UCASE_TOOLTIP = (String) labels.getObject("bruteForceUCaseTooltip");
    public static final String BRUTEFORCE_NUM_TOOLTIP = (String) labels.getObject("bruteForceNumTooltip");
    public static final String BRUTEFORCE_SPEC_TOOLTIP = (String) labels.getObject("bruteForceSpecTooltip");
    public static final String BRUTEFORCE_EXCLUDE_LABEL = (String) labels.getObject("bruteForceExcludeLabel");
    public static final String BRUTEFORCE_EXCLUDE_TOOLTIP = (String) labels.getObject("bruteForceExcludeTooltip");
    public static final String BRUTEFORCE_MIN_LABEL = (String) labels.getObject("bruteForceMinLabel");
    public static final String BRUTEFORCE_MAX_LABEL = (String) labels.getObject("bruteForceMaxLabel");
    public static final String BRUTEFORCE_MIN_TOOLTIP = (String) labels.getObject("bruteForceMinTooltip");
    public static final String BRUTEFORCE_MAX_TOOLTIP = (String) labels.getObject("bruteForceMaxTooltip");
    public static final String BRUTEFORCE_RUN_BUTTON = (String) labels.getObject("bruteForceRunButton");
    public static final String BRUTEFORCE_RUN_BUTTON_TOOLTIP = (String) labels.getObject("bruteForceRunButtonTooltip");

    /**
     * Bruteforce action
     */
    public static final String BRUTEFORCE_INCORRECT_LENGTH = (String) labels.getObject("bruteForceIncorrectLength");
    public static final String BRUTEFORCE_CHARACTER_RANGE = (String) labels.getObject("bruteForceCharacterRange");
    public static final String BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH = (String) labels.getObject("bruteForceIncorrectMinMaxLength");
    public static final String BRUTEFORCE_STOP = (String) labels.getObject("bruteForceStop");
    public static final String BRUTEFORCE_EMPTY_HASH = (String) labels.getObject("bruteForceEmptyHash");
    public static final String BRUTEFORCE_CURRENT_STRING = (String) labels.getObject("bruteForceCurrentString");
    public static final String BRUTEFORCE_CURRENT_HASH = (String) labels.getObject("bruteForceCurrentHash");
    public static final String BRUTEFORCE_POSSIBILITIES = (String) labels.getObject("bruteForcePossibilities");
    public static final String BRUTEFORCE_CHECKED_HASHES = (String) labels.getObject("bruteForceCheckedHashes");
    public static final String BRUTEFORCE_ESTIMATED = (String) labels.getObject("bruteForceEstimated");
    public static final String BRUTEFORCE_PERSECOND = (String) labels.getObject("bruteForcePerSecond");
    public static final String BRUTEFORCE_TRAVERSING_REMAINING = (String) labels.getObject("bruteForceTraversingRemaining");
    public static final String BRUTEFORCE_DAYS = (String) labels.getObject("bruteForceDays");
    public static final String BRUTEFORCE_HOURS = (String) labels.getObject("bruteForceHours");
    public static final String BRUTEFORCE_MINUTES = (String) labels.getObject("bruteForceMinutes");
    public static final String BRUTEFORCE_SECONDS = (String) labels.getObject("bruteForceSeconds");
    public static final String BRUTEFORCE_PERCENT_DONE = (String) labels.getObject("bruteForcePercentDone");
    public static final String BRUTEFORCE_ABORTED = (String) labels.getObject("bruteForceAborted");
    public static final String BRUTEFORCE_FOUND_HASH = (String) labels.getObject("bruteForceFoundHash");
    public static final String BRUTEFORCE_STRING = (String) labels.getObject("bruteForceString");
    public static final String BRUTEFORCE_HASH_NOT_FOUND = (String) labels.getObject("bruteForceHashNotFound");
    public static final String BRUTEFORCE_START = (String) labels.getObject("bruteForceStart");

    /**
     * Admin page
     */
    public static final String ADMIN_PAGE_RUN_BUTTON = (String) labels.getObject("adminPageRunButton");
    public static final String ADMIN_PAGE_RUN_BUTTON_TOOLTIP = (String) labels.getObject("adminPageRunButtonTooltip");
    
    /**
     * Bulk Test
     */
    public static final String SCANLIST_RUN_BUTTON = (String) labels.getObject("scanListRunButton");
    public static final String SCANLIST_RUN_BUTTON_TOOLTIP = (String) labels.getObject("scanListRunButtonTooltip");
    
    /**
     * List menu
     */
    public static final String IMPORT = (String) labels.getObject("import");
    public static final String EXPORT = (String) labels.getObject("export");
    public static final String CUT = (String) labels.getObject("cut");
    public static final String PASTE = (String) labels.getObject("paste");
    public static final String DELETE = (String) labels.getObject("delete");
    public static final String NEW_VALUE = (String) labels.getObject("newValue");
    public static final String RESTORE_DEFAULT = (String) labels.getObject("restoreDefault");
    
    public static final String OK = (String) labels.getObject("ok");
    public static final String CANCEL = (String) labels.getObject("cancel");
    public static final String LIST_ADD_VALUE = (String) labels.getObject("listAddValue");
    public static final String LIST_ADD_VALUE_LABEL = (String) labels.getObject("listAddValueLabel");
    
    public static final String LIST_EXPORT = (String) labels.getObject("listExport");
    public static final String LIST_CONFIRM_REPLACE = (String) labels.getObject("listConfirmReplace");
    public static final String LIST_CONFIRM_EXPORT = (String) labels.getObject("listConfirmExport");
    
    public static final String REPLACE = (String) labels.getObject("replace");
    public static final String ADD = (String) labels.getObject("add");
    public static final String LIST_IMPORT_REPLACE = (String) labels.getObject("listImportReplace");
    public static final String LIST_IMPORT = (String) labels.getObject("listImport");
    public static final String LIST_IMPORT_ERROR = (String) labels.getObject("listImportError");
    public static final String LIST_IMPORT_ERROR_TEXT = (String) labels.getObject("listImportErrorText");
    
    /**
     * Dialog replace file
     */
    public static final String DIALOG_REPLACE_FILE_CONFIRM = (String) labels.getObject("dialogReplaceFileConfirm");
    public static final String DIALOG_REPLACE_FILE_TITLE = (String) labels.getObject("dialogReplaceFileTitle");
    
    private I18n() {
        // Disable constructor
    }
}

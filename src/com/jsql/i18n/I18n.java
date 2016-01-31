package com.jsql.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
//    static {
//        Locale.setDefault(Locale.FRENCH);
//    }
    
    private static final ResourceBundle CURRENT_LOCALE = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    // ^^^^^^^ Report ExceptionInInitializerError #1548 
    
    public static final String SELECT_ALL = (String) CURRENT_LOCALE.getObject("selectAll");
    public static final String COPY = (String) CURRENT_LOCALE.getObject("copy");
    public static final String COPY_PAGE_URL = (String) CURRENT_LOCALE.getObject("copyPageURL");
    
    public static final String NEW_WINDOW = (String) CURRENT_LOCALE.getObject("newWindow");
    public static final String STARTING_NEW_WINDOW = (String) CURRENT_LOCALE.getObject("startingNewWindow");
    public static final String ERROR_OPENING_NEW_WINDOW = (String) CURRENT_LOCALE.getObject("errorOpeningNewWindow");
    
    public static final String CHECK_ALL = (String) CURRENT_LOCALE.getObject("checkAll");
    public static final String UNCHECK_ALL = (String) CURRENT_LOCALE.getObject("uncheckAll");
    
    public static final String LOAD = (String) CURRENT_LOCALE.getObject("load");
    public static final String STOP = (String) CURRENT_LOCALE.getObject("stop");
    public static final String PAUSE = (String) CURRENT_LOCALE.getObject("pause");
    public static final String RESUME = (String) CURRENT_LOCALE.getObject("resume");
    
    public static final String ENTER_ADDRESS = (String) CURRENT_LOCALE.getObject("enterAddress");
    
    /**
     * Managers
     */
    public static final String NO_DATABASE = (String) CURRENT_LOCALE.getObject("noDatabase");
    public static final String WEBSHELL = (String) CURRENT_LOCALE.getObject("webshell");
    public static final String WEBSHELL_TOOLTIP = (String) CURRENT_LOCALE.getObject("webshellTooltip");
    public static final String SQLSHELL = (String) CURRENT_LOCALE.getObject("sqlshell");
    public static final String SQLSHELL_TOOLTIP = (String) CURRENT_LOCALE.getObject("sqlshellTooltip");
    public static final String UPLOAD = (String) CURRENT_LOCALE.getObject("upload");
    public static final String UPLOAD_TOOLTIP = (String) CURRENT_LOCALE.getObject("uploadTooltip");
    public static final String BRUTEFORCE = (String) CURRENT_LOCALE.getObject("bruteForce");
    public static final String BRUTEFORCE_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceTooltip");
    public static final String CODER = (String) CURRENT_LOCALE.getObject("coder");
    public static final String CODER_TOOLTIP = (String) CURRENT_LOCALE.getObject("coderTooltip");
    public static final String DATABASE = (String) CURRENT_LOCALE.getObject("database");
    public static final String DATABASE_TOOLTIP = (String) CURRENT_LOCALE.getObject("databaseTooltip");
    public static final String ADMINPAGE = (String) CURRENT_LOCALE.getObject("adminPage");
    public static final String ADMINPAGE_TOOLTIP = (String) CURRENT_LOCALE.getObject("adminPageTooltip");
    public static final String SCANLIST = (String) CURRENT_LOCALE.getObject("scanList");
    public static final String SCANLIST_TOOLTIP = (String) CURRENT_LOCALE.getObject("scanListTooltip");
    public static final String FILE = (String) CURRENT_LOCALE.getObject("file");
    public static final String FILE_TOOLTIP = (String) CURRENT_LOCALE.getObject("fileTooltip");
    
    /**
     * Top panel
     */
    public static final String GET_TOOLTIP = (String) CURRENT_LOCALE.getObject("getTooltip");
    public static final String POST_TOOLTIP = (String) CURRENT_LOCALE.getObject("postTooltip");
    public static final String HEADER_TOOLTIP = (String) CURRENT_LOCALE.getObject("headerTooltip");
    public static final String GET_METHOD = (String) CURRENT_LOCALE.getObject("getMethod");
    public static final String POST_METHOD = (String) CURRENT_LOCALE.getObject("postMethod");
    public static final String HEADER_METHOD = (String) CURRENT_LOCALE.getObject("headerMethod");
    public static final String BUTTON_START_INJECTION = (String) CURRENT_LOCALE.getObject("buttonStartInjection");
    public static final String BUTTON_ADVANCED = (String) CURRENT_LOCALE.getObject("buttonAdvanced");
    public static final String DIALOG_NEW_INJECTION_TITLE = (String) CURRENT_LOCALE.getObject("dialogNewInjectionTitle");
    public static final String DIALOG_NEW_INJECTION_TEXT = (String) CURRENT_LOCALE.getObject("dialogNewInjectionText");
    
    /**
     * Statusbar
     */
    public static final String LABEL_TIMEBASED_TOOLTIP = (String) CURRENT_LOCALE.getObject("labelTimeBasedToolTip");
    public static final String LABEL_BLIND_TOOLTIP = (String) CURRENT_LOCALE.getObject("labelBlindToolTip");
    public static final String LABEL_ERRORBASED_TOOLTIP = (String) CURRENT_LOCALE.getObject("labelErrorBasedToolTip");
    public static final String LABEL_NORMAL_TOOLTIP = (String) CURRENT_LOCALE.getObject("labelNormalToolTipText");
    
    public static final String LABEL_NORMAL = (String) CURRENT_LOCALE.getObject("labelNormal");
    public static final String LABEL_ERRORBASED = (String) CURRENT_LOCALE.getObject("labelErrorBased");
    public static final String LABEL_BLIND = (String) CURRENT_LOCALE.getObject("labelBlind");
    public static final String LABEL_TIMEBASED = (String) CURRENT_LOCALE.getObject("labelTimeBased");
    
    public static final String TITLE_DATABASEVERSION = (String) CURRENT_LOCALE.getObject("titleDatabaseVersion");
    public static final String TITLE_CURRENTDB = (String) CURRENT_LOCALE.getObject("titleCurrentDB");
    public static final String TITLE_CURRENTUSER = (String) CURRENT_LOCALE.getObject("titleCurrentUser");
    public static final String TITLE_AUTHENTICATEDUSER = (String) CURRENT_LOCALE.getObject("titleAuthenticatedUser");
    
    /**
     * Panel bottom
     */
    public static final String JAVA_TAB_LABEL = (String) CURRENT_LOCALE.getObject("javaTabLabel");
    public static final String JAVA_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("javaTabTooltip");
    public static final String NETWORK_TAB_LABEL = (String) CURRENT_LOCALE.getObject("networkTabLabel");
    public static final String NETWORK_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("networkTabTooltip");
    public static final String BINARY_TAB_LABEL = (String) CURRENT_LOCALE.getObject("binaryTabLabel");
    public static final String BINARY_TAB_TOOLTIP  = (String) CURRENT_LOCALE.getObject("binaryTabTooltip");
    public static final String CHUNK_TAB_LABEL = (String) CURRENT_LOCALE.getObject("chunkTabLabel");
    public static final String CHUNK_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("chunkTabTooltip");
    public static final String CONSOLE_TAB_LABEL = (String) CURRENT_LOCALE.getObject("consoleTabLabel");
    public static final String CONSOLE_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("consoleTabTooltip");
    
    public static final String NETWORK_TAB_HEADERS_LABEL  = (String) CURRENT_LOCALE.getObject("networkTabHeadersLabel");
    public static final String NETWORK_TAB_PARAMS_LABEL   = (String) CURRENT_LOCALE.getObject("networkTabParamsLabel");
    public static final String NETWORK_TAB_RESPONSE_LABEL = (String) CURRENT_LOCALE.getObject("networkTabResponseLabel");
    public static final String NETWORK_TAB_TIMING_LABEL   = (String) CURRENT_LOCALE.getObject("networkTabTimingLabel");
    public static final String NETWORK_TAB_SOURCE_LABEL  = (String) CURRENT_LOCALE.getObject("networkTabSourceLabel");
    public static final String NETWORK_TAB_PREVIEW_LABEL  = (String) CURRENT_LOCALE.getObject("networkTabPreviewLabel");
                                                                                                           
    public static final String NETWORK_TAB_METHOD_COLUMN  = (String) CURRENT_LOCALE.getObject("networkTabMethodColumn");
    public static final String NETWORK_TAB_URL_COLUMN     = (String) CURRENT_LOCALE.getObject("networkTabUrlColumn");
    public static final String NETWORK_TAB_SIZE_COLUMN    = (String) CURRENT_LOCALE.getObject("networkTabSizeColumn");
    public static final String NETWORK_TAB_TYPE_COLUMN    = (String) CURRENT_LOCALE.getObject("networkTabTypeColumn");
    
    /**
     * Menubar
     */
    public static final String ITEM_REPORTISSUE = (String) CURRENT_LOCALE.getObject("itemReportIssue");
    public static final String ITEM_UPDATE = (String) CURRENT_LOCALE.getObject("itemUpdate");
    public static final String ITEM_ABOUT = (String) CURRENT_LOCALE.getObject("itemAbout");
    public static final String MENU_HELP = (String) CURRENT_LOCALE.getObject("menuHelp");
    public static final String MENU_VIEW = (String) CURRENT_LOCALE.getObject("menuView");
    public static final String MENU_PANEL = (String) CURRENT_LOCALE.getObject("menuPanel");
    public static final String MENU_PREFERENCES = (String) CURRENT_LOCALE.getObject("menuPreferences");
    public static final String MENU_WINDOWS = (String) CURRENT_LOCALE.getObject("menuWindows");
    public static final String MENU_EDIT = (String) CURRENT_LOCALE.getObject("menuEdit");
    public static final String ITEM_EXIT = (String) CURRENT_LOCALE.getObject("itemExit");
    public static final String MENU_FILE = (String) CURRENT_LOCALE.getObject("menuFile");
    
    /**
     * Update action
     */
    public static final String UPDATE_EXCEPTION = (String) CURRENT_LOCALE.getObject("updateException");
    public static final String UPDATE_NEW_VERSION_AVAILABLE = (String) CURRENT_LOCALE.getObject("updateNewVersionAvailable");
    public static final String UPDATE_UPTODATE = (String) CURRENT_LOCALE.getObject("updateUpToDate");
    public static final String UPDATE_LOADING = (String) CURRENT_LOCALE.getObject("updateLoading");
    
    /**
     * Webshell
     */
    public static final String PRIVILEGE_LABEL = (String) CURRENT_LOCALE.getObject("privilegeLabel");
    public static final String PRIVILEGE_TOOLTIP = (String) CURRENT_LOCALE.getObject("privilegeToolTip");
    public static final String SHELL_URL_LABEL = (String) CURRENT_LOCALE.getObject("shellURLLabel");
    public static final String SHELL_URL_TOOLTIP = (String) CURRENT_LOCALE.getObject("shellURLTooltip");
    public static final String SHELL_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("shellRunButton");
    public static final String SHELL_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("shellRunButtonTooltip");
    
    /**
     * Upload
     */
    public static final String UPLOAD_DIALOG_TEXT = (String) CURRENT_LOCALE.getObject("uploadDialogText");
    public static final String UPLOAD_URL_LABEL = (String) CURRENT_LOCALE.getObject("uploadURLLabel");
    public static final String UPLOAD_URL_TOOLTIP = (String) CURRENT_LOCALE.getObject("uploadURLTooltip");
    public static final String UPLOAD_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("uploadRunButton");
    public static final String UPLOAD_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("uploadRunButtonTooltip");
    
    /**
     * SQL shell
     */
    public static final String SQL_SHELL_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("sqlShellRunButton");
    public static final String SQL_SHELL_USERNAME_LABEL = (String) CURRENT_LOCALE.getObject("sqlShellUsernameLabel");
    public static final String SQL_SHELL_PASSWORD_LABEL = (String) CURRENT_LOCALE.getObject("sqlShellPasswordLabel");
    public static final String SQL_SHELL_USERNAME_TOOLTIP = (String) CURRENT_LOCALE.getObject("sqlShellUsernameTooltip");
    public static final String SQL_SHELL_PASSWORD_TOOLTIP = (String) CURRENT_LOCALE.getObject("sqlShellPasswordTooltip");
    
    /**
     * File
     */
    public static final String FILE_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("fileRunButton");
    public static final String FILE_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("fileRunButtonTooltip");
    
    /**
     * Coder
     */
    public static final String CODER_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("coderRunButton");
    
    /**
     * Bruteforce
     */
    public static final String BRUTEFORCE_HASH = (String) CURRENT_LOCALE.getObject("bruteForceHash");
    public static final String BRUTEFORCE_HASH_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceHashTooltip");
    public static final String BRUTEFORCE_HASH_TYPE_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceHashTypeTooltip");
    public static final String BRUTEFORCE_LCASE_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceLCaseTooltip");
    public static final String BRUTEFORCE_UCASE_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceUCaseTooltip");
    public static final String BRUTEFORCE_NUM_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceNumTooltip");
    public static final String BRUTEFORCE_SPEC_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceSpecTooltip");
    public static final String BRUTEFORCE_EXCLUDE_LABEL = (String) CURRENT_LOCALE.getObject("bruteForceExcludeLabel");
    public static final String BRUTEFORCE_EXCLUDE_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceExcludeTooltip");
    public static final String BRUTEFORCE_MIN_LABEL = (String) CURRENT_LOCALE.getObject("bruteForceMinLabel");
    public static final String BRUTEFORCE_MAX_LABEL = (String) CURRENT_LOCALE.getObject("bruteForceMaxLabel");
    public static final String BRUTEFORCE_MIN_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceMinTooltip");
    public static final String BRUTEFORCE_MAX_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceMaxTooltip");
    public static final String BRUTEFORCE_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("bruteForceRunButton");
    public static final String BRUTEFORCE_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("bruteForceRunButtonTooltip");

    /**
     * Bruteforce action
     */
    public static final String BRUTEFORCE_INCORRECT_LENGTH = (String) CURRENT_LOCALE.getObject("bruteForceIncorrectLength");
    public static final String BRUTEFORCE_CHARACTER_RANGE = (String) CURRENT_LOCALE.getObject("bruteForceCharacterRange");
    public static final String BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH = (String) CURRENT_LOCALE.getObject("bruteForceIncorrectMinMaxLength");
    public static final String BRUTEFORCE_STOP = (String) CURRENT_LOCALE.getObject("bruteForceStop");
    public static final String BRUTEFORCE_EMPTY_HASH = (String) CURRENT_LOCALE.getObject("bruteForceEmptyHash");
    public static final String BRUTEFORCE_CURRENT_STRING = (String) CURRENT_LOCALE.getObject("bruteForceCurrentString");
    public static final String BRUTEFORCE_CURRENT_HASH = (String) CURRENT_LOCALE.getObject("bruteForceCurrentHash");
    public static final String BRUTEFORCE_POSSIBILITIES = (String) CURRENT_LOCALE.getObject("bruteForcePossibilities");
    public static final String BRUTEFORCE_CHECKED_HASHES = (String) CURRENT_LOCALE.getObject("bruteForceCheckedHashes");
    public static final String BRUTEFORCE_ESTIMATED = (String) CURRENT_LOCALE.getObject("bruteForceEstimated");
    public static final String BRUTEFORCE_PERSECOND = (String) CURRENT_LOCALE.getObject("bruteForcePerSecond");
    public static final String BRUTEFORCE_TRAVERSING_REMAINING = (String) CURRENT_LOCALE.getObject("bruteForceTraversingRemaining");
    public static final String BRUTEFORCE_DAYS = (String) CURRENT_LOCALE.getObject("bruteForceDays");
    public static final String BRUTEFORCE_HOURS = (String) CURRENT_LOCALE.getObject("bruteForceHours");
    public static final String BRUTEFORCE_MINUTES = (String) CURRENT_LOCALE.getObject("bruteForceMinutes");
    public static final String BRUTEFORCE_SECONDS = (String) CURRENT_LOCALE.getObject("bruteForceSeconds");
    public static final String BRUTEFORCE_PERCENT_DONE = (String) CURRENT_LOCALE.getObject("bruteForcePercentDone");
    public static final String BRUTEFORCE_ABORTED = (String) CURRENT_LOCALE.getObject("bruteForceAborted");
    public static final String BRUTEFORCE_FOUND_HASH = (String) CURRENT_LOCALE.getObject("bruteForceFoundHash");
    public static final String BRUTEFORCE_STRING = (String) CURRENT_LOCALE.getObject("bruteForceString");
    public static final String BRUTEFORCE_HASH_NOT_FOUND = (String) CURRENT_LOCALE.getObject("bruteForceHashNotFound");
    public static final String BRUTEFORCE_START = (String) CURRENT_LOCALE.getObject("bruteForceStart");

    /**
     * Admin page
     */
    public static final String ADMIN_PAGE_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("adminPageRunButton");
    public static final String ADMIN_PAGE_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("adminPageRunButtonTooltip");
    
    /**
     * Bulk Test
     */
    public static final String SCANLIST_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("scanListRunButton");
    public static final String SCANLIST_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("scanListRunButtonTooltip");
    
    /**
     * List menu
     */
    public static final String IMPORT = (String) CURRENT_LOCALE.getObject("import");
    public static final String EXPORT = (String) CURRENT_LOCALE.getObject("export");
    public static final String CUT = (String) CURRENT_LOCALE.getObject("cut");
    public static final String PASTE = (String) CURRENT_LOCALE.getObject("paste");
    public static final String DELETE = (String) CURRENT_LOCALE.getObject("delete");
    public static final String NEW_VALUE = (String) CURRENT_LOCALE.getObject("newValue");
    public static final String RESTORE_DEFAULT = (String) CURRENT_LOCALE.getObject("restoreDefault");
    
    public static final String OK = (String) CURRENT_LOCALE.getObject("ok");
    public static final String CANCEL = (String) CURRENT_LOCALE.getObject("cancel");
    public static final String LIST_ADD_VALUE = (String) CURRENT_LOCALE.getObject("listAddValue");
    public static final String LIST_ADD_VALUE_LABEL = (String) CURRENT_LOCALE.getObject("listAddValueLabel");
    
    public static final String LIST_EXPORT = (String) CURRENT_LOCALE.getObject("listExport");
    public static final String LIST_CONFIRM_REPLACE = (String) CURRENT_LOCALE.getObject("listConfirmReplace");
    public static final String LIST_CONFIRM_EXPORT = (String) CURRENT_LOCALE.getObject("listConfirmExport");
    
    public static final String REPLACE = (String) CURRENT_LOCALE.getObject("replace");
    public static final String ADD = (String) CURRENT_LOCALE.getObject("add");
    public static final String LIST_IMPORT_REPLACE = (String) CURRENT_LOCALE.getObject("listImportReplace");
    public static final String LIST_IMPORT = (String) CURRENT_LOCALE.getObject("listImport");
    public static final String LIST_IMPORT_ERROR = (String) CURRENT_LOCALE.getObject("listImportError");
    public static final String LIST_IMPORT_ERROR_TEXT = (String) CURRENT_LOCALE.getObject("listImportErrorText");
    
    /**
     * Dialog replace file
     */
    public static final String DIALOG_REPLACE_FILE_CONFIRM = (String) CURRENT_LOCALE.getObject("dialogReplaceFileConfirm");
    public static final String DIALOG_REPLACE_FILE_TITLE = (String) CURRENT_LOCALE.getObject("dialogReplaceFileTitle");
    
    private I18n() {
        // Disable constructor
    }
}

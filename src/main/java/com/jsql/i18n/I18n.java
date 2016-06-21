package com.jsql.i18n;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class I18n {
//    static {
//        Locale.setDefault(Locale.FRENCH);
//    }
    
    public static ResourceBundle CURRENT_LOCALE = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    // ^^^^^^^ Report ExceptionInInitializerError #1548 
    
    public static String SELECT_ALL = (String) CURRENT_LOCALE.getObject("SELECT_ALL");
    public static String COPY = (String) CURRENT_LOCALE.getObject("COPY");
    public static String COPY_PAGE_URL = (String) CURRENT_LOCALE.getObject("COPY_PAGE_URL");
    
    public static String NEW_WINDOW = (String) CURRENT_LOCALE.getObject("NEW_WINDOW");
    public static String STARTING_NEW_WINDOW = (String) CURRENT_LOCALE.getObject("STARTING_NEW_WINDOW");
    public static String ERROR_OPENING_NEW_WINDOW = (String) CURRENT_LOCALE.getObject("ERROR_OPENING_NEW_WINDOW");
    
    public static String CHECK_ALL = (String) CURRENT_LOCALE.getObject("CHECK_ALL");
    public static String UNCHECK_ALL = (String) CURRENT_LOCALE.getObject("UNCHECK_ALL");
    
    public static String LOAD = (String) CURRENT_LOCALE.getObject("LOAD");
    public static String STOP = (String) CURRENT_LOCALE.getObject("STOP");
    public static String PAUSE = (String) CURRENT_LOCALE.getObject("PAUSE");
    public static String RESUME = (String) CURRENT_LOCALE.getObject("RESUME");
    
    public static String ENTER_ADDRESS = (String) CURRENT_LOCALE.getObject("ENTER_ADDRESS");
    
    /**
     * Managers
     */
    public static String NO_DATABASE = (String) CURRENT_LOCALE.getObject("NO_DATABASE");
    public static String WEBSHELL = (String) CURRENT_LOCALE.getObject("WEBSHELL");
    public static String WEBSHELL_TOOLTIP = (String) CURRENT_LOCALE.getObject("WEBSHELL_TOOLTIP");
    public static String SQLSHELL = (String) CURRENT_LOCALE.getObject("SQLSHELL");
    public static String SQLSHELL_TOOLTIP = (String) CURRENT_LOCALE.getObject("SQLSHELL_TOOLTIP");
    public static String UPLOAD = (String) CURRENT_LOCALE.getObject("UPLOAD");
    public static String UPLOAD_TOOLTIP = (String) CURRENT_LOCALE.getObject("UPLOAD_TOOLTIP");
    public static String BRUTEFORCE = (String) CURRENT_LOCALE.getObject("BRUTEFORCE");
    public static String BRUTEFORCE_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_TOOLTIP");
    public static String CODER = (String) CURRENT_LOCALE.getObject("CODER");
    public static String CODER_TOOLTIP = (String) CURRENT_LOCALE.getObject("CODER_TOOLTIP");
    public static String DATABASE = (String) CURRENT_LOCALE.getObject("DATABASE");
    public static String DATABASE_TOOLTIP = (String) CURRENT_LOCALE.getObject("DATABASE_TOOLTIP");
    public static String ADMINPAGE = (String) CURRENT_LOCALE.getObject("ADMINPAGE");
    public static String ADMINPAGE_TOOLTIP = (String) CURRENT_LOCALE.getObject("ADMINPAGE_TOOLTIP");
    public static String SCANLIST = (String) CURRENT_LOCALE.getObject("SCANLIST");
    public static String SCANLIST_TOOLTIP = (String) CURRENT_LOCALE.getObject("SCANLIST_TOOLTIP");
    public static String FILE = (String) CURRENT_LOCALE.getObject("FILE");
    public static String FILE_TOOLTIP = (String) CURRENT_LOCALE.getObject("FILE_TOOLTIP");
    
    /**
     * Top panel
     */
    public static String GET_TOOLTIP = (String) CURRENT_LOCALE.getObject("GET_TOOLTIP");
    public static String REQUEST_METHOD_TOOLTIP = (String) CURRENT_LOCALE.getObject("REQUEST_METHOD_TOOLTIP");
    public static String HEADER_TOOLTIP = (String) CURRENT_LOCALE.getObject("HEADER_TOOLTIP");
    public static String GET_METHOD = (String) CURRENT_LOCALE.getObject("GET_METHOD");
    public static String REQUEST_METHOD = (String) CURRENT_LOCALE.getObject("REQUEST_METHOD");
    public static String HEADER_METHOD = (String) CURRENT_LOCALE.getObject("HEADER_METHOD");
    public static String BUTTON_START_INJECTION = (String) CURRENT_LOCALE.getObject("BUTTON_START_INJECTION");
    public static String BUTTON_ADVANCED = (String) CURRENT_LOCALE.getObject("BUTTON_ADVANCED");
    public static String DIALOG_NEW_INJECTION_TITLE = (String) CURRENT_LOCALE.getObject("DIALOG_NEW_INJECTION_TITLE");
    public static String DIALOG_NEW_INJECTION_TEXT = (String) CURRENT_LOCALE.getObject("DIALOG_NEW_INJECTION_TEXT");
    
    /**
     * Statusbar
     */
    public static String LABEL_TIMEBASED_TOOLTIP = (String) CURRENT_LOCALE.getObject("LABEL_TIMEBASED_TOOLTIP");
    public static String LABEL_BLIND_TOOLTIP = (String) CURRENT_LOCALE.getObject("LABEL_BLIND_TOOLTIP");
    public static String LABEL_ERRORBASED_TOOLTIP = (String) CURRENT_LOCALE.getObject("LABEL_ERRORBASED_TOOLTIP");
    public static String LABEL_NORMAL_TOOLTIP = (String) CURRENT_LOCALE.getObject("LABEL_NORMAL_TOOLTIP");
    
    public static String LABEL_NORMAL = (String) CURRENT_LOCALE.getObject("LABEL_NORMAL");
    public static String LABEL_ERRORBASED = (String) CURRENT_LOCALE.getObject("LABEL_ERRORBASED");
    public static String LABEL_BLIND = (String) CURRENT_LOCALE.getObject("LABEL_BLIND");
    public static String LABEL_TIMEBASED = (String) CURRENT_LOCALE.getObject("LABEL_TIMEBASED");
    
    public static String TITLE_DATABASEVERSION = (String) CURRENT_LOCALE.getObject("TITLE_DATABASEVERSION");
    public static String TITLE_CURRENTDB = (String) CURRENT_LOCALE.getObject("TITLE_CURRENTDB");
    public static String TITLE_CURRENTUSER = (String) CURRENT_LOCALE.getObject("TITLE_CURRENTUSER");
    public static String TITLE_AUTHENTICATEDUSER = (String) CURRENT_LOCALE.getObject("TITLE_AUTHENTICATEDUSER");
    
    /**
     * Panel bottom
     */
    public static String JAVA_TAB_LABEL = (String) CURRENT_LOCALE.getObject("JAVA_TAB_LABEL");
    public static String JAVA_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("JAVA_TAB_TOOLTIP");
    public static String NETWORK_TAB_LABEL = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_LABEL");
    public static String NETWORK_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_TOOLTIP");
    public static String BINARY_TAB_LABEL = (String) CURRENT_LOCALE.getObject("BINARY_TAB_LABEL");
    public static String BINARY_TAB_TOOLTIP  = (String) CURRENT_LOCALE.getObject("BINARY_TAB_TOOLTIP");
    public static String CHUNK_TAB_LABEL = (String) CURRENT_LOCALE.getObject("CHUNK_TAB_LABEL");
    public static String CHUNK_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("CHUNK_TAB_TOOLTIP");
    public static String CONSOLE_TAB_LABEL = (String) CURRENT_LOCALE.getObject("CONSOLE_TAB_LABEL");
    public static String CONSOLE_TAB_TOOLTIP = (String) CURRENT_LOCALE.getObject("CONSOLE_TAB_TOOLTIP");
    
    public static String NETWORK_TAB_HEADERS_LABEL  = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_HEADERS_LABEL");
    public static String NETWORK_TAB_PARAMS_LABEL   = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_PARAMS_LABEL");
    public static String NETWORK_TAB_RESPONSE_LABEL = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_RESPONSE_LABEL");
    public static String NETWORK_TAB_TIMING_LABEL   = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_TIMING_LABEL");
    public static String NETWORK_TAB_SOURCE_LABEL  = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_SOURCE_LABEL");
    public static String NETWORK_TAB_PREVIEW_LABEL  = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_PREVIEW_LABEL");
                                                                                                           
    public static String NETWORK_TAB_METHOD_COLUMN  = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_METHOD_COLUMN");
    public static String NETWORK_TAB_URL_COLUMN     = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_URL_COLUMN");
    public static String NETWORK_TAB_SIZE_COLUMN    = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_SIZE_COLUMN");
    public static String NETWORK_TAB_TYPE_COLUMN    = (String) CURRENT_LOCALE.getObject("NETWORK_TAB_TYPE_COLUMN");
    
    /**
     * Menubar
     */
    public static String ITEM_REPORTISSUE = (String) CURRENT_LOCALE.getObject("ITEM_REPORTISSUE");
    public static String ITEM_UPDATE = (String) CURRENT_LOCALE.getObject("ITEM_UPDATE");
    public static String ITEM_ABOUT = (String) CURRENT_LOCALE.getObject("ITEM_ABOUT");
    public static String MENU_HELP = (String) CURRENT_LOCALE.getObject("MENU_HELP");
    public static String MENU_VIEW = (String) CURRENT_LOCALE.getObject("MENU_VIEW");
    public static String MENU_PANEL = (String) CURRENT_LOCALE.getObject("MENU_PANEL");
    public static String MENU_PREFERENCES = (String) CURRENT_LOCALE.getObject("MENU_PREFERENCES");
    public static String MENU_WINDOWS = (String) CURRENT_LOCALE.getObject("MENU_WINDOWS");
    public static String MENU_EDIT = (String) CURRENT_LOCALE.getObject("MENU_EDIT");
    public static String ITEM_EXIT = (String) CURRENT_LOCALE.getObject("ITEM_EXIT");
    public static String MENU_FILE = (String) CURRENT_LOCALE.getObject("MENU_FILE");
    
    /**
     * Update action
     */
    public static String UPDATE_EXCEPTION = (String) CURRENT_LOCALE.getObject("UPDATE_EXCEPTION");
    public static String UPDATE_NEW_VERSION_AVAILABLE = (String) CURRENT_LOCALE.getObject("UPDATE_NEW_VERSION_AVAILABLE");
    public static String UPDATE_UPTODATE = (String) CURRENT_LOCALE.getObject("UPDATE_UPTODATE");
    public static String UPDATE_LOADING = (String) CURRENT_LOCALE.getObject("UPDATE_LOADING");
    
    /**
     * Webshell
     */
    public static String PRIVILEGE_LABEL = (String) CURRENT_LOCALE.getObject("PRIVILEGE_LABEL");
    public static String PRIVILEGE_TOOLTIP = (String) CURRENT_LOCALE.getObject("PRIVILEGE_TOOLTIP");
    public static String SHELL_URL_LABEL = (String) CURRENT_LOCALE.getObject("SHELL_URL_LABEL");
    public static String SHELL_URL_TOOLTIP = (String) CURRENT_LOCALE.getObject("SHELL_URL_TOOLTIP");
    public static String SHELL_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("SHELL_RUN_BUTTON");
    public static String SHELL_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("SHELL_RUN_BUTTON_TOOLTIP");
    
    /**
     * Upload
     */
    public static String UPLOAD_DIALOG_TEXT = (String) CURRENT_LOCALE.getObject("UPLOAD_DIALOG_TEXT");
    public static String UPLOAD_URL_LABEL = (String) CURRENT_LOCALE.getObject("UPLOAD_URL_LABEL");
    public static String UPLOAD_URL_TOOLTIP = (String) CURRENT_LOCALE.getObject("UPLOAD_URL_TOOLTIP");
    public static String UPLOAD_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("UPLOAD_RUN_BUTTON");
    public static String UPLOAD_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("UPLOAD_RUN_BUTTON_TOOLTIP");
    
    /**
     * SQL shell
     */
    public static String SQL_SHELL_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("SQL_SHELL_RUN_BUTTON");
    public static String SQL_SHELL_USERNAME_LABEL = (String) CURRENT_LOCALE.getObject("SQL_SHELL_USERNAME_LABEL");
    public static String SQL_SHELL_PASSWORD_LABEL = (String) CURRENT_LOCALE.getObject("SQL_SHELL_PASSWORD_LABEL");
    public static String SQL_SHELL_USERNAME_TOOLTIP = (String) CURRENT_LOCALE.getObject("SQL_SHELL_USERNAME_TOOLTIP");
    public static String SQL_SHELL_PASSWORD_TOOLTIP = (String) CURRENT_LOCALE.getObject("SQL_SHELL_PASSWORD_TOOLTIP");
    
    /**
     * File
     */
    public static String FILE_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("FILE_RUN_BUTTON");
    public static String FILE_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("FILE_RUN_BUTTON_TOOLTIP");
    
    /**
     * Coder
     */
    public static String CODER_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("CODER_RUN_BUTTON");
    
    /**
     * Bruteforce
     */
    public static String BRUTEFORCE_HASH = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_HASH");
    public static String BRUTEFORCE_HASH_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_HASH_TOOLTIP");
    public static String BRUTEFORCE_HASH_TYPE_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_HASH_TYPE_TOOLTIP");
    public static String BRUTEFORCE_LCASE_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_LCASE_TOOLTIP");
    public static String BRUTEFORCE_UCASE_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_UCASE_TOOLTIP");
    public static String BRUTEFORCE_NUM_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_NUM_TOOLTIP");
    public static String BRUTEFORCE_SPEC_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_SPEC_TOOLTIP");
    public static String BRUTEFORCE_EXCLUDE_LABEL = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_EXCLUDE_LABEL");
    public static String BRUTEFORCE_EXCLUDE_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_EXCLUDE_TOOLTIP");
    public static String BRUTEFORCE_MIN_LABEL = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_MIN_LABEL");
    public static String BRUTEFORCE_MAX_LABEL = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_MAX_LABEL");
    public static String BRUTEFORCE_MIN_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_MIN_TOOLTIP");
    public static String BRUTEFORCE_MAX_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_MAX_TOOLTIP");
    public static String BRUTEFORCE_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_RUN_BUTTON");
    public static String BRUTEFORCE_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_RUN_BUTTON_TOOLTIP");

    /**
     * Bruteforce action
     */
    public static String BRUTEFORCE_INCORRECT_LENGTH = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_INCORRECT_LENGTH");
    public static String BRUTEFORCE_CHARACTER_RANGE = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_CHARACTER_RANGE");
    public static String BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH");
    public static String BRUTEFORCE_STOP = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_STOP");
    public static String BRUTEFORCE_EMPTY_HASH = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_EMPTY_HASH");
    public static String BRUTEFORCE_CURRENT_STRING = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_CURRENT_STRING");
    public static String BRUTEFORCE_CURRENT_HASH = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_CURRENT_HASH");
    public static String BRUTEFORCE_POSSIBILITIES = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_POSSIBILITIES");
    public static String BRUTEFORCE_CHECKED_HASHES = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_CHECKED_HASHES");
    public static String BRUTEFORCE_ESTIMATED = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_ESTIMATED");
    public static String BRUTEFORCE_PERSECOND = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_PERSECOND");
    public static String BRUTEFORCE_TRAVERSING_REMAINING = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_TRAVERSING_REMAINING");
    public static String BRUTEFORCE_DAYS = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_DAYS");
    public static String BRUTEFORCE_HOURS = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_HOURS");
    public static String BRUTEFORCE_MINUTES = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_MINUTES");
    public static String BRUTEFORCE_SECONDS = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_SECONDS");
    public static String BRUTEFORCE_PERCENT_DONE = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_PERCENT_DONE");
    public static String BRUTEFORCE_ABORTED = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_ABORTED");
    public static String BRUTEFORCE_FOUND_HASH = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_FOUND_HASH");
    public static String BRUTEFORCE_STRING = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_STRING");
    public static String BRUTEFORCE_HASH_NOT_FOUND = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_HASH_NOT_FOUND");
    public static String BRUTEFORCE_START = (String) CURRENT_LOCALE.getObject("BRUTEFORCE_START");

    /**
     * Admin page
     */
    public static String ADMIN_PAGE_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("ADMIN_PAGE_RUN_BUTTON");
    public static String ADMIN_PAGE_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("ADMIN_PAGE_RUN_BUTTON_TOOLTIP");
    
    /**
     * Bulk Test
     */
    public static String SCANLIST_RUN_BUTTON = (String) CURRENT_LOCALE.getObject("SCANLIST_RUN_BUTTON");
    public static String SCANLIST_RUN_BUTTON_TOOLTIP = (String) CURRENT_LOCALE.getObject("SCANLIST_RUN_BUTTON_TOOLTIP");
    
    /**
     * List menu
     */
    public static String IMPORT = (String) CURRENT_LOCALE.getObject("IMPORT");
    public static String EXPORT = (String) CURRENT_LOCALE.getObject("EXPORT");
    public static String CUT = (String) CURRENT_LOCALE.getObject("CUT");
    public static String PASTE = (String) CURRENT_LOCALE.getObject("PASTE");
    public static String DELETE = (String) CURRENT_LOCALE.getObject("DELETE");
    public static String NEW_VALUE = (String) CURRENT_LOCALE.getObject("NEW_VALUE");
    public static String RESTORE_DEFAULT = (String) CURRENT_LOCALE.getObject("RESTORE_DEFAULT");
    
    public static String OK = (String) CURRENT_LOCALE.getObject("OK");
    public static String CANCEL = (String) CURRENT_LOCALE.getObject("CANCEL");
    public static String LIST_ADD_VALUE = (String) CURRENT_LOCALE.getObject("LIST_ADD_VALUE");
    public static String LIST_ADD_VALUE_LABEL = (String) CURRENT_LOCALE.getObject("LIST_ADD_VALUE_LABEL");
    
    public static String LIST_EXPORT = (String) CURRENT_LOCALE.getObject("LIST_EXPORT");
    public static String LIST_CONFIRM_REPLACE = (String) CURRENT_LOCALE.getObject("LIST_CONFIRM_REPLACE");
    public static String LIST_CONFIRM_EXPORT = (String) CURRENT_LOCALE.getObject("LIST_CONFIRM_EXPORT");
    
    public static String REPLACE = (String) CURRENT_LOCALE.getObject("REPLACE");
    public static String ADD = (String) CURRENT_LOCALE.getObject("ADD");
    public static String LIST_IMPORT_REPLACE = (String) CURRENT_LOCALE.getObject("LIST_IMPORT_REPLACE");
    public static String LIST_IMPORT = (String) CURRENT_LOCALE.getObject("LIST_IMPORT");
    public static String LIST_IMPORT_ERROR = (String) CURRENT_LOCALE.getObject("LIST_IMPORT_ERROR");
    public static String LIST_IMPORT_ERROR_TEXT = (String) CURRENT_LOCALE.getObject("LIST_IMPORT_ERROR_TEXT");
    
    /**
     * Dialog replace file
     */
    public static String DIALOG_REPLACE_FILE_CONFIRM = (String) CURRENT_LOCALE.getObject("DIALOG_REPLACE_FILE_CONFIRM");
    public static String DIALOG_REPLACE_FILE_TITLE = (String) CURRENT_LOCALE.getObject("DIALOG_REPLACE_FILE_TITLE");
    
    public static Map<String, List<Object>> components = new HashMap<>();
    
    static {
        Class<?> cl = I18n.class;
        Field[] ct = cl.getFields();
        for (Field f: ct) {
            components.put(f.getName(), new ArrayList<>());
        }
    }
    
    private I18n() {
        // Disable constructor
    }
}

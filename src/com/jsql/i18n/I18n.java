package com.jsql.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private I18n() {
        // Disable constructor
    }
    
    static {
        Locale.setDefault(Locale.FRENCH);
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
}

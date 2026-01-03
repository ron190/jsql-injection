package com.jsql.util;

import org.apache.logging.log4j.Level;

import java.awt.*;

public class LogLevelUtil {

    // Used by model for vuln report and by view for text colors
    public static final Color COLOR_RED = new Color(0xFF6B68);
    public static final Color COLOR_BLU = new Color(0x5394EC);
    public static final Color COLOR_GREEN = new Color(0x299999);
    public static final Color COLOR_GRAY = new Color(0x555555);

    // ERROR 200
    public static final Level CONSOLE_JAVA = Level.forName("CONSOLE_JAVA", 202);
    public static final Level CONSOLE_ERROR = Level.forName("CONSOLE_ERROR", 201);
    
    // INFO 400
    public static final Level CONSOLE_INFORM = Level.forName("CONSOLE_INFORM", 403);
    public static final Level CONSOLE_SUCCESS = Level.forName("CONSOLE_SUCCESS", 402);
    public static final Level CONSOLE_DEFAULT = Level.forName("CONSOLE_DEFAULT", 401);
    
    // TRACE 600
    public static final Level IGNORE = Level.forName("IGNORE", 601);
    
    private LogLevelUtil() {
        // Utility class
    }
}

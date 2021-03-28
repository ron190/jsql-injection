package com.jsql.util;

import org.apache.logging.log4j.Level;

public class LogLevel {

    // ERROR 200
    public static final Level CONSOLE_JAVA = Level.forName("CONSOLE_JAVA", 202);
    public static final Level CONSOLE_ERROR = Level.forName("CONSOLE_ERROR", 201);
    
    // INFO 400
    public static final Level CONSOLE_INFORM = Level.forName("CONSOLE_INFORM", 403);
    public static final Level CONSOLE_SUCCESS = Level.forName("CONSOLE_SUCCESS", 402);
    public static final Level CONSOLE_DEFAULT = Level.forName("CONSOLE_DEFAULT", 401);
    
    // TRACE 600
    public static final Level IGNORE = Level.forName("IGNORE", 601);
    
    private LogLevel() {
        // Utility class
    }
}

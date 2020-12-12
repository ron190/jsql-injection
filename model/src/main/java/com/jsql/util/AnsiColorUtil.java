package com.jsql.util;

public class AnsiColorUtil {
    
    private static final String PREFIX = "\u001b[";
    
    private static final int NORMAL = 0;
    
    private static final char SEPARATOR = ';';
    
    private static final int FOREGROUND_RED = 31;
    private static final int FOREGROUND_GREEN = 32;
    
    private static final String SUFFIX = "m";
    
    private static final String END_COLOUR = PREFIX + SUFFIX;

    private static final String ERROR_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_RED + SUFFIX;
    private static final String INFO_COLOUR = PREFIX + NORMAL+ SEPARATOR + FOREGROUND_GREEN + SUFFIX;

    private AnsiColorUtil() {
        // Util
    }

    public static String addGreenColor(String text) {
        
        return INFO_COLOUR + text + END_COLOUR;
    }
    
    public static String addRedColor(String text) {
        
        return ERROR_COLOUR + text + END_COLOUR;
    }
}

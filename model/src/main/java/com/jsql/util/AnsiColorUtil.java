package com.jsql.util;

public class AnsiColorUtil {
    
    private static final String PREFIX = "\u001b[";
    
    private static final int NORMAL = 0;
    
    private static final char SEPARATOR = ';';
    
    private static final int FOREGROUND_RED = 31;
    private static final int FOREGROUND_GREEN = 32;
    
    private static final String SUFFIX = "m";
    
    private static final String END_COLOR = AnsiColorUtil.PREFIX + AnsiColorUtil.SUFFIX;

    private static final String ERROR_COLOR = AnsiColorUtil.PREFIX + AnsiColorUtil.NORMAL + AnsiColorUtil.SEPARATOR + AnsiColorUtil.FOREGROUND_RED + AnsiColorUtil.SUFFIX;
    private static final String INFO_COLOR = AnsiColorUtil.PREFIX + AnsiColorUtil.NORMAL + AnsiColorUtil.SEPARATOR + AnsiColorUtil.FOREGROUND_GREEN + AnsiColorUtil.SUFFIX;

    private AnsiColorUtil() {
        // Utility class
    }

    public static String addGreenColor(String text) {
        return AnsiColorUtil.INFO_COLOR + text + AnsiColorUtil.END_COLOR;
    }
    
    public static String addRedColor(String text) {
        return AnsiColorUtil.ERROR_COLOR + text + AnsiColorUtil.END_COLOR;
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import org.apache.log4j.Logger;

/**
 * Action ordered by the Model and applied to the View.
 */
@FunctionalInterface
public interface InteractionCommand {
    
    /**
     * Log4j logger sent to view.
     */
    Logger LOGGER = Logger.getRootLogger();
 
    int NORMAL = 0;
    int BRIGHT = 1;
    String FOREGROUND = "38;2";
    String BACKGROUND = "48;2";
    int FOREGROUND_BLACK = 30;
    int FOREGROUND_RED = 31;
    int FOREGROUND_GREEN = 32;
    int FOREGROUND_YELLOW = 33;
    int FOREGROUND_BLUE = 34;
    int FOREGROUND_MAGENTA = 35;
    int FOREGROUND_CYAN = 36;
    int FOREGROUND_WHITE = 37;
     
    String PREFIX = "\u001b[";
    String SUFFIX = "m";
    char SEPARATOR = ';';
    String END_COLOUR = PREFIX + SUFFIX;
     
    String FATAL_COLOUR = PREFIX + BRIGHT + SEPARATOR + FOREGROUND_RED + SUFFIX;
    String ERROR_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_RED + SUFFIX;
    String WARN_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_YELLOW + SUFFIX;
    String INFO_COLOUR = PREFIX + NORMAL+ SEPARATOR + FOREGROUND_GREEN + SUFFIX;
    String DEBUG_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_CYAN + SUFFIX;
    String TRACE_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_BLUE + SUFFIX;

    static String addGreenColor(String text) {
        return INFO_COLOUR + text + END_COLOUR;
    }
    
    static String addRedColor(String text) {
        return ERROR_COLOUR + text + END_COLOUR;
    }
    
    /**
     * Do the action ordered by the model.
     */
    void execute();
    
}

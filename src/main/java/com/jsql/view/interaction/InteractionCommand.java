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
    static final Logger LOGGER = Logger.getRootLogger();  
 
    static final int NORMAL = 0;
    static final int BRIGHT = 1;
    static final String FOREGROUND = "38;2";
    static final String BACKGROUND = "48;2";
    static final int FOREGROUND_BLACK = 30;
    static final int FOREGROUND_RED = 31;
    static final int FOREGROUND_GREEN = 32;
    static final int FOREGROUND_YELLOW = 33;
    static final int FOREGROUND_BLUE = 34;
    static final int FOREGROUND_MAGENTA = 35;
    static final int FOREGROUND_CYAN = 36;
    static final int FOREGROUND_WHITE = 37; 
    
    static final String PREFIX = "\u001b[";
    static final String SUFFIX = "m";
    static final char SEPARATOR = ';';
    static final String END_COLOUR = PREFIX + SUFFIX;   
 
    static final String FATAL_COLOUR = PREFIX + BRIGHT + SEPARATOR + FOREGROUND_RED + SUFFIX;
    static final String ERROR_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_RED + SUFFIX;
    static final String WARN_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_YELLOW + SUFFIX;
    static final String INFO_COLOUR = PREFIX + NORMAL+ SEPARATOR + FOREGROUND_GREEN + SUFFIX;
    static final String DEBUG_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_CYAN + SUFFIX;
    static final String TRACE_COLOUR = PREFIX + NORMAL + SEPARATOR + FOREGROUND_BLUE + SUFFIX;   
    
    static String addColor(String text) {
        return INFO_COLOUR + text + END_COLOUR;
    }
    
    static String addColor2(String text) {
        return PREFIX + FOREGROUND +"132;172;221"+ SEPARATOR + FOREGROUND_BLACK + SUFFIX + text + END_COLOUR;
    }
 
    /**
     * Do the action ordered by the model.
     */
    void execute();
    
}

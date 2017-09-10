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
	
    /**
     * Do the action ordered by the model.
     */
    void execute();
    
}

/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.scan.interaction;

import org.apache.log4j.Logger;

import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;

/**
 * Mark the injection as vulnerable to a time based injection.
 */
public class MarkTimebasedVulnerable implements IInteractionCommand {
    public static final Logger LOGGER = Logger.getLogger(MarkTimebasedVulnerable.class);

    /**
     * @param nullParam
     */
    public MarkTimebasedVulnerable(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.debug("Vulnerable to Time based injection.");
    }
}

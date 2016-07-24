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
package com.jsql.view.scan.interaction;

import org.apache.log4j.Logger;

import com.jsql.view.swing.MediatorGui;

/**
 * End the refreshing of administration page search button.
 */
public class EndScanList implements InteractionCommand {
    
    private static final Logger LOGGER = Logger.getLogger(EndScanList.class);
    
    /**
     * @param interactionParams
     */
    public EndScanList(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGui.tabManagers().scanListManager.restoreButtonText();
        MediatorGui.tabManagers().scanListManager.setButtonEnable(true);
        MediatorGui.tabManagers().scanListManager.hideLoader();
        
        LOGGER.trace("Scan finished");
    }
}

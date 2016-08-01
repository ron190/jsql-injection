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
package com.jsql.view.swing.interaction;

import com.jsql.view.swing.MediatorGui;

/**
 * End the refreshing of administration page search button.
 */
public class EndScanList implements InteractionCommand {
    /**
     * @param interactionParams
     */
    public EndScanList(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGui.managerScan().restoreButtonText();
        MediatorGui.managerScan().setButtonEnable(true);
        MediatorGui.managerScan().hideLoader();
    }
}

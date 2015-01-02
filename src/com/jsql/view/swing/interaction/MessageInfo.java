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
package com.jsql.view.swing.interaction;

import com.jsql.view.swing.MediatorGUI;

/**
 * Update the general information in status bar.
 */
public class MessageInfo implements IInteractionCommand {
    /**
     * @param nullParam
     */
    public MessageInfo(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGUI.status().setInfos(
            MediatorGUI.model().versionDatabase,
            MediatorGUI.model().currentDatabase,
            MediatorGUI.model().currentUser,
            MediatorGUI.model().authenticatedUser
        );
    }
}

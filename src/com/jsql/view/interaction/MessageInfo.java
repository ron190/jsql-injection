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
package com.jsql.view.interaction;

import com.jsql.view.GUIMediator;

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
        GUIMediator.status().setInfos(
            GUIMediator.model().versionDatabase,
            GUIMediator.model().currentDatabase,
            GUIMediator.model().currentUser,
            GUIMediator.model().authenticatedUser
        );
    }
}

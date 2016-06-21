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

import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as using a user profile invulnerable to file I/O.
 */
public class MarkFileSystemInvulnerable implements InteractionCommand {
    /**
     * @param interactionParams
     */
    public MarkFileSystemInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGui.tabManagers().fileManager.changePrivilegeIcon(HelperGui.SQUARE_RED);
        MediatorGui.tabManagers().fileManager.setButtonEnable(true);
        MediatorGui.tabManagers().fileManager.restoreButtonText();
        MediatorGui.tabManagers().fileManager.hideLoader();
        
        MediatorGui.tabManagers().shellManager.changePrivilegeIcon(HelperGui.SQUARE_RED);
        MediatorGui.tabManagers().shellManager.setButtonEnable(true);
        MediatorGui.tabManagers().shellManager.restoreButtonText();
        
        MediatorGui.tabManagers().uploadManager.changePrivilegeIcon(HelperGui.SQUARE_RED);
        MediatorGui.tabManagers().uploadManager.setButtonEnable(true);
        MediatorGui.tabManagers().uploadManager.restoreButtonText();
        
        MediatorGui.tabManagers().sqlShellManager.changePrivilegeIcon(HelperGui.SQUARE_RED);
        MediatorGui.tabManagers().sqlShellManager.setButtonEnable(true);
        MediatorGui.tabManagers().sqlShellManager.restoreButtonText();
    }
}

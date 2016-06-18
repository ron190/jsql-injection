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

import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;

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
        MediatorGUI.tabManagers().fileManager.changePrivilegeIcon(HelperGUI.SQUARE_RED);
        MediatorGUI.tabManagers().fileManager.setButtonEnable(true);
        MediatorGUI.tabManagers().fileManager.restoreButtonText();
        MediatorGUI.tabManagers().fileManager.hideLoader();
        
        MediatorGUI.tabManagers().shellManager.changePrivilegeIcon(HelperGUI.SQUARE_RED);
        MediatorGUI.tabManagers().shellManager.setButtonEnable(true);
        MediatorGUI.tabManagers().shellManager.restoreButtonText();
        
        MediatorGUI.tabManagers().uploadManager.changePrivilegeIcon(HelperGUI.SQUARE_RED);
        MediatorGUI.tabManagers().uploadManager.setButtonEnable(true);
        MediatorGUI.tabManagers().uploadManager.restoreButtonText();
        
        MediatorGUI.tabManagers().sqlShellManager.changePrivilegeIcon(HelperGUI.SQUARE_RED);
        MediatorGUI.tabManagers().sqlShellManager.setButtonEnable(true);
        MediatorGUI.tabManagers().sqlShellManager.restoreButtonText();
    }
}

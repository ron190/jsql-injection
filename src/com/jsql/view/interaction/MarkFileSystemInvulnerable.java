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

import com.jsql.view.MediatorGUI;
import com.jsql.view.ToolsGUI;

/**
 * Mark the injection as using a user profile invulnerable to file I/O.
 */
public class MarkFileSystemInvulnerable implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public MarkFileSystemInvulnerable(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGUI.left().fileManager.changePrivilegeIcon(ToolsGUI.SQUARE_RED);
        MediatorGUI.left().fileManager.setButtonEnable(true);
        MediatorGUI.left().fileManager.restoreButtonText();
        MediatorGUI.left().fileManager.hideLoader();
        
        MediatorGUI.left().shellManager.changePrivilegeIcon(ToolsGUI.SQUARE_RED);
        MediatorGUI.left().shellManager.setButtonEnable(true);
        MediatorGUI.left().shellManager.restoreButtonText();
        
        MediatorGUI.left().uploadManager.changePrivilegeIcon(ToolsGUI.SQUARE_RED);
        MediatorGUI.left().uploadManager.setButtonEnable(true);
        MediatorGUI.left().uploadManager.restoreButtonText();
        
        MediatorGUI.left().sqlShellManager.changePrivilegeIcon(ToolsGUI.SQUARE_RED);
        MediatorGUI.left().sqlShellManager.setButtonEnable(true);
        MediatorGUI.left().sqlShellManager.restoreButtonText();
    }
}

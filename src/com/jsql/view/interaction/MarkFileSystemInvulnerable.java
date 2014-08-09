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
import com.jsql.view.GUITools;

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
        GUIMediator.left().fileManager.changePrivilegeIcon(GUITools.SQUARE_RED);
        GUIMediator.left().fileManager.setButtonEnable(true);
        GUIMediator.left().fileManager.restoreButtonText();
        GUIMediator.left().fileManager.hideLoader();
        
        GUIMediator.left().shellManager.changePrivilegeIcon(GUITools.SQUARE_RED);
        GUIMediator.left().shellManager.setButtonEnable(true);
        GUIMediator.left().shellManager.restoreButtonText();
        
        GUIMediator.left().uploadManager.changePrivilegeIcon(GUITools.SQUARE_RED);
        GUIMediator.left().uploadManager.setButtonEnable(true);
        GUIMediator.left().uploadManager.restoreButtonText();
        
        GUIMediator.left().sqlShellManager.changePrivilegeIcon(GUITools.SQUARE_RED);
        GUIMediator.left().sqlShellManager.setButtonEnable(true);
        GUIMediator.left().sqlShellManager.restoreButtonText();
    }
}

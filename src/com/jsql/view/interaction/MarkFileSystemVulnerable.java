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
 * Mark the injection as using a user profile vulnerable to file I/O.
 */
public class MarkFileSystemVulnerable implements IInteractionCommand {
    /**
     * @param nullParam
     */
    public MarkFileSystemVulnerable(Object[] nullParam) {
        // Do nothing
    }

    public void execute() {
        GUIMediator.left().fileManager.changePrivilegeIcon(GUITools.TICK);
        GUIMediator.left().shellManager.changePrivilegeIcon(GUITools.TICK);
        GUIMediator.left().sqlShellManager.changePrivilegeIcon(GUITools.TICK);
        GUIMediator.left().uploadManager.changePrivilegeIcon(GUITools.TICK);
    }
}

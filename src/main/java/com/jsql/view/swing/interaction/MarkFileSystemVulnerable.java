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
 * Mark the injection as using a user profile vulnerable to file I/O.
 */
public class MarkFileSystemVulnerable implements InteractionCommand {
    /**
     * @param nullParam
     */
    public MarkFileSystemVulnerable(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGui.tabManagers().fileManager.changePrivilegeIcon(HelperGui.TICK);
        MediatorGui.tabManagers().shellManager.changePrivilegeIcon(HelperGui.TICK);
        MediatorGui.tabManagers().sqlShellManager.changePrivilegeIcon(HelperGui.TICK);
        MediatorGui.tabManagers().uploadManager.changePrivilegeIcon(HelperGui.TICK);
    }
}

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
        MediatorGUI.tabManagers().fileManager.changePrivilegeIcon(HelperGUI.TICK);
        MediatorGUI.tabManagers().shellManager.changePrivilegeIcon(HelperGUI.TICK);
        MediatorGUI.tabManagers().sqlShellManager.changePrivilegeIcon(HelperGUI.TICK);
        MediatorGUI.tabManagers().uploadManager.changePrivilegeIcon(HelperGUI.TICK);
    }
}

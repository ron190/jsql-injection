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

import com.jsql.view.swing.HelperUi;
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
        MediatorGui.managerFile().changePrivilegeIcon(HelperUi.TICK);
        MediatorGui.managerWebshell().changePrivilegeIcon(HelperUi.TICK);
        MediatorGui.managerSqlshell().changePrivilegeIcon(HelperUi.TICK);
        MediatorGui.managerUpload().changePrivilegeIcon(HelperUi.TICK);
    }
}

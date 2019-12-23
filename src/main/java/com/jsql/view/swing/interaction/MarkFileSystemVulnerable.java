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

import com.jsql.view.interaction.InteractionCommand;
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
        if (MediatorGui.managerFile() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.managerFile() in "+ this.getClass());
        }
        
        MediatorGui.managerFile().changePrivilegeIcon(HelperUi.ICON_TICK);
        MediatorGui.managerWebshell().changePrivilegeIcon(HelperUi.ICON_TICK);
        MediatorGui.managerSqlshell().changePrivilegeIcon(HelperUi.ICON_TICK);
        MediatorGui.managerUpload().changePrivilegeIcon(HelperUi.ICON_TICK);
    }
    
}

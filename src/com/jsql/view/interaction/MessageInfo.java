/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import com.jsql.view.GUI;

/**
 * Update the database general information in status bar
 */
public class MessageInfo implements Interaction{
    // The main View
    private GUI gui;

    /**
     * @param mainGUI
     * @param nullParam
     */
    public MessageInfo(GUI mainGUI, Object[] nullParam){
        gui = mainGUI;
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        gui.getStatusPanel().setInfos(gui.model.versionDB, gui.model.currentDB, gui.model.currentUser, gui.model.authenticatedUser);
    }
}

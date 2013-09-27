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
import com.jsql.view.GUITools;

/**
 * Mark the injection as using a user profile invulnerable to file I/O
 */
public class MarkFileSystemInvulnerable implements Interaction{
    // The main View
    private GUI gui;

    /**
     * @param mainGUI
     * @param interactionParams
     */
    public MarkFileSystemInvulnerable(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        gui.getOutputPanel().fileManager.changeIcon(GUITools.SQUARE_RED);
        gui.getOutputPanel().fileManager.setButtonEnable(true);
        gui.getOutputPanel().fileManager.restoreButtonText();
        gui.getOutputPanel().fileManager.hideLoader();

        gui.getOutputPanel().shellManager.changeIcon(GUITools.SQUARE_RED);
        gui.getOutputPanel().shellManager.setButtonEnable(true);
        gui.getOutputPanel().shellManager.restoreButtonText();
        
        gui.getOutputPanel().uploadManager.changeIcon(GUITools.SQUARE_RED);
        gui.getOutputPanel().uploadManager.setButtonEnable(true);
        gui.getOutputPanel().uploadManager.restoreButtonText();
        
        gui.getOutputPanel().sqlShellManager.changeIcon(GUITools.SQUARE_RED);
        gui.getOutputPanel().sqlShellManager.setButtonEnable(true);
        gui.getOutputPanel().sqlShellManager.restoreButtonText();
    }
}

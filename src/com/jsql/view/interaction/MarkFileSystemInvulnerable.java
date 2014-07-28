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

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;

/**
 * Mark the injection as using a user profile invulnerable to file I/O
 */
public class MarkFileSystemInvulnerable implements IInteractionCommand{
    /**
     * @param interactionParams
     */
    public MarkFileSystemInvulnerable(Object[] interactionParams){
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        GUIMediator.gui().getOutputPanel().fileManager.changeIcon(GUITools.SQUARE_RED);
        GUIMediator.gui().getOutputPanel().fileManager.setButtonEnable(true);
        GUIMediator.gui().getOutputPanel().fileManager.restoreButtonText();
        GUIMediator.gui().getOutputPanel().fileManager.hideLoader();
        
        GUIMediator.gui().getOutputPanel().shellManager.changeIcon(GUITools.SQUARE_RED);
        GUIMediator.gui().getOutputPanel().shellManager.setButtonEnable(true);
        GUIMediator.gui().getOutputPanel().shellManager.restoreButtonText();
        
        GUIMediator.gui().getOutputPanel().uploadManager.changeIcon(GUITools.SQUARE_RED);
        GUIMediator.gui().getOutputPanel().uploadManager.setButtonEnable(true);
        GUIMediator.gui().getOutputPanel().uploadManager.restoreButtonText();
        
        GUIMediator.gui().getOutputPanel().sqlShellManager.changeIcon(GUITools.SQUARE_RED);
        GUIMediator.gui().getOutputPanel().sqlShellManager.setButtonEnable(true);
        GUIMediator.gui().getOutputPanel().sqlShellManager.restoreButtonText();
    }
}

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

/**
 * End the refreshing of File search button
 */
public class EndFileSearch implements IInteractionCommand{
    /**
     * @param interactionParams
     */
    public EndFileSearch(Object[] interactionParams){
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        GUIMediator.gui().getOutputPanel().fileManager.restoreButtonText();
        GUIMediator.gui().getOutputPanel().fileManager.setButtonEnable(true);
        GUIMediator.gui().getOutputPanel().fileManager.hideLoader();
    }
}

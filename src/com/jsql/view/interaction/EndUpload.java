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

/**
 * End the refreshing of File search button
 */
public class EndUpload implements IInteractionCommand{
    /**
     * @param interactionParams
     */
    public EndUpload(Object[] interactionParams){
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        GUIMediator.left().uploadManager.restoreButtonText();
        GUIMediator.left().uploadManager.setButtonEnable(true);
        GUIMediator.left().uploadManager.hideLoader();
    }
}

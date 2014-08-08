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
 * End the refreshing of the main Start injection button.
 */
public class EndPreparation implements IInteractionCommand {
    /**
     * @param interactionParams
     */
    public EndPreparation(Object[] interactionParams) {
        // Do nothing
    }

    public void execute() {
        GUIMediator.top().submitAddressBar.setInjectionReady();
        GUIMediator.top().loader.setVisible(false);

        if (GUIMediator.model().isInjectionBuilt) {
            GUIMediator.left().fileManager.setButtonEnable(true);
            GUIMediator.left().shellManager.setButtonEnable(true);
            GUIMediator.left().sqlShellManager.setButtonEnable(true);
            GUIMediator.left().uploadManager.setButtonEnable(true);
        }
    }
}

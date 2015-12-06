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

import com.jsql.model.injection.MediatorModel;
import com.jsql.view.swing.MediatorGUI;

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

    @Override
    public void execute() {
        MediatorGUI.top().submitAddressBar.setInjectionReady();
        MediatorGUI.top().loader.setVisible(false);

        if (MediatorModel.model().isInjectionBuilt) {
            MediatorGUI.left().fileManager.setButtonEnable(true);
            MediatorGUI.left().shellManager.setButtonEnable(true);
            MediatorGUI.left().sqlShellManager.setButtonEnable(true);
            MediatorGUI.left().uploadManager.setButtonEnable(true);
        }
    }
}

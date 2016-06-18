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
public class EndPreparation implements InteractionCommand {
    /**
     * @param interactionParams
     */
    public EndPreparation(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        MediatorGUI.panelAddress().buttonAddressBar.setInjectionReady();
        MediatorGUI.panelAddress().loader.setVisible(false);

        if (MediatorModel.model().isInjectionBuilt) {
            MediatorGUI.tabManagers().fileManager.setButtonEnable(true);
            MediatorGUI.tabManagers().shellManager.setButtonEnable(true);
            MediatorGUI.tabManagers().sqlShellManager.setButtonEnable(true);
            MediatorGUI.tabManagers().uploadManager.setButtonEnable(true);
        }
    }
}

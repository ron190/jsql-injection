/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import javax.swing.JMenu;

import com.jsql.model.MediatorModel;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as invulnerable to a error based injection.
 */
public class MarkErrorStrategy implements InteractionCommand {
    
    /**
     * @param interactionParams
     */
    public MarkErrorStrategy(Object[] interactionParams) {
        // Do nothing
    }

    @Override
    public void execute() {
        if (MediatorGui.panelAddressBar() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.panelAddressBar() in "+ this.getClass());
        }
        
        MediatorGui.panelAddressBar().getMenuStrategy().setText(MediatorModel.model().getMediatorStrategy().getError().toString());
        
        JMenu menuError = (JMenu) MediatorGui.panelAddressBar().getMenuStrategy().getMenuComponent(2);
        int indexError = MediatorModel.model().getMediatorStrategy().getError().getIndexMethodError();
        String nameError = MediatorModel.model().getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getError().getMethod().get(indexError).getName();
        
        for (int i = 0 ; i < menuError.getItemCount() ; i++) {
            // Fix #44635: ArrayIndexOutOfBoundsException on getItem()
            try {
                if (menuError.getItem(i).getText().equals(nameError)) {
                    menuError.getItem(i).setSelected(true);
                    MediatorGui.panelAddressBar().getMenuStrategy().setText(nameError);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOGGER.error(e, e);
            }
        }
    }
    
}

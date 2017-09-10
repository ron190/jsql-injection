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

import javax.swing.JMenu;

import com.jsql.model.MediatorModel;
import com.jsql.model.injection.strategy.StrategyInjection;
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
        if (MediatorGui.managerDatabase() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.managerDatabase() in "+ this.getClass());
        }
        
        MediatorGui.managerDatabase().getMenuStrategy().setText(StrategyInjection.ERROR.toString());
        
        JMenu menuError = (JMenu) MediatorGui.managerDatabase().getMenuStrategy().getMenuComponent(2);
        int indexError = StrategyInjection.ERROR.instance().getIndexMethod();
        String nameError = MediatorModel.model().getVendor().instance().getXmlModel().getStrategy().getError().getMethod().get(indexError).getName();
        
        for (int i = 0 ; i < MediatorGui.managerDatabase().getMenuStrategy().getItemCount() ; i++) {
            // Fix #44635: ArrayIndexOutOfBoundsException on getItem()
            try {
                if (menuError.getItem(i).getText().equals(nameError)) {
                    menuError.getItem(i).setSelected(true);
                    MediatorGui.managerDatabase().getMenuStrategy().setText(nameError);
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOGGER.error(e, e);
            }
        }
    }
    
}

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
        MediatorGui.managerDatabase().getPanelStrategy().setText(StrategyInjection.ERROR.toString());
        for (int i = 0 ; i < MediatorGui.managerDatabase().getPanelStrategy().getItemCount() ; i++) {
            if (((JMenu) MediatorGui.managerDatabase().getPanelStrategy().getMenuComponent(2)).getItem(i).getText().equals(
                MediatorModel.model().getVendor().instance().getXmlModel().getStrategy().getError().getMethod().get(StrategyInjection.ERROR.instance().getIndexMethod()).getName()
            )) {
                ((JMenu) MediatorGui.managerDatabase().getPanelStrategy().getMenuComponent(2)).getItem(i).setSelected(true);
                MediatorGui.managerDatabase().getPanelStrategy().setText(
                    MediatorModel.model().getVendor().instance().getXmlModel().getStrategy().getError().getMethod().get(StrategyInjection.ERROR.instance().getIndexMethod()).getName()
                );
                break;
            }
        }
    }
    
}

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

import java.util.Map;

import javax.swing.JMenu;

import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as vulnerable to a error-based injection.
 */
public class MarkErrorVulnerable implements InteractionCommand {
    
    private Map<TypeHeader, Object> mapHeader;
    private int indexMethodError;
	
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public MarkErrorVulnerable(Object[] interactionParams) {
        this.mapHeader = (Map<TypeHeader, Object>) interactionParams[0];
        this.indexMethodError = (int) this.mapHeader.get(TypeHeader.SOURCE);
    }

    @Override
    public void execute() {
        for (int i = 0 ; i < MediatorGui.managerDatabase().getPanelStrategy().getItemCount() ; i++) {
            if (MediatorGui.managerDatabase().getPanelStrategy().getItem(i).getText().equals(StrategyInjection.ERROR.toString())) {
                MediatorGui.managerDatabase().getPanelStrategy().getItem(i).setEnabled(true);
                ((JMenu) MediatorGui.managerDatabase().getPanelStrategy().getItem(i)).getItem(this.indexMethodError).setEnabled(true);
                break;
            }
        }
    }
    
}

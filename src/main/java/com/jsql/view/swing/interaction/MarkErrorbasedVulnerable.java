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
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as vulnerable to a error-based injection.
 */
public class MarkErrorbasedVulnerable implements InteractionCommand {
    
    private Map<TypeHeader, Object> mapHeader;
    private int indexMethodError;
	
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public MarkErrorbasedVulnerable(Object[] interactionParams) {
        this.mapHeader = (Map<TypeHeader, Object>) interactionParams[0];
        this.indexMethodError = (int) mapHeader.get(TypeHeader.SOURCE);
    }

    @Override
    public void execute() {
        for (int i = 0 ; i < MediatorGui.managerDatabase().panelStrategy.getItemCount() ; i++) {
            if (MediatorGui.managerDatabase().panelStrategy.getItem(i).getText().equals(Strategy.ERRORBASED.toString())) {
                MediatorGui.managerDatabase().panelStrategy.getItem(i).setEnabled(true);
                ((JMenu) MediatorGui.managerDatabase().panelStrategy.getItem(i)).getItem(indexMethodError).setEnabled(true);
                break;
            }
        }
    }
    
}

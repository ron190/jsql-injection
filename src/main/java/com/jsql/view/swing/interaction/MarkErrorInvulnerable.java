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

import org.apache.log4j.Logger;

import com.jsql.model.bean.util.Header;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;

/**
 * Mark the injection as invulnerable to a error based injection.
 */
public class MarkErrorInvulnerable implements InteractionCommand {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
	
    private Map<Header, Object> mapHeader;
    private int indexMethodError;
    
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public MarkErrorInvulnerable(Object[] interactionParams) {
        this.mapHeader = (Map<Header, Object>) interactionParams[0];
        this.indexMethodError = (int) this.mapHeader.get(Header.SOURCE);
    }

    @Override
    public void execute() {
        // Fix #36975: ArrayIndexOutOfBoundsException on getItem()
        // Fix #40352: NullPointerException on ?
        try {
            for (int i = 0 ; i < MediatorGui.managerDatabase().getMenuStrategy().getItemCount() ; i++) {
                if (MediatorGui.managerDatabase().getMenuStrategy().getItem(i).getText().equals(StrategyInjection.ERROR.toString())) {
                    ((JMenu) MediatorGui.managerDatabase().getMenuStrategy().getItem(i)).getItem(this.indexMethodError).setEnabled(false);
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            LOGGER.error(e, e);
        }
    }
    
}

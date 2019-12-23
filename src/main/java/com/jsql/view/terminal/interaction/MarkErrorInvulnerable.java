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
package com.jsql.view.terminal.interaction;

import java.util.Map;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.view.interaction.InteractionCommand;

/**
 * Mark the injection as invulnerable to a error based injection.
 */
public class MarkErrorInvulnerable implements InteractionCommand {
    
    private Map<Header, Object> mapHeader;
    private int indexMethodError;
    private InjectionModel injectionModel;
    
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public MarkErrorInvulnerable(Object[] interactionParams) {
        this.mapHeader = (Map<Header, Object>) interactionParams[0];
        this.indexMethodError = (int) this.mapHeader.get(Header.SOURCE);
        this.injectionModel = (InjectionModel) this.mapHeader.get(Header.INJECTION_MODEL);
    }

    @Override
    public void execute() {
        LOGGER.info(InteractionCommand.addRedColor(this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getError().getMethod().get(this.indexMethodError).getName()));
    }
    
}

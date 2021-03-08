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
package com.jsql.view.scan.interaction;

import java.util.Map;

import com.jsql.model.bean.util.Header;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Mark the injection as vulnerable to a blind injection.
 */
public class MarkBlindVulnerable implements InteractionCommand {

    private String url;
    
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public MarkBlindVulnerable(Object[] interactionParams) {

        Map<Header, Object> params = (Map<Header, Object>) interactionParams[0];
        this.url = (String) params.get(Header.URL);
    }

    @Override
    public void execute() {
        
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getBlind();
        
        MediatorHelper.managerScan().highlight(this.url, strategy.toString());
    }
}

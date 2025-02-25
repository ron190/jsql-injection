/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import com.jsql.model.bean.util.Header;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

import java.util.Map;

/**
 * Mark the injection as vulnerable to an error-based injection.
 */
public class MarkErrorVulnerable implements InteractionCommand {
    
    private final int indexMethodError;
    
    @SuppressWarnings("unchecked")
    public MarkErrorVulnerable(Object[] interactionParams) {
        Map<Header, Object> mapHeader = (Map<Header, Object>) interactionParams[0];
        this.indexMethodError = (int) mapHeader.get(Header.INDEX_ERROR_STRATEGY);
    }

    @Override
    public void execute() {
        MediatorHelper.panelAddressBar().getPanelTrailingAddress().markErrorVulnerable(this.indexMethodError);
    }
}

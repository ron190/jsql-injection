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
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

import java.util.Map;

/**
 * Mark the injection as vulnerable to a blind injection.
 */
public class SetVendor implements InteractionCommand {

    private final Vendor vendor;
    
    @SuppressWarnings("unchecked")
    public SetVendor(Object[] interactionParams) {
        Map<Header, Object> params = (Map<Header, Object>) interactionParams[0];
        this.vendor = (Vendor) params.get(Header.VENDOR);
    }

    @Override
    public void execute() {
        MediatorHelper.panelAddressBar().getPanelTrailingAddress().setVendor(this.vendor);
    }
}

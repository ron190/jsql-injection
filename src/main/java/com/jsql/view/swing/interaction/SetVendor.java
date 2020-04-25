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

import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Mark the injection as vulnerable to a blind injection.
 */
public class SetVendor implements InteractionCommand {
    
    private Vendor vendor;
    
    /**
     * @param interactionParams
     */
    public SetVendor(Object[] interactionParams) {
        
        this.vendor = (Vendor) interactionParams[0];
    }

    @Override
    public void execute() {
        
        MediatorHelper.panelAddressBar().getAddressMenuBar().setVendor(this.vendor);
    }
}

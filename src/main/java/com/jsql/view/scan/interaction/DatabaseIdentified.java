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
package com.jsql.view.scan.interaction;

import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.ListItem;

/**
 * Mark the injection as vulnerable to a blind injection.
 */
public class DatabaseIdentified implements InteractionCommand {

    private String url;
    
    private Vendor vendor;
    
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public DatabaseIdentified(Object[] interactionParams) {
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        this.url = (String) params.get(TypeHeader.URL);
        this.vendor = (Vendor) params.get(TypeHeader.VENDOR);
    }

    @Override
    public void execute() {
        ListModel<ListItem> listModel = MediatorGui.managerScan().listPaths.getModel();
        for (int i = 0 ; i < listModel.getSize() ; i++) {
            if (listModel.getElementAt(i).getInternalString().contains(this.url)) {
                listModel.getElementAt(i).setIsDatabaseConfirmed(true);
                listModel.getElementAt(i).setInternalString(listModel.getElementAt(i).getInternalString() +" ["+this.vendor+"]");
                ((DefaultListModel<ListItem>) listModel).setElementAt(listModel.getElementAt(i), i);
            }
        }
    }
    
}

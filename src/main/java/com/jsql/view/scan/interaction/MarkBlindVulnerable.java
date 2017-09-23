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

import com.jsql.model.bean.util.Header;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.ItemList;

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
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        this.url = (String) params.get(Header.URL);
    }

    @Override
    public void execute() {
        ListModel<ItemList> listModel = MediatorGui.managerScan().getListPaths().getModel();
        for (int i = 0 ; i < listModel.getSize() ; i++) {
            if (listModel.getElementAt(i).getInternalString().contains(this.url)) {
                listModel.getElementAt(i).setIsVulnerable(true);
                listModel.getElementAt(i).setInternalString(listModel.getElementAt(i).getInternalString().replace(" [Blind]", "") +" [Blind]");
                ((DefaultListModel<ItemList>) listModel).setElementAt(listModel.getElementAt(i), i);
            }
        }
    }
    
}

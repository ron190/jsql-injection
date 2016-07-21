/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.ListItem;

/**
 * Mark the injection as vulnerable to a error-based injection.
 */
public class MarkErrorbasedVulnerable implements InteractionCommand {

    private String url;
    
    /**
     * @param interactionParams
     */
    @SuppressWarnings("unchecked")
    public MarkErrorbasedVulnerable(Object[] interactionParams) {
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        url = (String) params.get("Url");
    }

    @Override
    public void execute() {
        ListModel<ListItem> listModel = MediatorGui.tabManagers().scanListManager.listPaths.getModel();
        for (int i = 0 ; i < listModel.getSize() ; i++) {
            if (listModel.getElementAt(i).internalString.contains(url)) {
                listModel.getElementAt(i).isVulnerable = true;
                ((DefaultListModel<ListItem>) listModel).setElementAt(listModel.getElementAt(i), i);
            }
        }
    }
}

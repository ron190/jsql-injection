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
 * Mark the injection as vulnerable to a basic injection.
 */
public class MarkNormalVulnerable implements InteractionCommand {

    private String url;

    /**
     * @param nullParam
     */
    @SuppressWarnings("unchecked")
    public MarkNormalVulnerable(Object[] interactionParams) {
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        url = (String) params.get("Url");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void execute() {
        ListModel<ListItem> listModel = MediatorGui.tabManagers().scanListManager.listPaths.getModel();
        for (int i = 0 ; i < listModel.getSize() ; i++) {
            if (listModel.getElementAt(i).internalString.equals(url)) {
                listModel.getElementAt(i).isValidated = true;
                ((DefaultListModel) listModel).setElementAt(listModel.getElementAt(i), i);
            }
        }
    }
}

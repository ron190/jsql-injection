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

import org.apache.log4j.Logger;

import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;

/**
 * Mark the injection as vulnerable to a basic injection.
 */
public class MarkNormalVulnerable implements IInteractionCommand {
    public static final Logger LOGGER = Logger.getLogger(MarkNormalVulnerable.class);

    private String url;

    /**
     * @param nullParam
     */
    public MarkNormalVulnerable(Object[] interactionParams) {
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        url = (String) params.get("Url");
    }

    @Override
    public void execute() {
        LOGGER.debug("Vulnerable to Normal injection.");
        
        for (int i = 0 ; i < MediatorGUI.left().scanListManager.listPaths.getModel().getSize() ; i++) {
            if (MediatorGUI.left().scanListManager.listPaths.getModel().getElementAt(i).internalString.equals(url)) {
                MediatorGUI.left().scanListManager.listPaths.getModel().getElementAt(i).isValidated = true;
                ((DefaultListModel) MediatorGUI.left().scanListManager.listPaths.getModel()).setElementAt(
                        MediatorGUI.left().scanListManager.listPaths.getModel().getElementAt(i), 
                        i);
//                MediatorGUI.left().scanListManager.listPaths.getModel().getElementAt(i).internalString = "##=> " + 
//                        MediatorGUI.left().scanListManager.listPaths.getModel().getElementAt(i).internalString;
            }
        }
    }
}

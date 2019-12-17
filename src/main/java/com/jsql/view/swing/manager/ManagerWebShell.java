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
package com.jsql.view.swing.manager;

import com.jsql.model.MediatorModel;
import com.jsql.model.exception.JSqlException;

/**
 * Manager for uploading PHP webshell to the host and send system commands.
 */
@SuppressWarnings("serial")
public class ManagerWebShell extends AbstractManagerShell {
    
    public ManagerWebShell() {
        this.run.setText("Create Web shell(s)");
    }
    
    @Override
    void createPayload(String pathShell, String urlShell) throws JSqlException, InterruptedException {
        MediatorModel.model().getResourceAccess().createWebShell(pathShell, urlShell);
    }
    
}

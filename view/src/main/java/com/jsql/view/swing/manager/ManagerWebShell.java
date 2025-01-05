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
package com.jsql.view.swing.manager;

import com.jsql.model.exception.JSqlException;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Manager for uploading PHP webshell to the host and send system commands.
 */
public class ManagerWebShell extends AbstractManagerShell {
    
    public ManagerWebShell() {
        this.run.setText("Create Web shell(s)");
    }
    
    @Override
    protected void createPayload(String pathShell, String urlShell) throws JSqlException {
        MediatorHelper.model().getResourceAccess().createWebShell(pathShell, urlShell);
    }
}

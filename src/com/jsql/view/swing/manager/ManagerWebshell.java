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
package com.jsql.view.swing.manager;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;

/**
 * Manager for uploading PHP webshell to the host and send system commands.
 */
@SuppressWarnings("serial")
public class ManagerWebshell extends ManagerAbstractShell {
    @Override
    void action(String path, String shellURL) throws PreparationException,
            StoppableException {
        MediatorModel.model().ressourceAccessObject.getShell(path.toString(), shellURL);
    }
}

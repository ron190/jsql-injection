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

import java.util.List;

import org.apache.log4j.Logger;

import com.jsql.model.bean.Database;

/**
 * Add the databases to current injection panel.
 */
public class AddDatabases implements IInteractionCommand {
    public static final Logger LOGGER = Logger.getLogger(AddDatabases.class);
    
    /**
     * Databases retreived by the view.
     */
    private List<Database> databases;

    /**
     * @param interactionParams List of databases retreived by the Model
     */
    @SuppressWarnings("unchecked")
    public AddDatabases(Object[] interactionParams) {
        // Get list of databases from the model
        databases = (List<Database>) interactionParams[0];
    }

    @Override
    public void execute() {
        // Loop into the list of databases
        int i = 1;
        for (Database database: databases) {
            LOGGER.trace("\t" + i + ". " + database);
            i++;
        }
    }
}

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
package com.jsql.view.terminal.interaction;

import java.util.List;

import org.apache.log4j.Logger;

import com.jsql.model.bean.database.Database;
import com.jsql.view.interaction.InteractionCommand;

/**
 * Add the databases to current injection panel.
 */
public class AddDatabases implements InteractionCommand {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Databases retrieved by the view.
     */
    private List<Database> databases;

    /**
     * @param interactionParams List of databases retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddDatabases(Object[] interactionParams) {
        // Get list of databases from the model
        this.databases = (List<Database>) interactionParams[0];
    }

    @Override
    public void execute() {
        LOGGER.info(InteractionCommand.addGreenColor(this.getClass().getSimpleName()));
        
        // Loop into the list of databases
        for (Database database: this.databases) {
            LOGGER.info(database);
        }
    }
    
}

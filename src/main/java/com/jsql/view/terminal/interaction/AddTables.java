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

import com.jsql.model.bean.database.Table;
import com.jsql.view.swing.interaction.InteractionCommand;

/**
 * Add the tables to the corresponding database.
 */
public class AddTables implements InteractionCommand {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(AddTables.class);

    /**
     * Tables retreived by the view.
     */
    private List<Table> tables;

    /**
     * @param interactionParams List of tables retreived by the Model
     */
    @SuppressWarnings("unchecked")
    public AddTables(Object[] interactionParams) {
        tables = (List<Table>) interactionParams[0];
    }

    @Override
    public void execute() {
        // Loop into the list of tables
        for (Table table: tables) {
            LOGGER.info(table);
        }
        LOGGER.info("\n");
    }
}

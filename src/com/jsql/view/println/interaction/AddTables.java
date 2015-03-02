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
package com.jsql.view.println.interaction;

import java.util.List;

import com.jsql.model.bean.Table;
import com.jsql.view.swing.interaction.IInteractionCommand;

/**
 * Add the tables to the corresponding database.
 */
public class AddTables implements IInteractionCommand {
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
            System.out.println(table);
        }
        System.out.println();
    }
}

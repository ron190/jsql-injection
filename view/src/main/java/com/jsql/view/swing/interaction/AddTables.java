/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import java.util.List;

import com.jsql.model.bean.database.Table;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Add the tables to the corresponding database.
 */
public class AddTables implements InteractionCommand {

    /**
     * Tables retrieved by the view.
     */
    private List<Table> tables;

    /**
     * @param interactionParams List of tables retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddTables(Object[] interactionParams) {
        
        this.tables = (List<Table>) interactionParams[0];
    }

    @Override
    public void execute() {
        
        MediatorHelper.treeDatabase().addTables(this.tables);
    }
}

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

import com.jsql.model.bean.database.Column;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Add the columns to corresponding table.
 */
public class AddColumns implements InteractionCommand {
    
    /**
     * Columns retrieved by the view.
     */
    private List<Column> columns;

    /**
     * @param interactionParams List of columns retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddColumns(Object[] interactionParams) {
        
        // Get list of columns from the model
        this.columns = (List<Column>) interactionParams[0];
    }

    @Override
    public void execute() {
        
        MediatorHelper.treeDatabase().addColumns(this.columns);
    }
}

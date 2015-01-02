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

import com.jsql.model.bean.Column;
import com.jsql.view.swing.interaction.IInteractionCommand;

/**
 * Add the columns to corresponding table.
 */
public class AddColumns implements IInteractionCommand {
    /**
     * Columns retreived by the view.
     */
    private List<Column> columns;

    /**
     * @param interactionParams List of columns retreived by the Model
     */
    @SuppressWarnings("unchecked")
    public AddColumns(Object[] interactionParams) {
        // Get list of columns from the model
        columns = (List<Column>) interactionParams[0];
    }

    @Override
    public void execute() {
        // Loop into the list of columns
        for (Column column: columns) {
            System.out.println(column);
        }
    }
}

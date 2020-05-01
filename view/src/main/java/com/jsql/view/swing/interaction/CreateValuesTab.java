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

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab extends CreateTabHelper implements InteractionCommand {
    
    /**
     * Array of column names, displayed in header table.
     */
    private String[] columnNames;

    /**
     * 2D array of values.
     */
    private String[][] data;

    /**
     * The table containing the data.
     */
    private AbstractElementDatabase table;

    /**
     * @param interactionParams Names of columns, table's values and corresponding table
     */
    public CreateValuesTab(Object[] interactionParams) {
        
        // Array of column names, displayed in header table
        this.columnNames = (String[]) interactionParams[0];
        
        // 2D array of values
        this.data = (String[][]) interactionParams[1];
        
        // The table containing the data
        this.table = (AbstractElementDatabase) interactionParams[2];
    }

    @Override
    public void execute() {
        
        MediatorHelper.treeDatabase().createValuesTab(this.data, this.columnNames, this.table);
    }
}

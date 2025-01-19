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
package com.jsql.view.swing.interaction;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab extends CreateTabHelper implements InteractionCommand {
    
    private final String[] columnNames;
    private final String[][] data;
    private final AbstractElementDatabase table;

    /**
     * @param interactionParams Names of columns, table's values and corresponding table
     */
    public CreateValuesTab(Object[] interactionParams) {
        this.columnNames = (String[]) interactionParams[0];  // Array of column names, displayed in header table
        this.data = (String[][]) interactionParams[1];  // 2D array of values
        this.table = (AbstractElementDatabase) interactionParams[2];  // The table containing the data
    }

    @Override
    public void execute() {
        SwingUtilities.invokeLater(() -> MediatorHelper.treeDatabase().createValuesTab(this.data, this.columnNames, this.table));
    }
}

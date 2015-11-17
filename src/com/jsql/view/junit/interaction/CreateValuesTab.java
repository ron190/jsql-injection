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
package com.jsql.view.junit.interaction;

import java.util.Arrays;

import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.view.swing.interaction.IInteractionCommand;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab implements IInteractionCommand {
    /**
     * Array of column names, diplayed in header table.
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
        // Array of column names, diplayed in header table
        columnNames = (String[]) interactionParams[0];
        // 2D array of values
        data = (String[][]) interactionParams[1];
        // The table containing the data
        table = (AbstractElementDatabase) interactionParams[2];
    }

    @Override
    public void execute() {
        System.out.println(Arrays.deepToString(data));
        System.out.println();
    }
}

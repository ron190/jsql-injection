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
package com.jsql.model.bean.database;

/**
 * Define a Column, e.g. is sent to the view by the model after injection.
 * Allow to traverse upward to its corresponding table
 */
public class Column extends AbstractElementDatabase {
    
    /**
     * The table that contains the current column.
     */
    private final Table parentTable;

    /**
     * Define the column label and parent table.
     */
    public Column(String newColumnName, Table newTableName) {
        this.elementValue = newColumnName;
        this.parentTable = newTableName;
    }

    /**
     * Return the parent table.
     * @return Parent for column
     */
    @Override
    public AbstractElementDatabase getParent() {
        return this.parentTable;
    }

    /**
     * Default 0, a column doesn't contain anything.
     * @return No child for column
     */
    @Override
    public int getChildCount() {
        return 0;
    }

    /**
     * A readable label for column is its own label.
     * @return column text
     */
    @Override
    public String getLabelWithCount() {
        return this.toString();
    }
}


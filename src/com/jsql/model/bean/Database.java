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
package com.jsql.model.bean;

/**
 * Define a Database, e.g is sent to the view by the model after injection.
 */
public class Database extends ElementDatabase {
    /**
     * The number of tables in the database.
     */
    private String tableCount;

    /**
     * Define the database label and number of tables.
     * @param newDatabaseName
     * @param newTableCount
     */
    public Database(String newDatabaseName, String newTableCount) {
        this.elementValue = newDatabaseName;
        this.tableCount = newTableCount;
    }

    /**
     * A database has no parent.
     */
    @Override
    public ElementDatabase getParent() {
        return null;
    }

    /**
     * Return the number of tables in the table.
     */
    @Override
    public int getCount() {
        return Integer.parseInt(tableCount);
    }

    /**
     * A readable label for the database, with number of tables,
     * displayed by the view, e.g my_database (7 tables).
     */
    @Override
    public String getLabel() {
        return this.elementValue 
                + " (" + tableCount + " table" + (Integer.parseInt(tableCount) > 0 ? "s" : "") + ")";
    }
}

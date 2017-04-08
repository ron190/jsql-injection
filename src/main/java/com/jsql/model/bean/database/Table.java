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
package com.jsql.model.bean.database;

import org.apache.log4j.Logger;

/**
 * Define a Table, e.g is sent to the view by the model after injection.
 * Allow to traverse upward to its corresponding database.
 */
public class Table extends AbstractElementDatabase {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * The database that contains the current column.
     */
    private Database parentDatabase;
    
    /**
     * The number of rows in the table.
     */
    private String rowCount;

    /**
     * Define the table label, number of rows and parent database.
     * @param newTableName
     * @param newRowCount
     * @param newParentDatabase
     */
    public Table(String newTableName, String newRowCount, Database newParentDatabase) {
        this.elementValue = newTableName;
        this.rowCount = newRowCount;
        this.parentDatabase = newParentDatabase;
    }

    /**
     * Return the parent database.
     */
    @Override
    public AbstractElementDatabase getParent() {
        return this.parentDatabase;
    }

    /**
     * Return the number of rows in the table.
     */
    @Override
    public int getChildCount() {
        return Integer.parseInt(this.rowCount);
    }

    /**
     * A readable label for the table, with number of rows, displayed
     * by the view. If parent database is the system information_schema, number
     * of rows is unknown, e.g my_table (7 rows).
     */
    @Override
    public String getLabelCount() {
        String nbRow;
        if ("information_schema".equals(this.parentDatabase.toString())) {
            nbRow = "?";
        } else {
            nbRow = this.rowCount;
        }
        
        // Report #138: detect incorrect number of rows
        String sPlural = "";
        try {
            if (Integer.parseInt(this.rowCount) > 1) {
                sPlural = "s";
            }
        } catch (NumberFormatException e) {
            this.rowCount = "0";
            LOGGER.warn("Incorrect number of rows.");
        }
        
        return this.elementValue +" ("+ nbRow +" row"+ sPlural +")";
    }
    
}

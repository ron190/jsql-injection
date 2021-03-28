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
package com.jsql.model.bean.database;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * Define a Database, e.g is sent to the view by the model after injection.
 */
public class Database extends AbstractElementDatabase {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    // The number of tables in the database.
    // TODO to int
    private String tableCount;

    /**
     * Define the database label and number of tables.
     * @param databaseName
     * @param tableCount
     */
    public Database(String databaseName, String tableCount) {
        
        this.elementValue = databaseName;
        this.tableCount = tableCount;
    }

    // A database has no parent.
    @Override
    public AbstractElementDatabase getParent() {
        
        return null;
    }
    
    // Return the number of tables in the table.
    @Override
    public int getChildCount() {
        
        return Integer.parseInt(this.tableCount);
    }

    /**
     * A readable label for the database, with number of tables,
     * displayed by the view, e.g my_database (7 tables).
     */
    @Override
    public String getLabelCount() {
        
        // Report #1500: detect incorrect number of tables
        String sPlural = StringUtils.EMPTY;
        
        try {
            if (Integer.parseInt(this.tableCount) > 1) {
                
                sPlural = "s";
            }
            
        } catch (NumberFormatException e) {
            
            this.tableCount = "0";
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Incorrect number of tables for [{}].", this);
        }
        
        return
            String
            .format(
                "%s (%s table%s)",
                this.elementValue,
                this.tableCount,
                sPlural
            );
    }
}

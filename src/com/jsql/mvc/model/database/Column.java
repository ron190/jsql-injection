package com.jsql.mvc.model.database;

/**
 * Define a Column, e.g is sent to the view by the model after injection.
 * Allow to traverse upward to its corresponding table  
 */
public class Column extends ElementDatabase {
    // The table that contains the current column
    public Table parentTable;

    // Define the column label and parent table
    public Column(String newColumnName, Table newTableName) {
        this.elementValue = newColumnName;
        this.parentTable = newTableName;
    }

    // Return the parent table
    @Override
    public ElementDatabase getParent() {
        return parentTable;
    }

    // Default 0, a column doesn't contain anything
    @Override
    public int getCount() {
        return 0;
    }

    // A readable label for column is its own label, nothing fancy
    @Override
    public String toFormattedString() {
        return toString();
    }
}


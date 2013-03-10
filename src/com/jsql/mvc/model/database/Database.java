package com.jsql.mvc.model.database;

/**
 * Define a Database, e.g is sent to the view by the model after injection
 */
public class Database extends ElementDatabase {
    // The number of tables in the database
    public String tableCount;

    // Define the database label and number of tables
    public Database(String newDatabaseName, String newTableCount) {
        this.elementValue = newDatabaseName;
        this.tableCount = newTableCount;
    }

    // A database has no parent
    @Override
    public ElementDatabase getParent() {
        return null;
    }

    // Return the number of tables in the table
    @Override
    public int getCount() {
        return Integer.parseInt(tableCount);
    }

    /**
     * A readable label for the database, with number of tables, displayed by the view
     * e.g my_database (7 tables)
     */
    @Override
    public String toFormattedString() {
        return this.elementValue + " ("+tableCount+" table"+(Integer.parseInt(tableCount)>0?"s":"")+")";
    }
}

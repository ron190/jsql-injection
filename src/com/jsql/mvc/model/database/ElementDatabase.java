package com.jsql.mvc.model.database;

/**
 * Class used by the model to properly define components of the database.
 * When the model ends a process of injection, it builds the corresponding database elements
 * and provides them to the view.
 * You can traverse elements from columns, to its corresponding table, to its corresponding database,
 * inverse isn't required (database>table>column is not used)
 * Concern only databases, tables and columns, values are raw data directly processed by the view 
 */
public abstract class ElementDatabase {
    // Label of the current element
    protected String elementValue;
    
    // Traverse upward, and return the parent
    public abstract ElementDatabase getParent();
    /**
     * Return the number of elements contained by current element
     * - for database: number of tables
     * - for table: number of rows
     */
    public abstract int getCount();
    
    // Return a readable label displayed by the view 
    public abstract String toFormattedString();
    
    // Return the label of current element 
    public String toString() {
        return this.elementValue;
    }
}

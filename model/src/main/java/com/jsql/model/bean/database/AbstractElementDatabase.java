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

/**
 * Class used by the model to properly define components of the database.
 * When the model ends a process of injection, it builds the corresponding database elements
 * and provides them to the view.
 * You can traverse elements from columns, to its corresponding table, to its corresponding database,
 * inverse isn't required (database>table>column is not used)
 * Concern only databases, tables and columns, values are raw data directly processed by the view
 */
public abstract class AbstractElementDatabase {
    
    /**
     * Label of the current element.
     */
    protected String elementValue;
    
    /**
     * Traverse upward, and return the parent.
     * @return
     */
    public abstract AbstractElementDatabase getParent();
    
    /**
     * Return the number of elements contained by current element :<br>
     * - for database: number of tables,<br>
     * - for table: number of rows.<br>
     */
    public abstract int getChildCount();
    
    /**
     * Return a readable label displayed by the view.
     * @return
     */
    public abstract String getLabelCount();
    
    /**
     * Return the label of current element.
     */
    @Override
    public String toString() {
        return this.elementValue;
    }
    
    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }
}

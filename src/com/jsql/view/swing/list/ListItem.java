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
package com.jsql.view.swing.list;

/**
 * Basic object to avoid String incompatibility with drag and drop feature.
 */
public class ListItem {
    /**
     * Text displayed on item.
     */
    private String internalString;
    
    /**
     * Create a JList item.
     * @param newString
     */
    public ListItem(String newString) {
        internalString = newString;
    }

    @Override
    public String toString() {
        return internalString;
    }
}

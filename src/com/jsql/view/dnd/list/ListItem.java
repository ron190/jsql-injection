/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.dnd.list;

public class ListItem{
    
    String internalString;
    
    public ListItem(String newString){
        internalString = newString;
    }

    @Override
    public String toString() {
        return internalString;
    }
}

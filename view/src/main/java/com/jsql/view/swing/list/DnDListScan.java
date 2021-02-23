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
package com.jsql.view.swing.list;

import java.util.List;

/**
 * A list supporting drag and drop with copy/paste object functionality.
 */
@SuppressWarnings("serial")
public class DnDListScan extends DnDList {
    
    public DnDListScan(List<ItemList> newList) {
        
        super(newList);
    }

    @Override
    public void addItem(int endPosition, String line) {
        
        this.listModel.add(endPosition, new ItemListScan(new BeanInjection(line.replace("\\", "/"))));
    }
}

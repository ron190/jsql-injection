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
package com.jsql.view.tree;

import com.jsql.model.Interruptable;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Table;

/**
 * Model adding functional layer to the node, add information to tree node in term of injection process.
 * Used by renderer and editor.
 * @param <T> The database element for this node.
 */
public class NodeModel<T extends ElementDatabase>{
    public T dataObject;
    public int childUpgradeCount = 0;

    public Interruptable interruptable;

    public boolean isChecked = false;
    public boolean isRunning = false;
    public boolean hasChildChecked = false;
    public boolean hasBeenSearched = false;
    public boolean hasIndeterminatedProgress = false;
    public boolean hasProgress = false;

    public NodeModel(T newObject){
        this.dataObject = newObject;
    }

    public ElementDatabase getParent(){
        return dataObject.getParent();
    }
    public boolean isDatabase(){
        return dataObject instanceof Database;
    }
    public boolean isTable(){
        return dataObject instanceof Table;
    }
    public boolean isColumn(){
        return dataObject instanceof Column;
    }

    public String toString(){
        return this.dataObject.getLabel();
    }
}

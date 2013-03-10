package com.jsql.mvc.view.component;

import com.jsql.mvc.model.Interruptable;
import com.jsql.mvc.model.database.Column;
import com.jsql.mvc.model.database.Database;
import com.jsql.mvc.model.database.ElementDatabase;
import com.jsql.mvc.model.database.Table;


public class TreeNodeModel<T extends ElementDatabase>{
    public String textNode;
    public T dataObject;
    public int childUpgradeCount = 0;
    
    public Interruptable interruptable;
    
    public boolean isSelected = false;
    public boolean isRunning = false;
    public boolean hasCheckBox = false;
    public boolean hasChildSelected = false;
    public boolean hasBeenSearched = false;
    public boolean hasIndeterminatedProgress = false;
    public boolean hasProgress = false;
    
    public TreeNodeModel(T newObject){
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
        return this.dataObject.toFormattedString();
    }
}
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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.view.GUIMediator;

/**
 * Model adding functional layer to the node, add information to tree node in term of injection process.
 * Used by renderer and editor.
 * @param <T> The database element for this node.
 */
public class NodeModelDatabase extends NodeModel{
	
    public NodeModelDatabase(ElementDatabase newObject) {
		super(newObject);
	}

	@Override
	Icon getIcon(boolean leaf) {
		if(leaf)
            return new ImageIcon(getClass().getResource("/com/jsql/view/images/databaseGo.png"));
        else
            return new ImageIcon(getClass().getResource("/com/jsql/view/images/database.png"));
	}

	@Override
	void runAction() {
		Database selectedDatabase = (Database) this.dataObject;
        if(!this.hasBeenSearched && !this.isRunning){
        	this.interruptable = GUIMediator.controller().selectDatabase(selectedDatabase);
        	this.isRunning = true;
        }
	}
	
	@Override boolean verifyShowPopup() { 
		return !this.hasBeenSearched && this.isRunning; 
	}

	@Override void displayMenu(JPopupMenu tablePopupMenu, TreePath path) {}
}

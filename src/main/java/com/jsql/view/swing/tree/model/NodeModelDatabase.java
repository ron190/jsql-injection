/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree.model;

import javax.swing.Icon;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;

/**
 * Database model displaying the database icon on the label.
 */
public class NodeModelDatabase extends AbstractNodeModel {
    
    /**
     * Node as a database model.
     * @param database Element database coming from model
     */
    public NodeModelDatabase(Database database) {
        super(database);
    }

    @Override
    protected Icon getLeafIcon(boolean leaf) {
        if (leaf) {
            return HelperUi.ICON_DATABASE_GO;
        } else {
            return HelperUi.ICON_DATABASE;
        }
    }

    @Override
    public void runAction() {
        final Database selectedDatabase = (Database) this.getElementDatabase();
        if (/*!this.isLoaded && */!this.isRunning()) {
            MediatorGui.frame().getTreeNodeModels().get(this.getElementDatabase()).removeAllChildren();
            DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();
            treeModel.reload(MediatorGui.frame().getTreeNodeModels().get(this.getElementDatabase()));
            
            new SwingWorker<Object, Object>() {
                
                @Override
                protected Object doInBackground() throws Exception {
                    Thread.currentThread().setName("SwingWorkerNodeModelDatabase");
                    return MediatorModel.model().getDataAccess().listTables(selectedDatabase);
                }
                
            }.execute();
            
            this.setRunning(true);
        }
    }

    @Override
    public boolean isPopupDisplayable() {
        return this.isLoaded() || !this.isLoaded() && this.isRunning();
    }

    @Override
    protected void buildMenu(JPopupMenu2 tablePopupMenu, TreePath path) {
        // Do nothing
    }
    
}

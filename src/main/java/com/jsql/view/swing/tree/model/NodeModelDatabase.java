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

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.view.swing.HelperUi;

/**
 * Database model displaying the database icon on the label.
 */
public class NodeModelDatabase extends AbstractNodeModel {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeModelDatabase.class);
    
    /**
     * Node as a database model.
     * @param database Element database coming from model
     */
    public NodeModelDatabase(Database database) {
        super(database);
    }

    @Override
    Icon getLeafIcon(boolean leaf) {
        if (leaf) {
            return HelperUi.ICON_DATABASE_GO;
        } else {
            return HelperUi.ICON_DATABASE;
        }
    }

    @Override
    public void runAction() {
        final Database selectedDatabase = (Database) this.elementDatabase;
        if (!this.isSearched && !this.isRunning) {
            
            new SwingWorker<Object, Object>(){
                @Override
                protected Object doInBackground() throws Exception {
                    return DataAccess.listTables(selectedDatabase);
                }
                
                @Override
                @SuppressWarnings("unchecked")
                protected void done() {
                    List<Table> tables = new ArrayList<>();
                    try {
                        tables = (List<Table>) get();
                    } catch (Exception e) {
                        LOGGER.warn(e, e);
                    } finally {
                        Request requestAddTables = new Request();
                        requestAddTables.setMessage(TypeRequest.ADD_TABLES);
                        requestAddTables.setParameters(tables);
                        MediatorModel.model().sendToViews(requestAddTables);
                  
                        Request requestEndProgress = new Request();
                        requestEndProgress.setMessage(TypeRequest.END_PROGRESS);
                        requestEndProgress.setParameters(selectedDatabase);
                        MediatorModel.model().sendToViews(requestEndProgress);
                    }
                }
            }.execute();
            
            this.isRunning = true;
        }
    }

    @Override 
    public boolean isPopupDisplayable() {
        return !this.isSearched && this.isRunning;
    }

    @Override 
    void displayMenu(JPopupMenu tablePopupMenu, TreePath path) {
        // Do nothing
    }
}

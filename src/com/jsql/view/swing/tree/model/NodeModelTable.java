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
package com.jsql.view.swing.tree.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.i18n.I18n;
import com.jsql.model.bean.Table;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.tree.ImageObserverAnimated;
import com.jsql.view.swing.tree.ImageOverlap;
import com.jsql.view.swing.tree.PanelNode;

/**
 * Table model displaying the table icon on the label.
 */
public class NodeModelTable extends AbstractNodeModel {
    /**
     * Node as a table model.
     * @param table Element table coming from model
     */
    public NodeModelTable(Table table) {
        super(table);
    }

    @Override
    Icon getLeafIcon(boolean leaf) {
        if (leaf) {
            return new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/tableGo.png"));
        } else {
            return HelperGUI.TABLE_ICON;
        }
    }

    @Override
    protected void displayProgress(PanelNode panel, DefaultMutableTreeNode currentNode) {
        if ("information_schema".equals(this.getParent().toString())) {
            panel.showLoader();

            if (MediatorGUI.model().suspendables.get(this.dataObject).isPaused()) {
                ImageIcon animatedGIFPaused = new ImageOverlap(HelperGUI.PATH_PROGRESSBAR, HelperGUI.PATH_PAUSE);
                animatedGIFPaused.setImageObserver(new ImageObserverAnimated(MediatorGUI.databaseTree(), currentNode));
                panel.setLoaderIcon(animatedGIFPaused);
            }
        } else {
            super.displayProgress(panel, currentNode);
        }
    }

    @Override
    public void runAction() {
        final Table selectedTable = (Table) this.dataObject;
        if (!this.hasBeenSearched && !this.isRunning) {
            new SwingWorker<Object, Object>(){

                @Override
                protected Object doInBackground() throws Exception {
                    MediatorGUI.model().dataAccessObject.listColumns(selectedTable);
                    return null;
                }
                
            }.execute();
            this.isRunning = true;
        }
    }

    @Override
    void displayMenu(JPopupMenu tablePopupMenu, final TreePath path) {
        JMenuItem mnCheckAll = new JMenuItem(I18n.CHECK_ALL, 'C');
        JMenuItem mnUncheckAll = new JMenuItem(I18n.UNCHECK_ALL, 'U');

        mnCheckAll.setIcon(HelperGUI.EMPTY);
        mnUncheckAll.setIcon(HelperGUI.EMPTY);

        if (!this.hasBeenSearched) {
            mnCheckAll.setEnabled(false);
            mnUncheckAll.setEnabled(false);

            tablePopupMenu.add(mnCheckAll);
            tablePopupMenu.add(mnUncheckAll);
            tablePopupMenu.add(new JSeparator());
        }

        class TableMenuCheckUncheck implements ActionListener {
            private boolean check;
            
            TableMenuCheckUncheck(boolean check) {
                this.check = check;
            }

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                final AbstractNodeModel currentTableModel = (AbstractNodeModel) currentTableNode.getUserObject();
                
                DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGUI.databaseTree().getModel();

                int tableChildCount = treeModel.getChildCount(currentTableNode);
                for (int i = 0; i < tableChildCount; i++) {
                    DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
                    if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                        AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                        columnTreeNodeModel.isChecked = this.check;
                        currentTableModel.hasChildChecked = this.check;
                    }
                }

                treeModel.nodeChanged(currentTableNode);
            }
        }

        class CheckAll extends TableMenuCheckUncheck {
            CheckAll() {
                super(true);
            }
        }

        class UncheckAll extends TableMenuCheckUncheck {
            UncheckAll() {
                super(false);
            }
        }

        mnCheckAll.addActionListener(new CheckAll());
        mnUncheckAll.addActionListener(new UncheckAll());

        mnCheckAll.setIcon(HelperGUI.EMPTY);
        mnUncheckAll.setIcon(HelperGUI.EMPTY);

        tablePopupMenu.add(mnCheckAll);
        tablePopupMenu.add(mnUncheckAll);
        tablePopupMenu.add(new JSeparator());
    }
    
    @Override public boolean verifyShowPopup() {
        return this.hasBeenSearched || !this.hasBeenSearched && this.isRunning;
    }
}

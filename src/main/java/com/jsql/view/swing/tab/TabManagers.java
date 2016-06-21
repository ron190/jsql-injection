/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerBruteForce;
import com.jsql.view.swing.manager.ManagerCoder;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerScanList;
import com.jsql.view.swing.manager.ManagerSqlshell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebshell;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tree.CellEditorNode;
import com.jsql.view.swing.tree.CellRendererNode;
import com.jsql.view.swing.tree.model.NodeModelEmpty;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class TabManagers extends MouseTabbedPane {
    /**
     * Panel for executing system commands.
     */
    public final ManagerWebshell shellManager = new ManagerWebshell();

    /**
     * Panel for testing multiple URLs.
     */
    public final ManagerScanList scanListManager = new ManagerScanList();

    /**
     * Panel for testing backoffice admin pages.
     */
    public final ManagerAdminPage adminPageManager = new ManagerAdminPage();
    
    /**
     * Panel for reading files source.
     */
    public final ManagerFile fileManager = new ManagerFile();

    /**
     * Panel for uploading files.
     */
    public final ManagerUpload uploadManager = new ManagerUpload();

    /**
     * Panel for sending SQL requests.
     */
    public final ManagerSqlshell sqlShellManager = new ManagerSqlshell();

    /**
     * Create manager panel.
     */
    public TabManagers() {
        this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
        
        // Allows to resize to zero
        this.setMinimumSize(new Dimension());
        
        this.activateMenu();

        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty(I18n.NO_DATABASE));
        final JTree tree = new JTree(root);
        MediatorGui.register(tree);

        // Graphic manager for components
        tree.setCellRenderer(new CellRendererNode());

        // Action manager for components
        tree.setCellEditor(new CellEditorNode());

        // Tree setting
        // allows repaint nodes
        tree.setEditable(true);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // TODO Dirty trick that allows to repaint GIF progressbar
        tree.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if (arg0 != null) {
                    tree.firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY,
                        !tree.isRootVisible(),
                        tree.isRootVisible()
                    );
                    tree.treeDidChange();
                }
            }
            @Override public void treeStructureChanged(TreeModelEvent arg0) {
                // Do nothing
            }
            @Override public void treeNodesRemoved(TreeModelEvent arg0) {
                // Do nothing
            }
            @Override public void treeNodesInserted(TreeModelEvent arg0) {
                // Do nothing
            }
        });

        LightScrollPane scroller = new LightScrollPane(1, 1, 0, 0, tree);

        this.addTab(I18n.DATABASE, HelperGui.DATABASE_SERVER_ICON, scroller, I18n.DATABASE_TOOLTIP);
        this.addTab(I18n.ADMINPAGE, HelperGui.ADMIN_SERVER_ICON, adminPageManager, I18n.ADMINPAGE_TOOLTIP);
        this.addTab(I18n.FILE, HelperGui.FILE_SERVER_ICON, fileManager, I18n.FILE_TOOLTIP);
        this.addTab(I18n.WEBSHELL, HelperGui.SHELL_SERVER_ICON, shellManager, I18n.WEBSHELL_TOOLTIP);
        this.addTab(I18n.SQLSHELL, HelperGui.SHELL_SERVER_ICON, sqlShellManager, I18n.SQLSHELL_TOOLTIP);
        this.addTab(I18n.UPLOAD, HelperGui.UPLOAD_ICON, uploadManager, I18n.UPLOAD_TOOLTIP);
        this.addTab(I18n.BRUTEFORCE, HelperGui.BRUTER_ICON, new ManagerBruteForce(), I18n.BRUTEFORCE_TOOLTIP);
        this.addTab(I18n.CODER, HelperGui.CODER_ICON, new ManagerCoder(), I18n.CODER_TOOLTIP);
        this.addTab(I18n.SCANLIST, HelperGui.SCANLIST_ICON, scanListManager, I18n.SCANLIST_TOOLTIP);

        this.fileManager.setButtonEnable(false);
        this.shellManager.setButtonEnable(false);
        this.sqlShellManager.setButtonEnable(false);
    }
}

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
package com.jsql.view.tab;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.view.MediatorGUI;
import com.jsql.view.ToolsGUI;
import com.jsql.view.manager.ManagerAdminPage;
import com.jsql.view.manager.ManagerBruteForce;
import com.jsql.view.manager.ManagerCoder;
import com.jsql.view.manager.ManagerFile;
import com.jsql.view.manager.ManagerSQLShell;
import com.jsql.view.manager.ManagerUpload;
import com.jsql.view.manager.ManagerWebshell;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.tree.CellEditorNode;
import com.jsql.view.tree.CellRendererNode;
import com.jsql.view.tree.NodeModelEmpty;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class AdapterLeftTabbedPane extends MouseTabbedPane {
    /**
     * Panel for executing system commands.
     */
    public ManagerWebshell shellManager = new ManagerWebshell();

    /**
     * Panel for testing backoffice admin pages.
     */
    public ManagerAdminPage adminPageManager = new ManagerAdminPage();

    /**
     * Panel for reading files source.
     */
    public ManagerFile fileManager = new ManagerFile();

    /**
     * Panel for uploading files.
     */
    public ManagerUpload uploadManager = new ManagerUpload();

    /**
     * Panel for sending SQL requests.
     */
    public ManagerSQLShell sqlShellManager = new ManagerSQLShell();

    /**
     * Create manager panel.
     */
    public AdapterLeftTabbedPane() {
        this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
        // Allows to resize to zero
        this.setMinimumSize(new Dimension());
        this.activateMenu();

        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty("No database"));
        final JTree tree = new JTree(root);
        MediatorGUI.register(tree);

        // Graphic manager for components
        tree.setCellRenderer(new CellRendererNode());

        // Action manager for components
        tree.setCellEditor(new CellEditorNode());

        // Tree setting
        // allows repaint nodes
        tree.setEditable(true);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Dirty trick that allows to repaint progressbar
        tree.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if (arg0 != null) {
                    tree.firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY,
                        !tree.isRootVisible(),
                        tree.isRootVisible()
                    );
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

        tree.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent arg0) {
                System.out.println("x");
            }
            @Override
            public void focusGained(FocusEvent arg0) {
                System.out.println("y");
            }
        });

        JScrollPanePixelBorder scroller = new JScrollPanePixelBorder(1, 1, 0, 0, tree);

        this.addTab("Database", ToolsGUI.DATABASE_SERVER_ICON, scroller, "Explore databases from remote host");
        this.addTab("Admin page", ToolsGUI.ADMIN_SERVER_ICON, adminPageManager, "Test admin pages on remote host");
        this.addTab("File", ToolsGUI.FILE_SERVER_ICON, fileManager, "Read files from remote host");
        this.addTab("Web shell", ToolsGUI.SHELL_SERVER_ICON, shellManager, "<html>Create a web shell to remote host and open a terminal<br><i>Allows system commands like ipconfig/ifconfig</i></html>");
        this.addTab("SQL shell", ToolsGUI.SHELL_SERVER_ICON, sqlShellManager, "<html>Create a SQL shell to remote host and open a terminal<br><i>Allows SQL commands like update/grant</i></html>");
        this.addTab("Upload", ToolsGUI.UPLOAD_ICON, uploadManager, "Upload a file to host");
        this.addTab("Brute force", ToolsGUI.BRUTER_ICON, new ManagerBruteForce(), "Brute force hashes");
        this.addTab("Coder", ToolsGUI.CODER_ICON, new ManagerCoder(), "Encode or decode a string");

        this.fileManager.setButtonEnable(false);
        this.shellManager.setButtonEnable(false);
        this.sqlShellManager.setButtonEnable(false);
    }
}

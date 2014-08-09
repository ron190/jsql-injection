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
package com.jsql.view.panel;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.manager.AdminPageManager;
import com.jsql.view.manager.BruteForceManager;
import com.jsql.view.manager.CoderManager;
import com.jsql.view.manager.FileManager;
import com.jsql.view.manager.SQLShellManager;
import com.jsql.view.manager.UploadManager;
import com.jsql.view.manager.WebshellManager;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.tab.MouseTabbedPane;
import com.jsql.view.tree.NodeEditor;
import com.jsql.view.tree.NodeModelEmpty;
import com.jsql.view.tree.NodeRenderer;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class AdapterLeftPane extends MouseTabbedPane {
    /**
     * Panel for executing system commands.
     */
    public WebshellManager shellManager = new WebshellManager();

    /**
     * Panel for testing backoffice admin pages.
     */
    public AdminPageManager adminPageManager = new AdminPageManager();

    /**
     * Panel for reading files source.
     */
    public FileManager fileManager = new FileManager();

    /**
     * Panel for uploading files.
     */
    public UploadManager uploadManager = new UploadManager();

    /**
     * Panel for sending SQL requests.
     */
    public SQLShellManager sqlShellManager = new SQLShellManager();

    /**
     * Create manager panel.
     */
    public AdapterLeftPane() {
        this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
        // Allows to resize to zero
        this.setMinimumSize(new Dimension());
        this.activateMenu();

        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty("No database"));
        final JTree tree = new JTree(root);
        GUIMediator.register(tree);

        // Graphic manager for components
        tree.setCellRenderer(new NodeRenderer());

        // Action manager for components
        tree.setCellEditor(new NodeEditor());

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

        this.addTab("Database", GUITools.DATABASE_SERVER_ICON, scroller, "Explore databases from remote host");
        this.addTab("Admin page", GUITools.ADMIN_SERVER_ICON, adminPageManager, "Test admin pages on remote host");
        this.addTab("File", GUITools.FILE_SERVER_ICON, fileManager, "Read files from remote host");
        this.addTab("Web shell", GUITools.SHELL_SERVER_ICON, shellManager, "<html>Create a web shell to remote host ; open a terminal<br><i>Allows OS commands like ipconfig/ifconfig</i></html>");
        this.addTab("SQL shell", GUITools.SHELL_SERVER_ICON, sqlShellManager, "<html>Create a SQL shell to remote host ; open a terminal<br><i>Allows SQL commands like update/grant</i></html>");
        this.addTab("Upload", GUITools.UPLOAD_ICON, uploadManager, "Upload a file to host");
        this.addTab("Brute force", GUITools.BRUTER_ICON, new BruteForceManager(), "Brute force hashes");
        this.addTab("Coder", GUITools.CODER_ICON, new CoderManager(), "Encode or decode a string");

        this.fileManager.setButtonEnable(false);
        this.shellManager.setButtonEnable(false);
        this.sqlShellManager.setButtonEnable(false);
    }
}

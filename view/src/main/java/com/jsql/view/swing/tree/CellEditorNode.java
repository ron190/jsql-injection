/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.tree.action.ActionCheckSingle;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Tree cell editor responsible for mouse action on nodes.
 */
public class CellEditorNode extends AbstractCellEditor implements TreeCellEditor, TreeSelectionListener, MouseListener {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Renderer for nodes included JPanel, button, checkbox, icons...
     */
    private final CellRendererNode defaultTreeRenderer;

    /**
     * Value contained in the editor.
     * Returned by getCellEditorValue().
     */
    private transient AbstractNodeModel nodeModel;

    /**
     * Build editor, add tree and mouse listener.
     */
    public CellEditorNode() {
        this.defaultTreeRenderer = new CellRendererNode();
        MediatorHelper.treeDatabase().addTreeSelectionListener(this);
        MediatorHelper.treeDatabase().addMouseListener(this);
    }

    @Override
    public Component getTreeCellEditorComponent(
        JTree tree, 
        Object nodeRenderer, 
        boolean selected, 
        boolean expanded, 
        boolean leaf, 
        int row
    ) {
        var componentRenderer = this.defaultTreeRenderer.getTreeCellRendererComponent(
            tree, nodeRenderer, true, expanded, leaf, row, true
        );

        final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        var currentNodeModel = currentNode.getUserObject();
        try {
            this.nodeModel = (AbstractNodeModel) currentNodeModel;
            if (componentRenderer instanceof JCheckBox checkboxRenderer) {
                checkboxRenderer.addActionListener(new ActionCheckSingle(this.nodeModel, currentNode));
            }
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        return componentRenderer;
    }

    @Override
    public Object getCellEditorValue() {
        return this.nodeModel;
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) MediatorHelper.treeDatabase().getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        if (node.getUserObject() instanceof AbstractNodeModel dataModel && !dataModel.isLoaded()) {
            dataModel.runAction();
        }
    }

    /**
     * Fix compatibility issue with right click on Linux.
     * @param mouseEvent Mouse event
     */
    private void showPopup(MouseEvent mouseEvent) {
        if (!mouseEvent.isPopupTrigger()) {
            return;
        }
        
        JTree tree = (JTree) mouseEvent.getSource();
        TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (path == null) {
            return;
        }

        DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (
            currentTableNode.getUserObject() instanceof AbstractNodeModel currentTableModel
            && currentTableModel.isPopupDisplayable()
        ) {
            currentTableModel.showPopup(currentTableNode, path, mouseEvent);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.showPopup(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        this.showPopup(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.tree.model.AbstractNodeModel;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Tree cell editor responsible for mouse action on nodes.
 */
@SuppressWarnings("serial")
public class CellEditorNode extends AbstractCellEditor implements TreeCellEditor, TreeSelectionListener, MouseListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Renderer for nodes included JPanel, button, checkbox, icons...
     */
    private CellRendererNode defaultTreeRenderer;

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
        JTree tree, Object nodeRenderer, boolean selected, boolean expanded, boolean leaf, int row
    ) {

        var componentRenderer = this.defaultTreeRenderer.getTreeCellRendererComponent(
            tree, nodeRenderer, true, expanded, leaf, row, true
        );

        final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        var currentNodeModel = currentNode.getUserObject();
        
        try {
            this.nodeModel = (AbstractNodeModel) currentNodeModel;
            
            if (componentRenderer instanceof JCheckBox) {
                
                ((JCheckBox) componentRenderer).addActionListener(
                    new ActionCheckUncheck(this.nodeModel, currentNode)
                );
            }
            
        } catch (Exception e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        return componentRenderer;
    }

    @Override
    public Object getCellEditorValue() {
        return this.nodeModel;
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) MediatorHelper.treeDatabase().getLastSelectedPathComponent();

        // Get rid of java.lang.NullPointerException
        if (node == null) {
            return;
        }

        if (node.getUserObject() instanceof AbstractNodeModel) {
            
            AbstractNodeModel dataModel = (AbstractNodeModel) node.getUserObject();
            if (!dataModel.isLoaded()) {
                
                dataModel.runAction();
            }
        }
    }

    /**
     * Fix compatibility issue with right click on Linux.
     * @param e Mouse event
     */
    private void showPopup(MouseEvent e) {
        
        if (!e.isPopupTrigger()) {
            return;
        }
        
        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        
        if (path == null) {
            return;
        }

        DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (currentTableNode.getUserObject() instanceof AbstractNodeModel) {
            
            AbstractNodeModel currentTableModel = (AbstractNodeModel) currentTableNode.getUserObject();
            
            if (currentTableModel.isPopupDisplayable()) {
                currentTableModel.showPopup(currentTableNode, path, e);
            }
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

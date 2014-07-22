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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jsql.view.GUITools;
import com.jsql.view.component.RoundBorder;

/**
 * Model adding functional layer to the node, add information to tree node in term of injection process.
 * Used by renderer and editor.
 * @param <T> The database element for this node.
 */
public class NodeModelEmpty extends NodeModel{
	
    public NodeModelEmpty(String newObject){
    	super(newObject);
    }
    
    @Override
    public Component getComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus){
    	DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
    	JPanel emptyPanel = new JPanel(new BorderLayout());
        JLabel text = new JLabel(((DefaultMutableTreeNode)currentNode).getUserObject().toString());
        emptyPanel.add(text);
        text.setBorder(new RoundBorder(4,1,false));
        if( (currentNode != null) && (currentNode instanceof DefaultMutableTreeNode)){
            if( selected ){
                emptyPanel.setBackground( GUITools.SELECTION_BACKGROUND );
                text.setBorder(new RoundBorder(4,1,true));
            }else
                emptyPanel.setBackground(Color.WHITE);
            if(hasFocus)
                text.setBorder(new RoundBorder(4,1,true));
            else
                text.setBorder(new RoundBorder(4,1,false));
        }
        return emptyPanel;
    }

	@Override Icon getIcon(boolean leaf) {return null;}
	@Override void runAction() {}
	@Override void displayMenu(JPopupMenu tablePopupMenu, TreePath path) {}
	void showPopup(final DefaultMutableTreeNode currentTableNode, TreePath path, int i, int j){}
}

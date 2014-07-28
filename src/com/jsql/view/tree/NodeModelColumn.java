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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.ElementDatabase;

/**
 * Model adding functional layer to the node, add information to tree node in term of injection process.
 * Used by renderer and editor.
 * @param <T> The database element for this node.
 */
public class NodeModelColumn extends NodeModel{
	
    public NodeModelColumn(ElementDatabase newObject) {
		super(newObject);
	}
    
    @Override
    public Component getComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus){
    	JCheckBox checkbox = new JCheckBox(this.toString(), this.isChecked);
        checkbox.setFont( new Font(checkbox.getFont().getName(), Font.PLAIN|Font.ITALIC, checkbox.getFont().getSize()) );
        checkbox.setBackground(Color.WHITE);
        return checkbox;
    }

	@Override Icon getIcon(boolean leaf) {return null;}
	@Override void runAction() {}
	@Override void displayMenu(JPopupMenu tablePopupMenu, TreePath path) {}
	@Override void showPopup(final DefaultMutableTreeNode currentTableNode, TreePath path, int i, int j){}
	@Override boolean verifyShowPopup() { return false; }
}

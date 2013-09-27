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

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Observer to update tree node composed by the animated GIF.
 */
public class AnimatedObserver implements ImageObserver {
    private JTree tree;
    private DefaultTreeModel model;
    private TreeNode node;

    AnimatedObserver(JTree tree, TreeNode node) {
        this.tree = tree;
        this.model = (DefaultTreeModel) tree.getModel();
        this.node = node;
    }

    @Override
    public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
        if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
            TreePath path = new TreePath(model.getPathToRoot(node));
            Rectangle rect = tree.getPathBounds(path);
            if (rect != null) {
                tree.repaint(rect);
            }
        }
        return (flags & (ALLBITS | ABORT)) == 0;
    }
}

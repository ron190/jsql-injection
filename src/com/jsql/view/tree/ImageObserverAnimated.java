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
public class ImageObserverAnimated implements ImageObserver {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private TreeNode node;

    /**
     * Build GIF animator for tree node.
     * @param tree Tree containing GIF node
     * @param node Node with a GIF to animate
     */
    public ImageObserverAnimated(JTree tree, TreeNode node) {
        this.tree = tree;
        this.treeModel = (DefaultTreeModel) tree.getModel();
        this.node = node;
    }

    @Override
    public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
        if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
            TreePath path = new TreePath(this.treeModel.getPathToRoot(this.node));
            Rectangle rect = this.tree.getPathBounds(path);
            if (rect != null) {
                this.tree.repaint(rect);
            }
        }
        return (flags & (ALLBITS | ABORT)) == 0;
    }
}

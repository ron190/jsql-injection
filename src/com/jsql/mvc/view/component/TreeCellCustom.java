package com.jsql.mvc.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

class NodeImageObserver implements ImageObserver {
    JTree tree;
    DefaultTreeModel model;
    TreeNode node;

    NodeImageObserver(JTree tree, TreeNode node) {
        this.tree = tree;
        this.model = (DefaultTreeModel) tree.getModel();
        this.node = node;
    }

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

public class TreeCellCustom extends JPanel {
    private static final long serialVersionUID = -5833890081484609705L;
    
    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    
    public JLabel icon = new JLabel();
    public JLabel label = new JLabel();
    public JLabel loader = new JLabel();
    public CustomProgressBar progressBar = new CustomProgressBar();
    
    public TreeCellCustom(JTree tree, TreeNode currentNode){
        ImageIcon iconLoader = new ImageIcon(getClass().getResource("/com/jsql/images/loader2.gif"));
        iconLoader.setImageObserver(new NodeImageObserver(tree, currentNode));
        loader.setIcon(iconLoader);

        progressBar.setPreferredSize(new Dimension(16, 16));
        progressBar.setUI(new BasicProgressBarUI());
        label.setOpaque(true);

        label.setBorder(new RoundedCornerBorder(4,1,true));
        
        this.setBackground(Color.WHITE);
        
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
        this.add(icon);
        this.add(loader);
        this.add(progressBar);
        this.add(label);
        
        progressBar.setVisible(false);
             loader.setVisible(false);
              label.setVisible(false);
               icon.setVisible(false);
    }
}

class CustomProgressBar extends JProgressBar{
    public boolean showPauseBullet = false;
    public CustomProgressBar(){
        super();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        if(showPauseBullet){
            try {
                BufferedImage im2 = ImageIO.read(TreeCellCustom.class.getResource("/com/jsql/images/bullet_pause.png"));
                g.drawImage(im2, (this.getWidth()-im2.getWidth())/2, (this.getHeight()-im2.getHeight())/2, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

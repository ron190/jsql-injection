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
package com.jsql.view.swing.tree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.TreeNode;

import com.jsql.view.swing.HelperGUI;

/**
 * A tree Node composed of an icon, a GIF loader, a progress bar, a label.
 */
@SuppressWarnings("serial")
public class PanelNode extends JPanel {
    /**
     * Default icon of the node (database or table).
     */
    private JLabel icon = new JLabel();

    /**
     * A GIF loader, displayed if progress track is unknown (like columns).
     */
    private JLabel loader = new JLabel();

    /**
     * Progress bar displayed during injection, with pause icon displayed if user paused the process.
     */
    public ProgressBarPausable progressBar = new ProgressBarPausable();

    /**
     * Text of the node.
     */
    public JLabel label = new JLabel();

    /**
     * Create Panel for tree nodes.
     * @param tree JTree to populate
     * @param currentNode Node to draw in the tree
     */
    public PanelNode(final JTree tree, final TreeNode currentNode) {
        super();

        ImageIcon animatedGIF = new ImageIcon(PanelNode.class.getResource(HelperGUI.PATH_PROGRESSBAR));
        animatedGIF.setImageObserver(new ImageObserverAnimated(tree, currentNode));
        this.loader.setIcon(animatedGIF);

        this.progressBar.setPreferredSize(new Dimension(16, 16));
        this.progressBar.setUI(new BasicProgressBarUI());
        this.label.setOpaque(true);

        this.label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(132, 172, 221)));

        this.setBackground(Color.WHITE);

        this.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
        this.add(this.icon);
        this.add(this.loader);
        this.add(this.progressBar);
        this.add(this.label);

        this.progressBar.setVisible(false);
             this.loader.setVisible(false);
              this.label.setVisible(false);
               this.icon.setVisible(false);
    }

    /**
     * Change the text icon.
     * @param newIcon An icon to display next to the text.
     */
    public void setIcon(Icon newIcon) {
        this.icon.setIcon(newIcon);
    }
    
    /**
     * Display the normal text icon to the left.
     */
    public void showIcon() {
        this.icon.setVisible(true);
    }
    
    /**
     * Mask the node icon for example when the loader component is displayed.
     */
    public void hideIcon() {
        this.icon.setVisible(false);
    }
    
    /**
     * Change the loader icon.
     * @param newIcon An icon to display for the loader.
     */
    public void setLoaderIcon(Icon newIcon) {
        this.loader.setIcon(newIcon);
    }

    /**
     * Display the animated gif loader.
     */
    public void showLoader() {
        this.loader.setVisible(true);
    }
}

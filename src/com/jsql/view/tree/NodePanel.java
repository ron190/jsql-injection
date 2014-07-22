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
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.TreeNode;

import com.jsql.view.GUITools;
import com.jsql.view.component.RoundBorder;

/**
 * A tree Node composed of an icon, a GIF loader, a progress bar, a label.
 */
@SuppressWarnings("serial")
public class NodePanel extends JPanel {
    /**
     * Default icon of the node (database or table)
     */
    private JLabel icon = new JLabel();
    
    /**
     * A GIF loader, displayed if progress track is unknown (like columns)
     */
    private JLabel loader = new JLabel();
    
    /**
     * Progress bar displayed during injection, with pause icon displayed if user paused the process
     */
    public ProgressBarPausable progressBar = new ProgressBarPausable();
    
    /**
     * Text of the node.
     */
    public JLabel label = new JLabel();
    
    public NodePanel(JTree tree, TreeNode currentNode){
        ImageIcon animatedGIF = new ImageIcon(getClass().getResource(GUITools.PATH_PROGRESSBAR));
        animatedGIF.setImageObserver(new AnimatedObserver(tree, currentNode));
        loader.setIcon(animatedGIF);

        progressBar.setPreferredSize(new Dimension(16, 16));
        progressBar.setUI(new BasicProgressBarUI());
        label.setOpaque(true);

        label.setBorder(new RoundBorder(4,1,true));
        
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
    
    /**
     * Change the text icon.
     * @param newIcon An icon to display next to the text.
     */
    public void setIcon(Icon newIcon){
        icon.setIcon(newIcon);
    }
    
    /**
     * Display the normal text icon to the left.
     */
    public void showIcon(){
        icon.setVisible(true);
    }
    
    /**
     * Mask the node icon for example when the loader component is displayed.
     */
    public void hideIcon(){
        icon.setVisible(false);
    }
    
    /**
     * Change the loader icon.
     * @param newIcon An icon to display for the loader.
     */
    public void setLoaderIcon(Icon newIcon){
        loader.setIcon(newIcon);
    }

    /**
     * Display the animated gif loader.
     */
    public void showLoader(){
        loader.setVisible(true);
    }
}

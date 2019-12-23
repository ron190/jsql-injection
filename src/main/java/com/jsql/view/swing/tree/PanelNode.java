/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jsql.i18n.I18n;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

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
    private ProgressBarPausable progressBar = new ProgressBarPausable();

    /**
     * Text of the node.
     */
    private JLabel label = new JLabel();
    private JTextField textFieldEditable = new JTextField(15);
    
    /**
     * Create Panel for tree nodes.
     * @param tree JTree to populate
     * @param currentNode Node to draw in the tree
     */
    public PanelNode(final JTree tree, final DefaultMutableTreeNode currentNode) {
        ImageIcon animatedGIF = new ImageIcon(PanelNode.class.getClassLoader().getResource(HelperUi.PATH_PROGRESSBAR));
        animatedGIF.setImageObserver(new ImageObserverAnimated(tree, currentNode));
        
        this.loader.setIcon(animatedGIF);
        this.loader.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        this.progressBar.setPreferredSize(new Dimension(20, 20));
        this.progressBar.setUI(new BasicProgressBarUI());
        
        this.label.setOpaque(true);
        this.label.setBorder(HelperUi.BORDER_FOCUS_GAINED);

        this.icon.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        this.setBackground(Color.WHITE);

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(this.icon);
        this.add(this.loader);
        this.add(this.progressBar);
        this.add(this.label);
        this.add(this.textFieldEditable);
        this.setComponentOrientation(ComponentOrientation.getOrientation(I18n.getLocaleDefault()));

        this.progressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4,3,4,3),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createLineBorder(Color.WHITE)
            )
        ));
        
        this.progressBar.setVisible(false);
        this.loader.setVisible(false);
        this.label.setVisible(false);
        this.icon.setVisible(false);
        this.textFieldEditable.setVisible(false);
        
        this.textFieldEditable.setFont(HelperUi.FONT_SEGOE);
        this.textFieldEditable.setBorder(BorderFactory.createLineBorder(HelperUi.COLOR_FOCUS_GAINED, 1, false));
        this.textFieldEditable.addActionListener(e -> {
            AbstractNodeModel nodeModel = (AbstractNodeModel) currentNode.getUserObject();
            nodeModel.setIsEdited(false);
            
            PanelNode.this.label.setVisible(true);
            PanelNode.this.textFieldEditable.setVisible(false);
            tree.requestFocusInWindow();
            
            nodeModel.getElementDatabase().setElementValue(new String(PanelNode.this.textFieldEditable.getText().getBytes(StandardCharsets.UTF_8)));
            PanelNode.this.label.setText(StringUtil.detectUtf8Html(nodeModel.getElementDatabase().getLabelCount()));
            
            tree.revalidate();
            tree.repaint();
        });
        
        this.textFieldEditable.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                AbstractNodeModel nodeModel = (AbstractNodeModel) currentNode.getUserObject();
                nodeModel.setIsEdited(false);
                tree.revalidate();
                tree.repaint();
            }
            
        });
        
        KeyAdapter keyAdapterF2 = new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                AbstractNodeModel nodeModel = (AbstractNodeModel) currentNode.getUserObject();
                if (e.getKeyCode() == KeyEvent.VK_F2 && !nodeModel.isRunning()) {
                    nodeModel.setIsEdited(true);
                    
                    PanelNode.this.label.setVisible(false);
                    PanelNode.this.textFieldEditable.setVisible(true);
                    PanelNode.this.textFieldEditable.requestFocusInWindow();
                    
                    tree.revalidate();
                    tree.repaint();
                }
            }
            
        };
        
        this.addKeyListener(keyAdapterF2);
        this.textFieldEditable.addKeyListener(keyAdapterF2);

        this.addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e) {
                PanelNode.this.label.setBackground(HelperUi.COLOR_FOCUS_LOST);
                PanelNode.this.label.setBorder(HelperUi.BORDER_FOCUS_LOST);
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                PanelNode.this.label.setBackground(HelperUi.COLOR_FOCUS_GAINED);
                PanelNode.this.label.setBorder(HelperUi.BORDER_FOCUS_GAINED);
            }
            
        });
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
    
    // Getter and setter

    public ProgressBarPausable getProgressBar() {
        return this.progressBar;
    }

    public JLabel getLabel() {
        return this.label;
    }

    public JTextField getEditable() {
        return this.textFieldEditable;
    }
    
}

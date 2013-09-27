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
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.jsql.view.GUITools;
import com.jsql.view.RoundBorder;

/**
 * Render a tree node based on the node model.
 */
public class NodeRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 6713145837575127059L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        Component component = null;

        if ((nodeRenderer != null) && (nodeRenderer instanceof DefaultMutableTreeNode)) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
            if(currentNode != null){
                Object userObject = currentNode.getUserObject();
                if(userObject instanceof NodeModel<?>){
                    NodePanel panel = new NodePanel(tree,currentNode);
                    NodeModel<?> dataModel = (NodeModel<?>) userObject;

                    if(dataModel.isColumn()){
                        JCheckBox checkbox = new JCheckBox(dataModel.toString(), dataModel.isChecked);
                        checkbox.setFont( new Font(checkbox.getFont().getName(),Font.PLAIN|Font.ITALIC,checkbox.getFont().getSize()) );
                        checkbox.setBackground(Color.WHITE);
                        return checkbox;

                    }else if(dataModel.isTable() || dataModel.isDatabase()){
                        panel.label.setText(dataModel.toString());
                        panel.label.setVisible(true);
                        panel.showIcon();

                        if(dataModel.isTable())
                            if(leaf)
                                panel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/view/images/tableGo.png")));
                            else
                                panel.setIcon(GUITools.TABLE);
                        else
                            if(leaf)
                                panel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/view/images/databaseGo.png")));
                            else
                                panel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/view/images/database.png")));

                        if(selected){
                            panel.label.setBackground(GUITools.SELECTION_BACKGROUND);
                        }else{
                            panel.label.setBackground(new Color(255,255,255));
                            panel.label.setBorder(new RoundBorder(4,1,false));
                        }

                        if(dataModel.hasProgress){
                            if(dataModel.isTable() && (dataModel.getParent().toString()).equals("information_schema")){
                                panel.showLoader();

                                if(dataModel.interruptable.isPaused()){
                                    ImageIcon animatedGIFPaused = new IconOverlap(GUITools.PATH_PROGRESSBAR, GUITools.PATH_PAUSE);
                                    animatedGIFPaused.setImageObserver(new AnimatedObserver(tree, currentNode));
                                    panel.setLoaderIcon( animatedGIFPaused );
                                }
                            }else{
                                int dataCount = dataModel.dataObject.getCount();
                                panel.progressBar.setMaximum(dataCount);
                                panel.progressBar.setValue(dataModel.childUpgradeCount);
                                panel.progressBar.setVisible(true);

                                if(dataModel.interruptable.isPaused()){
                                    panel.progressBar.pause();
                                }
                            }
                            panel.hideIcon();
                        }else if(dataModel.hasIndeterminatedProgress){
                            panel.showLoader();
                            panel.hideIcon();

                            if(dataModel.interruptable.isPaused()){
                                ImageIcon animatedGIFPaused = new IconOverlap(GUITools.PATH_PROGRESSBAR, GUITools.PATH_PAUSE);
                                animatedGIFPaused.setImageObserver(new AnimatedObserver(tree, currentNode));
                                panel.setLoaderIcon(animatedGIFPaused);
                            }
                        }
                    }
                    component = panel;
                }else{
                    JPanel emptyPanel = new JPanel(new BorderLayout());
                    JLabel text = new JLabel((String)((DefaultMutableTreeNode)currentNode).getUserObject());
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
                    component = emptyPanel;
                }
            }
        }

        return component;
    }
}

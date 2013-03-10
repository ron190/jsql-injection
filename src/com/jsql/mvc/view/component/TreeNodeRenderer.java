package com.jsql.mvc.view.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeNodeRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 6713145837575127059L;

    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object nodeRenderer,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        
        Component returnValue = null;
        
        if ((nodeRenderer != null) && (nodeRenderer instanceof DefaultMutableTreeNode)) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
            if(currentNode != null){
                Object userObject = currentNode.getUserObject();
                if(userObject instanceof TreeNodeModel<?>){
                    TreeCellCustom c = new TreeCellCustom(tree,currentNode);
                    TreeNodeModel<?> dataModel = (TreeNodeModel<?>) userObject;
    
                    if(dataModel.isColumn()){
                        JCheckBox checkbox = new JCheckBox(dataModel+"", dataModel.isSelected);
                        checkbox.setFont( new Font(checkbox.getFont().getName(),Font.PLAIN|Font.ITALIC,checkbox.getFont().getSize()) );
                        checkbox.setBackground(Color.WHITE);
                        return checkbox;
                        
                    }else if(dataModel.isTable() || dataModel.isDatabase()){
                        c.label.setText(dataModel+"");
                        c.label.setVisible(true);
                        c.icon.setVisible(true);
                        
                        if(dataModel.isTable())
                            if(leaf)
                                c.icon.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/table_go.png")));
                            else
                                c.icon.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/table.png")));
                        else
                            if(leaf)
                                c.icon.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/database_go.png")));
                            else
                                c.icon.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/database.png")));
                        
                        if(selected){
                            c.label.setBackground(new Color(195,214,233));
                        }else{
                            c.label.setBackground(new Color(255,255,255));
                            c.label.setBorder(new RoundedCornerBorder(4,1,false));
                        }
                        
                        if(dataModel.hasProgress){
                            if(dataModel.isTable() && (dataModel.getParent()+"").equals("information_schema")){
                                c.loader.setVisible(true);
                                
                                if(dataModel.interruptable.suspendFlag){
                                    ImageIcon i = new ImageIcon(getClass().getResource("/com/jsql/images/loader2.gif")){
                                        @Override
                                        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                                            super.paintIcon(c, g, x, y);
                                            try {
                                                BufferedImage im2 = ImageIO.read(TreeCellCustom.class.getResource("/com/jsql/images/bullet_pause.png"));
                                                g.drawImage(im2, (this.getIconWidth()-im2.getWidth())/2, (this.getIconHeight()-im2.getHeight())/2, null);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    i.setImageObserver(new NodeImageObserver(tree, currentNode));
                                    c.loader.setIcon( i );
                                }
                            }else{
                                int dataCount = dataModel.dataObject.getCount();
                                c.progressBar.setMaximum(dataCount);
                                c.progressBar.setValue(dataModel.childUpgradeCount);
                                c.progressBar.setVisible(true);
                                
                                if(dataModel.interruptable.suspendFlag){
                                    c.progressBar.showPauseBullet = true;
                                }
                            }
                            c.icon.setVisible(false);
                        }else if(dataModel.hasIndeterminatedProgress){
                            c.loader.setVisible(true);
                            c.icon.setVisible(false);
                            
                            if(dataModel.interruptable.suspendFlag){
                                ImageIcon i = new ImageIcon(getClass().getResource("/com/jsql/images/loader2.gif")){
                                    @Override
                                    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                                        super.paintIcon(c, g, x, y);
                                        try {
                                            BufferedImage im2 = ImageIO.read(TreeCellCustom.class.getResource("/com/jsql/images/bullet_pause.png"));
                                            g.drawImage(im2, (this.getIconWidth()-im2.getWidth())/2, (this.getIconHeight()-im2.getHeight())/2, null);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                i.setImageObserver(new NodeImageObserver(tree, currentNode));
                                c.loader.setIcon( i );
                            }
                        }
                    }
                    returnValue = c;
                }else{
                    JPanel l = new JPanel(new BorderLayout());
                    l.setBorder(null);
                    JLabel m = new JLabel((String)((DefaultMutableTreeNode)currentNode).getUserObject());
                    l.add(m);
//                    m.setBorder()
                    m.setBorder(new RoundedCornerBorder(4,1,false));
                    if( (currentNode != null) && (currentNode instanceof DefaultMutableTreeNode)){
                        if( selected )
                          {
                                l.setBackground( new Color(195,214,233) );
                                m.setBorder(new RoundedCornerBorder(4,1,true));
                          }else
                              l.setBackground( Color.white );
                        if(hasFocus)
                            m.setBorder(new RoundedCornerBorder(4,1,true));
                        else
                            m.setBorder(new RoundedCornerBorder(4,1,false));
                    }
                    returnValue = l;
                }
            }
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree, nodeRenderer, selected, expanded,
                    leaf, row, hasFocus);
        }
        return returnValue;
    }
}
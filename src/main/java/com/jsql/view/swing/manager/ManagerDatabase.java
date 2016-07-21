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
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.combomenu.ArrowIcon;
import com.jsql.view.swing.combomenu.BlankIcon;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tree.CellEditorNode;
import com.jsql.view.swing.tree.CellRendererNode;
import com.jsql.view.swing.tree.model.NodeModelEmpty;

/**
 * Manager to code/uncode string in various methods.
 */
@SuppressWarnings("serial")
public class ManagerDatabase extends JPanel {

    public JMenu panelVendor;
    
    public JMenu panelStrategy;
    
    /**
     * Create a panel to encode a string.
     */
    public ManagerDatabase() {
        super(new BorderLayout());

        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty(I18n.valueByKey("NO_DATABASE")));
        final JTree tree = new JTree(root);
        MediatorGui.register(tree);

        // Graphic manager for components
        tree.setCellRenderer(new CellRendererNode());

        // Action manager for components
        tree.setCellEditor(new CellEditorNode());

        // Tree setting
        // allows repaint nodes
        tree.setEditable(true);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Repaint Gif progressbar
        tree.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if (arg0 != null) {
                    tree.firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY,
                        !tree.isRootVisible(),
                        tree.isRootVisible()
                    );
                    tree.treeDidChange();
                }
            }
            @Override public void treeStructureChanged(TreeModelEvent arg0) {
                // Do nothing
            }
            @Override public void treeNodesRemoved(TreeModelEvent arg0) {
                // Do nothing
            }
            @Override public void treeNodesInserted(TreeModelEvent arg0) {
                // Do nothing
            }
        });

        LightScrollPane scroller = new LightScrollPane(1, 1, 0, 0, tree);
        
//        JPanel p = new JPanel(new BorderLayout());
        
        class ComboMenu extends JMenu {
            ArrowIcon iconRenderer;

            public ComboMenu(String label) {
                super(label);
                iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
                this.setBorderPainted(false);
                this.setIcon(new BlankIcon(null, 11));
                this.setHorizontalTextPosition(JButton.RIGHT);
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension d = this.getPreferredSize();
                int x = Math.max(0, 0 +10);
                int y = Math.max(0, (d.height - iconRenderer.getIconHeight())/2 -2);
                iconRenderer.paintIcon(this,g, x,y);
            }
        }
        
        JMenuBar p2 = new JMenuBar();
        p2.setOpaque(false);
        p2.setBorder(null);
        
        p2.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COMPONENT_BORDER), 
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        panelVendor = new ComboMenu(Vendor.AUTO.toString());
        
        ButtonGroup g = new ButtonGroup();
        
        for (final Vendor vendor: Vendor.values()) {
            JMenuItem i1 = new JRadioButtonMenuItem(vendor.toString(), vendor == Vendor.AUTO);
            i1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panelVendor.setText(vendor.toString());
                    MediatorModel.model().vendorByUser = vendor;
                }
            });
            panelVendor.add(i1);
            g.add(i1);
        }
        
        panelStrategy = new ComboMenu("<Strategy auto>");
        panelStrategy.setEnabled(false);
        
        ButtonGroup g2 = new ButtonGroup();
        
        for (final Strategy strategy: Strategy.values()) {
            if (strategy != Strategy.UNDEFINED) {
                JMenuItem i1 = new JRadioButtonMenuItem(strategy.toString());
                i1.setEnabled(false);
                i1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panelStrategy.setText(strategy.toString());
                        MediatorModel.model().setStrategy(strategy);
                    }
                });
                panelStrategy.add(i1);
                g2.add(i1);
            }
        }
        
        p2.add(panelVendor);
        p2.add(Box.createHorizontalGlue());
        p2.add(panelStrategy);

        this.add(scroller, BorderLayout.CENTER);
        this.add(p2, BorderLayout.SOUTH);
    }
}

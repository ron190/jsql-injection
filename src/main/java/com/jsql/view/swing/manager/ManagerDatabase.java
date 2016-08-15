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
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTree;
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
import com.jsql.view.swing.manager.util.ComboMenu;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tree.CellEditorNode;
import com.jsql.view.swing.tree.CellRendererNode;
import com.jsql.view.swing.tree.model.NodeModelEmpty;

/**
 * Manager to code/uncode string in various methods.
 */
@SuppressWarnings("serial")
public class ManagerDatabase extends JPanel implements Manager {

    public JMenu panelVendor;
    
    public JMenu panelStrategy;
    
    /**
     * Create a panel to encode a string.
     */
    public ManagerDatabase() {
        super(new BorderLayout());

        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty(I18n.valueByKey("DATABASE_EMPTY")));
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
        
        JMenuBar panelLineBottom = new JMenuBar();
        panelLineBottom.setOpaque(false);
        panelLineBottom.setBorder(null);
        panelLineBottom.setPreferredSize(new Dimension(0, 26));
        
        panelLineBottom.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER), 
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        panelVendor = new ComboMenu(Vendor.AUTO.toString());
        
        ButtonGroup groupVendor = new ButtonGroup();
        
        for (final Vendor vendor: Vendor.values()) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == Vendor.AUTO);
            itemRadioVendor.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panelVendor.setText(vendor.toString());
                    MediatorModel.model().vendorByUser = vendor;
                }
            });
            panelVendor.add(itemRadioVendor);
            groupVendor.add(itemRadioVendor);
        }
        
        panelStrategy = new ComboMenu("<Strategy auto>");
        panelStrategy.setEnabled(false);
        
        ButtonGroup groupStrategy = new ButtonGroup();
        
        for (final Strategy strategy: Strategy.values()) {
            if (strategy != Strategy.UNDEFINED) {
                JMenuItem itemRadioStrategy = new JRadioButtonMenuItem(strategy.toString());
                itemRadioStrategy.setEnabled(false);
                itemRadioStrategy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panelStrategy.setText(strategy.toString());
                        MediatorModel.model().setStrategy(strategy);
                    }
                });
                itemRadioStrategy.setToolTipText(I18n.valueByKey("STRATEGY_"+ strategy.name() +"_TOOLTIP"));
                panelStrategy.add(itemRadioStrategy);
                groupStrategy.add(itemRadioStrategy);
            }
        }
        
        panelLineBottom.add(panelVendor);
        panelLineBottom.add(Box.createHorizontalGlue());
        panelLineBottom.add(panelStrategy);

        this.add(scroller, BorderLayout.CENTER);
        this.add(panelLineBottom, BorderLayout.SOUTH);
    }
}

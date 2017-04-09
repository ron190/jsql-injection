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

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTree;
import javax.swing.MenuElement;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.model.injection.strategy.StrategyInjectionError;
import com.jsql.model.injection.vendor.Model.Strategy.Error.Method;
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
    
    JMenu[] itemRadioStrategyError = new JMenu[1];
    
    private ButtonGroup groupStrategy = new ButtonGroup();
    
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

        tree.setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, tree);
        
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
        
        this.panelStrategy = new ComboMenu("<Strategy auto>");
        
        this.itemRadioStrategyError = new JMenu[1];
        
        for (final StrategyInjection strategy: StrategyInjection.values()) {
            if (strategy != StrategyInjection.UNDEFINED) {
                MenuElement itemRadioStrategy;
                
                if (strategy == StrategyInjection.ERRORBASED) {
                    itemRadioStrategy = new JMenu(strategy.toString());
                    this.itemRadioStrategyError[0] = (JMenu) itemRadioStrategy;
                } else {
                    itemRadioStrategy = new JRadioButtonMenuItem(strategy.toString());
                    ((AbstractButton) itemRadioStrategy).addActionListener(actionEvent -> {
                        ManagerDatabase.this.panelStrategy.setText(strategy.toString());
                        MediatorModel.model().setStrategy(strategy);
                    });
                    this.groupStrategy.add((AbstractButton) itemRadioStrategy);
                }
                
                this.panelStrategy.add((JMenuItem) itemRadioStrategy);
                ((JComponent) itemRadioStrategy).setToolTipText(I18n.valueByKey("STRATEGY_"+ strategy.name() +"_TOOLTIP"));
                ((JComponent) itemRadioStrategy).setEnabled(false);
            }
        }
        
        this.panelVendor = new ComboMenu(Vendor.AUTO.toString());
        
        ButtonGroup groupVendor = new ButtonGroup();
        
        for (final Vendor vendor: Vendor.values()) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == Vendor.AUTO);
            itemRadioVendor.addActionListener(actionEvent -> {
                ManagerDatabase.this.panelVendor.setText(vendor.toString());
                MediatorModel.model().setVendorByUser(vendor);
            });
            this.panelVendor.add(itemRadioVendor);
            groupVendor.add(itemRadioVendor);
        }
        
        panelLineBottom.add(this.panelVendor);
        panelLineBottom.add(Box.createHorizontalGlue());
        panelLineBottom.add(this.panelStrategy);

        this.add(scroller, BorderLayout.CENTER);
        this.add(panelLineBottom, BorderLayout.SOUTH);
    }
    
    public void initErrorMethods(Vendor vendor) {
        this.itemRadioStrategyError[0].removeAll();
        
        Integer[] i = {0};
        if (vendor != Vendor.AUTO && vendor.instance().getXmlModel().getStrategy().getError() != null) {
            for (Method methodError: vendor.instance().getXmlModel().getStrategy().getError().getMethod()) {
                JMenuItem itemRadioVendor = new JRadioButtonMenuItem(methodError.getName());
                itemRadioVendor.setEnabled(false);
                this.itemRadioStrategyError[0].add(itemRadioVendor);
                this.groupStrategy.add(itemRadioVendor);
                
                final int indexError = i[0];
                itemRadioVendor.addActionListener(actionEvent -> {
                    ManagerDatabase.this.panelStrategy.setText(methodError.getName());
                    MediatorModel.model().setStrategy(StrategyInjection.ERRORBASED);
                    ((StrategyInjectionError)StrategyInjection.ERRORBASED.instance()).setIndexMethod(indexError);
                });
                
                i[0]++;
            }
        }
    }
    
    // Getter and setter

    public ButtonGroup getGroupStrategy() {
        return groupStrategy;
    }
    
}

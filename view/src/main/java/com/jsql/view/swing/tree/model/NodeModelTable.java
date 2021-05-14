/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree.model;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.database.Table;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.view.swing.menubar.JMenuItemWithMargin;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.tree.CheckBoxMenuItemIconCustom;
import com.jsql.view.swing.tree.ImageObserverAnimated;
import com.jsql.view.swing.tree.ImageOverlap;
import com.jsql.view.swing.tree.PanelNode;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Table model displaying the table icon on the label.
 */
public class NodeModelTable extends AbstractNodeModel {
    
    /**
     * Node as a table model.
     * @param table Element table coming from model
     */
    public NodeModelTable(Table table) {
        
        super(table);
    }

    @Override
    protected Icon getLeafIcon(boolean leaf) {
        
        if (leaf) {
            
            return UiUtil.ICON_TABLE_GO;
            
        } else {
            
            return UiUtil.ICON_TABLE;
        }
    }

    @Override
    protected void displayProgress(PanelNode panelNode, DefaultMutableTreeNode currentNode) {
        
        if ("information_schema".equals(this.getParent().toString())) {
            
            panelNode.showLoader();
            
            AbstractSuspendable suspendableTask = MediatorHelper.model().getMediatorUtils().getThreadUtil().get(this.getElementDatabase());
            
            if (suspendableTask != null && suspendableTask.isPaused()) {
                
                ImageIcon animatedGifPaused = new ImageOverlap(UiUtil.PATH_PROGRESSBAR, UiUtil.PATH_PAUSE);
                animatedGifPaused.setImageObserver(new ImageObserverAnimated(MediatorHelper.treeDatabase(), currentNode));
                panelNode.setLoaderIcon(animatedGifPaused);
            }
            
        } else {
            
            super.displayProgress(panelNode, currentNode);
        }
    }

    @Override
    public void runAction() {
        
        // Prevent double thread run
        if (this.isRunning()) {
            return;
        }
            
        DefaultMutableTreeNode treeNode = MediatorHelper.treeDatabase().getTreeNodeModels().get(this.getElementDatabase());
        treeNode.removeAllChildren();
        
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorHelper.treeDatabase().getModel();
        treeModel.reload(treeNode);
        
        new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                
                Thread.currentThread().setName("SwingWorkerNodeModelTable");
                var selectedTable = (Table) NodeModelTable.this.getElementDatabase();
                
                return MediatorHelper.model().getDataAccess().listColumns(selectedTable);
            }
        }.execute();
        
        this.setRunning(true);
    }

    @Override
    protected void buildMenu(JPopupMenuCustomExtract tablePopupMenu, final TreePath path) {
        
        this.addCheckUncheckItems(tablePopupMenu, path);
        this.addCustomLoadItems(tablePopupMenu);
    }

    private void addCustomLoadItems(JPopupMenuCustomExtract tablePopupMenu) {
        
        var menuCustomLoad = new JMenu("Custom load");
        
        var buttonGroupLoadRows = new ButtonGroup();
        
        JMenuItem menuItemLoadAllRows = new JRadioButtonMenuItem("Load all rows (default)", true);
        JMenuItem menuItemLoadOneRow = new JRadioButtonMenuItem("Load first row only");
        JMenuItem menuItemDump = new JCheckBoxMenuItem("Dump to a file");
        
        var panelCustomFromRow = new JPanel(new BorderLayout());
        final JTextField inputCustomFromRow = new JPopupTextField("no.", "1").getProxy();
        inputCustomFromRow.setHorizontalAlignment(SwingConstants.TRAILING);
        var d = new Dimension(
            (int) inputCustomFromRow.getPreferredSize().getWidth() + 50,
            (int) inputCustomFromRow.getPreferredSize().getHeight()
        );
        inputCustomFromRow.setPreferredSize(d);

        final var radioCustomFromRow = new JCheckBox("<html><pre style=\"font-family:'Segoe UI';padding-left: 1px;\">Load from row no.&#9;</pre></html>");
        radioCustomFromRow.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomFromRow.setIcon(new CheckBoxMenuItemIconCustom());
        radioCustomFromRow.setFocusPainted(false);
        
        panelCustomFromRow.add(radioCustomFromRow, BorderLayout.LINE_START);
        panelCustomFromRow.add(inputCustomFromRow, BorderLayout.CENTER);
        
        var panelCustomToRow = new JPanel(new BorderLayout());
        final JTextField inputCustomToRow = new JPopupTextField("no.", "65565").getProxy();
        inputCustomToRow.setHorizontalAlignment(SwingConstants.TRAILING);
        inputCustomToRow.setPreferredSize(d);

        final var radioCustomToRow = new JCheckBox("<html><pre style=\"font-family:'Segoe UI';padding-left: 1px;\">Load to row no.&#9;&#9;&#9;&#9;&#9;&#9;</pre></html>");
        radioCustomToRow.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomToRow.setIcon(new CheckBoxMenuItemIconCustom());
        radioCustomToRow.setFocusPainted(false);
        
        panelCustomToRow.add(radioCustomToRow, BorderLayout.LINE_START);
        panelCustomToRow.add(inputCustomToRow, BorderLayout.CENTER);
        
        var panelCustomFromChar = new JPanel(new BorderLayout());
        final JTextField inputCustomFromChar = new JPopupTextField("no.", "1").getProxy();
        inputCustomFromChar.setHorizontalAlignment(SwingConstants.TRAILING);
        inputCustomFromChar.setPreferredSize(d);

        final var radioCustomFromChar = new JCheckBox("<html><pre style=\"font-family:'Segoe UI';padding-left: 1px;\">Load from char no.</pre></html>");
        radioCustomFromChar.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomFromChar.setIcon(new CheckBoxMenuItemIconCustom());
        radioCustomFromChar.setFocusPainted(false);
        
        panelCustomFromChar.add(radioCustomFromChar, BorderLayout.LINE_START);
        panelCustomFromChar.add(inputCustomFromChar, BorderLayout.CENTER);
        
        var panelCustomToChar = new JPanel(new BorderLayout());
        final JTextField inputCustomToChar = new JPopupTextField("no.", "65565").getProxy();
        inputCustomToChar.setHorizontalAlignment(SwingConstants.TRAILING);
        inputCustomToChar.setPreferredSize(d);

        final var radioCustomToChar = new JCheckBox("<html><pre style=\"font-family:'Segoe UI';padding-left: 1px;\">Load to char no.&#9;&#9;&#9;&#9;&#9;</pre></html>");
        radioCustomToChar.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomToChar.setIcon(new CheckBoxMenuItemIconCustom());
        radioCustomToChar.setFocusPainted(false);
        
        panelCustomToChar.add(radioCustomToChar, BorderLayout.LINE_START);
        panelCustomToChar.add(inputCustomToChar, BorderLayout.CENTER);

        buttonGroupLoadRows.add(menuItemLoadAllRows);
        buttonGroupLoadRows.add(menuItemLoadOneRow);
      
        menuCustomLoad.add(menuItemLoadAllRows);
        menuCustomLoad.add(menuItemLoadOneRow);
        menuCustomLoad.add(new JSeparator());
        menuCustomLoad.add(panelCustomFromRow);
        menuCustomLoad.add(panelCustomToRow);
        menuCustomLoad.add(panelCustomFromChar);
        menuCustomLoad.add(panelCustomToChar);
        menuCustomLoad.add(new JSeparator());
        menuCustomLoad.add(menuItemDump);
        
        for (JMenuItem menuItem: new JMenuItem[]{menuItemLoadAllRows, menuItemLoadOneRow}) {
            
            menuItem.setUI(
                new BasicRadioButtonMenuItemUI() {
                    
                    @Override
                    protected void doClick(MenuSelectionManager msm) {
                        
                        this.menuItem.doClick(0);
                    }
                }
            );
        }
        
        menuItemDump.setUI(
            new BasicCheckBoxMenuItemUI() {
                
                @Override
                protected void doClick(MenuSelectionManager msm) {
                    
                    this.menuItem.doClick(0);
                }
            }
        );

        tablePopupMenu.add(new JSeparator());
        tablePopupMenu.add(menuCustomLoad);
        
        tablePopupMenu.setButtonGroupLoadRows(buttonGroupLoadRows);
        tablePopupMenu.setRadioCustomFromChar(radioCustomFromChar);
        tablePopupMenu.setRadioCustomToChar(radioCustomToChar);
        tablePopupMenu.setRadioCustomFromRow(radioCustomFromRow);
        tablePopupMenu.setRadioCustomToRow(radioCustomToRow);
    }

    private void addCheckUncheckItems(JPopupMenuCustomExtract tablePopupMenu, final TreePath path) {
        
        JMenuItem menuItemCheckAll = new JMenuItemWithMargin(I18nViewUtil.valueByKey("COLUMNS_CHECK_ALL"), 'C');
        I18nViewUtil.addComponentForKey("COLUMNS_CHECK_ALL", menuItemCheckAll);
        
        JMenuItem menuItemUncheckAll = new JMenuItemWithMargin(I18nViewUtil.valueByKey("COLUMNS_UNCHECK_ALL"), 'U');
        I18nViewUtil.addComponentForKey("COLUMNS_UNCHECK_ALL", menuItemUncheckAll);

        if (!this.isLoaded()) {
            
            menuItemCheckAll.setEnabled(false);
            menuItemUncheckAll.setEnabled(false);
        }

        menuItemCheckAll.addActionListener(new ActionCheckbox(true, path));
        menuItemUncheckAll.addActionListener(new ActionCheckbox(false, path));

        tablePopupMenu.add(new JSeparator());
        tablePopupMenu.add(menuItemCheckAll);
        tablePopupMenu.add(menuItemUncheckAll);
    }
    
    @Override
    public boolean isPopupDisplayable() {
        
        return this.isLoaded() || !this.isLoaded() && this.isRunning();
    }
}

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
package com.jsql.view.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Column;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.ui.RoundBorder;

/**
 * Model adding functional layer to the node ; used by renderer and editor.
 */
public abstract class AbstractNodeModel {
    /**
     * Element from injection model in a linked list.
     */
    protected AbstractElementDatabase dataObject;

    /**
     * Text for empty node.
     */
    private String emptyObject;

    /**
     * Current item injection progress regarding total number of elements.
     */
    public int childUpgradeCount = 0;

    /**
     * Used by checkbox node ; true if checkbox is checked, false otherwise.
     */
    public boolean isChecked = false;

    /**
     * Indicates if process on current node is running.
     */
    public boolean isRunning = false;

    /**
     * True if current table node has checkbox selected, false otherwise.
     * Used to display popup menu and block injection start if no checkbox selected.
     */
    public boolean hasChildChecked = false;

    /**
     * True if current node has already been filled, false otherwise.
     * Used to display correct popup menu and block injection start if already done.
     */
    public boolean hasBeenSearched = false;

    /**
     * True if current node is loading with unknown total number, false otherwise.
     * Used to display gif loader.
     */
    public boolean hasIndeterminatedProgress = false;

    /**
     * True if current node is loading with total number known, false otherwise.
     * Used to display progress bar.
     */
    public boolean hasProgress = false;

    /**
     * Create a functional model for tree node.
     * @param dataObject Database structural component
     */
    public AbstractNodeModel(AbstractElementDatabase dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * Create an empty model for tree node.
     * @param emptyObject Empty tree default node
     */
    public AbstractNodeModel(String emptyObject) {
        this.emptyObject = emptyObject;
    }

    /**
     * Get the database parent of current node.
     * @return Parent
     */
    protected AbstractElementDatabase getParent() {
        return dataObject.getParent();
    }

    /**
     * Display a popup menu for a database or table node.
     * @param currentTableNode Current node
     * @param path Path of current node
     * @param x Popup menu x mouse coordinate
     * @param y Popup menu y mouse coordinate
     */
    public void showPopup(final DefaultMutableTreeNode currentTableNode, TreePath path, int x, int y) {
        JPopupMenu tablePopupMenu = new JPopupMenu();

        JMenuItem mnLoad = new JMenuItem("Load/Stop", 'o');
        JMenuItem mnPause = new JMenuItem("Pause/Resume", 's');
        mnLoad.setIcon(GUITools.EMPTY);
        mnPause.setIcon(GUITools.EMPTY);

        if (!this.hasChildChecked && !this.isRunning) {
            mnLoad.setEnabled(false);
        }
        mnLoad.addActionListener(new ActionLoadStop(this, currentTableNode));

        if (!this.isRunning) {
            mnPause.setEnabled(false);
        }
        mnPause.addActionListener(new ActionPauseUnpause());

        this.displayMenu(tablePopupMenu, path);
        tablePopupMenu.add(mnLoad);
        tablePopupMenu.add(mnPause);

        mnLoad.setIcon(GUITools.EMPTY);
        mnPause.setIcon(GUITools.EMPTY);

        tablePopupMenu.show(GUIMediator.databaseTree(), x, y);
    }

    /**
     * Action to start and stop injection process.
     */
    private class ActionLoadStop implements ActionListener {
        AbstractNodeModel nodeData;
        DefaultMutableTreeNode currentTableNode;

        public ActionLoadStop(AbstractNodeModel nodeData, DefaultMutableTreeNode currentTableNode) {
            this.nodeData = nodeData;
            this.currentTableNode = currentTableNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();
            DefaultMutableTreeNode tableNode = currentTableNode;
            final List<Column> columnsToSearch = new ArrayList<Column>();

            int tableChildCount = treeModel.getChildCount(tableNode);
            for (int i = 0; i < tableChildCount; i++) {
                DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
                if (currentChild.getUserObject() instanceof AbstractNodeModel) {
                    AbstractNodeModel columnTreeNodeModel = (AbstractNodeModel) currentChild.getUserObject();
                    if (columnTreeNodeModel.isChecked) {
                        columnsToSearch.add((Column) columnTreeNodeModel.dataObject);
                    }
                }
            }

            if (!this.nodeData.isRunning && columnsToSearch.isEmpty()) {
                return;
            }

            if (!this.nodeData.isRunning) {
                new SwingWorker<Object, Object>(){

                    @Override
                    protected Object doInBackground() throws Exception {
                        GUIMediator.model().dao.listValues(columnsToSearch);
                        return null;
                    }
                    
                }.execute();
            } else {
                GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).stop();
                GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).unPause();
                this.nodeData.childUpgradeCount = 0;
                this.nodeData.hasIndeterminatedProgress = false;
                this.nodeData.hasProgress = false;
                GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).resume();
            }
            this.nodeData.isRunning = !this.nodeData.isRunning;

            // !!important!!
            GUIMediator.databaseTree().getCellEditor().stopCellEditing();
        }
    }

    /**
     * Action to pause and unpause injection process.
     */
    private class ActionPauseUnpause implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).isPaused()) {
                GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).unPause();
            } else {
                GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).pause();
            }
            
            // Restart the action after an unpause
            if (!GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).isPaused()) {
                GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).resume();
            }

            // !!important!!
            GUIMediator.databaseTree().getCellEditor().stopCellEditing();
            // reload stucked GIF loader
            GUIMediator.databaseTree().repaint();
        }
    }

    /**
     * Draw the panel component based on node model.
     * @param tree
     * @param nodeRenderer
     * @param isSelected
     * @param isExpanded
     * @param isLeaf
     * @param row
     * @param hasFocus
     * @return
     */
    public Component getComponent(final JTree tree, Object nodeRenderer,
            final boolean isSelected, boolean isExpanded, boolean isLeaf, int row,
            boolean hasFocus) {

        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        final NodePanel panel = new NodePanel(tree, currentNode);

        panel.label.setText(this.toString());
        panel.label.setVisible(true);
        panel.showIcon();

        panel.setIcon(this.getLeafIcon(isLeaf));

        panel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                panel.label.setBackground(GUITools.SELECTION_BACKGROUND);
                panel.label.setBorder(new RoundBorder(4, 1, true));
                System.out.println("b");
            }
            @Override
            public void focusLost(FocusEvent arg0) {
                panel.label.setBackground(new Color(248, 249, 249));
                panel.label.setBorder(new RoundBorder(4, 1, new Color(218, 218, 218)));
                System.out.println("a");
            }
        });

//        if(isSelected && list.isFocusOwner())
//            renderer.setBackground(GUITools.SELECTION_BACKGROUND);
//        else if(mouseOver[0] == index)
//            renderer.setBackground(new Color(237,245,255));
//        else if(isSelected && !list.isFocusOwner())
//            renderer.setBackground(new Color(248,249,249));
//        else
//            renderer.setBackground(Color.WHITE);
        
//        if(isSelected && list.isFocusOwner())
//            renderer.setBorder(new LineBorder(new Color(132,172,221), 1, true));
//        else if(mouseOver[0] == index)
//            renderer.setBorder(new LineBorder(new Color(185,215,252), 1, true));
//        else if(isSelected && !list.isFocusOwner())
//            renderer.setBorder(new LineBorder(new Color(218,218,218), 1, true));
//        else if(cellHasFocus)
//            renderer.setBorder(BorderFactory.createCompoundBorder( new AbstractBorder() {
//                @Override
//                public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
//                    Graphics2D gg = (Graphics2D) g;
//                    gg.setColor(Color.GRAY);
//                    gg.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
//                    gg.drawRect(x, y, w - 1, h - 1);
//                }
//            },BorderFactory.createEmptyBorder(0, 1, 0, 0)));
//        else
//            renderer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        if (isSelected) {
            panel.label.setBackground(GUITools.SELECTION_BACKGROUND);
        } else {
            panel.label.setBackground(Color.WHITE);
            panel.label.setBorder(new RoundBorder(4, 1, false));
        }

        if (this.hasProgress) {
            displayProgress(panel, currentNode);
            panel.hideIcon();
        } else if (this.hasIndeterminatedProgress) {
            panel.showLoader();
            panel.hideIcon();

            if (GUIMediator.model().suspendables.get(AbstractNodeModel.this.dataObject).isPaused()) {
                ImageIcon animatedGIFPaused = new IconOverlap(GUITools.PATH_PROGRESSBAR, GUITools.PATH_PAUSE);
                animatedGIFPaused.setImageObserver(new AnimatedObserver(GUIMediator.databaseTree(), currentNode));
                panel.setLoaderIcon(animatedGIFPaused);
            }
        }
        
        return panel;
    }
    
    /**
     * Update progressbar ; dispay the pause icon if node is paused.
     * @param panel Panel that contains the bar to update
     * @param currentNode Functional node model object
     */
    protected void displayProgress(NodePanel panel, DefaultMutableTreeNode currentNode) {
        int dataCount = this.dataObject.getCount();
        panel.progressBar.setMaximum(dataCount);
        panel.progressBar.setValue(this.childUpgradeCount);
        panel.progressBar.setVisible(true);
        
        if (GUIMediator.model().suspendables.get(this.dataObject).isPaused()) {
            panel.progressBar.pause();
        }
    }
    
    /**
     * Display a popupmenu on mouse right click if needed.
     * @param tablePopupMenu Menu to display
     * @param path Treepath of current node
     */
    abstract void displayMenu(JPopupMenu tablePopupMenu, TreePath path);
    
    /**
     * Check if menu should be opened.
     * i.e: does not show menu on database except during injection.
     * @return True if popupup should be opened, false otherwise
     */
    abstract boolean verifyShowPopup();
    
    /**
     * Get icon displayed next to the node text. 
     * @param isLeaf True will display an arrow icon, false won't
     * @return Icon to display
     */
    abstract Icon getLeafIcon(boolean isLeaf);
    
    /**
     * Run injection process (see GUIMediator.model().dao).
     * Used by database and table nodes.
     */
    abstract void runAction();
    
    @Override
    public String toString() {
        return dataObject != null ? this.dataObject.getLabel() : emptyObject;
    }
}

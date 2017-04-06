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
package com.jsql.view.swing.tree.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jsql.i18n.I18n;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.StringUtil;
import com.jsql.util.ThreadUtil;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tree.ActionLoadStop;
import com.jsql.view.swing.tree.ActionPauseUnpause;
import com.jsql.view.swing.tree.ImageObserverAnimated;
import com.jsql.view.swing.tree.ImageOverlap;
import com.jsql.view.swing.tree.PanelNode;

/**
 * Model adding functional layer to the node ; used by renderer and editor.
 */
public abstract class AbstractNodeModel {
	
    /**
     * Element from injection model in a linked list.
     */
    public AbstractElementDatabase elementDatabase;

    /**
     * Text for empty node.
     */
    private String emptyObject;

    /**
     * Current item injection progress regarding total number of elements.
     */
    public int indexProgress = 0;

    /**
     * Used by checkbox node ; true if checkbox is checked, false otherwise.
     */
    public boolean isSelected = false;

    /**
     * Indicates if process on current node is running.
     */
    public boolean isRunning = false;

    /**
     * True if current table node has checkbox selected, false otherwise.
     * Used to display popup menu and block injection start if no checkbox selected.
     */
    public boolean isContainingSelection = false;

    /**
     * True if current node has already been filled, false otherwise.
     * Used to display correct popup menu and block injection start if already done.
     */
    public boolean isLoaded = false;

    /**
     * True if current node is loading with unknown total number, false otherwise.
     * Used to display gif loader.
     */
    public boolean isProgressing = false;

    /**
     * True if current node is loading with total number known, false otherwise.
     * Used to display progress bar.
     */
    public boolean isLoading = false;

    /**
     * Create a functional model for tree node.
     * @param elementDatabase Database structural component
     */
    public AbstractNodeModel(AbstractElementDatabase elementDatabase) {
        this.elementDatabase = elementDatabase;
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
        return elementDatabase.getParent();
    }

    /**
     * Display a popup menu for a database or table node.
     * @param currentTableNode Current node
     * @param path Path of current node
     * @param x Popup menu x mouse coordinate
     * @param y Popup menu y mouse coordinate
     */
    public void showPopup(DefaultMutableTreeNode currentTableNode, TreePath path, MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        AbstractSuspendable<?> suspendableTask = ThreadUtil.get(this.elementDatabase);

        JMenuItem mnLoad = new JMenuItem(
            this.isRunning 
                ? I18n.valueByKey("THREAD_STOP") 
                : I18n.valueByKey("THREAD_LOAD"),
            'o'
        );
        mnLoad.setIcon(HelperUi.ICON_EMPTY);
        
        if (!this.isContainingSelection && !this.isRunning) {
            mnLoad.setEnabled(false);
        }
        mnLoad.addActionListener(new ActionLoadStop(this, currentTableNode));

        JMenuItem mnPause = new JMenuItem(
            // Report #133: ignore if thread not found
            (suspendableTask != null && suspendableTask.isPaused())
                ? I18n.valueByKey("THREAD_RESUME")
                : I18n.valueByKey("THREAD_PAUSE"), 
            's'
        );
        mnPause.setIcon(HelperUi.ICON_EMPTY);

        if (!this.isRunning) {
            mnPause.setEnabled(false);
        }
        mnPause.addActionListener(new ActionPauseUnpause(this, currentTableNode));
        
        popupMenu.add(mnLoad);
        popupMenu.add(mnPause);
        
        JMenuItem mnRestart = new JMenuItem(
            this instanceof NodeModelDatabase ? "Reload tables" :
            this instanceof NodeModelTable ? "Reload columns" : "?"
        );
        mnRestart.setIcon(HelperUi.ICON_EMPTY);

        mnRestart.setEnabled(!this.isRunning);
        mnRestart.addActionListener(actionEvent -> AbstractNodeModel.this.runAction());
        
        popupMenu.add(new JSeparator());
        popupMenu.add(mnRestart);

        this.buildMenu(popupMenu, path);
        
        popupMenu.applyComponentOrientation(ComponentOrientation.getOrientation(I18n.getLocaleDefault()));

        popupMenu.show(
            MediatorGui.treeDatabase(), 
            ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
            ? e.getX() - popupMenu.getWidth()
            : e.getX(), 
            e.getY()
        );
        
        popupMenu.setLocation(
            ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
            ? e.getXOnScreen() - popupMenu.getWidth()
            : e.getXOnScreen(), 
            e.getYOnScreen()
        );
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
    public Component getComponent(
        final JTree tree, Object nodeRenderer, final boolean isSelected, boolean isLeaf, int row,boolean hasFocus
    ) {

        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        final PanelNode panel = new PanelNode(tree, currentNode);

        panel.label.setText(StringUtil.detectUtf8Html(this.toString()));
        panel.label.setVisible(true);
        panel.showIcon();

        panel.setIcon(this.getLeafIcon(isLeaf));

        if (isSelected) {
            panel.label.setBackground(HelperUi.COLOR_SELECTION_BACKGROUND);
        } else {
            panel.label.setBackground(Color.WHITE);
            panel.label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        if (this.isLoading) {
            displayProgress(panel, currentNode);
            panel.hideIcon();
        } else if (this.isProgressing) {
            panel.showLoader();
            panel.hideIcon();

            AbstractSuspendable<?> suspendableTask = ThreadUtil.get(this.elementDatabase);
            if (suspendableTask != null && suspendableTask.isPaused()) {
                ImageIcon animatedGIFPaused = new ImageOverlap(HelperUi.PATH_PROGRESSBAR, HelperUi.PATH_PAUSE);
                animatedGIFPaused.setImageObserver(
                    new ImageObserverAnimated(
                        MediatorGui.treeDatabase(), 
                        currentNode
                    )
                );
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
    protected void displayProgress(PanelNode panel, DefaultMutableTreeNode currentNode) {
        int dataCount = this.elementDatabase.getChildCount();
        panel.progressBar.setMaximum(dataCount);
        panel.progressBar.setValue(this.indexProgress);
        panel.progressBar.setVisible(true);
        
        // Report #135: ignore if thread not found
        AbstractSuspendable<?> suspendableTask = ThreadUtil.get(this.elementDatabase);
        if (suspendableTask != null && suspendableTask.isPaused()) {
            panel.progressBar.pause();
        }
    }
    
    /**
     * Display a popupmenu on mouse right click if needed.
     * @param tablePopupMenu Menu to display
     * @param path Treepath of current node
     */
    abstract void buildMenu(JPopupMenu tablePopupMenu, TreePath path);
    
    /**
     * Check if menu should be opened.
     * i.e: does not show menu on database except during injection.
     * @return True if popupup should be opened, false otherwise
     */
    public abstract boolean isPopupDisplayable();
    
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
    public abstract void runAction();
    
    @Override
    public String toString() {
        return elementDatabase != null ? this.elementDatabase.getLabel() : emptyObject;
    }
    
}

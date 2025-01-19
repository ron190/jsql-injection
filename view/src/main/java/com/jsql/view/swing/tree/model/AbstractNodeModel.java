/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree.model;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.tree.action.ActionLoadStop;
import com.jsql.view.swing.tree.action.ActionPauseUnpause;
import com.jsql.view.swing.tree.ImageOverlap;
import com.jsql.view.swing.tree.PanelNode;
import com.jsql.view.swing.tree.custom.JPopupMenuCustomExtract;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiStringUtil;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Model adding functional layer to the node ; used by renderer and editor.
 */
public abstract class AbstractNodeModel {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final String TREE_BACKGROUND = "Tree.background";

    /**
     * Element from injection model in a linked list.
     */
    private AbstractElementDatabase elementDatabase;

    /**
     * Text for empty node.
     */
    private String textEmptyNode;

    /**
     * Current item injection progress regarding total number of elements.
     */
    private int indexProgress = 0;

    /**
     * Used by checkbox node ; true if checkbox is checked, false otherwise.
     */
    private boolean isSelected = false;

    /**
     * Indicates if process on current node is running.
     */
    private boolean isRunning = false;

    /**
     * True if current table node has checkbox selected, false otherwise.
     * Used to display popup menu and block injection start if no checkbox selected.
     */
    private boolean isAnyCheckboxSelected = false;

    /**
     * True if current node has already been filled, false otherwise.
     * Used to display correct popup menu and block injection start if already done.
     */
    private boolean isLoaded = false;

    /**
     * True if current node is loading with unknown total number, false otherwise.
     * Used to display loader.
     */
    private boolean isProgressing = false;

    /**
     * True if current node is loading with total number known, false otherwise.
     * Used to display progress bar.
     */
    private boolean isLoading = false;
    
    private PanelNode panelNode;

    private boolean isEdited;

    /**
     * Create a functional model for tree node.
     * @param elementDatabase Database structural component
     */
    protected AbstractNodeModel(AbstractElementDatabase elementDatabase) {
        this.elementDatabase = elementDatabase;
    }

    /**
     * Create an empty model for tree node.
     * @param emptyObject Empty tree default node
     */
    protected AbstractNodeModel(String emptyObject) {
        this.textEmptyNode = emptyObject;
    }
    
    /**
     * Display a popupmenu on mouse right click if needed.
     * @param tablePopupMenu Menu to display
     * @param path Treepath of current node
     */
    protected abstract void buildMenu(JPopupMenuCustomExtract tablePopupMenu, TreePath path);
    
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
    protected abstract Icon getLeafIcon(boolean isLeaf);
    
    /**
     * Run injection process (see GUIMediator.model().dao).
     * Used by database and table nodes.
     */
    public abstract void runAction();

    /**
     * Display a popup menu for a database or table node.
     * @param currentTableNode Current node
     * @param path Path of current node
     */
    public void showPopup(DefaultMutableTreeNode currentTableNode, TreePath path, MouseEvent e) {
        var popupMenu = new JPopupMenuCustomExtract();
        AbstractSuspendable suspendableTask = MediatorHelper.model().getMediatorUtils().getThreadUtil().get(this.elementDatabase);

        this.initializeItemLoadPause(currentTableNode, popupMenu, suspendableTask);
        this.initializeItemRenameReload(currentTableNode, path, popupMenu);
        this.buildMenu(popupMenu, path);
        this.displayPopupMenu(e, popupMenu);
    }

    private void displayPopupMenu(MouseEvent e, JPopupMenuCustomExtract popupMenu) {
        popupMenu.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()));

        popupMenu.show(
            MediatorHelper.treeDatabase(),
            ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
            ? e.getX() - popupMenu.getWidth()
            : e.getX(),
            e.getY()
        );
        
        popupMenu.setLocation(
            ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
            ? e.getXOnScreen() - popupMenu.getWidth()
            : e.getXOnScreen(),
            e.getYOnScreen()
        );
    }

    private void initializeItemRenameReload(DefaultMutableTreeNode currentTableNode, TreePath path, JPopupMenuCustomExtract popupMenu) {
        String textReload;
        
        if (this instanceof NodeModelDatabase) {
            textReload = I18nViewUtil.valueByKey("RELOAD_TABLES");
        } else if (this instanceof NodeModelTable) {
            textReload = I18nViewUtil.valueByKey("RELOAD_COLUMNS");
        } else {
            textReload = "?";
        }
        
        JMenuItem menuItemReload = new JMenuItem(textReload);
        menuItemReload.setEnabled(!this.isRunning);
        menuItemReload.addActionListener(actionEvent -> this.runAction());
        
        JMenuItem menuItemRename = new JMenuItem(I18nViewUtil.valueByKey("RENAME_NODE"));
        menuItemRename.setEnabled(!this.isRunning);
        menuItemRename.addActionListener(actionEvent -> {
            AbstractNodeModel nodeModel = (AbstractNodeModel) currentTableNode.getUserObject();
            nodeModel.setIsEdited(true);

            this.getPanel().getNodeLabel().setVisible(false);
            this.getPanel().getTextFieldEditable().setVisible(true);
            
            MediatorHelper.treeDatabase().setSelectionPath(path);
        });
        
        popupMenu.add(new JSeparator());
        popupMenu.add(menuItemRename);
        popupMenu.add(menuItemReload);
    }

    private void initializeItemLoadPause(
        DefaultMutableTreeNode currentTableNode,
        JPopupMenuCustomExtract popupMenu,
        AbstractSuspendable suspendableTask
    ) {
        JMenuItem menuItemLoad = new JMenuItem(
            this.isRunning
            ? I18nViewUtil.valueByKey("THREAD_STOP")
            : I18nViewUtil.valueByKey("THREAD_LOAD"),
            'o'
        );
        if (!this.isAnyCheckboxSelected && !this.isRunning) {
            menuItemLoad.setEnabled(false);
        }
        menuItemLoad.addActionListener(new ActionLoadStop(this, currentTableNode));

        JMenuItem menuItemPause = new JMenuItem(
            // Report #133: ignore if thread not found
            suspendableTask != null && suspendableTask.isPaused()
            ? I18nViewUtil.valueByKey("THREAD_RESUME")
            : I18nViewUtil.valueByKey("THREAD_PAUSE"),
            's'
        );
        if (!this.isRunning) {
            menuItemPause.setEnabled(false);
        }
        menuItemPause.addActionListener(new ActionPauseUnpause(this));
        
        popupMenu.add(menuItemLoad);
        popupMenu.add(menuItemPause);
    }
    
    /**
     * Draw the panel component based on node model.
     */
    public Component getComponent(
        final JTree tree,
        Object nodeRenderer,
        final boolean isSelected,
        boolean isLeaf,
        boolean hasFocus
    ) {
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
        this.panelNode = new PanelNode(tree, currentNode);

        if (isSelected) {
            this.panelNode.setBackground(
                hasFocus
                ? UIManager.getColor("Tree.selectionBackground")
                : UIManager.getColor("Tree.selectionInactiveBackground")
            );  // required for transparency
        } else {
            this.panelNode.setBackground(UIManager.getColor(AbstractNodeModel.TREE_BACKGROUND));  // required for transparency
        }

        this.initializeIcon(isLeaf);
        
        AbstractNodeModel nodeModel = (AbstractNodeModel) currentNode.getUserObject();
        this.initializeEditable(nodeModel.isEdited);
        this.initializeLabel(isSelected, hasFocus, nodeModel.isEdited);
        this.initializeProgress(currentNode);
        
        return this.panelNode;
    }

    private void initializeIcon(boolean isLeaf) {
        this.panelNode.showIcon();
        this.panelNode.setIconNode(this.getLeafIcon(isLeaf));
    }

    private void initializeProgress(DefaultMutableTreeNode currentNode) {
        if (this.isLoading) {
            this.displayProgress(this.panelNode, currentNode);
            this.panelNode.hideIcon();
        } else if (this.isProgressing) {
            this.panelNode.showLoader();
            this.panelNode.hideIcon();

            AbstractSuspendable suspendableTask = MediatorHelper.model().getMediatorUtils().getThreadUtil().get(this.elementDatabase);
            if (suspendableTask != null && suspendableTask.isPaused()) {
                this.panelNode.setLoaderIcon(new ImageOverlap(UiUtil.HOURGLASS.icon, UiUtil.PATH_PAUSE));
            }
        }
    }

    private void initializeLabel(final boolean isSelected, boolean hasFocus, boolean isEdited) {
        // Fix #90521: NullPointerException on setText()
        JLabel nodeLabel = this.panelNode.getNodeLabel();
        try {
            nodeLabel.setText(UiStringUtil.detectUtf8Html(this.toString()));
        } catch (NullPointerException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        nodeLabel.setVisible(!isEdited);

        if (isSelected) {
            if (hasFocus) {
                nodeLabel.setForeground(UIManager.getColor("Tree.selectionForeground"));  // required by macOS light (opposite text color)
                nodeLabel.setBackground(UIManager.getColor("Tree.selectionBackground"));
            } else {
                nodeLabel.setForeground(UIManager.getColor("Tree.selectionInactiveForeground"));  // required by macOS light (opposite text color)
                nodeLabel.setBackground(UIManager.getColor("Tree.selectionInactiveBackground"));
            }
            nodeLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        } else {
            if (hasFocus) {
                nodeLabel.setBackground(UIManager.getColor("Tree.foreground"));  // required by macOS light (opposite text color)
                nodeLabel.setBackground(UIManager.getColor(AbstractNodeModel.TREE_BACKGROUND));
                nodeLabel.setBorder(new LineBorder(UIManager.getColor("Tree.selectionBorderColor"), 1, false));
            } else {
                nodeLabel.setBackground(UIManager.getColor("Tree.foreground"));  // required by macOS light (opposite text color)
                nodeLabel.setBackground(UIManager.getColor(AbstractNodeModel.TREE_BACKGROUND));
                nodeLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }

    private void initializeEditable(boolean isEdited) {
        if (StringUtil.isUtf8(this.getElementDatabase().toString())) {
            this.panelNode.getTextFieldEditable().setFont(UiUtil.FONT_MONO_ASIAN);
        } else {
            this.panelNode.getTextFieldEditable().setFont(UiUtil.FONT_NON_MONO);
        }
        
        this.panelNode.getTextFieldEditable().setText(StringUtil.detectUtf8(this.getElementDatabase().toString()));
        this.panelNode.getTextFieldEditable().setVisible(isEdited);
    }
    
    /**
     * Update progressbar ; display the pause icon if node is paused.
     * @param panelNode Panel that contains the bar to update
     * @param currentNode Functional node model object
     */
    protected void displayProgress(PanelNode panelNode, DefaultMutableTreeNode currentNode) {
        int dataCount = this.elementDatabase.getChildCount();
        panelNode.getProgressBar().setMaximum(dataCount);
        panelNode.getProgressBar().setValue(this.indexProgress);
        panelNode.getProgressBar().setVisible(true);
        
        // Report #135: ignore if thread not found
        AbstractSuspendable suspendableTask = MediatorHelper.model().getMediatorUtils().getThreadUtil().get(this.elementDatabase);
        if (suspendableTask != null && suspendableTask.isPaused()) {
            panelNode.getProgressBar().pause();
        }
    }
    
    @Override
    public String toString() {
        return this.elementDatabase != null ? this.elementDatabase.getLabelWithCount() : this.textEmptyNode;
    }
    
    
    // Getter and setter

    /**
     * Get the database parent of current node.
     * @return Parent
     */
    protected AbstractElementDatabase getParent() {
        return this.elementDatabase.getParent();
    }

    public AbstractElementDatabase getElementDatabase() {
        return this.elementDatabase;
    }

    public void setIndexProgress(int indexProgress) {
        this.indexProgress = indexProgress;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setIsAnyCheckboxSelected(boolean isAnyCheckboxSelected) {
        this.isAnyCheckboxSelected = isAnyCheckboxSelected;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public void setProgressing(boolean isProgressing) {
        this.isProgressing = isProgressing;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public PanelNode getPanel() {
        return this.panelNode;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }
    
    public void setText(String textI18n) {
        this.textEmptyNode = textI18n;
    }
}

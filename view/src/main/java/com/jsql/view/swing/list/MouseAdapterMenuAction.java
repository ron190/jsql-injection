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
package com.jsql.view.swing.list;

import java.awt.ComponentOrientation;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.menubar.JMenuItemWithMargin;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * A Mouse action to display a popupmenu on a JList.
 */
public class MouseAdapterMenuAction extends MouseAdapter {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * JList to add popupmenu.
     */
    private DnDList dndList;
    
    /**
     * Create a popup menu for current JList item.
     * @param dndList List with action
     */
    public MouseAdapterMenuAction(DnDList dndList) {
        this.dndList = dndList;
    }
    
    /**
     * Displays a popup menu for JList.
     * @param mouseEvent Mouse event
     */
    @SuppressWarnings("unchecked")
    public void showPopup(final MouseEvent mouseEvent) {
        
        if (mouseEvent.isPopupTrigger()) {
            
            JList<ItemList> list = (JList<ItemList>) mouseEvent.getSource();

            JPopupMenu popupMenuList = this.initializeMenu(mouseEvent);
            
            popupMenuList.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));

            // Fix #26274: IllegalComponentStateException on show()
            try {
                popupMenuList.show(
                    list,
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                    ? mouseEvent.getX() - popupMenuList.getWidth()
                    : mouseEvent.getX(),
                    mouseEvent.getY()
                );
                
            } catch (IllegalComponentStateException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
            
            popupMenuList.setLocation(
                ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                ? mouseEvent.getXOnScreen() - popupMenuList.getWidth()
                : mouseEvent.getXOnScreen(),
                mouseEvent.getYOnScreen()
            );
        }
    }

    private JPopupMenu initializeMenu(final MouseEvent mouseEvent) {
        
        var popupMenuList = new JPopupMenu();
        
        boolean isAsian = I18nUtil.isAsian(I18nUtil.getLocaleDefault());
        
        JMenuItem mnImport = new JMenuItemWithMargin();
        JMenuItem mnExport = new JMenuItemWithMargin();
        JMenuItem mnCut = new JMenuItemWithMargin();
        JMenuItem mnCopy = new JMenuItemWithMargin();
        JMenuItem mnPaste = new JMenuItemWithMargin();
        JMenuItem mnDelete = new JMenuItemWithMargin();
        JMenuItem mnNew = new JMenuItemWithMargin();
        JMenuItem mnRestoreDefault = new JMenuItemWithMargin();
        JMenuItem mnSelectAll = new JMenuItemWithMargin();
        
        Stream
        .of(
            new SimpleEntry<>(mnImport, "LIST_IMPORT_CONFIRM_TITLE"),
            new SimpleEntry<>(mnExport, "LIST_EXPORT_TITLE"),
            new SimpleEntry<>(mnCut, "LIST_CUT"),
            new SimpleEntry<>(mnCopy, "CONTEXT_MENU_COPY"),
            new SimpleEntry<>(mnPaste, "LIST_PASTE"),
            new SimpleEntry<>(mnDelete, "LIST_DELETE"),
            new SimpleEntry<>(mnNew, "LIST_NEW_VALUE"),
            new SimpleEntry<>(mnRestoreDefault, "LIST_RESTORE_DEFAULT"),
            new SimpleEntry<>(mnSelectAll, "CONTEXT_MENU_SELECT_ALL")
        )
        .forEach(entry -> {
            
            entry.getKey().setText(
                isAsian
                ? I18nViewUtil.valueByKey(entry.getValue())
                : I18nUtil.valueByKey(entry.getValue())
            );
            entry.getKey().setName(entry.getValue());
            
            I18nViewUtil.addComponentForKey(entry.getValue(), entry.getKey());
        });

        mnCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        mnCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        mnPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        mnSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        
        //Create a file chooser
        final var importFileDialog = new JFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());
        importFileDialog.setDialogTitle(I18nUtil.valueByKey("LIST_IMPORT_CONFIRM_TITLE"));
        importFileDialog.setMultiSelectionEnabled(true);

        mnNew.addActionListener(new MenuActionNewValue(this.dndList));

        mnImport.addActionListener(actionEvent -> {
            
            var choice = 0;
            
            // Fix #1896: NullPointerException on showOpenDialog()
            // Fix #42831: ClassCastException on showOpenDialog()
            try {
                choice = importFileDialog.showOpenDialog(this.dndList.getTopLevelAncestor());
                
            } catch (ClassCastException | NullPointerException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
            
            if (choice == JFileChooser.APPROVE_OPTION) {
                
                this.dndList.dropPasteFile(
                    Arrays.asList(importFileDialog.getSelectedFiles()),
                    this.dndList.locationToIndex(mouseEvent.getPoint())
                );
            }
        });

        mnCopy.addActionListener(actionEvent -> {
            
            var action = this.dndList.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
            
            if (action != null) {
                
                action.actionPerformed(
                    new ActionEvent(this.dndList, ActionEvent.ACTION_PERFORMED, null)
                );
            }
        });

        mnCut.addActionListener(actionEvent -> {
            
            var action = this.dndList.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
            
            if (action != null) {
                
                action.actionPerformed(
                    new ActionEvent(this.dndList, ActionEvent.ACTION_PERFORMED, null)
                );
            }
        });

        mnPaste.addActionListener(actionEvent -> {
            
            var action = this.dndList.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
            
            if (action != null) {
                
                action.actionPerformed(
                    new ActionEvent(this.dndList, ActionEvent.ACTION_PERFORMED, null)
                );
            }
        });

        mnDelete.addActionListener(actionEvent -> this.dndList.removeSelectedItem());

        mnExport.addActionListener(new MenuActionExport(this.dndList));

        mnRestoreDefault.addActionListener(actionEvent -> this.dndList.restore());

        mnSelectAll.addActionListener(actionEvent -> {
            
            var start = 0;
            int end = this.dndList.getModel().getSize() - 1;
            
            if (end >= 0) {
                
                this.dndList.setSelectionInterval(start, end);
            }
        });

        popupMenuList.add(mnNew);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(mnCut);
        popupMenuList.add(mnCopy);
        popupMenuList.add(mnPaste);
        popupMenuList.add(mnDelete);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(mnSelectAll);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(mnImport);
        popupMenuList.add(mnExport);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(mnRestoreDefault);
        
        return popupMenuList;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        if (SwingUtilities.isRightMouseButton(e)) {
            
            int clickIndex = this.dndList.locationToIndex(e.getPoint());
            var containsIndex = false;
            
            for (int currentIndex: this.dndList.getSelectedIndices()) {
                
                if (currentIndex == clickIndex) {
                    
                    containsIndex = true;
                    break;
                }
            }
            
            if (!containsIndex) {
                
                this.dndList.setSelectedIndex(clickIndex);
            }
        }
        
        this.showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.showPopup(e);
    }
}

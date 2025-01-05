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
package com.jsql.view.swing.list;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.stream.Stream;

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
    private final DnDList dndList;
    
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
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
            
            popupMenuList.setLocation(
                ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                ? mouseEvent.getXOnScreen() - popupMenuList.getWidth()
                : mouseEvent.getXOnScreen(),
                mouseEvent.getYOnScreen()
            );
        }
    }

    private JPopupMenu initializeMenu(final MouseEvent mouseEvent) {
        var popupMenuList = new JPopupMenu();
        
        boolean isNonUbuntu = I18nViewUtil.isNonUbuntu(I18nUtil.getLocaleDefault());
        
        JMenuItem menuImport = new JMenuItem();
        JMenuItem menuExport = new JMenuItem();
        JMenuItem menuCut = new JMenuItem();
        JMenuItem menuCopy = new JMenuItem();
        JMenuItem menuPaste = new JMenuItem();
        JMenuItem menuDelete = new JMenuItem();
        JMenuItem menuNew = new JMenuItem();
        JMenuItem menuRestoreDefault = new JMenuItem();
        JMenuItem menuSelectAll = new JMenuItem();
        
        Stream.of(
            new SimpleEntry<>(menuImport, "LIST_IMPORT_CONFIRM_TITLE"),
            new SimpleEntry<>(menuExport, "LIST_EXPORT_TITLE"),
            new SimpleEntry<>(menuCut, "LIST_CUT"),
            new SimpleEntry<>(menuCopy, "CONTEXT_MENU_COPY"),
            new SimpleEntry<>(menuPaste, "LIST_PASTE"),
            new SimpleEntry<>(menuDelete, "LIST_DELETE"),
            new SimpleEntry<>(menuNew, "LIST_NEW_VALUE"),
            new SimpleEntry<>(menuRestoreDefault, "LIST_RESTORE_DEFAULT"),
            new SimpleEntry<>(menuSelectAll, "CONTEXT_MENU_SELECT_ALL")
        )
        .forEach(entry -> {
            entry.getKey().setText(
                isNonUbuntu
                ? I18nViewUtil.valueByKey(entry.getValue())
                : I18nUtil.valueByKey(entry.getValue())
            );
            entry.getKey().setName(entry.getValue());
            I18nViewUtil.addComponentForKey(entry.getValue(), entry.getKey());
        });

        menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        
        final var importFileDialog = new JFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());
        importFileDialog.setDialogTitle(I18nUtil.valueByKey("LIST_IMPORT_CONFIRM_TITLE"));
        importFileDialog.setMultiSelectionEnabled(true);

        menuNew.addActionListener(new MenuActionNewValue(this.dndList));

        menuImport.addActionListener(actionEvent -> {
            var choice = 0;
            // Fix #1896: NullPointerException on showOpenDialog()
            // Fix #42831: ClassCastException on showOpenDialog()
            try {
                choice = importFileDialog.showOpenDialog(this.dndList.getTopLevelAncestor());
            } catch (ClassCastException | NullPointerException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
            if (choice == JFileChooser.APPROVE_OPTION) {
                this.dndList.dropPasteFile(
                    Arrays.asList(importFileDialog.getSelectedFiles()),
                    this.dndList.locationToIndex(mouseEvent.getPoint())
                );
            }
        });

        menuCopy.addActionListener(actionEvent -> {
            var action = this.dndList.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
            if (action != null) {
                action.actionPerformed(
                    new ActionEvent(this.dndList, ActionEvent.ACTION_PERFORMED, null)
                );
            }
        });

        menuCut.addActionListener(actionEvent -> {
            var action = this.dndList.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
            if (action != null) {
                action.actionPerformed(
                    new ActionEvent(this.dndList, ActionEvent.ACTION_PERFORMED, null)
                );
            }
        });

        menuPaste.addActionListener(actionEvent -> {
            var action = this.dndList.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
            if (action != null) {
                action.actionPerformed(
                    new ActionEvent(this.dndList, ActionEvent.ACTION_PERFORMED, null)
                );
            }
        });

        menuDelete.addActionListener(actionEvent -> this.dndList.removeSelectedItem());
        menuExport.addActionListener(new MenuActionExport(this.dndList));
        menuRestoreDefault.addActionListener(actionEvent -> this.dndList.restore());
        menuSelectAll.addActionListener(actionEvent -> {
            var start = 0;
            int end = this.dndList.getModel().getSize() - 1;
            if (end >= 0) {
                this.dndList.setSelectionInterval(start, end);
            }
        });

        popupMenuList.add(menuNew);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(menuCut);
        popupMenuList.add(menuCopy);
        popupMenuList.add(menuPaste);
        popupMenuList.add(menuDelete);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(menuSelectAll);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(menuImport);
        popupMenuList.add(menuExport);
        popupMenuList.add(new JSeparator());
        popupMenuList.add(menuRestoreDefault);
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

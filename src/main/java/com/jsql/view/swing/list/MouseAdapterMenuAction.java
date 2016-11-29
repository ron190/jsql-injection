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
package com.jsql.view.swing.list;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.jsql.i18n.I18n;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.swing.HelperUi;

/**
 * A Mouse action to display a popupmenu on a JList.
 */
public class MouseAdapterMenuAction extends MouseAdapter {
    /**
     * JList to add popupmenu.
     */
    private DnDList dndList;
    
    /**
     * Create a popup menu for current JList item.
     * @param dndList List with action
     * @param mouseOver Is JList hovered
     */
    public MouseAdapterMenuAction(DnDList dndList) {
        this.dndList = dndList;
    }
    
    /**
     * Displays a popup menu for JList.
     * @param e Mouse event
     */
    @SuppressWarnings("unchecked")
    public void showPopup(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            JList<ListItem> list = (JList<ListItem>) e.getSource();

            JPopupMenu popupMenuList = new JPopupMenu();

            JMenuItem mnImport = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_IMPORT_CONFIRM_TITLE") +"</span></html>"
                : I18n.valueByKey("LIST_IMPORT_CONFIRM_TITLE")
            );
            I18n.addComponentForKey("LIST_IMPORT_CONFIRM_TITLE", mnImport);
            
            JMenuItem mnExport = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_EXPORT_TITLE") +"</span></html>"
                : I18n.valueByKey("LIST_EXPORT_TITLE")
            );
            I18n.addComponentForKey("LIST_EXPORT_TITLE", mnExport);
            
            JMenuItem mnCut = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_CUT") +"</span></html>"
                : I18n.valueByKey("LIST_CUT")
            );
            I18n.addComponentForKey("LIST_CUT", mnCut);
            
            JMenuItem mnCopy = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("CONTEXT_MENU_COPY") +"</span></html>"
                : I18n.valueByKey("CONTEXT_MENU_COPY")
            );
            I18n.addComponentForKey("CONTEXT_MENU_COPY", mnCopy);
            
            JMenuItem mnPaste = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_PASTE") +"</span></html>"
                : I18n.valueByKey("LIST_PASTE")
            );
            I18n.addComponentForKey("LIST_PASTE", mnPaste);
            
//            JMenuItem mnDelete = new JMenuItem(I18n.valueByKey("LIST_DELETE"));
            JMenuItem mnDelete = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_DELETE") +"</span></html>"
                : I18n.valueByKey("LIST_DELETE")
            );
            I18n.addComponentForKey("LIST_DELETE", mnDelete);
            
//            JMenuItem mnNew = new JMenuItem(I18n.valueByKey("LIST_NEW_VALUE"));
            JMenuItem mnNew = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_NEW_VALUE") +"</span></html>"
                : I18n.valueByKey("LIST_NEW_VALUE")
            );
            I18n.addComponentForKey("LIST_NEW_VALUE", mnNew);
            
            JMenuItem mnRestoreDefault = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("LIST_RESTORE_DEFAULT") +"</span></html>"
                : I18n.valueByKey("LIST_RESTORE_DEFAULT")
            );
            I18n.addComponentForKey("LIST_RESTORE_DEFAULT", mnRestoreDefault);
            
            JMenuItem mnSelectAll = new JMenuItem(
                I18n.getLocaleDefault().getLanguage() == new Locale("zh").getLanguage() ?
                "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey("CONTEXT_MENU_SELECT_ALL") +"</span></html>"
                : I18n.valueByKey("CONTEXT_MENU_SELECT_ALL")
            );
            I18n.addComponentForKey("CONTEXT_MENU_SELECT_ALL", mnSelectAll);
            
            mnImport.setIcon(HelperUi.ICON_EMPTY);
            mnExport.setIcon(HelperUi.ICON_EMPTY);
            mnCut.setIcon(HelperUi.ICON_EMPTY);
            mnCopy.setIcon(HelperUi.ICON_EMPTY);
            mnPaste.setIcon(HelperUi.ICON_EMPTY);
            mnDelete.setIcon(HelperUi.ICON_EMPTY);
            mnNew.setIcon(HelperUi.ICON_EMPTY);
            mnRestoreDefault.setIcon(HelperUi.ICON_EMPTY);
            mnSelectAll.setIcon(HelperUi.ICON_EMPTY);

            mnCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
            mnCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
            mnPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
            mnSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
            
            //Create a file chooser
            final JFileChooser importFileDialog = new JFileChooser(PreferencesUtil.getPathFile());
            importFileDialog.setDialogTitle("Import a list of file paths");
            importFileDialog.setMultiSelectionEnabled(true);

            mnNew.addActionListener(new MenuActionNewValue(dndList));

            mnImport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int choice = importFileDialog.showOpenDialog(dndList.getTopLevelAncestor());
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        dndList.dropPasteFile(
                            Arrays.asList(importFileDialog.getSelectedFiles()), 
                            dndList.locationToIndex(e.getPoint())
                        );
                    }
                }
            });

            mnCopy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = dndList.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
                    if (action != null) {
                        action.actionPerformed(
                            new ActionEvent(dndList, ActionEvent.ACTION_PERFORMED, null)
                        );
                    }
                }
            });

            mnCut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = dndList.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
                    if (action != null) {
                        action.actionPerformed(
                            new ActionEvent(dndList, ActionEvent.ACTION_PERFORMED, null)
                        );
                    }
                }
            });

            mnPaste.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = dndList.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
                    if (action != null) {
                        action.actionPerformed(
                            new ActionEvent(dndList, ActionEvent.ACTION_PERFORMED, null)
                        );
                    }
                }
            });

            mnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    dndList.removeSelectedItem();
                }
            });

            mnExport.addActionListener(new MenuActionExport(dndList));

            mnRestoreDefault.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    dndList.restore();
                }
            });

            mnSelectAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int start = 0;
                    int end = dndList.getModel().getSize() - 1;
                    if (end >= 0) {
                        dndList.setSelectionInterval(start, end);
                    }
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
            
            popupMenuList.applyComponentOrientation(ComponentOrientation.getOrientation(I18n.getLocaleDefault()));

            popupMenuList.show(
                list,
                ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                ? e.getX() - popupMenuList.getWidth()
                : e.getX(), 
                e.getY()
            );
            
            popupMenuList.setLocation(
                ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                ? e.getXOnScreen() - popupMenuList.getWidth()
                : e.getXOnScreen(), 
                e.getYOnScreen()
            );
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int clickIndex = dndList.locationToIndex(e.getPoint());
            boolean containsIndex = false;
            for (int currentIndex: dndList.getSelectedIndices()) {
                if (currentIndex == clickIndex) {
                    containsIndex = true;
                    break;
                }
            }
            if (!containsIndex) {
                dndList.setSelectedIndex(clickIndex);
            }
        }
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }
}

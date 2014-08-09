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
package com.jsql.view.list.dnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;

/**
 * A Mouse action to display a popupmenu on a JList.
 */
public class MouseAdapterMenuAction extends MouseAdapter {
    /**
     * JList to add popupmenu.
     */
    private DnDList myList;
    
    /**
     * True if JList is hovered.
     */
    private int[] mouseOver;
    
    /**
     * Create a popup menu for current JList item.
     * @param myList List with action
     * @param mouseOver Is JList hovered
     */
    public MouseAdapterMenuAction(DnDList myList, int[] mouseOver) {
        this.myList = myList;
        this.mouseOver = mouseOver;
    }
    
    /**
     * Displays a popup menu for JList.
     * @param e Mouse event 
     */
    @SuppressWarnings("unchecked")
    public void showPopup(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            JList<ListItem> list = (JList<ListItem>) e.getSource();

            JPopupMenu tablePopupMenu = new JPopupMenu();

            JMenuItem mnImport = new JMenuItem("Import...");
            JMenuItem mnExport = new JMenuItem("Export...");
            JMenuItem mnCut = new JMenuItem("Cut");
            JMenuItem mnCopy = new JMenuItem("Copy");
            JMenuItem mnPaste = new JMenuItem("Paste");
            JMenuItem mnDelete = new JMenuItem("Delete");
            JMenuItem mnNew = new JMenuItem("New Value(s)...");
            JMenuItem mnRestoreDefault = new JMenuItem("Restore default");
            JMenuItem mnSelectAll = new JMenuItem("Select All");
            
            mnImport.setIcon(GUITools.EMPTY);
            mnExport.setIcon(GUITools.EMPTY);
            mnCut.setIcon(GUITools.EMPTY);
            mnCopy.setIcon(GUITools.EMPTY);
            mnPaste.setIcon(GUITools.EMPTY);
            mnDelete.setIcon(GUITools.EMPTY);
            mnNew.setIcon(GUITools.EMPTY);
            mnRestoreDefault.setIcon(GUITools.EMPTY);
            mnSelectAll.setIcon(GUITools.EMPTY);

            mnCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
            mnCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
            mnPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
            mnSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
            
            //Create a file chooser
            final JFileChooser importFileDialog = new JFileChooser(GUIMediator.model().prefPathFile);
            importFileDialog.setDialogTitle("Import a list of file paths");
            importFileDialog.setMultiSelectionEnabled(true);

            mnNew.addActionListener(new MenuActionNewValue(myList));

            mnImport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int choice = importFileDialog.showOpenDialog(myList.getTopLevelAncestor());
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        myList.dropPasteFile(Arrays.asList(importFileDialog.getSelectedFiles()), myList.locationToIndex(e.getPoint()));
                    }
                }
            });

            mnCopy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action a = myList.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
                    if (a != null) {
                        a.actionPerformed(new ActionEvent(myList, ActionEvent.ACTION_PERFORMED, null));
                    }
                }
            });

            mnCut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = myList.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
                    if (action != null) {
                        action.actionPerformed(new ActionEvent(myList, ActionEvent.ACTION_PERFORMED, null));
                    }
                }
            });

            mnPaste.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = myList.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
                    if (action != null) {
                        action.actionPerformed(new ActionEvent(myList, ActionEvent.ACTION_PERFORMED, null));
                    }
                }
            });

            mnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    myList.removeSelectedItem();
                }
            });

            mnExport.addActionListener(new MenuActionExport(myList));

            mnRestoreDefault.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    myList.listModel.clear();
                    for (String path: myList.defaultList) {
                        myList.listModel.addElement(new ListItem(path));
                    }
                }
            });

            mnSelectAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int start = 0;
                    int end = myList.getModel().getSize() - 1;
                    if (end >= 0) {
                        myList.setSelectionInterval(start, end);
                    }
                }
            });

            tablePopupMenu.add(mnNew);
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(mnCut);
            tablePopupMenu.add(mnCopy);
            tablePopupMenu.add(mnPaste);
            tablePopupMenu.add(mnDelete);
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(mnSelectAll);
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(mnImport);
            tablePopupMenu.add(mnExport);
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(mnRestoreDefault);

            tablePopupMenu.show(list, e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int clickIndex = myList.locationToIndex(e.getPoint());
            boolean containsIndex = false;
            for (int currentIndex: myList.getSelectedIndices()) {
                if (currentIndex == clickIndex) {
                    containsIndex = true;
                    break;
                }
            }
            if (!containsIndex) {
                myList.setSelectedIndex(clickIndex);
            }
        }
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        mouseOver[0] = -1;
        myList.repaint();
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.dnd.list;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundScroller;
import com.jsql.view.component.popup.JPopupTextAreaEditable;

public class MouseAction extends MouseAdapter {
    private DnDList myList;
    private int[] mouseOver;
    private GUI gui;

    public MouseAction(DnDList myList, int[] mouseOver){
        this.myList = myList;
        this.mouseOver = mouseOver;
        this.gui = gui;
    }
    
    public void showPopup(final MouseEvent e) {
        if (e.isPopupTrigger()){
            JList list = (JList)e.getSource();

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
            final JFileChooser importFileDialog = new JFileChooser(myList.gui.model.pathFile);
            importFileDialog.setDialogTitle("Import a list of file paths");
            importFileDialog.setMultiSelectionEnabled(true);

            mnNew.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JPanel panel = new JPanel(new BorderLayout());
                    final JPopupTextAreaEditable textarea = new JPopupTextAreaEditable(6, 50);
                    panel.add(new JLabel("Add new value(s) to the list:"), BorderLayout.NORTH);
                    panel.add(new RoundScroller(textarea));
                    
                    textarea.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            super.mousePressed(e);
                            textarea.requestFocusInWindow();
                        }
                    });

                    int result = JOptionPane.showOptionDialog(myList.getTopLevelAncestor(),
                            panel,
                            "Add Value(s)",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Ok", "Cancel"}, // this is the array
                            "Cancel");

                    if(!textarea.getText().equals("") && result == JOptionPane.YES_OPTION){
                        int lastIndex = 0;
                        if(myList.getSelectedIndex() > 0)
                            lastIndex = myList.getSelectedIndex();

                        int firstIndex = lastIndex;
                        for(String newItem: textarea.getText().split("\\n"))
                            if(!newItem.equals(""))
                                ((DefaultListModel<ListItem>)myList.getModel()).add(lastIndex++, new ListItem(newItem.replace("\\", "/")));

                        myList.setSelectionInterval(firstIndex, lastIndex-1);
                        myList.scrollRectToVisible(
                                myList.getCellBounds(
                                        myList.getMinSelectionIndex(),
                                        myList.getMaxSelectionIndex()
                                        )
                                );

                        textarea.setText(null);
                    }
                }
            });

            mnImport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int choice = importFileDialog.showOpenDialog(myList.getTopLevelAncestor());
                    if (choice == JFileChooser.APPROVE_OPTION)
                        myList.dropPasteFile(Arrays.asList(importFileDialog.getSelectedFiles()), myList.locationToIndex(e.getPoint()));
                }
            });

            mnCopy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action a = myList.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
                    if (a != null)
                        a.actionPerformed(new ActionEvent(myList, ActionEvent.ACTION_PERFORMED, null));
                }
            });

            mnCut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = myList.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
                    if (action != null)
                        action.actionPerformed(new ActionEvent(myList, ActionEvent.ACTION_PERFORMED, null));
                }
            });

            mnPaste.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Action action = myList.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
                    if (action != null)
                        action.actionPerformed(new ActionEvent(myList, ActionEvent.ACTION_PERFORMED, null));
                }
            });

            mnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    myList.remove();
                }
            });

            mnExport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    try {
                        final JFileChooser importFileDialog = new JFileChooser(myList.gui.model.pathFile){
                            private static final long serialVersionUID = 2636947540442342901L;

                            @Override
                            public void approveSelection(){
                                File file = this.getSelectedFile();
                                if(getDialogType() == SAVE_DIALOG)
                                    if(file.exists()){
                                        int replace = JOptionPane.showConfirmDialog(this,
                                                file.getName() + " already exists.\nDo you want to replace it?", "Confirm Export",
                                                JOptionPane.YES_NO_OPTION);
                                        switch(replace){
                                            case JOptionPane.YES_OPTION:
                                                super.approveSelection();
                                                return;
                                            case JOptionPane.NO_OPTION: return;
                                            case JOptionPane.CLOSED_OPTION: return;
                                            case JOptionPane.CANCEL_OPTION:
                                                cancelSelection();
                                                return;
                                        }
                                    }else{
                                        super.approveSelection();
                                    }
                            }
                        };
                        importFileDialog.setDialogTitle("Export list to a file");
                        int choice = importFileDialog.showSaveDialog(myList.getTopLevelAncestor());
                        if (choice != JFileChooser.APPROVE_OPTION) return;

                        PrintStream out = new PrintStream(new FileOutputStream(importFileDialog.getSelectedFile()));
                        int len = myList.getModel().getSize();
                        for(int i = 0; i < len; i++)
                            out.println( myList.getModel().getElementAt(i).toString() );
                        
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            mnRestoreDefault.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    myList.listModel.clear();
                    for(String path: myList.defaultList)
                        myList.listModel.addElement(new ListItem(path));
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

    @Override public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ){
            int clickIndex = myList.locationToIndex(e.getPoint());
            boolean containsIndex = false;
            for(int currentIndex: myList.getSelectedIndices())
                if(currentIndex == clickIndex){
                    containsIndex = true;
                    break;
                }
            if(!containsIndex)
                myList.setSelectedIndex(clickIndex);
        }
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    public void mouseExited(MouseEvent e) {
        mouseOver[0] = -1;
        myList.repaint();
    }
}

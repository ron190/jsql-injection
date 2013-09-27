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

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import com.jsql.view.GUI;

/**
 * A list supporting drag and drop.
 * @param <ListItem>
 */
public class DnDList extends JList {
    private static final long serialVersionUID = -9206830979770165601L;
    
    private JList<ListItem> myList;
    public DefaultListModel<ListItem> listModel;
    GUI gui;
    
    public List<String> defaultList;
    
    public DnDList(GUI gui, List<String> newList){
        this(newList);
        this.gui = gui;
    }
    
    public DnDList(List<String> newList){
        defaultList = newList;

        listModel = new DefaultListModel<ListItem>();

        for(String path: newList)
            listModel.addElement(new ListItem(path));

        myList = this;
        this.setModel(listModel);
        
        final int[] mouseOver = {-1};
        
        myList.addMouseListener(new MouseAction(this, mouseOver));

        // Transform Cut, selects next value
        ActionMap listActionMap = myList.getActionMap();
        listActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction() {
            private static final long serialVersionUID = -8154421201183457014L;

            @Override public void actionPerformed(ActionEvent e) {
                if(myList.getSelectedValuesList().isEmpty()) return;
                
                List<ListItem> selectedValues = myList.getSelectedValuesList();
                List<ListItem> siblings = new ArrayList<ListItem>();
                for(ListItem value:selectedValues){
                    int valueIndex = listModel.indexOf(value);

                    if(valueIndex < listModel.size()-1)
                        siblings.add(listModel.get(valueIndex+1));
                    else if(valueIndex > 0)
                        siblings.add(listModel.get(valueIndex-1));
                }

                TransferHandler.getCutAction().actionPerformed(e);
                for(ListItem sibling:siblings)
                    myList.setSelectedValue(sibling, true);
            }

        });

        listActionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        listActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        ListCellRenderer renderer = new ComplexCellRenderer(myList, mouseOver);
        myList.setCellRenderer(renderer);

        // Allows color change when list loses/gains focus
        myList.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent arg0) {
                myList.repaint();
            }
            @Override
            public void focusGained(FocusEvent arg0) {
                myList.repaint();
            }
        });

        myList.setDragEnabled(true);
        myList.setDropMode(DropMode.INSERT);
        
        // Allows deleting values
        myList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_DELETE)
                    remove();
            }
        });

        // Set Drag and Drop
        myList.setTransferHandler(new ListTransfertHandler(this));
    }

    void remove(){
        if(myList.getSelectedValuesList().isEmpty())return;

        List<ListItem> selectedValues = myList.getSelectedValuesList();
        for(ListItem i:selectedValues){
            int l = listModel.indexOf(i);
            listModel.removeElement(i);
            if(l == listModel.getSize())
                myList.setSelectedIndex(l-1);
            else
                myList.setSelectedIndex(l);
        }
        if(myList.getMinSelectionIndex() > -1 && myList.getMaxSelectionIndex() > -1)
            myList.scrollRectToVisible(
                    myList.getCellBounds(
                            myList.getMinSelectionIndex(),
                            myList.getMaxSelectionIndex()
                            )
                    );
    }

    /**
     * Load a file into the list (drag/drop or copy/paste)
     * @param filesToImport
     * @param position
     */
    void dropPasteFile(List<File> filesToImport, int position){
        final DefaultListModel<ListItem> listModel = (DefaultListModel<ListItem>) myList.getModel();

        if(filesToImport.size() == 0) return;
        try {
            for( Iterator<File> it = filesToImport.iterator(); it.hasNext(); ) {
                File fileToImport = it.next();

                if(Files.probeContentType(fileToImport.toPath())==null || !Files.probeContentType(fileToImport.toPath()).equals("text/plain")){
                    JOptionPane.showMessageDialog(myList.getTopLevelAncestor(),
                            "Unsupported file format.\nPlease import only text/plain files.",
                            "Import Error",
                            JOptionPane.ERROR_MESSAGE,
                            new ImageIcon(getClass().getResource("/com/jsql/view/images/error.png")));
                    return ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] options = {"Replace", "Add", "Cancel"};
        int answer = JOptionPane.showOptionDialog(myList.getTopLevelAncestor(),
                "Replace list or add to current location?",
                "Import file",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);
        
        int startPosition = position;
        int endPosition = startPosition;

        if (answer != JOptionPane.YES_OPTION && answer != JOptionPane.NO_OPTION) return;
        
        if (answer == JOptionPane.YES_OPTION){
            listModel.clear();
            startPosition = 0;
            endPosition = 0;
        }
        
        for( Iterator<File> iterator = (filesToImport).iterator(); iterator.hasNext(); ) {
            BufferedReader fileReader;
            try {
                fileReader = new BufferedReader(new FileReader(iterator.next()));
                String line;
                while((line = fileReader.readLine()) != null)
                    if(!line.equals(""))
                        listModel.add(endPosition++, new ListItem(line.replace("\\", "/")));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if(listModel.size()>0)
            myList.setSelectionInterval(startPosition, endPosition-1);
        
        myList.scrollRectToVisible(
                myList.getCellBounds(
                        myList.getMinSelectionIndex(),
                        myList.getMaxSelectionIndex()
                        )
                );
       
    }
}

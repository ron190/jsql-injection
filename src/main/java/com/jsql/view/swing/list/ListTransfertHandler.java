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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
@SuppressWarnings("serial")
public class ListTransfertHandler extends AbstractListTransfertHandler {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    @Override
    protected String initializeTransferable() {
        
        StringBuilder stringTransferable = new StringBuilder();

        for (ItemList itemPath: this.dragPaths) {
            
            stringTransferable.append(itemPath + "\n");
        }
        
        return stringTransferable.toString();
    }

    @Override
    protected void parseStringDrop(TransferSupport support, DnDList list, DefaultListModel<ItemList> listModel) {
        
        JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
        int childIndex = dropLocation.getIndex();

        List<Integer> listSelectedIndices = new ArrayList<>();

        // DnD from list
        if (this.dragPaths != null && !this.dragPaths.isEmpty()) {
            
            this.addFromList(listModel, childIndex, listSelectedIndices);
            
        } else {
            
            this.addFromOutside(support, listModel, childIndex, listSelectedIndices);
        }

        int[] selectedIndices = new int[listSelectedIndices.size()];
        int i = 0;
        
        for (Integer integer: listSelectedIndices) {
            
            selectedIndices[i] = integer;
            i++;
        }
        
        list.setSelectedIndices(selectedIndices);
    }

    private void addFromOutside(TransferSupport support, DefaultListModel<ItemList> listModel, int childIndex, List<Integer> listSelectedIndices) {
        
        try {
            String importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            
            for (String value: importString.split("\\n")) {
                
                if (StringUtils.isNotEmpty(value)) {
                    
                    listSelectedIndices.add(childIndex);
                    listModel.add(childIndex++, new ItemList(value.replace("\\", "/")));
                }
            }
        } catch (UnsupportedFlavorException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addFromList(DefaultListModel<ItemList> listModel, int childIndex, List<Integer> listSelectedIndices) {
        
        for (ItemList value: this.dragPaths) {
            
            if (StringUtils.isNotEmpty(value.toString())) {
                
                //! FUUuu
                ItemList newValue = new ItemList(value.toString().replace("\\", "/"));
                listSelectedIndices.add(childIndex);
                listModel.add(childIndex++, newValue);
            }
        }
    }

    @Override
    protected List<Integer> initializeStringPaste(String clipboardText, int selectedIndex, DefaultListModel<ItemList> listModel) {
        
        List<Integer> selectedIndexes = new ArrayList<>();

        for (String line: clipboardText.split("\\n")) {
            
            if (StringUtils.isNotEmpty(line)) {
                
                String newLine = line.replace("\\", "/");
                ItemList newItem = new ItemList(newLine);
                selectedIndexes.add(selectedIndex);
                listModel.add(selectedIndex++, newItem);
            }
        }
        
        return selectedIndexes;
    }
}

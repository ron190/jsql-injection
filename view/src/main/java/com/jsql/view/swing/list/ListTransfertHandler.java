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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
@SuppressWarnings("serial")
public class ListTransfertHandler extends AbstractListTransfertHandler {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected String initializeTransferable() {
        
        var stringTransferable = new StringBuilder();

        for (ItemList itemPath: this.dragPaths) {
            
            stringTransferable.append(itemPath + "\n");
        }
        
        return stringTransferable.toString();
    }

    @Override
    protected void parseStringDrop(TransferSupport support, DnDList list, DefaultListModel<ItemList> listModel) {
        
        var dropLocation = (JList.DropLocation) support.getDropLocation();
        int childIndex = dropLocation.getIndex();

        List<Integer> listSelectedIndices = new ArrayList<>();

        // DnD from list
        if (this.dragPaths != null && !this.dragPaths.isEmpty()) {
            
            this.addFromList(listModel, childIndex, listSelectedIndices);
            
        } else {
            
            this.addFromOutside(support, listModel, childIndex, listSelectedIndices);
        }

        var selectedIndices = new int[listSelectedIndices.size()];
        var i = 0;
        
        for (Integer integer: listSelectedIndices) {
            
            selectedIndices[i] = integer;
            i++;
        }
        
        list.setSelectedIndices(selectedIndices);
    }

    private void addFromOutside(TransferSupport support, DefaultListModel<ItemList> listModel, int childIndexFrom, List<Integer> listSelectedIndices) {
        
        try {
            int childIndexTo = childIndexFrom;
            
            var importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            
            for (String value: importString.split("\\n")) {
                
                if (StringUtils.isNotEmpty(value)) {
                    
                    listSelectedIndices.add(childIndexTo);
                    listModel.add(childIndexTo++, new ItemList(value.replace("\\", "/")));
                }
            }
            
        } catch (UnsupportedFlavorException | IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    private void addFromList(DefaultListModel<ItemList> listModel, int childIndexFrom, List<Integer> listSelectedIndices) {
        
        int childIndexTo = childIndexFrom;
        
        for (ItemList value: this.dragPaths) {
            
            if (StringUtils.isNotEmpty(value.toString())) {
                
                //! FUUuu
                var newValue = new ItemList(value.toString().replace("\\", "/"));
                listSelectedIndices.add(childIndexTo);
                listModel.add(childIndexTo++, newValue);
            }
        }
    }

    @Override
    protected List<Integer> initializeStringPaste(String clipboardText, int selectedIndexFrom, DefaultListModel<ItemList> listModel) {
        
        int selectedIndexTo = selectedIndexFrom;

        List<Integer> selectedIndexes = new ArrayList<>();

        for (String line: clipboardText.split("\\n")) {
            
            if (StringUtils.isNotEmpty(line)) {
                
                String newLine = line.replace("\\", "/");
                var newItem = new ItemList(newLine);
                selectedIndexes.add(selectedIndexTo);
                listModel.add(selectedIndexTo++, newItem);
            }
        }
        
        return selectedIndexes;
    }
}

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

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
@SuppressWarnings("serial")
public class ListTransfertHandlerScan extends TransferHandler {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * List of cut/copy/paste/drag/drop items.
     */
    private transient List<ItemList> dragPaths = null;
    
    @Override
    public int getSourceActions(JComponent c) {
        
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        
        DnDList list = (DnDList) c;
        this.dragPaths = list.getSelectedValuesList();
        
        List<JSONObject> jsons = new ArrayList<>();

        StringBuilder stringTransferable = new StringBuilder();
        try {
            for (ItemList itemPath: this.dragPaths) {
                ItemListScan itemScanPath = (ItemListScan) itemPath;
                jsons.add(new JSONObject(itemScanPath.getBeanInjectionToJSON()));
            }
            
            stringTransferable.append(new JSONArray(jsons).toString(4));
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return new StringSelection(stringTransferable.toString().trim());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        
        if (action == TransferHandler.MOVE) {
            
            JList<ItemList> list = (JList<ItemList>) c;
            DefaultListModel<ItemList> model = (DefaultListModel<ItemList>) list.getModel();
            
            for (ItemList itemPath: this.dragPaths) {
                model.remove(model.indexOf(itemPath));
            }
            
            this.dragPaths = null;
        }
    }

    @Override
    public boolean canImport(TransferSupport support) {
        
        return support.isDataFlavorSupported(DataFlavor.stringFlavor)
            || support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        
        if (!this.canImport(support)) {
            return false;
        }

        DnDList list = (DnDList) support.getComponent();
        DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) list.getModel();
        
        //This is a drop
        if (support.isDrop()) {
            
            if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                this.parseStringDrop(support, list, listModel);
            } else if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                this.parseFileDrop(support, list);
            }
        } else {
            
            //This is a paste
            Transferable transferableFromClipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            
            if (transferableFromClipboard != null) {

                if (transferableFromClipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    this.parseStringPaste(list, listModel, transferableFromClipboard);
                } else if (transferableFromClipboard.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    this.parseFilePaste(list, transferableFromClipboard);
                }
            }
        }

        return true;
    }

    private void parseStringDrop(TransferSupport support, DnDList list, DefaultListModel<ItemList> listModel) {
        
        JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
        int indexDropLocation = dropLocation.getIndex();

        List<Integer> listSelectedIndices = new ArrayList<>();

        // DnD from list
        if (this.dragPaths != null && !this.dragPaths.isEmpty()) {
            
            for (ItemList itemPath: this.dragPaths) {
                if (StringUtils.isNotEmpty(itemPath.toString())) {
                    //! FUUuu
                    ItemListScan itemDrag = (ItemListScan) itemPath;
                    ItemListScan itemDrop = new ItemListScan(itemDrag.getBeanInjection());
                    listSelectedIndices.add(indexDropLocation);
                    listModel.add(indexDropLocation++, itemDrop);
                }
            }
        } else {
            
            // DnD from outside
            try {
                String importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                
                for (ItemListScan itemListScan: ListTransfertHandlerScan.parse(importString)) {
                    listSelectedIndices.add(indexDropLocation);
                    listModel.add(indexDropLocation++, itemListScan);
                }
            } catch (UnsupportedFlavorException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        int[] selectedIndices = new int[listSelectedIndices.size()];
        int i = 0;
        for (Integer integer: listSelectedIndices) {
            selectedIndices[i] = integer;
            i++;
        }
        
        list.setSelectedIndices(selectedIndices);
    }

    @SuppressWarnings("unchecked")
    private void parseFileDrop(TransferSupport support, DnDList list) {
        
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
        int childIndex = dl.getIndex();

        try {
            list.dropPasteFile(
                (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor),
                childIndex
            );
        } catch (UnsupportedFlavorException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void parseStringPaste(DnDList list, DefaultListModel<ItemList> listModel, Transferable transferableFromClipboard) {
        
        try {
            String clipboardText = (String) transferableFromClipboard.getTransferData(DataFlavor.stringFlavor);

            int selectedIndex = 0;
            if (list.getSelectedIndex() > 0) {
                selectedIndex = list.getSelectedIndex();
            }
            list.clearSelection();

            List<Integer> selectedIndexes = new ArrayList<>();
            for (ItemListScan itemListScan: ListTransfertHandlerScan.parse(clipboardText)) {
                selectedIndexes.add(selectedIndex);
                listModel.add(selectedIndex++, itemListScan);
            }

            int[] selectedIndexesPasted = new int[selectedIndexes.size()];
            int i = 0;
            for (Integer integer : selectedIndexes) {
                selectedIndexesPasted[i] = integer;
                i++;
            }
            
            list.setSelectedIndices(selectedIndexesPasted);
            list.scrollRectToVisible(
                list.getCellBounds(
                    list.getMinSelectionIndex(),
                    list.getMaxSelectionIndex()
                )
            );
        } catch (NullPointerException | UnsupportedFlavorException | IOException e) {
            // Fix #8831: Multiple Exception on scrollRectToVisible()
            LOGGER.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseFilePaste(DnDList list, Transferable transferableFromClipboard) {
        
        try {
            int selectedIndex = 0;
            if (list.getSelectedIndex() > 0) {
                selectedIndex = list.getSelectedIndex();
            }
            list.clearSelection();

            list.dropPasteFile(
                (List<File>) transferableFromClipboard.getTransferData(DataFlavor.javaFileListFlavor),
                selectedIndex
            );
        } catch (UnsupportedFlavorException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    public static List<ItemListScan> parse(String clipboardText) {
        
        List<ItemListScan> itemsParsed = new ArrayList<>();
        
        try {
            parseJsonArray(clipboardText, itemsParsed);
        } catch (JSONException eJsonArray) {
            parseJsonObject(clipboardText, itemsParsed);
        }
        
        return itemsParsed;
    }

    private static void parseJsonArray(String clipboardText, List<ItemListScan> itemsParsed) {
        
        JSONArray itemsJsonArray = new JSONArray(clipboardText);
        
        for (int i = 0; i < itemsJsonArray.length(); i++) {
            
            JSONObject itemJsonObject = itemsJsonArray.getJSONObject(i);
            
            BeanInjection beanInjection = new BeanInjection(
                itemJsonObject.optString("url", StringUtils.EMPTY),
                itemJsonObject.optString("request", StringUtils.EMPTY),
                itemJsonObject.optString("header", StringUtils.EMPTY),
                itemJsonObject.optString("injectionType", StringUtils.EMPTY),
                itemJsonObject.optString("vendor", StringUtils.EMPTY),
                itemJsonObject.optString("requestType", StringUtils.EMPTY)
            );
            
            ItemListScan newItem = new ItemListScan(beanInjection);
            itemsParsed.add(newItem);
        }
    }

    private static void parseJsonObject(String clipboardText, List<ItemListScan> itemsParsed) {
        
        try {
            JSONObject itemsJsonObject = new JSONObject(clipboardText);
            
            BeanInjection beanInjection = new BeanInjection(
                itemsJsonObject.optString("url", StringUtils.EMPTY),
                itemsJsonObject.optString("request", StringUtils.EMPTY),
                itemsJsonObject.optString("header", StringUtils.EMPTY),
                itemsJsonObject.optString("injectionType", StringUtils.EMPTY),
                itemsJsonObject.optString("vendor", StringUtils.EMPTY),
                itemsJsonObject.optString("requestType", StringUtils.EMPTY)
            );
            
            ItemListScan newItem = new ItemListScan(beanInjection);
            itemsParsed.add(newItem);
        } catch (JSONException eJsonObject) {
            
            for (String url: clipboardText.split("\\n")) {
                BeanInjection beanInjection = new BeanInjection(url);
                
                ItemListScan newItem = new ItemListScan(beanInjection);
                itemsParsed.add(newItem);
            }
        }
    }
}

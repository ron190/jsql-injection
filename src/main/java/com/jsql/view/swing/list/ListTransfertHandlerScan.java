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
    private transient List<ListItem> dragPaths = null;
    
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
        for (ListItem itemPath: this.dragPaths) {
            ListItemScan a = (ListItemScan) itemPath;
            jsons.add(new JSONObject(a.getBeanInjection()));
        }
        stringTransferable.append(new JSONArray(jsons).toString(4));

        return new StringSelection(stringTransferable.toString().trim());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == TransferHandler.MOVE) {
            JList<ListItem> list = (JList<ListItem>) c;
            DefaultListModel<ListItem> model = (DefaultListModel<ListItem>) list.getModel();
            for (ListItem itemPath: this.dragPaths) {
                model.remove(model.indexOf(itemPath));
            }
            
            this.dragPaths = null;
        }
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return
            support.isDataFlavorSupported(DataFlavor.stringFlavor)
            || support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
        ;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean importData(TransferSupport support) {
        if (!this.canImport(support)) {
            return false;
        }

        DnDList list = (DnDList) support.getComponent();
        DefaultListModel<ListItem> listModel = (DefaultListModel<ListItem>) list.getModel();
        //This is a drop
        if (support.isDrop()) {
            if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
                int childIndex = dropLocation.getIndex();

                List<Integer> selectAfterDrop = new ArrayList<>();

                // DnD from list
                if (this.dragPaths != null && !this.dragPaths.isEmpty()) {
                    for (ListItem value: this.dragPaths) {
                        if (!"".equals(value.toString())) {
                            //! FUUuu
                            ListItemScan a = (ListItemScan) value;
                            ListItemScan newValue = new ListItemScan(a.getBeanInjection());
                            selectAfterDrop.add(childIndex);
                            listModel.add(childIndex++, newValue);
                        }
                    }
                // DnD from outside
                } else {
                    try {
                        String importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                        
                        for (ListItemScan c: this.parse(importString)) {
                            selectAfterDrop.add(childIndex);
                            listModel.add(childIndex++, c);
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

                //array is the Integer array
                int[] selectedIndices = new int[selectAfterDrop.size()];
                int i = 0;
                for (Integer integer: selectAfterDrop) {
                    selectedIndices[i] = integer.intValue();
                    i++;
                }
                list.setSelectedIndices(selectedIndices);
            } else if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
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
        //This is a paste
        } else {
            Transferable transferableFromClipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferableFromClipboard != null) {
                if (transferableFromClipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        String clipboardText = (String) transferableFromClipboard.getTransferData(DataFlavor.stringFlavor);

                        int selectedIndex = 0;
                        if (list.getSelectedIndex() > 0) {
                            selectedIndex = list.getSelectedIndex();
                        }
                        list.clearSelection();
                        
                        List<Integer> selectedIndexes = new ArrayList<>();
                        for (ListItemScan c: this.parse(clipboardText)) {
                            selectedIndexes.add(selectedIndex);
                            listModel.add(selectedIndex++, c);
                        }

                        int[] selectedIndexesPasted = new int[selectedIndexes.size()];
                        int i = 0;
                        for (Integer integer : selectedIndexes) {
                            selectedIndexesPasted[i] = integer.intValue();
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
                } else if (transferableFromClipboard.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
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
            }
        }

        return true;
    }
    
    private List<ListItemScan> parse(String clipboardText) {
        List<ListItemScan> itemsParsed = new ArrayList<>();
        try {
            JSONArray itemsJsonArray = new JSONArray(clipboardText);
            
            for (int i = 0; i < itemsJsonArray.length(); i++) {
                JSONObject itemJsonObject = itemsJsonArray.getJSONObject(i);
                
                BeanInjection beanInjection = new BeanInjection(
                    itemJsonObject.optString("url"),
                    itemJsonObject.optString("request"),
                    itemJsonObject.optString("header"),
                    itemJsonObject.optString("injectionType"),
                    itemJsonObject.optString("vendor"),
                    itemJsonObject.optString("requestType")
                );
                
                ListItemScan newItem = new ListItemScan(beanInjection);
                itemsParsed.add(newItem);
            }
        } catch (JSONException e) {
            try {
                JSONObject itemsJsonObject = new JSONObject(clipboardText);
                
                BeanInjection beanInjection = new BeanInjection(
                    itemsJsonObject.optString("url"),
                    itemsJsonObject.optString("request"),
                    itemsJsonObject.optString("header"),
                    itemsJsonObject.optString("injectionType"),
                    itemsJsonObject.optString("vendor"),
                    itemsJsonObject.optString("requestType")
                );
                
                ListItemScan newItem = new ListItemScan(beanInjection);
                itemsParsed.add(newItem);
            } catch (JSONException e2) {
                for (String url: clipboardText.split("\\n")) {
                    BeanInjection beanInjection = new BeanInjection(url);
                    
                    ListItemScan newItem = new ListItemScan(beanInjection);
                    itemsParsed.add(newItem);
                }
            }
        }
        
        return itemsParsed;
    }
    
}

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.util.LogLevel;

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
@SuppressWarnings("serial")
public class ListTransfertHandlerScan extends AbstractListTransfertHandler {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected List<Integer> initializeStringPaste(String clipboardText, int selectedIndexFrom, DefaultListModel<ItemList> listModel) {
        
        int selectedIndexTo = selectedIndexFrom;
        
        List<Integer> selectedIndexes = new ArrayList<>();

        for (ItemListScan itemListScan: ListTransfertHandlerScan.parse(clipboardText)) {

            selectedIndexes.add(selectedIndexTo);
            listModel.add(selectedIndexTo++, itemListScan);
        }
        
        return selectedIndexes;
    }

    @Override
    protected String initializeTransferable() {
        
        List<JSONObject> jsons = new ArrayList<>();

        var stringTransferable = new StringBuilder();
        
        try {
            for (ItemList itemPath: this.dragPaths) {
                
                ItemListScan itemScanPath = (ItemListScan) itemPath;
                jsons.add(new JSONObject(itemScanPath.getBeanInjectionToJSON()));
            }
            
            stringTransferable.append(new JSONArray(jsons).toString(4));
            
        } catch (JSONException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
        
        return stringTransferable.toString();
    }

    @Override
    protected void parseStringDrop(TransferSupport support, DnDList list, DefaultListModel<ItemList> listModel) {
        
        var dropLocation = (JList.DropLocation) support.getDropLocation();
        int indexDropLocation = dropLocation.getIndex();

        List<Integer> listSelectedIndices = new ArrayList<>();

        // DnD from list
        if (this.dragPaths != null && !this.dragPaths.isEmpty()) {
            
            for (ItemList itemPath: this.dragPaths) {
                
                if (StringUtils.isNotEmpty(itemPath.toString())) {
                    
                    //! FUUuu
                    ItemListScan itemDrag = (ItemListScan) itemPath;
                    var itemDrop = new ItemListScan(itemDrag.getBeanInjection());
                    listSelectedIndices.add(indexDropLocation);
                    listModel.add(indexDropLocation++, itemDrop);
                }
            }
            
        } else {
            
            // DnD from outside
            try {
                var importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                
                for (ItemListScan itemListScan: ListTransfertHandlerScan.parse(importString)) {
                    
                    listSelectedIndices.add(indexDropLocation);
                    listModel.add(indexDropLocation++, itemListScan);
                }
                
            } catch (UnsupportedFlavorException | IOException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }

        var selectedIndices = new int[listSelectedIndices.size()];
        var i = 0;
        
        for (Integer integer: listSelectedIndices) {
            
            selectedIndices[i] = integer;
            i++;
        }
        
        list.setSelectedIndices(selectedIndices);
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
        
        var itemsJsonArray = new JSONArray(clipboardText);
        
        for (var i = 0; i < itemsJsonArray.length(); i++) {
            
            var itemJsonObject = itemsJsonArray.getJSONObject(i);
            
            var beanInjection = new BeanInjection(
                itemJsonObject.optString("url", StringUtils.EMPTY),
                itemJsonObject.optString("request", StringUtils.EMPTY),
                itemJsonObject.optString("header", StringUtils.EMPTY),
                itemJsonObject.optString("method", StringUtils.EMPTY),
                itemJsonObject.optString("vendor", StringUtils.EMPTY),
                itemJsonObject.optString("requestType", StringUtils.EMPTY)
            );
            
            var newItem = new ItemListScan(beanInjection);
            itemsParsed.add(newItem);
        }
    }

    private static void parseJsonObject(String clipboardText, List<ItemListScan> itemsParsed) {
        
        try {
            var itemsJsonObject = new JSONObject(clipboardText);
            
            var beanInjection = new BeanInjection(
                itemsJsonObject.optString("url", StringUtils.EMPTY),
                itemsJsonObject.optString("request", StringUtils.EMPTY),
                itemsJsonObject.optString("header", StringUtils.EMPTY),
                itemsJsonObject.optString("method", StringUtils.EMPTY),
                itemsJsonObject.optString("vendor", StringUtils.EMPTY),
                itemsJsonObject.optString("requestType", StringUtils.EMPTY)
            );
            
            var newItem = new ItemListScan(beanInjection);
            itemsParsed.add(newItem);
            
        } catch (JSONException e) {
            
            for (String url: clipboardText.split("\\n")) {
                
                var beanInjection = new BeanInjection(url);
                
                var newItem = new ItemListScan(beanInjection);
                itemsParsed.add(newItem);
            }
        }
    }
}

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

import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
public class ListTransfertHandlerScan extends AbstractListTransfertHandler {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected List<Integer> initStringPaste(String clipboardText, int selectedIndexFrom, DefaultListModel<ItemList> listModel) {
        int selectedIndexTo = selectedIndexFrom;
        List<Integer> selectedIndexes = new ArrayList<>();
        for (ItemListScan itemListScan: ListTransfertHandlerScan.parse(clipboardText)) {
            selectedIndexes.add(selectedIndexTo);
            listModel.add(selectedIndexTo++, itemListScan);
        }
        return selectedIndexes;
    }

    @Override
    protected String initTransferable() {
        List<JSONObject> jsons = new ArrayList<>();
        var stringTransferable = new StringBuilder();
        try {
            for (ItemList itemPath: this.dragPaths) {
                ItemListScan itemScanPath = (ItemListScan) itemPath;
                jsons.add(new JSONObject(itemScanPath.getBeanInjectionToJSON()));
            }
            stringTransferable.append(new JSONArray(jsons).toString(4));
        } catch (JSONException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e.getMessage(), e);
        }
        return stringTransferable.toString();
    }

    @Override
    protected void parseStringDrop(TransferSupport support, DnDList list, DefaultListModel<ItemList> listModel) {
        var dropLocation = (JList.DropLocation) support.getDropLocation();
        int indexDropLocation = dropLocation.getIndex();

        List<Integer> listSelectedIndices = new ArrayList<>();

        if (this.dragPaths != null && !this.dragPaths.isEmpty()) {  // DnD from list
            for (ItemList itemPath: this.dragPaths) {
                if (StringUtils.isNotEmpty(itemPath.toString())) {
                    ItemListScan itemDrag = (ItemListScan) itemPath;  //! FUUuu
                    var itemDrop = new ItemListScan(itemDrag.getBeanInjection());
                    listSelectedIndices.add(indexDropLocation);
                    listModel.add(indexDropLocation++, itemDrop);
                }
            }
        } else {  // DnD from outside
            try {
                var importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                for (ItemListScan itemListScan: ListTransfertHandlerScan.parse(importString)) {
                    listSelectedIndices.add(indexDropLocation);
                    listModel.add(indexDropLocation++, itemListScan);
                }
            } catch (UnsupportedFlavorException | IOException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
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
            ListTransfertHandlerScan.parseJsonArray(clipboardText, itemsParsed);
        } catch (JSONException eJsonArray) {
            ListTransfertHandlerScan.parseJsonObject(clipboardText, itemsParsed);
        }
        return itemsParsed;
    }

    private static void parseJsonArray(String clipboardText, List<ItemListScan> itemsParsed) {
        var itemsJsonArray = new JSONArray(clipboardText);
        for (var i = 0; i < itemsJsonArray.length(); i++) {
            var newItem = new ItemListScan(itemsJsonArray.getJSONObject(i));
            itemsParsed.add(newItem);
        }
    }

    private static void parseJsonObject(String clipboardText, List<ItemListScan> itemsParsed) {
        try {
            var newItem = new ItemListScan(new JSONObject(clipboardText));
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

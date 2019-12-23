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

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
@SuppressWarnings("serial")
public class ListTransfertHandler extends TransferHandler {
    
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

        StringBuilder stringTransferable = new StringBuilder();
        for (ItemList itemPath: this.dragPaths) {
            stringTransferable.append(itemPath + "\n");
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
        return
            support.isDataFlavorSupported(DataFlavor.stringFlavor) ||
            support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
        ;
    }

    @SuppressWarnings("unchecked")
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
                JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                int childIndex = dl.getIndex();

                List<Integer> selectAfterDrop = new ArrayList<>();

                // DnD from list
                if (this.dragPaths != null && !this.dragPaths.isEmpty()) {
                    for (ItemList value: this.dragPaths) {
                        if (!"".equals(value.toString())) {
                            //! FUUuu
                            ItemList newValue = new ItemList(value.toString().replace("\\", "/"));
                            selectAfterDrop.add(childIndex);
                            listModel.add(childIndex++, newValue);
                        }
                    }
                } else {
                    // DnD from outside
                    try {
                        String importString = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                        for (String value: importString.split("\\n")) {
                            if (!"".equals(value)) {
                                selectAfterDrop.add(childIndex);
                                listModel.add(childIndex++, new ItemList(value.replace("\\", "/")));
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

                int[] selectedIndices = new int[selectAfterDrop.size()];
                int i = 0;
                for (Integer integer: selectAfterDrop) {
                    selectedIndices[i] = integer;
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
        } else {
            //This is a paste
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
                        for (String line: clipboardText.split("\\n")) {
                            if (!"".equals(line)) {
                                String newLine = line.replace("\\", "/");
                                ItemList newItem = new ItemList(newLine);
                                selectedIndexes.add(selectedIndex);
                                listModel.add(selectedIndex++, newItem);
                            }
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
    
}

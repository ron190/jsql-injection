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
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * Handler for processing cut/copy/paste/drag/drop action on a JList items.
 */
@SuppressWarnings("serial")
public abstract class AbstractListTransfertHandler extends TransferHandler {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * List of cut/copy/paste/drag/drop items.
     */
    protected transient List<ItemList> dragPaths = null;
    
    protected abstract String initializeTransferable();
    
    protected abstract void parseStringDrop(TransferSupport support, DnDList list, DefaultListModel<ItemList> listModel);
    
    protected abstract List<Integer> initializeStringPaste(String clipboardText, int selectedIndex, DefaultListModel<ItemList> listModel);
    
    @Override
    public int getSourceActions(JComponent c) {
        
        return TransferHandler.COPY_OR_MOVE;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        
        DnDList list = (DnDList) c;
        
        this.dragPaths = list.getSelectedValuesList();
        
        var stringTransferable = this.initializeTransferable();

        return new StringSelection(stringTransferable.trim());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        
        if (action == TransferHandler.MOVE) {
            
            JList<ItemList> list = (JList<ItemList>) c;
            DefaultListModel<ItemList> model = (DefaultListModel<ItemList>) list.getModel();
            
            for (ItemList itemPath: this.dragPaths) {
                
                // Unhandled ArrayIndexOutOfBoundsException #56115 on remove()
                try {
                    model.remove(model.indexOf(itemPath));
                    
                } catch (ArrayIndexOutOfBoundsException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
                }
            }
            
            this.dragPaths = null;
        }
    }

    @Override
    public boolean canImport(TransferSupport support) {
        
        return
            support.isDataFlavorSupported(DataFlavor.stringFlavor)
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
            var transferableFromClipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            
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
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    private void parseStringPaste(DnDList list, DefaultListModel<ItemList> listModel, Transferable transferableFromClipboard) {
        
        try {
            String clipboardText = (String) transferableFromClipboard.getTransferData(DataFlavor.stringFlavor);

            var selectedIndexPaste = 0;
            
            if (list.getSelectedIndex() > 0) {
                selectedIndexPaste = list.getSelectedIndex();
            }
            
            list.clearSelection();

            List<Integer> selectedIndexes = this.initializeStringPaste(clipboardText, selectedIndexPaste, listModel);

            var selectedIndexesPasted = new int[selectedIndexes.size()];
            
            var i = 0;
            
            for (Integer selectedIndex: selectedIndexes) {
                
                selectedIndexesPasted[i] = selectedIndex;
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
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseFilePaste(DnDList list, Transferable transferableFromClipboard) {
        
        try {
            var selectedIndex = 0;
            
            if (list.getSelectedIndex() > 0) {
                
                selectedIndex = list.getSelectedIndex();
            }
            
            list.clearSelection();

            list.dropPasteFile(
                (List<File>) transferableFromClipboard.getTransferData(DataFlavor.javaFileListFlavor),
                selectedIndex
            );
            
        } catch (UnsupportedFlavorException | IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}

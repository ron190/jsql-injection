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

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * A list supporting drag and drop.
 */
@SuppressWarnings("serial")
public class DnDList extends JList<ItemList> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Model for the JList.
     */
    protected DefaultListModel<ItemList> listModel;
    
    /**
     * List of default items.
     */
    private transient List<ItemList> defaultList;
    
    /**
     * Create a JList decorated with drag/drop features.
     * @param newList List to decorate
     */
    public DnDList(List<ItemList> newList) {
        
        this.defaultList = newList;

        this.listModel = new DefaultListModel<>();

        for (ItemList path: newList) {
            
            this.listModel.addElement(path);
        }

        this.setModel(this.listModel);

        ListCellRenderer<ItemList> renderer = new RendererComplexCell();
        this.setCellRenderer(renderer);
        
        this.initializeActionMap();
        this.initializeListener();
        
        this.setDragEnabled(true);
        this.setDropMode(DropMode.INSERT);

        // Set Drag and Drop
        this.setTransferHandler(new ListTransfertHandler());
    }

    private void initializeListener() {
        
        this.addMouseListener(new MouseAdapterMenuAction(this));

        // Allows color change when list loses/gains focus
        this.addFocusListener(new FocusListener() {
                
            @Override
            public void focusLost(FocusEvent arg0) {
                
                DnDList.this.repaint();
            }
            
            @Override
            public void focusGained(FocusEvent arg0) {
                
                DnDList.this.repaint();
            }
        });
        
        // Allows deleting values
        this.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent arg0) {
                
                if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
                    
                    DnDList.this.removeSelectedItem();
                }
            }
        });
    }

    private void initializeActionMap() {
        
        // Transform Cut, selects next value
        var listActionMap = this.getActionMap();
        
        listActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (DnDList.this.getSelectedValuesList().isEmpty()) {
                    
                    return;
                }
                
                List<ItemList> selectedValues = DnDList.this.getSelectedValuesList();
                List<ItemList> siblings = new ArrayList<>();
                
                for (ItemList value: selectedValues) {
                    
                    int valueIndex = DnDList.this.listModel.indexOf(value);

                    if (valueIndex < DnDList.this.listModel.size() - 1) {
                        
                        siblings.add(DnDList.this.listModel.get(valueIndex + 1));
                        
                    } else if (valueIndex > 0) {
                        
                        siblings.add(DnDList.this.listModel.get(valueIndex - 1));
                    }
                }

                TransferHandler.getCutAction().actionPerformed(e);
                
                for (ItemList sibling: siblings) {
                    
                    DnDList.this.setSelectedValue(sibling, true);
                }
            }
        });

        listActionMap.put(
            TransferHandler.getCopyAction().getValue(Action.NAME),
            TransferHandler.getCopyAction()
        );
        
        listActionMap.put(
            TransferHandler.getPasteAction().getValue(Action.NAME),
            TransferHandler.getPasteAction()
        );
    }

    /**
     * Delete selected items from the list.
     */
    public void removeSelectedItem() {
        
        if (this.getSelectedValuesList().isEmpty()) {
            
            return;
        }

        List<ItemList> selectedValues = this.getSelectedValuesList();
        
        for (ItemList itemSelected: selectedValues) {
            
            int indexOfItemSelected = this.listModel.indexOf(itemSelected);
            this.listModel.removeElement(itemSelected);
            
            if (indexOfItemSelected == this.listModel.getSize()) {
                
                this.setSelectedIndex(indexOfItemSelected - 1);
                
            } else {
                
                this.setSelectedIndex(indexOfItemSelected);
            }
        }
        
        try {
            var rectangle = this.getCellBounds(
                this.getMinSelectionIndex(),
                this.getMaxSelectionIndex()
            );
            
            if (rectangle != null) {
                
                this.scrollRectToVisible(
                    this.getCellBounds(
                        this.getMinSelectionIndex(),
                        this.getMaxSelectionIndex()
                    )
                );
            }
            
        } catch (NullPointerException e) {
            
            // Report NullPointerException #1571 : manual scroll elsewhere then run action
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    /**
     * Load a file into the list (drag/drop or copy/paste).
     * @param filesToImport
     * @param position
     */
    public void dropPasteFile(final List<File> filesToImport, int position) {
        
        if (filesToImport.isEmpty()) {
            
            return;
        }
        
        for (File fileToImport : filesToImport) {
            
            // Report NoSuchMethodError #1617
            if (
                !FilenameUtils
                .getExtension(fileToImport.getPath())
                .matches("txt|csv|ini")
            ) {
                
                // Fix #42832: ClassCastException on showMessageDialog()
                try {
                    JOptionPane.showMessageDialog(
                        this.getTopLevelAncestor(),
                        I18nUtil.valueByKey("LIST_IMPORT_ERROR_LABEL"),
                        I18nUtil.valueByKey("LIST_IMPORT_ERROR_TITLE"),
                        JOptionPane.ERROR_MESSAGE,
                        UiUtil.ICON_ERROR
                    );
                    
                } catch (ClassCastException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
                }
                
                return;
            }
        }

        var options = new String[] {
            I18nUtil.valueByKey("LIST_IMPORT_CONFIRM_REPLACE"),
            I18nUtil.valueByKey("LIST_IMPORT_CONFIRM_ADD"),
            I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
        };
        
        int answer = JOptionPane.showOptionDialog(
            this.getTopLevelAncestor(),
            I18nUtil.valueByKey("LIST_IMPORT_CONFIRM_LABEL"),
            I18nUtil.valueByKey("LIST_IMPORT_CONFIRM_TITLE"),
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]
        );
        
        int startPosition = position;

        if (answer != JOptionPane.YES_OPTION && answer != JOptionPane.NO_OPTION) {
            
            return;
        }
        
        if (answer == JOptionPane.YES_OPTION) {
            
            this.listModel.clear();
            startPosition = 0;
        }
        
        int startPositionFinal = startPosition;
        
        SwingUtilities.invokeLater(() -> this.addItems(filesToImport, startPositionFinal));
    }

    private void addItems(final List<File> filesToImport, int startPosition) {
        
        int endPosition = startPosition;
        
        for (File file: filesToImport) {
            
            endPosition = this.initializeItems(endPosition, file);
        }
        

        if (!this.listModel.isEmpty()) {
            
            DnDList.this.setSelectionInterval(startPosition, endPosition - 1);
        }
        
        try {
            DnDList.this.scrollRectToVisible(
                DnDList.this.getCellBounds(
                    DnDList.this.getMinSelectionIndex(),
                    DnDList.this.getMaxSelectionIndex()
                )
            );
            
        } catch (NullPointerException e) {
            
            // Report NullPointerException #1571 : manual scroll elsewhere then run action
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
    }

    private int initializeItems(int startPosition, File file) {
        
        int endPosition = startPosition;
        
        try (
            var fileReader = new FileReader(file);
            var bufferedReader = new BufferedReader(fileReader)
        ) {
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                
                if (
                    StringUtils.isNotEmpty(line)
                    // Fix Report #60
                    && 0 <= endPosition
                    && endPosition <= this.listModel.size()
                ) {
                    
                    this.addItem(endPosition++, line);
                }
            }
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
        
        return endPosition;
    }
    
    public void restore() {
        
        this.listModel.clear();
        
        for (ItemList path: this.defaultList) {
            
            this.listModel.addElement(path);
        }
    }
    
    public void addItem(int endPosition, String line) {
        
        this.listModel.add(endPosition, new ItemList(line.replace("\\", "/")));
    }
}

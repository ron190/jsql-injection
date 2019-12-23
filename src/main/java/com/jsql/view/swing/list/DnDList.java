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

import java.awt.Rectangle;
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
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperUi;

/**
 * A list supporting drag and drop.
 */
@SuppressWarnings("serial")
public class DnDList extends JList<ItemList> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Model for the JList.
     */
    private DefaultListModel<ItemList> listModel;
    
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
        
        this.addMouseListener(new MouseAdapterMenuAction(this));

        // Transform Cut, selects next value
        ActionMap listActionMap = this.getActionMap();
        listActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
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

        ListCellRenderer<ItemList> renderer = new RendererComplexCell();
        this.setCellRenderer(renderer);

        // Allows color change when list loses/gains focus
        this.addFocusListener(
            new FocusListener() {
                @Override
                public void focusLost(FocusEvent arg0) {
                    DnDList.this.repaint();
                }
                
                @Override
                public void focusGained(FocusEvent arg0) {
                    DnDList.this.repaint();
                }
            }
        );

        this.setDragEnabled(true);
        this.setDropMode(DropMode.INSERT);
        
        // Allows deleting values
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
                    DnDList.this.removeSelectedItem();
                }
            }
        });

        // Set Drag and Drop
        this.setTransferHandler(new ListTransfertHandler());
    }

    /**
     * Delete selected items from the list.
     */
    void removeSelectedItem() {
        if (this.getSelectedValuesList().isEmpty()) {
            return;
        }

        List<ItemList> selectedValues = this.getSelectedValuesList();
        for (ItemList itemSelected: selectedValues) {
            int l = this.listModel.indexOf(itemSelected);
            this.listModel.removeElement(itemSelected);
            if (l == this.listModel.getSize()) {
                this.setSelectedIndex(l - 1);
            } else {
                this.setSelectedIndex(l);
            }
        }
        
        try {
            Rectangle rectangle = this.getCellBounds(
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
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Load a file into the list (drag/drop or copy/paste).
     * @param filesToImport
     * @param position
     */
    void dropPasteFile(final List<File> filesToImport, int position) {
        if (filesToImport.isEmpty()) {
            return;
        }
        
        for (File fileToImport : filesToImport) {
            // Report NoSuchMethodError #1617
            if (!FilenameUtils.getExtension(fileToImport.getPath()).matches("txt|csv|ini")) {
                // Fix #42832: ClassCastException on showMessageDialog()
                try {
                    JOptionPane.showMessageDialog(
                        this.getTopLevelAncestor(),
                        I18n.valueByKey("LIST_IMPORT_ERROR_LABEL"),
                        I18n.valueByKey("LIST_IMPORT_ERROR_TITLE"),
                        JOptionPane.ERROR_MESSAGE,
                        HelperUi.ICON_ERROR
                    );
                } catch (ClassCastException e) {
                    LOGGER.error(e, e);
                }
                return;
            }
        }

        String[] options = {
            I18n.valueByKey("LIST_IMPORT_CONFIRM_REPLACE"),
            I18n.valueByKey("LIST_IMPORT_CONFIRM_ADD"),
            I18n.valueByKey("LIST_ADD_VALUE_CANCEL")
        };
        int answer = JOptionPane.showOptionDialog(
            this.getTopLevelAncestor(),
            I18n.valueByKey("LIST_IMPORT_CONFIRM_LABEL"),
            I18n.valueByKey("LIST_IMPORT_CONFIRM_TITLE"),
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]
        );
        
        final int[] startPosition = {position};
        final int[] endPosition = {startPosition[0]};

        if (answer != JOptionPane.YES_OPTION && answer != JOptionPane.NO_OPTION) {
            return;
        }
        
        if (answer == JOptionPane.YES_OPTION) {
            this.listModel.clear();
            startPosition[0] = 0;
            endPosition[0] = 0;
        }
        
        SwingUtilities.invokeLater(() -> {
            for (File file : filesToImport) {
                try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        if (
                            !"".equals(line)
                            // Fix Report #60
                            && 0 <= endPosition[0] && endPosition[0] <= this.listModel.size()
                        ) {
                            this.listModel.add(endPosition[0]++, new ItemList(line.replace("\\", "/")));
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            
            if (!this.listModel.isEmpty()) {
                DnDList.this.setSelectionInterval(startPosition[0], endPosition[0] - 1);
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
                LOGGER.error(e.getMessage(), e);
            }
        });
        
    }
    
    public void restore() {
        this.listModel.clear();
        for (ItemList path: this.defaultList) {
            this.listModel.addElement(path);
        }
    }
    
}

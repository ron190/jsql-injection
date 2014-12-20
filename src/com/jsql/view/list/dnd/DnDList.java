/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.list.dnd;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;

/**
 * A list supporting drag and drop.
 */
@SuppressWarnings("serial")
public class DnDList extends JList<ListItem> {
    /**
     * Model for the JList.
     */
    public DefaultListModel<ListItem> listModel;
    
    /**
     * List of default items.
     */
    public List<String> defaultList;
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DnDList.class);

    /**
     * Compatibility method for java 6.
     */
    public List<ListItem> getSelectedValuesList() {
        ListSelectionModel sm = getSelectionModel();
        ListModel<ListItem> dm = getModel();

        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return Collections.emptyList();
        }

        List<ListItem> selectedItems = new ArrayList<ListItem>();
        for (int i = iMin; i <= iMax; i++) {
            if (sm.isSelectedIndex(i)) {
                selectedItems.add(dm.getElementAt(i));
            }
        }
        return selectedItems;
    }
    
    /**
     * Create a JList decorated with drag/drop features.
     * @param newList List to decorate
     */
    public DnDList(List<String> newList) {
        defaultList = newList;

        listModel = new DefaultListModel<ListItem>();

        for (String path: newList) {
            listModel.addElement(new ListItem(path));
        }

        this.setModel(listModel);
        
//        final int[] mouseOver = {-1};
        
//        this.addMouseListener(new MouseAdapterMenuAction(this, mouseOver));
        this.addMouseListener(new MouseAdapterMenuAction(this));

        // Transform Cut, selects next value
        ActionMap listActionMap = this.getActionMap();
        listActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (DnDList.this.getSelectedValuesList().isEmpty()) {
                    return;
                }
                
                List<ListItem> selectedValues = DnDList.this.getSelectedValuesList();
                List<ListItem> siblings = new ArrayList<ListItem>();
                for (ListItem value: selectedValues) {
                    int valueIndex = listModel.indexOf(value);

                    if (valueIndex < listModel.size() - 1) {
                        siblings.add(listModel.get(valueIndex + 1));
                    } else if (valueIndex > 0) {
                        siblings.add(listModel.get(valueIndex - 1));
                    }
                }

                TransferHandler.getCutAction().actionPerformed(e);
                for (ListItem sibling: siblings) {
                    DnDList.this.setSelectedValue(sibling, true);
                }
            }

        });

        listActionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        listActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

//        ListCellRenderer<ListItem> renderer = new RendererComplexCell(mouseOver);
        ListCellRenderer<ListItem> renderer = new RendererComplexCell();
        this.setCellRenderer(renderer);

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

        List<ListItem> selectedValues = this.getSelectedValuesList();
        for (ListItem i: selectedValues) {
            int l = listModel.indexOf(i);
            listModel.removeElement(i);
            if (l == listModel.getSize()) {
                this.setSelectedIndex(l - 1);
            } else {
                this.setSelectedIndex(l);
            }
        }
        if (this.getMinSelectionIndex() > -1 && this.getMaxSelectionIndex() > -1) {
            this.scrollRectToVisible(
                this.getCellBounds(
                    this.getMinSelectionIndex(),
                    this.getMaxSelectionIndex()
                )
            );
        }
    }

    /**
     * Load a file into the list (drag/drop or copy/paste).
     * @param filesToImport
     * @param position
     */
    void dropPasteFile(List<File> filesToImport, int position) {
        if (filesToImport.isEmpty()) {
            return;
        }
        try {
            for (Iterator<File> it = filesToImport.iterator(); it.hasNext();) {
                File fileToImport = it.next();

                if (Files.probeContentType(fileToImport.toPath()) == null
                        || !"text/plain".equals(Files.probeContentType(fileToImport.toPath()))) {
                    JOptionPane.showMessageDialog(
                        this.getTopLevelAncestor(),
                        I18n.LIST_IMPORT_ERROR_TEXT,
                        I18n.LIST_IMPORT_ERROR,
                        JOptionPane.ERROR_MESSAGE,
                        new ImageIcon(getClass().getResource("/com/jsql/view/images/error.png"))
                    );
                    return;
                }
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        String[] options = {I18n.REPLACE, I18n.ADD, I18n.CANCEL};
        int answer = JOptionPane.showOptionDialog(
            this.getTopLevelAncestor(),
            I18n.LIST_IMPORT_REPLACE,
            I18n.LIST_IMPORT,
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]
        );
        
        int startPosition = position;
        int endPosition = startPosition;

        if (answer != JOptionPane.YES_OPTION && answer != JOptionPane.NO_OPTION) {
            return;
        }
        
        if (answer == JOptionPane.YES_OPTION) {
            listModel.clear();
            startPosition = 0;
            endPosition = 0;
        }
        
        for (Iterator<File> iterator = (filesToImport).iterator(); iterator.hasNext();) {
            BufferedReader fileReader;
            try {
                fileReader = new BufferedReader(new FileReader(iterator.next()));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    if (!"".equals(line)) {
                        listModel.add(endPosition++, new ListItem(line.replace("\\", "/")));
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.error(e, e);
            } catch (IOException e) {
                LOGGER.error(e, e);
            }
        }
        
        if (!listModel.isEmpty()) {
            this.setSelectionInterval(startPosition, endPosition - 1);
        }
        
        this.scrollRectToVisible(
            this.getCellBounds(this.getMinSelectionIndex(), this.getMaxSelectionIndex())
        );
        
    }
}

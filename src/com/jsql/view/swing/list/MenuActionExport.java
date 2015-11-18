/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.MediatorGUI;

/**
 * Action to export a JList.
 */
public class MenuActionExport implements ActionListener {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(MenuActionExport.class);

    /**
     * List to export.
     */
    private DnDList myList;
    
    /**
     * Create action to export a list.
     * @param myList List to export.
     */
    public MenuActionExport(DnDList myList) {
        super();
        this.myList = myList;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        try {
            @SuppressWarnings("serial")
            final JFileChooser importFileDialog = new JFileChooser(MediatorGUI.model().prefPathFile) {
                @Override
                public void approveSelection() {
                    File file = this.getSelectedFile();
                    if (getDialogType() == SAVE_DIALOG) {
                        if (file.exists()) {
                            int replace = JOptionPane.showConfirmDialog(this,
                                    file.getName() + " " +I18n.LIST_CONFIRM_REPLACE, I18n.LIST_CONFIRM_EXPORT,
                                    JOptionPane.YES_NO_OPTION);
                            switch (replace) {
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION: return;
                                case JOptionPane.CLOSED_OPTION: return;
                                case JOptionPane.CANCEL_OPTION:
                                    cancelSelection();
                                    return;
                                default:
                                    break;
                            }
                        } else {
                            super.approveSelection();
                        }
                    }
                }
            };
            importFileDialog.setDialogTitle(I18n.LIST_EXPORT);
            int choice = importFileDialog.showSaveDialog(myList.getTopLevelAncestor());
            if (choice != JFileChooser.APPROVE_OPTION) {
                return;
            }

            PrintStream out = new PrintStream(new FileOutputStream(importFileDialog.getSelectedFile()));
            int len = myList.getModel().getSize();
            for (int i = 0; i < len; i++) {
                out.println(myList.getModel().getElementAt(i).toString());
            }
            out.close();
        } catch (FileNotFoundException e) {
            LOGGER.error(e, e);
        }
    }
}

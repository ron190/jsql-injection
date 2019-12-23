/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;

/**
 * Action to export a JList.
 */
public class MenuActionExport implements ActionListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * List to export.
     */
    private DnDList myList;
    
    /**
     * Create action to export a list.
     * @param myList List to export.
     */
    public MenuActionExport(DnDList myList) {
        this.myList = myList;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        @SuppressWarnings("serial")
        final JFileChooser importFileDialog = new JFileChooser(MediatorModel.model().getMediatorUtils().getPreferencesUtil().getPathFile()) {
            @Override
            public void approveSelection() {
                File file = this.getSelectedFile();
                if (file.exists() && this.getDialogType() == JFileChooser.SAVE_DIALOG) {
                    int replace = JOptionPane.showConfirmDialog(
                        this,
                        file.getName() +" "+ I18n.valueByKey("LIST_EXPORT_CONFIRM_LABEL"),
                        I18n.valueByKey("LIST_EXPORT_CONFIRM_TITLE"),
                        JOptionPane.YES_NO_OPTION
                    );
                    switch (replace) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            this.cancelSelection();
                            return;
                        default:
                            break;
                    }
                } else {
                    super.approveSelection();
                }
            }
        };
        importFileDialog.setDialogTitle(I18n.valueByKey("LIST_EXPORT_TITLE"));
        int choice = importFileDialog.showSaveDialog(this.myList.getTopLevelAncestor());
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (
            FileOutputStream file = new FileOutputStream(importFileDialog.getSelectedFile())
        ) {
            PrintStream out = new PrintStream(file);
            int len = this.myList.getModel().getSize();
            for (int i = 0 ; i < len ; i++) {
                out.println(this.myList.getModel().getElementAt(i).toString());
            }
            out.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
}

/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.list;

import com.formdev.flatlaf.util.SystemFileChooser;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Action to export a JList.
 */
public class MenuActionExport implements ActionListener {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * List to export.
     */
    private final DnDList myList;
    
    /**
     * Create action to export a list.
     * @param myList List to export.
     */
    public MenuActionExport(DnDList myList) {
        this.myList = myList;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        final SystemFileChooser importFileDialog = new SystemFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());

        importFileDialog.setDialogTitle(I18nUtil.valueByKey("LIST_EXPORT_TITLE"));
        int choice = importFileDialog.showSaveDialog(this.myList.getTopLevelAncestor());
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (
            var file = new FileOutputStream(importFileDialog.getSelectedFile());
            var out = new PrintStream(file, false, StandardCharsets.UTF_8)
        ) {
            int len = this.myList.getModel().getSize();
            for (var i = 0 ; i < len ; i++) {
                out.println(this.myList.getModel().getElementAt(i).toString());
            }
            LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "List saved to {}", importFileDialog.getSelectedFile());
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
}

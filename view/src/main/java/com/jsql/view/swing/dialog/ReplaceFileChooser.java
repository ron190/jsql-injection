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
package com.jsql.view.swing.dialog;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;

/**
 * File chooser for supporting 'file already exists'.
 */
public class ReplaceFileChooser extends JFileChooser {
    
    /**
     * Create a file chooser with a replace confirm dialog.
     */
    public ReplaceFileChooser(String currentDirectoryPath) {
        // Unhandled NoSuchMethodError #82561 on constructor: NoSuchMethodError
        // Unhandled InternalError #93015 on constructor: InvocationTargetException
        super(currentDirectoryPath);
    }
    
    @Override
    public void approveSelection() {
        if (this.getDialogType() == SAVE_DIALOG) {
            var file = this.getSelectedFile();
            if (file.exists()) {
                int result = JOptionPane.showConfirmDialog(
                    MediatorHelper.frame(),
                    String.format(
                        "%s %s",
                        this.getSelectedFile().getName(),
                        I18nUtil.valueByKey("SAVE_TAB_CONFIRM_LABEL")
                    ),
                    I18nUtil.valueByKey("SAVE_TAB_CONFIRM_TITLE"),
                    JOptionPane.YES_NO_OPTION
                );
                switch (result) {
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
    }
}

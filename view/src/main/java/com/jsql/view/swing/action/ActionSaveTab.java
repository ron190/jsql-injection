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
package com.jsql.view.swing.action;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.dialog.ReplaceFileChooser;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;

/**
 * Save the content of tab in a file.
 */
public class ActionSaveTab extends AbstractAction {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private ReplaceFileChooser replaceFileChooser;

    public ActionSaveTab() {
        // Unhandled NoSuchMethodError #82561 on constructor: NoSuchMethodError
        // Unhandled InternalError #93015 on constructor: InvocationTargetException
        // Unhandled NullPointerException #95805 on constructor: desktop null on Windows
        // Unhandled IllegalArgumentException #95985 on constructor: Comparison method violates its general contract!
        try {
            this.replaceFileChooser = new ReplaceFileChooser(
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile()
            );
        } catch (IllegalArgumentException | NoSuchMethodError | InternalError | NullPointerException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Internal error in JFileChooser, verify your system and see stacktrace in tab Java: {}", e.getMessage());
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        this.putValue(Action.NAME, "Save Tab As...");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.replaceFileChooser.setDialogTitle("Save Tab As");
        var componentResult = MediatorHelper.tabResults().getSelectedComponent();
        if (componentResult instanceof PanelTable) {
            JTable table = ((PanelTable) componentResult).getTableValues();
            this.saveToFile(table);
        } else if (
            componentResult instanceof JScrollPane
            && ((JScrollPane) componentResult).getViewport().getView() instanceof JTextComponent textarea
        ) {
            this.saveToFile(textarea);
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Nothing to save");
        }
    }
    
    private void saveToFile(JComponent textarea) {
        if (textarea == null) {
            return;
        }

        this.replaceFileChooser.updateUI();  // required when changing dark/light mode
        try {  // Fix #96109: InvalidPathException on showSaveDialog()
            int stateSave = this.replaceFileChooser.showSaveDialog(MediatorHelper.frame());
            if (stateSave == JFileChooser.APPROVE_OPTION) {
                var folderSelectedFile = this.replaceFileChooser.getCurrentDirectory().toString();
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().set(folderSelectedFile);
                if (textarea instanceof JTextComponent) {
                    this.saveTextToFile((JTextComponent) textarea);
                } else if (textarea instanceof JTable) {
                    this.saveTableToFile((JTable) textarea);
                }
            }
        } catch (InvalidPathException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private void saveTableToFile(JTable tableResults) {
        var fileSelected = this.replaceFileChooser.getSelectedFile();
        
        try (var fileWriter = new FileWriter(fileSelected, StandardCharsets.UTF_8)) {
            var tableModel = tableResults.getModel();
            for (var i = 2 ; i < tableModel.getColumnCount() ; i++) {
                fileWriter.write(tableModel.getColumnName(i) + "\t");
            }
            fileWriter.write("\n");
            
            for (var i = 0 ; i < tableModel.getRowCount() ; i++) {
                for (var j = 2 ; j < tableModel.getColumnCount() ; j++) {
                    // Cell empty when string was too long to be injected (columnTooLong|cellEmpty|cellEmpty).
                    if (tableModel.getValueAt(i, j) == null) {
                        fileWriter.write("\t");
                    } else {
                        var line = tableModel.getValueAt(i, j).toString();  // Encode line break.
                        line = line
                            .replace("\n", "\\n")
                            .replace("\t", "\\t");
                        line = line + "\t";
                        fileWriter.write(line);
                    }
                }
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                String.format("Error writing to %s", fileSelected.getName()),
                e.getMessage()  // full stacktrace not required
            );
        }
    }

    private void saveTextToFile(JTextComponent textarea) {
        var fileSelected = this.replaceFileChooser.getSelectedFile();
        try (
            var fileWriter = new FileWriter(fileSelected, StandardCharsets.UTF_8);
            var fileOut = new BufferedWriter(fileWriter)
        ) {
            textarea.write(fileOut);
        } catch (IOException e) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                String.format("Error writing to %s", fileSelected.getName()),
                e
            );
        }
    }
}

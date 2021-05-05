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
package com.jsql.view.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.dialog.ReplaceFileChooser;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Save the content of tab in a file.
 */
@SuppressWarnings("serial")
public class ActionSaveTab extends AbstractAction {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final ReplaceFileChooser filechooser = new ReplaceFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());

    public ActionSaveTab() {
        
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        this.putValue(Action.NAME, "Save Tab As...");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        this.filechooser.setDialogTitle("Save Tab As");

        var componentResult = MediatorHelper.tabResults().getSelectedComponent();
        
        if (componentResult instanceof PanelTable) {
            
            JTable table = ((PanelTable) componentResult).getTableValues();
            this.saveToFile(table);
            
        } else if (
            componentResult instanceof LightScrollPane
            && ((LightScrollPane) componentResult).scrollPane.getViewport().getView() instanceof JTextComponent
        ) {
            
            JTextComponent textarea = (JTextComponent) ((LightScrollPane) componentResult).scrollPane.getViewport().getView();
            this.saveToFile(textarea);
        }
    }
    
    private void saveToFile(JComponent textarea) {
        
        if (textarea == null) {
            return;
        }
        
        int stateSave = this.filechooser.showSaveDialog(MediatorHelper.frame());
        
        if (stateSave == JFileChooser.APPROVE_OPTION) {
            
            var folderSelectedFile = this.filechooser.getCurrentDirectory().toString();
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().set(folderSelectedFile);
            
            if (textarea instanceof JTextComponent) {
                
                this.saveTextToFile((JTextComponent) textarea);
                
            } else if (textarea instanceof JTable) {
                
                this.saveTableToFile((JTable) textarea);
            }
        }
    }

    private void saveTableToFile(JTable tableResults) {
        
        var fileSelected = this.filechooser.getSelectedFile();
        
        try (var fileWriterExcel = new FileWriter(fileSelected)) {
            
            var tableModel = tableResults.getModel();
            
            for (var i = 2 ; i < tableModel.getColumnCount() ; i++) {
                
                fileWriterExcel.write(tableModel.getColumnName(i) + "\t");
            }
            
            fileWriterExcel.write("\n");
            
            for (var i = 0 ; i < tableModel.getRowCount() ; i++) {
                
                for (var j = 2 ; j < tableModel.getColumnCount() ; j++) {
                    
                    // Cell empty when string was too long to be injected (columnTooLong|cellEmpty|cellEmpty).
                    if (tableModel.getValueAt(i, j) == null) {
                        
                        fileWriterExcel.write("\t");
                        
                    } else {
                        
                        // Encode line break.
                        var line = tableModel.getValueAt(i, j).toString();
                        line =
                            line
                            .replace("\n", "\\n")
                            .replace("\t", "\\t");
                        line = line + "\t";
                        fileWriterExcel.write(line);
                    }
                }
                
                fileWriterExcel.write("\n");
            }
            
        } catch (IOException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Error writing to %s", fileSelected.getName()),
                e
            );
        }
    }

    private void saveTextToFile(JTextComponent textarea) {
        
        var fileSelected = this.filechooser.getSelectedFile();
        
        try (
            var fileWriter = new FileWriter(fileSelected);
            var fileOut = new BufferedWriter(fileWriter)
        ) {
            textarea.write(fileOut);
            
        } catch (IOException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Error writing to %s", fileSelected.getName()),
                e
            );
        }
    }
}

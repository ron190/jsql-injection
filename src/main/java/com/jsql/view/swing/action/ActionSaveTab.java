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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.dialog.ReplaceFileChooser;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.util.UiUtil;

/**
 * Save the content of tab in a file.
 */
@SuppressWarnings("serial")
public class ActionSaveTab extends AbstractAction {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    final ReplaceFileChooser filechooser = new ReplaceFileChooser(MediatorGui.model().getMediatorUtils().getPreferencesUtil().getPathFile());

    public ActionSaveTab() {
        
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        this.putValue(Action.NAME, "Save Tab As...");
        this.putValue(Action.SMALL_ICON, UiUtil.ICON_EMPTY);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        this.filechooser.setDialogTitle("Save Tab As");

        Component component = MediatorGui.tabResults().getSelectedComponent();
        
        if (component instanceof PanelTable) {
            
            JTable table = ((PanelTable) MediatorGui.tabResults().getSelectedComponent()).getTableValues();
            this.saveToFile(table);
        } else if (
            component instanceof LightScrollPane
            && ((LightScrollPane) component).scrollPane.getViewport().getView() instanceof JTextComponent
        ) {
            
            JTextComponent textarea = (JTextComponent) ((LightScrollPane) MediatorGui.tabResults().getSelectedComponent()).scrollPane.getViewport().getView();
            this.saveToFile(textarea);
        }
    }
    
    private void saveToFile(JComponent textarea) {
        
        if (textarea == null) {
            return;
        }
        
        int stateSave = this.filechooser.showSaveDialog(MediatorGui.frame());
        
        if (stateSave == JFileChooser.APPROVE_OPTION) {
            
            String folderSelectedFile = this.filechooser.getCurrentDirectory().toString();
            MediatorGui.model().getMediatorUtils().getPreferencesUtil().set(folderSelectedFile);
            
            if (textarea instanceof JTextComponent) {
                
                this.saveTextToFile((JTextComponent) textarea);
            } else if (textarea instanceof JTable) {
                
                this.saveTableToFile((JTable) textarea);
            }
        }
    }

    private void saveTableToFile(JTable tableResults) {
        
        File fileSelected = this.filechooser.getSelectedFile();
        
        try (FileWriter fileWriterExcel = new FileWriter(fileSelected)) {
            
            TableModel tableModel = tableResults.getModel();
            
            for (int i = 2 ; i < tableModel.getColumnCount() ; i++) {
                fileWriterExcel.write(tableModel.getColumnName(i) + "\t");
            }
            
            fileWriterExcel.write("\n");
            
            for (int i = 0 ; i < tableModel.getRowCount() ; i++) {
                
                for (int j = 2 ; j < tableModel.getColumnCount() ; j++) {
                    
                    // Cell empty when string was too long to be injected (columnTooLong|cellEmpty|cellEmpty).
                    if (tableModel.getValueAt(i, j) == null) {
                        
                        fileWriterExcel.write("\t");
                    } else {
                        
                        // Encode line break.
                        String line = tableModel.getValueAt(i, j).toString();
                        line = line.replaceAll("\n", "\\n").replaceAll("\t", "\\t");
                        line = line + "\t";
                        fileWriterExcel.write(line);
                    }
                }
                
                fileWriterExcel.write("\n");
            }
        } catch (IOException e) {
            LOGGER.warn("Error writing to "+ fileSelected.getName(), e);
        }
    }

    private void saveTextToFile(JTextComponent textarea) {
        
        File file = this.filechooser.getSelectedFile();
        
        try (
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter fileOut = new BufferedWriter(fileWriter)
        ) {
            textarea.write(fileOut);
        } catch (IOException e) {
            LOGGER.warn("Error writing to "+ file.getName(), e);
        }
    }
}

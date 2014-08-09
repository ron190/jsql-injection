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
package com.jsql.view.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;
import com.jsql.view.dialog.ReplaceFileChooser;
import com.jsql.view.table.TablePanel;

/**
 * Save the content of tab in a file.
 */
public class SaveTabAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        final ReplaceFileChooser filechooser = new ReplaceFileChooser(GUIMediator.model().prefPathFile);

        if (GUIMediator.right().getSelectedComponent() instanceof TablePanel) {
            JTable table = ((TablePanel) GUIMediator.right().getSelectedComponent()).table;
            
            if (table == null) {
                return;
            }
            
            filechooser.setDialogTitle("Save table as CSV");

            int returnVal = filechooser.showSaveDialog(GUIMediator.gui());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = filechooser.getSelectedFile();
                GUIMediator.model().prefPathFile = filechooser.getCurrentDirectory().toString();
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.put("pathFile", GUIMediator.model().prefPathFile);

                try {
                    TableModel model = table.getModel();
                    FileWriter excel = new FileWriter(file);
                    
                    for (int i = 2; i < model.getColumnCount(); i++) {
                        excel.write(model.getColumnName(i) + "\t");
                    }
                    
                    excel.write("\n");
                    
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 2; j < model.getColumnCount(); j++) {
                            excel.write(model.getValueAt(i, j).toString() + "\t");
                        }
                        excel.write("\n");
                    }
                    
                    excel.close();
                    
                } catch (IOException err) {
                    InjectionModel.LOGGER.warn(err.getMessage());
                }
            }
        } else if (GUIMediator.right().getSelectedComponent() instanceof JScrollPane) {
            if ((((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport()).getView() instanceof JTextArea) {
                JTextArea textArea = null;
                
                if ((JTextArea) (((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport()).getView() != null) {
                    textArea = (JTextArea) (((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport()).getView();
                }
                
                if (textArea == null) {
                    return;
                }
                
                filechooser.setDialogTitle("Save as");
                int returnVal = filechooser.showSaveDialog(GUIMediator.gui());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    GUIMediator.model().prefPathFile = filechooser.getCurrentDirectory().toString();
                    Preferences prefs = Preferences.userRoot().node(GUIMediator.model().getClass().getName());
                    prefs.put("pathFile", GUIMediator.model().prefPathFile);
                    
                    try {
                        BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
                        textArea.write(fileOut);
                        fileOut.close();
                    } catch (IOException err) {
                        InjectionModel.LOGGER.warn(err.getMessage());
                    }
                }
            } else if ((((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport()).getView() instanceof JTextPane) {
                JTextPane textArea = null;
                
                if ((JTextPane) (((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport()).getView() != null) {
                    textArea = (JTextPane) (((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport()).getView();
                }
                
                if (textArea == null) {
                    return;
                }
                
                filechooser.setDialogTitle("Save as");
                int returnVal = filechooser.showSaveDialog(GUIMediator.gui());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    GUIMediator.model().prefPathFile = filechooser.getCurrentDirectory().toString();
                    Preferences prefs = Preferences.userRoot().node(GUIMediator.model().getClass().getName());
                    prefs.put("pathFile", GUIMediator.model().prefPathFile);
                    
                    try {
                        BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
                        textArea.write(fileOut);
                        fileOut.close();
                    } catch (IOException err) {
                        InjectionModel.LOGGER.warn(err.getMessage());
                    }
                }
            } else {
                InjectionModel.LOGGER.warn("No tab to save.");
            }
        } else {
            InjectionModel.LOGGER.warn("No tab to save.");
        }
    }

}

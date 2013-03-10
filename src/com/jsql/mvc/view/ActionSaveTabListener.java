package com.jsql.mvc.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.table.TableModel;

import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.view.JFileChooserCustom;
import com.jsql.mvc.view.component.TablePanel;

public class ActionSaveTabListener implements ActionListener {
    GUI gui = null;
    JTabbedPane valuesTabbedPane = null;
    public ActionSaveTabListener(GUI _gui){
        gui = _gui;
        valuesTabbedPane = _gui.valuesTabbedPane;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final JFileChooserCustom filechooser = new JFileChooserCustom(gui.model.pathFile);

        if(valuesTabbedPane.getSelectedComponent() instanceof TablePanel){
            JTable table = ((TablePanel) valuesTabbedPane.getSelectedComponent()).newJTable;
            
            if(table == null)
                return;
            
            filechooser.setDialogTitle("Save table as");

            int returnVal = filechooser.showSaveDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = filechooser.getSelectedFile();
                gui.model.pathFile = filechooser.getCurrentDirectory().toString();
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                String ID1 = "pathFile";
                prefs.put(ID1, gui.model.pathFile);

                try{
                    TableModel model = table.getModel();
                    FileWriter excel = new FileWriter(file);
            
                    for(int i = 2; i < model.getColumnCount(); i++){
                        excel.write(model.getColumnName(i) + "\t");
                    }
            
                    excel.write("\n");
            
                    for(int i=0; i< model.getRowCount(); i++) {
                        for(int j=2; j < model.getColumnCount(); j++) {
                            excel.write(model.getValueAt(i,j).toString()+"\t");
                        }
                        excel.write("\n");
                    }
            
                    excel.close();
            
                }catch(IOException err){ 
                    gui.model.sendErrorMessage(err.getMessage()); 
                }
            }
        }else if(valuesTabbedPane.getSelectedComponent() instanceof JScrollPane){
            JTextArea textArea = null;

            try{
                if((JTextArea) (((JViewport) (((JScrollPane) valuesTabbedPane.getSelectedComponent()).getViewport()))).getView() != null)
                    textArea = (JTextArea) (((JViewport) (((JScrollPane) valuesTabbedPane.getSelectedComponent()).getViewport()))).getView();
                
                if(textArea == null)
                    return;

                filechooser.setDialogTitle("Save as");
                int returnVal = filechooser.showSaveDialog(gui);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    gui.model.pathFile = filechooser.getCurrentDirectory().toString();
                    Preferences prefs = Preferences.userRoot().node(gui.model.getClass().getName());
                    String ID1 = "pathFile";
                    prefs.put(ID1, gui.model.pathFile);
                    
                    try{
                        BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
                        textArea.write(fileOut);
                        fileOut.close();
                    }catch(IOException err){ 
                        gui.model.sendErrorMessage(err.getMessage()); 
                    }
                }
                
            }catch(Exception err){}
        }        
    }

}

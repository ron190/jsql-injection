package com.jsql.mvc.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * File chooser for supporting 'file already exists'
 */
public class JFileChooserCustom extends JFileChooser{
    private static final long serialVersionUID = -7032680779242125129L;
    
    public JFileChooserCustom(String s){
        super(s);
    }
    
    @Override
    public void approveSelection(){
        File f = getSelectedFile();
        if(getDialogType() == SAVE_DIALOG)
        if(f.exists()){
            int result = JOptionPane.showConfirmDialog(this,getSelectedFile().getName() + " already exists.\nDo you want to replace it?","Confirm Save As",
                    JOptionPane.YES_NO_OPTION);
            switch(result){
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                    return;
                case JOptionPane.CLOSED_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
            }
        }else{
            super.approveSelection();
        }
    }
}
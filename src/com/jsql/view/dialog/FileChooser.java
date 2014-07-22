/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * File chooser for supporting 'file already exists'
 */
@SuppressWarnings("serial")
public class FileChooser extends JFileChooser{
	
    public FileChooser(String s){
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

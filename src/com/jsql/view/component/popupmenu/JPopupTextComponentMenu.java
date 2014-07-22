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
package com.jsql.view.component.popupmenu;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import com.jsql.view.GUITools;

@SuppressWarnings("serial")
public class JPopupTextComponentMenu extends JPopupMenu{
	
    public JPopupTextComponentMenu(JTextComponent cmp){
        this(cmp, false);
    }
    
    public JPopupTextComponentMenu(JTextComponent newComponent, boolean isTextField){
        JTextComponent component = newComponent;
        
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(component.getActionMap().get(DefaultEditorKit.copyAction));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyItem.setMnemonic('C');
        copyItem.setText("Copy");
        copyItem.setIcon(GUITools.EMPTY);
        this.setLightWeightPopupEnabled(false);
        
        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setIcon(GUITools.EMPTY);
        selectAllItem.setAction(component.getActionMap().get(DefaultEditorKit.selectAllAction));
        selectAllItem.setText("Select All");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAllItem.setMnemonic('A');
        
        this.add( copyItem );
        
        if(isTextField){
            JMenuItem cutItem = new JMenuItem();
            cutItem.setIcon(GUITools.EMPTY);
            cutItem.setAction(component.getActionMap().get(DefaultEditorKit.cutAction));
            cutItem.setText("Cut");
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
            cutItem.setMnemonic('t');
            
            JMenuItem pasteItem = new JMenuItem();
            pasteItem.setIcon(GUITools.EMPTY);
            pasteItem.setAction(component.getActionMap().get(DefaultEditorKit.pasteAction));
            pasteItem.setText("Paste");
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
            pasteItem.setMnemonic('P');
            
            this.add( cutItem );
            this.add( pasteItem );
        }
        
        this.addSeparator();
        this.add( selectAllItem );
        
        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupTextComponentMenu.this.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }
}

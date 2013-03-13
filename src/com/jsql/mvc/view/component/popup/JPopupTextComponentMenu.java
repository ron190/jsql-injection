package com.jsql.mvc.view.component.popup;

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

public class JPopupTextComponentMenu extends JPopupMenu
{
    private static final long serialVersionUID = 8092372084470989050L;

    public JPopupTextComponentMenu(JTextComponent _cmp){
        this(_cmp, false);
    }
    public JPopupTextComponentMenu(JTextComponent _cmp, boolean isTextField){
        JTextComponent cmp = _cmp;
                
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(cmp.getActionMap().get(DefaultEditorKit.copyAction));
        copyItem.setText("Copy");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setAction(cmp.getActionMap().get(DefaultEditorKit.selectAllAction));
        selectAllItem.setText("Select All");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        
        this.add( copyItem );
        
        if(isTextField){
            JMenuItem cutItem = new JMenuItem();
            cutItem.setAction(cmp.getActionMap().get(DefaultEditorKit.cutAction));
            cutItem.setText("Cut");
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

            JMenuItem pasteItem = new JMenuItem();
            pasteItem.setAction(cmp.getActionMap().get(DefaultEditorKit.pasteAction));
            pasteItem.setText("Paste");
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
            
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
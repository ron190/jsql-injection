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
package com.jsql.view.swing.popupmenu;

import com.jsql.view.swing.menubar.JMenuItemWithMargin;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Default popup menu for textfield and texteditor.
 */
public class JPopupMenuText extends JPopupMenuComponent {
    
    /**
     * Create popup menu for this component.
     * @param component The component receiving the menu
     */
    public JPopupMenuText(JTextComponent component) {
        
        super(component);

        if (component.isEditable()) {
            
            JMenuItem cutItem = new JMenuItemWithMargin();
            cutItem.setAction(component.getActionMap().get(DefaultEditorKit.cutAction));
            cutItem.setText("Cut");
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
            cutItem.setMnemonic('t');

            JMenuItem pasteItem = new JMenuItemWithMargin();
            pasteItem.setAction(component.getActionMap().get(DefaultEditorKit.pasteAction));
            pasteItem.setText("Paste");
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
            pasteItem.setMnemonic('P');

            // Before Copy menu
            this.add(cutItem, 0);
            
            // After Copy menu
            this.add(pasteItem, 2);
        }
    }
}

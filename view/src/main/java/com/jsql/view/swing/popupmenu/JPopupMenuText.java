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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import com.jsql.view.swing.menubar.JMenuItemWithMargin;

/**
 * Default popup menu for textfield and texteditor.
 */
@SuppressWarnings("serial")
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
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
            cutItem.setMnemonic('t');

            JMenuItem pasteItem = new JMenuItemWithMargin();
            pasteItem.setAction(component.getActionMap().get(DefaultEditorKit.pasteAction));
            pasteItem.setText("Paste");
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
            pasteItem.setMnemonic('P');

            // Before Copy menu
            this.add(cutItem, 0);
            
            // After Copy menu
            this.add(pasteItem, 2);
        }
    }
}

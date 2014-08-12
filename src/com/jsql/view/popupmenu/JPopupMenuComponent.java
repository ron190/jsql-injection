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
package com.jsql.view.popupmenu;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;

import com.jsql.view.ToolsGUI;

/**
 * Popup menu for editable text component.
 */
@SuppressWarnings("serial")
public class JPopupMenuComponent extends JPopupMenu {
    /**
     * Create a popup menu for editable component.
     * @param component The component with the new menu
     */
    public JPopupMenuComponent(JComponent component) {
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(component.getActionMap().get(DefaultEditorKit.copyAction));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyItem.setMnemonic('C');
        copyItem.setText("Copy");
        copyItem.setIcon(ToolsGUI.EMPTY);
        this.setLightWeightPopupEnabled(false);

        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setIcon(ToolsGUI.EMPTY);
        selectAllItem.setAction(component.getActionMap().get(DefaultEditorKit.selectAllAction));
        selectAllItem.setText("Select All");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAllItem.setMnemonic('A');

        this.add(copyItem);
        this.addSeparator();
        this.add(selectAllItem);

        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupMenuComponent.this.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Do nothing
            }
            @Override public void popupMenuCanceled(PopupMenuEvent e) {
                // Do nothing
            }
        });
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.popupmenu;

import java.awt.ComponentOrientation;
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

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperUi;

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
        copyItem.setText(I18n.valueByKey("CONTEXT_MENU_COPY"));
        I18n.addComponentForKey("CONTEXT_MENU_COPY", copyItem);
        I18n.addComponentOrientable(copyItem);
        copyItem.setIcon(HelperUi.ICON_EMPTY);
        this.setLightWeightPopupEnabled(false);

        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setIcon(HelperUi.ICON_EMPTY);
        selectAllItem.setAction(component.getActionMap().get(DefaultEditorKit.selectAllAction));
        selectAllItem.setText(I18n.valueByKey("CONTEXT_MENU_SELECT_ALL"));
        I18n.addComponentForKey("CONTEXT_MENU_SELECT_ALL", selectAllItem);
        I18n.addComponentOrientable(selectAllItem);
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAllItem.setMnemonic('A');

        this.add(copyItem);
        this.addSeparator();
        this.add(selectAllItem);

        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JPopupMenuComponent.this.setLocation(MouseInfo.getPointerInfo().getLocation());
                
                JPopupMenuComponent.this.setLocation(
                    ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                    ? MouseInfo.getPointerInfo().getLocation().x - JPopupMenuComponent.this.getWidth()
                    : MouseInfo.getPointerInfo().getLocation().x, 
                    MouseInfo.getPointerInfo().getLocation().y
                );
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Do nothing
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Do nothing
            }
        });
    }
}

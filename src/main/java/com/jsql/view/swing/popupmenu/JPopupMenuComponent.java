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

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.text.JTextAreaPlaceholderConsole;
import com.jsql.view.swing.text.JTextPanePlaceholderConsole;

/**
 * Popup menu for editable text component.
 */
@SuppressWarnings("serial")
public class JPopupMenuComponent extends JPopupMenu {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    JComponent component;
    
    /**
     * Create a popup menu for editable component.
     * @param component The component with the new menu
     */
    public JPopupMenuComponent(JComponent component) {
        this.component = component;
        
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(component.getActionMap().get(DefaultEditorKit.copyAction));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyItem.setMnemonic('C');
        copyItem.setText(I18n.valueByKey("CONTEXT_MENU_COPY"));
        I18nView.addComponentForKey("CONTEXT_MENU_COPY", copyItem);
        copyItem.setIcon(HelperUi.ICON_EMPTY);
        this.setLightWeightPopupEnabled(false);

        JMenuItem selectAllItem = new JMenuItem();
        selectAllItem.setIcon(HelperUi.ICON_EMPTY);
        selectAllItem.setAction(component.getActionMap().get(DefaultEditorKit.selectAllAction));
        selectAllItem.setText(I18n.valueByKey("CONTEXT_MENU_SELECT_ALL"));
        I18nView.addComponentForKey("CONTEXT_MENU_SELECT_ALL", selectAllItem);
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAllItem.setMnemonic('A');
        
        this.add(copyItem);
        this.addSeparator();
        this.add(selectAllItem);
        
        if (
            component instanceof JTextAreaPlaceholderConsole
            || component instanceof JTextPanePlaceholderConsole
        ) {
            JMenuItem clearItem = new JMenuItem();
            clearItem.setIcon(HelperUi.ICON_EMPTY);
            clearItem.setAction(new AbstractAction() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((JTextComponent) JPopupMenuComponent.this.component).setText(null);
                }
                
            });
            
            clearItem.setText(I18n.valueByKey("CONTEXT_MENU_CLEAR"));
            I18nView.addComponentForKey("CONTEXT_MENU_CLEAR", clearItem);
            clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            clearItem.setMnemonic('E');
            
            this.addSeparator();
            this.add(clearItem);
        }

        this.addPopupMenuListener(new PopupMenuOrientedListener());
    }
    
    private class PopupMenuOrientedListener implements PopupMenuListener {
        
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
            // Fix #47018: NullPointerException on getLocation()
            try {
                JPopupMenuComponent.this.setLocation(MouseInfo.getPointerInfo().getLocation());
                
                JPopupMenuComponent.this.setLocation(
                    ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                    ? MouseInfo.getPointerInfo().getLocation().x - JPopupMenuComponent.this.getWidth()
                    : MouseInfo.getPointerInfo().getLocation().x,
                    MouseInfo.getPointerInfo().getLocation().y
                );
            } catch (NullPointerException e) {
                LOGGER.error(e, e);
            }
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // Do nothing
        }
        
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            // Do nothing
        }
        
    }
    
}

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.menubar.JMenuItemWithMargin;
import com.jsql.view.swing.text.JTextAreaPlaceholderConsole;
import com.jsql.view.swing.text.JTextPanePlaceholderConsole;
import com.jsql.view.swing.util.I18nViewUtil;

/**
 * Popup menu for editable text component.
 */
@SuppressWarnings("serial")
public class JPopupMenuComponent extends JPopupMenu {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private JComponent component;
    
    /**
     * Create a popup menu for editable component.
     * @param component The component with the new menu
     */
    public JPopupMenuComponent(JComponent component) {
        
        this.component = component;
        
        JMenuItem copyItem = new JMenuItemWithMargin(component.getActionMap().get(DefaultEditorKit.copyAction));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyItem.setMnemonic('C');
        copyItem.setText(I18nUtil.valueByKey("CONTEXT_MENU_COPY"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY", copyItem);

        JMenuItem selectAllItem = new JMenuItemWithMargin(component.getActionMap().get(DefaultEditorKit.selectAllAction));
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        selectAllItem.setMnemonic('A');
        selectAllItem.setText(I18nUtil.valueByKey("CONTEXT_MENU_SELECT_ALL"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_SELECT_ALL", selectAllItem);
        
        this.setLightWeightPopupEnabled(false);
        
        this.add(copyItem);
        this.addSeparator();
        this.add(selectAllItem);
        
        if (
            component instanceof JTextAreaPlaceholderConsole
            || component instanceof JTextPanePlaceholderConsole
        ) {
            JMenuItem clearItem = new JMenuItemWithMargin();
            
            clearItem.setAction(new AbstractAction() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    ((JTextComponent) JPopupMenuComponent.this.component).setText(null);
                }
            });
            
            clearItem.setText(I18nUtil.valueByKey("CONTEXT_MENU_CLEAR"));
            I18nViewUtil.addComponentForKey("CONTEXT_MENU_CLEAR", clearItem);
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
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                    ? MouseInfo.getPointerInfo().getLocation().x - JPopupMenuComponent.this.getWidth()
                    : MouseInfo.getPointerInfo().getLocation().x,
                    MouseInfo.getPointerInfo().getLocation().y
                );
                
            } catch (NullPointerException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
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

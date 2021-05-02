package com.jsql.view.swing.action;

import java.awt.KeyEventDispatcher;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.MenuSelectionManager;

import com.jsql.view.swing.util.MediatorHelper;

public class AltKeyEventDispatcher implements KeyEventDispatcher {

    private boolean wasAltDPressed = false;
    private boolean wasAltPressed = false;
    private boolean wasAltGraphPressed = false;
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        
        var shouldNotTakeFurtherAction = false;
        
        // Alt key press/release generates 2 events
        // AltGr key press/release generates 4 events including an Alt press/release
        // => AltGr:press Alt:press AltGr:release Alt:release
        // AltGr keycode is the same as Ctrl
        if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
            
            this.wasAltGraphPressed = true;
        }
        
        boolean isAltDPressed =
            keyEvent.isAltDown()
            && keyEvent.getKeyCode() == (KeyEvent.VK_ALT & KeyEvent.VK_D);
        
        boolean isAltReleased =
            keyEvent.getKeyCode() == KeyEvent.VK_ALT
            && keyEvent.getModifiersEx() == (InputEvent.ALT_DOWN_MASK & KeyEvent.KEY_RELEASED);
        
        boolean isAltPressed =
            keyEvent.isAltDown()
            && keyEvent.getKeyCode() == KeyEvent.VK_ALT
            && !this.wasAltGraphPressed;
        
        boolean wasAltPressedAlready =
            !this.wasAltDPressed
            && !this.wasAltPressed
            && !this.wasAltGraphPressed;
        
        if (isAltDPressed) {
            
            this.selectAddressBar();
            shouldNotTakeFurtherAction = true;
            
        } else if (isAltReleased) {
            
            this.showMenuBar(wasAltPressedAlready);
            shouldNotTakeFurtherAction = true;
            
        } else if (isAltPressed) {
            
            this.hideMenuBar();
            shouldNotTakeFurtherAction = true;
        }
        
        return shouldNotTakeFurtherAction;
    }

    private void selectAddressBar() {
        
        MediatorHelper.panelAddressBar().getTextFieldAddress().requestFocusInWindow();
        MediatorHelper.panelAddressBar().getTextFieldAddress().selectAll();
        this.wasAltDPressed = true;
    }

    private void showMenuBar(boolean wasAltPressedAlready) {
        
        // Avoid flickering and AltGr pollution
        if (wasAltPressedAlready) {
            
            if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                
                MenuSelectionManager.defaultManager().clearSelectedPath();
                
            } else if (!MediatorHelper.panelAddressBar().isAdvanceActivated()) {
                
                MediatorHelper.menubar().setVisible(!MediatorHelper.menubar().isVisible());
                this.wasAltGraphPressed = false;
            }
            
        } else {
            
            this.wasAltDPressed = false;
            this.wasAltPressed = false;
            this.wasAltGraphPressed = false;
        }
    }

    private void hideMenuBar() {
        
        // Avoid flickering and AltGr pollution
        if (
            !MediatorHelper.panelAddressBar().isAdvanceActivated()
            && MediatorHelper.menubar().isVisible()
        ) {
            
            MenuSelectionManager.defaultManager().clearSelectedPath();
            MediatorHelper.menubar().setVisible(false);
            this.wasAltPressed = true;
            this.wasAltGraphPressed = false;
        }
    }
}
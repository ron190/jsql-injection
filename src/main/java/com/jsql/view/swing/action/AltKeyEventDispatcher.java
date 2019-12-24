package com.jsql.view.swing.action;

import java.awt.KeyEventDispatcher;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.MenuSelectionManager;

import com.jsql.view.swing.MediatorGui;

public class AltKeyEventDispatcher implements KeyEventDispatcher {

    private final boolean[] wasAltDPressed = {false};
    private final boolean[] wasAltPressed = {false};
    private final boolean[] wasAltGraphPressed = {false};
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        boolean shouldTakeNoFurtherAction = false;
        
        // Alt key press/release generates 2 events
        // AltGr key press/release generates 4 events including an Alt press/release
        // => AltGr:press Alt:press AltGr:release Alt:release
        // AltGr keycode is the same as Ctrl
        if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.wasAltGraphPressed[0] = true;
        }
        
        boolean isAltDPressed =
            keyEvent.isAltDown()
            && keyEvent.getKeyCode() == (KeyEvent.VK_ALT & KeyEvent.VK_D)
        ;
        
        boolean isAltReleased =
            keyEvent.getKeyCode() == KeyEvent.VK_ALT
            && keyEvent.getModifiersEx() == (InputEvent.ALT_DOWN_MASK & KeyEvent.KEY_RELEASED)
        ;
        
        boolean isAltPressed =
            keyEvent.isAltDown()
            && keyEvent.getKeyCode() == KeyEvent.VK_ALT
            && !this.wasAltGraphPressed[0]
        ;
        
        boolean wasAltPressedAlready =
            !this.wasAltDPressed[0]
            && !this.wasAltPressed[0]
            && !this.wasAltGraphPressed[0]
        ;
        
        if (isAltDPressed) {
            MediatorGui.panelAddressBar().getTextFieldAddress().requestFocusInWindow();
            MediatorGui.panelAddressBar().getTextFieldAddress().selectAll();
            this.wasAltDPressed[0] = true;
            
            shouldTakeNoFurtherAction = true;
            
        } else if (isAltReleased) {
            // Avoid flickering and AltGr pollution
            if (wasAltPressedAlready) {
                if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                } else if (!MediatorGui.panelAddressBar().isAdvanceIsActivated()) {
                    MediatorGui.menubar().setVisible(!MediatorGui.menubar().isVisible());
                    this.wasAltGraphPressed[0] = false;
                }
            } else {
                this.wasAltDPressed[0] = false;
                this.wasAltPressed[0] = false;
                this.wasAltGraphPressed[0] = false;
            }
            
            shouldTakeNoFurtherAction = true;
            
        } else if (isAltPressed) {
            // Avoid flickering and AltGr pollution
            if (!MediatorGui.panelAddressBar().isAdvanceIsActivated() && MediatorGui.menubar().isVisible()) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                MediatorGui.menubar().setVisible(false);
                this.wasAltPressed[0] = true;
                this.wasAltGraphPressed[0] = false;
            }
            
            shouldTakeNoFurtherAction = true;
        }
        
        return shouldTakeNoFurtherAction;
    }
    
}
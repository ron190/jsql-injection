package com.jsql.view.swing.shell;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Cancel every mouse click, only gives focus.
 */
public class EmptyFocus implements MouseListener {
    
    private AbstractShell abstractShell;
    
    public EmptyFocus(AbstractShell abstractShell) {
        this.abstractShell = abstractShell;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        e.consume();
        this.abstractShell.requestFocusInWindow();
        this.abstractShell.setCaretPosition(this.abstractShell.getDocument().getLength());
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume();
    }
}
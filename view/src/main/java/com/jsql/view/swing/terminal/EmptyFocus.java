package com.jsql.view.swing.terminal;

import com.jsql.model.exception.JSqlRuntimeException;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * Cancel every mouse click, only gives focus.
 */
public class EmptyFocus implements MouseListener {
    
    private final AbstractExploit abstractExploit;
    
    public EmptyFocus(AbstractExploit abstractExploit) {
        this.abstractExploit = abstractExploit;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        e.consume();
        this.abstractExploit.requestFocusInWindow();
        this.abstractExploit.setCaretPosition(this.abstractExploit.getDocument().getLength());
        if (e.getButton() == MouseEvent.BUTTON3) {
            try {
                String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                abstractExploit.append(data);
            } catch (UnsupportedFlavorException | IOException ex) {
                throw new JSqlRuntimeException(ex);
            }
        }
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
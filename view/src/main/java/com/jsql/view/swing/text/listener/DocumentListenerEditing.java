package com.jsql.view.swing.text.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Listener for processing keyboard input.
 * Used for example to process tampering, sql engine,
 * or encoding when keyboard input is entered.
 */
public abstract class DocumentListenerEditing implements DocumentListener {

    public abstract void process();
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        
        this.process();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        
        this.process();
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        
        this.process();
    }
}
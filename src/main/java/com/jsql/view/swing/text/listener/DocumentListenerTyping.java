package com.jsql.view.swing.text.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentListenerTyping implements DocumentListener {
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        this.warn();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        this.warn();
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        this.warn();
    }
    
    public abstract void warn();
    
}
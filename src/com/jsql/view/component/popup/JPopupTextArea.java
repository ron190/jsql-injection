/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.component.popup;

import java.awt.Cursor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

public class JPopupTextArea extends JTextArea {
    private static final long serialVersionUID = 4071520941028129115L;
    
    public JPopupTextArea(){
        initialize();
    }
    
    public JPopupTextArea(int i, int j) {
        super(i,j);
        initialize();
    }

    public void initialize(){
        this.setComponentPopupMenu(new JPopupTextComponentMenu(this));
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JPopupTextArea.this.requestFocusInWindow();
            }
        });
        
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                JPopupTextArea.this.getCaret().setVisible(true);
                JPopupTextArea.this.getCaret().setSelectionVisible(true);
            }
        });
        
        this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.setEditable(false);
        this.setDragEnabled(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
}

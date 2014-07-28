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
package com.jsql.view.component.popupmenu;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class JPopupTextLabel extends JTextField {
	
    public JPopupTextLabel(String string) {
        super(string);
        initialize();
    }
    
    public void initialize(){
        this.setComponentPopupMenu(new JPopupTextMenu(this));
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                // Left button will deselect text after selectAll, so only for right click
                if(SwingUtilities.isRightMouseButton(e))
                	JPopupTextLabel.this.requestFocusInWindow();
            }
        });
        
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                JPopupTextLabel.this.getCaret().setVisible(true);
                JPopupTextLabel.this.getCaret().setSelectionVisible(true);
            }
        });

        Font boldFont = new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize());
        this.setFont(boldFont);
        this.setDragEnabled(true);

        this.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.setEditable(false);
    }
}

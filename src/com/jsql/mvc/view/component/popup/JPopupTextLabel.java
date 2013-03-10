package com.jsql.mvc.view.component.popup;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class JPopupTextLabel extends JTextField {
    private static final long serialVersionUID = 3902552856079929374L;
    
    public JPopupTextLabel(){
        initialize();
    }

    public JPopupTextLabel(String string) {
        super(string);
        initialize();
    }
    
    public void initialize(){
        this.setComponentPopupMenu(new JPopupTextComponentMenu(this));
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
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
        this.setBackground(this.getBackground());
    }
}

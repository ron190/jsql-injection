package com.jsql.view.swing.panel.preferences;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextPane;

import com.jsql.util.tampering.TamperingType;

public class TamperingMouseAdapter extends MouseAdapter {
    
    private TamperingType tampering;
    private JTextPane textPaneEval;
    private String eval = null;
    
    public TamperingMouseAdapter(TamperingType tampering, JTextPane textPaneEval) {
        
        this.tampering = tampering;
        this.textPaneEval = textPaneEval;
    }
    
    @Override
    public void mouseEntered(MouseEvent me) {
        
        this.eval = this.textPaneEval.getText();
        
        this.textPaneEval.setText(this.tampering.instance().getJavascript().trim());
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        
        this.textPaneEval.setText(this.eval);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
        this.eval = this.tampering.instance().getJavascript().trim();
        
        this.textPaneEval.setText(this.eval);
    }
}
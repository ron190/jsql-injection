package com.jsql.view.swing.panel.preferences.listener;

import com.jsql.util.tampering.TamperingType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TamperingMouseAdapter extends MouseAdapter {
    
    private final TamperingType tampering;
    private final RSyntaxTextArea textPaneEval;
    private String eval = null;
    
    public TamperingMouseAdapter(TamperingType tampering, RSyntaxTextArea textPaneEval) {
        this.tampering = tampering;
        this.textPaneEval = textPaneEval;
    }
    
    @Override
    public void mouseEntered(MouseEvent me) {
        this.eval = this.textPaneEval.getText();
        this.textPaneEval.setText(this.tampering.instance().getJavascript().trim());
        this.textPaneEval.setCaretPosition(0);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.textPaneEval.setText(this.eval);
        this.textPaneEval.setCaretPosition(0);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        this.eval = this.tampering.instance().getJavascript().trim();
        this.textPaneEval.setText(this.eval);
        this.textPaneEval.setCaretPosition(0);
    }
}
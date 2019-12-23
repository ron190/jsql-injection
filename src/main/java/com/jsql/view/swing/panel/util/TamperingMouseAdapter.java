package com.jsql.view.swing.panel.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextPane;

import com.jsql.util.tampering.Tampering;

public class TamperingMouseAdapter extends MouseAdapter {
    Tampering tampering;
    JTextPane l;
    String t = null;
    public TamperingMouseAdapter(Tampering tampering, JTextPane l) {
        this.tampering = tampering;
        this.l = l;
    }
    @Override
    public void mouseEntered(MouseEvent me) {
        this.t=this.l.getText();
        this.l.setText(this.tampering.instance().getModelYaml().getJavascript().trim());
    }
    @Override
    public void mouseExited(MouseEvent e) {
        this.l.setText(this.t);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        this.t=this.tampering.instance().getModelYaml().getJavascript().trim();
        this.l.setText(this.t);
    }
}
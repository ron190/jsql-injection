package com.jsql.view.swing.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.jsql.view.swing.HelperUi;

public class FlatButtonMouseAdapter extends MouseAdapter {
    
    private JButton buttonFlat;
    private boolean isVisible = false;

    public FlatButtonMouseAdapter(JButton buttonFlat) {
        this.buttonFlat = buttonFlat;
    }
    
    @Override public void mouseEntered(MouseEvent e) {
        if (this.buttonFlat.isEnabled() && !this.isVisible) {
            this.buttonFlat.setContentAreaFilled(true);
            this.buttonFlat.setBorder(HelperUi.BORDER_ROUND_BLU);
        }
    }

    @Override public void mouseExited(MouseEvent e) {
        if (this.buttonFlat.isEnabled() && !this.isVisible) {
            this.buttonFlat.setContentAreaFilled(false);
            this.buttonFlat.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        }
    }
    
    public void setContentVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    
}
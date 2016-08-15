package com.jsql.view.swing.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.jsql.view.swing.HelperUi;

public class FlatButtonMouseAdapter extends MouseAdapter {
    
    JButton buttonFlat;
    
    public FlatButtonMouseAdapter(JButton buttonFlat) {
        this.buttonFlat = buttonFlat;
    }
    
    @Override public void mouseEntered(MouseEvent e) {
        if (buttonFlat.isEnabled()) {
            buttonFlat.setContentAreaFilled(true);
            buttonFlat.setBorder(HelperUi.BORDER_ROUND_BLU);
        }
    }

    @Override public void mouseExited(MouseEvent e) {
        if (buttonFlat.isEnabled()) {
            buttonFlat.setContentAreaFilled(false);
            buttonFlat.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        }
    }
}
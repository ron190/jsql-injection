package com.jsql.view.swing.tab;

import java.awt.Dimension;

import javax.swing.JButton;

import com.jsql.view.swing.HelperUi;

@SuppressWarnings("serial")
public class ButtonClose extends JButton {
    public ButtonClose() {
        super(HelperUi.CLOSE_ICON);
        
        Dimension closeButtonSize = new Dimension(
            HelperUi.CLOSE_ICON.getIconWidth(), 
            HelperUi.CLOSE_ICON.getIconHeight()
        );
        this.setPreferredSize(closeButtonSize);
        
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setBorderPainted(false);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setRolloverIcon(HelperUi.CLOSE_ROLLOVER_ICON);
        this.setPressedIcon(HelperUi.CLOSE_PRESSED_ICON);
    }
}

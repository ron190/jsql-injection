package com.jsql.view.swing.tab;

import java.awt.Dimension;

import javax.swing.JButton;

import com.jsql.view.swing.HelperGui;

@SuppressWarnings("serial")
public class ButtonClose extends JButton {
    public ButtonClose() {
        super(HelperGui.CLOSE_ICON);
        
        Dimension closeButtonSize = new Dimension(
            HelperGui.CLOSE_ICON.getIconWidth(), 
            HelperGui.CLOSE_ICON.getIconHeight()
        );
        this.setPreferredSize(closeButtonSize);
        
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setBorderPainted(false);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setRolloverIcon(HelperGui.CLOSE_ROLLOVER_ICON);
        this.setPressedIcon(HelperGui.CLOSE_PRESSED_ICON);
    }
}

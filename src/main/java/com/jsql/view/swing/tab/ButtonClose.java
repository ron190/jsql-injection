package com.jsql.view.swing.tab;

import java.awt.Dimension;

import javax.swing.JButton;

import com.jsql.view.swing.HelperUi;

@SuppressWarnings("serial")
public class ButtonClose extends JButton {
    
    public ButtonClose() {
        super(HelperUi.ICON_CLOSE);
        
        Dimension closeButtonSize = new Dimension(
            HelperUi.ICON_CLOSE.getIconWidth(),
            HelperUi.ICON_CLOSE.getIconHeight()
        );
        this.setPreferredSize(closeButtonSize);
        
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setBorderPainted(false);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setRolloverIcon(HelperUi.ICON_CLOSE_ROLLOVER);
        this.setPressedIcon(HelperUi.ICON_CLOSE_PRESSED);
    }
    
}

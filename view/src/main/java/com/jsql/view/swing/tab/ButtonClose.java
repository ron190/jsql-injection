package com.jsql.view.swing.tab;

import java.awt.Dimension;

import javax.swing.JButton;

import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class ButtonClose extends JButton {
    
    public ButtonClose() {
        
        super(UiUtil.ICON_CLOSE);
        
        var closeButtonSize = new Dimension(
            UiUtil.ICON_CLOSE.getIconWidth(),
            UiUtil.ICON_CLOSE.getIconHeight()
        );
        
        this.setPreferredSize(closeButtonSize);
        
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setBorderPainted(false);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setRolloverIcon(UiUtil.ICON_CLOSE_ROLLOVER);
        this.setPressedIcon(UiUtil.ICON_CLOSE_PRESSED);
    }
}

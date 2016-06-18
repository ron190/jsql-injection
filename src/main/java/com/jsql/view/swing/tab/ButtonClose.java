package com.jsql.view.swing.tab;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ButtonClose extends JButton {
    // Icon for closing tab
    private static final Icon closeIcon = new ImageIcon(ButtonClose.class.getResource("/com/jsql/view/swing/resources/images/close.png"));
    
    public ButtonClose() {
        super(closeIcon);
        
        Dimension closeButtonSize = new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight());
        this.setPreferredSize(closeButtonSize);
        
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setBorderPainted(false);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setRolloverIcon(new ImageIcon(TabHeader.class.getResource("/com/jsql/view/swing/resources/images/closeRollover.png")));
        this.setPressedIcon(new ImageIcon(TabHeader.class.getResource("/com/jsql/view/swing/resources/images/closePressed.png")));
    }
}

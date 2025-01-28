package com.jsql.view.swing.table;

import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;

public class ButtonClose extends JButton {
    
    public ButtonClose() {
        super(UiUtil.CROSS_RED.getIcon());
        this.setContentAreaFilled(false);  // required
        this.setBorderPainted(false);  // required
    }
}

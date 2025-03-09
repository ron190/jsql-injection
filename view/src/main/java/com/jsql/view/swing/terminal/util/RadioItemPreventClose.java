package com.jsql.view.swing.terminal.util;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class RadioItemPreventClose extends JRadioButtonMenuItem {
    public RadioItemPreventClose(String text) {
        super(text);
    }
    public RadioItemPreventClose(String text, boolean selected) {
        super(text, selected);
    }
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_RELEASED && this.contains(e.getPoint())) {
            this.doClick();
            this.setArmed(true);
        } else {
            super.processMouseEvent(e);
        }
    }
}
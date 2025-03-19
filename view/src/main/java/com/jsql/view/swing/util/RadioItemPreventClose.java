package com.jsql.view.swing.util;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class RadioItemPreventClose extends JRadioButtonMenuItem {
    public RadioItemPreventClose(String text) {
        super(text);
    }
    public RadioItemPreventClose(String text, boolean selected) {
        super(text, selected);
    }
    public RadioItemPreventClose(String text, ImageIcon icon, boolean selected) {
        super(text, icon, selected);
    }
    public RadioItemPreventClose(AbstractAction a) {
        super(a);
    }
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (!RadioItemPreventClose.preventClose(e, this)) {
            super.processMouseEvent(e);
        }
    }
    public static boolean preventClose(MouseEvent e, JMenuItem m) {
        if (e.getID() == MouseEvent.MOUSE_RELEASED && m.contains(e.getPoint())) {
            m.doClick();
            m.setArmed(true);
            return true;
        }
        return false;
    }
}
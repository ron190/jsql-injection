package com.jsql.view.swing.util;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class RadioItemNonClosing extends JRadioButtonMenuItem {

    public RadioItemNonClosing(String text) {
        super(text);
    }

    public RadioItemNonClosing(String text, boolean selected) {
        super(text, selected);
    }

    public RadioItemNonClosing(String text, ImageIcon icon, boolean selected) {
        super(text, icon, selected);
    }

    public RadioItemNonClosing(AbstractAction a) {
        super(a);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (RadioItemNonClosing.shouldClose(e, this)) {
            super.processMouseEvent(e);
        }
    }

    public static boolean shouldClose(MouseEvent e, JMenuItem m) {
        if (e.getID() == MouseEvent.MOUSE_RELEASED && m.contains(e.getPoint())) {
            m.doClick();
            m.setArmed(true);
            return false;
        }
        return true;
    }
}
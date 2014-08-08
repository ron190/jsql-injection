package com.jsql.view.radio;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.jsql.view.GUITools;

public class RadioMouseAdapter extends MouseAdapter {
    private Font original;

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        
        RadioLink radio = (RadioLink) e.getComponent();
        
        if (radio.isActivable() && SwingUtilities.isLeftMouseButton(e)) {
            for (JLabel r: radio.getGroup()) {
                if (((JLabel) e.getComponent()) != r) {
                    r.setFont(GUITools.MYFONT);
                } else {
                    radio.action();
                }
            }

            radio.setUnderlined();

            original = e.getComponent().getFont();
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        
        RadioLink radio = (RadioLink) e.getComponent();
        
        original = e.getComponent().getFont();

        if (radio.isActivable()) {
            Font font = radio.getFont();
            Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>(font.getAttributes());
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            radio.setFont(font.deriveFont(attributes));
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        
        RadioLink radio = (RadioLink) e.getComponent();
        
        radio.setFont(original);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
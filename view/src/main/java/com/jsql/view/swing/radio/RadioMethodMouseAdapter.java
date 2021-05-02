package com.jsql.view.swing.radio;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.jsql.view.swing.util.UiUtil;

/**
 * Mouse adapter for radio link effect (hover and click).
 */
public class RadioMethodMouseAdapter extends MouseAdapter {
    
    /**
     * Font to display on mouse exit: underline or bold.
     */
    private Font original;

    @Override
    public void mouseClicked(MouseEvent e) {
        
        super.mouseClicked(e);
        
        AbstractRadioLink radio = (AbstractRadioLink) e.getComponent();
        
        if (radio.isActivable() && SwingUtilities.isLeftMouseButton(e)) {
            
            for (JLabel label: radio.getGroup()) {
                
                if ((JLabel) e.getComponent() != label) {
                    
                    label.setFont(UiUtil.FONT_NON_MONO);
                    
                } else {
                    
                    radio.action();
                }
            }

            radio.setUnderlined();

            this.original = e.getComponent().getFont();
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
        super.mouseEntered(e);
        
        AbstractRadioLink radio = (AbstractRadioLink) e.getComponent();
        
        this.original = e.getComponent().getFont();

        if (radio.isActivable()) {
            
            var font = radio.getFont();
            Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            radio.setFont(font.deriveFont(attributes));
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
        super.mouseExited(e);
        
        AbstractRadioLink radio = (AbstractRadioLink) e.getComponent();
        
        radio.setFont(this.original);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
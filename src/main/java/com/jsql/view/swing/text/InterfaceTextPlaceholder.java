package com.jsql.view.swing.text;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.text.JTextComponent;

public interface InterfaceTextPlaceholder {
    
    default void drawPlaceholder(JTextComponent textComponent, Graphics g, String placeholderText) {
        int w = textComponent.getWidth();
        
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        Insets ins = textComponent.getInsets();
        FontMetrics fm = g.getFontMetrics();
        
        int c0 = textComponent.getBackground().getRGB();
        int c1 = textComponent.getForeground().getRGB();
        int m = 0xfefefefe;
        int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
        
        g.setColor(new Color(c2, true));
        g.setFont(textComponent.getFont().deriveFont(Font.ITALIC));
        
        g.drawString(
            placeholderText,
            textComponent.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT
                ? w - (fm.stringWidth(placeholderText) + ins.left + 2)
                : ins.left + 2,
            fm.getAscent() + 2
        );
    }

}

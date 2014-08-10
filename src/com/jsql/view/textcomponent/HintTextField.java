package com.jsql.view.textcomponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JTextField;

/**
 * Textfield with information text displayed when empty.
 */
@SuppressWarnings("serial")
public class HintTextField extends JTextField {
    /**
     * Text to display when empty.
     */
    private String hint = "";
    
    /**
     * Create a textfield with hint and default value.
     * @param hint Text displayed when empty
     * @param value Default value
     */
    public HintTextField(String hint, String value) {
        this(hint);
        setText(value);
    }
    
    /**
     * Create a textfield with hint.
     * @param hint Text displayed when empty
     */
    public HintTextField(String hint) {
        this.hint = hint;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.setFont(this.getFont().deriveFont(Font.ITALIC));
            g.drawString(hint, ins.left + 2, h / 2 + fm.getAscent() / 2 - 1);
        }
    }
}
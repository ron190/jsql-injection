/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;

/**
 * A JTextField with globe icon displayed on the left. 
 */
@SuppressWarnings("serial")
public class JTextFieldWithIcon extends JTextField {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(JTextFieldWithIcon.class);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        URL url = JAddressBar.class.getResource("/com/jsql/view/swing/images/globe.png");
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        Border border = UIManager.getBorder("TextField.border");

        int x = border.getBorderInsets(this).left;
        int y = (getHeight() - image.getHeight()) / 2;

        g.drawImage(image, x + 4, y + 1, this);
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
            g.drawString(I18n.ENTER_ADDRESS, ins.left, h / 2 + fm.getAscent() / 2 - 1);
        }
    }
}
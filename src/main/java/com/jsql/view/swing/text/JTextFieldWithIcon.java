/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.text;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * A JTextField with globe icon displayed on the left. 
 */
@SuppressWarnings("serial")
public class JTextFieldWithIcon extends JTextFieldPlaceholder {

    public JTextFieldWithIcon(String placeholder) {
        super(placeholder);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        URL url = JTextFieldWithIcon.class.getResource("/com/jsql/view/swing/resources/images/icons/globe.png");
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        } catch (IOException | IllegalArgumentException e) {
            // Fix IllegalArgumentException when globe.png is unavailable
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        }

        Border border = UIManager.getBorder("TextField.border");

        int x = border.getBorderInsets(this).left;
        int y = (getHeight() - image.getHeight()) / 2;

        // Fix #1654 (Linux only) : ClassCastException: sun.awt.image.BufImgSurfaceData cannot be cast to sun.java2d.xr.XRSurfaceData
        try {
            g.drawImage(image, x + 4, y + 1, this);
        } catch (ClassCastException e) {
            // Ignore Exception
        }
    }
}
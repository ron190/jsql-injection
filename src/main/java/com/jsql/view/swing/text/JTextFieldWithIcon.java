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
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
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
        Image image = null;
        try {
            image = new ImageIcon(url).getImage();
        } catch (IllegalArgumentException e) {
            // Ignore Exception when globe.png is unavailable
        }

        Border border = UIManager.getBorder("TextField.border");

        int x = border.getBorderInsets(this).left;
        int y = (getHeight() - 16) / 2;

        // Fix #1654 (Linux only) : ClassCastException: sun.awt.image.BufImgSurfaceData cannot be cast to sun.java2d.xr.XRSurfaceData
        try {
            g.drawImage(image, x + 4, y + 1, this);
        } catch (ClassCastException e) {
            // Ignore Exception
        }
    }
}
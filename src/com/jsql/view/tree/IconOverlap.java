/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.tree;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * An icon composed of a main icon and
 * another one displayed in the nottom right corner.
 */
public class IconOverlap extends ImageIcon {
    private static final long serialVersionUID = 7320065670040840803L;

    /**
     * The icon displayed on the bottom right corner.
     */
    private String overlap;

    public IconOverlap(String main, String overlap){
        super(IconOverlap.class.getResource(main));

        this.overlap = overlap;
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        super.paintIcon(c, g, x, y);
        try {
            BufferedImage im2 = ImageIO.read(IconOverlap.class.getResource(overlap));
            g.drawImage(im2, (this.getIconWidth()-im2.getWidth())/2, (this.getIconHeight()-im2.getHeight())/2, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

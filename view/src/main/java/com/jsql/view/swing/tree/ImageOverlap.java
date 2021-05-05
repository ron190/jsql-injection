/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * An icon composed of a main icon and another one displayed in the bottom right corner.
 */
@SuppressWarnings("serial")
public class ImageOverlap extends ImageIcon {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * The path of icon displayed on the bottom right corner.
     */
    private String iconPathOverlap;

    /**
     * Create icon with tiny icon on top layer.
     * @param main Main icon to display
     * @param iconPathOverlap Secondary icon to display on top of main icon
     */
    public ImageOverlap(String main, String iconPathOverlap) {
        
        super(ImageOverlap.class.getClassLoader().getResource(main));

        this.iconPathOverlap = iconPathOverlap;
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        
        super.paintIcon(c, g, x, y);
        
        try {
            BufferedImage im2 = ImageIO.read(ImageOverlap.class.getClassLoader().getResource(this.iconPathOverlap));
            
            g.drawImage(
                im2,
                (this.getIconWidth() - im2.getWidth()) / 2,
                (this.getIconHeight() - im2.getHeight()) / 2,
                null
            );
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}

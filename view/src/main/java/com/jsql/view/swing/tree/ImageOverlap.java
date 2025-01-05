/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.tree;

import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * An icon composed of a main icon and another one displayed in the bottom right corner.
 */
public class ImageOverlap extends ImageIcon {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * The path of icon displayed in the bottom right corner.
     */
    private final String iconPathOverlap;

    /**
     * Create icon with tiny icon on top layer.
     * @param main Main icon to display
     * @param iconPathOverlap Secondary icon to display on top of main icon
     */
    public ImageOverlap(ImageIcon main, String iconPathOverlap) {
        super(main.getImage());
        this.iconPathOverlap = iconPathOverlap;
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        super.paintIcon(c, g, x, y);
        try {
            BufferedImage bufferedImage = ImageIO.read(
                Objects.requireNonNull(ImageOverlap.class.getClassLoader().getResource(this.iconPathOverlap))
            );
            
            g.drawImage(
                bufferedImage,
                (this.getIconWidth() - bufferedImage.getWidth()) / 2,
                (this.getIconHeight() - bufferedImage.getHeight()) / 2,
                null
            );
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
}

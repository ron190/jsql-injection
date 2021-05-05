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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JProgressBar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * A progress bar with a Pause icon over it.
 */
@SuppressWarnings("serial")
public class ProgressBarPausable extends JProgressBar {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * True if icon should be displayed, false otherwise.
     */
    private boolean isIconDisplayed = false;

    @Override
    public void paint(Graphics g) {
        
        // Fix #42285: InternalError on paint()
        try {
            super.paint(g);
            
        } catch (InternalError e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        if (this.isIconDisplayed) {
            
            try {
                BufferedImage im2 = ImageIO.read(ProgressBarPausable.class.getClassLoader().getResource(UiUtil.PATH_PAUSE));
                g.drawImage(
                    im2,
                    (this.getWidth() - im2.getWidth()) / 2,
                    (this.getHeight() - im2.getHeight()) / 2,
                    null
                );
                
            } catch (IOException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }
    }

    /**
     * Activate pause state, hence display pause icon.
     */
    public void pause() {
        
        this.isIconDisplayed = true;
    }
}

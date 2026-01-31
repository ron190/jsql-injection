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
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * A progress bar with a Pause icon over it.
 */
public class ProgressBarPausable extends JProgressBar {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * True if icon should be displayed, false otherwise.
     */
    private boolean isIconDisplayed = false;

    @Override
    public void paint(Graphics graphics) {
        // Fix #42285: InternalError on paint()
        try {
            super.paint(graphics);
        } catch (InternalError e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }

        if (this.isIconDisplayed) {
            try {
                BufferedImage bufferedImage = ImageIO.read(
                    Objects.requireNonNull(ProgressBarPausable.class.getClassLoader().getResource(UiUtil.PATH_PAUSE))
                );
                graphics.drawImage(
                    bufferedImage,
                    (this.getWidth() - bufferedImage.getWidth()) / 2,
                    (this.getHeight() - bufferedImage.getHeight()) / 2,
                    null
                );
            } catch (IOException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
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

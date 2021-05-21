/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.text;

import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * A JTextField with globe icon displayed on the left.
 */
@SuppressWarnings("serial")
public class JTextFieldWithIcon extends JTextFieldPlaceholder {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    public JTextFieldWithIcon(String placeholder) {
        super(placeholder);
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        // Unhandled InternalError #92917: Unable to Stroke shape (no dcpr in java.library.path)
        super.paintComponent(g);

        var url = UiUtil.URL_GLOBE;
        
        if (url == null) {
            // Fix NullPointerException in constructor ImageIcon()
            return;
        }
        
        Image image = null;
        
        try {
            image = new ImageIcon(url).getImage();
            
        } catch (IllegalArgumentException e) {
            
            // Exception if globe.png is unavailable
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        var border = UIManager.getBorder("TextField.border");

        int x = border.getBorderInsets(this).left;
        int y = (this.getHeight() - 16) / 2;

        // Fix #1654 (Linux only) : ClassCastException: sun.awt.image.BufImgSurfaceData cannot be cast to sun.java2d.xr.XRSurfaceData
        try {
            g.drawImage(
                image,
                ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                ? JTextFieldWithIcon.this.getWidth() - (16 + x + 4)
                : x + 4,
                y + 1,
                this
            );
            
        } catch (ClassCastException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}
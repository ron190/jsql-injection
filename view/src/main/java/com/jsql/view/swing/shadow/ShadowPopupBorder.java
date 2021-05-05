package com.jsql.view.swing.shadow;

/*
 * Copyright (c) 2001-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

/**
 * A border with a drop shadow intended to be used as the outer border
 * of popups. Can paint the screen background if used with heavy-weight
 * popup windows.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.9 $
 *
 * @see ShadowPopup
 * @see ShadowPopupFactory
 */
@SuppressWarnings("serial")
public final class ShadowPopupBorder extends AbstractBorder {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * The drop shadow needs 5 pixels at the bottom and the right hand side.
     */
    private static final int SHADOW_SIZE = 5;

    /**
     * The singleton instance used to draw all borders.
     */
    private static ShadowPopupBorder instance = new ShadowPopupBorder();

    /**
     * The drop shadow is created from a PNG image with 8 bit alpha channel.
     */
    private static Image shadow = UiUtil.IMG_SHADOW;


    // Instance Creation *****************************************************

    /**
     * Returns the singleton instance used to draw all borders.
     */
    public static ShadowPopupBorder getInstance() {
        return instance;
    }

    /**
     * Paints the border for the specified component with the specified
     * position and size.
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        
        // Fix #7497, Fix #8247, Fix #8332: BufImgSurfaceData cannot be cast to XRSurfaceData on drawImage()
        try {
            // fake drop shadow effect in case of heavy weight popups
            JComponent popup = (JComponent) c;
            
            Image hShadowBg = (Image) popup.getClientProperty(ShadowPopupFactory.PROP_HORIZONTAL_BACKGROUND);
            
            if (hShadowBg != null) {
                
                g.drawImage(hShadowBg, x, y + height - 5, c);
            }
            
            Image vShadowBg = (Image) popup.getClientProperty(ShadowPopupFactory.PROP_VERTICAL_BACKGROUND);
            
            if (vShadowBg != null) {
                
                g.drawImage(vShadowBg, x + width - 5, y, c);
            }
    
            // draw drop shadow
            g.drawImage(shadow, x +  5, y + height - 5, x + 10, y + height, 0, 6, 5, 11, null, c);
            g.drawImage(shadow, x + 10, y + height - 5, x + width - 5, y + height, 5, 6, 6, 11, null, c);
            g.drawImage(shadow, x + width - 5, y + 5, x + width, y + 10, 6, 0, 11, 5, null, c);
            g.drawImage(shadow, x + width - 5, y + 10, x + width, y + height - 5, 6, 5, 11, 6, null, c);
            g.drawImage(shadow, x + width - 5, y + height - 5, x + width, y + height, 6, 6, 11, 11, null, c);
            
        } catch (ClassCastException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }


    /**
     * Returns the insets of the border.
     */
    @Override
    public Insets getBorderInsets(Component c) {
        
        return new Insets(0, 0, SHADOW_SIZE, SHADOW_SIZE);
    }


    /**
     * Reinitializes the insets parameter with this Border's current Insets.
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     * @return the {@code insets} object
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        
        insets.left = insets.top = 0;
        insets.right = insets.bottom = SHADOW_SIZE;
        return insets;
    }
}

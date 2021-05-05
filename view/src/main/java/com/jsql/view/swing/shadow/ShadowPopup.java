package com.jsql.view.swing.shadow;

/*
 * Copyright (c) 2007-2013 JGoodies Software GmbH. All Rights Reserved.
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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * Does all the magic for getting popups with drop shadows.
 * It adds the drop shadow border to the Popup,
 * in {@code #show} it snapshots the screen background as needed,
 * and in {@code #hide} it cleans up all changes made before.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.12 $
 *
 * @see com.jgoodies.looks.common.ShadowPopupBorder
 * @see com.jgoodies.looks.common.ShadowPopupFactory
 */
public final class ShadowPopup extends Popup {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Max number of items to store in the cache.
     */
    private static final int MAX_CACHE_SIZE = 5;

    /**
     * The cache to use for ShadowPopups.
     */
    private static List<ShadowPopup> cache;

    /**
     * The singleton instance used to draw all borders.
     */
    private static final Border SHADOW_BORDER = ShadowPopupBorder.getInstance();

    /**
     * The size of the drop shadow.
     */
    private static final int SHADOW_SIZE = 5;

    /**
     * Indicates whether we can make snapshots from screen or not.
     */
    private boolean canSnapshot = true;

    /**
     * The component mouse coordinates are relative to, may be null.
     */
    private Component owner;

    /**
     * The contents of the popup.
     */
    private Component contents;

    /**
     * The desired x and y location of the popup.
     */
    private int x;
    private int y;

    /**
     * The real popup. The #show() and #hide() methods will delegate
     * all calls to these popup.
     */
    private Popup popup;

    /**
     * The border of the contents' parent replaced by SHADOW_BORDER.
     */
    private Border oldBorder;

    /**
     * The old value of the opaque property of the contents' parent.
     */
    private boolean oldOpaque;

    /**
     * The heavy weight container of the popup contents, may be null.
     */
    private Container heavyWeightContainer;

    /**
     * The 'scratch pad' objects used to calculate dirty regions of
     * the screen snapshots.
     *
     * @see #snapshot()
     */
    private static final Point     POINT = new Point();
    private static final Rectangle RECT  = new Rectangle();

    /**
     * Returns a previously used {@code ShadowPopup}, or a new one
     * if none of the popups have been recycled.
     */
    public static Popup getInstance(Component owner, Component contents, int x, int y, Popup delegate) {
        
        ShadowPopup result;
        
        synchronized (ShadowPopup.class) {
            
            if (cache == null) {
                
                cache = new ArrayList<>(MAX_CACHE_SIZE);
            }
            
            if (!cache.isEmpty()) {
                
                result = cache.remove(0);
                
            } else {
                
                result = new ShadowPopup();
            }
        }
        
        result.reset(owner, contents, x, y, delegate);
        
        return result;
    }

    /**
     * Recycles the ShadowPopup.
     */
    private static void recycle(ShadowPopup popup) {
        
        synchronized (ShadowPopup.class) {
            
            if (cache.size() < MAX_CACHE_SIZE) {
                
                cache.add(popup);
            }
        }
    }

    public boolean canSnapshot() {
        
        return this.canSnapshot;
    }

    /**
     * Hides and disposes of the {@code Popup}. Once a {@code Popup}
     * has been disposed you should no longer invoke methods on it. A
     * {@code dispose}d {@code Popup} may be reclaimed and later used
     * based on the {@code PopupFactory}. As such, if you invoke methods
     * on a {@code disposed} {@code Popup}, indeterminate
     * behavior will result.<p>
     *
     * In addition to the superclass behavior, we reset the stored
     * horizontal and vertical drop shadows - if any.
     */
    @Override
    public void hide() {
        
        if (this.contents == null) {
            
            return;
        }

        JComponent parent = (JComponent) this.contents.getParent();
        this.popup.hide();
        
        if ((parent != null) && parent.getBorder() == SHADOW_BORDER) {
            
            parent.setBorder(this.oldBorder);
            parent.setOpaque(this.oldOpaque);
            this.oldBorder = null;
            
            if (this.heavyWeightContainer != null) {
                
                parent.putClientProperty(ShadowPopupFactory.PROP_HORIZONTAL_BACKGROUND, null);
                parent.putClientProperty(ShadowPopupFactory.PROP_VERTICAL_BACKGROUND, null);
                this.heavyWeightContainer = null;
            }
        }
        
        this.owner = null;
        this.contents = null;
        this.popup = null;
        
        recycle(this);
    }

    /**
     * Makes the {@code Popup} visible. If the popup has a
     * heavy-weight container, we try to snapshot the background.
     * If the {@code Popup} is currently visible, it remains visible.
     */
    @Override
    public void show() {
        
        if (this.heavyWeightContainer != null) {
            
            this.snapshot();
        }
        
        this.popup.show();
    }

    /**
     * Reinitializes this ShadowPopup using the given parameters.
     *
     * @param owner component mouse coordinates are relative to, may be null
     * @param contents the contents of the popup
     * @param x the desired x location of the popup
     * @param y the desired y location of the popup
     * @param popup the popup to wrap
     */
    private void reset(
        Component owner, Component contents, int x, int y,
        Popup popup
    ) {
        
        this.owner = owner;
        this.contents = contents;
        this.popup = popup;
        this.x = x;
        this.y = y;
        
        // Do not install the shadow border when the contents
        // has a preferred size less than or equal to 0.
        // We can't use the size, because it is(0, 0) for new popups.
        var contentsPrefSize = new Dimension();
        
        // Fix #4172: NullPointerException on getPreferredSize()
        // Implementation by javax.swing.plaf.metal.MetalToolTipUI.getPreferredSize()
        try {
            contentsPrefSize = contents.getPreferredSize();
            
        } catch(NullPointerException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        if (contentsPrefSize.width <= 0 || contentsPrefSize.height <= 0) {
            
            return;
        }
        
        for (Container p = contents.getParent() ; p != null ; p = p.getParent()) {
            
            if (p instanceof JWindow || p instanceof Panel) {
                
                // Workaround for the gray rect problem.
                p.setBackground(contents.getBackground());
                this.heavyWeightContainer = p;
                
                break;
            }
        }
        
        JComponent parent = (JComponent) contents.getParent();
        this.oldOpaque = parent.isOpaque();
        this.oldBorder = parent.getBorder();
        parent.setOpaque(false);
        parent.setBorder(SHADOW_BORDER);
        
        // Pack it because we have changed the border.
        if (this.heavyWeightContainer != null) {
            
            this.heavyWeightContainer.setSize(this.heavyWeightContainer.getPreferredSize());
            
        } else {
            
            parent.setSize(parent.getPreferredSize());
        }
    }

    /**
     * Snapshots the background. The snapshots are stored as client
     * properties of the contents' parent. The next time the border is drawn,
     * this background will be used.<p>
     *
     * Uses a robot on the default screen device to capture the screen
     * region under the drop shadow. Does <em>not</em> use the window's
     * device, because that may be an outdated device (due to popup reuse)
     * and the robot's origin seems to be adjusted with the default screen
     * device.
     *
     * @see #show()
     * @see com.jgoodies.looks.common.ShadowPopupBorder
     * @see Robot#createScreenCapture(Rectangle)
     */
    private void snapshot() {
        
        try {
            Dimension size = this.heavyWeightContainer.getPreferredSize();
            int width = size.width;
            int height = size.height;

            // Avoid unnecessary and illegal screen captures
            // for degenerated popups.
            if ((width <= 0) || (height <= SHADOW_SIZE)) {
                
                return;
            }

            var robot = new Robot(); // uses the default screen device

            RECT.setBounds(this.x, this.y + height - SHADOW_SIZE, width, SHADOW_SIZE);
            BufferedImage hShadowBg = robot.createScreenCapture(RECT);

            RECT.setBounds(this.x + width - SHADOW_SIZE, this.y, SHADOW_SIZE,
                    height - SHADOW_SIZE);
            BufferedImage vShadowBg = robot.createScreenCapture(RECT);

            JComponent parent = (JComponent) this.contents.getParent();
            parent.putClientProperty(ShadowPopupFactory.PROP_HORIZONTAL_BACKGROUND, hShadowBg);
            parent.putClientProperty(ShadowPopupFactory.PROP_VERTICAL_BACKGROUND, vShadowBg);

            Container layeredPane = this.getLayeredPane();
            if (layeredPane == null) {
                
                // This could happen if owner is null.
                return;
            }

            int layeredPaneWidth = layeredPane.getWidth();
            int layeredPaneHeight = layeredPane.getHeight();

            POINT.x = this.x;
            POINT.y = this.y;
            SwingUtilities.convertPointFromScreen(POINT, layeredPane);

            this.paintHorizontalSnapshot(width, height, hShadowBg, layeredPane, layeredPaneWidth, layeredPaneHeight);

            this.paintVerticalSnapshot(width, height, vShadowBg, layeredPane, layeredPaneWidth, layeredPaneHeight);
            
        } catch (AWTException | SecurityException | IllegalArgumentException e) {
            
            this.canSnapshot = false;
            
            LOGGER.log(LogLevel.IGNORE, e);
        }
    }

    private void paintVerticalSnapshot(int width, int height, BufferedImage vShadowBg, Container layeredPane, int layeredPaneWidth, int layeredPaneHeight) {
        
        // If needed paint dirty region of the vertical snapshot.
        RECT.x = POINT.x + width - SHADOW_SIZE;
        RECT.y = POINT.y;
        RECT.width = SHADOW_SIZE;
        RECT.height = height - SHADOW_SIZE;

        this.extracted(vShadowBg, layeredPane, layeredPaneWidth, layeredPaneHeight);
    }

    private void paintHorizontalSnapshot(int width, int height, BufferedImage hShadowBg, Container layeredPane, int layeredPaneWidth, int layeredPaneHeight) {
        
        // If needed paint dirty region of the horizontal snapshot.
        RECT.x = POINT.x;
        RECT.y = POINT.y + height - SHADOW_SIZE;
        RECT.width = width;
        RECT.height = SHADOW_SIZE;

        this.extracted(hShadowBg, layeredPane, layeredPaneWidth, layeredPaneHeight);
    }

    private void extracted(BufferedImage shadowBg, Container layeredPane, int layeredPaneWidth, int layeredPaneHeight) {
        
        if ((RECT.x + RECT.width) > layeredPaneWidth) {
            
            RECT.width = layeredPaneWidth - RECT.x;
        }
        
        if ((RECT.y + RECT.height) > layeredPaneHeight) {
            
            RECT.height = layeredPaneHeight - RECT.y;
        }
        
        if (!RECT.isEmpty()) {
            
            Graphics g = shadowBg.createGraphics();
            g.translate(-RECT.x, -RECT.y);
            g.setClip(RECT);
            
            if (layeredPane instanceof JComponent) {
                
                JComponent c = (JComponent) layeredPane;
                boolean doubleBuffered = c.isDoubleBuffered();
                c.setDoubleBuffered(false);
                ShadowPopup.paintAll(c, g);
                c.setDoubleBuffered(doubleBuffered);
                
            } else {
                
                layeredPane.paintAll(g);
            }
            
            g.dispose();
        }
    }
    
    private static void paintAll(JComponent c, Graphics g) {
        
        // Fix #3127, Fix #6772, Fix #48907: Multiple Exceptions on paintAll()
        try {
            c.paintAll(g);
            
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
    }

    /**
     * @return the top level layered pane which contains the owner.
     */
    private Container getLayeredPane() {
        
        // The code below is copied from PopupFactory#LightWeightPopup#show()
        Container parent = null;
        
        if (this.owner != null) {
            
            parent =
                this.owner instanceof Container
                ? (Container) this.owner
                : this.owner.getParent();
        }
        
        // Try to find a JLayeredPane and Window to add
        for (Container p = parent; p != null; p = p.getParent()) {
            
            if (p instanceof JRootPane) {
                
                if (!(p.getParent() instanceof JInternalFrame)) {
                    
                    parent = ((JRootPane) p).getLayeredPane();
                }
                // Continue, so that if there is a higher JRootPane, we'll
                // pick it up.
                
            } else if (p instanceof Window) {
                
                if (parent == null) {
                    parent = p;
                }
                
                break;
            }
        }
        
        return parent;
    }
}

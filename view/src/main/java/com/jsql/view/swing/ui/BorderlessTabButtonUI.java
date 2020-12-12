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
package com.jsql.view.swing.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import com.jsql.view.swing.util.UiUtil;

/**
 * Tab UI to remove inner borders on empty tabbedpane and force header height on Linux.
 */
public class BorderlessTabButtonUI extends MetalTabbedPaneUI {
    
    @Override
    protected JButton createScrollButton(int direction) {
        
        if (direction != SOUTH && direction != NORTH && direction != EAST && direction != WEST) {
            
            throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST");
        }

        return new ScrollableTabButton(direction);
    }
    
    @SuppressWarnings("serial")
    private class ScrollableTabButton extends BasicArrowButton implements UIResource, SwingConstants {
        
        public ScrollableTabButton(int direction) {
            
            super(
                direction,
                UiUtil.COLOR_DEFAULT_BACKGROUND,
                UIManager.getColor("TabbedPane.darkShadow"),
                new Color(122, 138, 153),
                UIManager.getColor("TabbedPane.highlight")
            );
            
            this.setBorder(BorderFactory.createEmptyBorder());
            this.setOpaque(false);
            this.setBorderPainted(false);
        }
    }
}

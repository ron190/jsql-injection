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
package com.jsql.view.swing.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import com.jsql.view.swing.HelperUi;

/**
 * Tab UI to remove inner borders on empty tabbedpane and force header height on Linux.
 */
public class CustomMetalTabbedPaneUI extends MetalTabbedPaneUI {
    
    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // Do nothing
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // Do nothing
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // Do nothing
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // Do nothing
    }

    @Override
    protected int calculateMaxTabHeight(int tabPlacement) {
        return 22;
    }
    
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
                HelperUi.COLOR_DEFAULT_BACKGROUND,
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

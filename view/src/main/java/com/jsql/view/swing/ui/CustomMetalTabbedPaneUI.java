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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import com.jsql.view.swing.util.UiUtil;

/**
 * Tab UI to remove inner borders on empty tabbedpane and force header height on
 * Linux.
 */
public class CustomMetalTabbedPaneUI extends BorderlessTabButtonUI {
    
    private static final float ADJ2 = 0f;
    
    private static final Color TAB_BACKGROUND = UiUtil.COLOR_DEFAULT_BACKGROUND;
    private static final Color TAB_BORDER = UiUtil.COLOR_COMPONENT_BORDER;
    
    @Override
    protected int calculateMaxTabHeight(int tabPlacement) {
        return 22;
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
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
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // Do nothing
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Do nothing
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // Do nothing
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        int tabCount = this.tabPane.getTabCount();

        var iconRect = new Rectangle();
        var textRect = new Rectangle();
        Rectangle clipRect = g.getClipBounds();

        // copied from BasicTabbedPaneUI#paintTabArea(...)
        for (int i = this.runCount - 1 ; i >= 0 ; i--) {
            
            int start = this.tabRuns[i];
            int next = this.tabRuns[(i == this.runCount - 1) ? 0 : i + 1];
            int end = next == 0 ? tabCount - 1 : next - 1;
            
            // https://stackoverflow.com/questions/41566659/tabs-rendering-order-in-custom-jtabbedpane
            for (int j = end; j >= start; j--) {
                
                if (j != selectedIndex && this.rects[j].intersects(clipRect)) {
                    
                    this.paintTab(g, tabPlacement, this.rects, j, iconRect, textRect);
                }
            }
        }
        
        if (selectedIndex >= 0 && this.rects[selectedIndex].intersects(clipRect)) {
            
            this.paintTab(g, tabPlacement, this.rects, selectedIndex, iconRect, textRect);
        }
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        var textShiftOffset = 0f;
        var trapezoid = new GeneralPath();
        trapezoid.moveTo(x - ADJ2, (float) y + h);
        trapezoid.lineTo(x + ADJ2, y + textShiftOffset);
        trapezoid.lineTo(x + w - ADJ2, y + textShiftOffset);
        trapezoid.lineTo(x + w + ADJ2, (float) y + h);

        g2.setColor(isSelected ? UiUtil.COLOR_FOCUS_GAINED : TAB_BACKGROUND);
        g2.fill(trapezoid);

        g2.setColor(TAB_BORDER);
        g2.draw(trapezoid);

        g2.dispose();
    }
}

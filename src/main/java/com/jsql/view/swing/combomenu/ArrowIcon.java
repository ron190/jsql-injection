package com.jsql.view.swing.combomenu;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import org.apache.log4j.Logger;

import com.jsql.view.swing.interaction.MessageChunk;

public class ArrowIcon implements Icon, SwingConstants {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ArrowIcon.class);
    
    private static final int DEFAULT_SIZE = 9;

    private int size;
    private int iconSize;
    private int direction;
    private boolean isEnabled;
    private BasicArrowButton iconRenderer;

    public ArrowIcon(int direction, boolean isPressedView) {
        this(DEFAULT_SIZE, direction, isPressedView);
    }

    public ArrowIcon(int iconSize, int direction, boolean isEnabled) {
        this.size = iconSize / 2;
        this.iconSize = iconSize;
        this.direction = direction;
        this.isEnabled = isEnabled;
        iconRenderer = new BasicArrowButton(direction);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        try {
            iconRenderer.paintTriangle(g, x, y + 3, size, direction, isEnabled);
        } catch(ClassCastException e) {
            // Fix #4731
            LOGGER.error(e, e);
        }
    }

    @Override
    public int getIconWidth() {
        switch (direction) {
            case NORTH:
            case SOUTH: return iconSize;
            case EAST:
            case WEST: 
            default: return size;
        }
    }

    @Override
    public int getIconHeight() {
        switch (direction) {
            case NORTH:
            case SOUTH: return size;
            case EAST:
            case WEST: 
            default: return iconSize;
        }
    }
}
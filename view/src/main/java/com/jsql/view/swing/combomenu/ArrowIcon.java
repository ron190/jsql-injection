package com.jsql.view.swing.combomenu;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

public class ArrowIcon implements Icon, SwingConstants {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
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
        this.iconRenderer = new BasicArrowButton(direction);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        
        // Fix #4731: ClassCastException on paintTriangle()
        // Implementation by sun.awt.image
        try {
            this.iconRenderer.paintTriangle(g, x, y + 3, this.size, this.direction, this.isEnabled);
            
        } catch(ClassCastException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    @Override
    public int getIconWidth() {
        
        switch (this.direction) {
        
            case NORTH:
            case SOUTH: return this.iconSize;
            case EAST:
            case WEST:
            default: return this.size;
        }
    }

    @Override
    public int getIconHeight() {
        
        switch (this.direction) {
        
            case NORTH:
            case SOUTH: return this.size;
            case EAST:
            case WEST:
            default: return this.iconSize;
        }
    }
}
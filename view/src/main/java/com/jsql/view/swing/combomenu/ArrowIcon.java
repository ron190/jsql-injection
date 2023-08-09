package com.jsql.view.swing.combomenu;

import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;

public class ArrowIcon implements Icon, SwingConstants {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final int DEFAULT_SIZE = 9;

    private final int size;
    private final int iconSize;
    private final int direction;
    private final boolean isEnabled;
    private final BasicArrowButton iconRenderer;

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
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
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
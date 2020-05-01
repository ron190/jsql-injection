/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.scrollpane;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.jsql.view.swing.util.UiUtil;

/**
 * Scroller with border.
 */
@SuppressWarnings("serial")
public class JScrollPanePixelBorder extends JScrollPane {
    
    /**
     * Create a scrollpane with top and left border for default component and a slide one.
     * A component slided to the right will normally hide the left border, JScrollPanePixelBorder fix this.
     * @param c Component to decorate with a scroll
     */
    public JScrollPanePixelBorder(Component c) {
        
        super(c);
        
        this.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        this.setViewportBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
    }

    /**
     * A scrollpane with custom borders
     * @param top Border top size
     * @param left Border left size
     * @param bottom Border bottom size
     * @param right Border right size
     * @param c Component to decorate
     */
    public JScrollPanePixelBorder(int top, int left, int bottom, int right, Component c) {
        
        this(c);

        this.setBorder(BorderFactory.createMatteBorder(top, 0, bottom, 0, UiUtil.COLOR_COMPONENT_BORDER));
        this.setViewportBorder(BorderFactory.createMatteBorder(0, left, 0, right, UiUtil.COLOR_COMPONENT_BORDER));
    }
}

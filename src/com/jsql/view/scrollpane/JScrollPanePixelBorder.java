/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.scrollpane;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.jsql.view.GUITools;


/**
 * Scroller with round border.
 */
@SuppressWarnings("serial")
public class JScrollPanePixelBorder extends JScrollPane {
	
	public JScrollPanePixelBorder(int top, int left, int bottom, int right, Component c){
		this(c);
		
		this.setBorder(BorderFactory.createMatteBorder(top, 0, bottom, 0, GUITools.COMPONENT_BORDER));
		this.setViewportBorder(BorderFactory.createMatteBorder(0, left, 0, right, GUITools.COMPONENT_BORDER));
	}
	
    public JScrollPanePixelBorder(Component c){
        super(c);
        
        this.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, GUITools.COMPONENT_BORDER));
        this.setViewportBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, GUITools.COMPONENT_BORDER));
    }
}

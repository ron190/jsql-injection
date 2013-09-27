/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;


/**
 * Scroller with round border.
 */
public class RoundScroller extends JScrollPane {
    private static final long serialVersionUID = -5044132525591212636L;

    public RoundScroller(Component c){
        super(c);
        
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setViewportBorder(new RoundBorder(2,2,true));
    }
}

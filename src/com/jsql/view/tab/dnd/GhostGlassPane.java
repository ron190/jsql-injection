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
package com.jsql.view.tab.dnd;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class GhostGlassPane extends JPanel {
    private DnDTabbedPane tabbedPane;
    public GhostGlassPane(DnDTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        setOpaque(false);
    }
    public void setTargetTabbedPane(DnDTabbedPane tab) {
        tabbedPane = tab;
    }
    @Override public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //tabbedPane.paintDropLine(g2);
        Rectangle rect = tabbedPane.getDropLineRect();
        if(rect!=null) {
            Rectangle r = SwingUtilities.convertRectangle(tabbedPane, rect, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2.setColor(new Color(34,177,76));
            g2.fill(r);
            //tabbedPane.paintDropLine(g2);
        }
    }
}

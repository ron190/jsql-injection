package com.jsql.view.swing.panel.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jsql.view.swing.MediatorGui;

/**
 * MouseAdapter to show/hide bottom panel.
 */
public class ActionHideShowConsole implements ActionListener {
	
    /**
     * Save the divider location when bottom panel is not visible.
     */
    private int loc = 0;

    /**
     * Ersatz panel to display in place of tabbedpane.
     */
    private JPanel panel;

    /**
     * Create the hide/show bottom panel action.
     */
    public ActionHideShowConsole(JPanel panel) {
        this.panel = panel;
    }

    /**
     * Hide bottom panel if both main and bottom are visible, also
     * displays an ersatz bar replacing tabbedpane.
     * Or else if only main panel is visible then displays bottom panel
     * and hide ersatz panel.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (
            MediatorGui.frame().splitHorizontalTopBottom.getTopComponent().isVisible() &&
            MediatorGui.frame().splitHorizontalTopBottom.getBottomComponent().isVisible()
        ) {
            MediatorGui.frame().splitHorizontalTopBottom.getBottomComponent().setVisible(false);
            this.loc = MediatorGui.frame().splitHorizontalTopBottom.getDividerLocation();
            this.panel.setVisible(true);
            MediatorGui.frame().splitHorizontalTopBottom.disableDragSize();
        } else {
            MediatorGui.frame().splitHorizontalTopBottom.getBottomComponent().setVisible(true);
            MediatorGui.frame().splitHorizontalTopBottom.setDividerLocation(this.loc);
            this.panel.setVisible(false);
            MediatorGui.frame().splitHorizontalTopBottom.enableDragSize();
        }
    }
    
}
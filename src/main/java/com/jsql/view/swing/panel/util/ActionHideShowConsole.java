package com.jsql.view.swing.panel.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.panel.PanelConsoles;

/**
 * MouseAdapter to show/hide bottom panel.
 */
public class ActionHideShowConsole implements ActionListener {
    
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
            MediatorGui.frame().getSplitHorizontalTopBottom().getTopComponent().isVisible()
            && MediatorGui.frame().getSplitHorizontalTopBottom().getBottomComponent().isVisible()
        ) {
            
            PanelConsoles.setLoc(MediatorGui.frame().getSplitHorizontalTopBottom().getDividerLocation());
            MediatorGui.frame().getSplitHorizontalTopBottom().getBottomComponent().setVisible(false);
            this.panel.setVisible(true);
            MediatorGui.frame().getSplitHorizontalTopBottom().disableDragSize();
            
        } else if (
            this.panel.isVisible()
            || !MediatorGui.frame().getSplitHorizontalTopBottom().getTopComponent().isVisible()
            && MediatorGui.frame().getSplitHorizontalTopBottom().getBottomComponent().isVisible()
        ) {
            
            MediatorGui.frame().getSplitHorizontalTopBottom().getBottomComponent().setVisible(true);
            MediatorGui.frame().getSplitHorizontalTopBottom().getTopComponent().setVisible(true);
            this.panel.setVisible(false);
            MediatorGui.frame().getSplitHorizontalTopBottom().setDividerLocation(PanelConsoles.getLoc());
            MediatorGui.frame().getSplitHorizontalTopBottom().enableDragSize();
            PanelConsoles.getButtonShowNorth().setVisible(true);
            
        }
        
    }
    
}
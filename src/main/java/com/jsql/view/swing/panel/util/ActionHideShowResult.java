package com.jsql.view.swing.panel.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.panel.PanelConsoles;

/**
 * MouseAdapter to show/hide bottom panel.
 */
public class ActionHideShowResult implements ActionListener {
    
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
            MediatorGui.frame().getSplitHorizontalTopBottom().getTopComponent().setVisible(false);
            MediatorGui.frame().getSplitHorizontalTopBottom().disableDragSize();
            PanelConsoles.getButtonShowNorth().setVisible(false);
            
        } else if (
            MediatorGui.frame().getSplitHorizontalTopBottom().getTopComponent().isVisible()
            && !MediatorGui.frame().getSplitHorizontalTopBottom().getBottomComponent().isVisible()
        ) {
            
            MediatorGui.frame().getSplitHorizontalTopBottom().setDividerLocation(PanelConsoles.getLoc());
            MediatorGui.frame().getSplitHorizontalTopBottom().getBottomComponent().setVisible(true);
            MediatorGui.frame().getSplitHorizontalTopBottom().getTopComponent().setVisible(true);
            MediatorGui.frame().getSplitHorizontalTopBottom().enableDragSize();
            
        }
        
    }
    
}
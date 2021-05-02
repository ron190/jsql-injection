package com.jsql.view.swing.panel.split;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jsql.view.swing.util.MediatorHelper;

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
        
        var splitHorizontalTopBottom = MediatorHelper.frame().getSplitHorizontalTopBottom();
        
        if (
            splitHorizontalTopBottom.getTopComponent().isVisible()
            && splitHorizontalTopBottom.getBottomComponent().isVisible()
        ) {
            
            MediatorHelper.panelConsoles().setDividerLocation(splitHorizontalTopBottom.getDividerLocation());
            splitHorizontalTopBottom.getTopComponent().setVisible(false);
            splitHorizontalTopBottom.disableDragSize();
            MediatorHelper.panelConsoles().getButtonShowNorth().setVisible(false);
            
        } else if (
            splitHorizontalTopBottom.getTopComponent().isVisible()
            && !splitHorizontalTopBottom.getBottomComponent().isVisible()
        ) {
            
            splitHorizontalTopBottom.setDividerLocation(MediatorHelper.panelConsoles().getDividerLocation());
            splitHorizontalTopBottom.getBottomComponent().setVisible(true);
            splitHorizontalTopBottom.getTopComponent().setVisible(true);
            splitHorizontalTopBottom.enableDragSize();
        }
    }
}
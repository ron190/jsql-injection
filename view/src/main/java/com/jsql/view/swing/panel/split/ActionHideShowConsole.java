package com.jsql.view.swing.panel.split;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jsql.view.swing.util.MediatorHelper;

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
        
        var splitHorizontalTopBottom = MediatorHelper.frame().getSplitHorizontalTopBottom();
        
        if (
            splitHorizontalTopBottom.getTopComponent().isVisible()
            && splitHorizontalTopBottom.getBottomComponent().isVisible()
        ) {
            
            MediatorHelper.panelConsoles().setDividerLocation(splitHorizontalTopBottom.getDividerLocation());
            splitHorizontalTopBottom.getBottomComponent().setVisible(false);
            this.panel.setVisible(true);
            splitHorizontalTopBottom.disableDragSize();
            
        } else if (
            this.panel.isVisible()
            || !splitHorizontalTopBottom.getTopComponent().isVisible()
            && splitHorizontalTopBottom.getBottomComponent().isVisible()
        ) {
            
            splitHorizontalTopBottom.getBottomComponent().setVisible(true);
            splitHorizontalTopBottom.getTopComponent().setVisible(true);
            this.panel.setVisible(false);
            splitHorizontalTopBottom.setDividerLocation(MediatorHelper.panelConsoles().getDividerLocation());
            splitHorizontalTopBottom.enableDragSize();
            MediatorHelper.panelConsoles().getButtonShowNorth().setVisible(true);
        }
    }
}
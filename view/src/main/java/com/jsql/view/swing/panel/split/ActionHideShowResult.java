package com.jsql.view.swing.panel.split;

import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        
        var split = MediatorHelper.frame().getSplitHorizontalTopBottom();
        
        if (split.getTopComponent().isVisible() && split.getBottomComponent().isVisible()) {
            MediatorHelper.panelConsoles().setDividerLocation(split.getDividerLocation());
            split.getTopComponent().setVisible(false);
            MediatorHelper.panelConsoles().getLabelShowNorth().setVisible(false);
            split.setDividerSize(0);  // required to hide bar
        } else if (split.getTopComponent().isVisible() && !split.getBottomComponent().isVisible()) {
            split.setDividerLocation(MediatorHelper.panelConsoles().getDividerLocation());
            split.getBottomComponent().setVisible(true);
            split.getTopComponent().setVisible(true);
            split.setDividerSize(UIManager.getInt("SplitPane.dividerSize"));
        }
    }
}
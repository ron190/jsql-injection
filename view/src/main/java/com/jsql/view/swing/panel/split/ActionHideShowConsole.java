package com.jsql.view.swing.panel.split;

import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MouseAdapter to show/hide bottom panel.
 */
public class ActionHideShowConsole implements ActionListener {
    
    /**
     * Ersatz panel to display in place of tabbedpane.
     */
    private final JPanel panel;
    
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
        var split = MediatorHelper.frame().getSplitHorizontalTopBottom();
        if (split.getTopComponent().isVisible() && split.getBottomComponent().isVisible()) {
            MediatorHelper.panelConsoles().setDividerLocation(split.getDividerLocation());
            split.getBottomComponent().setVisible(false);
            this.panel.setVisible(true);
            split.setDividerSize(0);  // required to hide bar
        } else if (
            this.panel.isVisible()
            || !split.getTopComponent().isVisible()
            && split.getBottomComponent().isVisible()
        ) {
            split.getBottomComponent().setVisible(true);
            split.getTopComponent().setVisible(true);
            this.panel.setVisible(false);
            split.setDividerLocation(MediatorHelper.panelConsoles().getDividerLocation());
            split.setDividerSize(UIManager.getInt("SplitPane.dividerSize"));
            MediatorHelper.panelConsoles().getLabelShowNorth().setVisible(true);
        }
    }
}
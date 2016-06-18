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
package com.jsql.view.swing.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.model.injection.InjectionModel;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.tab.TabbedPaneManagers;
import com.jsql.view.swing.tab.TabbedPaneResults;

/**
 * SplitPane composed of tree and tabs on top, and info tabs on bottom.
 */
@SuppressWarnings("serial")
public class SplitPaneCenter extends JSplitPaneWithZeroSizeDivider {
    /**
     * Name of preference for splitter vertical.
     * Reset divider position for current application version.
     */
    public static final String NAME_V_SPLITPANE = "verticalSplitter-" + InjectionModel.JSQLVERSION;
    
    /**
     * Name of preference for splitter horizontal.
     * Reset divider position for current application version. 
     */
    public static final String NAME_H_SPLITPANE = "horizontalSplitter-" + InjectionModel.JSQLVERSION;

    /**
     * SplitPane containing Manager panels on the left and result tabs on the right.
     */
    public JSplitPaneWithZeroSizeDivider leftRight;

    private static final JPanel PANEL_HIDDEN_CONSOLES = new JPanel();
    
    /**
     * MouseAdapter used on arrow on tabbedpane header and on
     * ersatz button when bottom panel is hidden.
     */
    public static final ActionHideShowConsole ACTION_HIDE_SHOW_CONSOLE = new ActionHideShowConsole(PANEL_HIDDEN_CONSOLES);

    /**
     * Create main panel with Manager panels on the left, result tabs on the right,
     * and consoles in the bottom. 
     */
    public SplitPaneCenter() {
        super(JSplitPane.VERTICAL_SPLIT, true);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        int verticalSplitter = prefs.getInt(SplitPaneCenter.NAME_V_SPLITPANE, 300);
        int horizontalSplitter = prefs.getInt(SplitPaneCenter.NAME_H_SPLITPANE, 200);

        MediatorGUI.register(new TabbedPaneManagers());
        MediatorGUI.register(new TabbedPaneResults());

        // Tree and tabs on top
        this.leftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        this.leftRight.setLeftComponent(MediatorGUI.tabManagers());
        this.leftRight.setRightComponent(MediatorGUI.tabResults());
        this.leftRight.setDividerLocation(verticalSplitter);
        this.leftRight.setDividerSize(0);
        this.leftRight.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperGUI.COMPONENT_BORDER));

        this.setDividerSize(0);
        this.setBorder(null);

        JPanel leftRightBottomPanel = new JPanel(new BorderLayout());
        leftRightBottomPanel.add(leftRight, BorderLayout.CENTER);

        PANEL_HIDDEN_CONSOLES.setLayout(new BorderLayout());
        PANEL_HIDDEN_CONSOLES.setOpaque(false);
        PANEL_HIDDEN_CONSOLES.setPreferredSize(new Dimension(17, 22));
        PANEL_HIDDEN_CONSOLES.setMaximumSize(new Dimension(17, 22));
        JButton hideBottomButton = new BasicArrowButton(BasicArrowButton.NORTH);
        hideBottomButton.setBorderPainted(false);
        hideBottomButton.setOpaque(false);

        hideBottomButton.addActionListener(SplitPaneCenter.ACTION_HIDE_SHOW_CONSOLE);
        PANEL_HIDDEN_CONSOLES.add(Box.createHorizontalGlue());
        PANEL_HIDDEN_CONSOLES.add(hideBottomButton, BorderLayout.EAST);
        PANEL_HIDDEN_CONSOLES.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperGUI.COMPONENT_BORDER));
        PANEL_HIDDEN_CONSOLES.setVisible(false);

        leftRightBottomPanel.add(PANEL_HIDDEN_CONSOLES, BorderLayout.SOUTH);

        // Setting for top and bottom components
        this.setTopComponent(leftRightBottomPanel);

        MediatorGUI.register(new PanelConsoles());

        this.setBottomComponent(MediatorGUI.panelConsoles());
        this.setDividerLocation(594 - horizontalSplitter);

        // defines left and bottom pane
        this.setResizeWeight(1);
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.panel.split;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.tab.TabManagersProxy;
import com.jsql.view.swing.tab.TabResults;
import com.jsql.view.swing.tab.TabbedPaneMouseWheelListener;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * SplitPane composed of tree and tabs on top, and info tabs on bottom.
 */
@SuppressWarnings("serial")
public class SplitHorizontalTopBottom extends JSplitPaneWithZeroSizeDivider {
    
    /**
     * Name of preference for splitter vertical.
     * Reset divider position for current application version.
     */
    private static final String NAME_V_SPLITPANE = "verticalSplitter-";
    
    /**
     * Name of preference for splitter horizontal.
     * Reset divider position for current application version.
     */
    private static final String NAME_H_SPLITPANE = "horizontalSplitter-";
    
    private static final int HORIZONTAL_SPLITTER = 200;
    private static final int VERTICAL_SPLITTER = 350;

    /**
     * SplitPane containing Manager panels on the left and result tabs on the right.
     */
    private JSplitPaneWithZeroSizeDivider splitVerticalLeftRight;

    private static final JPanel PANEL_HIDDEN_CONSOLES = new JPanel();
    
    /**
     * MouseAdapter used on arrow on tabbedpane header and on
     * ersatz button when bottom panel is hidden.
     */
    private static final ActionHideShowConsole ACTION_HIDE_SHOW_CONSOLE = new ActionHideShowConsole(PANEL_HIDDEN_CONSOLES);
    private static final ActionHideShowResult ACTION_HIDE_SHOW_RESULT= new ActionHideShowResult();

    private final JLabel labelPlaceholderResult;
    
    /**
     * Create main panel with Manager panels on the left, result tabs on the right,
     * and consoles in the bottom.
     */
    public SplitHorizontalTopBottom() {
        
        super(JSplitPane.VERTICAL_SPLIT);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        int verticalSplitter = prefs.getInt(SplitHorizontalTopBottom.NAME_V_SPLITPANE, VERTICAL_SPLITTER);
        int horizontalSplitter = prefs.getInt(SplitHorizontalTopBottom.NAME_H_SPLITPANE, HORIZONTAL_SPLITTER);
        
        if (verticalSplitter <= 30) {
            verticalSplitter = VERTICAL_SPLITTER;
        }
        
        if (horizontalSplitter <= 30) {
            horizontalSplitter = HORIZONTAL_SPLITTER;
        }

        TabManagersProxy tabManagers = new TabManagersProxy();
        MediatorHelper.register(tabManagers);

        TabResults tabResults = new TabResults();
        tabResults.addMouseWheelListener(new TabbedPaneMouseWheelListener());
        MediatorHelper.register(tabResults);

        // Tree and tabs on top
        this.splitVerticalLeftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT);
        this.splitVerticalLeftRight.setLeftComponent(tabManagers);
        
        this.labelPlaceholderResult = new JLabel(UiUtil.IMG_BUG);
        this.labelPlaceholderResult.setMinimumSize(new Dimension(100, 0));

        this.labelPlaceholderResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.labelPlaceholderResult.setAlignmentY(Component.CENTER_ALIGNMENT);
      
        this.splitVerticalLeftRight.setRightComponent(this.labelPlaceholderResult);
        this.splitVerticalLeftRight.setDividerLocation(verticalSplitter);
        this.splitVerticalLeftRight.setDividerSize(0);
        this.splitVerticalLeftRight.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));

        this.setDividerSize(0);
        this.setBorder(null);

        JPanel panelManagerResult = new JPanel(new BorderLayout());
        panelManagerResult.add(this.splitVerticalLeftRight, BorderLayout.CENTER);

        PANEL_HIDDEN_CONSOLES.setLayout(new BorderLayout());
        PANEL_HIDDEN_CONSOLES.setOpaque(false);
        PANEL_HIDDEN_CONSOLES.setPreferredSize(new Dimension(17, 22));
        PANEL_HIDDEN_CONSOLES.setMaximumSize(new Dimension(17, 22));
        
        JButton buttonShowConsoles = new BasicArrowButton(SwingConstants.NORTH);
        buttonShowConsoles.setBorderPainted(false);
        buttonShowConsoles.setOpaque(false);
        buttonShowConsoles.addActionListener(SplitHorizontalTopBottom.ACTION_HIDE_SHOW_CONSOLE);
        buttonShowConsoles.setName("buttonShowConsolesHidden");
        
        PANEL_HIDDEN_CONSOLES.add(Box.createHorizontalGlue());
        PANEL_HIDDEN_CONSOLES.add(buttonShowConsoles, BorderLayout.LINE_END);
        PANEL_HIDDEN_CONSOLES.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        PANEL_HIDDEN_CONSOLES.setVisible(false);

        panelManagerResult.add(PANEL_HIDDEN_CONSOLES, BorderLayout.SOUTH);

        // Setting for top and bottom components
        this.setTopComponent(panelManagerResult);

        PanelConsoles panelConsoles = new PanelConsoles();
        MediatorHelper.register(panelConsoles);

        this.setBottomComponent(panelConsoles);
        this.setDividerLocation(669 - horizontalSplitter);

        // defines left and bottom pane
        this.setResizeWeight(1);
    }
    
    // Getter and setter

    public static String getNameVSplitpane() {
        return NAME_V_SPLITPANE;
    }

    public static String getNameHSplitpane() {
        return NAME_H_SPLITPANE;
    }

    public JSplitPaneWithZeroSizeDivider getSplitVerticalLeftRight() {
        return this.splitVerticalLeftRight;
    }

    public static ActionHideShowConsole getActionHideShowConsole() {
        return ACTION_HIDE_SHOW_CONSOLE;
    }
    
    public static ActionHideShowResult getActionHideShowResult() {
        return ACTION_HIDE_SHOW_RESULT;
    }

    public JLabel getLabelPlaceholderResult() {
        return this.labelPlaceholderResult;
    }
}

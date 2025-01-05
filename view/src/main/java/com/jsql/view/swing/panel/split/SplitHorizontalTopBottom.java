/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.panel.split;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.tab.TabManagersCards;
import com.jsql.view.swing.tab.TabResults;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

/**
 * SplitPane composed of tree and tabs on top, and info tabs on bottom.
 */
public class SplitHorizontalTopBottom extends JSplitPane {
    
    /**
     * Name of preference for splitter vertical.
     * Reset divider position for current application version.
     */
    public static final String NAME_LEFT_RIGHT_SPLITPANE = "verticalSplitter";
    
    /**
     * Name of preference for splitter horizontal.
     * Reset divider position for current application version.
     */
    public static final String NAME_TOP_BOTTOM_SPLITPANE = "horizontalSplitter";
    
    /**
     * SplitPane containing Manager panels on the left and result tabs on the right.
     */
    private final JSplitPane splitVerticalLeftRight;

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
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        var verticalLeftRightSplitter = preferences.getInt(SplitHorizontalTopBottom.NAME_LEFT_RIGHT_SPLITPANE, 350);
        var tabManagersProxy = new TabManagersCards();
        new TabResults();  // initialized but hidden

        // Tree and tabs on top
        this.splitVerticalLeftRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitVerticalLeftRight.setLeftComponent(tabManagersProxy);
        this.labelPlaceholderResult = new JLabel(UiUtil.APP_RESULT.icon);
        this.labelPlaceholderResult.setMinimumSize(new Dimension(100, 0));
        this.splitVerticalLeftRight.setRightComponent(this.labelPlaceholderResult);
        this.splitVerticalLeftRight.setDividerLocation(verticalLeftRightSplitter);

        JLabel labelShowConsoles = new JLabel(UiUtil.ARROW_UP.icon);
        labelShowConsoles.setBorder(BorderFactory.createEmptyBorder());
        labelShowConsoles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SplitHorizontalTopBottom.ACTION_HIDE_SHOW_CONSOLE.actionPerformed(null);
            }
        });
        labelShowConsoles.setName("buttonShowConsolesHidden");
        PANEL_HIDDEN_CONSOLES.setLayout(new BorderLayout());
        PANEL_HIDDEN_CONSOLES.add(labelShowConsoles, BorderLayout.LINE_END);
        PANEL_HIDDEN_CONSOLES.setVisible(false);

        var panelManagerResult = new JPanel(new BorderLayout());
        panelManagerResult.add(this.splitVerticalLeftRight, BorderLayout.CENTER);
        panelManagerResult.add(PANEL_HIDDEN_CONSOLES, BorderLayout.SOUTH);
        this.setTopComponent(panelManagerResult);

        var panelConsoles = new PanelConsoles();
        MediatorHelper.register(panelConsoles);
        this.setBottomComponent(panelConsoles);

        this.setResizeWeight(1);
    }
    
    
    // Getter and setter

    public JSplitPane getSplitVerticalLeftRight() {
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

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
package com.jsql.view.panel;

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
import com.jsql.view.MediatorGUI;
import com.jsql.view.HelperGUI;
import com.jsql.view.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.tab.AdapterLeftTabbedPane;
import com.jsql.view.tab.AdapterRightTabbedPane;

/**
 * SplitPane composed of tree and tabs on top, and info tabs on bottom.
 */
@SuppressWarnings("serial")
public class PanelLeftRightBottom extends JSplitPaneWithZeroSizeDivider {
    /**
     * Name of preference for splitter vertical.
     * Reset divider position for current application version.
     */
    public static final String VERTICALSPLITTER_PREFNAME = "verticalSplitter-" + InjectionModel.JSQLVERSION;
    
    /**
     * Name of preference for splitter horizontal.
     * Reset divider position for current application version. 
     */
    public static final String HORIZONTALSPLITTER_PREFNAME = "horizontalSplitter-" + InjectionModel.JSQLVERSION;

    /**
     * SplitPane containing Manager panels on the left and result tabs on the right.
     */
    public JSplitPaneWithZeroSizeDivider leftRight;

    /**
     * 
     */
    private static final JPanel ERSATZARROWPANEL = new JPanel();
    
    /**
     * MouseAdapter used on arrow on tabbedpane header and on
     * ersatz button when bottom panel is hidden.
     */
    public static final ActionHideShowConsole HIDESHOWPANEL = new ActionHideShowConsole(ERSATZARROWPANEL);

    /**
     * Create main panel with Manager panels on the left, result tabs on the right,
     * and consoles in the bottom. 
     */
    public PanelLeftRightBottom() {
        super(JSplitPane.VERTICAL_SPLIT, true);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        int verticalSplitter = prefs.getInt(PanelLeftRightBottom.VERTICALSPLITTER_PREFNAME, 300);
        int horizontalSplitter = prefs.getInt(PanelLeftRightBottom.HORIZONTALSPLITTER_PREFNAME, 200);

        MediatorGUI.register(new AdapterLeftTabbedPane());
        MediatorGUI.register(new AdapterRightTabbedPane());

        // Tree and tabs on top
        this.leftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        this.leftRight.setLeftComponent(MediatorGUI.left());
        this.leftRight.setRightComponent(MediatorGUI.right());
        this.leftRight.setDividerLocation(verticalSplitter);
        this.leftRight.setDividerSize(0);
        this.leftRight.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperGUI.COMPONENT_BORDER));

        this.setDividerSize(0);
        this.setBorder(null);

        JPanel leftRightBottomPanel = new JPanel(new BorderLayout());
        leftRightBottomPanel.add(leftRight, BorderLayout.CENTER);

        ERSATZARROWPANEL.setLayout(new BorderLayout());
        ERSATZARROWPANEL.setOpaque(false);
        ERSATZARROWPANEL.setPreferredSize(new Dimension(17, 22));
        ERSATZARROWPANEL.setMaximumSize(new Dimension(17, 22));
        JButton hideBottomButton = new BasicArrowButton(BasicArrowButton.NORTH);
        hideBottomButton.setBorderPainted(false);
        hideBottomButton.setOpaque(false);

        hideBottomButton.addActionListener(PanelLeftRightBottom.HIDESHOWPANEL);
        ERSATZARROWPANEL.add(Box.createHorizontalGlue());
        ERSATZARROWPANEL.add(hideBottomButton, BorderLayout.EAST);
        ERSATZARROWPANEL.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperGUI.COMPONENT_BORDER));
        ERSATZARROWPANEL.setVisible(false);

        leftRightBottomPanel.add(ERSATZARROWPANEL, BorderLayout.SOUTH);

        // Setting for top and bottom components
        this.setTopComponent(leftRightBottomPanel);

        MediatorGUI.register(new PanelBottom());

        this.setBottomComponent(MediatorGUI.bottomPanel());
        this.setDividerLocation(601 - horizontalSplitter);

        // defines left and bottom pane
        this.setResizeWeight(1);
    }
}

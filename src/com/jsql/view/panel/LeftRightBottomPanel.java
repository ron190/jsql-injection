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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.tab.AdapterLeftTabbedPane;
import com.jsql.view.tab.AdapterRightTabbedPane;

/**
 * SplitPane composed of tree and tabs on top, and info tabs on bottom.
 */
@SuppressWarnings("serial")
public class LeftRightBottomPanel extends JSplitPaneWithZeroSizeDivider {
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
     * Create main panel with Manager panels on the left, result tabs on the right,
     * and consoles in the bottom. 
     */
    public LeftRightBottomPanel() {
        super(JSplitPane.VERTICAL_SPLIT, true);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        int verticalSplitter = prefs.getInt(LeftRightBottomPanel.VERTICALSPLITTER_PREFNAME, 300);
        int horizontalSplitter = prefs.getInt(LeftRightBottomPanel.HORIZONTALSPLITTER_PREFNAME, 200);

        GUIMediator.register(new AdapterLeftTabbedPane());
        GUIMediator.register(new AdapterRightTabbedPane());

        // Tree and tabs on top
        this.leftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        this.leftRight.setLeftComponent(GUIMediator.left());
        this.leftRight.setRightComponent(GUIMediator.right());
        this.leftRight.setDividerLocation(verticalSplitter);
        this.leftRight.setDividerSize(0);
        this.leftRight.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER));

        this.setDividerSize(0);
        this.setBorder(null);

        JPanel leftRightBottomPanel = new JPanel(new BorderLayout());
        leftRightBottomPanel.add(leftRight, BorderLayout.CENTER);

        JPanel arrowUpPanel = new JPanel();
        arrowUpPanel.setLayout(new BorderLayout());
        arrowUpPanel.setOpaque(false);
        arrowUpPanel.setPreferredSize(new Dimension(17, 22));
        arrowUpPanel.setMaximumSize(new Dimension(17, 22));
        JButton hideBottomButton = new BasicArrowButton(BasicArrowButton.NORTH);
        hideBottomButton.setBorderPainted(false);
        hideBottomButton.setOpaque(false);

        hideShowAction = new HideShowConsoleAction(arrowUpPanel);

        hideBottomButton.addMouseListener(LeftRightBottomPanel.hideShowAction);
        arrowUpPanel.add(Box.createHorizontalGlue());
        arrowUpPanel.add(hideBottomButton, BorderLayout.EAST);
        arrowUpPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER));
        arrowUpPanel.setVisible(false);

        leftRightBottomPanel.add(arrowUpPanel, BorderLayout.SOUTH);

        // Setting for top and bottom components
        this.setTopComponent(leftRightBottomPanel);

        GUIMediator.register(new BottomPanel());

        this.setBottomComponent(GUIMediator.bottomPanel());
        this.setDividerLocation(601 - horizontalSplitter);

        // defines left and bottom pane
        this.setResizeWeight(1);
    }

    /**
     * MouseAdapter used on arrow on tabbedpane header and on
     * ersatz button when bottom panel is hidden.
     */
    public static HideShowConsoleAction hideShowAction;

    /**
     * MouseAdapter to show/hide bottom panel.
     */
    class HideShowConsoleAction extends MouseAdapter {
        /**
         * Save the divider location when bottom panel is not visible.
         */
        private int loc = 0;
        
        /**
         * Ersatz panel to display in place of tabbedpane.
         */
        private JPanel panel;
        
        /**
         * Create the hide/show bottom panel action.
         */
        public HideShowConsoleAction(JPanel panel) {
            super();
            this.panel = panel;
        }
        
        /**
         * Hide bottom panel if both main and bottom are visible, also
         * displays an ersatz bar replacing tabbedpane.  
         * Or else if only main panel is visible then displays bottom panel
         * and hide ersatz panel.
         */
        @Override
        public void mouseClicked(MouseEvent arg0) {
            if (LeftRightBottomPanel.this.getTopComponent().isVisible() && LeftRightBottomPanel.this.getBottomComponent().isVisible()) {
                LeftRightBottomPanel.this.getBottomComponent().setVisible(false);
                this.loc = LeftRightBottomPanel.this.getDividerLocation();
                this.panel.setVisible(true);
                LeftRightBottomPanel.this.disableDragSize();
            } else {
                LeftRightBottomPanel.this.getBottomComponent().setVisible(true);
                LeftRightBottomPanel.this.setDividerLocation(this.loc);
                this.panel.setVisible(false);
                LeftRightBottomPanel.this.enableDragSize();
            }
        }
    }
}

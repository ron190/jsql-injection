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

import com.jsql.view.swing.util.JSplitPaneWithZeroSizeDivider;
import com.jsql.model.InjectionModel;
import com.jsql.util.I18nUtil;
import com.jsql.util.PreferencesUtil;
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
public class SplitNS extends JSplitPaneWithZeroSizeDivider {

    /**
     * SplitPane containing Manager panels on the left and result tabs on the right.
     */
    private final JSplitPane splitEW = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT);

    private static final JPanel PANEL_HIDDEN_CONSOLES = new JPanel();
    
    /**
     * MouseAdapter used on arrow on tabbedpane header and on
     * ersatz button when bottom panel is hidden.
     */
    private static final ActionHideShowConsole ACTION_HIDE_SHOW_CONSOLE = new ActionHideShowConsole(SplitNS.PANEL_HIDDEN_CONSOLES);
    private static final ActionHideShowResult ACTION_HIDE_SHOW_RESULT= new ActionHideShowResult();

    /**
     * Create main panel with Manager panels on the left, result tabs on the right,
     * and consoles in the bottom.
     */
    public SplitNS() {
        super(JSplitPane.VERTICAL_SPLIT);
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        var tabManagersProxy = new TabManagersCards();
        new TabResults();  // initialized but hidden

        // Tree and tabs on top
        this.splitEW.setLeftComponent(tabManagersProxy);
        JLabel labelApp = new JLabel(UiUtil.APP_BIG.getIcon());
        labelApp.setMinimumSize(new Dimension(100, 0));
        this.splitEW.setRightComponent(labelApp);
        var verticalLeftRightSplitter = preferences.getDouble(PreferencesUtil.EW_SPLIT, 0.33);
        this.splitEW.setDividerLocation(Math.clamp(verticalLeftRightSplitter, 0.0, 1.0));

        JLabel labelShowConsoles = new JLabel(UiUtil.ARROW_UP.getIcon());
        labelShowConsoles.setBorder(BorderFactory.createEmptyBorder());
        labelShowConsoles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SplitNS.ACTION_HIDE_SHOW_CONSOLE.actionPerformed(null);
            }
        });
        labelShowConsoles.setName("buttonShowConsolesHidden");
        SplitNS.PANEL_HIDDEN_CONSOLES.setLayout(new BorderLayout());
        SplitNS.PANEL_HIDDEN_CONSOLES.add(labelShowConsoles, BorderLayout.LINE_END);
        SplitNS.PANEL_HIDDEN_CONSOLES.setVisible(false);
        SplitNS.PANEL_HIDDEN_CONSOLES.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {  // middle click on header with no tab
                    SplitNS.ACTION_HIDE_SHOW_CONSOLE.actionPerformed(null);
                }
            }
        });

        var panelManagerResult = new JPanel(new BorderLayout());
        panelManagerResult.add(this.splitEW, BorderLayout.CENTER);
        panelManagerResult.add(SplitNS.PANEL_HIDDEN_CONSOLES, BorderLayout.SOUTH);
        this.setTopComponent(panelManagerResult);

        var panelConsoles = new PanelConsoles();
        MediatorHelper.register(panelConsoles);
        this.setBottomComponent(panelConsoles);

        this.setResizeWeight(1);
    }

    /**
     * Switch left component with right component when locale orientation requires it.
     */
    public void initSplitOrientation() {
        if (MediatorHelper.tabResults().getTabCount() == 0) {
            int dividerLocation = this.splitEW.getDividerLocation();
            if (ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))) {
                this.splitEW.setLeftComponent(MediatorHelper.tabResults());
            } else {
                this.splitEW.setRightComponent(MediatorHelper.tabResults());
            }
            this.splitEW.setDividerLocation(dividerLocation);
        }
    }


    // Getter and setter

    public JSplitPane getSplitEW() {
        return this.splitEW;
    }

    public static ActionHideShowConsole getActionHideShowConsole() {
        return SplitNS.ACTION_HIDE_SHOW_CONSOLE;
    }
    
    public static ActionHideShowResult getActionHideShowResult() {
        return SplitNS.ACTION_HIDE_SHOW_RESULT;
    }
}

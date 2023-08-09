/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
public class TabManagers extends TabbedPaneWheeled {
    
    /**
     * Create manager panel.
     */
    public TabManagers() {
        
        this.setName("tabManagers");
        
        this.setUI(new CustomMetalTabbedPaneUI() {
            
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                
                return Math.max(75, super.calculateTabWidth(tabPlacement, tabIndex, metrics));
            }
        });

        this.addChangeListener(e -> MediatorHelper.tabManagers().setSelectedIndex(TabManagers.this.getSelectedIndex()));
        
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, 25));
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, 25));
        
        this.addMouseClickMenu();
        
        this.setMinimumSize(new Dimension(100, 0));
        
        this.buildI18nTab("DATABASE_TAB", "DATABASE_TOOLTIP", UiUtil.ICON_DATABASE_SERVER, 0);
        this.buildI18nTab("ADMINPAGE_TAB", "ADMINPAGE_TOOLTIP", UiUtil.ICON_ADMIN_SERVER, 1);
        this.buildI18nTab("FILE_TAB", "FILE_TOOLTIP", UiUtil.ICON_FILE_SERVER, 2);
        this.buildI18nTab("WEBSHELL_TAB", "WEBSHELL_TOOLTIP", UiUtil.ICON_SHELL_SERVER, 3);
        this.buildI18nTab("SQLSHELL_TAB", "SQLSHELL_TOOLTIP", UiUtil.ICON_SHELL_SERVER, 4);
        this.buildI18nTab("UPLOAD_TAB", "UPLOAD_TOOLTIP", UiUtil.ICON_UPLOAD, 5);
        this.buildI18nTab("BRUTEFORCE_TAB", "BRUTEFORCE_TOOLTIP", UiUtil.ICON_BRUTER, 6);
        this.buildI18nTab("CODER_TAB", "CODER_TOOLTIP", UiUtil.ICON_CODER, 7);
        this.buildI18nTab("SCANLIST_TAB", "SCANLIST_TOOLTIP", UiUtil.ICON_SCANLIST, 8);
    }
    
    private void buildI18nTab(String keyLabel, String keyTooltip, Icon icon, int index) {
        
        final var refTooltip = new JToolTipI18n[]{ new JToolTipI18n(I18nUtil.valueByKey(keyTooltip)) };
        
        JLabel labelTab = new JLabel(I18nUtil.valueByKey(keyLabel), icon, SwingConstants.CENTER) {
            
            @Override
            public JToolTip createToolTip() {
                
                JToolTipI18n tipI18n = new JToolTipI18n(I18nUtil.valueByKey(keyTooltip));
                refTooltip[0] = tipI18n;
                
                return tipI18n;
            }
        };
        
        labelTab.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                MediatorHelper.tabManagers().setSelectedIndex(index);
                TabManagers.this.setSelectedIndex(index);
                super.mousePressed(e);
            }
        });
        
        this.addTab(I18nUtil.valueByKey(keyLabel), icon, labelTab);

        I18nViewUtil.addComponentForKey(keyLabel, labelTab);
        I18nViewUtil.addComponentForKey(keyTooltip, refTooltip[0]);
        
        labelTab.setToolTipText(I18nUtil.valueByKey(keyTooltip));
        labelTab.addMouseListener(new TabMouseAdapter(this));
    }
}

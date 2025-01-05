/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
public class TabManagers extends TabbedPaneWheeled {
    
    /**
     * Create manager panel.
     */
    public TabManagers() {
        
        this.setName("tabManagers");
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, 35));
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, 35));
        this.addMouseListener(new TabMouseAdapter(this));
        this.addMouseClickMenu();

        this.buildI18nTab("DATABASE_TAB", "DATABASE_TOOLTIP", UiUtil.DATABASE_BOLD.icon, 0);
        this.buildI18nTab("ADMINPAGE_TAB", "ADMINPAGE_TOOLTIP", UiUtil.ADMIN.icon, 1);
        this.buildI18nTab("FILE_TAB", "FILE_TOOLTIP", UiUtil.DOWNLOAD.icon, 2);
        this.buildI18nTab("WEBSHELL_TAB", "WEBSHELL_TOOLTIP", UiUtil.TERMINAL.icon, 3);
        this.buildI18nTab("SQLSHELL_TAB", "SQLSHELL_TOOLTIP", UiUtil.TERMINAL.icon, 4);
        this.buildI18nTab("UPLOAD_TAB", "UPLOAD_TOOLTIP", UiUtil.UPLOAD.icon, 5);
        this.buildI18nTab("BRUTEFORCE_TAB", "BRUTEFORCE_TOOLTIP", UiUtil.LOCK.icon, 6);
        this.buildI18nTab("CODER_TAB", "CODER_TOOLTIP", UiUtil.TEXTFIELD.icon, 7);
        this.buildI18nTab("SCANLIST_TAB", "SCANLIST_TOOLTIP", UiUtil.BATCH.icon, 8);

        this.addChangeListener(e -> {
            CardLayout cardLayout = (CardLayout) MediatorHelper.tabManagersCards().getLayout();
            cardLayout.show(MediatorHelper.tabManagersCards(), TabManagers.this.getTabComponentAt(TabManagers.this.getSelectedIndex()).getName());
        });
    }
    
    private void buildI18nTab(String keyLabel, String keyTooltip, Icon icon, int index) {
        AtomicReference<JToolTipI18n> tooltipAtomic = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(keyTooltip)));
        JLabel labelTab = new JLabel(I18nUtil.valueByKey(keyLabel), icon, SwingConstants.CENTER) {
            @Override
            public JToolTip createToolTip() {
                tooltipAtomic.set(new JToolTipI18n(I18nUtil.valueByKey(keyTooltip)));
                return tooltipAtomic.get();
            }
        };
        
        labelTab.setName(keyLabel);
        labelTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                CardLayout cardLayout = (CardLayout) MediatorHelper.tabManagersCards().getLayout();
                cardLayout.show(MediatorHelper.tabManagersCards(), TabManagers.this.getTabComponentAt(index).getName());
                TabManagers.this.setSelectedIndex(index);
            }
        });

        this.addTab(I18nUtil.valueByKey(keyLabel), icon, null);  // Required for i18n to work
        this.setTabComponentAt(  // TODO Break focus, should not be used
            this.indexOfTab(I18nUtil.valueByKey(keyLabel)),
            labelTab
        );

        I18nViewUtil.addComponentForKey(keyLabel, labelTab);
        I18nViewUtil.addComponentForKey(keyTooltip, tooltipAtomic.get());
        
        labelTab.setToolTipText(I18nUtil.valueByKey(keyTooltip));
        labelTab.addMouseListener(new TabMouseAdapter(this));  // required as label incorrectly focused
    }
}

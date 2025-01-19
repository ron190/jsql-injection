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
import com.jsql.view.swing.util.ModelSvgIcon;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
public class TabManagers extends TabbedPaneWheeled {

    private final List<ModelSvgIcon> iconsTabs = Arrays.asList(
        UiUtil.DATABASE_BOLD, UiUtil.ADMIN, UiUtil.DOWNLOAD, UiUtil.TERMINAL, UiUtil.LOCK, UiUtil.TEXTFIELD, UiUtil.BATCH
    );

    /**
     * Create manager panel.
     */
    public TabManagers() {
        this.setName("tabManagers");
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, 35));
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, 35));
        this.addMouseListener(new TabMouseAdapter(this));
        this.addMouseClickMenu();

        AtomicInteger indexTab = new AtomicInteger();
        this.iconsTabs.forEach(modelSvgIcon -> this.buildI18nTab(modelSvgIcon, indexTab.getAndIncrement()));
        this.addChangeListener(e -> {
            CardLayout cardLayout = (CardLayout) MediatorHelper.tabManagersCards().getLayout();
            cardLayout.show(MediatorHelper.tabManagersCards(), this.getTabComponentAt(this.getSelectedIndex()).getName());
        });
    }
    
    private void buildI18nTab(ModelSvgIcon modelSvgIcon, int index) {
        Icon icon = modelSvgIcon.icon;
        String keyLabel = modelSvgIcon.keyLabel;
        String keyTooltip = modelSvgIcon.keyTooltip;
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

    public List<ModelSvgIcon> getIconsTabs() {
        return this.iconsTabs;
    }
}

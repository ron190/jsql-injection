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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;

import com.jsql.i18n.I18n;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerBruteForce;
import com.jsql.view.swing.manager.ManagerCoder;
import com.jsql.view.swing.manager.ManagerDatabase;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.manager.ManagerSqlShell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebShell;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class TabManagers extends MouseTabbedPane {
    
    /**
     * Create manager panel.
     */
    public TabManagers() {
        
        this.setUI(new CustomMetalTabbedPaneUI() {
            
            @Override protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return Math.max(75, super.calculateTabWidth(tabPlacement, tabIndex, metrics));
            }
        });
        
        ManagerWebShell managerWebShell = new ManagerWebShell();
        ManagerScan managerScanList = new ManagerScan();
        ManagerDatabase managerDatabase = new ManagerDatabase();
        ManagerAdminPage managerAdminPage = new ManagerAdminPage();
        ManagerFile managerFile = new ManagerFile();
        ManagerUpload managerUpload = new ManagerUpload();
        ManagerSqlShell managerSqlShell = new ManagerSqlShell();
        ManagerBruteForce managerBruteForce = new ManagerBruteForce();
        
        MediatorGui.register(managerWebShell);
        MediatorGui.register(managerScanList);
        MediatorGui.register(managerDatabase);
        MediatorGui.register(managerAdminPage);
        MediatorGui.register(managerFile);
        MediatorGui.register(managerUpload);
        MediatorGui.register(managerSqlShell);
        MediatorGui.register(managerBruteForce);
        
        this.setMinimumSize(new Dimension(100, 0));
        this.addMouseClickMenu();
        
        this.buildI18nTab("DATABASE_TAB", "DATABASE_TOOLTIP", HelperUi.ICON_DATABASE_SERVER, managerDatabase);
        this.buildI18nTab("ADMINPAGE_TAB", "ADMINPAGE_TOOLTIP", HelperUi.ICON_ADMIN_SERVER, managerAdminPage);
        this.buildI18nTab("FILE_TAB", "FILE_TOOLTIP", HelperUi.ICON_FILE_SERVER, managerFile);
        this.buildI18nTab("WEBSHELL_TAB", "WEBSHELL_TOOLTIP", HelperUi.ICON_SHELL_SERVER, managerWebShell);
        this.buildI18nTab("SQLSHELL_TAB", "SQLSHELL_TOOLTIP", HelperUi.ICON_SHELL_SERVER, managerSqlShell);
        this.buildI18nTab("UPLOAD_TAB", "UPLOAD_TOOLTIP", HelperUi.ICON_UPLOAD, managerUpload);
        this.buildI18nTab("BRUTEFORCE_TAB", "BRUTEFORCE_TOOLTIP", HelperUi.ICON_BRUTER, managerBruteForce);
        this.buildI18nTab("CODER_TAB", "CODER_TOOLTIP", HelperUi.ICON_CODER, new ManagerCoder());
        this.buildI18nTab("SCANLIST_TAB", "SCANLIST_TOOLTIP", HelperUi.ICON_SCANLIST, managerScanList);
        
        managerFile.setButtonEnable(false);
        managerWebShell.setButtonEnable(false);
        managerSqlShell.setButtonEnable(false);
    }
    
    private void buildI18nTab(
        String keyLabel,
        String keyTooltip,
        Icon icon,
        Component manager
    ) {
        
        final JToolTipI18n[] refTooltip = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey(keyTooltip))};
        
        JLabel labelTab = new JLabel(I18n.valueByKey(keyLabel), icon, SwingConstants.CENTER){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey(keyTooltip));
                refTooltip[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        };
        
        labelTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TabManagers.this.setSelectedComponent(manager);
                super.mousePressed(e);
            }
        });
        
        this.addTab(I18n.valueByKey(keyLabel), icon, manager);
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey(keyLabel)),
            labelTab
        );
        
        I18nView.addComponentForKey(keyLabel, labelTab);
        I18nView.addComponentForKey(keyTooltip, refTooltip[0]);
        labelTab.setToolTipText(I18n.valueByKey(keyTooltip));
        labelTab.addMouseListener(new TabSelectionMouseHandler());
    }
    
}

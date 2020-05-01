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

import org.apache.commons.lang3.StringUtils;

import com.jsql.util.I18nUtil;
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
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class TabManagers extends TabbedPaneWheeled {
    
    private ManagerWebShell managerWebShell = new ManagerWebShell();
    private ManagerFile managerFile = new ManagerFile();
    private ManagerUpload managerUpload = new ManagerUpload();
    private ManagerSqlShell managerSqlShell = new ManagerSqlShell();
    
    /**
     * Create manager panel.
     */
    public TabManagers() {
        
        this.setUI(new CustomMetalTabbedPaneUI() {
            
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                
                return Math.max(75, super.calculateTabWidth(tabPlacement, tabIndex, metrics));
            }
        });
        
        ManagerScan managerScanList = new ManagerScan();
        ManagerDatabase managerDatabase = new ManagerDatabase();
        ManagerAdminPage managerAdminPage = new ManagerAdminPage();
        ManagerBruteForce managerBruteForce = new ManagerBruteForce();
        
        MediatorHelper.register(this.managerWebShell);
        MediatorHelper.register(this.managerFile);
        MediatorHelper.register(this.managerUpload);
        MediatorHelper.register(this.managerSqlShell);
        MediatorHelper.register(managerScanList);
        MediatorHelper.register(managerAdminPage);
        MediatorHelper.register(managerBruteForce);
        
        this.setMinimumSize(new Dimension(100, 0));
        this.addMouseClickMenu();
        
        this.buildI18nTab("DATABASE_TAB", "DATABASE_TOOLTIP", UiUtil.ICON_DATABASE_SERVER, managerDatabase);
        this.buildI18nTab("ADMINPAGE_TAB", "ADMINPAGE_TOOLTIP", UiUtil.ICON_ADMIN_SERVER, managerAdminPage);
        this.buildI18nTab("FILE_TAB", "FILE_TOOLTIP", UiUtil.ICON_FILE_SERVER, this.managerFile);
        this.buildI18nTab("WEBSHELL_TAB", "WEBSHELL_TOOLTIP", UiUtil.ICON_SHELL_SERVER, this.managerWebShell);
        this.buildI18nTab("SQLSHELL_TAB", "SQLSHELL_TOOLTIP", UiUtil.ICON_SHELL_SERVER, this.managerSqlShell);
        this.buildI18nTab("UPLOAD_TAB", "UPLOAD_TOOLTIP", UiUtil.ICON_UPLOAD, this.managerUpload);
        this.buildI18nTab("BRUTEFORCE_TAB", "BRUTEFORCE_TOOLTIP", UiUtil.ICON_BRUTER, managerBruteForce);
        this.buildI18nTab("CODER_TAB", "CODER_TOOLTIP", UiUtil.ICON_CODER, new ManagerCoder());
        this.buildI18nTab("SCANLIST_TAB", "SCANLIST_TOOLTIP", UiUtil.ICON_SCANLIST, managerScanList);
    }
    
    public void createFileTab(String path, String name) {
        
        // Add the path String to the list of files only if there is no same StringObject value already
        this.managerWebShell.addToList(path.replace(name, StringUtils.EMPTY));
        this.managerUpload.addToList(path.replace(name, StringUtils.EMPTY));
        this.managerSqlShell.addToList(path.replace(name, StringUtils.EMPTY));
    }
    
    public void markFileSystemInvulnerable() {
        
        this.managerFile.changePrivilegeIcon(UiUtil.ICON_SQUARE_RED);
        this.managerFile.endProcess();
        
        this.managerWebShell.changePrivilegeIcon(UiUtil.ICON_SQUARE_RED);
        this.managerWebShell.endProcess();
        
        this.managerUpload.changePrivilegeIcon(UiUtil.ICON_SQUARE_RED);
        this.managerUpload.endProcess();
        
        this.managerSqlShell.changePrivilegeIcon(UiUtil.ICON_SQUARE_RED);
        this.managerSqlShell.endProcess();
    }
    
    public void endPreparation() {
        
        this.managerFile.setButtonEnable(true);
        this.managerWebShell.setButtonEnable(true);
        this.managerSqlShell.setButtonEnable(true);
        this.managerUpload.setButtonEnable(true);
    }
    
    public void markFileSystemVulnerable() {
        
        this.managerFile.changePrivilegeIcon(UiUtil.ICON_TICK);
        this.managerWebShell.changePrivilegeIcon(UiUtil.ICON_TICK);
        this.managerSqlShell.changePrivilegeIcon(UiUtil.ICON_TICK);
        this.managerUpload.changePrivilegeIcon(UiUtil.ICON_TICK);
    }
    
    private void buildI18nTab(String keyLabel, String keyTooltip, Icon icon, Component manager) {
        
        final JToolTipI18n[] refTooltip = new JToolTipI18n[]{new JToolTipI18n(I18nUtil.valueByKey(keyTooltip))};
        
        JLabel labelTab = new JLabel(I18nUtil.valueByKey(keyLabel), icon, SwingConstants.CENTER) {
            
            @Override
            public JToolTip createToolTip() {
                
                JToolTip tipI18n = new JToolTipI18n(I18nUtil.valueByKey(keyTooltip));
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
        
        this.addTab(I18nUtil.valueByKey(keyLabel), icon, manager);
        this.setTabComponentAt(
            this.indexOfTab(I18nUtil.valueByKey(keyLabel)),
            labelTab
        );
        
        I18nViewUtil.addComponentForKey(keyLabel, labelTab);
        I18nViewUtil.addComponentForKey(keyTooltip, refTooltip[0]);
        
        labelTab.setToolTipText(I18nUtil.valueByKey(keyTooltip));
        labelTab.addMouseListener(new TabMouseAdapter());
    }
}

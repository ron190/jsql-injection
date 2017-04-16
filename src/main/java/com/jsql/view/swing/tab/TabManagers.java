/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;

import com.jsql.i18n.I18n;
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

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class TabManagers extends MouseTabbedPane {
	
    /**
     * Create manager panel.
     */
    public TabManagers() {
        
        ManagerWebShell managerWebShell = new ManagerWebShell();
        ManagerScan managerScanList = new ManagerScan();
        ManagerDatabase managerDatabase = new ManagerDatabase();
        ManagerAdminPage managerAdminPage = new ManagerAdminPage("admin-page.txt");
        ManagerFile managerFile = new ManagerFile("file.txt");
        ManagerUpload managerUpload = new ManagerUpload();
        ManagerSqlShell managerSqlShell = new ManagerSqlShell();
        
        MediatorGui.register(managerWebShell);
        MediatorGui.register(managerScanList);
        MediatorGui.register(managerDatabase);
        MediatorGui.register(managerAdminPage);
        MediatorGui.register(managerFile);
        MediatorGui.register(managerUpload);
        MediatorGui.register(managerSqlShell);
        
        this.setMinimumSize(new Dimension(100, 0));
        this.addMouseClickMenu();

        JLabel labelDatabase = new JLabel(I18n.valueByKey("DATABASE_TAB"), HelperUi.ICON_DATABASE_SERVER, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("DATABASE_TAB"), HelperUi.ICON_DATABASE_SERVER, managerDatabase, I18n.valueByKey("DATABASE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("DATABASE_TAB")),
            labelDatabase
        );
        I18n.addComponentForKey("DATABASE_TAB", labelDatabase);
        
        JLabel labelAdminPage = new JLabel(I18n.valueByKey("ADMINPAGE_TAB"), HelperUi.ICON_ADMIN_SERVER, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("ADMINPAGE_TAB"), HelperUi.ICON_ADMIN_SERVER, managerAdminPage, I18n.valueByKey("ADMINPAGE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("ADMINPAGE_TAB")),
            labelAdminPage
        );
        I18n.addComponentForKey("ADMINPAGE_TAB", labelAdminPage);
        
        JLabel labelFile = new JLabel(I18n.valueByKey("FILE_TAB"), HelperUi.ICON_FILE_SERVER, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("FILE_TAB"), HelperUi.ICON_FILE_SERVER, managerFile, I18n.valueByKey("FILE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("FILE_TAB")),
            labelFile
        );
        I18n.addComponentForKey("FILE_TAB", labelFile);
        
        final JToolTipI18n[] j = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("WEBSHELL_TOOLTIP"))};
        JLabel labelWebShell = new JLabel(I18n.valueByKey("WEBSHELL_TAB"), HelperUi.ICON_SHELL_SERVER, SwingConstants.CENTER){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("WEBSHELL_TOOLTIP"));
                j[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        };
        labelWebShell.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TabManagers.this.setSelectedComponent(managerWebShell);
                super.mousePressed(e);
            }
        });
        this.addTab(I18n.valueByKey("WEBSHELL_TAB"), HelperUi.ICON_SHELL_SERVER, managerWebShell, I18n.valueByKey("WEBSHELL_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("WEBSHELL_TAB")),
            labelWebShell
        );
        I18n.addComponentForKey("WEBSHELL_TAB", labelWebShell);
        I18n.addComponentForKey("WEBSHELL_TOOLTIP", j[0]);
        labelWebShell.setToolTipText(I18n.valueByKey("WEBSHELL_TOOLTIP"));
        
        final JToolTipI18n[] j2 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("SQLSHELL_TOOLTIP"))};
        JLabel labelSqlShell = new JLabel(I18n.valueByKey("SQLSHELL_TAB"), HelperUi.ICON_SHELL_SERVER, SwingConstants.CENTER){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("SQLSHELL_TOOLTIP"));
                j2[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        };
        labelSqlShell.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TabManagers.this.setSelectedComponent(managerSqlShell);
                super.mousePressed(e);
            }
        });
        this.addTab(I18n.valueByKey("SQLSHELL_TAB"), HelperUi.ICON_SHELL_SERVER, managerSqlShell, I18n.valueByKey("SQLSHELL_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("SQLSHELL_TAB")),
            labelSqlShell
        );
        I18n.addComponentForKey("SQLSHELL_TAB", labelSqlShell);
        I18n.addComponentForKey("SQLSHELL_TOOLTIP", j2[0]);
        labelSqlShell.setToolTipText(I18n.valueByKey("SQLSHELL_TOOLTIP"));
        
        JLabel labelUpload = new JLabel(I18n.valueByKey("UPLOAD_TAB"), HelperUi.ICON_UPLOAD, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("UPLOAD_TAB"), HelperUi.ICON_UPLOAD, managerUpload, I18n.valueByKey("UPLOAD_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("UPLOAD_TAB")),
            labelUpload
        );
        I18n.addComponentForKey("UPLOAD_TAB", labelUpload);
        
        JLabel labelBruteforce = new JLabel(I18n.valueByKey("BRUTEFORCE_TAB"), HelperUi.ICON_BRUTER, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("BRUTEFORCE_TAB"), HelperUi.ICON_BRUTER, new ManagerBruteForce(), I18n.valueByKey("BRUTEFORCE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("BRUTEFORCE_TAB")),
            labelBruteforce
        );
        I18n.addComponentForKey("BRUTEFORCE_TAB", labelBruteforce);
        
        JLabel labelCoder = new JLabel(I18n.valueByKey("CODER_TAB"), HelperUi.ICON_CODER, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("CODER_TAB"), HelperUi.ICON_CODER, new ManagerCoder(), I18n.valueByKey("CODER_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("CODER_TAB")),
            labelCoder
        );
        I18n.addComponentForKey("CODER_TAB", labelCoder);
        
        JLabel labelScan = new JLabel(I18n.valueByKey("SCANLIST_TAB"), HelperUi.ICON_SCANLIST, SwingConstants.CENTER);
        this.addTab(I18n.valueByKey("SCANLIST_TAB"), HelperUi.ICON_SCANLIST, managerScanList, I18n.valueByKey("SCANLIST_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab(I18n.valueByKey("SCANLIST_TAB")),
            labelScan
        );
        I18n.addComponentForKey("SCANLIST_TAB", labelScan);

        managerFile.setButtonEnable(false);
        managerWebShell.setButtonEnable(false);
        managerSqlShell.setButtonEnable(false);
    }
    
}

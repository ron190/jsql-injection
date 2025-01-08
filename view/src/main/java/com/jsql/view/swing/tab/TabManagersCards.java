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

import com.jsql.view.swing.manager.*;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
public class TabManagersCards extends JPanel {
    
    private final ManagerWebShell managerWebShell = new ManagerWebShell();
    private final ManagerFile managerFile = new ManagerFile();
    private final ManagerUpload managerUpload = new ManagerUpload();
    private final ManagerSqlShell managerSqlShell = new ManagerSqlShell();
    
    /**
     * Create manager panel.
     */
    public TabManagersCards() {
        this.setName("tabManagersProxy");
        this.setLayout(new CardLayout());
        this.setMinimumSize(new Dimension(100, 0));  // allow proper minimize

        var managerScanList = new ManagerScan();
        var managerDatabase = new ManagerDatabase();
        var managerAdminPage = new ManagerAdminPage();
        var managerBruteForce = new ManagerBruteForce();

        MediatorHelper.register(this.managerWebShell);
        MediatorHelper.register(this.managerFile);
        MediatorHelper.register(this.managerUpload);
        MediatorHelper.register(this.managerSqlShell);
        MediatorHelper.register(managerScanList);
        MediatorHelper.register(managerAdminPage);
        MediatorHelper.register(managerBruteForce);

        this.buildI18nTab("DATABASE_TAB", managerDatabase);
        this.buildI18nTab("ADMINPAGE_TAB", managerAdminPage);
        this.buildI18nTab("FILE_TAB", this.managerFile);
        this.buildI18nTab("WEBSHELL_TAB", this.managerWebShell);
        this.buildI18nTab("SQLSHELL_TAB", this.managerSqlShell);
        this.buildI18nTab("UPLOAD_TAB", this.managerUpload);
        this.buildI18nTab("BRUTEFORCE_TAB", managerBruteForce);
        this.buildI18nTab("CODER_TAB", new ManagerCoder());
        this.buildI18nTab("SCANLIST_TAB", managerScanList);

        MediatorHelper.register(this);
    }

    public void addToLists(String path, String name) {
        Arrays.asList(this.managerWebShell, this.managerSqlShell, this.managerUpload)
            .forEach(manager -> manager.addToList(path.replace(name, StringUtils.EMPTY)));
    }
    
    public void markFileSystemInvulnerable() {
        this.getManagers().forEach(manager -> {
            manager.changePrivilegeIcon(UiUtil.CROSS_RED.icon);
            manager.endProcess();
        });
    }
    
    public void endPreparation() {
        this.getManagers().forEach(manager -> manager.setButtonEnable(true));
    }
    
    public void markFileSystemVulnerable() {
        this.getManagers().forEach(manager -> manager.changePrivilegeIcon(UiUtil.TICK_GREEN.icon));
    }

    private void buildI18nTab(String keyLabel, Component manager) {
        manager.setName(keyLabel);
        this.add(manager, keyLabel);
    }

    private List<AbstractManagerList> getManagers() {
        return Arrays.asList(this.managerFile, this.managerWebShell, this.managerSqlShell, this.managerUpload);
    }
}

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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
public class TabManagersCards extends JPanel {
    
    private final ManagerFile managerFile = new ManagerFile();
    private final ManagerExploit managerExploit = new ManagerExploit();

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

        MediatorHelper.register(this.managerFile);
        MediatorHelper.register(this.managerExploit);
        MediatorHelper.register(managerScanList);
        MediatorHelper.register(managerAdminPage);
        MediatorHelper.register(managerBruteForce);

        var managers = Arrays.asList(
            managerDatabase, managerAdminPage, this.managerFile, this.managerExploit, managerBruteForce,
            new ManagerCoder(), managerScanList
        );
        AtomicInteger i = new AtomicInteger();
        MediatorHelper.frame().getTabManagers().getIconsTabs().forEach(modelSvgIcon -> this.buildI18nTab(
            modelSvgIcon.keyLabel,
            managers.get(i.getAndIncrement())
        ));

        MediatorHelper.register(this);
    }

    public void addToLists(String path, String name) {
        this.managerExploit.addToList(path.replace(name, StringUtils.EMPTY));
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
        return Arrays.asList(this.managerFile, this.managerExploit);
    }
}

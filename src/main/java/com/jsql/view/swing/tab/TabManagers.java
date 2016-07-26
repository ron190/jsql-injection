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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerBruteForce;
import com.jsql.view.swing.manager.ManagerCoder;
import com.jsql.view.swing.manager.ManagerDatabase;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.manager.ManagerSqlshell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebshell;

/**
 * Panel on the left with functionalities like webshell, file reading and admin page finder.
 */
@SuppressWarnings("serial")
public class TabManagers extends MouseTabbedPane {
    /**
     * Panel for executing system commands.
     */
    public final ManagerWebshell shellManager = new ManagerWebshell();

    /**
     * Panel for testing multiple URLs.
     */
    public final ManagerScan scanListManager = new ManagerScan();

    /**
     * Panel for testing backoffice admin pages.
     */
    public final ManagerDatabase databaseManager = new ManagerDatabase();
    
    /**
     * Panel for testing backoffice admin pages.
     */
    public final ManagerAdminPage adminPageManager = new ManagerAdminPage();
    
    /**
     * Panel for reading files source.
     */
    public final ManagerFile fileManager = new ManagerFile();

    /**
     * Panel for uploading files.
     */
    public final ManagerUpload uploadManager = new ManagerUpload();

    /**
     * Panel for sending SQL requests.
     */
    public final ManagerSqlshell sqlShellManager = new ManagerSqlshell();

    /**
     * Create manager panel.
     */
    public TabManagers() {
        this.setMinimumSize(new Dimension(100, 0));
        this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
        this.activateMenu();

        JLabel labelDatabase = new JLabel(I18n.valueByKey("DATABASE_TAB"), HelperUi.DATABASE_SERVER_ICON, SwingConstants.CENTER);
        this.addTab("Database", HelperUi.DATABASE_SERVER_ICON, databaseManager, I18n.valueByKey("DATABASE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Database"),
            labelDatabase
        );
        I18n.addComponentForKey("DATABASE_TAB", labelDatabase);
        
        JLabel labelAdminPage = new JLabel(I18n.valueByKey("ADMINPAGE_TAB"), HelperUi.ADMIN_SERVER_ICON, SwingConstants.CENTER);
        this.addTab("Admin page", HelperUi.ADMIN_SERVER_ICON, adminPageManager, I18n.valueByKey("ADMINPAGE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Admin page"),
            labelAdminPage
        );
        I18n.addComponentForKey("ADMINPAGE_TAB", labelAdminPage);
        
        JLabel labelFile = new JLabel(I18n.valueByKey("FILE_TAB"), HelperUi.FILE_SERVER_ICON, SwingConstants.CENTER);
        this.addTab("File", HelperUi.FILE_SERVER_ICON, fileManager, I18n.valueByKey("FILE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("File"),
            labelFile
        );
        I18n.addComponentForKey("FILE_TAB", labelFile);
        
        JLabel labelWebShell = new JLabel(I18n.valueByKey("WEBSHELL_TAB"), HelperUi.SHELL_SERVER_ICON, SwingConstants.CENTER);
        this.addTab("Web shell", HelperUi.SHELL_SERVER_ICON, shellManager, I18n.valueByKey("WEBSHELL_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Web shell"),
            labelWebShell
        );
        I18n.addComponentForKey("WEBSHELL_TAB", labelWebShell);
        
        JLabel labelSqlShell = new JLabel(I18n.valueByKey("SQLSHELL_TAB"), HelperUi.SHELL_SERVER_ICON, SwingConstants.CENTER);
        this.addTab("SQL shell", HelperUi.SHELL_SERVER_ICON, sqlShellManager, I18n.valueByKey("SQLSHELL_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("SQL shell"),
            labelSqlShell
        );
        I18n.addComponentForKey("SQLSHELL_TAB", labelSqlShell);
        
        JLabel labelUpload = new JLabel(I18n.valueByKey("UPLOAD_TAB"), HelperUi.UPLOAD_ICON, SwingConstants.CENTER);
        this.addTab("Upload", HelperUi.UPLOAD_ICON, uploadManager, I18n.valueByKey("UPLOAD_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Upload"),
            labelUpload
        );
        I18n.addComponentForKey("UPLOAD_TAB", labelUpload);
        
        JLabel labelBruteforce = new JLabel(I18n.valueByKey("BRUTEFORCE_TAB"), HelperUi.BRUTER_ICON, SwingConstants.CENTER);
        this.addTab("Brute force", HelperUi.BRUTER_ICON, new ManagerBruteForce(), I18n.valueByKey("BRUTEFORCE_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Brute force"),
            labelBruteforce
        );
        I18n.addComponentForKey("BRUTEFORCE_TAB", labelBruteforce);
        
        JLabel labelCoder = new JLabel(I18n.valueByKey("CODER_TAB"), HelperUi.CODER_ICON, SwingConstants.CENTER);
        this.addTab("Coder", HelperUi.CODER_ICON, new ManagerCoder(), I18n.valueByKey("CODER_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Coder"),
            labelCoder
        );
        I18n.addComponentForKey("CODER_TAB", labelCoder);
        
        JLabel labelScan = new JLabel(I18n.valueByKey("SCANLIST_TAB"), HelperUi.SCANLIST_ICON, SwingConstants.CENTER);
        this.addTab("Scan", HelperUi.SCANLIST_ICON, scanListManager, I18n.valueByKey("SCANLIST_TOOLTIP"));
        this.setTabComponentAt(
            this.indexOfTab("Scan"),
            labelScan
        );
        I18n.addComponentForKey("SCANLIST_TAB", labelScan);

        this.fileManager.setButtonEnable(false);
        this.shellManager.setButtonEnable(false);
        this.sqlShellManager.setButtonEnable(false);
    }
}

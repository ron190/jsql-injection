package com.jsql.view.swing.util;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerBruteForce;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.manager.ManagerSqlShell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebShell;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.tab.TabConsoles;
import com.jsql.view.swing.tab.TabManagersProxy;
import com.jsql.view.swing.tab.TabResults;
import com.jsql.view.swing.tree.TreeDatabase;

/**
 * Mediator for loosely coupled components.
 */
public final class MediatorHelper {
    
    private static InjectionModel model;
    
    private static TreeDatabase treeDatabase;
    private static TabResults tabResults;
    private static TabManagersProxy tabManagers;
    
    private static JFrameView frame;
    private static Menubar menubar;
    private static PanelAddressBar panelAddressBar;
    private static TabConsoles tabConsoles;
    private static PanelConsoles panelConsoles;
    
    private static ManagerScan managerScan;
    private static ManagerAdminPage managerAdminPage;
    private static ManagerWebShell managerWebshell;
    private static ManagerSqlShell managerSqlshell;
    private static ManagerFile managerFile;
    private static ManagerUpload managerUpload;
    private static ManagerBruteForce managerBruteForce;
    
    /**
     * Utility class.
     */
    private MediatorHelper() {
        //not called
    }

    public static InjectionModel model() {
        return model;
    }
    
    public static TreeDatabase treeDatabase() {
        return treeDatabase;
    }
    
    public static TabResults tabResults() {
        return tabResults;
    }
    
    public static TabManagersProxy tabManagers() {
        return tabManagers;
    }
     
    public static JFrameView frame() {
        return frame;
    }
    
    public static Menubar menubar() {
        return menubar;
    }
    
    public static PanelAddressBar panelAddressBar() {
        return panelAddressBar;
    }
    
    public static TabConsoles tabConsoles() {
        return tabConsoles;
    }
    
    public static PanelConsoles panelConsoles() {
        return panelConsoles;
    }
     
    public static ManagerWebShell managerWebshell() {
        return managerWebshell;
    }
    
    public static ManagerAdminPage managerAdminPage() {
        return managerAdminPage;
    }
    
    public static ManagerFile managerFile() {
        return managerFile;
    }
    
    public static ManagerUpload managerUpload() {
        return managerUpload;
    }
    
    public static ManagerSqlShell managerSqlshell() {
        return managerSqlshell;
    }
    
    public static ManagerScan managerScan() {
        return managerScan;
    }
    
    public static ManagerBruteForce managerBruteForce() {
        return managerBruteForce;
    }
    
    
    // Registering GUI components
    
    public static void register(JFrameView frame) {
        MediatorHelper.frame = frame;
    }
    
    public static void register(Menubar menubar) {
        MediatorHelper.menubar = menubar;
    }
    
    public static void register(PanelAddressBar panelAddress) {
        MediatorHelper.panelAddressBar = panelAddress;
    }
    
    public static void register(TabConsoles tabConsoles) {
        MediatorHelper.tabConsoles = tabConsoles;
    }
    
    public static void register(PanelConsoles panelConsoles) {
        MediatorHelper.panelConsoles = panelConsoles;
    }
    
    public static void register(TreeDatabase treeDatabase) {
        MediatorHelper.treeDatabase = treeDatabase;
    }
    
    public static void register(TabResults tabResults) {
        MediatorHelper.tabResults = tabResults;
    }
    
    public static void register(TabManagersProxy tabManagers) {
        MediatorHelper.tabManagers = tabManagers;
    }
    
    
    // Registering Managers
    
    public static void register(ManagerWebShell managerWebshell) {
        MediatorHelper.managerWebshell = managerWebshell;
    }
    
    public static void register(ManagerAdminPage managerAdminPage) {
        MediatorHelper.managerAdminPage = managerAdminPage;
    }
    
    public static void register(ManagerFile managerFile) {
        MediatorHelper.managerFile = managerFile;
    }
    
    public static void register(ManagerUpload managerUpload) {
        MediatorHelper.managerUpload = managerUpload;
    }
    
    public static void register(ManagerSqlShell managerSqlshell) {
        MediatorHelper.managerSqlshell = managerSqlshell;
    }
    
    public static void register(ManagerScan managerScan) {
        MediatorHelper.managerScan = managerScan;
    }
    
    public static void register(ManagerBruteForce managerBruteForce) {
        MediatorHelper.managerBruteForce = managerBruteForce;
    }

    public static void register(InjectionModel model) {
        MediatorHelper.model = model;
    }
}

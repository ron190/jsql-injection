package com.jsql.view.swing;

import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerBruteForce;
import com.jsql.view.swing.manager.ManagerDatabase;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.manager.ManagerSqlShell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebShell;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.tab.TabConsoles;
import com.jsql.view.swing.tab.TabManagers;
import com.jsql.view.swing.tab.TabResults;
import com.jsql.view.swing.tree.TreeDatabase;

/**
 * Mediator for loosely coupled components.
 */
public final class MediatorGui {
    
    private static TreeDatabase treeDatabase;
    private static TabResults tabResults;
    private static TabManagers tabManagers;
    
    private static JFrameView frame;
    private static Menubar menubar;
    private static PanelAddressBar panelAddressBar;
    private static TabConsoles tabConsoles;
    private static PanelConsoles panelConsoles;
    
    private static ManagerDatabase managerDatabase;
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
    private MediatorGui() {
        //not called
    }
    
    public static TreeDatabase treeDatabase() {
        return treeDatabase;
    }
    
    public static TabResults tabResults() {
        return tabResults;
    }
    
    public static TabManagers tabManagers() {
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
     
    // Registering Managers
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
    
    public static ManagerDatabase managerDatabase() {
        return managerDatabase;
    }
    
    public static ManagerBruteForce managerBruteForce() {
        return managerBruteForce;
    }
    
    // Registering GUI components
    public static void register(JFrameView frame) {
        MediatorGui.frame = frame;
    }
    
    public static void register(Menubar menubar) {
        MediatorGui.menubar = menubar;
    }
    
    public static void register(PanelAddressBar panelAddress) {
        MediatorGui.panelAddressBar = panelAddress;
    }
    
    public static void register(TabConsoles tabConsoles) {
        MediatorGui.tabConsoles = tabConsoles;
    }
    
    public static void register(PanelConsoles panelConsoles) {
        MediatorGui.panelConsoles = panelConsoles;
    }
    
    public static void register(TreeDatabase treeDatabase) {
        MediatorGui.treeDatabase = treeDatabase;
    }
    
    public static void register(TabResults tabResults) {
        MediatorGui.tabResults = tabResults;
    }
    
    public static void register(TabManagers tabManagers) {
        MediatorGui.tabManagers = tabManagers;
    }
    
    // Registering Managers
    public static void register(ManagerWebShell managerWebshell) {
        MediatorGui.managerWebshell = managerWebshell;
    }
    
    public static void register(ManagerAdminPage managerAdminPage) {
        MediatorGui.managerAdminPage = managerAdminPage;
    }
    
    public static void register(ManagerFile managerFile) {
        MediatorGui.managerFile = managerFile;
    }
    
    public static void register(ManagerUpload managerUpload) {
        MediatorGui.managerUpload = managerUpload;
    }
    
    public static void register(ManagerSqlShell managerSqlshell) {
        MediatorGui.managerSqlshell = managerSqlshell;
    }
    
    public static void register(ManagerScan managerScan) {
        MediatorGui.managerScan = managerScan;
    }
    
    public static void register(ManagerDatabase managerDatabase) {
        MediatorGui.managerDatabase = managerDatabase;
    }
    
    public static void register(ManagerBruteForce managerBruteForce) {
        MediatorGui.managerBruteForce = managerBruteForce;
    }
}

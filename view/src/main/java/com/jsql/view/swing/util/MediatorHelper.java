package com.jsql.view.swing.util;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.manager.*;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.tab.TabManagersCards;
import com.jsql.view.swing.tab.TabResults;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.tree.TreeDatabase;

/**
 * Mediator for loosely coupled components.
 */
public final class MediatorHelper {
    
    private static InjectionModel model;
    
    private static TreeDatabase treeDatabase;
    private static TabResults tabResults;
    private static TabManagersCards tabManagersCards;
    
    private static JFrameView frame;
    private static AppMenubar appMenubar;
    private static PanelAddressBar panelAddressBar;
    private static TabbedPaneWheeled tabConsoles;
    private static PanelConsoles panelConsoles;
    
    private static ManagerScan managerScan;
    private static ManagerAdminPage managerAdminPage;
    private static ManagerFile managerFile;
    private static ManagerExploit managerExploit;
    private static ManagerBruteForce managerBruteForce;

    private MediatorHelper() {
        // Utility class
    }

    public static InjectionModel model() {
        return MediatorHelper.model;
    }
    
    public static TreeDatabase treeDatabase() {
        return MediatorHelper.treeDatabase;
    }
    
    public static TabResults tabResults() {
        return MediatorHelper.tabResults;
    }
    
    public static TabManagersCards tabManagersCards() {
        return MediatorHelper.tabManagersCards;
    }
     
    public static JFrameView frame() {
        return MediatorHelper.frame;
    }
    
    public static AppMenubar menubar() {
        return MediatorHelper.appMenubar;
    }
    
    public static PanelAddressBar panelAddressBar() {
        return MediatorHelper.panelAddressBar;
    }
    
    public static TabbedPaneWheeled tabConsoles() {
        return MediatorHelper.tabConsoles;
    }
    
    public static PanelConsoles panelConsoles() {
        return MediatorHelper.panelConsoles;
    }
    
    public static ManagerAdminPage managerAdminPage() {
        return MediatorHelper.managerAdminPage;
    }
    
    public static ManagerFile managerFile() {
        return MediatorHelper.managerFile;
    }

    public static ManagerExploit managerExploit() {
        return MediatorHelper.managerExploit;
    }
    
    public static ManagerScan managerScan() {
        return MediatorHelper.managerScan;
    }
    
    public static ManagerBruteForce managerBruteForce() {
        return MediatorHelper.managerBruteForce;
    }
    
    
    // Registering GUI components
    
    public static void register(JFrameView frame) {
        MediatorHelper.frame = frame;
    }
    
    public static void register(AppMenubar appMenubar) {
        MediatorHelper.appMenubar = appMenubar;
    }
    
    public static void register(PanelAddressBar panelAddress) {
        MediatorHelper.panelAddressBar = panelAddress;
    }
    
    public static void register(TabbedPaneWheeled tabConsoles) {
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
    
    public static void register(TabManagersCards tabManagersCards) {
        MediatorHelper.tabManagersCards = tabManagersCards;
    }
    
    
    // Registering Managers
    public static void register(ManagerAdminPage managerAdminPage) {
        MediatorHelper.managerAdminPage = managerAdminPage;
    }
    
    public static void register(ManagerFile managerFile) {
        MediatorHelper.managerFile = managerFile;
    }
    public static void register(ManagerExploit managerExploit) {
        MediatorHelper.managerExploit = managerExploit;
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

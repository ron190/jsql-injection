package com.jsql.view.swing;

import javax.swing.JTree;

import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerSqlshell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebshell;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.tab.TabConsoles;
import com.jsql.view.swing.tab.TabManagers;
import com.jsql.view.swing.tab.TabResults;

/**
 * Mediator registering usefull components in a loose dependance way.  
 */
public final class MediatorGui {
    private static JTree treeDatabase;
    private static TabResults tabResults;
    private static TabManagers tabManagers;
    
    private static JFrameSoftware frame;
    private static Menubar menubar;
    private static PanelAddressBar panelAddress;
    private static TabConsoles tabConsoles;
    private static PanelConsoles panelConsoles;
    
    private static ManagerWebshell webshellManager;
    private static ManagerAdminPage adminPageManager;
    private static ManagerFile fileManager;
    private static ManagerUpload uploadManager;
    private static ManagerSqlshell sqlshellManager;
    
    /**
     * Utility class.
     */
    private MediatorGui() {
        //not called
    }
    
    public static JTree treeDatabase() {
        return treeDatabase;
    }
    public static TabResults tabResults() {
        return tabResults;
    }
    public static TabManagers tabManagers() {
        return tabManagers;
    }
     
    public static JFrameSoftware frame() {
        return frame;
    }
    public static Menubar menubar() {
        return menubar;
    }
    public static PanelAddressBar panelAddress() {
        return panelAddress;
    }
    public static TabConsoles tabConsoles() {
        return tabConsoles;
    }
    public static PanelConsoles panelConsoles() {
        return panelConsoles;
    }
     
    public static ManagerWebshell webshellManager() {
        return webshellManager;
    }
    public static ManagerAdminPage adminPageManager() {
        return adminPageManager;
    }
    public static ManagerFile fileManager() {
        return fileManager;
    }
    public static ManagerUpload uploadManager() {
        return uploadManager;
    }
    public static ManagerSqlshell sqlshellManager() {
        return sqlshellManager;
    }
    
    // Registering GUI components
    public static void register(JFrameSoftware frame) {
        MediatorGui.frame = frame;
    }
    public static void register(Menubar menubar) {
        MediatorGui.menubar = menubar;
    }
    public static void register(PanelAddressBar panelAddress) {
        MediatorGui.panelAddress = panelAddress;
    }
    public static void register(TabConsoles tabConsoles) {
        MediatorGui.tabConsoles = tabConsoles;
    }
    public static void register(PanelConsoles panelConsoles) {
        MediatorGui.panelConsoles = panelConsoles;
    }
    public static void register(JTree treeDatabase) {
        MediatorGui.treeDatabase = treeDatabase;
    }
    public static void register(TabResults tabResults) {
        MediatorGui.tabResults = tabResults;
    }
    public static void register(TabManagers tabManagers) {
        MediatorGui.tabManagers = tabManagers;
    }
    
    // Registering Managers
    public static void register(ManagerWebshell webshellManager) {
        MediatorGui.webshellManager = webshellManager;
    }
    public static void register(ManagerAdminPage adminPageManager) {
        MediatorGui.adminPageManager = adminPageManager;
    }
    public static void register(ManagerFile fileManager) {
        MediatorGui.fileManager = fileManager;
    }
    public static void register(ManagerUpload uploadManager) {
        MediatorGui.uploadManager = uploadManager;
    }
    public static void register(ManagerSqlshell sqlShellManager) {
        MediatorGui.sqlshellManager = sqlShellManager;
    }
}

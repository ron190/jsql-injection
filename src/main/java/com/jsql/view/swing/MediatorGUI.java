package com.jsql.view.swing;

import javax.swing.JTree;

import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerFile;
import com.jsql.view.swing.manager.ManagerSqlShell;
import com.jsql.view.swing.manager.ManagerUpload;
import com.jsql.view.swing.manager.ManagerWebshell;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.panel.PanelStatusbar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.tab.TabbedPaneConsoles;
import com.jsql.view.swing.tab.TabbedPaneManagers;
import com.jsql.view.swing.tab.TabbedPaneResults;

/**
 * Mediator registering usefull components in a loose dependance way.  
 */
public final class MediatorGUI {
    private static JTree databaseTree;
    private static TabbedPaneResults right;
    private static TabbedPaneManagers left;
    
    private static JFrameGUI gui;
    private static Menubar menubar;
    private static PanelAddressBar top;
    private static TabbedPaneConsoles bottom;
    private static PanelConsoles bottomPanel;
    private static PanelStatusbar status;
    
    private static ManagerWebshell shellManager;
    private static ManagerAdminPage adminPageManager;
    private static ManagerFile fileManager;
    private static ManagerUpload uploadManager;
    private static ManagerSqlShell sqlShellManager;
    
    /**
     * Utility class.
     */
    private MediatorGUI() {
        //not called
    }
    
    public static JTree databaseTree() {
        return databaseTree;
    }
    public static TabbedPaneResults tabResults() {
        return right;
    }
    public static TabbedPaneManagers tabManagers() {
        return left;
    }
     
    public static JFrameGUI jFrame() {
        return gui;
    }
    public static Menubar menubar() {
        return menubar;
    }
    public static PanelAddressBar panelAddress() {
        return top;
    }
    public static TabbedPaneConsoles tabConsoles() {
        return bottom;
    }
    public static PanelConsoles panelConsoles() {
        return bottomPanel;
    }
    public static PanelStatusbar panelStatus() {
        return status;
    }
     
    public static ManagerWebshell shellManager() {
        return shellManager;
    }
    public static ManagerAdminPage adminPageManager() {
        return adminPageManager;
    }
    public static ManagerFile shellfileManager() {
        return fileManager;
    }
    public static ManagerUpload uploadManager() {
        return uploadManager;
    }
    public static ManagerSqlShell sqlShellManager() {
        return sqlShellManager;
    }
    
    // Registering GUI components
    public static void register(JFrameGUI gui) {
        MediatorGUI.gui = gui;
    }
    public static void register(Menubar menubar) {
        MediatorGUI.menubar = menubar;
    }
    public static void register(PanelAddressBar top) {
        MediatorGUI.top = top;
    }
    public static void register(TabbedPaneConsoles bottom) {
        MediatorGUI.bottom = bottom;
    }
    public static void register(PanelConsoles bottomPanel) {
        MediatorGUI.bottomPanel = bottomPanel;
    }
    public static void register(PanelStatusbar status) {
        MediatorGUI.status = status;
    }
    public static void register(JTree databaseTree) {
        MediatorGUI.databaseTree = databaseTree;
    }
    public static void register(TabbedPaneResults right) {
        MediatorGUI.right = right;
    }
    public static void register(TabbedPaneManagers left) {
        MediatorGUI.left = left;
    }
    
    // Registering Managers
    public static void register(ManagerWebshell shellManager) {
        MediatorGUI.shellManager = shellManager;
    }
    public static void register(ManagerAdminPage adminPageManager) {
        MediatorGUI.adminPageManager = adminPageManager;
    }
    public static void register(ManagerFile fileManager) {
        MediatorGUI.fileManager = fileManager;
    }
    public static void register(ManagerUpload uploadManager) {
        MediatorGUI.uploadManager = uploadManager;
    }
    public static void register(ManagerSqlShell sqlShellManager) {
        MediatorGUI.sqlShellManager = sqlShellManager;
    }
}

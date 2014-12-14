package com.jsql.view;

import javax.swing.JTree;

import com.jsql.model.injection.InjectionModel;
import com.jsql.view.manager.ManagerAdminPage;
import com.jsql.view.manager.ManagerFile;
import com.jsql.view.manager.ManagerSQLShell;
import com.jsql.view.manager.ManagerUpload;
import com.jsql.view.manager.ManagerWebshell;
import com.jsql.view.menubar.Menubar;
import com.jsql.view.panel.PanelBottom;
import com.jsql.view.panel.PanelStatusbar;
import com.jsql.view.panel.PanelTop;
import com.jsql.view.tab.AdapterBottomTabbedPane;
import com.jsql.view.tab.AdapterLeftTabbedPane;
import com.jsql.view.tab.AdapterRightTabbedPane;

/**
 * Mediator registering usefull components in a loose dependance way.  
 */
public final class MediatorGUI {
    private static JTree databaseTree;
    private static AdapterRightTabbedPane right;
    private static AdapterLeftTabbedPane left;
    
    private static InjectionModel model;
    private static GUI gui;
    private static Menubar menubar;
    private static PanelTop top;
    private static AdapterBottomTabbedPane bottom;
    private static PanelBottom bottomPanel;
    private static PanelStatusbar status;
    
    private static ManagerWebshell shellManager;
    private static ManagerAdminPage adminPageManager;
    private static ManagerFile fileManager;
    private static ManagerUpload uploadManager;
    private static ManagerSQLShell sqlShellManager;
    
    /**
     * Utility class.
     */
    private MediatorGUI() {
        //not called
    }
    
    public static JTree databaseTree() {
        return databaseTree;
    }
    public static AdapterRightTabbedPane right() {
        return right;
    }
    public static AdapterLeftTabbedPane left() {
        return left;
    }
     
    public static InjectionModel model() {
        return model;
    }
    public static GUI gui() {
        return gui;
    }
    public static Menubar menubar() {
        return menubar;
    }
    public static PanelTop top() {
        return top;
    }
    public static AdapterBottomTabbedPane bottom() {
        return bottom;
    }
    public static PanelBottom bottomPanel() {
        return bottomPanel;
    }
    public static PanelStatusbar status() {
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
    public static ManagerSQLShell sqlShellManager() {
        return sqlShellManager;
    }
    
    // Registering Model
    public static void register(InjectionModel model) {
        MediatorGUI.model = model;
    }
    
    // Registering GUI components
    public static void register(GUI gui) {
        MediatorGUI.gui = gui;
    }
    public static void register(Menubar menubar) {
        MediatorGUI.menubar = menubar;
    }
    public static void register(PanelTop top) {
        MediatorGUI.top = top;
    }
    public static void register(AdapterBottomTabbedPane bottom) {
        MediatorGUI.bottom = bottom;
    }
    public static void register(PanelBottom bottomPanel) {
        MediatorGUI.bottomPanel = bottomPanel;
    }
    public static void register(PanelStatusbar status) {
        MediatorGUI.status = status;
    }
    public static void register(JTree databaseTree) {
        MediatorGUI.databaseTree = databaseTree;
    }
    public static void register(AdapterRightTabbedPane right) {
        MediatorGUI.right = right;
    }
    public static void register(AdapterLeftTabbedPane left) {
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
    public static void register(ManagerSQLShell sqlShellManager) {
        MediatorGUI.sqlShellManager = sqlShellManager;
    }
}

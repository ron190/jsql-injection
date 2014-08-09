package com.jsql.view;

import javax.swing.JTree;

import com.jsql.model.InjectionModel;
import com.jsql.view.manager.AdminPageManager;
import com.jsql.view.manager.FileManager;
import com.jsql.view.manager.SQLShellManager;
import com.jsql.view.manager.UploadManager;
import com.jsql.view.manager.WebshellManager;
import com.jsql.view.menubar.Menubar;
import com.jsql.view.panel.AdapterLeftPane;
import com.jsql.view.panel.AdapterRightPane;
import com.jsql.view.panel.BottomPanel;
import com.jsql.view.panel.StatusbarPanel;
import com.jsql.view.panel.TopPanel;
import com.jsql.view.tab.AdapterBottomTabbedPane;

/**
 * Mediator registering usefull components in a loose dependance way.  
 */
public final class GUIMediator {
    /**
     * Utility class.
     */
    private GUIMediator() {
        //not called
    }
    
    private static JTree databaseTree;
    private static AdapterRightPane right;
    private static AdapterLeftPane left;
    
    private static InjectionModel model;
    private static GUI gui;
    private static Menubar menubar;
    private static TopPanel top;
    private static AdapterBottomTabbedPane bottom;
    private static BottomPanel bottomPanel;
    private static StatusbarPanel status;
    
    private static WebshellManager shellManager;
    private static AdminPageManager adminPageManager;
    private static FileManager fileManager;
    private static UploadManager uploadManager;
    private static SQLShellManager sqlShellManager;
    
    public static JTree databaseTree() {
        return databaseTree;
    }
    public static AdapterRightPane right() {
        return right;
    }
    public static AdapterLeftPane left() {
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
    public static TopPanel top() {
        return top;
    }
    public static AdapterBottomTabbedPane bottom() {
        return bottom;
    }
    public static BottomPanel bottomPanel() {
        return bottomPanel;
    }
    public static StatusbarPanel status() {
        return status;
    }
     
    public static WebshellManager shellManager() {
        return shellManager;
    }
    public static AdminPageManager adminPageManager() {
        return adminPageManager;
    }
    public static FileManager shellfileManager() {
        return fileManager;
    }
    public static UploadManager uploadManager() {
        return uploadManager;
    }
    public static SQLShellManager sqlShellManager() {
        return sqlShellManager;
    }
    
    // Registering Model
    public static void register(InjectionModel model) {
        GUIMediator.model = model;
    }
    
    // Registering GUI components
    public static void register(GUI gui) {
        GUIMediator.gui = gui;
    }
    public static void register(Menubar menubar) {
        GUIMediator.menubar = menubar;
    }
    public static void register(TopPanel top) {
        GUIMediator.top = top;
    }
    public static void register(AdapterBottomTabbedPane bottom) {
        GUIMediator.bottom = bottom;
    }
    public static void register(BottomPanel bottomPanel) {
        GUIMediator.bottomPanel = bottomPanel;
    }
    public static void register(StatusbarPanel status) {
        GUIMediator.status = status;
    }
    public static void register(JTree databaseTree) {
        GUIMediator.databaseTree = databaseTree;
    }
    public static void register(AdapterRightPane right) {
        GUIMediator.right = right;
    }
    public static void register(AdapterLeftPane left) {
        GUIMediator.left = left;
    }
    
    // Registering Managers
    public static void register(WebshellManager shellManager) {
        GUIMediator.shellManager = shellManager;
    }
    public static void register(AdminPageManager adminPageManager) {
        GUIMediator.adminPageManager = adminPageManager;
    }
    public static void register(FileManager fileManager) {
        GUIMediator.fileManager = fileManager;
    }
    public static void register(UploadManager uploadManager) {
        GUIMediator.uploadManager = uploadManager;
    }
    public static void register(SQLShellManager sqlShellManager) {
        GUIMediator.sqlShellManager = sqlShellManager;
    }
}

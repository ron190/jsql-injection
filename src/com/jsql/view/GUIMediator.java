package com.jsql.view;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jsql.controller.InjectionController;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.view.dnd.tab.DnDTabbedPane;
import com.jsql.view.manager.AdminPageManager;
import com.jsql.view.manager.FileManager;
import com.jsql.view.manager.SQLShellManager;
import com.jsql.view.manager.UploadManager;
import com.jsql.view.manager.WebshellManager;
import com.jsql.view.panel.TopPanel;
import com.jsql.view.pattern.adapter.BottomTabbedPane;
import com.jsql.view.pattern.adapter.LeftTabbedPane;

public class GUIMediator {
	private static JTree databaseTree;
	private static DnDTabbedPane right;
	private static LeftTabbedPane left;
	private static TopPanel top;
	private static BottomTabbedPane bottom;
	 
	private static InjectionController controller;     
	private static InjectionModel model;     
	private static GUI gui;
	 
	private static WebshellManager shellManager;     
	private static AdminPageManager adminPageManager;
	private static FileManager fileManager;          
	private static UploadManager uploadManager;      
	private static SQLShellManager sqlShellManager;
	
	public static JTree databaseTree(){return databaseTree;} // gui.model.
	public static DnDTabbedPane right(){return right;} // gui.model.
	public static LeftTabbedPane left(){return left;} // gui.model.
	 
	public static InjectionController controller(){return controller;} // gui.model.
	public static InjectionModel model(){return model;} // gui.model.
	public static GUI gui(){return gui;} // gui.
	public static TopPanel top(){return top;} // gui.
	public static BottomTabbedPane bottom(){return bottom;} // gui.
	 
	public static WebshellManager shellManager(){return shellManager;}
	public static AdminPageManager adminPageManager(){return adminPageManager;}
	public static FileManager shellfileManager(){return fileManager;}
	public static UploadManager uploadManager(){return uploadManager;}
	public static SQLShellManager sqlShellManager(){return sqlShellManager;}
	
	public static void register(InjectionController controller){GUIMediator.controller = controller;}
	
	public static void register(JTree databaseTree){GUIMediator.databaseTree = databaseTree;}
	public static void register(DnDTabbedPane right){GUIMediator.right = right;}
	public static void register(LeftTabbedPane left){GUIMediator.left = left;}
	
	public static void register(InjectionModel model){GUIMediator.model = model;}
	public static void register(GUI gui){GUIMediator.gui = gui;}
	public static void register(TopPanel top){GUIMediator.top = top;}
	public static void register(BottomTabbedPane bottom){GUIMediator.bottom = bottom;}
	        
	public static void register(WebshellManager shellManager){GUIMediator.shellManager = shellManager;}
	public static void register(AdminPageManager adminPageManager){GUIMediator.adminPageManager = adminPageManager;}
	public static void register(FileManager fileManager){GUIMediator.fileManager = fileManager;}
	public static void register(UploadManager uploadManager){GUIMediator.uploadManager = uploadManager;}
	public static void register(SQLShellManager sqlShellManager){GUIMediator.sqlShellManager = sqlShellManager;}
	
	public DefaultMutableTreeNode getNode(ElementDatabase elt){
        return gui.treeNodeModels.get(elt);
    }
}

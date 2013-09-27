/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.panel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.model.InjectionModel;
import com.jsql.view.ActionHandler;
import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundBorder;
import com.jsql.view.RoundScroller;
import com.jsql.view.component.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.component.TabbedPane;
import com.jsql.view.manager.AdminPageManager;
import com.jsql.view.manager.BruteForceManager;
import com.jsql.view.manager.CoderManager;
import com.jsql.view.manager.FileManager;
import com.jsql.view.manager.SQLShellManager;
import com.jsql.view.manager.UploadManager;
import com.jsql.view.manager.WebshellManager;
import com.jsql.view.tree.NodeEditor;
import com.jsql.view.tree.NodeRenderer;


/**
 * Pane composed of tree and tabs on top, and info tabs on bottom.
 */
public class LeftRightBottom extends JSplitPaneWithZeroSizeDivider{
    private static final long serialVersionUID = -5696939494054282278L;
    
    public JTabbedPane left;
    
    int verticalSplitter,horizontalSplitter;
    
    public WebshellManager shellManager;
    public AdminPageManager adminPageManager;
    public FileManager fileManager;
    public UploadManager uploadManager;
    public SQLShellManager sqlShellManager;
    
    private final String VERTICALSPLITTER_PREFNAME = "verticalSplitter-0.4";
    private final String HORIZONTALSPLITTER_PREFNAME = "horizontalSplitter-0.4";
    
    public JTabbedPane bottom;
    
    public LeftRightBottom(final GUI gui){
        super(JSplitPane.VERTICAL_SPLIT, true);
        
        shellManager = new WebshellManager(gui);
        adminPageManager = new AdminPageManager(gui);
        fileManager = new FileManager(gui);
        uploadManager = new UploadManager(gui);
        sqlShellManager = new SQLShellManager(gui);
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        this.verticalSplitter = prefs.getInt(this.VERTICALSPLITTER_PREFNAME, 300);
        this.horizontalSplitter = prefs.getInt(this.HORIZONTALSPLITTER_PREFNAME, 200);
//        if(this.horizontalSplitter > 365)
//            this.horizontalSplitter = 365;
        
        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("No database");
        gui.databaseTree = new JTree(root);
        
        // Graphic manager for components
        NodeRenderer renderer = new NodeRenderer();
        gui.databaseTree.setCellRenderer(renderer);
        
        // Action manager for components
        NodeEditor editor = new NodeEditor(gui.databaseTree, gui.controller, gui.right);
        gui.databaseTree.setCellEditor(editor);
        
        // Tree setting
        gui.databaseTree.setEditable(true);    // allows repaint nodes
        gui.databaseTree.setShowsRootHandles(true);
        gui.databaseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Dirty trick that allows repaint progressbar
        gui.databaseTree.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if(arg0 != null){
                    gui.databaseTree.firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY, 
                        !gui.databaseTree.isRootVisible(),
                        gui.databaseTree.isRootVisible()
                    );
                }
            }
            @Override public void treeStructureChanged(TreeModelEvent arg0) {}
            @Override public void treeNodesRemoved(TreeModelEvent arg0) {}
            @Override public void treeNodesInserted(TreeModelEvent arg0) {}
        });
        
        // Give focus on tab change
        left = new TabbedPane(true);
        left.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        RoundScroller scroller = new RoundScroller(gui.databaseTree);
        left.addTab("Database", GUITools.DATABASE_SERVER, scroller, "Explore databases from remote host");
        left.addTab("Admin page", GUITools.ADMIN_SERVER, adminPageManager, "Test admin pages on remote host");
        left.addTab("File", GUITools.FILE_SERVER, fileManager, "Read files from remote host");
        left.addTab("Web shell", GUITools.SHELL_SERVER, shellManager, "<html>Create a web shell to remote host ; open a terminal<br><i>Allows OS commands like ipconfig/ifconfig</i></html>");
        left.addTab("SQL shell", GUITools.SHELL_SERVER, sqlShellManager, "<html>Create a SQL shell to remote host ; open a terminal<br><i>Allows SQL commands like update/grant</i></html>");
        left.addTab("Upload", GUITools.UPLOAD, uploadManager, "Upload a file to host");
        left.addTab("Brute force", GUITools.BRUTER, new BruteForceManager(gui.model), "Brute force hashes");
        left.addTab("Coder", GUITools.CODER, new CoderManager(), "Encode or decode a string");
        left.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        new ActionHandler(left);
        
        left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        gui.right.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        gui.right.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Tree and tabs on top
        final JSplitPaneWithZeroSizeDivider leftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        leftRight.setLeftComponent( left );
        leftRight.setRightComponent( gui.right );
        leftRight.setDividerLocation(this.verticalSplitter);
        leftRight.setDividerSize(0);
        leftRight.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        this.setDividerSize(0);
        this.setBorder(new RoundBorder(2,2,true));
        
        // Infos tabs in bottom
        bottom = new TabbedPane();
        new ActionHandler(bottom);
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        bottom.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        bottom.addTab("Console", new ImageIcon(getClass().getResource("/com/jsql/view/images/console.gif")), new RoundScroller(gui.consoleArea), "General information");
        if (prefs.getBoolean(gui.CHUNK_VISIBLE, true))
            bottom.addTab("Chunk", new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), new RoundScroller(gui.chunks), "Hexadecimal data recovered");
        if (prefs.getBoolean(gui.BINARY_VISIBLE, true))
            bottom.addTab("Binary", new ImageIcon(getClass().getResource("/com/jsql/view/images/binary.gif")), new RoundScroller(gui.binaryArea), "Time/Blind bytes");
        if (prefs.getBoolean(gui.HEADER_VISIBLE, true))
            bottom.addTab("Header", new ImageIcon(getClass().getResource("/com/jsql/view/images/header.gif")), new RoundScroller(gui.headers), "URL calls information");
        if (prefs.getBoolean(gui.JAVA_VISIBLE, false))
            bottom.addTab("Java", new ImageIcon(getClass().getResource("/com/jsql/view/images/cup.png")), new RoundScroller(gui.javaDebug), "Java console");
        
        // Setting for top and bottom components
        this.setTopComponent(leftRight);
        this.setBottomComponent(bottom);
        this.setDividerLocation(570 - this.horizontalSplitter);
        
        this.setResizeWeight(1); // defines left and bottom pane
        
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(LeftRightBottom.this.VERTICALSPLITTER_PREFNAME, leftRight.getDividerLocation());
                prefs.putInt(LeftRightBottom.this.HORIZONTALSPLITTER_PREFNAME, LeftRightBottom.this.getHeight() - LeftRightBottom.this.getDividerLocation());
                
                prefs.putBoolean(gui.BINARY_VISIBLE, false);
                prefs.putBoolean(gui.CHUNK_VISIBLE, false);
                prefs.putBoolean(gui.HEADER_VISIBLE, false);
                prefs.putBoolean(gui.JAVA_VISIBLE, false);
                
                for(int i=0; i < gui.outputPanel.bottom.getTabCount() ;i++){
                    if (gui.outputPanel.bottom.getTitleAt(i).equals("Binary")) {
                        prefs.putBoolean(gui.BINARY_VISIBLE, true);
                    }else if (gui.outputPanel.bottom.getTitleAt(i).equals("Chunk")) {
                        prefs.putBoolean(gui.CHUNK_VISIBLE, true);
                    }else if (gui.outputPanel.bottom.getTitleAt(i).equals("Header")) {
                        prefs.putBoolean(gui.HEADER_VISIBLE, true);
                    }else if (gui.outputPanel.bottom.getTitleAt(i).equals("Java")) {
                        prefs.putBoolean(gui.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        gui.chunks.setLineWrap(true);
        gui.headers.setLineWrap(true);
        gui.binaryArea.setLineWrap(true);
        gui.consoleArea.setLineWrap(true);
    }
}

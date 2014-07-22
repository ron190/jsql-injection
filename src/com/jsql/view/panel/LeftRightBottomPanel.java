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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.OverlayLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.model.InjectionModel;
import com.jsql.view.ActionHandler;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.component.MouseTabbedPane;
import com.jsql.view.component.RoundBorder;
import com.jsql.view.component.RoundScroller;
import com.jsql.view.manager.AdminPageManager;
import com.jsql.view.manager.BruteForceManager;
import com.jsql.view.manager.CoderManager;
import com.jsql.view.manager.FileManager;
import com.jsql.view.manager.SQLShellManager;
import com.jsql.view.manager.UploadManager;
import com.jsql.view.manager.WebshellManager;
import com.jsql.view.pattern.adapter.BottomTabbedPane;
import com.jsql.view.pattern.adapter.LeftTabbedPane;
import com.jsql.view.tree.NodeEditor;
import com.jsql.view.tree.NodeModelEmpty;
import com.jsql.view.tree.NodeRenderer;

/**
 * Pane composed of tree and tabs on top, and info tabs on bottom.
 */
@SuppressWarnings("serial")
public class LeftRightBottomPanel extends JSplitPaneWithZeroSizeDivider{
    
    public WebshellManager shellManager;
    public AdminPageManager adminPageManager;
    public FileManager fileManager;
    public UploadManager uploadManager;
    public SQLShellManager sqlShellManager;
    
    /* Hardcoded setDividerLocation height : 600 ? */
    private final String VERTICALSPLITTER_PREFNAME = "verticalSplitter-0.6";
    private final String HORIZONTALSPLITTER_PREFNAME = "horizontalSplitter-0.6";
    int verticalSplitter, horizontalSplitter;
    
    public LeftRightBottomPanel(){
        super(JSplitPane.VERTICAL_SPLIT, true);
        
        shellManager = new WebshellManager();
        adminPageManager = new AdminPageManager();
        fileManager = new FileManager();
        uploadManager = new UploadManager();
        sqlShellManager = new SQLShellManager();
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        this.verticalSplitter = prefs.getInt(this.VERTICALSPLITTER_PREFNAME, 300);
        this.horizontalSplitter = prefs.getInt(this.HORIZONTALSPLITTER_PREFNAME, 200);
//        if(this.horizontalSplitter > 365)
//            this.horizontalSplitter = 365;
        
        // First node in tree
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("No database");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty("No database"));
//        gui.databaseTree = new JTree(root);
        GUIMediator.register(new JTree(root));
        
        // Graphic manager for components
        NodeRenderer renderer = new NodeRenderer();
        GUIMediator.databaseTree().setCellRenderer(renderer);
        
        // Action manager for components
        NodeEditor editor = new NodeEditor();
        GUIMediator.databaseTree().setCellEditor(editor);
        
        // Tree setting
        GUIMediator.databaseTree().setEditable(true);    // allows repaint nodes
        GUIMediator.databaseTree().setShowsRootHandles(true);
        GUIMediator.databaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Dirty trick that allows repaint progressbar
        GUIMediator.databaseTree().getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if(arg0 != null){
                    GUIMediator.databaseTree().firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY, 
                        !GUIMediator.databaseTree().isRootVisible(),
                        GUIMediator.databaseTree().isRootVisible()
                    );
                }
            }
            @Override public void treeStructureChanged(TreeModelEvent arg0) {}
            @Override public void treeNodesRemoved(TreeModelEvent arg0) {}
            @Override public void treeNodesInserted(TreeModelEvent arg0) {}
        });
        
        // Give focus on tab change
        GUIMediator.register(new LeftTabbedPane(new MouseTabbedPane()));
        GUIMediator.left().activateMenu();
        GUIMediator.left().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        RoundScroller scroller = new RoundScroller(GUIMediator.databaseTree());
        GUIMediator.left().addTab("Database", GUITools.DATABASE_SERVER, scroller, "Explore databases from remote host");
        GUIMediator.left().addTab("Admin page", GUITools.ADMIN_SERVER, adminPageManager, "Test admin pages on remote host");
        GUIMediator.left().addTab("File", GUITools.FILE_SERVER, fileManager, "Read files from remote host");
        GUIMediator.left().addTab("Web shell", GUITools.SHELL_SERVER, shellManager, "<html>Create a web shell to remote host ; open a terminal<br><i>Allows OS commands like ipconfig/ifconfig</i></html>");
        GUIMediator.left().addTab("SQL shell", GUITools.SHELL_SERVER, sqlShellManager, "<html>Create a SQL shell to remote host ; open a terminal<br><i>Allows SQL commands like update/grant</i></html>");
        GUIMediator.left().addTab("Upload", GUITools.UPLOAD, uploadManager, "Upload a file to host");
        GUIMediator.left().addTab("Brute force", GUITools.BRUTER, new BruteForceManager(GUIMediator.model()), "Brute force hashes");
        GUIMediator.left().addTab("Coder", GUITools.CODER, new CoderManager(), "Encode or decode a string");
        GUIMediator.left().setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        ActionHandler.addShortcut(GUIMediator.left());
        
        GUIMediator.left().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        GUIMediator.right().setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        GUIMediator.right().setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Tree and tabs on top
        final JSplitPaneWithZeroSizeDivider leftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        leftRight.setLeftComponent(GUIMediator.left());
        leftRight.setRightComponent( GUIMediator.right() );
        leftRight.setDividerLocation(this.verticalSplitter);
        leftRight.setDividerSize(0);
        leftRight.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        this.setDividerSize(0);
        this.setBorder(new RoundBorder(2,2,true));
        
        // Infos tabs in bottom
        GUIMediator.register(new BottomTabbedPane(new MouseTabbedPane()));
        
        ActionHandler.addShortcut(GUIMediator.bottom());
        GUIMediator.bottom().setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        GUIMediator.bottom().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        GUIMediator.bottom().addTab("Console", new ImageIcon(getClass().getResource("/com/jsql/view/images/console.gif")), new RoundScroller(GUIMediator.gui().consoleArea), "General information");
        if (prefs.getBoolean(GUIMediator.gui().CHUNK_VISIBLE, true))
            GUIMediator.bottom().addTab("Chunk", new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), new RoundScroller(GUIMediator.gui().chunks), "Hexadecimal data recovered");
        if (prefs.getBoolean(GUIMediator.gui().BINARY_VISIBLE, true))
            GUIMediator.bottom().addTab("Binary", new ImageIcon(getClass().getResource("/com/jsql/view/images/binary.gif")), new RoundScroller(GUIMediator.gui().binaryArea), "Time/Blind bytes");
        if (prefs.getBoolean(GUIMediator.gui().HEADER_VISIBLE, true))
            GUIMediator.bottom().addTab("Header", new ImageIcon(getClass().getResource("/com/jsql/view/images/header.gif")), new RoundScroller(GUIMediator.gui().headers), "URL calls information");
        if (prefs.getBoolean(GUIMediator.gui().JAVA_VISIBLE, false))
            GUIMediator.bottom().addTab("Java", new ImageIcon(getClass().getResource("/com/jsql/view/images/cup.png")), new RoundScroller(GUIMediator.gui().javaDebug), "Java errors");
        
        JPanel leftRightBottomPanel = new JPanel(new BorderLayout());
        leftRightBottomPanel.add(leftRight, BorderLayout.CENTER);
        JPanel arrowUpPanel= new JPanel();
        arrowUpPanel.setLayout(new BorderLayout());
        arrowUpPanel.setOpaque(false);
        arrowUpPanel.setPreferredSize(new Dimension(17,22));
        arrowUpPanel.setMaximumSize(new Dimension(17,22));
        JButton hideBottomButton = new BasicArrowButton(BasicArrowButton.NORTH);
        hideBottomButton.setBorderPainted(false);
        hideBottomButton.setOpaque(false);

        HideShowConsoleAction hideShowAction = new HideShowConsoleAction(arrowUpPanel);
        
        hideBottomButton.addMouseListener(hideShowAction);
        arrowUpPanel.add(Box.createHorizontalGlue());
        arrowUpPanel.add(hideBottomButton, BorderLayout.EAST);
        arrowUpPanel.setVisible(false);

        leftRightBottomPanel.add(arrowUpPanel, BorderLayout.SOUTH);
        
        // Setting for top and bottom components
        this.setTopComponent(leftRightBottomPanel);
        
        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout( new OverlayLayout(bottomPanel) );
        
        BasicArrowButton showBottomButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        showBottomButton.setBorderPainted(false);
        showBottomButton.setPreferredSize(showBottomButton.getPreferredSize());
        showBottomButton.setMaximumSize(showBottomButton.getPreferredSize());
        
        showBottomButton.addMouseListener(hideShowAction);
        
        JPanel arrowDownPanel = new JPanel();
        arrowDownPanel.setLayout( new BoxLayout(arrowDownPanel, BoxLayout.PAGE_AXIS) );
        arrowDownPanel.setOpaque(false);
        showBottomButton.setOpaque(false);
        arrowDownPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0)); // Disable overlap with zerosizesplitter
        arrowDownPanel.setPreferredSize(new Dimension(17,30));
        arrowDownPanel.setMaximumSize(new Dimension(17,30));
        arrowDownPanel.add(showBottomButton);
        bottomPanel.add( arrowDownPanel );
        bottomPanel.add(GUIMediator.bottom());
        
        // Do Overlay
        arrowDownPanel.setAlignmentX(1.0f);
        arrowDownPanel.setAlignmentY(0.0f);
        GUIMediator.bottom().setAlignmentX(1.0f);
        GUIMediator.bottom().setAlignmentY(0.0f);
        
        this.setBottomComponent(bottomPanel);
        this.setDividerLocation(596 - this.horizontalSplitter);
        
        this.setResizeWeight(1); // defines left and bottom pane
        
        GUIMediator.gui().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(LeftRightBottomPanel.this.VERTICALSPLITTER_PREFNAME, leftRight.getDividerLocation());
                prefs.putInt(LeftRightBottomPanel.this.HORIZONTALSPLITTER_PREFNAME, LeftRightBottomPanel.this.getHeight() - LeftRightBottomPanel.this.getDividerLocation());
                
                prefs.putBoolean(GUIMediator.gui().BINARY_VISIBLE, false);
                prefs.putBoolean(GUIMediator.gui().CHUNK_VISIBLE, false);
                prefs.putBoolean(GUIMediator.gui().HEADER_VISIBLE, false);
                prefs.putBoolean(GUIMediator.gui().JAVA_VISIBLE, false);
                
                for(int i=0; i < GUIMediator.bottom().getTabCount() ;i++){
                    if (GUIMediator.bottom().getTitleAt(i).equals("Binary")) {
                        prefs.putBoolean(GUIMediator.gui().BINARY_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Chunk")) {
                        prefs.putBoolean(GUIMediator.gui().CHUNK_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Header")) {
                        prefs.putBoolean(GUIMediator.gui().HEADER_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Java")) {
                        prefs.putBoolean(GUIMediator.gui().JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        GUIMediator.gui().chunks.setLineWrap(true);
        GUIMediator.gui().headers.setLineWrap(true);
        GUIMediator.gui().binaryArea.setLineWrap(true);
        GUIMediator.gui().consoleArea.setLineWrap(true);
    }
    
    class HideShowConsoleAction extends MouseAdapter {
    	int loc = 0;
		JPanel panel;
		public HideShowConsoleAction(JPanel panel) {
			super();
			this.panel = panel;
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
    		if(LeftRightBottomPanel.this.getTopComponent().isVisible() && LeftRightBottomPanel.this.getBottomComponent().isVisible()){
    			LeftRightBottomPanel.this.getBottomComponent().setVisible(false);
    			loc = LeftRightBottomPanel.this.getDividerLocation();
    			panel.setVisible(true);
    			LeftRightBottomPanel.this.disableDragSize();
    		}else{
    			LeftRightBottomPanel.this.getBottomComponent().setVisible(true);
    			LeftRightBottomPanel.this.setDividerLocation(loc);
    			panel.setVisible(false);
    			LeftRightBottomPanel.this.enableDragSize();
    		}
		}
	}
}

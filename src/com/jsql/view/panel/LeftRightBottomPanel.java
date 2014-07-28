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

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.HTTPHeader;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.JScrollPanePixelBorder;
import com.jsql.view.component.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.manager.AdminPageManager;
import com.jsql.view.manager.BruteForceManager;
import com.jsql.view.manager.CoderManager;
import com.jsql.view.manager.FileManager;
import com.jsql.view.manager.SQLShellManager;
import com.jsql.view.manager.UploadManager;
import com.jsql.view.manager.WebshellManager;
import com.jsql.view.tab.BottomTabbedPaneAdapter;
import com.jsql.view.tab.LeftTabbedPaneAdapter;
import com.jsql.view.tab.MouseTabbedPane;
import com.jsql.view.tree.NodeEditor;
import com.jsql.view.tree.NodeModelEmpty;
import com.jsql.view.tree.NodeRenderer;

/**
 * Pane composed of tree and tabs on top, and info tabs on bottom.
 */
@SuppressWarnings("serial")
public class LeftRightBottomPanel extends JSplitPaneWithZeroSizeDivider{
    
    public WebshellManager shellManager = new WebshellManager();
    public AdminPageManager adminPageManager = new AdminPageManager();
    public FileManager fileManager = new FileManager();
    public UploadManager uploadManager = new UploadManager();
    public SQLShellManager sqlShellManager = new SQLShellManager();
    
    private final String VERTICALSPLITTER_PREFNAME = "verticalSplitter-" + InjectionModel.JSQLVERSION;
    private final String HORIZONTALSPLITTER_PREFNAME = "horizontalSplitter-" + InjectionModel.JSQLVERSION;
    
    public List<HTTPHeader> listHTTPHeader = new ArrayList<HTTPHeader>();
    public JTable networkTable;

    public LeftRightBottomPanel(){
        super(JSplitPane.VERTICAL_SPLIT, true);
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        int verticalSplitter = prefs.getInt(this.VERTICALSPLITTER_PREFNAME, 300);
        int horizontalSplitter = prefs.getInt(this.HORIZONTALSPLITTER_PREFNAME, 200);
//        if(this.horizontalSplitter > 365)
//            this.horizontalSplitter = 365;
        
        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeModelEmpty("No database"));
        GUIMediator.register(new JTree(root));
        
        // Graphic manager for components
        GUIMediator.databaseTree().setCellRenderer(new NodeRenderer());
        
        // Action manager for components
        GUIMediator.databaseTree().setCellEditor(new NodeEditor());
        
        // Tree setting
        GUIMediator.databaseTree().setEditable(true);    // allows repaint nodes
        GUIMediator.databaseTree().setShowsRootHandles(true);
        GUIMediator.databaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Dirty trick that allows to repaint progressbar
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
        
        GUIMediator.register(new LeftTabbedPaneAdapter());
        GUIMediator.left().setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
        GUIMediator.left().setMinimumSize(new Dimension()); // Allows to resize to zero
        GUIMediator.left().activateMenu();
        
        JScrollPanePixelBorder scroller = new JScrollPanePixelBorder(1,1,0,0,GUIMediator.databaseTree());
        
        GUIMediator.left().addTab("Database", GUITools.DATABASE_SERVER_ICON, scroller, "Explore databases from remote host");
        GUIMediator.left().addTab("Admin page", GUITools.ADMIN_SERVER_ICON, adminPageManager, "Test admin pages on remote host");
        GUIMediator.left().addTab("File", GUITools.FILE_SERVER_ICON, fileManager, "Read files from remote host");
        GUIMediator.left().addTab("Web shell", GUITools.SHELL_SERVER_ICON, shellManager, "<html>Create a web shell to remote host ; open a terminal<br><i>Allows OS commands like ipconfig/ifconfig</i></html>");
        GUIMediator.left().addTab("SQL shell", GUITools.SHELL_SERVER_ICON, sqlShellManager, "<html>Create a SQL shell to remote host ; open a terminal<br><i>Allows SQL commands like update/grant</i></html>");
        GUIMediator.left().addTab("Upload", GUITools.UPLOAD_ICON, uploadManager, "Upload a file to host");
        GUIMediator.left().addTab("Brute force", GUITools.BRUTER_ICON, new BruteForceManager(), "Brute force hashes");
        GUIMediator.left().addTab("Coder", GUITools.CODER_ICON, new CoderManager(), "Encode or decode a string");
        
        GUIMediator.right().setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Tree and tabs on top
        final JSplitPaneWithZeroSizeDivider leftRight = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        leftRight.setLeftComponent(GUIMediator.left());
        leftRight.setRightComponent( GUIMediator.right() );
        leftRight.setDividerLocation(verticalSplitter);
        leftRight.setDividerSize(0);
        leftRight.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER));
        
        this.setDividerSize(0);
        this.setBorder(null);
        
        GUIMediator.register(new BottomTabbedPaneAdapter());
        GUIMediator.bottom().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
        
        GUIMediator.gui().network = new JSplitPaneWithZeroSizeDivider();
        GUIMediator.gui().network.setResizeWeight(1);
        GUIMediator.gui().network.setDividerSize(0);
        GUIMediator.gui().network.setDividerLocation(600);
        GUIMediator.gui().network.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, GUITools.COMPONENT_BORDER));
        networkTable = new JTable(0,4){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        networkTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        networkTable.setRowSelectionAllowed(true);
        networkTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        networkTable.setRowHeight(20);
        networkTable.setGridColor(Color.LIGHT_GRAY);
        networkTable.getTableHeader().setReorderingAllowed(false);
        
        networkTable.addMouseListener( new MouseAdapter(){
            public void mousePressed( MouseEvent e ){
                networkTable.requestFocusInWindow();
                if ( SwingUtilities.isRightMouseButton( e ) ){ // move selected row and place cursor on focused cell
                    Point p = e.getPoint();
                    
                    // get the row index that contains that coordinate
                    int rowNumber = networkTable.rowAtPoint( p );
                    int colNumber = networkTable.columnAtPoint( p );
                    // Get the ListSelectionModel of the JTable
                    DefaultListSelectionModel  model = (DefaultListSelectionModel) networkTable.getSelectionModel();
                    DefaultListSelectionModel  model2 = (DefaultListSelectionModel) networkTable.getColumnModel().getSelectionModel();
                    
                    networkTable.setRowSelectionInterval(rowNumber, rowNumber);
                    model.moveLeadSelectionIndex(rowNumber);
                    model2.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        networkTable.setModel(new DefaultTableModel() { 
        	String[] columns = {"Method", "Url", "Size", "Type"}; 
        	
        	@Override 
        	public int getColumnCount() { 
        		return columns.length; 
        	} 
        	
        	@Override 
        	public String getColumnName(int index) { 
        		return columns[index]; 
        	} 
        });
        
        class CenterRenderer extends DefaultTableCellRenderer{
            public CenterRenderer(){
                this.setHorizontalAlignment(JLabel.CENTER);
            }
        }
        
        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        networkTable.getColumnModel().getColumn(2).setCellRenderer(centerHorizontalAlignment);
        networkTable.getColumnModel().getColumn(3).setCellRenderer(centerHorizontalAlignment);

        networkTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null);
        networkTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), null);
        
        Set<AWTKeyStroke> forward = new HashSet<AWTKeyStroke>(networkTable.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        networkTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        Set<AWTKeyStroke> backward = new HashSet<AWTKeyStroke>(networkTable.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        networkTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
        
        final TableCellRenderer tcrOs = networkTable.getTableHeader().getDefaultRenderer();
        networkTable.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
                return lbl;
            }
        });
        GUIMediator.gui().network.setLeftComponent(new JScrollPane(networkTable){
        	@Override
        	public void setBorder(Border border) {
        	}
        });
        MouseTabbedPane networkDetailTabs = new MouseTabbedPane();
        networkDetailTabs.addTab("Headers", new JScrollPanePixelBorder(1,0,0,0,new JPanel()));
        networkDetailTabs.addTab("Cookies", new JScrollPanePixelBorder(1,0,0,0,new JPanel()));
        networkDetailTabs.addTab("Params", new JScrollPanePixelBorder(1,0,0,0,new JPanel()));
        networkDetailTabs.addTab("Response", new JScrollPanePixelBorder(1,0,0,0,new JPanel()));
        networkDetailTabs.addTab("Timing", new JScrollPanePixelBorder(1,0,0,0,new JPanel()));
        networkDetailTabs.addTab("Preview", new JScrollPanePixelBorder(1,0,0,0,new JPanel()));
        
        networkTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
            	if (! event.getValueIsAdjusting()){ // prevent double event
            		System.out.println(listHTTPHeader.get(networkTable.getSelectedRow()).url);
            	}
            }
        });
        
        GUIMediator.gui().network.setRightComponent(networkDetailTabs);
        GUIMediator.bottom().setMinimumSize(new Dimension());
        
        GUIMediator.bottom().addTab("Console", new ImageIcon(getClass().getResource("/com/jsql/view/images/console.gif")), new JScrollPanePixelBorder(1,1,0,0,GUIMediator.gui().consoleArea), "General information");
        
        // Order is important
        if (prefs.getBoolean(GUITools.JAVA_VISIBLE, false))
        	GUIMediator.bottom().insertJavaDebugTab();
        if (prefs.getBoolean(GUITools.NETWORK_VISIBLE, true))
        	GUIMediator.bottom().insertNetworkTab();
        if (prefs.getBoolean(GUITools.CHUNK_VISIBLE, true))
	        GUIMediator.bottom().insertChunkTab();
        if (prefs.getBoolean(GUITools.BINARY_VISIBLE, true))
        	GUIMediator.bottom().insertBinaryTab();
        
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
        arrowUpPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER));
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
        arrowDownPanel.setPreferredSize(new Dimension(17,27));
        arrowDownPanel.setMaximumSize(new Dimension(17,27));
        arrowDownPanel.add(showBottomButton);
        bottomPanel.add( arrowDownPanel );
        bottomPanel.add(GUIMediator.bottom());
        
        // Do Overlay
        arrowDownPanel.setAlignmentX(1.0f);
        arrowDownPanel.setAlignmentY(0.0f);
        GUIMediator.bottom().setAlignmentX(1.0f);
        GUIMediator.bottom().setAlignmentY(0.0f);
        
        this.setBottomComponent(bottomPanel);
        this.setDividerLocation(596 - horizontalSplitter);
        
        this.setResizeWeight(1); // defines left and bottom pane
        
        GUIMediator.gui().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(LeftRightBottomPanel.this.VERTICALSPLITTER_PREFNAME, leftRight.getDividerLocation());
                prefs.putInt(LeftRightBottomPanel.this.HORIZONTALSPLITTER_PREFNAME, LeftRightBottomPanel.this.getHeight() - LeftRightBottomPanel.this.getDividerLocation());
                
                prefs.putBoolean(GUITools.BINARY_VISIBLE, false);
                prefs.putBoolean(GUITools.CHUNK_VISIBLE, false);
                prefs.putBoolean(GUITools.NETWORK_VISIBLE, false);
                prefs.putBoolean(GUITools.JAVA_VISIBLE, false);
                
                for(int i=0; i < GUIMediator.bottom().getTabCount() ;i++){
                    if (GUIMediator.bottom().getTitleAt(i).equals("Binary")) {
                        prefs.putBoolean(GUITools.BINARY_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Chunk")) {
                        prefs.putBoolean(GUITools.CHUNK_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Network")) {
                        prefs.putBoolean(GUITools.NETWORK_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Java")) {
                        prefs.putBoolean(GUITools.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        GUIMediator.gui().chunks.setLineWrap(true);
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

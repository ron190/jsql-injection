package com.jsql.view.panel;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.HTTPHeader;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.SwingAppender;
import com.jsql.view.console.DefaultConsoleAdapter;
import com.jsql.view.console.JavaConsoleAdapter;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.tab.BottomTabbedPaneAdapter;
import com.jsql.view.tab.MouseTabbedPane;
import com.jsql.view.textcomponent.JPopupTextArea;

@SuppressWarnings("serial")
public class BottomPanel extends JPanel {
	public DefaultConsoleAdapter consoleArea;
	public JTextArea chunks;
	public JSplitPaneWithZeroSizeDivider network;
	public JTextArea binaryArea;
	public JavaConsoleAdapter javaDebug;

	public SwingAppender logAppender = new SwingAppender();

    public List<HTTPHeader> listHTTPHeader = new ArrayList<HTTPHeader>();
    public JTable networkTable;

	public BottomPanel(){        
        // Object creation after customization
        consoleArea = new DefaultConsoleAdapter("Console");
        logAppender.register(consoleArea);
        
        chunks = new JPopupTextArea().getProxy();
        chunks.setEditable(false);
        binaryArea = new JPopupTextArea().getProxy();
        binaryArea.setEditable(false);
        javaDebug = new JavaConsoleAdapter("Java");
        logAppender.register(javaDebug);

        network = new JSplitPaneWithZeroSizeDivider();
        network.setResizeWeight(1);
        network.setDividerSize(0);
        network.setDividerLocation(600);
        network.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, GUITools.COMPONENT_BORDER));
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
        network.setLeftComponent(new JScrollPane(networkTable){
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
        
        network.setRightComponent(networkDetailTabs);
        
        GUIMediator.register(new BottomTabbedPaneAdapter());
        GUIMediator.bottom().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
        GUIMediator.bottom().setMinimumSize(new Dimension());

        GUIMediator.bottom().addTab("Console", new ImageIcon(getClass().getResource("/com/jsql/view/images/console.gif")), new JScrollPanePixelBorder(1,1,0,0,consoleArea), "General information");
        GUIMediator.bottom().setTabComponentAt(GUIMediator.bottom().indexOfTab("Console"), new JLabel("Console",
				new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), SwingConstants.CENTER));

        // Order is important
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        if (prefs.getBoolean(GUITools.JAVA_VISIBLE, false)) {
        	this.insertJavaDebugTab();
		}
        if (prefs.getBoolean(GUITools.NETWORK_VISIBLE, true)) {
        	this.insertNetworkTab();
		}
        if (prefs.getBoolean(GUITools.CHUNK_VISIBLE, true)) {
        	this.insertChunkTab();
		}
        if (prefs.getBoolean(GUITools.BINARY_VISIBLE, true)) {
        	this.insertBinaryTab();
		}
        
        GUIMediator.bottom().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JTabbedPane tabs = GUIMediator.bottom();
				if(tabs.getSelectedIndex() > -1){
					Component currentTabHeader = tabs.getTabComponentAt(tabs.getSelectedIndex());
					if(currentTabHeader != null)
						currentTabHeader.setFont(currentTabHeader.getFont().deriveFont(Font.PLAIN));
				}
			}
		});
        
        this.setLayout( new OverlayLayout(this) );
        
        BasicArrowButton showBottomButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        showBottomButton.setBorderPainted(false);
        showBottomButton.setPreferredSize(showBottomButton.getPreferredSize());
        showBottomButton.setMaximumSize(showBottomButton.getPreferredSize());
        
        showBottomButton.addMouseListener(LeftRightBottomPanel.hideShowAction);
        
        JPanel arrowDownPanel = new JPanel();
        arrowDownPanel.setLayout( new BoxLayout(arrowDownPanel, BoxLayout.PAGE_AXIS) );
        arrowDownPanel.setOpaque(false);
        showBottomButton.setOpaque(false);
        arrowDownPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0)); // Disable overlap with zerosizesplitter
        arrowDownPanel.setPreferredSize(new Dimension(17,27));
        arrowDownPanel.setMaximumSize(new Dimension(17,27));
        arrowDownPanel.add(showBottomButton);
        this.add( arrowDownPanel );
        this.add(GUIMediator.bottom());
        
        // Do Overlay
        arrowDownPanel.setAlignmentX(1.0f);
        arrowDownPanel.setAlignmentY(0.0f);
        GUIMediator.bottom().setAlignmentX(1.0f);
        GUIMediator.bottom().setAlignmentY(0.0f);
        
        chunks.setLineWrap(true);
        binaryArea.setLineWrap(true);
//        GUIMediator.gui().consoleArea.setLineWrap(true);
	}
	
	public void insertChunkTab(){
		GUIMediator.bottom().insertTab(
    		"Chunk", 
    		new ImageIcon(BottomPanel.class.getResource("/com/jsql/view/images/chunk.gif")), 
    		new JScrollPanePixelBorder(1,1,0,0,BottomPanel.this.chunks), 
    		"Hexadecimal data recovered",
    		1
		);
		
        GUIMediator.bottom().setTabComponentAt(GUIMediator.bottom().indexOfTab("Chunk"), new JLabel("Chunk",
				new ImageIcon(BottomTabbedPaneAdapter.class.getResource("/com/jsql/view/images/chunk.gif")), SwingConstants.CENTER));
	}
	
	public void insertBinaryTab(){
		GUIMediator.bottom().insertTab(
			"Binary", 
			new ImageIcon(BottomPanel.class.getResource("/com/jsql/view/images/binary.gif")), 
			new JScrollPanePixelBorder(1,1,0,0,BottomPanel.this.binaryArea), 
			"Time/Blind bytes", 
			1 + (GUIMediator.menubar().chunk.isSelected() ? 1 : 0)
		);
		
        GUIMediator.bottom().setTabComponentAt(GUIMediator.bottom().indexOfTab("Binary"), new JLabel("Binary",
				new ImageIcon(BottomTabbedPaneAdapter.class.getResource("/com/jsql/view/images/binary.gif")), SwingConstants.CENTER));
	}
	
	public void insertNetworkTab(){
		GUIMediator.bottom().insertTab(
    		"Network", 
    		new ImageIcon(BottomPanel.class.getResource("/com/jsql/view/images/header.gif")), 
    		BottomPanel.this.network, 
    		"URL calls information", 
    		GUIMediator.bottom().getTabCount() - (GUIMediator.menubar().javaDebug.isSelected() ? 1 : 0) 
		);
		
        GUIMediator.bottom().setTabComponentAt(GUIMediator.bottom().indexOfTab("Network"), new JLabel("Network",
				new ImageIcon(BottomTabbedPaneAdapter.class.getResource("/com/jsql/view/images/header.gif")), SwingConstants.CENTER));
	}
	
	public void insertJavaDebugTab(){
		GUIMediator.bottom().insertTab(
    		"Java", 
    		new ImageIcon(BottomPanel.class.getResource("/com/jsql/view/images/cup.png")), 
    		new JScrollPanePixelBorder(1,1,0,0,BottomPanel.this.javaDebug), 
    		"Java console", 
    		GUIMediator.bottom().getTabCount()
		);
		
        GUIMediator.bottom().setTabComponentAt(GUIMediator.bottom().indexOfTab("Java"), new JLabel("Java",
				new ImageIcon(BottomTabbedPaneAdapter.class.getResource("/com/jsql/view/images/cup.png")), SwingConstants.CENTER));
	}
}

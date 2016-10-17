/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.panel;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultCaret;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.HttpHeader;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.console.JavaConsoleAdapter;
import com.jsql.view.swing.console.SimpleConsoleAdapter;
import com.jsql.view.swing.console.SwingAppender;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.tab.MouseTabbedPane;
import com.jsql.view.swing.tab.TabConsoles;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextPanePlaceholder;

/**
 * A panel with different consoles displayed on the bottom.
 */
@SuppressWarnings("serial")
public class PanelConsoles extends JPanel {
    /**
     * Console for default application messages.
     */
    public SimpleConsoleAdapter consoleTab = new SimpleConsoleAdapter("Console", "You should not see that!!");

    /**
     * Console for java exception messages.
     */
    public JavaConsoleAdapter javaTab = new JavaConsoleAdapter("Java", "Java unhandled exception");
    
    /**
     * Console for raw SQL results.
     */
    public JTextArea chunkTab;

    /**
     * Panel displaying table of HTTP requests and responses.
     */
    public JSplitPaneWithZeroSizeDivider network;

    /**
     * Console for binary representation of characters found with blind/time injection.
     */
    public JTextArea binaryTab;

    /**
     * List of HTTP injection requests and responses.
     */
    private List<HttpHeader> listHttpHeader = new ArrayList<>();

    /**
     * Table in Network tab displaying HTTP requests.
     */
    public JTable networkTable;

    public JTextArea networkTabResponse = new JPopupTextArea("Header server response").getProxy();
    public JTextArea networkTabSource = new JPopupTextArea("Raw page source").getProxy();
    public JTextPane networkTabPreview = new JTextPanePlaceholder("Web browser rendering");
    public JTextArea networkTabHeader = new JPopupTextArea("Header client request").getProxy();
    public JTextArea networkTabParam = new JPopupTextArea("HTTP POST parameters").getProxy();
    public JTextArea networkTabTiming = new JPopupTextArea("Response time duration").getProxy();
    
    /**
     * Create panel at the bottom with differents consoles to report injection process.
     */
    public PanelConsoles() {
        // Object creation after customization
        this.consoleTab.getProxy().setEditable(false);
        SwingAppender.register(this.consoleTab);
        
        this.chunkTab = new JPopupTextArea("Raw data extracted during injection").getProxy();
        this.chunkTab.setEditable(false);
        
        this.binaryTab = new JPopupTextArea("Characters extracted during blind or time based injection").getProxy();
        this.binaryTab.setEditable(false);
        
        this.javaTab.getProxy().setEditable(false);
        SwingAppender.register(this.javaTab);
        
        this.network = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT);
        this.network.setResizeWeight(1);
        this.network.setDividerSize(0);
        this.network.setDividerLocation(600);
        this.network.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        this.networkTable = new JTable(0, 4) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        this.networkTable.setComponentPopupMenu(new JPopupMenuTable(this.networkTable));
        this.networkTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.networkTable.setRowSelectionAllowed(true);
        this.networkTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.networkTable.setRowHeight(20);
        this.networkTable.setGridColor(Color.LIGHT_GRAY);
        this.networkTable.getTableHeader().setReorderingAllowed(false);
        
        this.networkTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                PanelConsoles.this.networkTable.requestFocusInWindow();
                // move selected row and place cursor on focused cell
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = e.getPoint();

                    // get the row index that contains that coordinate
                    int rowNumber = PanelConsoles.this.networkTable.rowAtPoint(p);
                    int colNumber = PanelConsoles.this.networkTable.columnAtPoint(p);
                    // Get the ListSelectionModel of the JTable
                    DefaultListSelectionModel  model = (DefaultListSelectionModel) PanelConsoles.this.networkTable.getSelectionModel();
                    DefaultListSelectionModel  model2 = (DefaultListSelectionModel) PanelConsoles.this.networkTable.getColumnModel().getSelectionModel();

                    PanelConsoles.this.networkTable.setRowSelectionInterval(rowNumber, rowNumber);
                    model.moveLeadSelectionIndex(rowNumber);
                    model2.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        this.networkTable.setModel(new DefaultTableModel() {
            private String[] columns = {
                I18n.valueByKey("NETWORK_TAB_METHOD_COLUMN"), 
                I18n.valueByKey("NETWORK_TAB_URL_COLUMN"), 
                I18n.valueByKey("NETWORK_TAB_SIZE_COLUMN"), 
                I18n.valueByKey("NETWORK_TAB_TYPE_COLUMN")
            };

            @Override
            public int getColumnCount() {
                return this.columns.length;
            } 

            @Override
            public String getColumnName(int index) {
                return this.columns[index];
            }
        });

        class PathCellRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
            ) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setToolTipText(
                    "<html><div style=\"font-size:10px;font-family:'Ubuntu Mono'\">"
                    + c.getText().replaceAll("(.{100})(?!$)", "$1<br>")
                    + "</div></html>"
                );
                
                return c;
            }
        }
        
        this.networkTable.getColumnModel().getColumn(1).setCellRenderer(new PathCellRenderer());
        
        class CenterRenderer extends DefaultTableCellRenderer {
            public CenterRenderer() {
                this.setHorizontalAlignment(JLabel.CENTER);
            }
        }

        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        this.networkTable.getColumnModel().getColumn(2).setCellRenderer(centerHorizontalAlignment);
        this.networkTable.getColumnModel().getColumn(3).setCellRenderer(centerHorizontalAlignment);
        
        this.networkTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null);
        this.networkTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), null);
        
        Set<AWTKeyStroke> forward = new HashSet<>(this.networkTable.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        this.networkTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        Set<AWTKeyStroke> backward = new HashSet<>(this.networkTable.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        this.networkTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
        
        final TableCellRenderer tcrOs = this.networkTable.getTableHeader().getDefaultRenderer();
        this.networkTable.getTableHeader().setDefaultRenderer(
            new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
                ) {
                    JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    lbl.setBorder(
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY), 
                            BorderFactory.createEmptyBorder(0, 5, 0, 5)
                        )
                    );
                    return lbl;
                }
            }
        );
        
        JScrollIndicator scroller = new JScrollIndicator(this.networkTable);
        scroller.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        scroller.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        this.network.setLeftComponent(scroller);
        
        MouseTabbedPane networkDetailTabs = new MouseTabbedPane();
        networkDetailTabs.addTab(I18n.valueByKey("NETWORK_TAB_RESPONSE_LABEL"), new LightScrollPane(1, 1, 0, 0, networkTabResponse));
        networkDetailTabs.addTab(I18n.valueByKey("NETWORK_TAB_SOURCE_LABEL"), new LightScrollPane(1, 1, 0, 0, networkTabSource));
        networkDetailTabs.addTab(I18n.valueByKey("NETWORK_TAB_PREVIEW_LABEL"), new LightScrollPane(1, 1, 0, 0, networkTabPreview));
        networkDetailTabs.addTab(I18n.valueByKey("NETWORK_TAB_HEADERS_LABEL"), new LightScrollPane(1, 1, 0, 0, networkTabHeader));
        networkDetailTabs.addTab(I18n.valueByKey("NETWORK_TAB_PARAMS_LABEL"), new LightScrollPane(1, 1, 0, 0, networkTabParam));
        networkDetailTabs.addTab(I18n.valueByKey("NETWORK_TAB_TIMING_LABEL"), new LightScrollPane(1, 1, 0, 0, networkTabTiming));
        
        DefaultCaret caret = (DefaultCaret) networkTabResponse.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabSource.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabPreview.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabHeader.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabParam.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabTiming.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        networkTabHeader.setLineWrap(true);
        networkTabParam.setLineWrap(true);
        networkTabResponse.setLineWrap(true);
        networkTabTiming.setLineWrap(true);
        networkTabSource.setLineWrap(true);
        
        networkTabPreview.setContentType("text/html");
        networkTabPreview.setEditable(false);
        
        this.networkTable.getColumnModel().getColumn(1).setPreferredWidth(500);
        
        this.networkTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // prevent double event
                if (!event.getValueIsAdjusting() && PanelConsoles.this.networkTable.getSelectedRow() > -1) {
                    HttpHeader networkData = listHttpHeader.get(PanelConsoles.this.networkTable.getSelectedRow());
                    networkTabHeader.setText(networkData.getHeader());
                    networkTabParam.setText(networkData.getPost());
                    
                    networkTabResponse.setText("");
                    for(String key: networkData.getResponse().keySet()) {
                        networkTabResponse.append(key + ": " + networkData.getResponse().get(key));
                        networkTabResponse.append("\n");
                    }
                    
                    networkTabTiming.setText("?");
                    networkTabSource.setText(networkData.getSource());
                    networkTabPreview.setText(
                        "<html>" 
                        + networkData.getSource()
                        + "</html>"
                    );
                    // ^^^^ Report EmptyStackException #1551 
                    // To avoid this, create a new document, getEditorKit().createDefaultDocument(), and replace the existing Document with the new one.
                }
            }
        });

        this.network.setRightComponent(networkDetailTabs);

        MediatorGui.register(new TabConsoles());
        MediatorGui.tabConsoles().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));

        MediatorGui.tabConsoles().addTab(
            "Console",
            HelperUi.ICON_CONSOLE,
            new LightScrollPane(1, 1, 0, 0, this.consoleTab.getProxy()),
            I18n.valueByKey("CONSOLE_MAIN_TOOLTIP")
        );
        JLabel labelConsole = new JLabel(I18n.valueByKey("CONSOLE_MAIN_LABEL"), HelperUi.ICON_CONSOLE, SwingConstants.CENTER);
        MediatorGui.tabConsoles().setTabComponentAt(
            MediatorGui.tabConsoles().indexOfTab("Console"),
            labelConsole
        );
        I18n.addComponentForKey("CONSOLE_MAIN_LABEL", labelConsole);

        // Order is important
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        if (prefs.getBoolean(HelperUi.JAVA_VISIBLE, false)) {
            this.insertJavaDebugTab();
        }
        if (prefs.getBoolean(HelperUi.NETWORK_VISIBLE, true)) {
            this.insertNetworkTab();
        }
        if (prefs.getBoolean(HelperUi.CHUNK_VISIBLE, true)) {
            this.insertChunkTab();
        }
        if (prefs.getBoolean(HelperUi.BINARY_VISIBLE, true)) {
            this.insertBinaryTab();
        }

        MediatorGui.tabConsoles().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane tabs = MediatorGui.tabConsoles();
                if (tabs.getSelectedIndex() > -1) {
                    Component currentTabHeader = tabs.getTabComponentAt(tabs.getSelectedIndex());
                    if (currentTabHeader != null) {
                        currentTabHeader.setFont(currentTabHeader.getFont().deriveFont(Font.PLAIN));
                    }
                }
            }
        });

        this.setLayout(new OverlayLayout(this));

        BasicArrowButton showBottomButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        showBottomButton.setBorderPainted(false);
        showBottomButton.setPreferredSize(showBottomButton.getPreferredSize());
        showBottomButton.setMaximumSize(showBottomButton.getPreferredSize());

        showBottomButton.addActionListener(SplitHorizontalTopBottom.ACTION_HIDE_SHOW_CONSOLE);

        JPanel arrowDownPanel = new JPanel();
//        arrowDownPanel.setLayout(new BoxLayout(arrowDownPanel, BoxLayout.PAGE_AXIS));
        arrowDownPanel.setLayout(new BorderLayout());
        arrowDownPanel.setOpaque(false);
        showBottomButton.setOpaque(false);
        // Disable overlap with zerosizesplitter
        arrowDownPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        arrowDownPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 27));
        arrowDownPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
//        arrowDownPanel.add(Box.createHorizontalGlue());
        arrowDownPanel.add(showBottomButton, BorderLayout.LINE_END);
        this.add(arrowDownPanel);
        this.add(MediatorGui.tabConsoles());

        // Do Overlay
        arrowDownPanel.setAlignmentX(FlowLayout.TRAILING);
        arrowDownPanel.setAlignmentY(JTextArea.TOP_ALIGNMENT);
        MediatorGui.tabConsoles().setAlignmentX(FlowLayout.LEADING);
        MediatorGui.tabConsoles().setAlignmentY(JTextArea.TOP_ALIGNMENT);

        this.chunkTab.setLineWrap(true);
        this.binaryTab.setLineWrap(true);
    }

    /**
     * Add Chunk console to bottom panel.
     */
    public void insertChunkTab() {
        MediatorGui.tabConsoles().insertTab(
            "Chunk",
            HelperUi.ICON_CHUNK,
            new LightScrollPane(1, 1, 0, 0, PanelConsoles.this.chunkTab),
            I18n.valueByKey("CONSOLE_CHUNK_TOOLTIP"),
            1
        );

        JLabel labelTimebased = new JLabel(I18n.valueByKey("CONSOLE_CHUNK_LABEL"), HelperUi.ICON_CHUNK, SwingConstants.CENTER);
        MediatorGui.tabConsoles().setTabComponentAt(
            MediatorGui.tabConsoles().indexOfTab("Chunk"),
            labelTimebased
        );
        I18n.addComponentForKey("CONSOLE_CHUNK_LABEL", labelTimebased);
    }

    /**
     * Add Binary console to bottom panel.
     */
    public void insertBinaryTab() {
        MediatorGui.tabConsoles().insertTab(
            "Binary",
            HelperUi.ICON_BINARY,
            new LightScrollPane(1, 1, 0, 0, PanelConsoles.this.binaryTab),
            I18n.valueByKey("CONSOLE_BINARY_TOOLTIP"),
            1 + (MediatorGui.menubar().chunkMenu.isSelected() ? 1 : 0)
        );

        JLabel labelBinary = new JLabel(I18n.valueByKey("CONSOLE_BINARY_LABEL"), HelperUi.ICON_BINARY, SwingConstants.CENTER);
        MediatorGui.tabConsoles().setTabComponentAt(
            MediatorGui.tabConsoles().indexOfTab("Binary"), 
            labelBinary
        );
        I18n.addComponentForKey("CONSOLE_BINARY_LABEL", labelBinary);
    }

    /**
     * Add Network tab to bottom panel.
     */
    public void insertNetworkTab() {
        MediatorGui.tabConsoles().insertTab(
            "Network",
            HelperUi.ICON_HEADER,
            PanelConsoles.this.network,
            I18n.valueByKey("CONSOLE_NETWORK_TOOLTIP"),
            MediatorGui.tabConsoles().getTabCount() - (MediatorGui.menubar().javaDebugMenu.isSelected() ? 1 : 0)
        );

        JLabel labelNetwork = new JLabel(I18n.valueByKey("CONSOLE_NETWORK_LABEL"), HelperUi.ICON_HEADER, SwingConstants.CENTER);
        MediatorGui.tabConsoles().setTabComponentAt(
            MediatorGui.tabConsoles().indexOfTab("Network"), 
            labelNetwork
        );
        I18n.addComponentForKey("CONSOLE_NETWORK_LABEL", labelNetwork);
    }

    /**
     * Add Java console to bottom panel.
     */
    public void insertJavaDebugTab() {
        MediatorGui.tabConsoles().insertTab(
            "Java",
            HelperUi.ICON_CUP,
            new LightScrollPane(1, 1, 0, 0, PanelConsoles.this.javaTab.getProxy()),
            I18n.valueByKey("CONSOLE_JAVA_TOOLTIP"),
            MediatorGui.tabConsoles().getTabCount()
        );

        JLabel labelJava = new JLabel(I18n.valueByKey("CONSOLE_JAVA_LABEL"), HelperUi.ICON_CUP, SwingConstants.CENTER);
        MediatorGui.tabConsoles().setTabComponentAt(
            MediatorGui.tabConsoles().indexOfTab("Java"), 
            labelJava
        );
        I18n.addComponentForKey("CONSOLE_JAVA_LABEL", labelJava);
    }
    
    public void addHeader(HttpHeader header) {
        this.listHttpHeader.add(header);
    }
    
    public void reset() {
        this.listHttpHeader.clear();
    }
}

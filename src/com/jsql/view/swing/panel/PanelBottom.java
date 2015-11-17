/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
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
import javax.swing.text.DefaultCaret;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

import com.jsql.i18n.I18n;
import com.jsql.model.bean.HTTPHeader;
import com.jsql.model.injection.InjectionModel;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.SwingAppender;
import com.jsql.view.swing.console.JavaConsoleAdapter;
import com.jsql.view.swing.console.SimpleConsoleAdapter;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.tab.AdapterBottomTabbedPane;
import com.jsql.view.swing.tab.MouseTabbedPane;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * A panel with different consoles displayed on the bottom.
 */
@SuppressWarnings("serial")
public class PanelBottom extends JPanel {
    /**
     * Log4j appender to displays log message to consoles.
     */
    private SwingAppender logAppender = new SwingAppender();
    
    /**
     * Console for default application messages.
     */
    public SimpleConsoleAdapter consoleTab = new SimpleConsoleAdapter("Console");

    /**
     * Console for java exception messages.
     */
    public JavaConsoleAdapter javaTab = new JavaConsoleAdapter("Java");
    
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
    public List<HTTPHeader> listHTTPHeader = new ArrayList<HTTPHeader>();

    /**
     * Table in Network tab displaying HTTP requests.
     */
    public JTable networkTable;

    JTextArea networkTabHeader = new JPopupTextArea().getProxy();
    JTextArea networkTabCookie = new JPopupTextArea().getProxy();
    JTextArea networkTabParam = new JPopupTextArea().getProxy();
    JTextArea networkTabResponse = new JPopupTextArea().getProxy();
    JTextArea networkTabTiming = new JPopupTextArea().getProxy();
    JTextArea networkTabSource = new JPopupTextArea().getProxy();
    JTextPane networkTabPreview = new JTextPane();
    
    /**
     * Create panel at the bottom with differents consoles to report injection process.
     */
    public PanelBottom() {
        // Object creation after customization
//        this.consoleArea = new SimpleConsoleAdapter("Console");
        this.consoleTab.getProxy().setEditable(false);
        this.logAppender.register(this.consoleTab);
//        SwingAppender.register(this.consoleArea);
        
        this.chunkTab = new JPopupTextArea().getProxy();
        this.chunkTab.setEditable(false);
//        this.chunkTab.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
        
        this.binaryTab = new JPopupTextArea().getProxy();
        this.binaryTab.setEditable(false);
//        this.binaryTab.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
        
//        this.javaDebug = new JavaConsoleAdapter("Java");
        this.javaTab.getProxy().setEditable(false);
        this.logAppender.register(this.javaTab);
        
        this.network = new JSplitPaneWithZeroSizeDivider();
        this.network.setResizeWeight(1);
        this.network.setDividerSize(0);
        this.network.setDividerLocation(600);
        this.network.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, HelperGUI.COMPONENT_BORDER));
        this.networkTable = new JTable(0, 4) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        new TableFilterHeader(this.networkTable, AutoChoices.ENABLED);

        this.networkTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        this.networkTable.setRowSelectionAllowed(true);
        this.networkTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.networkTable.setRowHeight(20);
        this.networkTable.setGridColor(Color.LIGHT_GRAY);
        this.networkTable.getTableHeader().setReorderingAllowed(false);
        
        this.networkTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                PanelBottom.this.networkTable.requestFocusInWindow();
                // move selected row and place cursor on focused cell
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = e.getPoint();

                    // get the row index that contains that coordinate
                    int rowNumber = PanelBottom.this.networkTable.rowAtPoint(p);
                    int colNumber = PanelBottom.this.networkTable.columnAtPoint(p);
                    // Get the ListSelectionModel of the JTable
                    DefaultListSelectionModel  model = (DefaultListSelectionModel) PanelBottom.this.networkTable.getSelectionModel();
                    DefaultListSelectionModel  model2 = (DefaultListSelectionModel) PanelBottom.this.networkTable.getColumnModel().getSelectionModel();

                    PanelBottom.this.networkTable.setRowSelectionInterval(rowNumber, rowNumber);
                    model.moveLeadSelectionIndex(rowNumber);
                    model2.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        this.networkTable.setModel(new DefaultTableModel() {
            private String[] columns = {
                I18n.NETWORK_TAB_METHOD_COLUMN, 
                I18n.NETWORK_TAB_URL_COLUMN, 
                I18n.NETWORK_TAB_SIZE_COLUMN, 
                I18n.NETWORK_TAB_TYPE_COLUMN
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
        
        Set<AWTKeyStroke> forward = new HashSet<AWTKeyStroke>(this.networkTable.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        this.networkTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        Set<AWTKeyStroke> backward = new HashSet<AWTKeyStroke>(this.networkTable.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        this.networkTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
        
        final TableCellRenderer tcrOs = this.networkTable.getTableHeader().getDefaultRenderer();
        this.networkTable.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
                return lbl;
            }
        });
//        this.network.setLeftComponent(new JScrollPane(this.networkTable) {
        
        JScrollIndicator j = new JScrollIndicator(this.networkTable);
        j.scrollPane.setBorder(BorderFactory.createEmptyBorder(0,0,-1,-1));
        j.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0,0,-1,-1));
        this.network.setLeftComponent(j 
//        this.network.setLeftComponent(new JScrollIndicator(this.networkTable) {
//            @Override
//            public void setBorder(Border border) {
//                // Do nothing
//            }
        );
//        });
        
        MouseTabbedPane networkDetailTabs = new MouseTabbedPane();
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_RESPONSE_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabResponse));
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_SOURCE_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabSource));
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_PREVIEW_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabPreview));
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_HEADERS_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabHeader));
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_COOKIES_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabCookie));
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_PARAMS_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabParam));
//        networkDetailTabs.addTab(I18n.NETWORK_TAB_TIMING_LABEL, new JScrollPanePixelBorder(1, 1, 0, 0, networkTabTiming));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_RESPONSE_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabResponse));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_SOURCE_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabSource));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_PREVIEW_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabPreview));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_HEADERS_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabHeader));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_COOKIES_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabCookie));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_PARAMS_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabParam));
        networkDetailTabs.addTab(I18n.NETWORK_TAB_TIMING_LABEL, new LightScrollPane(1, 1, 0, 0, networkTabTiming));

        DefaultCaret caret = (DefaultCaret) networkTabResponse.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabSource.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabPreview.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabHeader.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabCookie.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabParam.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        caret = (DefaultCaret) networkTabTiming.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        networkTabHeader.setLineWrap(true);
        networkTabCookie.setLineWrap(true);
        networkTabParam.setLineWrap(true);
        networkTabResponse.setLineWrap(true);
        networkTabTiming.setLineWrap(true);
        networkTabSource.setLineWrap(true);
        
        networkTabPreview.setContentType("text/html");
        networkTabPreview.setEditable(false);
        
        this.networkTable.getColumnModel().getColumn(0).setResizable(false);
        this.networkTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        this.networkTable.getColumnModel().getColumn(0).setMinWidth(120);
        this.networkTable.getColumnModel().getColumn(0).setMaxWidth(120);

        this.networkTable.getColumnModel().getColumn(2).setResizable(false);
        this.networkTable.getColumnModel().getColumn(2).setPreferredWidth(45);
        this.networkTable.getColumnModel().getColumn(2).setMinWidth(45);
        this.networkTable.getColumnModel().getColumn(2).setMaxWidth(45);
        
        this.networkTable.getColumnModel().getColumn(3).setResizable(false);
        this.networkTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        this.networkTable.getColumnModel().getColumn(3).setMinWidth(85);
        this.networkTable.getColumnModel().getColumn(3).setMaxWidth(85);
        
        this.networkTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // prevent double event
                if (!event.getValueIsAdjusting() && PanelBottom.this.networkTable.getSelectedRow() > -1) {
                    networkTabHeader.setText(listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).getHeader());
                    networkTabCookie.setText(listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).getCookie());
                    networkTabParam.setText(listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).getPost());
                    
                    networkTabResponse.setText("");
                    for(String key: listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).getResponse().keySet()) {
                        networkTabResponse.append(key + ": " + listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).getResponse().get(key));
                        networkTabResponse.append("\n");
                    }
                    
                    networkTabTiming.setText("?");
                    networkTabSource.setText(listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).source);
                    networkTabPreview.setText("<html>" + listHTTPHeader.get(PanelBottom.this.networkTable.getSelectedRow()).source + "</html>");
                }
            }
        });

        this.network.setRightComponent(networkDetailTabs);

        MediatorGUI.register(new AdapterBottomTabbedPane());
        MediatorGUI.bottom().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
        MediatorGUI.bottom().setMinimumSize(new Dimension());

        MediatorGUI.bottom().addTab("Console",
                new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/console.gif")),
//                new JScrollPanePixelBorder(1, 1, 0, 0, this.consoleTab.getProxy()),
                new LightScrollPane(1, 1, 0, 0, this.consoleTab.getProxy()),
                I18n.CONSOLE_TAB_TOOLTIP);
        MediatorGUI.bottom().setTabComponentAt(
                MediatorGUI.bottom().indexOfTab("Console"),
                new JLabel(I18n.CONSOLE_TAB_LABEL, new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/console.gif")),
                SwingConstants.CENTER));

        // Order is important
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        if (prefs.getBoolean(HelperGUI.JAVA_VISIBLE, false)) {
            this.insertJavaDebugTab();
        }
        if (prefs.getBoolean(HelperGUI.NETWORK_VISIBLE, true)) {
            this.insertNetworkTab();
        }
        if (prefs.getBoolean(HelperGUI.CHUNK_VISIBLE, true)) {
            this.insertChunkTab();
        }
        if (prefs.getBoolean(HelperGUI.BINARY_VISIBLE, true)) {
            this.insertBinaryTab();
        }

        MediatorGUI.bottom().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane tabs = MediatorGUI.bottom();
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

        showBottomButton.addActionListener(PanelLeftRightBottom.HIDESHOWPANEL);

        JPanel arrowDownPanel = new JPanel();
        arrowDownPanel.setLayout(new BoxLayout(arrowDownPanel, BoxLayout.PAGE_AXIS));
        arrowDownPanel.setOpaque(false);
        showBottomButton.setOpaque(false);
        // Disable overlap with zerosizesplitter
        arrowDownPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        arrowDownPanel.setPreferredSize(new Dimension(17, 27));
        arrowDownPanel.setMaximumSize(new Dimension(17, 27));
        arrowDownPanel.add(showBottomButton);
        this.add(arrowDownPanel);
        this.add(MediatorGUI.bottom());

        // Do Overlay
        arrowDownPanel.setAlignmentX(1.0f);
        arrowDownPanel.setAlignmentY(0.0f);
        MediatorGUI.bottom().setAlignmentX(1.0f);
        MediatorGUI.bottom().setAlignmentY(0.0f);

        this.chunkTab.setLineWrap(true);
        this.binaryTab.setLineWrap(true);
    }

    /**
     * Add Chunk console to bottom panel.
     */
    public void insertChunkTab() {
        MediatorGUI.bottom().insertTab(
            "Chunk",
            new ImageIcon(PanelBottom.class.getResource("/com/jsql/view/swing/images/chunk.gif")),
//            new JScrollPanePixelBorder(1, 1, 0, 0, PanelBottom.this.chunkTab),
            new LightScrollPane(1, 1, 0, 0, PanelBottom.this.chunkTab),
            I18n.CHUNK_TAB_TOOLTIP,
            1
        );

        MediatorGUI.bottom().setTabComponentAt(MediatorGUI.bottom().indexOfTab("Chunk"), new JLabel(I18n.CHUNK_TAB_LABEL,
                new ImageIcon(AdapterBottomTabbedPane.class.getResource("/com/jsql/view/swing/images/chunk.gif")), SwingConstants.CENTER));
    }

    /**
     * Add Binary console to bottom panel.
     */
    public void insertBinaryTab() {
        MediatorGUI.bottom().insertTab(
            "Binary",
            new ImageIcon(PanelBottom.class.getResource("/com/jsql/view/swing/images/binary.gif")),
//            new JScrollPanePixelBorder(1, 1, 0, 0, PanelBottom.this.binaryTab),
            new LightScrollPane(1, 1, 0, 0, PanelBottom.this.binaryTab),
            I18n.BINARY_TAB_TOOLTIP,
            1 + (MediatorGUI.menubar().chunkMenu.isSelected() ? 1 : 0)
        );

        MediatorGUI.bottom().setTabComponentAt(MediatorGUI.bottom().indexOfTab("Binary"), new JLabel(I18n.BINARY_TAB_LABEL,
                new ImageIcon(AdapterBottomTabbedPane.class.getResource("/com/jsql/view/swing/images/binary.gif")), SwingConstants.CENTER));
    }

    /**
     * Add Network tab to bottom panel.
     */
    public void insertNetworkTab() {
        MediatorGUI.bottom().insertTab(
            "Network",
            new ImageIcon(PanelBottom.class.getResource("/com/jsql/view/swing/images/header.gif")),
            PanelBottom.this.network,
            I18n.NETWORK_TAB_TOOLTIP,
            MediatorGUI.bottom().getTabCount() - (MediatorGUI.menubar().javaDebugMenu.isSelected() ? 1 : 0)
        );

        MediatorGUI.bottom().setTabComponentAt(MediatorGUI.bottom().indexOfTab("Network"), new JLabel(I18n.NETWORK_TAB_LABEL,
                new ImageIcon(AdapterBottomTabbedPane.class.getResource("/com/jsql/view/swing/images/header.gif")), SwingConstants.CENTER));
    }

    /**
     * Add Java console to bottom panel.
     */
    public void insertJavaDebugTab() {
        MediatorGUI.bottom().insertTab(
            "Java",
            new ImageIcon(PanelBottom.class.getResource("/com/jsql/view/swing/images/cup.png")),
//            new JScrollPanePixelBorder(1, 1, 0, 0, PanelBottom.this.javaTab.getProxy()),
            new LightScrollPane(1, 1, 0, 0, PanelBottom.this.javaTab.getProxy()),
            I18n.JAVA_TAB_TOOLTIP,
            MediatorGUI.bottom().getTabCount()
        );

        MediatorGUI.bottom().setTabComponentAt(MediatorGUI.bottom().indexOfTab("Java"), new JLabel(I18n.JAVA_TAB_LABEL,
                new ImageIcon(AdapterBottomTabbedPane.class.getResource("/com/jsql/view/swing/images/cup.png")), SwingConstants.CENTER));
    }
}

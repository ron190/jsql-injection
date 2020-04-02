package com.jsql.view.swing.panel.consoles;

import java.awt.AWTKeyStroke;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jsql.model.bean.util.HttpHeader;
import com.jsql.util.I18nUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;

@SuppressWarnings("serial")
public class NetworkTable extends JTable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * List of HTTP injection requests and responses.
     */
    private transient List<HttpHeader> listHttpHeader = new ArrayList<>();
    
    private TabbedPaneNetworkTab tabbedPaneNetworkTab;

    public NetworkTable(TabbedPaneNetworkTab tabbedPaneNetworkTab) {
        
        super(0, 4);
        
        this.tabbedPaneNetworkTab = tabbedPaneNetworkTab;
        
        this.setComponentPopupMenu(new JPopupMenuTable(this));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setRowHeight(20);
        this.setGridColor(Color.LIGHT_GRAY);
        this.getTableHeader().setReorderingAllowed(false);
        
        this.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                NetworkTable.this.requestFocusInWindow();
                
                // move selected row and place cursor on focused cell
                if (SwingUtilities.isRightMouseButton(e)) {
                    
                    Point p = e.getPoint();

                    // get the row index that contains that coordinate
                    int rowNumber = NetworkTable.this.rowAtPoint(p);
                    int colNumber = NetworkTable.this.columnAtPoint(p);
                    
                    // TODO Not finished
                    // Get the ListSelectionModel of the JTable
                    DefaultListSelectionModel modelRow = (DefaultListSelectionModel) NetworkTable.this.getSelectionModel();
                    DefaultListSelectionModel modelColumn = (DefaultListSelectionModel) NetworkTable.this.getColumnModel().getSelectionModel();

                    NetworkTable.this.setRowSelectionInterval(rowNumber, rowNumber);
                    modelRow.moveLeadSelectionIndex(rowNumber);
                    modelColumn.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        this.setModel(new DefaultTableModel() {
            
            private String[] columns = {
                I18nUtil.valueByKey("NETWORK_TAB_METHOD_COLUMN"),
                I18nUtil.valueByKey("NETWORK_TAB_URL_COLUMN"),
                I18nUtil.valueByKey("NETWORK_TAB_SIZE_COLUMN"),
                I18nUtil.valueByKey("NETWORK_TAB_TYPE_COLUMN")
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

        this.getColumnModel().getColumn(1).setCellRenderer(new TooltipCellRenderer());

        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        this.getColumnModel().getColumn(2).setCellRenderer(centerHorizontalAlignment);
        this.getColumnModel().getColumn(3).setCellRenderer(centerHorizontalAlignment);
        
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null);
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), null);
        
        Set<AWTKeyStroke> forward = new HashSet<>(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        
        Set<AWTKeyStroke> backward = new HashSet<>(this.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
        
        final TableCellRenderer tableCellRenderer = this.getTableHeader().getDefaultRenderer();
        this.getTableHeader().setDefaultRenderer(
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
                
                JLabel label = (JLabel) tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    )
                );
                
                return label;
            }
        );
        
        this.getColumnModel().getColumn(0).setPreferredWidth(75);
        this.getColumnModel().getColumn(1).setPreferredWidth(300);
        this.getColumnModel().getColumn(2).setPreferredWidth(40);
        this.getColumnModel().getColumn(3).setPreferredWidth(50);
        
        this.getSelectionModel().addListSelectionListener(event -> {
            
            // prevent double event
            if (!event.getValueIsAdjusting() && NetworkTable.this.getSelectedRow() > -1) {
                this.changeTextNetwork();
            }
        });
    }
    
    public void changeTextNetwork() {
        
        HttpHeader networkData = this.getListHttpHeader().get(this.getSelectedRow());
        
        this.tabbedPaneNetworkTab.getTextAreaNetworkTabHeader().setText(networkData.getHeader());
        this.tabbedPaneNetworkTab.getTextAreaNetworkTabParams().setText(networkData.getPost());
        this.tabbedPaneNetworkTab.getTextAreaNetworkTabUrl().setText(networkData.getUrl());
        
        this.tabbedPaneNetworkTab.getTextAreaNetworkTabResponse().setText("");
        
        for (String key: networkData.getResponse().keySet()) {
            
            this.tabbedPaneNetworkTab.getTextAreaNetworkTabResponse().append(key + ": " + networkData.getResponse().get(key));
            this.tabbedPaneNetworkTab.getTextAreaNetworkTabResponse().append("\n");
        }
        
        // Fix #53736: ArrayIndexOutOfBoundsException on setText()
        // Fix #54573: NullPointerException on setText()
        try {
            this.tabbedPaneNetworkTab.getTextAreaNetworkTabSource().setText(
                StringUtil
                .detectUtf8(networkData.getSource())
                .replaceAll("#{5,}", "#*")
                .trim()
            );
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            LOGGER.error(e, e);
        }
        
        // Reset EditorKit to disable previous document effect
        this.tabbedPaneNetworkTab.getTextAreaNetworkTabPreview().getEditorKit().createDefaultDocument();
        
        // Proxy is used by jsoup to display <img> tags
        // Previous test for 2xx Success and 3xx Redirection was Header only,
        // now get the HTML content
        // Fix #35352: EmptyStackException on setText()
        // Fix #39841: RuntimeException on setText()
        // Fix #42523: ExceptionInInitializerError on clean()
        try {
            this.tabbedPaneNetworkTab.getTextAreaNetworkTabPreview().setText(
                Jsoup.clean(
                    "<html>"+ StringUtil.detectUtf8(networkData.getSource()).replaceAll("#{5,}", "#*") + "</html>"
                        .replaceAll("<img.*>", "")
                        .replaceAll("<input.*type=\"?hidden\"?.*>", "")
                        .replaceAll("<input.*type=\"?(submit|button)\"?.*>", "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>")
                        .replaceAll("<input.*>", "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"),
                    Whitelist.relaxed()
                        .addTags("center", "div", "span")
                        .addAttributes(":all", "style")
                )
            );
        } catch (RuntimeException | ExceptionInInitializerError e) {
            LOGGER.error(e, e);
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    public void addHeader(HttpHeader header) {
        this.listHttpHeader.add(header);
    }

    public List<HttpHeader> getListHttpHeader() {
        return this.listHttpHeader;
    }
}

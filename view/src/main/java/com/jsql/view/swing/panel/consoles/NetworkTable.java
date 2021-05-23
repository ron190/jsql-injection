package com.jsql.view.swing.panel.consoles;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
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

import com.jsql.model.bean.util.HttpHeader;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;

@SuppressWarnings("serial")
public class NetworkTable extends JTable {
    
    /**
     * List of HTTP injection requests and responses.
     */
    private transient List<HttpHeader> listHttpHeader = new ArrayList<>();

    public NetworkTable(TabbedPaneNetworkTab tabbedPaneNetworkTab) {
        
        super(0, 4);
        
        this.setName("networkTable");
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
                    
                    var point = e.getPoint();

                    // get the row index that contains that coordinate
                    var rowNumber = NetworkTable.this.rowAtPoint(point);
                    var colNumber = NetworkTable.this.columnAtPoint(point);
                    
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
                    
                I18nUtil.valueByKey("NETWORK_TAB_URL_COLUMN"),
                String
                .format(
                    "%s (KB)",
                    I18nUtil.valueByKey("NETWORK_TAB_SIZE_COLUMN")
                ),
                "Strategy",
                "Metadata"
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

        this.getColumnModel().getColumn(0).setCellRenderer(new TooltipCellRenderer());

        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        this.getColumnModel().getColumn(1).setCellRenderer(centerHorizontalAlignment);
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
        
        final var tableCellRenderer = this.getTableHeader().getDefaultRenderer();
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
        
        this.getColumnModel().getColumn(0).setPreferredWidth(300);
        this.getColumnModel().getColumn(1).setPreferredWidth(20);
        this.getColumnModel().getColumn(2).setPreferredWidth(50);
        this.getColumnModel().getColumn(3).setPreferredWidth(50);
        
        this.getSelectionModel().addListSelectionListener(event -> {
            
            // prevent double event
            if (!event.getValueIsAdjusting() && this.getSelectedRow() > -1) {
                
                var httpHeader = this.listHttpHeader.get(this.getSelectedRow());
                tabbedPaneNetworkTab.changeTextNetwork(httpHeader);
            }
        });
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public List<HttpHeader> getListHttpHeader() {
        return this.listHttpHeader;
    }
    
    public void addHeader(HttpHeader header) {
        this.listHttpHeader.add(header);
    }
}

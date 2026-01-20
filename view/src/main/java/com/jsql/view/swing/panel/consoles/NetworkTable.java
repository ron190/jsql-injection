package com.jsql.view.swing.panel.consoles;

import com.jsql.model.bean.util.Request3;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkTable extends JTable {
    
    /**
     * List of HTTP injection requests and responses.
     */
    private final transient List<Request3.MessageHeader> listHttpHeader = new ArrayList<>();

    public NetworkTable(TabbedPaneNetworkTab tabbedPaneNetworkTab) {
        super(0, 4);
        
        this.setName("networkTable");
        this.setComponentPopupMenu(new JPopupMenuTable(this));
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setRowHeight(20);
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
            private final String[] columns = {
                I18nUtil.valueByKey("NETWORK_TAB_URL_COLUMN"),
                String.format("%s (KB)", I18nUtil.valueByKey("NETWORK_TAB_SIZE_COLUMN")),
                I18nUtil.valueByKey("SQLENGINE_STRATEGY"),
                I18nUtil.valueByKey("SQLENGINE_METADATA")
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
        this.getColumnModel().getColumn(3).setCellRenderer(new CenterRendererWithIcon());
        
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null);
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), null);
        
        Set<AWTKeyStroke> forward = new HashSet<>(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));  // required to unlock focus
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);

        Set<AWTKeyStroke> backward = new HashSet<>(this.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));  // required to unlock focus
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
        
        this.getColumnModel().getColumn(0).setPreferredWidth(300);
        this.getColumnModel().getColumn(1).setPreferredWidth(20);
        this.getColumnModel().getColumn(2).setPreferredWidth(50);
        this.getColumnModel().getColumn(3).setPreferredWidth(70);
        
        this.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && this.getSelectedRow() > -1) {  // prevent double event
                var httpHeader = this.listHttpHeader.get(this.getSelectedRow());
                tabbedPaneNetworkTab.changeTextNetwork(httpHeader);
            }
        });
        tabbedPaneNetworkTab.getCheckBoxDecode().addActionListener(e -> {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().withIsUrlDecodeNetworkTab(
                tabbedPaneNetworkTab.getCheckBoxDecode().isSelected()
            ).persist();
            if (this.getSelectedRow() > -1) {  // prevent double event
                var httpHeader = this.listHttpHeader.get(this.getSelectedRow());
                tabbedPaneNetworkTab.changeTextNetwork(httpHeader);
            }
        });
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public List<Request3.MessageHeader> getListHttpHeader() {
        return this.listHttpHeader;
    }

    public void addHeader(Request3.MessageHeader header) {
        this.listHttpHeader.add(header);
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.table;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.jsql.util.StringUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.tab.ButtonClose;
import com.jsql.view.swing.text.JTextFieldPlaceholder;

/**
 * Display a table for database values. Add keyboard shortcut, mouse icon, text
 * and header formatting.
 */
@SuppressWarnings("serial")
public class PanelTable extends JPanel {
	
    /**
     * Table to display in the panel.
     */
    public JTable tableValues;
    
    public FixedColumnTable tableFixedColumn = new FixedColumnTable();

    /**
     * Create a panel containing a table to display injection values.
     * 
     * @param data Array 2D with injection table data
     * @param columnNames Names of columns from database
     */
    public PanelTable(String[][] data, String[] columnNames) {
        super(new BorderLayout());

        tableValues = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableValues.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableValues.setColumnSelectionAllowed(true);
        tableValues.setRowHeight(20);
        tableValues.setRowSelectionAllowed(true);
        tableValues.setCellSelectionEnabled(true);
        tableValues.setGridColor(Color.LIGHT_GRAY);

        final TableCellRenderer cellRendererHeader = tableValues.getTableHeader().getDefaultRenderer();
        final DefaultTableCellRenderer cellRendererDefault = new DefaultTableCellRenderer();
        tableValues.getTableHeader().setDefaultRenderer(
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            JLabel label = (JLabel) cellRendererHeader.getTableCellRendererComponent(
                table, StringUtil.detectUtf8HtmlNoWrap(" "+ value +" "), isSelected, hasFocus, row, column
            );
            label.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)
                )
            );
            return label;
        });
        
        tableValues.setDefaultRenderer(tableValues.getColumnClass(2), 
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            // Prepare cell value to be utf8 inspected
            String cellValue = value != null ? value.toString() : "";
            JLabel label = (JLabel) cellRendererDefault.getTableCellRendererComponent(
                table, StringUtil.detectUtf8HtmlNoWrap(cellValue), isSelected, hasFocus, row, column
            );
            return label;
        });

        tableValues.getTableHeader().setReorderingAllowed(false);

        tableValues.setComponentPopupMenu(new JPopupMenuTable(tableValues));

        tableValues.setDragEnabled(true);

        tableValues.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tableValues.requestFocusInWindow();

                if (SwingUtilities.isRightMouseButton(e)) {
                    /**
                     * Keep selection when multiple cells are selected,
                     * move focus only
                     */
                    Point p = e.getPoint();

                    int rowNumber = tableValues.rowAtPoint(p);
                    int colNumber = tableValues.columnAtPoint(p);

                    DefaultListSelectionModel modelRow = (DefaultListSelectionModel) tableValues
                            .getSelectionModel();
                    DefaultListSelectionModel modelColumn = (DefaultListSelectionModel) tableValues
                            .getColumnModel().getSelectionModel();

                    modelRow.moveLeadSelectionIndex(rowNumber);
                    modelColumn.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        tableValues.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), 
            null
        );
        tableValues.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), 
            null
        );

        Set<AWTKeyStroke> forward = new HashSet<>(
            tableValues.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS
            )
        );
        forward.add(KeyStroke.getKeyStroke("TAB"));
        tableValues.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        
        Set<AWTKeyStroke> backward = new HashSet<>(
            tableValues.getFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS
            )
        );
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        tableValues.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);

        AdjusterTableColumn columnAdjuster = new AdjusterTableColumn(tableValues);
        columnAdjuster.adjustColumns();

        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableValues.getModel());
        tableValues.setRowSorter(rowSorter);
        
        JScrollIndicator scroller = new JScrollIndicator(tableValues);
        scroller.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        scroller.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        
        AdjustmentListener singleItemScroll = adjustmentEvent -> {
            // The user scrolled the List (using the bar, mouse wheel or something else):
            if (adjustmentEvent.getAdjustmentType() == AdjustmentEvent.TRACK){
                adjustmentEvent.getAdjustable().setBlockIncrement(100);
                adjustmentEvent.getAdjustable().setUnitIncrement(100);
            }
        };

        scroller.scrollPane.getVerticalScrollBar().addAdjustmentListener(singleItemScroll);
        scroller.scrollPane.getHorizontalScrollBar().addAdjustmentListener(singleItemScroll);

        tableFixedColumn.fixColumnSize(2, scroller.scrollPane);
        
        this.add(scroller, BorderLayout.CENTER);
        
        final JPanel panelSearch = new JPanel(new BorderLayout());
        panelSearch.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        final JTextField textFilter = new JTextFieldPlaceholder("Find in table");
        panelSearch.add(textFilter, BorderLayout.CENTER);

        tableValues.getActionMap().put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelSearch.setVisible(true);
                textFilter.requestFocusInWindow();
            }
        });
        tableValues.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "search");

        class ActionCloseSearch extends AbstractAction {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelSearch.setVisible(false);
                tableValues.requestFocusInWindow();
            }
        }
        
        Action actionCloseSearch = new ActionCloseSearch();
        textFilter.getActionMap().put("close", actionCloseSearch);
        textFilter.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");

        textFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = textFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = textFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        JButton buttonCloseSearch = new ButtonClose();
        buttonCloseSearch.addActionListener(actionCloseSearch);
        panelSearch.add(buttonCloseSearch, BorderLayout.EAST);
        this.add(panelSearch, BorderLayout.SOUTH);
        panelSearch.setVisible(false);

        Comparator<Object> comparatorNumeric = new ComparatorColumn<>();
        for (int i = 0 ; i < tableValues.getColumnCount() ; i++) {
            rowSorter.setComparator(i, comparatorNumeric);
        }
    }

    /**
     * Select every cells.
     */
    public void selectTable() {
        tableValues.selectAll();
    }

    /**
     * Perform copy event on current table.
     */
    public void copyTable() {
        ActionEvent nev = new ActionEvent(tableValues, ActionEvent.ACTION_PERFORMED, "copy");
        tableValues.getActionMap().get(nev.getActionCommand()).actionPerformed(nev);
    }
    
}

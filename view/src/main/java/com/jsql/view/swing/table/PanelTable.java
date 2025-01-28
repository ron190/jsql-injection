/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.table;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.util.UiStringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Display a table for database values. Add keyboard shortcut, mouse icon, text
 * and header formatting.
 */
public class PanelTable extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Table to display in the panel.
     */
    private final JTable tableValues;

    /**
     * Create a panel containing a table to display injection values.
     * 
     * @param data Array 2D with injection table data
     * @param columnNames Names of columns from database
     */
    public PanelTable(String[][] data, String[] columnNames) {
        super(new BorderLayout());

        this.tableValues = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tableValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.tableValues.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.tableValues.setColumnSelectionAllowed(true);
        this.tableValues.setRowHeight(20);
        this.tableValues.setRowSelectionAllowed(true);
        this.tableValues.setCellSelectionEnabled(true);

        this.initRenderer();

        this.tableValues.getTableHeader().setReorderingAllowed(false);

        this.initMouseEvent();
        this.initTabShortcut();

        var columnAdjuster = new AdjusterTableColumn(this.tableValues);
        columnAdjuster.adjustColumns();

        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(this.tableValues.getModel());
        this.tableValues.setRowSorter(rowSorter);
        this.initTableScroller();
        this.initPanelSearch(rowSorter);

        Comparator<Object> comparatorNumeric = new ComparatorColumn<>();
        for (var i = 0 ; i < this.tableValues.getColumnCount() ; i++) {
            rowSorter.setComparator(i, comparatorNumeric);
        }
    }

    private void initMouseEvent() {
        this.tableValues.setDragEnabled(true);
        this.tableValues.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                PanelTable.this.tableValues.requestFocusInWindow();
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Keep selection when multiple cells are selected,
                    // move focus only
                    var p = e.getPoint();
                    var rowNumber = PanelTable.this.tableValues.rowAtPoint(p);
                    var colNumber = PanelTable.this.tableValues.columnAtPoint(p);
                    DefaultListSelectionModel modelRow = (DefaultListSelectionModel) PanelTable.this.tableValues.getSelectionModel();
                    DefaultListSelectionModel modelColumn = (DefaultListSelectionModel) PanelTable.this.tableValues.getColumnModel().getSelectionModel();
                    modelRow.moveLeadSelectionIndex(rowNumber);
                    modelColumn.moveLeadSelectionIndex(colNumber);
                }
            }
        });
    }

    private void initRenderer() {
        final TableCellRenderer cellRendererHeader = this.tableValues.getTableHeader().getDefaultRenderer();
        this.tableValues.getTableHeader().setDefaultRenderer(
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> cellRendererHeader.getTableCellRendererComponent(
                table,
                UiStringUtil.detectUtf8HtmlNoWrap(StringUtils.SPACE + value + StringUtils.SPACE),
                isSelected,
                hasFocus,
                row,
                column
            )
        );

        final var cellRendererDefault = new DefaultTableCellRenderer();
        this.tableValues.setDefaultRenderer(
            this.tableValues.getColumnClass(2),
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
                // Prepare cell value to be utf8 inspected
                String cellValue = value != null ? value.toString() : StringUtils.EMPTY;
                // Fix #90481: NullPointerException on getTableCellRendererComponent()
                try {
                    return cellRendererDefault.getTableCellRendererComponent(
                        table, UiStringUtil.detectUtf8HtmlNoWrap(cellValue), isSelected, hasFocus, row, column
                    );
                } catch (NullPointerException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                    return null;
                }
            }
        );
    }

    private void initTableScroller() {
        var scroller = new JScrollPane(this.tableValues);
        var tableFixedColumn = new FixedColumnTable();
        tableFixedColumn.fixColumnSize(2, scroller);
        this.add(scroller, BorderLayout.CENTER);
    }

    private void initPanelSearch(final TableRowSorter<TableModel> rowSorter) {
        final var panelSearch = new JPanel(new BorderLayout());

        final JTextField textFilter = new JTextFieldPlaceholder("Find in table");
        panelSearch.add(textFilter, BorderLayout.CENTER);

        Action actionShowSearchTable = new ActionShowSearch(panelSearch, textFilter);
        String keySearch = "search";
        this.tableValues.getActionMap().put(keySearch, actionShowSearchTable);
        this.tableValues.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), keySearch);

        Action actionCloseSearch = new ActionCloseSearch(textFilter, panelSearch, this);
        String keyClose = "close";
        textFilter.getActionMap().put(keyClose, actionCloseSearch);
        textFilter.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), keyClose);

        // Fix #43974: PatternSyntaxException on regexFilter() => Pattern.quote()
        textFilter.getDocument().addDocumentListener(new DocumentListener() {
            private void insertUpdateFixed() {
                String text = textFilter.getText();
                if (text.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.insertUpdateFixed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                this.insertUpdateFixed();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        this.tableValues.setComponentPopupMenu(new JPopupMenuTable(this.tableValues, actionShowSearchTable));
        
        JButton buttonCloseSearch = new ButtonClose();
        buttonCloseSearch.addActionListener(actionCloseSearch);
        panelSearch.add(buttonCloseSearch, BorderLayout.EAST);
        panelSearch.setVisible(false);
        this.add(panelSearch, BorderLayout.SOUTH);
    }

    private void initTabShortcut() {
        this.tableValues.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
            null
        );
        this.tableValues.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK),
            null
        );

        Set<AWTKeyStroke> forward = new HashSet<>(
            this.tableValues.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)
        );
        forward.add(KeyStroke.getKeyStroke("TAB"));
        this.tableValues.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        
        Set<AWTKeyStroke> backward = new HashSet<>(
            this.tableValues.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS)
        );
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        this.tableValues.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
    }

    /**
     * Select every cell.
     */
    public void selectTable() {
        this.tableValues.selectAll();
    }

    /**
     * Perform copy event on current table.
     */
    public void copyTable() {
        var actionEvent = new ActionEvent(this.tableValues, ActionEvent.ACTION_PERFORMED, "copy");
        this.tableValues.getActionMap().get(actionEvent.getActionCommand()).actionPerformed(actionEvent);
    }

    
    // Getter and setter
    
    public JTable getTableValues() {
        return this.tableValues;
    }
}

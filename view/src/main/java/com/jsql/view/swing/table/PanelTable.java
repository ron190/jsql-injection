/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import java.util.regex.Pattern;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.tab.ButtonClose;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.util.UiStringUtil;

/**
 * Display a table for database values. Add keyboard shortcut, mouse icon, text
 * and header formatting.
 */
@SuppressWarnings("serial")
public class PanelTable extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Table to display in the panel.
     */
    private JTable tableValues;

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
        this.tableValues.setGridColor(Color.LIGHT_GRAY);

        this.initializeRenderer();

        this.tableValues.getTableHeader().setReorderingAllowed(false);

        this.initializeMouseEvent();
        this.initializeTabShortcut();

        var columnAdjuster = new AdjusterTableColumn(this.tableValues);
        columnAdjuster.adjustColumns();

        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(this.tableValues.getModel());
        this.tableValues.setRowSorter(rowSorter);
        
        this.initializeTableScroller();
        this.initializePanelSearch(rowSorter);

        Comparator<Object> comparatorNumeric = new ComparatorColumn<>();
        
        for (var i = 0 ; i < this.tableValues.getColumnCount() ; i++) {
            
            rowSorter.setComparator(i, comparatorNumeric);
        }
    }

    private void initializeMouseEvent() {
        
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

    private void initializeRenderer() {
        
        final TableCellRenderer cellRendererHeader = this.tableValues.getTableHeader().getDefaultRenderer();
        final var cellRendererDefault = new DefaultTableCellRenderer();
        
        this.tableValues.getTableHeader().setDefaultRenderer(
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
                
                JLabel label = (JLabel) cellRendererHeader.getTableCellRendererComponent(
                    table,
                    UiStringUtil.detectUtf8HtmlNoWrap(StringUtils.SPACE + value + StringUtils.SPACE),
                    isSelected,
                    hasFocus,
                    row,
                    column
                );
                
                label.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    )
                );
                
                return label;
            }
        );
        
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
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                    return null;
                }
            }
        );
    }

    private void initializeTableScroller() {
        
        var scroller = new JScrollIndicator(this.tableValues);
        scroller.getScrollPane().setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        scroller.getScrollPane().setViewportBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        
        AdjustmentListener singleItemScroll = adjustmentEvent -> {
            
            // The user scrolled the List (using the bar, mouse wheel or something else):
            if (adjustmentEvent.getAdjustmentType() == AdjustmentEvent.TRACK) {
                
                adjustmentEvent.getAdjustable().setBlockIncrement(100);
                adjustmentEvent.getAdjustable().setUnitIncrement(100);
            }
        };

        scroller.getScrollPane().getVerticalScrollBar().addAdjustmentListener(singleItemScroll);
        scroller.getScrollPane().getHorizontalScrollBar().addAdjustmentListener(singleItemScroll);

        var tableFixedColumn = new FixedColumnTable();
        tableFixedColumn.fixColumnSize(2, scroller.getScrollPane());
        
        this.add(scroller, BorderLayout.CENTER);
    }

    private void initializePanelSearch(final TableRowSorter<TableModel> rowSorter) {
        
        final var panelSearch = new JPanel(new BorderLayout());
        panelSearch.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        final JTextField textFilter = new JTextFieldPlaceholder("Find in table");
        panelSearch.add(textFilter, BorderLayout.CENTER);

        Action actionShowSearchTable = new ActionShowSearch(panelSearch, textFilter);
        this.tableValues.getActionMap().put("search", actionShowSearchTable);
        this.tableValues.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "search");

        Action actionCloseSearch = new ActionCloseSearch(textFilter, panelSearch, this);
        textFilter.getActionMap().put("close", actionCloseSearch);
        textFilter.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");

        // Fix #43974: PatternSyntaxException on regexFilter() => Pattern.quote()
        textFilter.getDocument().addDocumentListener(new DocumentListener() {
            
            private void insertUpdateFixed() {
                
                String text = textFilter.getText();

                if (text.trim().length() == 0) {
                    
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
        
        this.add(panelSearch, BorderLayout.SOUTH);
        
        panelSearch.setVisible(false);
    }

    private void initializeTabShortcut() {
        
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
     * Select every cells.
     */
    public void selectTable() {
        
        this.tableValues.selectAll();
    }

    /**
     * Perform copy event on current table.
     */
    public void copyTable() {
        
        var nev = new ActionEvent(this.tableValues, ActionEvent.ACTION_PERFORMED, "copy");
        this.tableValues.getActionMap().get(nev.getActionCommand()).actionPerformed(nev);
    }

    
    // Getter and setter
    
    public JTable getTableValues() {
        return this.tableValues;
    }
}

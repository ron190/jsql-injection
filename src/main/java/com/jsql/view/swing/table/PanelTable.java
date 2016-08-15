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
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
    public JTable table;
    
    private FixedColumnTable tableFixedColumn = new FixedColumnTable();

    /**
     * Create a panel containing a table to display injection values.
     * 
     * @param data Array 2D with injection table data
     * @param columnNames Names of columns from database
     */
    public PanelTable(String[][] data, String[] columnNames) {
        super(new BorderLayout());

        table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(true);
        table.setRowHeight(20);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setGridColor(Color.LIGHT_GRAY);

        final TableCellRenderer tcrOs = table.getTableHeader().getDefaultRenderer();
        table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column
            ) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(
                    table, " "+ value +" ", isSelected, hasFocus, row, column
                );
                lbl.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    )
                );
                return lbl;
            }
        });

        table.getTableHeader().setReorderingAllowed(false);

        table.setComponentPopupMenu(new JPopupMenuTable(table));

        table.setDragEnabled(true);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                table.requestFocusInWindow();

                if (SwingUtilities.isRightMouseButton(e)) {
                    /**
                     * Keep selection when multiple cells are selected,
                     * move focus only
                     */
                    Point p = e.getPoint();

                    int rowNumber = table.rowAtPoint(p);
                    int colNumber = table.columnAtPoint(p);

                    DefaultListSelectionModel modelRow = (DefaultListSelectionModel) table
                            .getSelectionModel();
                    DefaultListSelectionModel modelColumn = (DefaultListSelectionModel) table
                            .getColumnModel().getSelectionModel();

                    modelRow.moveLeadSelectionIndex(rowNumber);
                    modelColumn.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), 
            null
        );
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), 
            null
        );

        Set<AWTKeyStroke> forward = new HashSet<>(
            table.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS
            )
        );
        forward.add(KeyStroke.getKeyStroke("TAB"));
        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        
        Set<AWTKeyStroke> backward = new HashSet<>(
            table.getFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS
            )
        );
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);

        AdjusterTableColumn columnAdjuster = new AdjusterTableColumn(table);
        columnAdjuster.adjustColumns();

        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);
        
        JScrollIndicator scroller = new JScrollIndicator(table);
        scroller.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        scroller.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));

        tableFixedColumn.fixColumnSize(2, scroller.scrollPane);
        
        this.add(scroller, BorderLayout.CENTER);
        
        final JPanel panelSearch = new JPanel(new BorderLayout());
        panelSearch.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        final JTextField textFilter = new JTextFieldPlaceholder("Find in table");
        panelSearch.add(textFilter, BorderLayout.CENTER);

        table.getActionMap().put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelSearch.setVisible(true);
                textFilter.requestFocusInWindow();
            }
        });
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "search");

        class ActionCloseSearch extends AbstractAction {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelSearch.setVisible(false);
                table.requestFocusInWindow();
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
        for (int i = 0 ; i < table.getColumnCount() ; i++) {
            rowSorter.setComparator(i, comparatorNumeric);
        }
    }

    /**
     * Select every cells.
     */
    public void selectTable() {
        table.selectAll();
    }

    /**
     * Perform copy event on current table.
     */
    public void copyTable() {
        ActionEvent nev = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
        table.getActionMap().get(nev.getActionCommand()).actionPerformed(nev);
    }
}

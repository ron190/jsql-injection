/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.scrollpane.JScrollIndicator;

/**
 * Display a table for database values.
 * Add keyboard shortcut, mouse icon, text and header formatting.
 */
@SuppressWarnings("serial")
public class PanelTable extends JPanel {
    /**
     * Table to display in the panel.
     */
    public JTable table;

    /**
     * Create a panel containing a table to display injection values.
     * @param data Array 2D with injection table data
     * @param columnNames Names of columns from database
     * @param newJTabbedPane Tabbed pane containing tab for values
     */
    public PanelTable(String[][] data, String[] columnNames, JTabbedPane newJTabbedPane) {
        super(new GridLayout(1, 0));

        table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        new TableFilterHeader(table, AutoChoices.ENABLED);

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
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, " " + value + " ", isSelected, hasFocus, row, column);
                lbl.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY), 
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    )
                );
                return lbl;
            }
        });

        table.getColumnModel().getColumn(0).setResizable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(38);

        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(centerHorizontalAlignment);

        table.getTableHeader().setReorderingAllowed(false);

        table.setComponentPopupMenu(new JPopupMenuTable(table));
        table.setAutoCreateRowSorter(true);

        table.setDragEnabled(true);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                table.requestFocusInWindow();
                
                if (SwingUtilities.isRightMouseButton(e)) {
                    /*
                     * Keep selection when multiple cells are selected and only move focus
                     */
                    Point p = e.getPoint();

                    int rowNumber = table.rowAtPoint(p);
                    int colNumber = table.columnAtPoint(p);
                    
                    DefaultListSelectionModel  modelRow = (DefaultListSelectionModel) table.getSelectionModel();
                    DefaultListSelectionModel  modelColumn = (DefaultListSelectionModel) table.getColumnModel().getSelectionModel();

                    modelRow.moveLeadSelectionIndex(rowNumber);
                    modelColumn.moveLeadSelectionIndex(colNumber);
                }
            }
        });

        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), null);

        Set<AWTKeyStroke> forward = new HashSet<AWTKeyStroke>(table.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        Set<AWTKeyStroke> backward = new HashSet<AWTKeyStroke>(table.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);

        AdjusterTableColumn columnAdjuster = new AdjusterTableColumn(table);
        columnAdjuster.adjustColumns();

        JScrollIndicator scroller = new JScrollIndicator(table);
        scroller.scrollPane.setBorder(BorderFactory.createEmptyBorder(0,0,-1,-1));
        scroller.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0,0,-1,-1));

        new FixedColumnTable(1, scroller.scrollPane);
        this.add(scroller);
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

    /**
     * Renderer used to center text on certains columns.
     */
    private class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            this.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(new Color(230, 230, 230));
            // Report #218: ignore if value is null
            if (value != null) {
                setText(value.toString());
            }
            return this;
        }
    }
}


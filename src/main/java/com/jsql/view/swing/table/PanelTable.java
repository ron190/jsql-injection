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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.jsql.view.swing.popupmenu.JPopupMenuTable;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.tab.ButtonClose;
import com.jsql.view.swing.text.JTextFieldPlaceholder;

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
//        super(new GridLayout(1, 0));
        super(new BorderLayout());

        table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
//        new TableFilterHeader(table, AutoChoices.ENABLED);

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
//        table.setAutoCreateRowSorter(true);

        table.setDragEnabled(true);

        table.addMouseListener(new MouseAdapter() {
            @Override
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

        Set<AWTKeyStroke> forward = new HashSet<>(table.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        Set<AWTKeyStroke> backward = new HashSet<>(table.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);

        AdjusterTableColumn columnAdjuster = new AdjusterTableColumn(table);
        columnAdjuster.adjustColumns();

        JScrollIndicator scroller = new JScrollIndicator(table);
        scroller.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        scroller.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));

        
        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);
        new FixedColumnTable(1, scroller.scrollPane);
        this.add(scroller, BorderLayout.CENTER);
        final JTextField jtfFilter = new JTextFieldPlaceholder("Find in table");
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        panel.add(jtfFilter, BorderLayout.CENTER);
        JButton tabCloseButton = new ButtonClose();
        tabCloseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(false);
            }
        });
        
        table.getActionMap().put("search", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(true);
                jtfFilter.requestFocusInWindow();
            }
        });
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "search");
        
        jtfFilter.getActionMap().put("close", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(false);
                table.requestFocusInWindow();
            }
        });
        jtfFilter.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        
        panel.add(tabCloseButton, BorderLayout.EAST);
        this.add(panel, BorderLayout.SOUTH);
        panel.setVisible(false);
        jtfFilter.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
        

//      TableRowSorter<TableModel> rowSorter = new TableRowSorter<>();
//      newTableJPanel.table.setRowSorter(rowSorter);
//      
      // TODO No more applied (filter lib in cause)
      Comparator<Object> c1 = new ComparatorColumn();
//      
//      rowSorter.setModel(newTableJPanel.table.getModel());
      for (int i = 0; i < table.getColumnCount(); i++) {
          rowSorter.setComparator(i, c1);
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


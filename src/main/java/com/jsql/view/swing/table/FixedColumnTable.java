package com.jsql.view.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *  Prevent the specified number of columns from scrolling horizontally in
 *  the scroll pane. The table must already exist in the scroll pane.
 *
 *  The functionality is accomplished by creating a second JTable (fixed)
 *  that will share the TableModel and SelectionModel of the main table.
 *  This table will be used as the row header of the scroll pane.
 *
 *  The fixed table created can be accessed by using the getFixedTable()
 *  method. will be returned from this method. It will allow you to:
 *
 *  You can change the model of the main table and the change will be
 *  reflected in the fixed model. However, you cannot change the structure
 *  of the model.
 */
public class FixedColumnTable implements ChangeListener, PropertyChangeListener {
    
    private static JTable mainTable;
    private static JTable fixedTable;
    private static JScrollPane scrollPane;
    private static FixedColumnTable p = new FixedColumnTable();

    /**
     *  Specify the number of columns to be fixed and the scroll pane
     *  containing the table.
     */
    @SuppressWarnings("serial")
    public static void fixColumnSize(int fixedColumns, JScrollPane scrollPane) {
        FixedColumnTable.scrollPane = scrollPane;

        FixedColumnTable.mainTable = (JTable) scrollPane.getViewport().getView();
        FixedColumnTable.mainTable.setAutoCreateColumnsFromModel(false);
        FixedColumnTable.mainTable.addPropertyChangeListener(p);

        //  Use the existing table to create a new table sharing
        //  the DataModel and ListSelectionModel
        FixedColumnTable.fixedTable = new JTable() {
            @Override
            public boolean isCellEditable(int row,int column) {
                return false;
            }
        };
        
        FixedColumnTable.fixedTable.setAutoCreateColumnsFromModel(false);
        final DefaultTableModel modelFixedTable = new DefaultTableModel(){
            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }

            @Override
            public int getRowCount() {
                return FixedColumnTable.mainTable.getRowCount();
            }

            @Override
            public Class<?> getColumnClass(int colNum) {
                Class<?> columnClass;
                
                if (colNum == 0) {
                    columnClass = String.class;
                } else {
                    columnClass = super.getColumnClass(colNum);
                }
                
                return columnClass;
            }
        };
        
        FixedColumnTable.fixedTable.setModel(modelFixedTable);
        FixedColumnTable.fixedTable.setSelectionModel(FixedColumnTable.mainTable.getSelectionModel());
        
        FixedColumnTable.fixedTable.setRowHeight(20);
        FixedColumnTable.fixedTable.setFocusable(false);
        FixedColumnTable.fixedTable.getTableHeader().setReorderingAllowed(false);
        FixedColumnTable.fixedTable.setGridColor(Color.LIGHT_GRAY);
        
        FixedColumnTable.fixedTable.getTableHeader().setDefaultRenderer(new RowHeaderRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column
            ) {
                JComponent label = (JComponent) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );
                label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
                return label;
            }
        });
        
        //  Remove the fixed columns from the main table
        //  and add them to the fixed table
        for (int i = 0 ; i < fixedColumns ; i++) {
            TableColumnModel columnModel = FixedColumnTable.mainTable.getColumnModel();
            TableColumn column = columnModel.getColumn(0);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            FixedColumnTable.fixedTable.getColumnModel().addColumn(new TableColumn());
        }

        DefaultTableCellRenderer rowHeaderRender = new RowHeaderRenderer();
        FixedColumnTable.fixedTable.getColumnModel().getColumn(0).setCellRenderer(rowHeaderRender);
        FixedColumnTable.fixedTable.getColumnModel().getColumn(0).setResizable(false);
        FixedColumnTable.fixedTable.getColumnModel().getColumn(0).setPreferredWidth(38);

        FixedColumnTable.mainTable.getRowSorter().addRowSorterListener(
            new RowSorterListener() {
                @Override
                public void sorterChanged(RowSorterEvent e) {
                    modelFixedTable.fireTableDataChanged();
                    // Copy data from hidden column in main table
                    for (int i = 0 ; i < FixedColumnTable.mainTable.getRowCount() ; i++) {
                        FixedColumnTable.fixedTable.setValueAt(FixedColumnTable.mainTable.getValueAt(i, 0), i, 0);
                    }
                }
            }
        );
        
        FixedColumnTable.mainTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    modelFixedTable.fireTableRowsUpdated(0, modelFixedTable.getRowCount() - 1);
                }
            }
        );
        
        // Copy data from first colum of main table to fixed column
        for (int i = 0 ; i < FixedColumnTable.mainTable.getRowCount() ; i++) {
            FixedColumnTable.fixedTable.setValueAt(FixedColumnTable.mainTable.getValueAt(i, 0), i, 0);
        }
        
        //  Add the fixed table to the scroll pane
        FixedColumnTable.fixedTable.setPreferredScrollableViewportSize(FixedColumnTable.fixedTable.getPreferredSize());
        scrollPane.setRowHeaderView(FixedColumnTable.fixedTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, FixedColumnTable.fixedTable.getTableHeader());

        // Synchronize scrolling of the row header with the main table
        scrollPane.getRowHeader().addChangeListener(p);
    }

    /**
     * Implement the ChangeListener
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        //  Sync the scroll pane scrollbar with the row header
        JViewport viewport = (JViewport) e.getSource();
        scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
    }
    
    /**
     * Implement the PropertyChangeListener
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        //  Keep the fixed table in sync with the main table
        if ("selectionModel".equals(e.getPropertyName())) {
            FixedColumnTable.fixedTable.setSelectionModel(FixedColumnTable.mainTable.getSelectionModel());
        }

        if ("model".equals(e.getPropertyName())) {
            FixedColumnTable.fixedTable.setModel(mainTable.getModel());
        }
    }
}

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
    private JTable mainTable;
    private JTable fixedTable;
    private JScrollPane scrollPane;

    /**
     *  Specify the number of columns to be fixed and the scroll pane
     *  containing the table.
     */
    @SuppressWarnings("serial")
    public FixedColumnTable(int fixedColumns, JScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        mainTable = ((JTable) scrollPane.getViewport().getView());
        mainTable.setAutoCreateColumnsFromModel(false);
        mainTable.addPropertyChangeListener(this);

        //  Use the existing table to create a new table sharing
        //  the DataModel and ListSelectionModel
        fixedTable = new JTable() {
            @Override
            public boolean isCellEditable(int row,int column) {
                return false;
            }
        };
        
        fixedTable.setAutoCreateColumnsFromModel(false);
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
                return mainTable.getRowCount();
            }

            @Override
            public Class<?> getColumnClass(int colNum) {
                switch (colNum) {
                    case 0:
                        return String.class;
                    default:
                        return super.getColumnClass(colNum);
                }
            }
        };
        
        fixedTable.setModel(modelFixedTable);
        fixedTable.setSelectionModel(mainTable.getSelectionModel());
        
        fixedTable.setRowHeight(20);
        fixedTable.setFocusable(false);
        fixedTable.getTableHeader().setReorderingAllowed(false);
        fixedTable.setGridColor(Color.LIGHT_GRAY);
        
        fixedTable.getTableHeader().setDefaultRenderer(new RowHeaderRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column
            ) {
                JComponent lbl = (JComponent) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );
                lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
                return lbl;
            }
        });
        
        //  Remove the fixed columns from the main table
        //  and add them to the fixed table
        for (int i = 0; i < fixedColumns; i++) {
            TableColumnModel columnModel = mainTable.getColumnModel();
            TableColumn column = columnModel.getColumn(0);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            fixedTable.getColumnModel().addColumn(new TableColumn());
        }

        DefaultTableCellRenderer rowHeaderRender = new RowHeaderRenderer();
        fixedTable.getColumnModel().getColumn(0).setCellRenderer(rowHeaderRender);
        fixedTable.getColumnModel().getColumn(0).setResizable(false);
        fixedTable.getColumnModel().getColumn(0).setPreferredWidth(38);

        mainTable.getRowSorter().addRowSorterListener(
            new RowSorterListener() {
                @Override
                public void sorterChanged(RowSorterEvent e) {
                    modelFixedTable.fireTableDataChanged();
                    // Copy data from hidden column in main table
                    for (int i = 0; i < mainTable.getRowCount(); i++) {
                        fixedTable.setValueAt(mainTable.getValueAt(i, 0), i, 0);
                    }
                }
            }
        );
        
        mainTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    modelFixedTable.fireTableRowsUpdated(0, modelFixedTable.getRowCount() - 1);
                }
            }
        );
        
        // Copy data from first colum of main table to fixed column
        for (int i = 0; i < mainTable.getRowCount(); i++) {
            fixedTable.setValueAt(mainTable.getValueAt(i, 0), i, 0);
        }
        
        //  Add the fixed table to the scroll pane
        fixedTable.setPreferredScrollableViewportSize(fixedTable.getPreferredSize());
        scrollPane.setRowHeaderView(fixedTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());

        // Synchronize scrolling of the row header with the main table
        scrollPane.getRowHeader().addChangeListener(this);
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
            fixedTable.setSelectionModel(mainTable.getSelectionModel());
        }

        if ("model".equals(e.getPropertyName())) {
            fixedTable.setModel(mainTable.getModel());
        }
    }
}

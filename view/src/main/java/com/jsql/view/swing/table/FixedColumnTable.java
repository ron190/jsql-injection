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
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    public void fixColumnSize(int fixedColumns, JScrollPane scrollPane) {
        
        this.scrollPane = scrollPane;

        this.mainTable = (JTable) scrollPane.getViewport().getView();
        this.mainTable.setAutoCreateColumnsFromModel(false);
        this.mainTable.addPropertyChangeListener(this);

        //  Use the existing table to create a new table sharing
        //  the DataModel and ListSelectionModel
        this.fixedTable = new JTable() {
            
            @Override
            public boolean isCellEditable(int row,int column) {
                
                return false;
            }
        };
        
        this.fixedTable.setAutoCreateColumnsFromModel(false);
        
        final DefaultTableModel modelFixedTable = new DefaultTableModel() {
            
            @Override
            public int getColumnCount() {
                
                return 2;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                
                return false;
            }

            @Override
            public int getRowCount() {
                
                return FixedColumnTable.this.mainTable.getRowCount();
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
        
        this.fixedTable.setModel(modelFixedTable);
        this.fixedTable.setSelectionModel(this.mainTable.getSelectionModel());
        
        this.fixedTable.setRowHeight(20);
        this.fixedTable.setFocusable(false);
        this.fixedTable.getTableHeader().setReorderingAllowed(false);
        this.fixedTable.setGridColor(Color.LIGHT_GRAY);
        
        this.fixedTable.getTableHeader().setDefaultRenderer(new RowHeaderRenderer() {
            
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
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
        TableColumnModel columnModel = this.mainTable.getColumnModel();
        for (var i = 0 ; i < fixedColumns ; i++) {
            
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            
            this.fixedTable.getColumnModel().addColumn(new TableColumn(i));
        }

        this.fixedTable.getColumnModel().getColumn(0).setCellRenderer(new RowHeaderRenderer());
        this.fixedTable.getColumnModel().getColumn(0).setResizable(false);
        this.fixedTable.getColumnModel().getColumn(0).setPreferredWidth(38);
        this.fixedTable.getColumnModel().getColumn(1).setCellRenderer(new RowHeaderRenderer());
        this.fixedTable.getColumnModel().getColumn(1).setResizable(false);
        this.fixedTable.getColumnModel().getColumn(1).setPreferredWidth(38);

        this.mainTable.getRowSorter().addRowSorterListener(rowSorterEvent -> {
            
            modelFixedTable.fireTableDataChanged();
            
            // Copy data from hidden column in main table
            for (var i = 0 ; i < FixedColumnTable.this.mainTable.getRowCount() ; i++) {
                
                FixedColumnTable.this.fixedTable.setValueAt(FixedColumnTable.this.mainTable.getValueAt(i, 0), i, 0);
                FixedColumnTable.this.fixedTable.setValueAt(FixedColumnTable.this.mainTable.getValueAt(i, 1), i, 1);
            }
        });
        
        this.mainTable.getSelectionModel().addListSelectionListener(listSelectionEvent ->
            modelFixedTable.fireTableRowsUpdated(0, modelFixedTable.getRowCount() - 1)
        );
        
        // Copy data from first column of main table to fixed column
        for (var i = 0 ; i < this.mainTable.getRowCount() ; i++) {
            
            this.fixedTable.setValueAt(this.mainTable.getValueAt(i, 0), i, 0);
            this.fixedTable.setValueAt(this.mainTable.getValueAt(i, 1), i, 1);
        }
        
        //  Add the fixed table to the scroll pane
        this.fixedTable.setPreferredScrollableViewportSize(this.fixedTable.getPreferredSize());
        scrollPane.setRowHeaderView(this.fixedTable);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, this.fixedTable.getTableHeader());

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
        this.scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
    }
    
    /**
     * Implement the PropertyChangeListener
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        
        //  Keep the fixed table in sync with the main table
        if ("selectionModel".equals(e.getPropertyName())) {
            
            this.fixedTable.setSelectionModel(this.mainTable.getSelectionModel());
        }

        if ("model".equals(e.getPropertyName())) {
            
            this.fixedTable.setModel(this.mainTable.getModel());
        }
    }
}

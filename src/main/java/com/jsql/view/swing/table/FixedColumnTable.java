package com.jsql.view.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
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
import javax.swing.table.TableCellRenderer;
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
    private JTable main;
    private JTable fixed;
    private JScrollPane scrollPane;

    /**
     *  Specify the number of columns to be fixed and the scroll pane
     *  containing the table.
     */
    @SuppressWarnings("serial")
    public FixedColumnTable(int fixedColumns, JScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        main = ((JTable)scrollPane.getViewport().getView());
        main.setAutoCreateColumnsFromModel( false );
        main.addPropertyChangeListener( this );

        //  Use the existing table to create a new table sharing
        //  the DataModel and ListSelectionModel
        fixed = new JTable() {
            @Override
            public boolean isCellEditable(int row,int column) {
                return false;
            }
        };
        fixed.setAutoCreateColumnsFromModel(false);
        fixed.setRowHeight(20);
        final DefaultTableModel a = new DefaultTableModel(){

            private static final long serialVersionUID = 1L;

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
                return main.getRowCount();
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
        
//        fixed.setModel(main.getModel());
        fixed.setModel(a);
        fixed.setSelectionModel(main.getSelectionModel());
        fixed.setFocusable(false);
        fixed.getTableHeader().setReorderingAllowed(false);
        
        fixed.setGridColor(Color.LIGHT_GRAY);
        
        final TableCellRenderer tcrOs = fixed.getTableHeader().getDefaultRenderer();
        fixed.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);
                lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
                if (column == 1) {
                    lbl.setBackground(new Color(230,230,230));
                }
                return lbl;
            }
        });
        
        //  Remove the fixed columns from the main table
        //  and add them to the fixed table
        List<String> numdata = new ArrayList<String>();
        for (int count = 0; count < main.getModel().getRowCount(); count++){
              numdata.add(main.getModel().getValueAt(count, 0).toString());
        }
        for (int i = 0; i < fixedColumns; i++) {
            TableColumnModel columnModel = main.getColumnModel();
            TableColumn column = columnModel.getColumn(0);
//            columnModel.removeColumn(column);
            column.setWidth(0);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            column.setPreferredWidth(0);
//            fixed.getColumnModel().addColumn(column);
            fixed.getColumnModel().addColumn(new TableColumn());
        }
        
        fixed.getColumnModel().getColumn(0).setResizable(false);
        fixed.getColumnModel().getColumn(0).setPreferredWidth(38);

        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        fixed.getColumnModel().getColumn(0).setCellRenderer(centerHorizontalAlignment);

        main.getRowSorter().addRowSorterListener(new RowSorterListener() {

            @Override
            public void sorterChanged(RowSorterEvent e) {
                a.fireTableDataChanged();
                for (int i = 0; i < main.getRowCount(); i++) {
                    fixed.setValueAt(main.getValueAt(i, 0), i, 0);
                }
            }
        });
        main.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                a.fireTableRowsUpdated(0, a.getRowCount() - 1);
            }
        });
        
        for (int i = 0; i < main.getRowCount(); i++) {
            fixed.setValueAt(main.getValueAt(i, 0), i, 0);
        }
        
        //  Add the fixed table to the scroll pane
        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
        scrollPane.setRowHeaderView(fixed);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());

        // Synchronize scrolling of the row header with the main table
        scrollPane.getRowHeader().addChangeListener(this);
    }

    /**
     *  Return the table being used in the row header
     */
    public JTable getFixedTable() {
        return fixed;
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
            fixed.setSelectionModel(main.getSelectionModel());
        }

        if ("model".equals(e.getPropertyName())) {
            fixed.setModel(main.getModel());
        }
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

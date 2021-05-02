package com.jsql.view.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *  Class to manage the widths of columns in a table.
 *
 *  Various properties control how the width of the column is calculated.
 *  Another property controls whether column width calculation should be dynamic.
 *  Finally, various Actions will be added to the table to allow the user
 *  to customize the functionality.
 *
 *  This class was designed to be used with tables that use an auto resize mode
 *  of AUTO_RESIZE_OFF. With all other modes you are constrained as the width
 *  of the columns must fit inside the table. So if you increase one column, one
 *  or more of the other columns must decrease. Because of this the resize mode
 *  of RESIZE_ALL_COLUMNS will work the best.
 */
public class AdjusterTableColumn implements PropertyChangeListener, TableModelListener {
    
    private JTable tableAdjust;
    private int spacing;
    private boolean isColumnHeaderIncluded;
    private boolean isColumnDataIncluded;
    private boolean isOnlyAdjustLarger;
    private boolean isDynamicAdjustment;
    private Map<TableColumn, Integer> columnSizes = new HashMap<>();

    /**
     *  Specify the table and use default spacing
     */
    public AdjusterTableColumn(JTable table) {
        this(table, 6);
    }

    /**
     *  Specify the table and spacing
     */
    public AdjusterTableColumn(JTable tableAdjust, int spacing) {
        
        this.tableAdjust = tableAdjust;
        
        final TableCellRenderer tcrOs = tableAdjust.getTableHeader().getDefaultRenderer();
        tableAdjust.getTableHeader().setDefaultRenderer(
            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
                    
                JLabel label = (JLabel) tcrOs.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );
                
                label.setBackground(new Color(230, 230, 230));
                
                return label;
            }
        );
        
        this.spacing = spacing;
        this.setColumnHeaderIncluded( true );
        this.setColumnDataIncluded( true );
        this.setOnlyAdjustLarger( true );
        this.setDynamicAdjustment( false );
        this.installActions();
    }

    /**
     *  Adjust the widths of all the columns in the table
     */
    public void adjustColumns() {
        
        TableColumnModel tcm = this.tableAdjust.getColumnModel();

        for (var i = 0 ; i < tcm.getColumnCount() ; i++) {
            
            this.adjustColumn(i);
        }
    }

    /**
     *  Adjust the width of the specified column in the table
     */
    public void adjustColumn(final int column) {
        
        var tableColumn = this.tableAdjust.getColumnModel().getColumn(column);

        if (! tableColumn.getResizable()) {
            
            return;
        }

        int columnHeaderWidth = this.getColumnHeaderWidth( column );
        int columnDataWidth   = this.getColumnDataWidth( column );
        int preferredWidth    = Math.max(columnHeaderWidth, columnDataWidth);

        this.updateTableColumn(column, preferredWidth);
    }

    /**
     *  Calculated the width based on the column name
     */
    private int getColumnHeaderWidth(int column) {
        
        if (! this.isColumnHeaderIncluded) {
            
            return 0;
        }

        var tableColumn = this.tableAdjust.getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();

        if (renderer == null) {
            
            renderer = this.tableAdjust.getTableHeader().getDefaultRenderer();
        }

        var c = renderer.getTableCellRendererComponent(this.tableAdjust, value, false, false, -1, column);
        
        return c.getPreferredSize().width;
    }

    /**
     *  Calculate the width based on the widest cell renderer for the
     *  given column.
     */
    private int getColumnDataWidth(int column) {
        
        if (! this.isColumnDataIncluded) {
            
            return 0;
        }

        var preferredWidth = 0;
        int maxWidth = this.tableAdjust.getColumnModel().getColumn(column).getMaxWidth();

        for (var row = 0 ; row < this.tableAdjust.getRowCount() ; row++) {
            
            preferredWidth = Math.max(preferredWidth, this.getCellDataWidth(row, column));

            //  We've exceeded the maximum width, no need to check other rows
            if (preferredWidth >= maxWidth) {
                
                break;
            }
        }

        return preferredWidth;
    }

    /**
     *  Get the preferred width for the specified cell
     */
    private int getCellDataWidth(int row, int column) {
        
        //  Invoke the renderer for the cell to calculate the preferred width
        TableCellRenderer cellRenderer = this.tableAdjust.getCellRenderer(row, column);
        Component c = this.tableAdjust.prepareRenderer(cellRenderer, row, column);
        
        return c.getPreferredSize().width + this.tableAdjust.getIntercellSpacing().width;
    }

    /**
     *  Update the TableColumn with the newly calculated width
     */
    private void updateTableColumn(int column, int width) {
        
        final var tableColumn = this.tableAdjust.getColumnModel().getColumn(column);

        if (! tableColumn.getResizable()) {
            
            return;
        }

        int calculatedWidth = width;
        calculatedWidth += this.spacing;

        //  Don't shrink the column width
        if (this.isOnlyAdjustLarger) {
            
            calculatedWidth = Math.max(calculatedWidth, tableColumn.getPreferredWidth());
        }

        this.columnSizes.put(tableColumn, tableColumn.getWidth());
        this.tableAdjust.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(calculatedWidth);
    }

    /**
     *  Restore the widths of the columns in the table to its previous width
     */
    public void restoreColumns() {
        
        TableColumnModel tcm = this.tableAdjust.getColumnModel();

        for (var i = 0 ; i < tcm.getColumnCount() ; i++) {
            
            this.restoreColumn(i);
        }
    }

    /**
     *  Restore the width of the specified column to its previous width
     */
    private void restoreColumn(int column) {
        
        var tableColumn = this.tableAdjust.getColumnModel().getColumn(column);
        Integer width = this.columnSizes.get(tableColumn);

        if (width != null) {
            
            this.tableAdjust.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth( width );
        }
    }

    /**
     *    Indicates whether to include the header in the width calculation
     */
    public void setColumnHeaderIncluded(boolean isColumnHeaderIncluded) {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    /**
     *    Indicates whether to include the model data in the width calculation
     */
    public void setColumnDataIncluded(boolean isColumnDataIncluded) {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    /**
     *    Indicates whether columns can only be increased in size
     */
    public void setOnlyAdjustLarger(boolean isOnlyAdjustLarger) {
        this.isOnlyAdjustLarger = isOnlyAdjustLarger;
    }

    /**
     *  Indicate whether changes to the model should cause the width to be
     *  dynamically recalculated.
     */
    public void setDynamicAdjustment(boolean isDynamicAdjustment) {
        
        //  May need to add or remove the TableModelListener when changed
        if (this.isDynamicAdjustment != isDynamicAdjustment) {
            
            if (isDynamicAdjustment) {
                
                this.tableAdjust.addPropertyChangeListener( this );
                this.tableAdjust.getModel().addTableModelListener( this );
                
            } else {
                
                this.tableAdjust.removePropertyChangeListener( this );
                this.tableAdjust.getModel().removeTableModelListener( this );
            }
        }

        this.isDynamicAdjustment = isDynamicAdjustment;
    }
    
    /**
     * Implement the PropertyChangeListener
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        
        //  When the TableModel changes we need to update the listeners
        //  and column widths
        if ("model".equals(e.getPropertyName())) {
            
            TableModel model = (TableModel)e.getOldValue();
            model.removeTableModelListener( this );

            model = (TableModel)e.getNewValue();
            model.addTableModelListener( this );
            this.adjustColumns();
        }
    }
    
    /**
     * Implement the TableModelListener
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        
        if (! this.isColumnDataIncluded) {
            return;
        }

        //  A cell has been updated
        if (e.getType() == TableModelEvent.UPDATE) {
            
            int column = this.tableAdjust.convertColumnIndexToView(e.getColumn());

            //  Only need to worry about an increase in width for this cell
            if (this.isOnlyAdjustLarger) {
                
                int    row = e.getFirstRow();
                var tableColumn = this.tableAdjust.getColumnModel().getColumn(column);

                if (tableColumn.getResizable()) {
                    
                    int width = this.getCellDataWidth(row, column);
                    this.updateTableColumn(column, width);
                }
                
            } else {
                
                //    Could be an increase of decrease so check all rows
                this.adjustColumn( column );
            }
        } else {
            
            //  The update affected more than one column so adjust all columns
            this.adjustColumns();
        }
    }

    /**
     *  Install Actions to give user control of certain functionality.
     */
    private void installActions() {
        
        this.installColumnAction(true,  true,  "adjustColumn",   "control ADD");
        this.installColumnAction(false, true,  "adjustColumns",  "control shift ADD");
        this.installColumnAction(true,  false, "restoreColumn",  "control SUBTRACT");
        this.installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");

        this.installToggleAction(true,  false, "toggleDynamic",  "control MULTIPLY");
        this.installToggleAction(false, true,  "toggleLarger",   "control DIVIDE");
    }

    /**
     *  Update the input and action maps with a new ColumnAction
     */
    private void installColumnAction(boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke) {
        
        Action action = new ColumnAction(isSelectedColumn, isAdjust);
        var ks = KeyStroke.getKeyStroke( keyStroke );
        
        this.tableAdjust.getInputMap().put(ks, key);
        this.tableAdjust.getActionMap().put(key, action);
    }

    /**
     *  Update the input and action maps with new ToggleAction
     */
    private void installToggleAction(boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke) {
        
        Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
        var ks = KeyStroke.getKeyStroke( keyStroke );
        
        this.tableAdjust.getInputMap().put(ks, key);
        this.tableAdjust.getActionMap().put(key, action);
    }

    /**
     *  Action to adjust or restore the width of a single column or all columns
     */
    @SuppressWarnings("serial")
    class ColumnAction extends AbstractAction {
        
        private boolean isSelectedColumn;
        private boolean isAdjust;

        public ColumnAction(boolean isSelectedColumn, boolean isAdjust) {
            
            this.isSelectedColumn = isSelectedColumn;
            this.isAdjust = isAdjust;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            //  Handle selected column(s) width change actions
            if (this.isSelectedColumn) {
                
                int[] columns = AdjusterTableColumn.this.tableAdjust.getSelectedColumns();

                for (int column: columns) {
                    
                    if (this.isAdjust) {
                        
                        AdjusterTableColumn.this.adjustColumn(column);
                        
                    } else {
                        
                        AdjusterTableColumn.this.restoreColumn(column);
                    }
                }
            } else {
                
                if (this.isAdjust) {
                    
                    AdjusterTableColumn.this.adjustColumns();
                    
                } else {
                    
                    AdjusterTableColumn.this.restoreColumns();
                }
            }
        }
    }

    /**
     *  Toggle properties of the TableColumnAdjuster so the user can
     *  customize the functionality to their preferences
     */
    @SuppressWarnings("serial")
    class ToggleAction extends AbstractAction {
        
        private boolean isToggleDynamic;
        private boolean isToggleLarger;

        public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger) {
            
            this.isToggleDynamic = isToggleDynamic;
            this.isToggleLarger = isToggleLarger;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if (this.isToggleDynamic) {
                
                AdjusterTableColumn.this.setDynamicAdjustment(! AdjusterTableColumn.this.isDynamicAdjustment);
                
            } else if (this.isToggleLarger) {
                
                AdjusterTableColumn.this.setOnlyAdjustLarger(! AdjusterTableColumn.this.isOnlyAdjustLarger);
            }
        }
    }
}

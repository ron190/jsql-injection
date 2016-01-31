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
package com.jsql.view.swing.interaction;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.tool.ToolsString;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.table.ComparatorColumn;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab implements IInteractionCommand {
    /**
     * Array of column names, diplayed in header table.
     */
    private String[] columnNames;

    /**
     * 2D array of values.
     */
    private String[][] data;

    /**
     * The table containing the data.
     */
    private AbstractElementDatabase table;

    /**
     * @param interactionParams Names of columns, table's values and corresponding table
     */
    public CreateValuesTab(Object[] interactionParams) {
        // Array of column names, diplayed in header table
        columnNames = (String[]) interactionParams[0];
        // 2D array of values
        data = (String[][]) interactionParams[1];
        // The table containing the data
        table = (AbstractElementDatabase) interactionParams[2];
    }

    @Override
    public void execute() {
        // Report NullPointerException #1683 
        DefaultMutableTreeNode node = MediatorGUI.gui().getTreeNodeModels().get(table);
        
        if (node != null) {
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Update the progress value of the model, end the progress
            progressingTreeNodeModel.childUpgradeCount = table.getCount();
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;
            
            // Create a new table to display the values
            PanelTable newTableJPanel = new PanelTable(data, columnNames, MediatorGUI.right());
            
            TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>();
            newTableJPanel.table.setRowSorter(rowSorter);
            
            // TODO No more applied (filter lib in cause)
            Comparator<Object> c1 = new ComparatorColumn();
            
            rowSorter.setModel(newTableJPanel.table.getModel());
            for (int i = 2; i < newTableJPanel.table.getColumnCount(); i++) {
                rowSorter.setComparator(i, c1);
            }
            
            // Create a new tab: add header and table
            MediatorGUI.right().addTab(table + " ", newTableJPanel);
            // Focus on the new tab
            MediatorGUI.right().setSelectedComponent(newTableJPanel);
            
            // Create a custom tab header with close button
            TabHeader header = new TabHeader();
            
            MediatorGUI.right().setToolTipTextAt(
                    MediatorGUI.right().indexOfComponent(newTableJPanel),
                    "<html><b>" + table.getParent() + "." + table + "</b><br>"
                            + "<i>" + ToolsString.join(Arrays.copyOfRange(columnNames, 2, columnNames.length), "<br>") + "</i></html>");
            
            // Apply the custom header to the tab
            MediatorGUI.right().setTabComponentAt(MediatorGUI.right().indexOfComponent(newTableJPanel), header);
        }
    }
}

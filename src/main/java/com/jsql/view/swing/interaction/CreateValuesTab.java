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

import javax.swing.tree.DefaultMutableTreeNode;

import com.jsql.model.accessible.bean.AbstractElementDatabase;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab implements InteractionCommand {
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
        DefaultMutableTreeNode node = MediatorGui.frame().getTreeNodeModels().get(table);
        
        if (node != null) {
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Update the progress value of the model, end the progress
            progressingTreeNodeModel.indexProgress = table.getCount();
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;
            
            // Create a new table to display the values
            PanelTable newTableJPanel = new PanelTable(data, columnNames);
            
            // Create a new tab: add header and table
            MediatorGui.tabResults().addTab(table + " ", newTableJPanel);
            // Focus on the new tab
            MediatorGui.tabResults().setSelectedComponent(newTableJPanel);
            
            // Create a custom tab header with close button
            TabHeader header = new TabHeader();
            
            MediatorGui.tabResults().setToolTipTextAt(
                MediatorGui.tabResults().indexOfComponent(newTableJPanel),
                "<html><b>"
                + table.getParent() +"."+ table +"</b><br>"
                + "<i>"+ StringUtil.join(Arrays.copyOfRange(columnNames, 2, columnNames.length), "<br>") 
                + "</i></html>"
            );
            
            // Apply the custom header to the tab
            MediatorGui.tabResults().setTabComponentAt(
                MediatorGui.tabResults().indexOfComponent(newTableJPanel), 
                header
            );
        }
    }
}

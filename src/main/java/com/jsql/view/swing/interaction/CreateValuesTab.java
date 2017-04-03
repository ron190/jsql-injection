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
package com.jsql.view.swing.interaction;

import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.tree.model.AbstractNodeModel;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab extends CreateTab implements InteractionCommand {
	
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
        this.columnNames = (String[]) interactionParams[0];
        // 2D array of values
        this.data = (String[][]) interactionParams[1];
        // The table containing the data
        this.table = (AbstractElementDatabase) interactionParams[2];
    }

    @Override
    public void execute() {
        // Report NullPointerException #1683 
        DefaultMutableTreeNode node = MediatorGui.frame().getTreeNodeModels().get(this.table);
        
        if (node != null) {
            // Get the node
            AbstractNodeModel progressingTreeNodeModel = (AbstractNodeModel) node.getUserObject();
            
            // Update the progress value of the model, end the progress
            progressingTreeNodeModel.indexProgress = this.table.getChildCount();
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;
            
            // Create a new table to display the values
            PanelTable newTableJPanel = new PanelTable(this.data, this.columnNames);
            
            // Create a new tab: add header and table
            MediatorGui.tabResults().addTab(StringUtil.detectUtf8(this.table.toString()), newTableJPanel);
            
            // Focus on the new tab
            MediatorGui.tabResults().setSelectedComponent(newTableJPanel);
            
            // Create a custom tab header with close button
            TabHeader header = new TabHeader(StringUtil.detectUtf8Html(this.table +" "));
            
            MediatorGui.tabResults().setToolTipTextAt(
                MediatorGui.tabResults().indexOfComponent(newTableJPanel),
                "<html>"
                + "<b>"+ this.table.getParent() +"."+ this.table +"</b><br>"
                + "<i>"+ StringUtil.join(Arrays.copyOfRange(this.columnNames, 2, this.columnNames.length), "<br>") +"</i>"
                + "</html>"
            );
            
            // Apply the custom header to the tab
            MediatorGui.tabResults().setTabComponentAt(
                MediatorGui.tabResults().indexOfComponent(newTableJPanel), 
                header
            );
        }
    }
    
}

/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.jsql.model.bean.ElementDatabase;
import com.jsql.tool.StringTool;
import com.jsql.view.GUIMediator;
import com.jsql.view.component.TabHeader;
import com.jsql.view.table.ColumnComparator;
import com.jsql.view.table.TablePanel;
import com.jsql.view.tree.NodeModel;

/**
 * Create a new tab for the values
 */
public class CreateValuesTab implements InteractionCommand{
    // Array of column names, diplayed in header table
    private String[] columnNames;

    // 2D array of values
    private String[][] data;

    // The table containing the data
    private ElementDatabase table;

    /**
     * @param mainGUI
     * @param interactionParams Names of columns, table's values and corresponding table
     */
    public CreateValuesTab(Object[] interactionParams){
        // Array of column names, diplayed in header table
        columnNames = (String[]) interactionParams[0];
        // 2D array of values
        data = (String[][]) interactionParams[1];
        // The table containing the data
        table = (ElementDatabase) interactionParams[2];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        // Get the node
        NodeModel progressingTreeNodeModel = (NodeModel) GUIMediator.gui().getNode(table).getUserObject();

        // Update the progress value of the model, end the progress
        progressingTreeNodeModel.childUpgradeCount = table.getCount();
        // Mark the node model as 'no stop/pause/resume button'
        progressingTreeNodeModel.isRunning = false;

        // Create a new table to display the values
        TablePanel newTableJPanel = new TablePanel(data, columnNames, GUIMediator.right());

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>();
        newTableJPanel.table.setRowSorter(rowSorter);

        Comparator<Object> c1 = new ColumnComparator();

        rowSorter.setModel(newTableJPanel.table.getModel());
        for(int i = 2 ; i < newTableJPanel.table.getColumnCount() ; i++)
            rowSorter.setComparator(i, c1);

        // Create a new tab: add header and table
        GUIMediator.right().addTab(table+" ",newTableJPanel);
        // Focus on the new tab
        GUIMediator.right().setSelectedComponent(newTableJPanel);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader();

        GUIMediator.right().setToolTipTextAt(GUIMediator.right().indexOfComponent(newTableJPanel),
                "<html><b>"+table.getParent()+"."+table+"</b><br>"+
                        "<i>"+StringTool.join(Arrays.copyOfRange(columnNames, 2, columnNames.length),"<br>")+"</i></html>");

        // Apply the custom header to the tab
        GUIMediator.right().setTabComponentAt(GUIMediator.right().indexOfComponent(newTableJPanel), header);
    }
}

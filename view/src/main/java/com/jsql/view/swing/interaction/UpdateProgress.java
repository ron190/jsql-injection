/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Refresh the progress bar of an element in the database tree.
 */
public class UpdateProgress implements InteractionCommand {
    
    /**
     * The element in the database tree to refresh.
     */
    private AbstractElementDatabase dataElementDatabase;

    /**
     * The index of progression.
     */
    private int dataCount;

    /**
     * @param interactionParams Element in the database tree and progression index
     */
    public UpdateProgress(Object[] interactionParams) {
        
        this.dataElementDatabase = (AbstractElementDatabase) interactionParams[0];

        this.dataCount = (Integer) interactionParams[1];
    }

    @Override
    public void execute() {
        
        MediatorHelper.treeDatabase().updateProgess(this.dataElementDatabase, this.dataCount);
    }
}

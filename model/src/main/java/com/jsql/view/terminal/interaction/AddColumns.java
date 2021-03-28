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
package com.jsql.view.terminal.interaction;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.bean.database.Column;
import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;

/**
 * Add the columns to corresponding table.
 */
public class AddColumns implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Columns retrieved by the view.
     */
    private List<Column> columns;

    /**
     * @param interactionParams List of columns retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddColumns(Object[] interactionParams) {
        
        // Get list of columns from the model
        this.columns = (List<Column>) interactionParams[0];
    }

    @Override
    public void execute() {
        
        LOGGER.info(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
        
        // Loop into the list of columns
        for (Column column: this.columns) {
            
            LOGGER.debug(column);
        }
    }
}

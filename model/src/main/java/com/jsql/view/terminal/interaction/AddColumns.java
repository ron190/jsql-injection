/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.terminal.interaction;

import com.jsql.model.bean.database.Column;
import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Add the columns to corresponding table.
 */
public class AddColumns implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Columns retrieved by the view.
     */
    private final List<Column> columns;

    /**
     * @param interactionParams List of columns retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddColumns(Object[] interactionParams) {
        this.columns = (List<Column>) interactionParams[0];  // Get list of columns from the model
    }

    @Override
    public void execute() {
        LOGGER.info(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
        for (Column column: this.columns) {
            LOGGER.debug(column);
        }
    }
}

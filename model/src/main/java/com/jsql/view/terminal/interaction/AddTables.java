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

import com.jsql.model.bean.database.Table;
import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Add the tables to the corresponding database.
 */
public class AddTables implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Tables retrieved by the view.
     */
    private final List<Table> tables;

    /**
     * @param interactionParams List of tables retrieved by the Model
     */
    @SuppressWarnings("unchecked")
    public AddTables(Object[] interactionParams) {
        this.tables = (List<Table>) interactionParams[0];
    }

    @Override
    public void execute() {
        LOGGER.info(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
        for (Table table: this.tables) {
            LOGGER.debug(table);
        }
    }
}

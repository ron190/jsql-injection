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

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;

/**
 * Create a new tab for the values.
 */
public class CreateValuesTab implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * 2D array of values.
     */
    private String[][] data;

    /**
     * @param interactionParams Names of columns, table's values and corresponding table
     */
    public CreateValuesTab(Object[] interactionParams) {
        
        // 2D array of values
        this.data = (String[][]) interactionParams[1];
    }

    @Override
    public void execute() {
        
        LOGGER.debug(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
        LOGGER.debug(() -> Arrays.deepToString(this.data));
    }
}

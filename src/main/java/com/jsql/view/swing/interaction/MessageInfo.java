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

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;

/**
 * Update the general information in status bar.
 */
public class MessageInfo implements InteractionCommand {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(MessageInfo.class);
    
    /**
     * @param nullParam
     */
    public MessageInfo(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.debug(
            "Database ["+ MediatorModel.model().nameDatabase +"] "
            + "on "+ MediatorModel.model().vendor +" ["+ MediatorModel.model().versionDatabase +"] "
            + "for user ["+ MediatorModel.model().username +"]"
        );
    }
}

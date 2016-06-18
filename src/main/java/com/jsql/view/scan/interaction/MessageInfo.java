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
package com.jsql.view.scan.interaction;

import org.apache.log4j.Logger;

import com.jsql.model.injection.MediatorModel;

/**
 * Update the general information in status bar.
 */
public class MessageInfo implements InteractionCommand {
    private static final Logger LOGGER = Logger.getLogger(MessageInfo.class);
    
    /**
     * @param nullParam
     */
    public MessageInfo(Object[] nullParam) {
        // Do nothing
    }

    @Override
    public void execute() {
        LOGGER.trace(MediatorModel.model().versionDatabase + ", Database: " + MediatorModel.model().currentDatabase + ", User: " + MediatorModel.model().currentUser);
    }
}

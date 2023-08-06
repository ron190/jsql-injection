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

import com.jsql.util.AnsiColorUtil;
import com.jsql.view.interaction.InteractionCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Create a new tab for the terminal.
 */
public class CreateShellTab implements InteractionCommand {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Full path of the shell file on remote host.
     */
    private final String path;

    /**
     * Url of the shell webpage on remote host.
     */
    private final String url;

    /**
     * @param interactionParams The local path and url for the shell
     */
    public CreateShellTab(Object[] interactionParams) {
        
        this.path = (String) interactionParams[0];
        this.url = (String) interactionParams[1];
    }

    @Override
    public void execute() {

        LOGGER.info(() -> AnsiColorUtil.addGreenColor(this.getClass().getSimpleName()));
    }
}

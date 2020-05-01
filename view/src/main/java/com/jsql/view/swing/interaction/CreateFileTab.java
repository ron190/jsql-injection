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

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Create a new tab for the file.
 */
public class CreateFileTab extends CreateTabHelper implements InteractionCommand {
    
    /**
     * Name of the file.
     */
    private String name;

    /**
     * Content of the file.
     */
    private String content;

    /**
     * Full path of the file.
     */
    private String path;

    /**
     * @param interactionParams Name, content and path of the file
     */
    public CreateFileTab(Object[] interactionParams) {
        
        this.name = (String) interactionParams[0];
        this.content = (String) interactionParams[1];
        this.path = (String) interactionParams[2];
    }

    @Override
    public void execute() {
        
        MediatorHelper.tabResults().createFileTab(this.name, this.content, this.path);
    }
}

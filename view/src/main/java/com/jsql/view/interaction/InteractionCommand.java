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
package com.jsql.view.interaction;

/**
 * Action ordered by the Model and applied to the View.
 */
@FunctionalInterface
public interface InteractionCommand {
    
    /**
     * Do the action ordered by the model.
     */
    void execute();
}

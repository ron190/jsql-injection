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
package com.jsql.model.bean.util;

/**
 * A request sent by the Model to the View in order to update the main window.
 * Used with the Observer pattern.
 */
public class Request {
    
    /**
     * Message identifier for the interaction.
     */
    private Interaction message;
    
    /**
     * List of custom parameters.
     */
    private Object[] parameters;
    
    // Getter and setter

    public Interaction getMessage() {
        return this.message;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public void setMessage(Interaction message) {
        this.message = message;
    }

    public void setParameters(Object... parameters) {
        this.parameters = parameters;
    }
}

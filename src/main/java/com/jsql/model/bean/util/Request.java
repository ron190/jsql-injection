/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
     * Class identifier for the interaction.
     */
    private Interaction message;
    
    /**
     * List of parameters.
     */
    private Object[] parameters;

    /**
     * Get identifier class name for this interaction.
     * @return Name of interaction
     */
    public Interaction getMessage() {
        return this.message;
    }

    /**
     * Get the list of custom parameters for this request.
     * @return List of parameter(s).
     */
    public Object[] getParameters() {
        return this.parameters;
    }

    /**
     * Identifier message for this request.
     * @param message Text identifier
     */
    public void setMessage(Interaction message) {
        this.message = message;
    }

    /**
     * Set custom parameters for the request.
     * @param parameters List of parameters
     */
    public void setParameters(Object... parameters) {
        this.parameters = parameters;
    }
    
}

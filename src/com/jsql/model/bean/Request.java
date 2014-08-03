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
package com.jsql.model.bean;

/**
 * A request sent by the Model to the View in order to update the main window.
 * Used with the Observer pattern.
 */
public class Request {
    private String message;
    private Object[] parameters;

    public String getMessage(){
        return message;
    }

    public Object getParameters(){
        return parameters;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setParameters(Object... parameters){
        this.parameters = parameters;
    }
}

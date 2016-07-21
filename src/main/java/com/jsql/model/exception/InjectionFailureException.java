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
package com.jsql.model.exception;

/**
 * Exception class thrown during initial step of injection (aka preparation).
 * Concerns every steps before the user can interact
 * with database elements (database, table, column)
 */
@SuppressWarnings("serial")
public class InjectionFailureException extends SlidingException {
    
    private String slidingWindowAllRows = "";
    
    public InjectionFailureException() {
        super("Execution stopped");
    }
    
    public InjectionFailureException(String message) {
        super(message);
    }
    
    public void setSlidingWindowAllRows(String slidingWindowAllRows) {
        this.slidingWindowAllRows = slidingWindowAllRows;
    }
    
    public String getSlidingWindowAllRows() {
        return this.slidingWindowAllRows;
    }
    
}

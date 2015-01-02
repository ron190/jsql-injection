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
package com.jsql.exception;

/**
 * Exception class thrown during initial step of injection (aka preparation).
 * Concerns every steps before the user can interact
 * with database elements (database, table, column)
 */
@SuppressWarnings("serial")
public class PreparationException extends Exception {
    
    public PreparationException() {
//        super("Execution stopped by user.");
        super("Execution stopped.");
    }
    
    public PreparationException(String message) {
        super(message);
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2013.
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
 * Exception class thrown during normal injection process,
 * concern every steps when user interact with database elements (database, table, column)
 */
@SuppressWarnings("serial")
public class StoppableException extends Exception {
	
    public StoppableException(){
        super("Execution stopped by user.");
    }
}

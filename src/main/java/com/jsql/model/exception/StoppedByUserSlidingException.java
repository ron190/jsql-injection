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
package com.jsql.model.exception;

/**
 * Exception class thrown during normal injection process,
 * concerns every steps when user interact with
 * database elements (database, table, column).
 */
@SuppressWarnings("serial")
public class StoppedByUserSlidingException extends SlidingException {

    public StoppedByUserSlidingException() {
        super("Stopped by user");
    }
    
    public StoppedByUserSlidingException(String slidingWindowAllRows) {
        super("Stopped by user", slidingWindowAllRows);
    }

    public StoppedByUserSlidingException(String slidingWindowAllRows, String slidingWindowCurrentRows) {
        super("Stopped by user", slidingWindowAllRows, slidingWindowCurrentRows);
    }

}

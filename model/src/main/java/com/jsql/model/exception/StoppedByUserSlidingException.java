/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.exception;

/**
 * Exception class thrown during normal injection process,
 * concerns every step when user interact with
 * database elements (database, table, column).
 */
public class StoppedByUserSlidingException extends AbstractSlidingException {
    
    private static final String STR_STOPPED_BY_USER = "Stopped by user";

    public StoppedByUserSlidingException() {
        super(StoppedByUserSlidingException.STR_STOPPED_BY_USER);
    }
    
    public StoppedByUserSlidingException(String slidingWindowAllRows) {
        super(StoppedByUserSlidingException.STR_STOPPED_BY_USER, slidingWindowAllRows);
    }

    public StoppedByUserSlidingException(String slidingWindowAllRows, String slidingWindowCurrentRows) {
        super(StoppedByUserSlidingException.STR_STOPPED_BY_USER, slidingWindowAllRows, slidingWindowCurrentRows);
    }
}

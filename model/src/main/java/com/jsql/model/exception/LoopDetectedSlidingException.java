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
package com.jsql.model.exception;

/**
 * Exception class thrown during normal injection process,
 * concerns every steps when user interact with
 * database elements (database, table, column).
 */
@SuppressWarnings("serial")
public class LoopDetectedSlidingException extends AbstractSlidingException {

    public LoopDetectedSlidingException(String slidingWindowAllRows, String slidingWindowCurrentRows) {
        
        super("Loop detected during injection, job stopped", slidingWindowAllRows, slidingWindowCurrentRows);
    }
}

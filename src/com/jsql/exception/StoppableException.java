package com.jsql.exception;

/**
 * Exception class thrown during normal injection process, 
 * concern every steps when user interact with database elements (database, table, column)
 */
public class StoppableException extends Exception {
    private static final long serialVersionUID = -3573501525824167565L;
    
    public StoppableException(){
        super("Execution stopped by user.");
    }
}

package com.jsql.exception;

/**
 * Exception class thrown during initial step of injection (aka preparation),
 * concern every steps before the user can interact with database elements (database, table, column)
 */
public class PreparationException extends Exception {
    private static final long serialVersionUID = -5602296831875522603L;
    
    public PreparationException(){
        super("Execution stopped by user.");
    }
    
    public PreparationException(String message){
        super(message);
    }
}
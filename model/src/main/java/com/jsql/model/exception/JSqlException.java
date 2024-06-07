package com.jsql.model.exception;

public class JSqlException extends Exception {
    
    public JSqlException(String message) {
        
        super(message);
    }

    public JSqlException(String message, Throwable e) {
        
        super(message, e);
    }

    public JSqlException(Throwable e) {

        super(null, e);  // get only original implicit reason
    }
}

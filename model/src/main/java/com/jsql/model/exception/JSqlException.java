package com.jsql.model.exception;

@SuppressWarnings("serial")
public class JSqlException extends Exception {
    
    public JSqlException(String message) {
        
        super(message);
    }

    public JSqlException(String message, Throwable e) {
        
        super(message, e);
    }
}

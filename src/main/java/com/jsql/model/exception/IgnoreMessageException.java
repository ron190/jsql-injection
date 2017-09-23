package com.jsql.model.exception;


// Instancier qu'une fois static
@SuppressWarnings("serial")
public class IgnoreMessageException extends Exception {

    public IgnoreMessageException(Exception e) {
        super(e);
    }
    
}

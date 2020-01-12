package com.jsql.model.exception;


@SuppressWarnings("serial")
public class IgnoreMessageException extends Exception {

    public IgnoreMessageException(Exception e) {
        super(e);
    }
    
}

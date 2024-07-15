package com.jsql.model.exception;

public class JSqlRuntimeException extends RuntimeException {

    public JSqlRuntimeException(String message) {
        super(message);
    }

    public JSqlRuntimeException(String message, Throwable e) {
        super(message, e);
    }

    public JSqlRuntimeException(Throwable e) {
        super(null, e);  // get only original implicit reason
    }
}

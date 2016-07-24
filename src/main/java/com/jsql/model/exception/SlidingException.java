package com.jsql.model.exception;

@SuppressWarnings("serial")
public abstract class SlidingException extends JSqlException {
    
    private String slidingWindowAllRows = "";
    
    public SlidingException(String message) {
        super(message);
    }
    
    public SlidingException(String message, Throwable e) {
        super(message, e);
    }

    public void setSlidingWindowAllRows(String slidingWindowAllRows) {
        this.slidingWindowAllRows = slidingWindowAllRows;
    }
    
    public String getSlidingWindowAllRows() {
        return this.slidingWindowAllRows;
    }
    
}

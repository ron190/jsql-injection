package com.jsql.model.exception;

@SuppressWarnings("serial")
public abstract class SlidingException extends Exception {
    
    public SlidingException(String a) {
        super(a);
    }
    
    private String slidingWindowAllRows;
    
    public void setSlidingWindowAllRows(String slidingWindowAllRows) {
        this.slidingWindowAllRows = slidingWindowAllRows;
    }
    
    public String getSlidingWindowAllRows() {
        return this.slidingWindowAllRows;
    }
    
}

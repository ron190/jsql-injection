package com.jsql.model.exception;

@SuppressWarnings("serial")
public abstract class SlidingException extends JSqlException {
    
    private String slidingWindowAllRows = "";
    
    private String slidingWindowCurrentRows = "";
    
    public SlidingException(String message) {
        super(message);
    }
    
    public SlidingException(String message, Throwable e) {
        super(message, e);
    }

    public void setSlidingWindowCurrentRows(String slidingWindowCurrentRows) {
        this.slidingWindowCurrentRows = slidingWindowCurrentRows;
    }
    
    public String getSlidingWindowCurrentRows() {
        return this.slidingWindowCurrentRows;
    }
    
    public void setSlidingWindowAllRows(String slidingWindowAllRows) {
        this.slidingWindowAllRows = slidingWindowAllRows;
    }
    
    public String getSlidingWindowAllRows() {
        return this.slidingWindowAllRows;
    }
    
}

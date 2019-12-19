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
    
    public SlidingException(String string, String slidingWindowAllRows) {
        this(string);
        this.slidingWindowAllRows = slidingWindowAllRows;
    }

    public SlidingException(String string, String slidingWindowAllRows, String slidingWindowCurrentRows) {
        this(string);
        this.slidingWindowAllRows = slidingWindowAllRows;
        this.slidingWindowCurrentRows = slidingWindowCurrentRows;
    }

    public String getSlidingWindowCurrentRows() {
        return this.slidingWindowCurrentRows;
    }
    
    public String getSlidingWindowAllRows() {
        return this.slidingWindowAllRows;
    }
    
}

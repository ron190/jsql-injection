package com.jsql.model.exception;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public abstract class SlidingException extends JSqlException {
    
    private String slidingWindowAllRows = StringUtils.EMPTY;
    
    private String slidingWindowCurrentRows = StringUtils.EMPTY;
    
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

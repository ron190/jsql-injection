package com.jsql.model.exception;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public abstract class SlidingException extends JSqlException {
    
    private final String slidingWindowAllRows;
    
    private final String slidingWindowCurrentRows;
    
    public SlidingException(String message) {
        
        super(message);
        
        this.slidingWindowAllRows = StringUtils.EMPTY;
        this.slidingWindowCurrentRows = StringUtils.EMPTY;
    }
    
    public SlidingException(String message, Throwable e) {
        
        super(message, e);
        
        this.slidingWindowAllRows = StringUtils.EMPTY;
        this.slidingWindowCurrentRows = StringUtils.EMPTY;
    }
    
    public SlidingException(String string, String slidingWindowAllRows) {
        
        super(string);
        
        this.slidingWindowCurrentRows = StringUtils.EMPTY;
        
        this.slidingWindowAllRows =
            StringUtils.isNotEmpty(slidingWindowAllRows)
            ? slidingWindowAllRows
            : StringUtils.EMPTY;
    }

    public SlidingException(String string, String slidingWindowAllRows, String slidingWindowCurrentRows) {
        
        super(string);
        
        this.slidingWindowAllRows =
            StringUtils.isNotEmpty(slidingWindowAllRows)
            ? slidingWindowAllRows
            : StringUtils.EMPTY;
        
        this.slidingWindowCurrentRows =
            StringUtils.isNotEmpty(slidingWindowCurrentRows)
            ? slidingWindowCurrentRows
            : StringUtils.EMPTY;
    }

    public String getSlidingWindowCurrentRows() {
        return this.slidingWindowCurrentRows;
    }
    
    public String getSlidingWindowAllRows() {
        return this.slidingWindowAllRows;
    }
}

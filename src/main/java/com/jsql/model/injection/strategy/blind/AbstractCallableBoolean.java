package com.jsql.model.injection.strategy.blind;

import java.util.concurrent.Callable;

/**
 * Callable used to read blind/time database information.
 */
public abstract class AbstractCallableBoolean<T extends AbstractCallableBoolean<T>> implements Callable<T> {
    
    /**
     * The URL called.
     */
    protected String blindUrl;
    
    /**
     * Character position.
     */
    protected int currentIndex;
    
    /**
     * Bit searched.
     */
    protected int currentBit;

    /**
     * Default call used for bit test.
     */
    protected boolean isTestingLength = false;
    
    /**
     * Get state of this callable.
     * @return True if callable is testing length of result, false otherwise
     */
    public boolean isTestingLength() {
        return this.isTestingLength;
    }
    
    /**
     * Get index of this callable.
     */
    public int getCurrentIndex() {
        return this.currentIndex;
    }
    
    /**
     * Get bit searched by this callable.
     * @return
     */
    public int getCurrentBit() {
        return this.currentBit;
    }
    
    /**
     * Check if a response time means the SQL query is true.
     * @return true if the current SQL test is confirmed
     */
    abstract boolean isTrue();
    
}

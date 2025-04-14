package com.jsql.model.injection.strategy.blind;

import java.util.concurrent.Callable;

/**
 * Callable used to read blind/time database information.
 */
public abstract class AbstractCallableBit<T extends AbstractCallableBit<T>> implements Callable<T> {
    
    /**
     * The URL called.
     */
    protected String booleanUrl;
    
    /**
     * Character position.
     */
    protected int currentIndex;
    
    /**
     * Bit searched.
     */
    protected int currentBit;
    int idPage = -1;
    int block = -1;

    /**
     * Default call used for bit test.
     */
    protected boolean isMultibit = false;
    protected boolean isBinary = false;

    /**
     * Character representation of Boolean bits
     */
    protected String charText;
    
    /**
     * Check if a response time means the SQL query is true.
     * @return true if the current SQL test is confirmed
     */
    public abstract boolean isTrue();
    
    public int getCurrentIndex() {
        return this.currentIndex;
    }
    
    public int getCurrentBit() {
        return this.currentBit;
    }

    public String getCharText() {
        return this.charText;
    }

    public boolean isMultibit() {
        return this.isMultibit;
    }

    public boolean isBinary() {
        return this.isBinary;
    }
}

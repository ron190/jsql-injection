package com.jsql.model.blind;

import java.util.List;
import java.util.concurrent.Callable;

import com.jsql.model.blind.diff_match_patch.Diff;

/**
 * Callable used to read blind/time database information.
 */
public abstract class AbstractBlindCallable implements Callable<AbstractBlindCallable> {
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
    protected boolean isLengthTest = false;
    
    /**
     * Check if a response time means the SQL query is true.
     * @return true if the current SQL test is confirmed
     */
    abstract boolean isTrue();

    /**
     * Get blind diffs against a true page.
     * @return List of diffs
     */
    abstract List<Diff> getOpcodes();
    
    /**
     * Get state of this callable.
     * @return True if callable is testing length of result, false otherwise
     */
    public boolean isLengthTest() {
        return this.isLengthTest;
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
}

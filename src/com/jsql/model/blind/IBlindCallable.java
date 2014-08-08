package com.jsql.model.blind;

import java.util.List;
import java.util.concurrent.Callable;

import com.jsql.model.blind.diff_match_patch.Diff;

public interface IBlindCallable extends Callable<IBlindCallable> {
    /**
     * Check if a response time means the SQL query is true.
     * @return true if the current SQL test is confirmed
     */
    boolean isTrue();

    boolean isLengthTest();
    
    int getCurrentIndex();
    
    int getCurrentBit();

    List<Diff> getOpcodes();
}

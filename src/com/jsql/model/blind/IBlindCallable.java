package com.jsql.model.blind;

import java.util.concurrent.Callable;

public interface IBlindCallable<V> extends Callable<V> {
    /**
     * Check if a response time means the SQL query is true,
     * @return true if the current SQL test is confirmed
     */
    public boolean isTrue();

	public boolean getisLengthTest();
	
	public int getCurrentIndex();
	
	public int getCurrentBit();
}

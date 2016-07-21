package com.jsql.model.injection.strategy;

import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Define a strategy to inject SQL with methods like errorbased or timebased.
 */
public abstract class AbstractStrategy {
    /**
     * True if injection can be used, false otherwise.
     */
    protected boolean isApplicable = false;

    /**
     * Return if this strategy can be used to inject SQL.
     * @return True if strategy can be used, false otherwise.
     */
    public boolean isApplicable() {
        return isApplicable;
    }

    /**
     * Test if this strategy can be used to inject SQL.
     * @return
     * @throws InjectionFailureException
     * @throws StoppedByUserException
     */
    public abstract void checkApplicability() throws InjectionFailureException, StoppedByUserException;
    
    /**
     * Inform the view that this strategy can be used.
     */
    protected abstract void allow();
    
    /**
     * Inform the view that this strategy can't be used.
     */
    protected abstract void unallow();
    
    /**
     * Start the strategy work.
     * @return Source code
     */
    public abstract String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserException;
    
    /**
     * Change the strategy of the model to current strategy.
     */
    public abstract void activateStrategy();
    
    /**
     * Get number of characters you can obtain from the strategy.
     */
    public abstract String getPerformanceLength();
    
    /**
     * Get the injection strategy name.
     */
    public abstract String getName();
}

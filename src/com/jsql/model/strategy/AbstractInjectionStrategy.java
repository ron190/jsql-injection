package com.jsql.model.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.injection.suspendable.AbstractSuspendable;

/**
 * Define a strategy to inject SQL with methods like errorbased or timebased.
 */
public abstract class AbstractInjectionStrategy {
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
     * @throws PreparationException
     * @throws StoppableException 
     */
    protected abstract void checkApplicability() throws PreparationException, StoppableException;
    
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
    public abstract String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException;
    
    /**
     * Change the strategy of the model to current strategy.
     */
    protected abstract void applyStrategy();
    
    /**
     * Get number of characters you can obtain from the strategy.
     */
    public abstract String getPerformanceLength();
    
    /**
     * Get the injection strategy name.
     */
    public abstract String getName();
}

package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.Suspendable;

/**
 * Define a strategy to inject SQL with methods like errorbased or timebased.
 */
public interface IInjectionStrategy {
    /**
     * Return if this strategy can be used to inject SQL.
     * @return True if strategy can be used, false otherwise.
     */
    boolean isApplicable();

    /**
     * Test if this strategy can be used to inject SQL.
     * @return
     * @throws PreparationException
     */
    void checkApplicability() throws PreparationException;
    
    /**
     * Inform the view that this strategy can't be used.
     */
    void activate();
    
    /**
     * Inform the view that this strategy can be used.
     */
    void deactivate();
    
    /**
     * Start the strategy work.
     * @return Source code
     */
    String inject(String sqlQuery, String startPosition, Suspendable stoppable) throws StoppableException;
    
    /**
     * Change the strategy of the model to current strategy.
     */
    void applyStrategy();
}

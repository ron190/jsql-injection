package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.interruptable.Interruptable;
import com.jsql.model.interruptable.Stoppable;

/**
 * Define a strategy to inject SQL with methods like errorbased or timebased
 */
public interface IInjectionStrategy {
	/**
	 * Return if this strategy can be used to inject SQL
	 * @return
	 */
	boolean isApplicable();

	/**
	 * Test if this strategy can be used to inject SQL
	 * @return
	 * @throws PreparationException 
	 */
	void checkApplicability() throws PreparationException;
	
	/**
	 * Inform the view that this strategy can't be used 
	 */
	void activate();
	
	/**
	 * Inform the view that this strategy can be used
	 */
	void deactivate();
	
	/**
	 * Start the strategy work
	 */
	String inject(String sqlQuery, String startPosition, Interruptable interruptable, Stoppable stoppable) throws StoppableException;
	
	/**
	 * Change the strategy of the model to current strategy
	 */
	void applyStrategy();
}

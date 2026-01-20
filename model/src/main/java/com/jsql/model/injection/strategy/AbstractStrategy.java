package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Define a strategy to inject SQL with methods like Error and Time.
 */
public abstract class AbstractStrategy {

    private static final Logger LOGGER = LogManager.getRootLogger();

    protected static final String KEY_LOG_CHECKING_STRATEGY = "LOG_CHECKING_STRATEGY";
    protected static final String KEY_LOG_VULNERABLE = "LOG_VULNERABLE";
    protected static final String FORMAT_STRATEGY_NOT_IMPLEMENTED = "Strategy [{}] for [{}] not implemented, share a working example on GitHub to speed up release";
    protected static final String FORMAT_SKIP_STRATEGY_DISABLED = "Skipping strategy [{}] disabled";
    protected static final String FORMAT_CHECKING_STRATEGY = "{} [{}]...";

    /**
     * True if injection can be used, false otherwise.
     */
    protected boolean isApplicable = false;

    protected final InjectionModel injectionModel;
    
    protected AbstractStrategy(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    /**
     * Test if this strategy can be used to inject SQL.
     */
    public abstract void checkApplicability() throws JSqlException;
    
    /**
     * Inform the view that this strategy can be used.
     */
    protected abstract void allow(int... i);
    
    /**
     * Inform the view that this strategy can't be used.
     */
    protected abstract void unallow(int... i);
    
    /**
     * Start the strategy work.
     * @return Source code
     */
    public abstract String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException;
    
    /**
     * Change model strategy to current applicable strategy only when not already set.
     * Union > Stacked > Error > Multibit > Blind > Time
     */
    public abstract void activateWhenApplicable();
    
    /**
     * Get number of characters you can obtain from the strategy.
     */
    public abstract String getPerformanceLength();
    
    /**
     * Get the injection strategy name.
     */
    public abstract String getName();

    public void logChecking() {
        LOGGER.log(
            LogLevelUtil.CONSOLE_DEFAULT,
            AbstractStrategy.FORMAT_CHECKING_STRATEGY,
            () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_CHECKING_STRATEGY),
            this::getName
        );
    }
    
    @Override
    public String toString() {
        return this.getName();
    }


    // Getter and setter
    
    public boolean isApplicable() {
        return this.isApplicable;
    }
    
    public void setApplicable(boolean isApplicable) {
        this.isApplicable = isApplicable;
    }
}

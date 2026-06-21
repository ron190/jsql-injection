package com.jsql.model.injection.strategy.blind.callable;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.strategy.blind.InjectionTime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * diffSeconds represents the response time of the current page
 */
public class CallableTime extends AbstractCallableBit<CallableTime> {
    
    /**
     * Current page loading time.
     */
    private long diffSeconds;

    private final InjectionModel injectionModel;
    
    private final InjectionTime injectionTime;
    private final String metadataInjectionProcess;
    
    /**
     * Constructor for preparation and blind confirmation.
     */
    public CallableTime(
        String sqlQuery,
        InjectionModel injectionModel,
        InjectionTime injectionTime,
        BlindOperator blindOperator,
        String metadataInjectionProcess
    ) {
        this.injectionModel = injectionModel;
        this.injectionTime = injectionTime;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorEngine().getEngine().instance().sqlTestTimeWithOperator(sqlQuery, blindOperator);
    }
    
    /**
     * Constructor for bit test.
     */
    public CallableTime(
        String sqlQuery,
        int indexChar,
        int bit,
        InjectionModel injectionModel,
        InjectionTime injectionTime,
        BlindOperator blindOperator,
        String metadataInjectionProcess
    ) {
        this(sqlQuery, injectionModel, injectionTime, blindOperator, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorEngine().getEngine().instance().sqlTimeBit(sqlQuery, indexChar, bit, blindOperator);
        this.currentIndex = indexChar;
        this.currentBit = bit;
    }
    
    @Override
    public boolean isTrue() {
        return this.diffSeconds < this.injectionTime.getSleepTime();
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Calculate the response time of the current page.
     * @return Functional Time callable
     */
    @Override
    public CallableTime call() {
        var timeInMillisOnStart = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC);
        this.injectionTime.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        var timeInMillisOnEnd = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC);
        this.diffSeconds = timeInMillisOnEnd - timeInMillisOnStart;
        return this;
    }
}

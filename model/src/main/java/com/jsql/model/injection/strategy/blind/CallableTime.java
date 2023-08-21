package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;

import java.util.Calendar;
import java.util.Date;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * diffSeconds represents the response time of the current page
 */
public class CallableTime extends AbstractCallableBoolean<CallableTime> {
    
    /**
     * Time before the url call.
     */
    private final Calendar calendar1 = Calendar.getInstance();
    
    /**
     * Time at the end of the url call.
     */
    private final Calendar calendar2 = Calendar.getInstance();
    
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
    public CallableTime(String sqlQuery, InjectionModel injectionModel, InjectionTime injectionTime, BooleanMode blindMode, String metadataInjectionProcess) {
        
        this.injectionModel = injectionModel;
        this.injectionTime = injectionTime;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTimeTest(sqlQuery, blindMode);
    }
    
    /**
     * Constructor for bit test.
     */
    public CallableTime(String sqlQuery, int indexCharacter, int bit, InjectionModel injectionModel, InjectionTime injectionTime, BooleanMode blindMode, String metadataInjectionProcess) {
        
        this(sqlQuery, injectionModel, injectionTime, blindMode, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlBitTestTime(sqlQuery, indexCharacter, bit, blindMode);
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }
    
    @Override
    public boolean isTrue() {
        
        return this.diffSeconds < this.injectionModel.getMediatorUtils().getPreferencesUtil().countSleepTimeStrategy();
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Calculate the response time of the current page.
     * @return Functional Time callable
     */
    @Override
    public CallableTime call() {
        
        this.calendar1.setTime(new Date());
        this.injectionTime.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.calendar2.setTime(new Date());
        
        long milliseconds1 = this.calendar1.getTimeInMillis();
        long milliseconds2 = this.calendar2.getTimeInMillis();
        
        long diff = milliseconds2 - milliseconds1;
        
        this.diffSeconds = diff / 1000;
        
        return this;
    }
}

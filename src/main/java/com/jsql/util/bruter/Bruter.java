package com.jsql.util.bruter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jsql.model.exception.IgnoreMessageException;

public class Bruter {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    protected List<String> characters = new ArrayList<>();
     
    protected boolean found = false;
     
    protected int maxLength;
    protected int minLength;
     
    protected int count;
     
    protected long starttime;
    protected long endtime;
     
    private static final char[] specialCharacters = {
        '~', '`', '!', '@', '#', '$', '%', '^',
        '&', '*', '(', ')', '_', '-', '+', '=', '{', '}', '[', ']', '|', '\\',
        ';', ':', '\'', '"', '<', '.', ',', '>', '/', '?', ' '
    };
     
    protected boolean done = false;
    protected boolean paused = false;

    public long getRemainder() {
        return this.getNumberOfPossibilities() - this.count;
    }

    public long getNumberOfPossibilities() {
        
        long possibilities = 0;
        
        for (int i = this.minLength; i <= this.maxLength; i++) {
            
            possibilities += (long) Math.pow(this.characters.size(), i);
        }
        
        return possibilities;
    }

    public void addLowerCaseLetters() {
        
        for (char c = 'a'; c <= 'z'; c++) {
            this.characters.add(String.valueOf(c));
        }
    }

    public void addDigits() {
        
        for (int c = 0; c <= 9; c++) {
            this.characters.add(String.valueOf(c));
        }
    }

    public void addUpperCaseLetters() {
        
        for (char c = 'A'; c <= 'Z'; c++) {
            this.characters.add(String.valueOf(c));
        }
    }

    public void addSpecialCharacters() {
        
        for (char c : specialCharacters) {
            this.characters.add(String.valueOf(c));
        }
    }

    public void excludeChars(String s) {
        
        char[] arrayChars = s.toCharArray();
        
        for (char arrayChar : arrayChars) {
            this.characters.remove(Character.toString(arrayChar));
        }
    }

    public int getPerSecond() {
        
        int i;
        
        try {
            
            i = (int) (this.count / this.calculateTimeDifference());
            
        } catch (Exception e) {
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
            
            // Ignore division by zero
            return 0;
        }
        
        return i;
    }

    public String calculateTimeElapsed() {
        
        long timeTaken = this.calculateTimeDifference();
        
        int seconds = (int) timeTaken;
        
        int minutes = 0;
        int hours = 0;
        int days = 0;
        
        minutes = seconds / 60;
        seconds = seconds % 60;
        hours = minutes / 60;
        minutes = minutes % 60;
        days = hours / 24;
        hours = hours % 24;
        
        return "Time elapsed: "+ days +"days "+ hours +"h "+ minutes +"min "+ seconds +"s";
    }

    private long calculateTimeDifference() {
        
        return (long) ((this.endtime - this.starttime) * (1 * Math.pow(10, -9)));
    }
    
    // Getter and setter

    public synchronized void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public boolean isFound() {
        return this.found;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public int getCounter() {
        return this.count;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public void setIsDone(Boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return this.done;
    }
}

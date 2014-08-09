package com.jsql.view.bruteforce;

import java.util.ArrayList;

public class Bruter {

    public ArrayList<String> characters = new ArrayList<String>();
    public boolean found = false;
    public int maxLength;
    public int minLength;
    public int count;
    long starttime, endtime;
    public int minutes, seconds, hours, days;
    public char[] specialCharacters = {'~', '`', '!', '@', '#', '$', '%', '^',
        '&', '*', '(', ')', '_', '-', '+', '=', '{', '}', '[', ']', '|', '\\',
        ';', ':', '\'', '"', '<', '.', ',', '>', '/', '?', ' '};
    public boolean done = false;
    public boolean paused = false;

    public boolean isFound() {
        return found;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public synchronized void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getCounter() {
        return count;
    }

    public long getRemainder() {
        return getNumberOfPossibilities() - count;
    }

    public long getNumberOfPossibilities() {
        long possibilities = 0;
        for (int i = minLength; i <= maxLength; i++) {
            possibilities += (long) Math.pow(characters.size(), i);
        }
        return possibilities;
    }

    public void addExtendedSet() {
        for (char c = (char) 0; c <= (char) 31; c++) {
            characters.add(String.valueOf(c));
        }
    }

    public void addStandardCharacterSet() {
        for (char c = (char) 32; c <= (char) 127; c++) {
            characters.add(String.valueOf(c));
        }
    }

    public void addLowerCaseLetters() {
        for (char c = 'a'; c <= 'z'; c++) {
            characters.add(String.valueOf(c));
        }
    }

    public void addDigits() {
        for (int c = 0; c <= 9; c++) {
            characters.add(String.valueOf(c));
        }
    }

    public void addUpperCaseLetters() {
        for (char c = 'A'; c <= 'Z'; c++) {
            characters.add(String.valueOf(c));
        }
    }

    public void addSpecialCharacters() {
        for (char c : specialCharacters) {
            characters.add(String.valueOf(c));
        }
    }

    public void setMaxLength(int i) {
        maxLength = i;
    }

    public void setMinLength(int i) {
        minLength = i;
    }

    public int getPerSecond() {
        int i;
        try {
            i = (int) (getCounter() / calculateTimeDifference());
        } catch (Exception ex) {
            return 0;
        }
        return i;

    }

    public String calculateTimeElapsed() {
        long timeTaken = calculateTimeDifference();
        seconds = (int) timeTaken;
        if (seconds > 60) {
            minutes = (int) (seconds / 60);
            if (minutes * 60 > seconds) {
                minutes = minutes - 1;
            }

            if (minutes > 60) {
                hours = (int) minutes / 60;
                if (hours * 60 > minutes) {
                    hours = hours - 1;
                }
            }

            if (hours > 24) {
                days = (int) hours / 24;
                if (days * 24 > hours) {
                    days = days - 1;
                }
            }
            seconds -= (minutes * 60);
            minutes -= (hours * 60);
            hours -= (days * 24);
            days -= (hours * 24);
        }
        return "Time elapsed: " + days + "days " + hours + "h " + minutes + "min " + seconds + "s";
    }

    private long calculateTimeDifference() {
        long timeTaken = (long) ((endtime - starttime) * (1 * Math.pow(10, -9)));
        return timeTaken;
    }

    public boolean excludeChars(String s) {
        char[] arrayChars = s.toCharArray();
        for (int i = 0; i < arrayChars.length; i++) {
            characters.remove(arrayChars[i] + "");
        }
        if (characters.size() < maxLength) {
            return false;
        } else {
            return true;

        }
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setIsDone(Boolean b) {
        done = b;
    }

    public boolean isDone() {
        return done;
    }
}

package com.jsql.mvc.model;

/**
 * Runnable class, allow stop/pause/resume features for each parallelized tasks,
 * Method action() is paused/stopped every time a loop checks is own state with isInterrupted() (holy sh*t, fuck oral language),
 * How to use: subclass Interruptable, define the abstract method action() with pausable task, then start it with begin()
 */
public abstract class Interruptable implements Runnable {
    public Boolean stopFlag = false;
    public boolean suspendFlag = false;

    public boolean isInterrupted(){
        synchronized(this) {
            while(suspendFlag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(stopFlag){
                return true;
            }else{
                return false;
            }
        }
    }
    
    @Override
    public void run() {
        action();
    }
    
    public void begin(){
        Thread t = new Thread(this, "Interruptable - begin");
        t.start();
    }
    
    public synchronized void myresume() {
        notify();
    }
    
    public abstract void action(Object... args);
}

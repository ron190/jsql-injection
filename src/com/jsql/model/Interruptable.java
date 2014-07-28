/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model;

import com.jsql.view.GUIMediator;


/**
 * An action class that allows stop/pause/resume features for each parallelized tasks (mainly for tree actions).
 * An interruptable wake up a stoppable
 * Method action() is paused/stopped every time a loop checks is own state with PAUSEshouldStopPAUSE(),
 * How to use: subclass Interruptable, define the abstract method action() with pausable task, then start it with begin()
 */
public abstract class Interruptable implements Runnable {
    
    // Make the action to stop if true
    private boolean stopFlag = false;
    
    // Make the action to pause if true, else make it unpause
    private boolean pauseFlag = false;

    /**
     * Thread's states Pause and Stop are processed by this method:
     * - Pause action in infinite loop if invoked while pauseFlag is set to true
     * - Return stop state
     * @return Stop state
     */
    public boolean PAUSEshouldStopPAUSE(){
        synchronized(this) {
            
            // Make application loop until pauseFlag is set to true
            while(pauseFlag) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    GUIMediator.model().sendDebugMessage(e);
                }
            }
            
            // Return true if stop requested, return false otherwise
            return stopFlag;
        }
    }
    
    /**
     * Start the runnable by method start() invocation (Thread/Runnable pattern).
     * Doesn't called directly.
     */
    @Override
    public void run() {
        this.action();
    }

    /**
     * Start the thread.
     * Called directly.
     */
    public void begin(){
        Thread thread = new Thread(this, "Interruptable - begin");
        thread.start();
    }

    /**
     * Mark as stopped.
     */
    public void stop(){
        this.stopFlag = true;
    }
    
    /**
     * Mark as paused.
     */
    public void pause(){
        this.pauseFlag = true;
    }
    
    /**
     * Mark as unpaused.
     */
    public void unPause(){
        this.pauseFlag = false;
    }
    
    /**
     * Return true if thread is paused, false otherwise.
     * @return Pause state
     */
    public boolean isPaused(){
        return this.pauseFlag;
    }
    
    /**
     * Un-wait the thread.
     */
    public synchronized void resume() {
        this.notify();
    }
    
    /**
     * The pausable/stoppable action.
     */
    protected abstract void action();
}

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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;

/**
 * Runnable class, can stop preparation, whereas Interruptable also provides pause/resume.
 * Execution should be stopped after a state check with isPreparationStopped(),
 * How to use: subclass Stoppable, define the abstract method action() with task that can be stopped, then start it with begin()
 */
public abstract class Stoppable implements Runnable {
    
    InjectionModel model;
    
    Interruptable interruptable;
    
    private String errorResponse;
    
    private String threadResponse = "";
    
    public Stoppable(InjectionModel model){
        this.model = model;
    }
    
    /**
     * Starting from the view, the controller calls selectDatabase() that creates an Interruptable,
     * which call Stoppable_loopIntoResults(), so Interruptable must go through Stoppable in order to
     * let the view pause itself
     * #Need rework
     * @param model needed for accessing the stopFlag
     * @param interruptable pass it for the view pause/resume
     */
    public Stoppable(InjectionModel model, Interruptable interruptable){
        this.model = model;
        this.interruptable = interruptable;
    }

    /**
     * Return true if stop requested, return false otherwise
     * @return
     */
    public boolean shouldStop(){
        synchronized(this) {
            return model.stopFlag;
        }
    }
    
    /**
     * Start the job defined by action(), and wait for it to finish
     * @return source page
     * @throws PreparationException if action throws exception
     */
    public String begin() throws PreparationException{
        Thread t = new Thread(this, "Stoppable - begin");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            this.model.sendDebugMessage(e);
            model.sendErrorMessage("Current thread was interrupted while waiting.");
        }
        
        if(this.errorResponse != null){
            throw new PreparationException(this.errorResponse);
        }else{
            return this.threadResponse;
        }
    }
    
    /**
     * Runnable default job, run action() defined by subclass,
     * Take care of result and error coming from action()
     */
    @Override
    public void run() {
        
        try {
            this.threadResponse = action();
        } catch (PreparationException e) {
            this.errorResponse = e.getMessage();
        } catch (StoppableException e) {
            this.errorResponse = e.getMessage();
        }
        
    }
    
    abstract String action(Object... args) throws PreparationException, StoppableException;
}

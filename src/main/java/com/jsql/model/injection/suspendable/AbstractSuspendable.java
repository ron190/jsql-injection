package com.jsql.model.injection.suspendable;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.injection.MediatorModel;

/**
 * A thread used to inject database ; stoppable and pausable.
 */
public abstract class AbstractSuspendable /*implements Runnable*/ {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractSuspendable.class);

    /**
     * Make the action to stop if true.
     */
    private boolean shouldStopThread = false;
    
    /**
     * Make the action to pause if true, else make it unpause.
     */
    private boolean shouldPauseThread = false;

    /**
     * Thread's states Pause and Stop are processed by this method.<br>
     * - Pause action in infinite loop if invoked while shouldPauseThread is set to true,<br>
     * - Return stop state.
     * @return Stop state
     */
    public synchronized boolean shouldSuspend() {
        // Make application loop until shouldPauseThread is set to false by another user action
        while (shouldPauseThread) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            }
        }
        
        // Return true if stop requested, return false otherwise
        return shouldStopThread || MediatorModel.model().shouldStopAll;
    }
    
    /**
     * Mark as stopped.
     */
    public void stop() {
        this.shouldStopThread = true;
    }
    
    /**
     * Mark as paused.
     */
    public void pause() {
        this.shouldPauseThread = true;
    }
    
    /**
     * Mark as unpaused.
     */
    public void unpause() {
        this.shouldPauseThread = false;
    }
    
    /**
     * Return true if thread is paused, false otherwise.
     * @return Pause state
     */
    public boolean isPaused() {
        return this.shouldPauseThread;
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
    public abstract String run(Object... args) throws PreparationException, StoppableException;
}

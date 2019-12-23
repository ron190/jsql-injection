package com.jsql.model.suspendable;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;

/**
 * A thread used to inject database ; stoppable and pausable.
 */
public abstract class AbstractSuspendable<T> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Make the action to stop if true.
     */
    private boolean isStopped = false;
    
    /**
     * Make the action to pause if true, else make it unpause.
     */
    private boolean isPaused = false;

    public AbstractSuspendable(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Thread's states Pause and Stop are processed by this method.<br>
     * - Pause action in infinite loop if invoked while shouldPauseThread is set to true,<br>
     * - Return stop state.
     * @return Stop state
     */
    public synchronized boolean isSuspended() {
        // Make application loop until shouldPauseThread is set to false by another user action
        while (this.isPaused) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                LOGGER.error("Interruption while suspendable is waiting", e);
                Thread.currentThread().interrupt();
            }
        }
        
        // Return true if stop requested, return false otherwise
        return this.isStopped || this.injectionModel.isStoppedByUser();
    }
    
    /**
     * Mark as stopped.
     */
    public void stop() {
        this.unpause();
        this.isStopped = true;
    }
    
    /**
     * Mark as paused.
     */
    public void pause() {
        this.isPaused = true;
    }
    
    /**
     * Mark as unpaused.
     */
    public void unpause() {
        this.isPaused = false;
        
        // Restart the action after an unpause
        this.resume();
    }
    
    /**
     * Return true if thread is paused, false otherwise.
     * @return Pause state
     */
    public boolean isPaused() {
        return this.isPaused;
    }
    
    /**
     * Un-wait the thread.
     */
    public synchronized void resume() {
        this.notifyAll();
    }
    
    /**
     * The pausable/stoppable action.
     * @param args
     * @return
     * @throws JSqlException
     */
    public abstract T run(Object... args) throws JSqlException;
    
}

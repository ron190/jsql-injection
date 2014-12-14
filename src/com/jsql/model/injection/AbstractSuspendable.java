package com.jsql.model.injection;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;

/**
 * A thread used to inject database ; stoppable and pausable.
 */
public abstract class AbstractSuspendable implements Runnable{
    /**
     * Make the action to stop if true.
     */
    private boolean stopFlag = false;
    
    /**
     * Make the action to pause if true, else make it unpause.
     */
    private boolean pauseFlag = false;

    /**
     * Error message.
     */
    private String errorResponse;
    
    /**
     * PAge source code as Hex string.
     */
    private String threadResponse = "";
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractSuspendable.class);

    /**
     * Thread's states Pause and Stop are processed by this method.
     * - Pause action in infinite loop if invoked while pauseFlag is set to true,<br>
     * - Return stop state.
     * @return Stop state
     */
    public boolean stopOrPause() {
        synchronized (this) {
            
            // Make application loop until pauseFlag is set to true
            while (pauseFlag) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    LOGGER.error(e, e);
                }
            }
            
            // Return true if stop requested, return false otherwise
            return stopFlag || MediatorModel.model().stopFlag;
        }
    }
    
    /**
     * Runnable default job.
     * Run action() defined by subclass,
     * Start the runnable by method start() invocation (Thread/Runnable pattern).
     * Doesn't called directly.
     * Take care of result and error coming from action()
     */
    @Override
    public void run() {
        try {
            this.threadResponse = this.action();
        } catch (PreparationException e) {
            this.errorResponse = e.getMessage();
        } catch (StoppableException e) {
            this.errorResponse = e.getMessage();
        }
    }
    
    /**
     * Start the job defined by action(), and wait for it to finish.
     * @return source page
     * @throws PreparationException if action throws exception
     */
    public String beginSynchrone() throws PreparationException {
        Thread t = new Thread(this, "Stoppable - begin");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }
        
        if (this.errorResponse != null) {
            throw new PreparationException(this.errorResponse);
        } else {
            return this.threadResponse;
        }
    }

    /**
     * Mark as stopped.
     */
    public void stop() {
        this.stopFlag = true;
    }
    
    /**
     * Mark as paused.
     */
    public void pause() {
        this.pauseFlag = true;
    }
    
    /**
     * Mark as unpaused.
     */
    public void unPause() {
        this.pauseFlag = false;
    }
    
    /**
     * Return true if thread is paused, false otherwise.
     * @return Pause state
     */
    public boolean isPaused() {
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
    public abstract String action(Object... args) throws PreparationException, StoppableException;
}

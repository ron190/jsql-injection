/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.util;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Utility class managing running threads on which the user can act.
 * It must be noted that as SwingWorker class are used then only 10 jobs can be run
 * at the same time, the 11th will be waiting in the thread pool until one of the 10
 * actives one is freed.
 */
public final class ThreadUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * List of running jobs associated to a database injection task.
     * We can interact with those tasks in order to pause/resume and stop the process.
     */
    private final Map<AbstractElementDatabase, AbstractSuspendable<?>> suspendables = new HashMap<>();
    
    InjectionModel injectionModel;
    // Utility class
    public ThreadUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    /**
     * Add a job to the list of ongoing tasks. It is used to allow the user to act
     * on the job and stop/pause a running process.
     * @param elementDatabase component associated to the active job
     * @param suspendable active job to act on
     */
    public void put(AbstractElementDatabase elementDatabase, AbstractSuspendable<String> suspendable) {
        this.injectionModel.getMediatorUtils().getThreadUtil().suspendables.put(elementDatabase, suspendable);
    }
    
    /**
     * Get the task associated to the database component.
     * It's usually done to act on the task like stop/pause the corresponding process, or
     * to check the status of the job.
     * @param elementDatabase component associated to the active job
     * @return job currently running
     */
    public AbstractSuspendable<?> get(AbstractElementDatabase elementDatabase) {
        return this.injectionModel.getMediatorUtils().getThreadUtil().suspendables.get(elementDatabase);
    }
    
    /**
     * Remove the thread corresponding to the component in order to be
     * garbage collected. The thread should be stopped prior the deletion.
     * @param elementDatabase component associated to thread
     */
    public void remove(AbstractElementDatabase elementDatabase) {
        this.injectionModel.getMediatorUtils().getThreadUtil().suspendables.remove(elementDatabase);
    }
    
    /**
     * Force to stop every threads still running and empty the list where
     * they were instantiated in order to be garbage collected.
     */
    public void reset() {
        // Fix #8258: ConcurrentModificationException on java.util.HashMap$ValueIterator.next()
        try {
            for (AbstractSuspendable<?> suspendable : this.injectionModel.getMediatorUtils().getThreadUtil().suspendables.values()) {
                suspendable.stop();
            }
            this.injectionModel.getMediatorUtils().getThreadUtil().suspendables.clear();
        } catch (ConcurrentModificationException e) {
            LOGGER.error(e, e);
        }
    }
    
}

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

import java.util.HashMap;
import java.util.Map;

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
     * List of running jobs associated to a database injection task.
     * We can interract with those tasks in order to pause/resume and stop the process.
     */
    private static final Map<AbstractElementDatabase, AbstractSuspendable<?>> suspendables = new HashMap<>();
    
    // Utility class
    private ThreadUtil() {
        // not called
    }

    /**
     * Add a job to the list of ongoing tasks. It is used to allow the user to act
     * on the job and stop/pause a running process.
     * @param elementDatabase component associated to the active job
     * @param suspendable active job to act on
     */
    public static void put(AbstractElementDatabase elementDatabase, AbstractSuspendable<?> suspendable) {
    	ThreadUtil.suspendables.put(elementDatabase, suspendable);
    }
    
    /**
     * Get the task associated to the database component.
     * It's usually done to act on the task like stop/pause the corresponding process, or
     * to check the status of the job.
     * @param elementDatabase component associated to the active job
     * @return job currently running
     */
    public static AbstractSuspendable<?> get(AbstractElementDatabase elementDatabase) {
        return ThreadUtil.suspendables.get(elementDatabase);
    }
    
    /**
     * Remove the thread corresponding to the component in order to be
     * garbage collected. The thread should be stopped prior the deletion. 
     * @param elementDatabase component associated to thread
     */
    public static void remove(AbstractElementDatabase elementDatabase) {
    	ThreadUtil.suspendables.remove(elementDatabase);
    }
    
    /**
     * Force to stop every threads still running and empty the list where
     * they were instanciated in order to be garbage collected. 
     */
    public static void reset() {
        for (AbstractSuspendable<?> suspendable : ThreadUtil.suspendables.values()) {
            suspendable.stop();
        }
        ThreadUtil.suspendables.clear();
    }
    
}

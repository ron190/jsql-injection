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
 * String operations missing like join().
 */
public final class ThreadUtil {
    /**
     * List of running jobs.
     */
    private final Map<AbstractElementDatabase, AbstractSuspendable<?>> suspendables = new HashMap<>();
    
    private static final ThreadUtil INSTANCE = new ThreadUtil();
    
    /**
     * Utility class.
     */
    private ThreadUtil() {
        //not called
    }

    public static void put(AbstractElementDatabase dataObject, AbstractSuspendable<?> suspendable) {
        INSTANCE.suspendables.put(dataObject, suspendable);
    }
    
    public static AbstractSuspendable<?> get(AbstractElementDatabase dataObject) {
        return INSTANCE.suspendables.get(dataObject);
    }
    
    public static void remove(AbstractElementDatabase dataObject) {
        INSTANCE.suspendables.remove(dataObject);
    }
    
    public static void reset() {
        for (AbstractSuspendable<?> suspendable : INSTANCE.suspendables.values()) {
            suspendable.stop();
        }
        INSTANCE.suspendables.clear();
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model;

import java.util.Observable;

import javax.swing.SwingUtilities;

import com.jsql.model.bean.Request;

/**
 * Define the features of the injection model :<br>
 * - stop the preparation of injection,<br>
 * - Callable for parallelizing HTTP tasks,<br>
 * - communication with view, via Observable.
 */
public abstract class ModelObservable extends Observable {
    /**
     * Simple boolean state, true if user wants to stop preparation.<br>
     * During the preparation, several methods will
     * check if the execution must be stopped.
     */
    public boolean stopFlag = false;

    public void stop() {
        stopFlag = true;
    }

    /**
     *  Function header for the inject() methods, definition needed by call(),
     *  dataInjection: SQL query,
     *  responseHeader unused,
     *  useVisibleIndex false if injection indexes aren't needed,
     *  return source page after the HTTP call.
     */
    public abstract String inject(String dataInjection);
    public abstract String inject(String dataInjection, String[] responseHeader, boolean useVisibleIndex);

    /**
     * Send an interaction message to registered views.
     * @param interaction The evenement bean corresponding to the interaction
     */
    public void interact(final Request interaction) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ModelObservable.this.setChanged();
                ModelObservable.this.notifyObservers(interaction);
            }
        });
    }
}

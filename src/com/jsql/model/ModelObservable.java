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
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import com.jsql.model.bean.Request;

/**
 * Define the features of the injection model:
 * - stop the preparation of injection,
 * - Callable for parallelizing HTTP tasks,
 * - communication with view, via Observable
 */
public abstract class ModelObservable extends Observable {
    /**
     * Simple boolean state, true if user wants to stop preparation.
     * During the preparation, several methods will check if the execution must be stopped
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
     *  return source page after the HTTP call
     */
    public abstract String inject( String dataInjection );
    public abstract String inject( String dataInjection, String[] responseHeader, boolean useVisibleIndex );

    /**
     * Callable for parallelized HTTP tasks
     * url: SQL query
     * content: source code of the web page
     * tag: store user information (ex. current index)
     */
    public class SimpleCallable implements Callable<SimpleCallable>{
        public String url, content, tag;
        SimpleCallable(String url){
            this.url = url;
        }

        SimpleCallable(String url, String tag){
            this(url);
            this.tag = tag;
        }

        @Override
        public SimpleCallable call() throws Exception {
            content = ModelObservable.this.inject(url);
            return this;
        }
    }

    /**
     * Send an interaction message to registered views
     * @param interaction The evenement bean corresponding to the interaction
     */
    public void interact(final Request interaction){
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                ModelObservable.this.setChanged();
                ModelObservable.this.notifyObservers( interaction );
            }
        });
    }
}

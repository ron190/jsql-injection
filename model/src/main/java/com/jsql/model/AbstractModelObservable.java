/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model;

import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.strategy.blind.AbstractCallableBinary;

import java.util.concurrent.SubmissionPublisher;

/**
 * Define the features of the injection model :<br>
 * - stop the preparation of injection,<br>
 * - Callable for parallelize HTTP tasks,<br>
 * - communication with view, via Observable.
 */
public abstract class AbstractModelObservable extends SubmissionPublisher<Request> {
    
    /**
     * True if user wants to stop preparation.<br>
     * During the preparation, several methods will
     * check if the execution must be stopped.
     */
    protected boolean isStoppedByUser = false;

    /**
     * Function header for the inject() methods, definition needed by call(),
     * dataInjection: SQL query,
     * responseHeader unused,
     * useVisibleIndex false if injection indexes aren't needed,
     * return source page after the HTTP call.
     */
    public abstract String inject(
        String dataInjection,
        boolean isUsingIndex,
        String metadataInjectionProcess,
        AbstractCallableBinary<?> callableBoolean,
        boolean isReport
    );
    
    /**
     * Inject without the need of index like in "select 1,2,...".<br>
     * Used for example by: first index test (getVisibleIndex), Error test, and Error, Blind, Time strategies.
     * @return source code of current page
     */
    public String injectWithoutIndex(String dataInjection, String metadataInjectionProcess) {
        return this.inject(dataInjection, false, metadataInjectionProcess, null, false);
    }

    public String injectWithoutIndex(String dataInjection, String metadataInjectionProcess, AbstractCallableBinary<?> callableBoolean) {
        return this.inject(dataInjection, false, metadataInjectionProcess, callableBoolean, false);
    }

    public String injectWithIndexes(String dataInjection, String metadataInjectionProcess) {
        return this.inject(dataInjection, true, metadataInjectionProcess, null, false);
    }

    public String getReportWithoutIndex(String dataInjection, String metadataInjectionProcess) {
        return this.inject(dataInjection, false, metadataInjectionProcess, null, true);
    }

    public String getReportWithoutIndex(String dataInjection, String metadataInjectionProcess, AbstractCallableBinary<?> callableBoolean) {
        return this.inject(dataInjection, false, metadataInjectionProcess, callableBoolean, true);
    }

    public String getReportWithIndexes(String dataInjection, String metadataInjectionProcess) {
        return this.inject(dataInjection, true, metadataInjectionProcess, null, true);
    }

    /**
     * Send an interaction message to registered views.
     * @param request The event bean corresponding to the interaction
     */
    public void sendToViews(final Request request) {
        AbstractModelObservable.this.submit(request);
    }

    
    // Getters and setters
    
    public boolean isStoppedByUser() {
        return this.isStoppedByUser;
    }

    public void setIsStoppedByUser(boolean processStopped) {
        this.isStoppedByUser = processStopped;
    }
}

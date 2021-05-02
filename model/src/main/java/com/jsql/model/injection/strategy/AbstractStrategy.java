package com.jsql.model.injection.strategy;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Define a strategy to inject SQL with methods like Error and Time.
 */
public abstract class AbstractStrategy {
    
    /**
     * i.e, 2 in "[..]union select 1,2,[..]", if 2 is found in HTML body.
     */
    protected String visibleIndex;
    
    /**
     * HTML body of page successfully responding to
     * multiple fields selection (select 1,2,3,..).
     */
    protected String sourceIndexesFound = StringUtils.EMPTY;
    
    /**
     * True if injection can be used, false otherwise.
     */
    protected boolean isApplicable = false;

    protected InjectionModel injectionModel;
    
    protected AbstractStrategy(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    /**
     * Test if this strategy can be used to inject SQL.
     * @return
     * @throws InjectionFailureException
     * @throws StoppedByUserSlidingException
     */
    public abstract void checkApplicability() throws JSqlException;
    
    /**
     * Inform the view that this strategy can be used.
     */
    protected abstract void allow(int... i);
    
    /**
     * Inform the view that this strategy can't be used.
     */
    protected abstract void unallow(int... i);
    
    /**
     * Start the strategy work.
     * @return Source code
     */
    public abstract String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException;
    
    /**
     * Change the strategy of the model to current strategy.
     */
    public abstract void activateStrategy();
    
    /**
     * Get number of characters you can obtain from the strategy.
     */
    public abstract String getPerformanceLength();
    
    /**
     * Get the injection strategy name.
     */
    public abstract String getName();
    
    public void markVulnerability(Interaction message, int... indexErrorStrategy) {
        
        var request = new Request();
        request.setMessage(message);
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser());
        
        // Ellipse default to non null array
        if (indexErrorStrategy.length > 0) {
            
            msgHeader.put(Header.INDEX_ERROR_STRATEGY, indexErrorStrategy[0]);
            msgHeader.put(Header.INJECTION_MODEL, this.injectionModel);
        }

        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
    }
    
    @Override
    public String toString() {
        return this.getName();
    }

    // Getter and setter
    
    public boolean isApplicable() {
        return this.isApplicable;
    }
    
    public void setApplicable(boolean isApplicable) {
        this.isApplicable = isApplicable;
    }
    
    public String getVisibleIndex() {
        return this.visibleIndex;
    }

    public void setVisibleIndex(String visibleIndex) {
        this.visibleIndex = visibleIndex;
    }

    public String getSourceIndexesFound() {
        return this.sourceIndexesFound;
    }

    public void setSourceIndexesFound(String sourceIndexesFound) {
        this.sourceIndexesFound = sourceIndexesFound;
    }
}

package com.jsql.model.injection.strategy;

import java.util.EnumMap;
import java.util.Map;

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
    
    public AbstractStrategy(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;
	
    /**
     * True if injection can be used, false otherwise.
     */
    protected boolean isApplicable = false;

    /**
     * Return if this strategy can be used to inject SQL.
     * @return True if strategy can be used, false otherwise.
     */
    public boolean isApplicable() {
        return this.isApplicable;
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
    protected abstract void allow();
    
    /**
     * Inform the view that this strategy can't be used.
     */
    protected abstract void unallow();
    
    public void markVulnerable(Interaction message) {
        Request request = new Request();
        request.setMessage(message);
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, injectionModel.connectionUtil.getUrlByUser());

        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
    }

    public void markInvulnerable(Interaction message) {
        Request request = new Request();
        request.setMessage(message);
        this.injectionModel.sendToViews(request);
    }
    
    public void markVulnerable(Interaction message, int i) {
        Request request = new Request();
        request.setMessage(message);
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, injectionModel.connectionUtil.getUrlByUser());
        msgHeader.put(Header.SOURCE, i);
        msgHeader.put(Header.INJECTION_MODEL, injectionModel);

        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
    }

    public void markInvulnerable(Interaction message, int i) {
        Request request = new Request();
        request.setMessage(message);
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.SOURCE, i);
        msgHeader.put(Header.INJECTION_MODEL, injectionModel);

        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
    }
    
    /**
     * Start the strategy work.
     * @return Source code
     */
    public abstract String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException;
    
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
    
    public Integer getIndexMethod() {
        return 0;
    }

    public void allow(int i) {
        // TODO Auto-generated method stub
        
    }

    public void unallow(int i) {
        // TODO Auto-generated method stub
        
    }
    
}

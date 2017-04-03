package com.jsql.model.injection.strategy;

import java.util.EnumMap;
import java.util.Map;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.ConnectionUtil;

/**
 * Define a strategy to inject SQL with methods like errorbased or timebased.
 */
public abstract class AbstractStrategy {
	
    /**
     * True if injection can be used, false otherwise.
     */
    protected boolean isApplicable = false;

    /**
     * Return if this strategy can be used to inject SQL.
     * @return True if strategy can be used, false otherwise.
     */
    public boolean isApplicable() {
        return isApplicable;
    }

    /**
     * Test if this strategy can be used to inject SQL.
     * @return
     * @throws InjectionFailureException
     * @throws StoppedByUserException
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
    
    public void markVulnerable(TypeRequest message) {
        Request request = new Request();
        request.setMessage(message);
        
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.URL, ConnectionUtil.getUrlByUser());

        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
    }

    public void markInvulnerable(TypeRequest message) {
        Request request = new Request();
        request.setMessage(message);
        MediatorModel.model().sendToViews(request);
    }
    
    public void markVulnerable(TypeRequest message, int i) {
        Request request = new Request();
        request.setMessage(message);
        
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.URL, ConnectionUtil.getUrlByUser());
        msgHeader.put(TypeHeader.SOURCE, i);

        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
    }

    public void markInvulnerable(TypeRequest message, int i) {
        Request request = new Request();
        request.setMessage(message);
        
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.SOURCE, i);

        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Start the strategy work.
     * @return Source code
     */
    public abstract String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserException;
    
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
    public Integer getErrorIndex() {
        return 0;
    }

    public void allow(int i) {
        // TODO Auto-generated method stub
        
    }

    public void unallow(int i) {
        // TODO Auto-generated method stub
        
    }
    
}

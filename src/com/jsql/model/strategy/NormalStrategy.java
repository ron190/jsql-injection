package com.jsql.model.strategy;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Request;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.injection.suspendable.AbstractSuspendable;
import com.jsql.model.injection.suspendable.SuspendableGetSQLIndices;

/**
 * Injection strategy using normal attack.
 */
public class NormalStrategy extends AbstractInjectionStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(NormalStrategy.class);

    @Override
    public void checkApplicability() throws PreparationException, StoppableException {
        LOGGER.trace("Normal test...");
        MediatorModel.model().initialQuery = new SuspendableGetSQLIndices().action();

        this.isApplicable = !"".equals(MediatorModel.model().initialQuery);
        
        if (this.isApplicable) {
            allow();
        } else {
            unallow();
        }
    }

    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkNormalVulnerable");
        
        Map<String, Object> msgHeader = new HashMap<String, Object>();
        msgHeader.put("Url", MediatorModel.model().initialUrl + MediatorModel.model().getData + MediatorModel.model().insertionCharacter);

        request.setParameters(msgHeader);
        MediatorModel.model().interact(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkNormalInvulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return MediatorModel.model().inject(MediatorModel.model().sqlStrategy.normalStrategy(sqlQuery, startPosition), true);
    }

    @Override
    public void applyStrategy() {
        MediatorModel.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MarkNormalStrategy");
        MediatorModel.model().interact(request);
    }
    
    @Override
    public String getPerformanceLength() {
        return MediatorModel.model().performanceLength;
    }
}

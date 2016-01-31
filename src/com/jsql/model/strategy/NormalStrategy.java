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

    public String performanceLength = "0";
    
    /**
     * i.e, 2 in "[..]union select 1,2,[..]", if 2 is found in HTML source.
     */
    public String visibleIndex;
    
    @Override
    public void checkApplicability() throws PreparationException, StoppableException {
        LOGGER.trace("Normal test...");
        MediatorModel.model().initialQuery = new SuspendableGetSQLIndices().action();

        // If there is no page source defined then there is no injection possible
        if (MediatorModel.model().firstSuccessPageSource != null) {
            // Define visibleIndex, i.e, 2 in "[..]union select 1,2,[..]", if 2 is found in HTML source
            this.visibleIndex = MediatorModel.model().getVisibleIndex(MediatorModel.model().firstSuccessPageSource);
        }
        
        this.isApplicable = (!"".equals(MediatorModel.model().initialQuery)) 
                && new Integer(MediatorModel.model().normalStrategy.getPerformanceLength()) > 0
                && this.visibleIndex != null
                && MediatorModel.model().firstSuccessPageSource != null;
        
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
        return MediatorModel.model().inject(MediatorModel.model().currentVendor.getStrategy().normalStrategy(sqlQuery, startPosition), true);
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
        return this.performanceLength;
    }
    
    @Override
    public String getName() {
        return "Normal";
    }
}

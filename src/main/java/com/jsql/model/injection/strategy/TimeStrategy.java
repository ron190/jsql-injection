/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.model.injection.strategy;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;
import com.jsql.model.injection.strategy.blind.ConcreteTimeInjection;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.ConnectionUtil;

/**
 * Injection strategy using time attack.
 */
public class TimeStrategy extends AbstractStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(TimeStrategy.class);

    /**
     * Injection method using time attack.
     */
    private ConcreteTimeInjection timeInjection;
    
    @Override
    public void checkApplicability() throws PreparationException {
        LOGGER.trace("Time based test...");
        
        this.timeInjection = new ConcreteTimeInjection();
        
        this.isApplicable = this.timeInjection.isInjectable();
        
        if (this.isApplicable) {
            LOGGER.debug("Vulnerable to Time injection");
            allow();
        } else {
            unallow();
        }
    }
    
    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkTimebasedVulnerable");
        
        Map<String, Object> msgHeader = new HashMap<>();
        msgHeader.put("Url", ConnectionUtil.urlByUser + ConnectionUtil.dataQuery + MediatorModel.model().charInsertion);

        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkTimebasedInvulnerable");
        MediatorModel.model().sendToViews(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppableException {
        return this.timeInjection.inject(
            MediatorModel.model().vendor.getValue().getSqlTime(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.TIME);
        
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters(timeInjection.getInfoMessage());
        MediatorModel.model().sendToViews(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkTimebasedStrategy");
        MediatorModel.model().sendToViews(request2);
    }
    
    @Override
    public String getPerformanceLength() {
        return "65565";
    }
    
    @Override
    public String getName() {
        return "Time";
    }
}

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
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.strategy.blind.ConcreteBlindInjection;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.ConnectionUtil;

/**
 * Injection strategy using blind attack.
 */
public class BlindStrategy extends AbstractStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(BlindStrategy.class);

    /**
     * Blind injection object.
     */
    private ConcreteBlindInjection blind;
    
    @Override
    public void checkApplicability() throws StoppedByUserException {
        LOGGER.trace("Blind test...");
        
        this.blind = new ConcreteBlindInjection();
        
        this.isApplicable = this.blind.isInjectable();
        
        if (this.isApplicable) {
            LOGGER.debug("Vulnerable to Blind injection");
            allow();
        } else {
            unallow();
        }
    }

    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkBlindVulnerable");
        
        Map<String, Object> msgHeader = new HashMap<>();
        msgHeader.put("Url", ConnectionUtil.urlByUser);

        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkBlindInvulnerable");
        MediatorModel.model().sendToViews(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserException {
        return blind.inject(
            MediatorModel.model().vendor.getValue().getSqlBlind(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.BLIND);
        
        Request requestMessageBinary = new Request();
        requestMessageBinary.setMessage("MessageBinary");
        requestMessageBinary.setParameters(blind.getInfoMessage());
        MediatorModel.model().sendToViews(requestMessageBinary);
        
        Request requestMarkBlindStrategy = new Request();
        requestMarkBlindStrategy.setMessage("MarkBlindStrategy");
        MediatorModel.model().sendToViews(requestMarkBlindStrategy);
    }
    
    @Override
    public String getPerformanceLength() {
        return "65565";
    }
    
    @Override
    public String getName() {
        return "Blind";
    }
}

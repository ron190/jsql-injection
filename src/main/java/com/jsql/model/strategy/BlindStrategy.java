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
package com.jsql.model.strategy;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.bean.Request;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;
import com.jsql.model.strategy.blind.ConcreteBlindInjection;
import com.jsql.model.suspendable.AbstractSuspendable;

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
    public void checkApplicability() throws PreparationException {
        LOGGER.trace("Blind test...");
        
        this.blind = new ConcreteBlindInjection();
        
        this.isApplicable = this.blind.isInjectable();
        
        if (this.isApplicable) {
            allow();
        } else {
            unallow();
        }
    }

    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkBlindVulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkBlindInvulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return blind.inject(
            MediatorModel.model().currentVendor.getValue().getSqlBlind(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using blind injection...");
        MediatorModel.model().setStrategy(Strategy.BLIND);
        
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters(blind.getInfoMessage());
        MediatorModel.model().interact(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkBlindStrategy");
        MediatorModel.model().interact(request2);
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

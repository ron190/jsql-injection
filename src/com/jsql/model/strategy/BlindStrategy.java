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

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteBlindInjection;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.injection.suspendable.AbstractSuspendable;

/**
 * Injection strategy using blind attack.
 */
public class BlindStrategy extends AbstractInjectionStrategy {
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
            MediatorModel.model().currentVendor.getStrategy().blindStrategy(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void applyStrategy() {
        LOGGER.info("Using blind injection...");
        MediatorModel.model().applyStrategy(this);
        
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

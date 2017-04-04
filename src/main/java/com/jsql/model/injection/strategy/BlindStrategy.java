/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.model.injection.strategy;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.ConcreteBlindInjection;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Injection strategy using blind attack.
 */
public class BlindStrategy extends AbstractStrategy {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Blind injection object.
     */
    private ConcreteBlindInjection blind;
    
    @Override
    public void checkApplicability() throws StoppedByUserSlidingException {
        LOGGER.trace("Blind test...");
        
        this.blind = new ConcreteBlindInjection();
        
        this.isApplicable = this.blind.isInjectable();
        
        if (this.isApplicable) {
            LOGGER.debug("Vulnerable to Blind injection");
            this.allow();
        } else {
            this.unallow();
        }
    }

    @Override
    public void allow() {
        this.markVulnerable(TypeRequest.MARK_BLIND_VULNERABLE);
    }

    @Override
    public void unallow() {
        this.markVulnerable(TypeRequest.MARK_BLIND_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException {
        return this.blind.inject(
            MediatorModel.model().vendor.instance().sqlBlind(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.BLIND);
        
        Request requestMessageBinary = new Request();
        requestMessageBinary.setMessage(TypeRequest.MESSAGE_BINARY);
        requestMessageBinary.setParameters(blind.getInfoMessage());
        MediatorModel.model().sendToViews(requestMessageBinary);
        
        Request requestMarkBlindStrategy = new Request();
        requestMarkBlindStrategy.setMessage(TypeRequest.MARK_BLIND_STRATEGY);
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

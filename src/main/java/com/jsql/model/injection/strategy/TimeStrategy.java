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
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.strategy.blind.ConcreteTimeInjection;
import com.jsql.model.suspendable.AbstractSuspendable;

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
    public void checkApplicability() throws StoppedByUserException {
        LOGGER.trace("Time based test...");
        
        this.timeInjection = new ConcreteTimeInjection();
        
        this.isApplicable = this.timeInjection.isInjectable();
        
        if (this.isApplicable) {
            LOGGER.debug("Vulnerable to Time injection");
            this.allow();
        } else {
            this.unallow();
        }
    }
    
    @Override
    public void allow() {
        this.markVulnerable(TypeRequest.MARK_TIMEBASED_VULNERABLE);
    }

    @Override
    public void unallow() {
        this.markVulnerable(TypeRequest.MARK_TIMEBASED_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserException {
        return this.timeInjection.inject(
            MediatorModel.model().vendor.instance().sqlTime(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.TIME);
        
        Request requestMessageBinary = new Request();
        requestMessageBinary.setMessage(TypeRequest.MESSAGE_BINARY);
        requestMessageBinary.setParameters(this.timeInjection.getInfoMessage());
        MediatorModel.model().sendToViews(requestMessageBinary);
        
        Request requestMarkTimebasedStrategy = new Request();
        requestMarkTimebasedStrategy.setMessage(TypeRequest.MARK_TIMEBASED_STRATEGY);
        MediatorModel.model().sendToViews(requestMarkTimebasedStrategy);
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

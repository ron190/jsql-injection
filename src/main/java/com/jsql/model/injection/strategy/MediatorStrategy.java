package com.jsql.model.injection.strategy;

import java.util.Arrays;
import java.util.List;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.AbstractSuspendable;

public class MediatorStrategy {
    
    private AbstractStrategy UNDEFINED;
    private AbstractStrategy TIME;
    private AbstractStrategy BLIND;
    private AbstractStrategy ERROR;
    private AbstractStrategy NORMAL;
    
    private List<AbstractStrategy> strategies = Arrays.asList(this.getUNDEFINED(),this.getTIME(),this.getBLIND(),this.getERROR(),this.NORMAL);
    
    /**
     * Current injection strategy.
     */
    private AbstractStrategy strategy;

    private InjectionModel injectionModel;
    
    public MediatorStrategy(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        this.TIME = new StrategyInjectionTime(this.injectionModel);
        this.BLIND = new StrategyInjectionBlind(this.injectionModel);
        this.ERROR = new StrategyInjectionError(this.injectionModel);
        this.NORMAL = new StrategyInjectionNormal(this.injectionModel);
        
        this.UNDEFINED = new AbstractStrategy(this.injectionModel) {

            @Override
            public void checkApplicability() throws JSqlException {
                // TODO Auto-generated method stub
                
            }

            @Override
            protected void allow() {
                // TODO Auto-generated method stub
                
            }

            @Override
            protected void unallow() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void activateStrategy() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public String getPerformanceLength() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
    }

    public AbstractStrategy getNORMAL() {
        return NORMAL;
    }

    public AbstractStrategy getERROR() {
        return ERROR;
    }

    public AbstractStrategy getBLIND() {
        return BLIND;
    }

    public AbstractStrategy getTIME() {
        return TIME;
    }

    public AbstractStrategy getUNDEFINED() {
        return UNDEFINED;
    }

    public List<AbstractStrategy> getStrategies() {
        return strategies;
    }

    public AbstractStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(AbstractStrategy strategy) {
        this.strategy = strategy;
    }

}

package com.jsql.model.injection.strategy;

import java.util.Arrays;
import java.util.List;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.AbstractSuspendable;

public class MediatorStrategy {
    
    private AbstractStrategy undefined;
    private AbstractStrategy time;
    private AbstractStrategy blind;
    private AbstractStrategy error;
    private AbstractStrategy normal;
    
    private List<AbstractStrategy> strategies;
    
    /**
     * Current injection strategy.
     */
    private AbstractStrategy strategy;

    private InjectionModel injectionModel;
    
    public MediatorStrategy(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        this.time = new StrategyInjectionTime(this.injectionModel);
        this.blind = new StrategyInjectionBlind(this.injectionModel);
        this.error = new StrategyInjectionError(this.injectionModel);
        this.normal = new StrategyInjectionNormal(this.injectionModel);
        
        this.undefined = new AbstractStrategy(this.injectionModel) {

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
        
        strategies = Arrays.asList(
            this.undefined,
            this.time,
            this.blind,
            this.error,
            this.normal
        );
    }

    public AbstractStrategy getNormal() {
        return normal;
    }

    public AbstractStrategy getError() {
        return error;
    }

    public AbstractStrategy getBlind() {
        return blind;
    }

    public AbstractStrategy getTime() {
        return time;
    }

    public AbstractStrategy getUndefined() {
        return undefined;
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

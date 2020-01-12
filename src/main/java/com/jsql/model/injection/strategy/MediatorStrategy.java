package com.jsql.model.injection.strategy;

import java.util.Arrays;
import java.util.List;

import com.jsql.model.InjectionModel;

public class MediatorStrategy {
    
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
        
        this.strategies = Arrays.asList(
            this.time,
            this.blind,
            this.error,
            this.normal
        );
    }

    public AbstractStrategy getNormal() {
        return this.normal;
    }

    public AbstractStrategy getError() {
        return this.error;
    }

    public AbstractStrategy getBlind() {
        return this.blind;
    }

    public AbstractStrategy getTime() {
        return this.time;
    }

    public List<AbstractStrategy> getStrategies() {
        return this.strategies;
    }

    public AbstractStrategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(AbstractStrategy strategy) {
        this.strategy = strategy;
    }

}

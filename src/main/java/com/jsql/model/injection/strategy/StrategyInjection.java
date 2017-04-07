package com.jsql.model.injection.strategy;

public enum StrategyInjection {
	
    UNDEFINED("Undefined", null),
    TIME("Time", new StrategyInjectionTime()),
    BLIND("Blind", new StrategyInjectionBlind()),
    ERRORBASED("Error", new StrategyInjectionError()),
    NORMAL("Normal", new StrategyInjectionNormal());
    
    private final String nameStrategy;

    private final AbstractStrategy instanceStrategy;
    
    private StrategyInjection(String nameStrategy, AbstractStrategy instanceStrategy) {
        this.nameStrategy = nameStrategy;
        this.instanceStrategy = instanceStrategy;
    }
    
    public AbstractStrategy instance() {
        return instanceStrategy;
    }
    
    @Override
    public String toString() {
        return this.nameStrategy;
    }
    
}

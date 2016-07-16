package com.jsql.model.injection.strategy;

public enum Strategy {
    UNDEFINED("Undefined", null),
    TIME("Time", new TimeStrategy()),
    BLIND("Blind", new BlindStrategy()),
    ERRORBASED("ErrorBased", new ErrorbasedStrategy()),
    NORMAL("Normal", new NormalStrategy());

    private final AbstractStrategy strategy;
    private final String strategyName;
    
    Strategy(String strategyName, AbstractStrategy vendor) {
        this.strategyName = strategyName;
        this.strategy = vendor;
    }
    
    public AbstractStrategy instance() {
        return strategy;
    }
    
    @Override
    public String toString() {
        return this.strategyName +" ";
    }
}

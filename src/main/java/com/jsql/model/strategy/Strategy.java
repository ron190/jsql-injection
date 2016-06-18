package com.jsql.model.strategy;

public enum Strategy {
    
    UNDEFINED("Undefined", null),
    BLIND("Blind", new BlindStrategy()),
    TIME("Time", new TimeStrategy()),
    ERRORBASED("ErrorBased", new ErrorbasedStrategy()),
    NORMAL("Normal", new NormalStrategy());

    private final AbstractStrategy strategy;
    private final String strategyName;
    
    Strategy(String strategyName, AbstractStrategy vendor) {
        this.strategyName = strategyName;
        this.strategy = vendor;
    }
    
    public AbstractStrategy getValue() {
        return strategy;
    }
    
    @Override
    public String toString() {
        return this.strategyName +" ";
    }
}

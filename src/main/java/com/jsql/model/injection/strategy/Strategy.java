package com.jsql.model.injection.strategy;

public enum Strategy {
    UNDEFINED("Undefined", null),
    TIME("Time", new TimeStrategy()),
    BLIND("Blind", new BlindStrategy()),
    ERRORBASED("ErrorBased", new ErrorbasedStrategy()),
    NORMAL("Normal", new NormalStrategy());

    private final AbstractStrategy instanceStrategy;
    private final String nameStrategy;
    
    Strategy(String nameStrategy, AbstractStrategy instanceStrategy) {
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

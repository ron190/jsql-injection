package com.jsql.model.injection.engine.model;

public class Engine {
    
    private final String labelEngine;
    
    private AbstractEngine instanceEngine;
    
    public Engine(AbstractEngine instanceEngine) {
        this.labelEngine = instanceEngine.getModelYaml().getEngine();
        this.instanceEngine = instanceEngine;
    }
    
    public Engine() {
        this.labelEngine = "Engine auto";
    }

    public String transformSqlite(String resultToParse) {
        return resultToParse;
    }

    public AbstractEngine instance() {
        return this.instanceEngine;
    }

    @Override
    public String toString() {
        return this.labelEngine;
    }
}
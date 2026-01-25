
package com.jsql.model.injection.engine.model.yaml;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ModelYaml implements Serializable {

    private String engine = StringUtils.EMPTY;
    private Resource resource = new Resource();
    private Strategy strategy = new Strategy();

    public String getEngine() {
        return this.engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}

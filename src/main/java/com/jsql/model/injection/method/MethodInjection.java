package com.jsql.model.injection.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public abstract class MethodInjection {
    
    public abstract boolean isCheckingAllParam();
    public abstract String getParamsAsString();
    public abstract List<SimpleEntry<String, String>> getParams();
    public abstract String name();
    
}
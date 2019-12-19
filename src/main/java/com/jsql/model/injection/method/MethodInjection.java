package com.jsql.model.injection.method;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public interface MethodInjection extends Serializable {
    
    boolean isCheckingAllParam();
    String getParamsAsString();
    List<SimpleEntry<String, String>> getParams();
    String name();
    
}
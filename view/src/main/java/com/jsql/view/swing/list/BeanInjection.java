package com.jsql.view.swing.list;

import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.model.injection.engine.model.Engine;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.NoSuchElementException;

public class BeanInjection {
    
    private final String url;
    private String request = StringUtils.EMPTY;
    private String header = StringUtils.EMPTY;
    private String requestType;
    
    private AbstractMethodInjection method;
    private Engine engine;

    public BeanInjection(String url) {
        this.url = url;
        this.method = MediatorHelper.model().getMediatorMethod().getQuery();
        this.engine = MediatorHelper.model().getMediatorEngine().getAuto();
        this.requestType = StringUtil.GET;
    }
    
    public BeanInjection(String url, String request, String header, String nameMethod, String engine, String requestType) {
        this(url);
        this.request = request;
        this.header = header;
        
        try {
            this.method = MediatorHelper.model().getMediatorMethod().getMethods().stream()
                .filter(m -> m.name().equalsIgnoreCase(nameMethod))
                .findAny()
                .orElse(MediatorHelper.model().getMediatorMethod().getQuery());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.method = MediatorHelper.model().getMediatorMethod().getQuery();
        }
        
        try {
            this.engine = MediatorHelper.model().getMediatorEngine().getEngines().stream()
                .filter(v -> v.toString().equals(engine))
                .findAny()
                .orElse(MediatorHelper.model().getMediatorEngine().getAuto());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.engine = MediatorHelper.model().getMediatorEngine().getAuto();
        }
        
        this.requestType = requestType.isEmpty() ? StringUtil.GET : requestType;
    }
    
    
    // Json getter for serialization
    
    public String getMethod() {
        return this.method.name();
    }
    
    public String getEngine() {
        return this.engine.toString();
    }
    
    
    // Getter and setter

    public String getUrl() {
        return this.url;
    }

    public String getRequest() {
        return this.request;
    }

    public String getHeader() {
        return this.header;
    }
    
    public String getRequestType() {
        return this.requestType;
    }

    public AbstractMethodInjection getMethodInstance() {
        return this.method;
    }
}

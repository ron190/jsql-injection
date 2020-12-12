package com.jsql.view.swing.list;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.view.swing.util.MediatorHelper;

public class BeanInjection {
    
    private String url = StringUtils.EMPTY;
    private String request = StringUtils.EMPTY;
    private String header = StringUtils.EMPTY;
    private String requestType = StringUtils.EMPTY;
    
    private AbstractMethodInjection method;
    private Vendor vendor;

    public BeanInjection(String url) {
        
        this.url = url;
        this.method = MediatorHelper.model().getMediatorMethod().getQuery();
        this.vendor = MediatorHelper.model().getMediatorVendor().getAuto();
        this.requestType = "POST";
    }
    
    public BeanInjection(String url, String request, String header, String nameMethod, String vendor, String requestType) {
        
        this(url);
        
        this.request = request;
        this.header = header;
        
        try {
            this.method =
                MediatorHelper
                .model()
                .getMediatorMethod()
                .getMethods()
                .stream()
                .filter(m -> m.name().equalsIgnoreCase(nameMethod))
                .findAny()
                .orElse(MediatorHelper.model().getMediatorMethod().getQuery());
            
        } catch (IllegalArgumentException | NoSuchElementException e) {
            
            this.method = MediatorHelper.model().getMediatorMethod().getQuery();
        }
        
        try {
            this.vendor =
                MediatorHelper
                .model()
                .getMediatorVendor()
                .getVendors()
                .stream()
                .filter(v -> v.toString().equals(vendor))
                .findAny()
                .orElse(MediatorHelper.model().getMediatorVendor().getAuto());
            
        } catch (IllegalArgumentException | NoSuchElementException e) {
            
            this.vendor = MediatorHelper.model().getMediatorVendor().getAuto();
        }
        
        this.requestType =
            requestType.isEmpty()
            ? "POST"
            : requestType;
    }
    
    
    // Json getter for serialization
    
    public String getMethod() {
        return this.method.name();
    }
    
    public String getVendor() {
        return this.vendor.toString();
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

    public Vendor getVendorInstance() {
        return this.vendor;
    }
}

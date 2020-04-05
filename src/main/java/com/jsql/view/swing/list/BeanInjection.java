package com.jsql.view.swing.list;

import java.util.NoSuchElementException;

import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.view.swing.MediatorGui;

public class BeanInjection {
    
    private String url = "";
    private String request = "";
    private String header = "";
    private MethodInjection injectionType;
    private Vendor vendor;
    private String requestType = "";

    public BeanInjection(String url) {
        
        this.url = url;
        this.injectionType = MediatorGui.model().getMediatorMethodInjection().getQuery();
        this.vendor = MediatorGui.model().getMediatorVendor().getAuto();
        this.requestType = "POST";
    }
    
    public BeanInjection(String url, String request, String header, String injectionType, String vendor, String requestType) {
        
        this(url);
        
        this.request = request;
        this.header = header;
        
        try {
            this.injectionType = MediatorGui.model().getMediatorMethodInjection().getMethods().stream().filter(m -> m.name().equalsIgnoreCase(injectionType)).findAny().orElse(MediatorGui.model().getMediatorMethodInjection().getQuery());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.injectionType = MediatorGui.model().getMediatorMethodInjection().getQuery();
        }
        
        try {
            this.vendor = MediatorGui.model().getMediatorVendor().getVendors().stream().filter(m -> m.toString().equals(vendor)).findAny().orElse(MediatorGui.model().getMediatorVendor().getAuto());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.vendor = MediatorGui.model().getMediatorVendor().getAuto();
        }
        
        this.requestType = requestType.isEmpty() ? "POST" : requestType;
    }

    public String getUrl() {
        return this.url;
    }

    public String getRequest() {
        return this.request;
    }

    public String getHeader() {
        return this.header;
    }
    
    public String getInjectionType() {
        return this.injectionType.name();
    }

    public MethodInjection getInjectionTypeAsEnum() {
        return this.injectionType;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public String getVendor() {
        return this.vendor.toString();
    }

    public Vendor getVendorAsEnum() {
        return this.vendor;
    }
}

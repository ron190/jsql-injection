package com.jsql.view.swing.list;

import java.util.NoSuchElementException;

import com.jsql.model.InjectionModel.MethodInjection;
import com.jsql.model.InjectionModel.Vendor;
import com.jsql.model.MediatorModel;

public class BeanInjection {
    
    private String url = "";
    private String request = "";
    private String header = "";
    private MethodInjection injectionType;
    private Vendor vendor;
    private String requestType = "";

    public BeanInjection(
        String url
    ) {
        this.url = url;
        this.injectionType = MediatorModel.model().QUERY;
        this.vendor = MediatorModel.model().AUTO;
        this.requestType = "POST";
    }
    
    public BeanInjection(
        String url,
        String request,
        String header,
        String injectionType,
        String vendor,
        String requestType
    ) {
        this(url);
        
        this.request = request;
        this.header = header;
        
        try {
            this.injectionType = MediatorModel.model().methods.stream().filter(m -> m.name().equalsIgnoreCase(injectionType)).findAny().orElse(MediatorModel.model().QUERY);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.injectionType = MediatorModel.model().QUERY;
        }
        
        try {
            this.vendor = MediatorModel.model().vendors.stream().filter(m -> m.toString().equals(vendor)).findAny().orElse(MediatorModel.model().AUTO);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.vendor = MediatorModel.model().AUTO;
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

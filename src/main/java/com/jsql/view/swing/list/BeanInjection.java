package com.jsql.view.swing.list;

import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.vendor.Vendor;

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
        this.injectionType = MethodInjection.QUERY;
        this.vendor = Vendor.AUTO;
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
            this.injectionType = MethodInjection.valueOf(injectionType);
        } catch (IllegalArgumentException e) {
            this.injectionType = MethodInjection.QUERY;
        }
        
        try {
            this.vendor = Vendor.valueOf(vendor);
        } catch (IllegalArgumentException e) {
            this.vendor = Vendor.AUTO;
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
        return this.vendor.name();
    }

    public Vendor getVendorAsEnum() {
        return this.vendor;
    }
    
}

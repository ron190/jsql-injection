package com.jsql.view.swing.list;

import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.vendor.Vendor;

public class BeanInjection {
    
    private String url;
    private String request;
    private String header;
    private MethodInjection injectionType;
    private Vendor vendor;
    private String requestType;

    public BeanInjection(
        String url,
        String request,
        String header,
        String injectionType,
        String vendor,
        String requestType
    ) {
        this.url = url;
        this.request = request;
        this.header = header;
        this.injectionType = injectionType.isEmpty() ? MethodInjection.QUERY : MethodInjection.valueOf(injectionType);
        this.vendor = vendor.isEmpty() ? Vendor.AUTO : Vendor.valueOf(vendor);
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

    public MethodInjection getInjectionType() {
        return this.injectionType;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public Vendor getVendor() {
        return this.vendor;
    }
    
}

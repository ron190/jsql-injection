package com.jsql.view.swing.list;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.view.swing.util.MediatorHelper;

public class BeanInjection {
    
    private String url = StringUtils.EMPTY;
    private String request = StringUtils.EMPTY;
    private String header = StringUtils.EMPTY;
    private MethodInjection injectionType;
    private Vendor vendor;
    private String requestType = StringUtils.EMPTY;

    public BeanInjection(String url) {
        
        this.url = url;
        this.injectionType = MediatorHelper.model().getMediatorMethodInjection().getQuery();
        this.vendor = MediatorHelper.model().getMediatorVendor().getAuto();
        this.requestType = "POST";
    }
    
    public BeanInjection(String url, String request, String header, String injectionType, String vendor, String requestType) {
        
        this(url);
        
        this.request = request;
        this.header = header;
        
        try {
            this.injectionType = MediatorHelper.model().getMediatorMethodInjection().getMethods().stream().filter(m -> m.name().equalsIgnoreCase(injectionType)).findAny().orElse(MediatorHelper.model().getMediatorMethodInjection().getQuery());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.injectionType = MediatorHelper.model().getMediatorMethodInjection().getQuery();
        }
        
        try {
            this.vendor = MediatorHelper.model().getMediatorVendor().getVendors().stream().filter(m -> m.toString().equals(vendor)).findAny().orElse(MediatorHelper.model().getMediatorVendor().getAuto());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.vendor = MediatorHelper.model().getMediatorVendor().getAuto();
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

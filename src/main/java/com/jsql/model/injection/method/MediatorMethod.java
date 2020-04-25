package com.jsql.model.injection.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.InjectionModel;

public class MediatorMethod {

    private MethodInjection query;
    private MethodInjection request;
    private MethodInjection header;
    
    private List<MethodInjection> methods;
    
    @SuppressWarnings("serial")
    public MediatorMethod(InjectionModel injectionModel) {
        
        this.query = new MethodInjection(injectionModel) {
            
            @Override
            public boolean isCheckingAllParam() {
                return this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam();
            }

            @Override
            public String getParamsAsString() {
                return this.injectionModel.getMediatorUtils().getParameterUtil().getQueryStringFromEntries();
            }

            @Override
            public List<SimpleEntry<String, String>> getParams() {
                return this.injectionModel.getMediatorUtils().getParameterUtil().getListQueryString();
            }

            @Override
            public String name() {
                return "QUERY";
            }
        };
        
        this.request = new MethodInjection(injectionModel) {
            
            @Override
            public boolean isCheckingAllParam() {
                return this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam();
            }

            @Override
            public String getParamsAsString() {
                return this.injectionModel.getMediatorUtils().getParameterUtil().getRequestFromEntries();
            }

            @Override
            public List<SimpleEntry<String, String>> getParams() {
                return this.injectionModel.getMediatorUtils().getParameterUtil().getListRequest();
            }

            @Override
            public String name() {
                return "REQUEST";
            }
        };
        
        this.header = new MethodInjection(injectionModel) {
            
            @Override
            public boolean isCheckingAllParam() {
                return this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam();
            }

            @Override
            public String getParamsAsString() {
                return this.injectionModel.getMediatorUtils().getParameterUtil().getHeaderFromEntries();
            }

            @Override
            public List<SimpleEntry<String, String>> getParams() {
                return this.injectionModel.getMediatorUtils().getParameterUtil().getListHeader();
            }

            @Override
            public String name() {
                return "HEADER";
            }
        };
        
        this.methods = Arrays.asList(this.getQuery(), this.getRequest(), this.getHeader());
    }

    public MethodInjection getQuery() {
        return this.query;
    }

    public MethodInjection getRequest() {
        return this.request;
    }

    public MethodInjection getHeader() {
        return this.header;
    }

    public List<MethodInjection> getMethods() {
        return this.methods;
    }
}

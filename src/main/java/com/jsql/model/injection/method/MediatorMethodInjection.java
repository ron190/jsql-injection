package com.jsql.model.injection.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

import com.jsql.model.InjectionModel;

public class MediatorMethodInjection {

    @SuppressWarnings("serial")
    private MethodInjection query = new MethodInjection() {
        
        @Override
        public boolean isCheckingAllParam() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam();
        }

        @Override
        public String getParamsAsString() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getParameterUtil().getQueryStringFromEntries();
        }

        @Override
        public List<SimpleEntry<String, String>> getParams() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getParameterUtil().getQueryString();
        }

        @Override
        public String name() {
            return "QUERY";
        }
        
    };
    
    @SuppressWarnings("serial")
    private MethodInjection request = new MethodInjection() {
        
        @Override
        public boolean isCheckingAllParam() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam();
        }

        @Override
        public String getParamsAsString() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getParameterUtil().getRequestFromEntries();
        }

        @Override
        public List<SimpleEntry<String, String>> getParams() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getParameterUtil().getRequest();
        }

        @Override
        public String name() {
            return "REQUEST";
        }
        
    };
    
    @SuppressWarnings("serial")
    private MethodInjection header = new MethodInjection() {
        
        @Override
        public boolean isCheckingAllParam() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam();
        }

        @Override
        public String getParamsAsString() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getParameterUtil().getHeaderFromEntries();
        }

        @Override
        public List<SimpleEntry<String, String>> getParams() {
            return MediatorMethodInjection.this.injectionModel.getMediatorUtils().getParameterUtil().getHeader();
        }

        @Override
        public String name() {
            return "HEADER";
        }
        
    };
    
    private List<MethodInjection> methods = Arrays.asList(this.getQuery(), this.getRequest(), this.getHeader());
    
    private InjectionModel injectionModel;
    
    public MediatorMethodInjection(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
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

package com.jsql.model.injection.method;

import com.jsql.model.InjectionModel;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

public class MediatorMethod {

    private final AbstractMethodInjection query;
    private final AbstractMethodInjection request;
    private final AbstractMethodInjection header;
    
    private final List<AbstractMethodInjection> methods;
    
        public MediatorMethod(InjectionModel injectionModel) {
        
        this.query = new AbstractMethodInjection(injectionModel) {
            
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
                
                return "Query";
            }
        };

        this.request = new AbstractMethodInjection(injectionModel) {

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

                return "Request";
            }
        };
        
        this.header = new AbstractMethodInjection(injectionModel) {
            
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
                
                return "Header";
            }
        };
        
        this.methods = Arrays.asList(this.query, this.request, this.header);
    }

    public AbstractMethodInjection getQuery() {
        return this.query;
    }

    public AbstractMethodInjection getRequest() {
        return this.request;
    }

    public AbstractMethodInjection getHeader() {
        return this.header;
    }

    public List<AbstractMethodInjection> getMethods() {
        return this.methods;
    }
}

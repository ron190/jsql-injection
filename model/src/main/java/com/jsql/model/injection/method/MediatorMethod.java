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
                return this.injectionModel.getMediatorUtils().preferencesUtil().isCheckingAllURLParam();
            }
            @Override
            public String getParamsAsString() {
                return this.injectionModel.getMediatorUtils().parameterUtil().getQueryStringFromEntries();
            }
            @Override
            public List<SimpleEntry<String, String>> getParams() {
                return this.injectionModel.getMediatorUtils().parameterUtil().getListQueryString();
            }
            @Override
            public String name() {
                return "Query";
            }
        };

        this.request = new AbstractMethodInjection(injectionModel) {
            @Override
            public boolean isCheckingAllParam() {
                return this.injectionModel.getMediatorUtils().preferencesUtil().isCheckingAllRequestParam();
            }
            @Override
            public String getParamsAsString() {
                return this.injectionModel.getMediatorUtils().parameterUtil().getRequestFromEntries();
            }
            @Override
            public List<SimpleEntry<String, String>> getParams() {
                return this.injectionModel.getMediatorUtils().parameterUtil().getListRequest();
            }
            @Override
            public String name() {
                return "Request";
            }
        };
        
        this.header = new AbstractMethodInjection(injectionModel) {
            @Override
            public boolean isCheckingAllParam() {
                return this.injectionModel.getMediatorUtils().preferencesUtil().isCheckingAllHeaderParam();
            }
            @Override
            public String getParamsAsString() {
                return this.injectionModel.getMediatorUtils().parameterUtil().getHeaderFromEntries();
            }
            @Override
            public List<SimpleEntry<String, String>> getParams() {
                return this.injectionModel.getMediatorUtils().parameterUtil().getListHeader();
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

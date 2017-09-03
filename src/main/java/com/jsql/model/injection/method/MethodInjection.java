package com.jsql.model.injection.method;

import com.jsql.util.PreferencesUtil;

public enum MethodInjection {
	
    QUERY {
        
        @Override
        public boolean isCheckingAllParam() {
            return PreferencesUtil.isCheckingAllURLParam();
        }
        
    }, REQUEST {
        
        @Override
        public boolean isCheckingAllParam() {
            return PreferencesUtil.isCheckingAllRequestParam();
        }
        
    }, HEADER {
        
        @Override
        public boolean isCheckingAllParam() {
            return PreferencesUtil.isCheckingAllHeaderParam();
        }
        
    };
    
    public abstract boolean isCheckingAllParam();
    
}

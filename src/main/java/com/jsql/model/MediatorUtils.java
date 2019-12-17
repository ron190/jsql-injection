package com.jsql.model;

import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.HeaderUtil;
import com.jsql.util.JsonUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.PropertiesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.util.SoapUtil;
import com.jsql.util.ThreadUtil;
import com.jsql.util.tampering.TamperingUtil;

public class MediatorUtils {
    
    private PropertiesUtil propertiesUtil;
    private ConnectionUtil connectionUtil;
    private AuthenticationUtil authenticationUtil;
    private GitUtil gitUtil;
    private HeaderUtil headerUtil;
    private ParameterUtil parameterUtil;
    private ExceptionUtil exceptionUtil;
    private SoapUtil soapUtil;
    private JsonUtil jsonUtil;
    private PreferencesUtil preferencesUtil;
    private ProxyUtil proxyUtil;
    private ThreadUtil threadUtil;
    private TamperingUtil tamperingUtil;

    public PropertiesUtil getPropertiesUtil() {
        return this.propertiesUtil;
    }

    public ConnectionUtil getConnectionUtil() {
        return this.connectionUtil;
    }

    public AuthenticationUtil getAuthenticationUtil() {
        return this.authenticationUtil;
    }

    public GitUtil getGitUtil() {
        return this.gitUtil;
    }

    public HeaderUtil getHeaderUtil() {
        return this.headerUtil;
    }

    public ParameterUtil getParameterUtil() {
        return this.parameterUtil;
    }

    public ExceptionUtil getExceptionUtil() {
        return this.exceptionUtil;
    }

    public SoapUtil getSoapUtil() {
        return this.soapUtil;
    }

    public JsonUtil getJsonUtil() {
        return this.jsonUtil;
    }

    public PreferencesUtil getPreferencesUtil() {
        return this.preferencesUtil;
    }

    public ProxyUtil getProxyUtil() {
        return this.proxyUtil;
    }

    public ThreadUtil getThreadUtil() {
        return this.threadUtil;
    }

    public TamperingUtil getTamperingUtil() {
        return this.tamperingUtil;
    }

    public void setPropertiesUtil(PropertiesUtil propertiesUtil) {
        this.propertiesUtil = propertiesUtil;
    }

    public void setConnectionUtil(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void setAuthenticationUtil(AuthenticationUtil authenticationUtil) {
        this.authenticationUtil = authenticationUtil;
    }

    public void setGitUtil(GitUtil gitUtil) {
        this.gitUtil = gitUtil;
    }

    public void setHeaderUtil(HeaderUtil headerUtil) {
        this.headerUtil = headerUtil;
    }

    public void setParameterUtil(ParameterUtil parameterUtil) {
        this.parameterUtil = parameterUtil;
    }

    public void setExceptionUtil(ExceptionUtil exceptionUtil) {
        this.exceptionUtil = exceptionUtil;
    }

    public void setSoapUtil(SoapUtil soapUtil) {
        this.soapUtil = soapUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public void setPreferencesUtil(PreferencesUtil preferencesUtil) {
        this.preferencesUtil = preferencesUtil;
    }

    public void setProxyUtil(ProxyUtil proxyUtil) {
        this.proxyUtil = proxyUtil;
    }

    public void setThreadUtil(ThreadUtil threadUtil) {
        this.threadUtil = threadUtil;
    }

    public void setTamperingUtil(TamperingUtil tamperingUtil) {
        this.tamperingUtil = tamperingUtil;
    }

}
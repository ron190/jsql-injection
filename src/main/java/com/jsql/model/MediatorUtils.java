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

    InjectionModel injectionModel;
    public MediatorUtils(InjectionModel injectionModel) {
//        this.injectionModel = injectionModel;
//        this.propertiesUtil = new PropertiesUtil(this.injectionModel);
//        this.connectionUtil = new ConnectionUtil(this.injectionModel);
//        this.authenticationUtil = new AuthenticationUtil(this.injectionModel);
//        this.gitUtil = new GitUtil(this.injectionModel);
//        this.headerUtil = new HeaderUtil(this.injectionModel);
//        this.parameterUtil = new ParameterUtil(this.injectionModel);
//        this.exceptionUtil = new ExceptionUtil(this.injectionModel);
//        this.soapUtil = new SoapUtil(this.injectionModel);
//        this.jsonUtil = new JsonUtil(this.injectionModel);
//        this.preferencesUtil = new PreferencesUtil(this.injectionModel);
//        this.proxyUtil = new ProxyUtil(this.injectionModel);
//        this.threadUtil = new ThreadUtil(this.injectionModel);
//        this.tamperingUtil = new TamperingUtil(this.injectionModel);
    }

    public PropertiesUtil getPropertiesUtil() {
        return propertiesUtil;
    }

    public ConnectionUtil getConnectionUtil() {
        return connectionUtil;
    }

    public AuthenticationUtil getAuthenticationUtil() {
        return authenticationUtil;
    }

    public GitUtil getGitUtil() {
        return gitUtil;
    }

    public HeaderUtil getHeaderUtil() {
        return headerUtil;
    }

    public ParameterUtil getParameterUtil() {
        return parameterUtil;
    }

    public ExceptionUtil getExceptionUtil() {
        return exceptionUtil;
    }

    public SoapUtil getSoapUtil() {
        return soapUtil;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public PreferencesUtil getPreferencesUtil() {
        return preferencesUtil;
    }

    public ProxyUtil getProxyUtil() {
        return proxyUtil;
    }

    public ThreadUtil getThreadUtil() {
        return threadUtil;
    }

    public TamperingUtil getTamperingUtil() {
        return tamperingUtil;
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
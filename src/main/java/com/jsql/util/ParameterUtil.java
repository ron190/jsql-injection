package com.jsql.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;

public class ParameterUtil {

    private ParameterUtil() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Query string built from the URL submitted by user.
     */
    private static List<SimpleEntry<String, String>> queryString = new ArrayList<>();

    /**
     * Request submitted by user.
     */
    private static List<SimpleEntry<String, String>> request = new ArrayList<>();

    /**
     * Header submitted by user.
     */
    private static List<SimpleEntry<String, String>> header = new ArrayList<>();
    
    public static String checkParametersFormat(boolean isTest, boolean checkAllParameters, SimpleEntry<String, String> parameter) throws InjectionFailureException {
        int nbStarInParameter = 0;
        String characterInsertionByUser = "";
        
        if (ParameterUtil.getQueryStringAsString().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        if (ParameterUtil.getRequestAsString().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        if (ParameterUtil.getHeaderAsString().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        
        // Injection Point
        if (nbStarInParameter >= 2) {
            throw new InjectionFailureException("Character * must be used once in Query String, Request or Header parameters");
            
        } else if (
            ParameterUtil.getQueryStringAsString().contains(InjectionModel.STAR)
            && ConnectionUtil.getMethodInjection() != MethodInjection.QUERY
            && !PreferencesUtil.isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select method GET to use character [*] or remove [*] from GET parameters");
            
        } else if (
            ParameterUtil.getRequestAsString().contains(InjectionModel.STAR)
            && ConnectionUtil.getMethodInjection() != MethodInjection.REQUEST
            && !PreferencesUtil.isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select a Request method (like POST) to use [*], or remove [*] from Request parameters");
            
        } else if (
            ParameterUtil.getHeaderAsString().contains(InjectionModel.STAR)
            && ConnectionUtil.getMethodInjection() != MethodInjection.HEADER
            && !PreferencesUtil.isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select method Header to use character [*] or remove [*] from Header parameters");
            
        }
        
        // Query String
        else if (
            ConnectionUtil.getMethodInjection() == MethodInjection.QUERY
            && ParameterUtil.getQueryString().isEmpty()
            && !ConnectionUtil.getUrlBase().contains(InjectionModel.STAR)
        ) {
            throw new InjectionFailureException("No query string");
            
        } else if (
            ParameterUtil.getQueryString().stream().anyMatch(e -> e.getKey().matches("[^\\w]*"))
        ) {
            throw new InjectionFailureException("Incorrect Query String");
            
        }
        
        // Request/Header data
        else if (
            ConnectionUtil.getMethodInjection() == MethodInjection.REQUEST
            && ParameterUtil.getRequest().isEmpty()
        ) {
            throw new InjectionFailureException("Incorrect Request format");
            
        } else if (
            ConnectionUtil.getMethodInjection() == MethodInjection.HEADER
            && ParameterUtil.getHeader().isEmpty()
        ) {
            throw new InjectionFailureException("Incorrect Header format");
            
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.QUERY) {
            if (
                !checkAllParameters
                && (
                    ParameterUtil.getQueryStringAsString().contains(InjectionModel.STAR)
                    || ConnectionUtil.getUrlBase().contains(InjectionModel.STAR)
                )
            ) {
                if (!isTest && parameter != null) {
                    parameter.setValue(InjectionModel.STAR);
                }
                return InjectionModel.STAR;
            } else if (parameter != null) {
                characterInsertionByUser = parameter.getValue();
                parameter.setValue(InjectionModel.STAR);
            }
            
        // Parse post information
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.REQUEST) {
            if (
                !checkAllParameters
                && ParameterUtil.getRequestAsString().contains(InjectionModel.STAR)
            ) {
                if (!isTest && parameter != null) {
                    parameter.setValue(InjectionModel.STAR);
                }
                return InjectionModel.STAR;
            } else if (parameter != null) {
                characterInsertionByUser = parameter.getValue();
                parameter.setValue(InjectionModel.STAR);
            }
        // Parse header information
        } else if (ConnectionUtil.getMethodInjection() == MethodInjection.HEADER) {
            if (
                !checkAllParameters
                && ParameterUtil.getHeaderAsString().contains(InjectionModel.STAR)
            ) {
                if (!isTest && parameter != null) {
                    parameter.setValue(InjectionModel.STAR);
                }
                return InjectionModel.STAR;
            } else if (parameter != null) {
                characterInsertionByUser = parameter.getValue();
                parameter.setValue(InjectionModel.STAR);
            }
        }
        
        return characterInsertionByUser;
    }
    
    // Getters / setters
    
    public static List<SimpleEntry<String, String>> getQueryString() {
        return queryString;
    }
    
    public static String getQueryStringAsString() {
        return queryString.stream().map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.joining("&"));
    }

    public static void setQueryString(List<SimpleEntry<String, String>> queryString) {
        ParameterUtil.queryString = queryString;
    }

    public static List<SimpleEntry<String, String>> getRequest() {
        return request;
    }
    public static String getRequestAsString() {
        return request.stream().map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.joining("&"));
    }

    public static void setRequest(List<SimpleEntry<String, String>> request) {
        ParameterUtil.request = request;
    }

    public static List<SimpleEntry<String, String>> getHeader() {
        return header;
    }
    public static String getHeaderAsString() {
        return header.stream().map(e -> e.getKey()+":"+e.getValue()).collect(Collectors.joining("\\r\\n"));
    }

    public static void setHeader(List<SimpleEntry<String, String>> header) {
        ParameterUtil.header = header;
    }

}

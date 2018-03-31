package com.jsql.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;

public class ParameterUtil {
    
    /**
     * Query string built from the URL submitted by user.
     */
    private static List<SimpleEntry<String, String>> queryString = new ArrayList<>();

    /**
     * Request submitted by user.
     */
    private static List<SimpleEntry<String, String>> request = new ArrayList<>();
    private static String requestAsText = "";

    /**
     * Header submitted by user.
     */
    private static List<SimpleEntry<String, String>> header = new ArrayList<>();

    // Utility class
    private ParameterUtil() {
        // nothing
    }
    
    /**
     * Verify integrity of parameters defined by user.
     * @param isTest true if only cheking general integrity at the start of process
     * @param isParamByUser true if no injection point is defined
     * @param parameter currently injected from Query/Request/Header, is null if simply tests integrity
     * @return either the original insertion character by user, or the STAR, to then be tested against insertion chars
     * @throws InjectionFailureException when params' integrity is failure
     */
    // TODO merge isTest with parameter: isTest = parameter == null
    public static String checkParametersFormat(boolean isTest, boolean isParamByUser, SimpleEntry<String, String> parameter) throws InjectionFailureException {
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
            && !PreferencesUtil.isCheckingAllParam()
            && ParameterUtil.getQueryString().isEmpty()
            && !ConnectionUtil.getUrlBase().contains(InjectionModel.STAR)
        ) {
            throw new InjectionFailureException("No query string");
            
        } else if (
//            ParameterUtil.getQueryString().stream().anyMatch(e -> e.getKey().matches("[^\\w]*"))
            parameter != null
            && (parameter.getKey() == null || parameter.getKey().isEmpty())
        ) {
//            throw new InjectionFailureException("Incorrect Query String");
            throw new InjectionFailureException("Ignoring empty parameter "+ parameter.toString().replace(InjectionModel.STAR, ""));
            
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
                !isParamByUser
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
                !isParamByUser
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
                !isParamByUser
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

    public static void initQueryString(String urlQuery) throws MalformedURLException {
        URL url = new URL(urlQuery);
        if ("".equals(urlQuery) || "".equals(url.getHost())) {
            throw new MalformedURLException("empty URL");
        }
        
        ConnectionUtil.setUrlByUser(urlQuery);
        
        // Parse url and GET query string
        ParameterUtil.setQueryString(new ArrayList<SimpleEntry<String, String>>());
        Matcher regexSearch = Pattern.compile("(.*\\?)(.*)").matcher(urlQuery);
        if (regexSearch.find()) {
            ConnectionUtil.setUrlBase(regexSearch.group(1));
            if (!"".equals(url.getQuery())) {
                ParameterUtil.setQueryString(
                    Pattern.compile("&").splitAsStream(regexSearch.group(2))
                    .map(s -> Arrays.copyOf(s.split("="), 2))
                    .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                    .collect(Collectors.toList())
                );
            }
        } else {
            ConnectionUtil.setUrlBase(urlQuery);
        }
    }

    public static void initRequest(String request) {
        ParameterUtil.requestAsText = request;
        
        ParameterUtil.request =
            Pattern
                .compile("&")
                .splitAsStream(request)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                .collect(Collectors.toList())
        ;
    }

    public static void initHeader(String header) {
        ParameterUtil.setHeader(
            Pattern
                .compile("\\\\r\\\\n")
                .splitAsStream(header)
                .map(s -> Arrays.copyOf(s.split(":"), 2))
                .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                .collect(Collectors.toList())
        );
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

    public static String getRequestAsText() {
        return requestAsText;
    }

}

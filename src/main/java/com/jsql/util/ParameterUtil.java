package com.jsql.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;

public class ParameterUtil {
    
    /**
     * Query string built from the URL submitted by user.
     */
    private List<SimpleEntry<String, String>> queryString = new ArrayList<>();

    /**
     * Request submitted by user.
     */
    private List<SimpleEntry<String, String>> request = new ArrayList<>();
    
    private String requestAsText = "";

    /**
     * Header submitted by user.
     */
    private List<SimpleEntry<String, String>> header = new ArrayList<>();

    // Utility class
    private ParameterUtil() {
        // nothing
    }
    
    public ParameterUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Verify integrity of parameters defined by user.
     * @param isTest true if only cheking general integrity at the start of process
     * @param isParamByUser true if no injection point is defined
     * @param parameter currently injected from Query/Request/Header, is null if simply tests integrity
     * @throws InjectionFailureException when params' integrity is failure
     */
    // TODO merge isTest with parameter: isTest = parameter == null
    public void checkParametersFormat() throws InjectionFailureException {
        int nbStarInParameter = 0;
        
        if (getQueryStringFromEntries().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        if (getRequestFromEntries().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        if (getHeaderFromEntries().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        
        // Injection Point
        if (nbStarInParameter >= 2) {
            throw new InjectionFailureException("Character * must be used once in Query String, Request or Header parameters");
            
        } else if (
            getQueryStringFromEntries().contains(InjectionModel.STAR)
            && injectionModel.connectionUtil.getMethodInjection() != injectionModel.QUERY
            && !injectionModel.preferencesUtil.isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select method GET to use character [*] or remove [*] from GET parameters");
            
        } else if (
            getRequestFromEntries().contains(InjectionModel.STAR)
            && injectionModel.connectionUtil.getMethodInjection() != injectionModel.REQUEST
            && !injectionModel.preferencesUtil.isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select a Request method (like POST) to use [*], or remove [*] from Request parameters");
            
        } else if (
            getHeaderFromEntries().contains(InjectionModel.STAR)
            && injectionModel.connectionUtil.getMethodInjection() != injectionModel.HEADER
            && !injectionModel.preferencesUtil.isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select method Header to use character [*] or remove [*] from Header parameters");
        }
        
        // Query String
        else if (
            injectionModel.connectionUtil.getMethodInjection() == injectionModel.QUERY
            && !injectionModel.preferencesUtil.isCheckingAllParam()
            && getQueryString().isEmpty()
            && !injectionModel.connectionUtil.getUrlBase().contains(InjectionModel.STAR)
        ) {
            throw new InjectionFailureException("No query string");
        }
        
        // Request/Header data
        else if (
            injectionModel.connectionUtil.getMethodInjection() == injectionModel.REQUEST
            && getRequest().isEmpty()
        ) {
            throw new InjectionFailureException("Incorrect Request format");
            
        } else if (
            injectionModel.connectionUtil.getMethodInjection() == injectionModel.HEADER
            && getHeader().isEmpty()
        ) {
            throw new InjectionFailureException("Incorrect Header format");
            
        }
    }
    
    public String getCharacterInsertion(boolean isParamByUser, SimpleEntry<String, String> parameter) throws InjectionFailureException {
        String characterInsertionByUser = "";
        
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        if (injectionModel.connectionUtil.getMethodInjection() == injectionModel.QUERY) {
            if (
                !isParamByUser
                && (
                    getQueryStringFromEntries().contains(InjectionModel.STAR)
                    || injectionModel.connectionUtil.getUrlBase().contains(InjectionModel.STAR)
                )
            ) {
                if (parameter != null) {
                    parameter.setValue(InjectionModel.STAR);
                }
                return InjectionModel.STAR;
            } else if (parameter != null) {
                characterInsertionByUser = parameter.getValue();
                parameter.setValue(InjectionModel.STAR);
            }
            
        // Parse post information
        } else if (injectionModel.connectionUtil.getMethodInjection() == injectionModel.REQUEST) {
            if (
                !isParamByUser
                && getRequestFromEntries().contains(InjectionModel.STAR)
            ) {
                if (parameter != null) {
                    parameter.setValue(InjectionModel.STAR);
                }
                return InjectionModel.STAR;
            } else if (parameter != null) {
                characterInsertionByUser = parameter.getValue();
                parameter.setValue(InjectionModel.STAR);
            }
            
        // Parse header information
        } else if (injectionModel.connectionUtil.getMethodInjection() == injectionModel.HEADER) {
            if (
                !isParamByUser
                && getHeaderFromEntries().contains(InjectionModel.STAR)
            ) {
                if (parameter != null) {
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
    
    public String getQueryStringFromEntries() {
        return queryString.stream().filter(Objects::nonNull).map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.joining("&"));
    }

    public String getRequestFromEntries() {
        return request.stream().filter(Objects::nonNull).map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.joining("&"));
    }
    
    public String getHeaderFromEntries() {
        return header.stream().filter(Objects::nonNull).map(e -> e.getKey()+":"+e.getValue()).collect(Collectors.joining("\\r\\n"));
    }

    public void initQueryString(String urlQuery) throws MalformedURLException {
        URL url = new URL(urlQuery);
        if ("".equals(urlQuery) || "".equals(url.getHost())) {
            throw new MalformedURLException("empty URL");
        }
        
        injectionModel.connectionUtil.setUrlByUser(urlQuery);
        
        // Parse url and GET query string
        setQueryString(new ArrayList<SimpleEntry<String, String>>());
        Matcher regexSearch = Pattern.compile("(.*\\?)(.*)").matcher(urlQuery);
        if (regexSearch.find()) {
            injectionModel.connectionUtil.setUrlBase(regexSearch.group(1));
            if (!"".equals(url.getQuery())) {
                setQueryString(
                    Pattern.compile("&").splitAsStream(regexSearch.group(2))
                    .map(s -> Arrays.copyOf(s.split("="), 2))
                    .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                    .collect(Collectors.toList())
                );
            }
        } else {
            injectionModel.connectionUtil.setUrlBase(urlQuery);
        }
    }

    public void initRequest(String request) {
        this.requestAsText = request;
        
        if (!"".equals(request)) {
            this.request = 
                Pattern
                .compile("&")
                .splitAsStream(request)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                .collect(Collectors.toList())
            ;
        }
    }

    public void initHeader(String header) {
        if (!"".equals(header)) {
            setHeader(
                Pattern
                .compile("\\\\r\\\\n")
                .splitAsStream(header)
                .map(s -> Arrays.copyOf(s.split(":"), 2))
                .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                .collect(Collectors.toList())
            );
        }
    }

    public boolean isRequestSoap() {
        return requestAsText.trim().matches("^<\\?xml.*");
    }

    // Getters / setters
    
    public List<SimpleEntry<String, String>> getRequest() {
        return request;
    }

    public void setRequest(List<SimpleEntry<String, String>> request) {
        this.request = request;
    }

    public List<SimpleEntry<String, String>> getHeader() {
        return header;
    }

    public void setHeader(List<SimpleEntry<String, String>> header) {
        this.header = header;
    }
    
    public List<SimpleEntry<String, String>> getQueryString() {
        return queryString;
    }
    
    public void setQueryString(List<SimpleEntry<String, String>> queryString) {
        this.queryString = queryString;
    }

    public String getRawRequest() {
        return requestAsText;
    }

}

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
        
        if (this.getQueryStringFromEntries().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        if (this.getRequestFromEntries().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        if (this.getHeaderFromEntries().contains(InjectionModel.STAR)) {
            nbStarInParameter++;
        }
        
        // Injection Point
        if (nbStarInParameter >= 2) {
            throw new InjectionFailureException("Character * must be used once in Query String, Request or Header parameters");
            
        } else if (
            this.getQueryStringFromEntries().contains(InjectionModel.STAR)
            && this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() != this.injectionModel.QUERY
            && !this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select method GET to use character [*] or remove [*] from GET parameters");
            
        } else if (
            this.getRequestFromEntries().contains(InjectionModel.STAR)
            && this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() != this.injectionModel.REQUEST
            && !this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select a Request method (like POST) to use [*], or remove [*] from Request parameters");
            
        } else if (
            this.getHeaderFromEntries().contains(InjectionModel.STAR)
            && this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() != this.injectionModel.HEADER
            && !this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam()
        ) {
            throw new InjectionFailureException("Select method Header to use character [*] or remove [*] from Header parameters");
        }
        
        // Query String
        else if (
            this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() == this.injectionModel.QUERY
            && !this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam()
            && this.getQueryString().isEmpty()
            && !this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)
        ) {
            throw new InjectionFailureException("No query string");
        }
        
        // Request/Header data
        else if (
            this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() == this.injectionModel.REQUEST
            && this.getRequest().isEmpty()
        ) {
            throw new InjectionFailureException("Incorrect Request format");
            
        } else if (
            this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() == this.injectionModel.HEADER
            && this.getHeader().isEmpty()
        ) {
            throw new InjectionFailureException("Incorrect Header format");
            
        }
    }
    
    public String getCharacterInsertion(boolean isParamByUser, SimpleEntry<String, String> parameter) throws InjectionFailureException {
        String characterInsertionByUser = "";
        
        // Parse query information: url=>everything before the sign '=',
        // start of query string=>everything after '='
        if (this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() == this.injectionModel.QUERY) {
            if (
                !isParamByUser
                && (
                    this.getQueryStringFromEntries().contains(InjectionModel.STAR)
                    || this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)
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
        } else if (this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() == this.injectionModel.REQUEST) {
            if (
                !isParamByUser
                && this.getRequestFromEntries().contains(InjectionModel.STAR)
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
        } else if (this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() == this.injectionModel.HEADER) {
            if (
                !isParamByUser
                && this.getHeaderFromEntries().contains(InjectionModel.STAR)
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
        return this.queryString.stream().filter(Objects::nonNull).map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.joining("&"));
    }

    public String getRequestFromEntries() {
        return this.request.stream().filter(Objects::nonNull).map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.joining("&"));
    }
    
    public String getHeaderFromEntries() {
        return this.header.stream().filter(Objects::nonNull).map(e -> e.getKey()+":"+e.getValue()).collect(Collectors.joining("\\r\\n"));
    }

    public void initQueryString(String urlQuery) throws MalformedURLException {
        URL url = new URL(urlQuery);
        if ("".equals(urlQuery) || "".equals(url.getHost())) {
            throw new MalformedURLException("empty URL");
        }
        
        this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlByUser(urlQuery);
        
        // Parse url and GET query string
        this.setQueryString(new ArrayList<SimpleEntry<String, String>>());
        Matcher regexSearch = Pattern.compile("(.*\\?)(.*)").matcher(urlQuery);
        if (regexSearch.find()) {
            this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlBase(regexSearch.group(1));
            if (!"".equals(url.getQuery())) {
                this.setQueryString(
                    Pattern.compile("&").splitAsStream(regexSearch.group(2))
                    .map(s -> Arrays.copyOf(s.split("="), 2))
                    .map(o -> new SimpleEntry<>(o[0], o[1] == null ? "" : o[1]))
                    .collect(Collectors.toList())
                );
            }
        } else {
            this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlBase(urlQuery);
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
                .map(o -> new SimpleEntry<>(o[0], o[1] == null ? "" : o[1]))
                .collect(Collectors.toList())
            ;
        }
    }

    public void initHeader(String header) {
        if (!"".equals(header)) {
            this.setHeader(
                Pattern
                .compile("\\\\r\\\\n")
                .splitAsStream(header)
                .map(s -> Arrays.copyOf(s.split(":"), 2))
                .map(o -> new SimpleEntry<>(o[0], o[1] == null ? "" : o[1]))
                .collect(Collectors.toList())
            );
        }
    }

    public boolean isRequestSoap() {
        return this.requestAsText.trim().matches("^<\\?xml.*");
    }

    // Getters / setters
    
    public List<SimpleEntry<String, String>> getRequest() {
        return this.request;
    }

    public void setRequest(List<SimpleEntry<String, String>> request) {
        this.request = request;
    }

    public List<SimpleEntry<String, String>> getHeader() {
        return this.header;
    }

    public void setHeader(List<SimpleEntry<String, String>> header) {
        this.header = header;
    }
    
    public List<SimpleEntry<String, String>> getQueryString() {
        return this.queryString;
    }
    
    public void setQueryString(List<SimpleEntry<String, String>> queryString) {
        this.queryString = queryString;
    }

    public String getRawRequest() {
        return this.requestAsText;
    }

}

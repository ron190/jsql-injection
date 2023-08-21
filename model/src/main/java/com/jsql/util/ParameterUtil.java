package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.AbstractMethodInjection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class ParameterUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Query string built from the URL submitted by user.
     */
    private List<SimpleEntry<String, String>> listQueryString = new ArrayList<>();

    /**
     * Request submitted by user.
     */
    private List<SimpleEntry<String, String>> listRequest = new ArrayList<>();

    /**
     * Header submitted by user.
     */
    private List<SimpleEntry<String, String>> listHeader = new ArrayList<>();
    
    private String rawRequest = StringUtils.EMPTY;
    private String rawHeader = StringUtils.EMPTY;
    private boolean isMultipartRequest = false;

    private final InjectionModel injectionModel;
    
    public ParameterUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }
    
    /**
     * Send each parameters from the GUI to the model in order to
     * start the preparation of injection, the injection process is
     * started in a new thread via model function inputValidation().
     */
    public void controlInput(
        String urlQuery,
        String rawRequest,
        String rawHeader,
        AbstractMethodInjection methodInjection,
        String typeRequest,
        boolean isScanning
    ) {
        try {
            String urlQueryFixed = urlQuery;
               
            // Keep single check
            if (!urlQueryFixed.isEmpty() && !urlQueryFixed.matches("(?i)^https?://.*")) {
                
                if (!urlQueryFixed.matches("(?i)^\\w+://.*")) {
                    
                    LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Undefined URL protocol, forcing to [http://]");
                    urlQueryFixed = "http://"+ urlQueryFixed;
                    
                } else {
                    
                    throw new MalformedURLException("unknown URL protocol");
                }
            }
                     
            this.initializeQueryString(urlQueryFixed);
            this.initializeHeader(rawHeader);
            this.initializeRequest(rawRequest);

            this.injectionModel.getMediatorUtils().getConnectionUtil().setMethodInjection(methodInjection);
            this.injectionModel.getMediatorUtils().getConnectionUtil().setTypeRequest(typeRequest);
            
            if (isScanning) {
                
                this.injectionModel.beginInjection();
                
            } else {
                
                // Start the model injection process in a thread
                new Thread(
                    this.injectionModel::beginInjection,
                    "ThreadBeginInjection"
                )
                .start();
            }
            
        } catch (MalformedURLException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect Url: {}", e.getMessage());
            
            // Incorrect URL, reset the start button
            var request = new Request();
            request.setMessage(Interaction.END_PREPARATION);
            this.injectionModel.sendToViews(request);
        }
    }

    /**
     * Check integrity of parameters defined by user.
     * @throws InjectionFailureException when params integrity is failure
     */
    public void checkParametersFormat() throws InjectionFailureException {
        
        this.checkOneOrLessStar();
        this.checkStarMatchMethod();
        this.checkMethodNotEmpty();
        this.checkMultipart();
    }

    private void checkMultipart() throws InjectionFailureException {

        isMultipartRequest = false;

        if (
            this.getListHeader()
            .stream()
            .filter(entry -> "Content-Type".equals(entry.getKey()))
            .anyMatch(entry ->
                entry.getValue() != null
                && entry.getValue().contains("multipart/form-data")
                && entry.getValue().contains("boundary=")
            )
        ) {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Multipart boundary found in header");
            Matcher matcherBoundary = Pattern.compile("boundary=([^;]*)").matcher(this.getHeaderFromEntries());
            if (matcherBoundary.find()) {
                String boundary = matcherBoundary.group(1);
                if (!this.rawRequest.contains(boundary)) {
                    throw new InjectionFailureException(
                        String.format("Incorrect multipart data, boundary not found in body: %s", boundary)
                    );
                } else {
                    isMultipartRequest = true;
                }
            }
        }
    }

    private void checkOneOrLessStar() throws InjectionFailureException {
        
        var nbStarInParameter = 0;
        
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
            
            throw new InjectionFailureException("Character insertion [*] must be used once in Query String, Request or Header parameters");
        }
    }
    
    public void checkStarMatchMethod() throws InjectionFailureException {
        
        AbstractMethodInjection methodInjection = this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection();
        boolean isCheckingAllParam = this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam();

        if (
            this.getQueryStringFromEntries().contains(InjectionModel.STAR)
            && methodInjection != this.injectionModel.getMediatorMethod().getQuery()
            && !isCheckingAllParam
        ) {
            
            throw new InjectionFailureException("Select method GET to use character [*] or remove [*] from GET parameters");
            
        } else if (
            this.getRequestFromEntries().contains(InjectionModel.STAR)
            && methodInjection != this.injectionModel.getMediatorMethod().getRequest()
            && !isCheckingAllParam
        ) {
            
            throw new InjectionFailureException("Select a Request method (like POST) to use [*], or remove [*] from Request parameters");
            
        } else if (
            this.getHeaderFromEntries().contains(InjectionModel.STAR)
            && methodInjection != this.injectionModel.getMediatorMethod().getHeader()
            && !isCheckingAllParam
        ) {
            
            throw new InjectionFailureException("Select method Header to use character [*] or remove [*] from Header parameters");
        }
    }
    
    public void checkMethodNotEmpty() throws InjectionFailureException {
        
        AbstractMethodInjection methodInjection = this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection();
        boolean isCheckingAllParam = this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam();
        
        if (
            methodInjection == this.injectionModel.getMediatorMethod().getQuery()
            && !isCheckingAllParam
            && this.getListQueryString().isEmpty()
            && !this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)
        ) {
            
            throw new InjectionFailureException("No query string");
        
        } else if (
            methodInjection == this.injectionModel.getMediatorMethod().getRequest()
            && this.getListRequest().isEmpty()
        ) {
            
            throw new InjectionFailureException("Incorrect Request format");
            
        } else if (
            methodInjection == this.injectionModel.getMediatorMethod().getHeader()
            && this.getListHeader().isEmpty()
        ) {
            
            throw new InjectionFailureException("Incorrect Header format");
        }
    }
    
    public String initializeStar(SimpleEntry<String, String> parameterToInject) {
        
        String characterInsertionByUser;
        
        // TODO path param injection
        if (parameterToInject == null) {
            
            characterInsertionByUser = InjectionModel.STAR;
            
        } else {
            
            characterInsertionByUser = parameterToInject.getValue();
            parameterToInject.setValue(InjectionModel.STAR);
        }
        
        return characterInsertionByUser;
    }

    public void initializeQueryString(String urlQuery) throws MalformedURLException {

        // Format and get rid of anchor fragment using native URL
        var url = new URL(urlQuery);
        
        if (
            StringUtils.isEmpty(urlQuery)
            || StringUtils.isEmpty(url.getHost())
        ) {
            
            throw new MalformedURLException("empty URL");
        }
        
        this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlByUser(urlQuery);
        this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlBase(urlQuery);
        
        this.listQueryString.clear();
        
        // Parse url and GET query string
        var regexQueryString = Pattern.compile("(.*\\?)(.*)").matcher(urlQuery);
        
        if (!regexQueryString.find()) {
            
            return;
        }
        
        this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlBase(regexQueryString.group(1));
        
        if (StringUtils.isNotEmpty(url.getQuery())) {
            
            this.listQueryString = Pattern.compile("&")
                .splitAsStream(url.getQuery())
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .map(o -> 
                    new SimpleEntry<>(
                        o[0],
                        o[1] == null
                        ? StringUtils.EMPTY
                        : o[1]
                    )
                )
                .collect(Collectors.toList());
        }
    }

    public void initializeRequest(String rawRequest) {

        this.rawRequest = rawRequest;
        this.listRequest.clear();

        if (StringUtils.isNotEmpty(rawRequest)) {

            if (isMultipartRequest()) {
                // Pass request containing star * param without any parsing
                this.listRequest = new ArrayList<>(
                    List.of(new SimpleEntry<>(
                        rawRequest,
                        ""
                    ))
                );
            } else {
                this.listRequest = Pattern
                    .compile("&")
                    .splitAsStream(rawRequest)
                    .map(s -> Arrays.copyOf(s.split("="), 2))
                    .map(o ->
                        new SimpleEntry<>(
                            o[0],
                            o[1] == null
                            ? StringUtils.EMPTY
                            : o[1]
                        )
                    )
                    .collect(Collectors.toList());
            }
        }
    }

    public void initializeHeader(String rawHeader) {
        
        this.rawHeader = rawHeader;
        this.listHeader.clear();

        if (StringUtils.isNotEmpty(rawHeader)) {
            
            this.listHeader =
                Pattern
                .compile("\\\\r\\\\n")
                .splitAsStream(rawHeader)
                .map(commaEntry ->
                    Arrays.copyOf(
                        commaEntry.split(":"),
                        2
                    )
                )
                .map(arrayEntry ->
                    new SimpleEntry<>(
                        arrayEntry[0],
                        arrayEntry[1] == null
                        ? StringUtils.EMPTY
                        : arrayEntry[1]
                    )
                )
                .collect(Collectors.toList());
        }
    }
    
    public String getQueryStringFromEntries() {
        
        return this.listQueryString.stream()
            .filter(Objects::nonNull)
            .map(entry -> {
                if (
                    this.injectionModel.getMediatorStrategy().getStrategy() == this.injectionModel.getMediatorStrategy().getMultibit()
                    && entry.getValue() != null
                    && entry.getValue().contains(InjectionModel.STAR)
                ) {
                    return String.format("%s=%s", entry.getKey(), InjectionModel.STAR);
                } else {
                    return String.format("%s=%s", entry.getKey(), entry.getValue());
                }
            })
            .collect(Collectors.joining("&"));
    }

    public String getRequestFromEntries() {

        return this.listRequest.stream()
            .filter(Objects::nonNull)
            .map(entry ->
                String.format(
                    "%s=%s",
                    entry.getKey(),
                    StringUtils.isEmpty(entry.getValue()) ? "" : entry.getValue()
                )
            )
            .collect(Collectors.joining("&"));
    }
    
    public String getHeaderFromEntries() {
        
        return this.listHeader
            .stream()
            .filter(Objects::nonNull)
            .map(entry ->
                String.format(
                    "%s:%s",
                    entry.getKey(),
                    entry.getValue()
                )
            )
            .collect(Collectors.joining("\\r\\n"));
    }

    public boolean isRequestSoap() {
        
        return this.rawRequest
            .trim()
            .matches("^(<soapenv:|<\\?xml).*");
    }

    
    // Getters / setters
    
    public String getRawRequest() {
        return this.rawRequest;
    }

    public String getRawHeader() {
        return this.rawHeader;
    }

    public List<SimpleEntry<String, String>> getListRequest() {
        return this.listRequest;
    }

    public void setListRequest(List<SimpleEntry<String, String>> listRequest) {
        this.listRequest = listRequest;
    }

    public List<SimpleEntry<String, String>> getListHeader() {
        return this.listHeader;
    }

    public void setListHeader(List<SimpleEntry<String, String>> listHeader) {
        this.listHeader = listHeader;
    }
    
    public List<SimpleEntry<String, String>> getListQueryString() {
        return this.listQueryString;
    }
    
    public void setListQueryString(List<SimpleEntry<String, String>> listQueryString) {
        this.listQueryString = listQueryString;
    }

    public boolean isMultipartRequest() {
        return isMultipartRequest;
    }
}

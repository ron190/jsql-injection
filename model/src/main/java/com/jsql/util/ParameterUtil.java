package com.jsql.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.AbstractMethodInjection;

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
    
    private String requestAsText = StringUtils.EMPTY;

    private InjectionModel injectionModel;
    
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
        String dataRequest,
        String dataHeader,
        AbstractMethodInjection methodInjection,
        String typeRequest,
        boolean isScanning
    ) {
        try {
            String urlQueryFixed = urlQuery;
               
            // Keep single check
            if (!urlQueryFixed.isEmpty() && !urlQueryFixed.matches("(?i)^https?://.*")) {
                
                if (!urlQueryFixed.matches("(?i)^\\w+://.*")) {
                    
                    LOGGER.log(LogLevel.CONSOLE_INFORM, "Undefined URL protocol, forcing to [http://]");
                    urlQueryFixed = "http://"+ urlQueryFixed;
                    
                } else {
                    
                    throw new MalformedURLException("unknown URL protocol");
                }
            }
                     
            this.initializeQueryString(urlQueryFixed);
            this.initializeRequest(dataRequest);
            this.initializeHeader(dataHeader);
            
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
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Incorrect Url: {}", e.getMessage());
            
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
            
            this.listQueryString = Pattern
                .compile("&")
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

    public void initializeRequest(String request) {
        
        this.requestAsText = request;
        this.listRequest.clear();
        
        if (StringUtils.isNotEmpty(request)) {
            
            this.listRequest =
                Pattern
                .compile("&")
                .splitAsStream(request)
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

    public void initializeHeader(String header) {
        
        this.listHeader.clear();
        
        if (StringUtils.isNotEmpty(header)) {
            
            this.listHeader =
                Pattern
                .compile("\\\\r\\\\n")
                .splitAsStream(header)
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
        
        return
            this.listQueryString
            .stream()
            .filter(Objects::nonNull)
            .map(entry ->
                String.format(
                    "%s=%s",
                    entry.getKey(),
                    entry.getValue()
                )
            )
            .collect(Collectors.joining("&"));
    }

    public String getRequestFromEntries() {
        
        return
            this.listRequest
            .stream()
            .filter(Objects::nonNull)
            .map(entry ->
                String.format(
                    "%s=%s",
                    entry.getKey(),
                    entry.getValue()
                )
            )
            .collect(Collectors.joining("&"));
    }
    
    public String getHeaderFromEntries() {
        
        return
            this.listHeader
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
        
        return
            this.requestAsText
            .trim()
            .matches("^(<soapenv:|<\\?xml).*");
    }

    
    // Getters / setters
    
    public String getRawRequest() {
        return this.requestAsText;
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
}

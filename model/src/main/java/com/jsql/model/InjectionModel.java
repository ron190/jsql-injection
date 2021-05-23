/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.ResourceAccess;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.model.injection.method.MediatorMethod;
import com.jsql.model.injection.strategy.MediatorStrategy;
import com.jsql.model.injection.vendor.MediatorVendor;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.CertificateUtil;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.CsrfUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.FormUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.HeaderUtil;
import com.jsql.util.I18nUtil;
import com.jsql.util.JsonUtil;
import com.jsql.util.LogLevel;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.PropertiesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.util.SoapUtil;
import com.jsql.util.StringUtil;
import com.jsql.util.TamperingUtil;
import com.jsql.util.ThreadUtil;
import com.jsql.util.UserAgentUtil;

/**
 * Model class of MVC pattern for processing SQL injection automatically.<br>
 * Different views can be attached to this observable, like Swing or command line, in order to separate
 * the functional job from the graphical processing.<br>
 * The Model has a specific database vendor and strategy which run an automatic injection to get name of
 * databases, tables, columns and values, and it can also retrieve resources like files and shell.<br>
 * Tasks are run in multi-threads in general to speed the process.
 */
@SuppressWarnings("serial")
public class InjectionModel extends AbstractModelObservable implements Serializable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private transient MediatorVendor mediatorVendor = new MediatorVendor(this);
    private transient MediatorMethod mediatorMethod = new MediatorMethod(this);
    private transient MediatorUtils mediatorUtils;
    private transient MediatorStrategy mediatorStrategy;

    private transient PropertiesUtil propertiesUtil = new PropertiesUtil();
             
    private transient DataAccess dataAccess = new DataAccess(this);
    private transient ResourceAccess resourceAccess = new ResourceAccess(this);
    
    public static final String STAR = "*";
    
    /**
     * initialUrl transformed to a correct injection url.
     */
    private String indexesInUrl = StringUtils.EMPTY;
    
    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    private boolean shouldErasePreviousInjection = false;
    
    private boolean isScanning = false;
    
    public InjectionModel() {
        
        this.mediatorUtils = new MediatorUtils();
        
        this.mediatorStrategy = new MediatorStrategy(this);

        this.mediatorUtils.setCertificateUtil(new CertificateUtil());
        this.mediatorUtils.setPropertiesUtil(this.propertiesUtil);
        this.mediatorUtils.setConnectionUtil(new ConnectionUtil(this));
        this.mediatorUtils.setAuthenticationUtil(new AuthenticationUtil());
        this.mediatorUtils.setGitUtil(new GitUtil(this));
        this.mediatorUtils.setHeaderUtil(new HeaderUtil(this));
        this.mediatorUtils.setParameterUtil(new ParameterUtil(this));
        this.mediatorUtils.setExceptionUtil(new ExceptionUtil(this));
        this.mediatorUtils.setSoapUtil(new SoapUtil(this));
        this.mediatorUtils.setJsonUtil(new JsonUtil(this));
        this.mediatorUtils.setPreferencesUtil(new PreferencesUtil());
        this.mediatorUtils.setProxyUtil(new ProxyUtil(this));
        this.mediatorUtils.setThreadUtil(new ThreadUtil(this));
        this.mediatorUtils.setTamperingUtil(new TamperingUtil());
        this.mediatorUtils.setUserAgentUtil(new UserAgentUtil());
        this.mediatorUtils.setCsrfUtil(new CsrfUtil(this));
        this.mediatorUtils.setFormUtil(new FormUtil(this));
    }

    /**
     * Reset each injection attributes: Database metadata, General Thread status, Strategy.
     */
    public void resetModel() {
        
        this.mediatorStrategy.getNormal().setVisibleIndex(null);
        
        this.mediatorStrategy.getNormal().setApplicable(false);
        this.mediatorStrategy.getError().setApplicable(false);
        this.mediatorStrategy.getBlind().setApplicable(false);
        this.mediatorStrategy.getTime().setApplicable(false);
        
        this.indexesInUrl = StringUtils.EMPTY;
        
        this.mediatorUtils.getCsrfUtil().setTokenCsrf(null);
        
        this.setIsStoppedByUser(false);
        
        this.shouldErasePreviousInjection = false;
        
        this.mediatorStrategy.setStrategy(null);
        
        this.resourceAccess.setReadingIsAllowed(false);
        
        this.mediatorUtils.getThreadUtil().reset();
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     * Run by Scan, Standard and TU.
     */
    public void beginInjection() {
        
        this.resetModel();
        
        try {
            if (!this.mediatorUtils.getProxyUtil().isLive(ShowOnConsole.YES)) {
                
                return;
            }
            
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                "{}: {}",
                () -> I18nUtil.valueByKey("LOG_START_INJECTION"),
                () -> this.mediatorUtils.getConnectionUtil().getUrlByUser()
            );
            
            // Check general integrity if user's parameters
            this.mediatorUtils.getParameterUtil().checkParametersFormat();
            
            this.mediatorUtils.getConnectionUtil().testConnection();
            
            boolean hasFoundInjection = this.mediatorMethod.getQuery().testParameters();

            if (!hasFoundInjection) {
                
                hasFoundInjection = this.mediatorUtils.getSoapUtil().testParameters();
            }
            
            if (!hasFoundInjection) {
                
                LOGGER.log(LogLevel.CONSOLE_DEFAULT, "Checking standard Request parameters");
                hasFoundInjection = this.mediatorMethod.getRequest().testParameters();
            }
            
            if (!hasFoundInjection) {
                
                hasFoundInjection = this.mediatorMethod.getHeader().testParameters();
            }
            
            if (hasFoundInjection && !this.isScanning) {
                
                if (this.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
                    
                    LOGGER.log(LogLevel.CONSOLE_INFORM, "Using Zip strategy for minimal query size");
                    
                } else if (this.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
                    
                    LOGGER.log(LogLevel.CONSOLE_INFORM, "Using Dump In One Shot strategy for single query dump");
                }
                
                if (!this.mediatorUtils.getPreferencesUtil().isNotInjectingMetadata()) {
                    
                    this.dataAccess.getDatabaseInfos();
                }
                
                this.dataAccess.listDatabases();
            }
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("LOG_DONE"));
            
            this.shouldErasePreviousInjection = true;
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
            
        } catch (Exception e) {  // Catch all exceptions like interrupt, URL format and JSqlException
            
            String eMessageImplicit = "Problem connecting to URL (implicit reason): "+ getImplicitReason(e);
            
            String eMessage = Optional.ofNullable(e.getMessage()).orElse(eMessageImplicit);

            LOGGER.log(LogLevel.CONSOLE_ERROR, eMessage);
            
        } finally {
            
            var request = new Request();
            request.setMessage(Interaction.END_PREPARATION);
            this.sendToViews(request);
        }
    }
    
    public static String getImplicitReason(Throwable e) {
        
        String eMessage = e.getClass().getSimpleName();
        
        if (e.getMessage() != null) {
            
            eMessage += ": "+ e.getMessage();
        }
        
        if (e.getCause() != null && !e.equals(e.getCause())) {
            
            eMessage += " > "+ getImplicitReason(e.getCause());
        }
        
        return eMessage;
    }
    
    /**
     * Run a HTTP connection to the web server.
     * @param newDataInjection SQL query
     * @return source code of current page
     */
    @Override
    public String inject(String newDataInjection, boolean isUsingIndex, String metadataInjectionProcess) {
        
        // Temporary url, we go from "select 1,2,3,4..." to "select 1,([complex query]),2...", but keep initial url
        String urlInjection = this.mediatorUtils.getConnectionUtil().getUrlBase();
        
        String dataInjection = StringUtils.SPACE + newDataInjection;
        
        urlInjection = this.mediatorStrategy.buildURL(urlInjection, isUsingIndex, dataInjection);
        
        urlInjection = StringUtil.clean(urlInjection.trim());

        URL urlObject = null;
        
        // TODO Keep only a single check
        try {
            urlObject = new URL(urlInjection);
            
        } catch (MalformedURLException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Incorrect Query Url: %s", e.getMessage())
            );
            return StringUtils.EMPTY;
        }

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);

        // TODO identique urlInjection == urlObject
        urlObject = this.initializeQueryString(
            isUsingIndex,
            urlInjection,
            dataInjection,
            urlObject,
            msgHeader
        );
        
        String pageSource = StringUtils.EMPTY;
        
        // Define the connection
        try {
            var httpRequestBuilder =
                HttpRequest
                    .newBuilder()
                    .uri(URI.create(urlObject.toString()))
                    .setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "text/plain")
                    .timeout(Duration.ofSeconds(15));
            
            this.mediatorUtils.getCsrfUtil().addHeaderToken(httpRequestBuilder);
            
            this.mediatorUtils.getConnectionUtil().setCustomUserAgent(httpRequestBuilder);
            
            this.initializeHeader(isUsingIndex, dataInjection, httpRequestBuilder);
            this.initializeRequest(isUsingIndex, dataInjection, httpRequestBuilder, msgHeader);
            
            var httpRequest = httpRequestBuilder.build();
            
            HttpResponse<String> response = this.getMediatorUtils().getConnectionUtil().getHttpClient().send(
                httpRequestBuilder.build(),
                BodyHandlers.ofString()
            );
            pageSource = response.body();
            
            Map<String, String> headers = ConnectionUtil.getHeadersMap(response);
            
            msgHeader.put(Header.RESPONSE, headers);
            msgHeader.put(Header.HEADER, ConnectionUtil.getHeadersMap(httpRequest.headers()));
            
            int sizeHeaders = headers
                .keySet()
                .stream()
                .map(key -> headers.get(key).length() + key.length())
                .mapToInt(Integer::intValue)
                .sum();
            
            float size = (float) (pageSource.length() + sizeHeaders) / 1024;
            var decimalFormat = new DecimalFormat("0.000");
            msgHeader.put(Header.PAGE_SIZE, decimalFormat.format(size));
            
            if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
                
                pageSource = StringUtil.fromHtml(pageSource);
            }
            
            msgHeader.put(
                Header.SOURCE,
                pageSource
                .replaceAll("(#){60,}", "$1...")  // Remove ranges of # created by calibration
                .replaceAll("(jIyM){60,}", "$1...")  // Remove batch of chars created by Dios
            );
            msgHeader.put(Header.METADATA_PROCESS, metadataInjectionProcess);
            msgHeader.put(Header.METADATA_STRATEGY, this.mediatorStrategy.getMeta());
            
            // Send data to Views
            var request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.sendToViews(request);
            
        } catch (IOException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Error during connection: %s", e.getMessage())
            );
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }

        // return the source code of the page
        return pageSource;
    }

    private URL initializeQueryString(
        boolean isUsingIndex,
        String urlInjection,
        String dataInjection,
        URL urlObject,
        Map<Header, Object> msgHeader
    ) {
        
        String urlInjectionFixed = urlInjection;
        var urlObjectFixed = urlObject;
        
        if (
            this.mediatorUtils.getParameterUtil().getListQueryString().isEmpty()
            && !this.mediatorUtils.getPreferencesUtil().isProcessingCsrf()
        ) {

            msgHeader.put(Header.URL, urlInjectionFixed);
            return urlObjectFixed;
        }
            
        // URL without query string like Request and Header can receive
        // new params from <form> parsing, in that case add the '?' to URL
        if (!urlInjectionFixed.contains("?")) {
            
            urlInjectionFixed += "?";
        }

        urlInjectionFixed += this.buildQuery(
            this.mediatorMethod.getQuery(),
            this.mediatorUtils.getParameterUtil().getQueryStringFromEntries(),
            isUsingIndex,
            dataInjection
        );

        urlInjectionFixed = this.mediatorUtils.getCsrfUtil().addQueryStringToken(urlInjectionFixed);
        
        // TODO Keep single check
        try {
            urlObjectFixed = new URL(urlInjectionFixed);
            
        } catch (MalformedURLException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                String.format("Incorrect Url: %s", e.getMessage())
            );
        }

        msgHeader.put(Header.URL, urlInjectionFixed);
        
        return urlObjectFixed;
    }

    private void initializeHeader(
        boolean isUsingIndex,
        String dataInjection,
        Builder httpRequest
    ) {
        
        if (!this.mediatorUtils.getParameterUtil().getListHeader().isEmpty()) {
            
            Stream
            .of(
                this.buildQuery(
                    this.mediatorMethod.getHeader(),
                    this.mediatorUtils.getParameterUtil().getHeaderFromEntries(),
                    isUsingIndex,
                    dataInjection
                )
                .split("\\\\r\\\\n")
            )
            .forEach(e -> {
                
                if (e.split(":").length == 2) {
                    
                    HeaderUtil.sanitizeHeaders(
                        httpRequest,
                        new SimpleEntry<>(
                            e.split(":")[0],
                            e.split(":")[1]
                        )
                    );
                }
            });
        }
    }

    private void initializeRequest(
        boolean isUsingIndex,
        String dataInjection,
        Builder httpRequest,
        Map<Header, Object> msgHeader
    ) {
        
        if (
            this.mediatorUtils.getParameterUtil().getListRequest().isEmpty()
            && this.mediatorUtils.getCsrfUtil().getTokenCsrf() == null
        ) {
            return;
        }
            
        // Set connection method
        // Active for query string injection too, in that case inject query string still with altered method
        
        var body = new StringBuilder();
        
        if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
            
            httpRequest.setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "text/xml");
            
        } else {
            
            httpRequest.setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "application/x-www-form-urlencoded");
        }
   
        this.mediatorUtils.getCsrfUtil().addRequestToken(body);
            
        if (this.mediatorUtils.getConnectionUtil().getTypeRequest().matches("PUT|POST")) {
            
            if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
                
                body.append(
                    this.buildQuery(
                        this.mediatorMethod.getRequest(),
                        this.mediatorUtils.getParameterUtil().getRawRequest(),
                        isUsingIndex,
                        dataInjection
                    )
                );
                
            } else {
                
                body.append(
                    this.buildQuery(
                        this.mediatorMethod.getRequest(),
                        this.mediatorUtils.getParameterUtil().getRequestFromEntries(),
                        isUsingIndex,
                        dataInjection
                    )
                );
            }
        }
        
        var bodyPublisher = BodyPublishers.ofString(body.toString());
        
        httpRequest.method(
            this.mediatorUtils.getConnectionUtil().getTypeRequest(),
            bodyPublisher
        );
        
        if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
            
            msgHeader.put(
                Header.POST,
                this.buildQuery(
                    this.mediatorMethod.getRequest(),
                    this.mediatorUtils.getParameterUtil().getRawRequest(),
                    isUsingIndex,
                    dataInjection
                )
            );
            
        } else {
            
            msgHeader.put(
                Header.POST,
                this.buildQuery(
                    this.mediatorMethod.getRequest(),
                    this.mediatorUtils.getParameterUtil().getRequestFromEntries(),
                    isUsingIndex,
                    dataInjection
                )
            );
        }
    }
    
    private String buildQuery(AbstractMethodInjection methodInjection, String paramLead, boolean isUsingIndex, String sqlTrail) {
        
        String query;
        String paramLeadFixed = paramLead.replace(InjectionModel.STAR, "<tampering>*</tampering>");
        
        if (
            // No parameter transformation if method is not selected by user
            this.mediatorUtils.getConnectionUtil().getMethodInjection() != methodInjection
            // No parameter transformation if injection point in URL
            || this.mediatorUtils.getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)
        ) {
            
            // Just pass parameters without any transformation
            query = paramLeadFixed;
            
        } else if (
            // If method is selected by user and URL does not contains injection point
            // but parameters contain an injection point
            // then replace injection point by SQL expression in those parameter
            paramLeadFixed.contains(InjectionModel.STAR)
        ) {
            
            query = this.initializeStarInjection(paramLeadFixed, isUsingIndex, sqlTrail);
            
        } else {
            
            query = this.initializeRawInjection(paramLeadFixed, isUsingIndex, sqlTrail);
        }
        
        // Remove comments except empty /**/
        query = this.clean(methodInjection, query);
        
        // Add empty comments with space=>/**/
        if (this.mediatorUtils.getConnectionUtil().getMethodInjection() == methodInjection) {
            
            query = this.mediatorUtils.getTamperingUtil().tamper(query);
        }
        
        query = this.applyRfcEncoding(methodInjection, query);
        
        return query;
    }

    private String initializeRawInjection(String paramLead, boolean isUsingIndex, String sqlTrail) {
        
        String query;
        
        // Method is selected by user and there's no injection point
        if (
            // Several SQL expressions does not use indexes in SELECT,
            // like Boolean, Error, Shell and search for character insertion,
            // in that case concat SQL expression to the end of param.
            !isUsingIndex
        ) {
            
            query = paramLead + sqlTrail;
            
            // Add ending line comment by vendor
            query = query + this.mediatorVendor.getVendor().instance().endingComment();
            
        } else {
            
            // Concat indexes found for Normal strategy to params
            // and use visible Index for injection
            query = paramLead + this.indexesInUrl.replaceAll(
                "1337" + this.mediatorStrategy.getNormal().getVisibleIndex() + "7331",
                // Oracle column often contains $, which is reserved for regex.
                // => need to be escape with quoteReplacement()
                Matcher.quoteReplacement(sqlTrail)
            );
            
            // Add ending line comment by vendor
            query = query + this.mediatorVendor.getVendor().instance().endingComment();
        }
        
        return query;
    }

    private String initializeStarInjection(String paramLead, boolean isUsingIndex, String sqlTrail) {
        
        String query;
        
        // Several SQL expressions does not use indexes in SELECT,
        // like Boolean, Error, Shell and search for character insertion,
        // in that case replace injection point by SQL expression.
        // Injection point is always at the end?
        if (!isUsingIndex) {
            
            query =
                paramLead
                .replace(
                    InjectionModel.STAR,
                    sqlTrail + this.mediatorVendor.getVendor().instance().endingComment()
                );
            
        } else {
            
            // Replace injection point by indexes found for Normal strategy
            // and use visible Index for injection
            query = paramLead.replace(
                InjectionModel.STAR,
                this.indexesInUrl.replace(
                    "1337" + this.mediatorStrategy.getNormal().getVisibleIndex() + "7331",
                    sqlTrail
                )
                + this.mediatorVendor.getVendor().instance().endingComment()
            );
        }
        
        return query;
    }

    /**
     * Dependency:
     * - Tamper space=>comment
     * @param methodInjection
     * @param query
     * @return
     */
    private String clean(AbstractMethodInjection methodInjection, String query) {
        
        String queryFixed = query;
        
        if (
            methodInjection == this.mediatorMethod.getRequest()
            && this.mediatorUtils.getParameterUtil().isRequestSoap()
        ) {
            
            queryFixed =
                queryFixed
                // Remove SQL comments except tamper /**/ /*!...*/
                // Negative lookahead: don't match tamper empty comment /**/ or version comment /*!...*/
                // JavaScript: (?!\/\*!.*\*\/|\/\*\*\/)\/\*.*\*\/
                .replaceAll("(?s)(?!/\\*\\*/|/\\*!.*\\*/)/\\*.*?\\*/", StringUtils.EMPTY)
                .replace("+", " ")
                // Trap canceller
                .replace("%2b", "+")
                // End comment
                .replace("%23", "#")
                ;
            
        } else {
            
            queryFixed = StringUtil.clean(queryFixed);
        }
        
        return queryFixed;
    }

    private String applyRfcEncoding(AbstractMethodInjection methodInjection, String query) {
        
        String queryFixed = query;
        
        if (!this.mediatorUtils.getParameterUtil().isRequestSoap()) {
        
            if (methodInjection != this.mediatorMethod.getHeader()) {
                
                // URL encode each character because no query parameter context
                if (!this.mediatorUtils.getPreferencesUtil().isUrlEncodingDisabled()) {
                    
                    queryFixed = queryFixed.replace("\"", "%22");
                    queryFixed = queryFixed.replace("'", "%27");
                    queryFixed = queryFixed.replace("(", "%28");
                    queryFixed = queryFixed.replace(")", "%29");
                    queryFixed = queryFixed.replace("{", "%7b");
                    queryFixed = queryFixed.replace("[", "%5b");
                    queryFixed = queryFixed.replace("`", "%60");
                    queryFixed = queryFixed.replace("]", "%5d");
                    queryFixed = queryFixed.replace("}", "%7d");
                    queryFixed = queryFixed.replace(">", "%3e");
                    queryFixed = queryFixed.replace("<", "%3c");
                    queryFixed = queryFixed.replace("?", "%3f");
                    queryFixed = queryFixed.replace("_", "%5f");
                    queryFixed = queryFixed.replace(",", "%2c");
                    queryFixed = queryFixed.replace(StringUtils.SPACE, "+");
                }
                
                queryFixed = queryFixed.replace("|", "%7c");
                queryFixed = queryFixed.replace("\\", "%5c");
                
            } else {
                
                // For cookies in Spring
                // Replace spaces
                queryFixed = queryFixed.replace("+", "%20");
                queryFixed = queryFixed.replace(",", "%2c");
            }
        }
        
        return queryFixed;
    }
    
    /**
     * Display source code in console.
     * @param message Error message
     * @param source Text to display in console
     */
    public void sendResponseFromSite(String message, String source) {
        
        LOGGER.log(LogLevel.CONSOLE_ERROR, "{}, response from site:", message);
        LOGGER.log(LogLevel.CONSOLE_ERROR, ">>>{}", source);
    }
    
    public void displayVersion() {
        
        String versionJava = System.getProperty("java.version");
        String architecture = System.getProperty("os.arch");
        
        LOGGER.log(
            LogLevel.CONSOLE_DEFAULT,
            "jSQL Injection v{} on Java {}-{}-{}",
            this::getVersionJsql,
            () -> versionJava,
            () -> architecture,
            () -> System.getProperty("user.language")
        );
    }
    
    
    // Getters and setters

    public String getIndexesInUrl() {
        return this.indexesInUrl;
    }

    public void setIndexesInUrl(String indexesInUrl) {
        this.indexesInUrl = indexesInUrl;
    }

    public boolean shouldErasePreviousInjection() {
        return this.shouldErasePreviousInjection;
    }

    public void setIsScanning(boolean isScanning) {
        this.isScanning = isScanning;
    }

    public String getVersionJsql() {
        return this.propertiesUtil.getProperties().getProperty("jsql.version");
    }

    public MediatorUtils getMediatorUtils() {
        return this.mediatorUtils;
    }

    public MediatorVendor getMediatorVendor() {
        return this.mediatorVendor;
    }

    public MediatorMethod getMediatorMethod() {
        return this.mediatorMethod;
    }

    public DataAccess getDataAccess() {
        return this.dataAccess;
    }

    public ResourceAccess getResourceAccess() {
        return this.resourceAccess;
    }

    public MediatorStrategy getMediatorStrategy() {
        return this.mediatorStrategy;
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model;

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.ResourceAccess;
import com.jsql.model.bean.util.Request3;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.model.injection.method.MediatorMethod;
import com.jsql.model.injection.strategy.MediatorStrategy;
import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;
import com.jsql.model.injection.engine.MediatorEngine;
import com.jsql.model.injection.engine.model.EngineYaml;
import com.jsql.util.*;
import com.jsql.util.GitUtil.ShowOnConsole;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Model class of MVC pattern for processing SQL injection automatically.<br>
 * Different views can be attached to this observable, like Swing or command line, in order to separate
 * the functional job from the graphical processing.<br>
 * The Model has a specific database engine and strategy which run an automatic injection to get name of
 * databases, tables, columns and values, and it can also retrieve resources like files and shell.<br>
 * Tasks are run in multi-threads in general to speed the process.
 */
public class InjectionModel extends AbstractModelObservable implements Serializable {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final transient MediatorEngine mediatorEngine = new MediatorEngine(this);
    private final transient MediatorMethod mediatorMethod = new MediatorMethod(this);
    private final transient DataAccess dataAccess = new DataAccess(this);
    private final transient ResourceAccess resourceAccess = new ResourceAccess(this);
    private final transient PropertiesUtil propertiesUtil = new PropertiesUtil();
    private final transient MediatorUtils mediatorUtils;
    private final transient MediatorStrategy mediatorStrategy;

    public static final String STAR = "*";
    public static final String BR = "<br>&#10;";

    /**
     * initialUrl transformed to a correct injection url.
     */
    private String analysisReport = StringUtils.EMPTY;

    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    private boolean shouldErasePreviousInjection = false;
    private boolean isScanning = false;

    public InjectionModel() {
        this.mediatorStrategy = new MediatorStrategy(this);
        this.mediatorUtils = new MediatorUtils(
            this.propertiesUtil,
            new ConnectionUtil(this),
            new AuthenticationUtil(),
            new GitUtil(this),
            new HeaderUtil(this),
            new ParameterUtil(this),
            new ExceptionUtil(this),
            new SoapUtil(this),
            new MultipartUtil(this),
            new CookiesUtil(this),
            new JsonUtil(this),
            new PreferencesUtil(),
            new ProxyUtil(),
            new ThreadUtil(this),
            new TamperingUtil(),
            new UserAgentUtil(),
            new CsrfUtil(this),
            new DigestUtil(this),
            new FormUtil(this),
            new CertificateUtil()
        );
    }

    /**
     * Reset each injection attributes: Database metadata, General Thread status, Strategy.
     */
    public void resetModel() {
        this.mediatorStrategy.getTime().setApplicable(false);
        this.mediatorStrategy.getBlindBin().setApplicable(false);
        this.mediatorStrategy.getBlindBit().setApplicable(false);
        this.mediatorStrategy.getMultibit().setApplicable(false);
        this.mediatorStrategy.getDns().setApplicable(false);
        this.mediatorStrategy.getError().setApplicable(false);
        this.mediatorStrategy.getStack().setApplicable(false);
        this.mediatorStrategy.getUnion().setApplicable(false);
        this.mediatorStrategy.setStrategy(null);

        this.mediatorStrategy.getSpecificUnion().setVisibleIndex(null);
        this.mediatorStrategy.getSpecificUnion().setIndexesInUrl(StringUtils.EMPTY);

        this.analysisReport = StringUtils.EMPTY;
        this.isStoppedByUser = false;
        this.shouldErasePreviousInjection = false;

        this.mediatorUtils.csrfUtil().setTokenCsrf(null);
        this.mediatorUtils.digestUtil().setTokenDigest(null);
        this.mediatorUtils.threadUtil().reset();
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     * Run by Scan, Standard and TU.
     */
    public void beginInjection() {
        this.resetModel();
        try {
            if (this.mediatorUtils.proxyUtil().isNotLive(ShowOnConsole.YES)) {
                return;
            }
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{}: {}",
                () -> I18nUtil.valueByKey("LOG_START_INJECTION"),
                () -> this.mediatorUtils.connectionUtil().getUrlByUser()
            );
            
            // Check general integrity if user's parameters
            this.mediatorUtils.parameterUtil().checkParametersFormat();
            this.mediatorUtils.connectionUtil().testConnection();

            // TODO Check all path params URL segments
            boolean hasFoundInjection = this.mediatorMethod.getQuery().testParameters(false);
            hasFoundInjection = this.mediatorUtils.multipartUtil().testParameters(hasFoundInjection);
            hasFoundInjection = this.mediatorUtils.soapUtil().testParameters(hasFoundInjection);
            hasFoundInjection = this.mediatorMethod.getRequest().testParameters(hasFoundInjection);
            hasFoundInjection = this.mediatorMethod.getHeader().testParameters(hasFoundInjection);
            hasFoundInjection = this.mediatorUtils.cookiesUtil().testParameters(hasFoundInjection);

            if (hasFoundInjection && !this.isScanning) {
                if (!this.getMediatorUtils().preferencesUtil().isNotShowingVulnReport()) {
                    this.sendToViews(new Request3.CreateAnalysisReport(this.analysisReport));
                }
                if (this.getMediatorUtils().preferencesUtil().isZipStrategy()) {
                    LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Using Zip mode for reduced query size");
                } else if (this.getMediatorUtils().preferencesUtil().isDiosStrategy()) {
                    LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Using Dump In One Shot strategy for single query dump");
                }
                if (!this.mediatorUtils.preferencesUtil().isNotInjectingMetadata()) {
                    this.dataAccess.getDatabaseInfos();
                }
                this.dataAccess.listDatabases();
            }
            
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("LOG_DONE"));
            this.shouldErasePreviousInjection = true;
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        } catch (JSqlRuntimeException | JSqlException | IOException e) {  // Catch expected exceptions only
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "Interruption: {}",
                e.getMessage() == null ? InjectionModel.getImplicitReason(e) : e.getMessage()
            );
        } finally {
            this.sendToViews(new Request3.EndPreparation());
        }
    }
    
    public static String getImplicitReason(Throwable e) {
        String message = e.getClass().getSimpleName();
        if (e.getMessage() != null) {
            message += ": "+ e.getMessage();
        }
        if (e.getCause() != null && !e.equals(e.getCause())) {
            message += " > "+ InjectionModel.getImplicitReason(e.getCause());
        }
        return message;
    }
    
    /**
     * Run an HTTP connection to the web server.
     * @param dataInjection SQL query
     * @return source code of current page
     */
    @Override
    public String inject(
        String dataInjection,
        boolean isUsingIndex,
        String metadataInjectionProcess,
        AbstractCallableBit<?> callableBoolean,
        boolean isReport
    ) {
        // Temporary url, we go from "select 1,2,3,4..." to "select 1,([complex query]),2...", but keep initial url
        String urlInjection = this.mediatorUtils.connectionUtil().getUrlBase();
        urlInjection = this.mediatorStrategy.buildPath(urlInjection, isUsingIndex, dataInjection);
        urlInjection = StringUtil.cleanSql(urlInjection.trim());

        URL urlObject;
        String urlInjectionFixed;
        try {
            urlInjectionFixed = this.initQueryString(
                isUsingIndex,
                urlInjection,
                dataInjection
            );
            urlObject = new URI(urlInjectionFixed).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format("Incorrect Query Url: %s", e.getMessage()));
            return StringUtils.EMPTY;
        }

        String pageSource = StringUtils.EMPTY;
        
        // Define the connection
        try {
            var httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(urlObject.toString()))
                .setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "text/plain")
                .timeout(Duration.ofSeconds(15));
            
            this.mediatorUtils.csrfUtil().addHeaderToken(httpRequestBuilder);
            this.mediatorUtils.digestUtil().addHeaderToken(httpRequestBuilder);
            this.mediatorUtils.connectionUtil().setCustomUserAgent(httpRequestBuilder);

            String body = this.initRequest(isUsingIndex, dataInjection, httpRequestBuilder);
            this.initHeader(isUsingIndex, dataInjection, httpRequestBuilder);
            
            var httpRequest = httpRequestBuilder.build();
            if (isReport) {
                Color colorReport = UIManager.getColor("TextArea.inactiveForeground");
                String report = InjectionModel.BR + StringUtil.formatReport(colorReport, "Method: ") + httpRequest.method();
                report += InjectionModel.BR + StringUtil.formatReport(colorReport, "Path: ") + httpRequest.uri().getPath();
                if (httpRequest.uri().getQuery() != null) {
                    report += InjectionModel.BR + StringUtil.formatReport(colorReport, "Query: ") + httpRequest.uri().getQuery();
                }
                if (
                    !(this.mediatorUtils.parameterUtil().getListRequest().isEmpty()
                    && this.mediatorUtils.csrfUtil().getTokenCsrf() == null)
                ) {
                    report += InjectionModel.BR + StringUtil.formatReport(colorReport, "Body: ") + body;
                }
                report += InjectionModel.BR 
                    + StringUtil.formatReport(colorReport, "Header: ") 
                    + httpRequest.headers().map().entrySet().stream()
                    .map(entry -> 
                        String.format("%s: %s", entry.getKey(), 
                        String.join(StringUtils.EMPTY, entry.getValue()))
                    )
                    .collect(Collectors.joining(InjectionModel.BR));
                return report;
            }
            
            HttpResponse<String> response = this.getMediatorUtils().connectionUtil().getHttpClient().build().send(
                httpRequestBuilder.build(),
                BodyHandlers.ofString()
            );
            if (this.mediatorUtils.parameterUtil().isRequestSoap()) {
                // Invalid XML control chars like \x04 requires urlencoding from server
                pageSource = URLDecoder.decode(response.body(), StandardCharsets.UTF_8);
                pageSource = StringUtil.fromHtml(pageSource);
            } else {
                pageSource = response.body();
            }

            Map<String, String> headersResponse = ConnectionUtil.getHeadersMap(response);
            int sizeHeaders = headersResponse.keySet()
                .stream()
                .map(key -> headersResponse.get(key).length() + key.length())
                .mapToInt(Integer::intValue)
                .sum();
            float size = (float) (pageSource.length() + sizeHeaders) / 1024;
            var decimalFormat = new DecimalFormat("0.000");

            String pageSourceFixed = pageSource
                .replaceAll("("+ EngineYaml.CALIBRATOR_SQL +"){60,}", "$1...")  // Remove ranges of # created by calibration
                .replaceAll("(jIyM){60,}", "$1...");  // Remove batch of chars created by Dios

            // Send data to Views
            this.sendToViews(new Request3.MessageHeader(
                urlInjectionFixed,
                body,
                ConnectionUtil.getHeadersMap(httpRequest.headers()),
                headersResponse,
                pageSourceFixed,
                decimalFormat.format(size),
                this.mediatorStrategy.getMeta(),
                metadataInjectionProcess,
                callableBoolean
            ));
        } catch (IOException e) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                String.format("Error during connection: %s", e.getMessage())
            );
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }

        return pageSource;
    }

    private String initQueryString(
        boolean isUsingIndex,
        String urlInjection,
        String dataInjection
    ) throws URISyntaxException, MalformedURLException {
        String urlInjectionFixed = urlInjection;
        if (
            this.mediatorUtils.parameterUtil().getListQueryString().isEmpty()
            && !this.mediatorUtils.preferencesUtil().isProcessingCsrf()
        ) {
            return urlInjectionFixed;
        }
            
        // URL without query string like Request and Header can receive
        // new params from <form> parsing, in that case add the '?' to URL
        if (!urlInjectionFixed.contains("?")) {
            urlInjectionFixed += "?";
        }
        urlInjectionFixed += this.buildQuery(
            this.mediatorMethod.getQuery(),
            this.mediatorUtils.parameterUtil().getQueryStringFromEntries(),
            isUsingIndex,
            dataInjection
        );
        return this.mediatorUtils.csrfUtil().addQueryStringToken(urlInjectionFixed);
    }

    private void initHeader(
        boolean isUsingIndex,
        String dataInjection,
        Builder httpRequest
    ) {
        if (!this.mediatorUtils.parameterUtil().getListHeader().isEmpty()) {
            Stream.of(
                this.buildQuery(
                    this.mediatorMethod.getHeader(),
                    this.mediatorUtils.parameterUtil().getHeaderFromEntries(),
                    isUsingIndex,
                    dataInjection
                )
                .split("\\\\r\\\\n")
            )
            .forEach(header -> {
                if (header.split(":").length == 2) {
                    try {  // TODO Should not catch, rethrow or use runtime exception
                        HeaderUtil.sanitizeHeaders(
                            httpRequest,
                            new SimpleEntry<>(
                                header.split(":")[0],
                                header.split(":")[1]
                            )
                        );
                    } catch (JSqlException e) {
                        LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Headers sanitizing issue caught already during connection, ignoring", e);
                    }
                }
            });
        }
    }

    private String initRequest(
        boolean isUsingIndex,
        String dataInjection,
        Builder httpRequest
    ) {
        if (
            this.mediatorUtils.parameterUtil().getListRequest().isEmpty()
            && this.mediatorUtils.csrfUtil().getTokenCsrf() == null
        ) {
            return dataInjection;
        }
            
        // Set connection method
        // Active for query string injection too, in that case inject query string still with altered method
        
        if (this.mediatorUtils.parameterUtil().isRequestSoap()) {
            httpRequest.setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "text/xml");
        } else {
            httpRequest.setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "application/x-www-form-urlencoded");
        }

        var body = new StringBuilder();
        this.mediatorUtils.csrfUtil().addRequestToken(body);
            
        if (this.mediatorUtils.connectionUtil().getTypeRequest().matches("PUT|POST")) {
            if (this.mediatorUtils.parameterUtil().isRequestSoap()) {
                body.append(
                    this.buildQuery(
                        this.mediatorMethod.getRequest(),
                        this.mediatorUtils.parameterUtil().getRawRequest(),
                        isUsingIndex,
                        dataInjection
                    )
                    // Invalid XML characters in recent Spring version
                    // Server needs to urldecode, or stop using out of range chars
                    .replace("\u0001", "&#01;")
                    .replace("\u0003", "&#03;")
                    .replace("\u0004", "&#04;")
                    .replace("\u0005", "&#05;")
                    .replace("\u0006", "&#06;")
                    .replace("\u0007", "&#07;")
                    .replace("+", "%2B")  // Prevent replace '+' into 'space' on server side urldecode
                );
            } else {
                body.append(
                    this.buildQuery(
                        this.mediatorMethod.getRequest(),
                        this.mediatorUtils.parameterUtil().getRequestFromEntries(),
                        isUsingIndex,
                        dataInjection
                    )
                );
            }
        }
        
        var bodyPublisher = BodyPublishers.ofString(body.toString());
        httpRequest.method(
            this.mediatorUtils.connectionUtil().getTypeRequest(),
            bodyPublisher
        );
        return body.toString();
    }
    
    private String buildQuery(AbstractMethodInjection methodInjection, String paramLead, boolean isUsingIndex, String sqlTrail) {
        String query;
        String paramLeadFixed = paramLead.replace(
            InjectionModel.STAR,
            TamperingUtil.TAG_OPENED + InjectionModel.STAR + TamperingUtil.TAG_CLOSED
        );
        if (
            // No parameter transformation if method is not selected by user
            this.mediatorUtils.connectionUtil().getMethodInjection() != methodInjection
            // No parameter transformation if injection point in URL
            || this.mediatorUtils.connectionUtil().getUrlBase().contains(InjectionModel.STAR)
        ) {
            query = paramLeadFixed;  // Just pass parameters without any transformation
        } else if (
            // If method is selected by user and URL does not contain injection point
            // but parameters contain an injection point
            // then replace injection point by SQL expression in this parameter
            paramLeadFixed.contains(InjectionModel.STAR)
        ) {
            query = this.initStarInjection(paramLeadFixed, isUsingIndex, sqlTrail);
        } else {
            query = this.initRawInjection(paramLeadFixed, isUsingIndex, sqlTrail);
        }
        query = this.cleanQuery(methodInjection, query);  // Remove comments except empty /**/
        // Add empty comments with space=>/**/
        if (this.mediatorUtils.connectionUtil().getMethodInjection() == methodInjection) {
            query = this.mediatorUtils.tamperingUtil().tamper(query);
        }
        return this.applyEncoding(methodInjection, query);
    }

    private String initRawInjection(String paramLead, boolean isUsingIndex, String sqlTrail) {
        String query;
        // Method is selected by user and there's no injection point
        if (!isUsingIndex) {
            // Several SQL expressions does not use indexes in SELECT,
            // like Boolean, Error, Shell and search for character insertion,
            // in that case concat SQL expression to the end of param.
            query = paramLead + sqlTrail;
        } else {
            // Concat indexes found for Union strategy to params
            // and use visible Index for injection
            query = paramLead + this.getMediatorStrategy().getSpecificUnion().getIndexesInUrl().replaceAll(
                String.format(EngineYaml.FORMAT_INDEX, this.mediatorStrategy.getSpecificUnion().getVisibleIndex()),
                // Oracle column often contains $, which is reserved for regex.
                // => need to be escape with quoteReplacement()
                Matcher.quoteReplacement(sqlTrail)
            );
        }
        // Add ending line comment by engine
        return query + this.mediatorEngine.getEngine().instance().endingComment();
    }

    private String initStarInjection(String paramLead, boolean isUsingIndex, String sqlTrail) {
        String query;
        // Several SQL expressions does not use indexes in SELECT,
        // like Boolean, Error, Shell and search for character insertion,
        // in that case replace injection point by SQL expression.
        // Injection point is always at the end?
        if (!isUsingIndex) {
            query = paramLead.replace(
                InjectionModel.STAR,
                sqlTrail + this.mediatorEngine.getEngine().instance().endingComment()
            );
        } else {
            // Replace injection point by indexes found for Union strategy
            // and use visible Index for injection
            query = paramLead.replace(
                InjectionModel.STAR,
                this.mediatorStrategy.getSpecificUnion().getIndexesInUrl().replace(
                    String.format(EngineYaml.FORMAT_INDEX, this.mediatorStrategy.getSpecificUnion().getVisibleIndex()),
                    sqlTrail
                )
                + this.mediatorEngine.getEngine().instance().endingComment()
            );
        }
        return query;
    }

    /**
     * Dependency:
     * - Tamper space=>comment
     */
    private String cleanQuery(AbstractMethodInjection methodInjection, String query) {
        String queryFixed = query;
        if (
            methodInjection == this.mediatorMethod.getRequest()
            && (
                this.mediatorUtils.parameterUtil().isRequestSoap()
                || this.mediatorUtils.parameterUtil().isMultipartRequest()
            )
        ) {
            queryFixed = StringUtil.removeSqlComment(queryFixed)
                .replace("+", " ")
                .replace("%2b", "+")  // Failsafe
                .replace("%23", "#");  // End comment
            if (this.mediatorUtils.parameterUtil().isMultipartRequest()) {
                // restore linefeed from textfield
                queryFixed = queryFixed.replaceAll("(?s)\\\\n", "\r\n");
            }
        } else {
            queryFixed = StringUtil.cleanSql(queryFixed);
        }
        return queryFixed;
    }

    private String applyEncoding(AbstractMethodInjection methodInjection, String query) {
        String queryFixed = query;
        if (!this.mediatorUtils.parameterUtil().isRequestSoap()) {
            if (methodInjection == this.mediatorMethod.getQuery()) {
                // URL encode each character because no query parameter context
                if (!this.mediatorUtils.preferencesUtil().isUrlEncodingDisabled()) {
                    queryFixed = queryFixed.replace("'", "%27");
                    queryFixed = queryFixed.replace("(", "%28");
                    queryFixed = queryFixed.replace(")", "%29");
                    queryFixed = queryFixed.replace("{", "%7b");
                    queryFixed = queryFixed.replace("[", "%5b");
                    queryFixed = queryFixed.replace("]", "%5d");
                    queryFixed = queryFixed.replace("}", "%7d");
                    queryFixed = queryFixed.replace(">", "%3e");
                    queryFixed = queryFixed.replace("<", "%3c");
                    queryFixed = queryFixed.replace("?", "%3f");
                    queryFixed = queryFixed.replace("_", "%5f");
                    queryFixed = queryFixed.replace(",", "%2c");
                }
                // HTTP forbidden characters
                queryFixed = queryFixed.replace(StringUtils.SPACE, "+");
                queryFixed = queryFixed.replace("`", "%60");  // from `${database}`.`${table}`
                queryFixed = queryFixed.replace("\"", "%22");
                queryFixed = queryFixed.replace("|", "%7c");
                queryFixed = queryFixed.replace("\\", "%5c");
            } else if (methodInjection != this.mediatorMethod.getRequest()) {
                // For cookies in Spring (confirmed, covered by integration tests)
                queryFixed = queryFixed.replace("+", "%20");
                queryFixed = queryFixed.replace(",", "%2c");
                try {  // fix #95709: IllegalArgumentException on decode()
                    queryFixed = URLDecoder.decode(queryFixed, StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect values in [{}], please check the parameters", methodInjection.name());
                    throw new JSqlRuntimeException(e);
                }
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
        LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{}, response from site:", message);
        LOGGER.log(LogLevelUtil.CONSOLE_ERROR, ">>>{}", source);
    }
    
    
    // Getters and setters

    public boolean shouldErasePreviousInjection() {
        return this.shouldErasePreviousInjection;
    }

    public void setIsScanning(boolean isScanning) {
        this.isScanning = isScanning;
    }

    public PropertiesUtil getPropertiesUtil() {
        return this.propertiesUtil;
    }

    public MediatorUtils getMediatorUtils() {
        return this.mediatorUtils;
    }

    public MediatorEngine getMediatorEngine() {
        return this.mediatorEngine;
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

    public void appendAnalysisReport(String analysisReport) {
        this.appendAnalysisReport(analysisReport, false);
    }

    public void appendAnalysisReport(String analysisReport, boolean isInit) {
        this.analysisReport += (isInit ? StringUtils.EMPTY : "<br>&#10;<br>&#10;") + analysisReport;
    }
}

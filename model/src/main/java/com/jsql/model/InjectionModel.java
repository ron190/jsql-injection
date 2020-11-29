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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivilegedActionException;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.ResourceAccess;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.model.injection.method.MediatorMethod;
import com.jsql.model.injection.strategy.MediatorStrategy;
import com.jsql.model.injection.vendor.MediatorVendor;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.HeaderUtil;
import com.jsql.util.I18nUtil;
import com.jsql.util.JsonUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.PropertiesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.util.SoapUtil;
import com.jsql.util.StringUtil;
import com.jsql.util.TamperingUtil;
import com.jsql.util.ThreadUtil;
import com.jsql.util.UserAgentUtil;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

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
    private static final Logger LOGGER = Logger.getRootLogger();
    
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

        this.mediatorUtils.setPropertiesUtil(this.propertiesUtil);
        this.mediatorUtils.setConnectionUtil(new ConnectionUtil(this));
        this.mediatorUtils.setAuthenticationUtil(new AuthenticationUtil(this));
        this.mediatorUtils.setGitUtil(new GitUtil(this));
        this.mediatorUtils.setHeaderUtil(new HeaderUtil(this));
        this.mediatorUtils.setParameterUtil(new ParameterUtil(this));
        this.mediatorUtils.setExceptionUtil(new ExceptionUtil(this));
        this.mediatorUtils.setSoapUtil(new SoapUtil(this));
        this.mediatorUtils.setJsonUtil(new JsonUtil(this));
        this.mediatorUtils.setPreferencesUtil(new PreferencesUtil());
        this.mediatorUtils.setProxyUtil(new ProxyUtil(this));
        this.mediatorUtils.setThreadUtil(new ThreadUtil());
        this.mediatorUtils.setTamperingUtil(new TamperingUtil());
        this.mediatorUtils.setUserAgentUtil(new UserAgentUtil());
    }

    /**
     * Reset each injection attributes: Database metadata, General Thread status, Strategy.
     */
    public void resetModel() {
        
        this.mediatorStrategy.getNormal().setVisibleIndex(null);
        
        // TODO remove, must be done on checkApplicability()
        this.mediatorStrategy.getNormal().setApplicable(false);
        this.mediatorStrategy.getError().setApplicable(false);
        this.mediatorStrategy.getBlind().setApplicable(false);
        this.mediatorStrategy.getTime().setApplicable(false);
        
        this.indexesInUrl = StringUtils.EMPTY;
        
        this.mediatorUtils.getConnectionUtil().setTokenCsrf(null);
        
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
            
            LOGGER.info(I18nUtil.valueByKey("LOG_START_INJECTION") +": "+ this.mediatorUtils.getConnectionUtil().getUrlByUser());
            
            // Check general integrity if user's parameters
            this.mediatorUtils.getParameterUtil().checkParametersFormat();
            
            // Check connection is working: define Cookie management, check HTTP status, parse <form> parameters, process CSRF
            LOGGER.trace(I18nUtil.valueByKey("LOG_CONNECTION_TEST"));
            this.mediatorUtils.getConnectionUtil().testConnection();
            
            boolean hasFoundInjection = this.mediatorMethod.getQuery().testParameters();

            if (!hasFoundInjection) {
                
                hasFoundInjection = this.mediatorUtils.getSoapUtil().testParameters();
            }
            
            if (!hasFoundInjection) {
                
                LOGGER.trace("Checking standard Request parameters");
                hasFoundInjection = this.mediatorMethod.getRequest().testParameters();
            }
            
            if (!hasFoundInjection) {
                
                hasFoundInjection = this.mediatorMethod.getHeader().testParameters();
            }
            
            if (hasFoundInjection && !this.isScanning) {
                
                if (this.getMediatorUtils().getPreferencesUtil().isZippedStrategy()) {
                    
                    LOGGER.info("Using minimal query size");
                }
                
                if (!this.mediatorUtils.getPreferencesUtil().isNotInjectingMetadata()) {
                    
                    this.dataAccess.getDatabaseInfos();
                }
                
                this.dataAccess.listDatabases();
            }
            
            LOGGER.trace(I18nUtil.valueByKey("LOG_DONE"));
            
            this.shouldErasePreviousInjection = true;
            
        } catch (JSqlException e) {
            
            LOGGER.warn(e.getMessage(), e);
            
        } finally {
            
            Request request = new Request();
            request.setMessage(Interaction.END_PREPARATION);
            this.sendToViews(request);
        }
    }
    
    /**
     * Run a HTTP connection to the web server.
     * @param dataInjection SQL query
     * @param responseHeader unused
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
        
        try {
            urlObject = new URL(urlInjection);
            
        } catch (MalformedURLException e) {
            
            LOGGER.warn("Incorrect Query Url: "+ e.getMessage(), e);
            return StringUtils.EMPTY;
        }

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);

        // TODO identique urlInjection == urlObject
        urlObject = this.initializeQueryString(isUsingIndex, urlInjection, dataInjection, urlObject, msgHeader);
        
        String pageSource = StringUtils.EMPTY;
        
        // Define the connection
        try {
            HttpURLConnection connection = this.initializeConnection(urlObject);
            
            // Csrf
            
            if (this.mediatorUtils.getConnectionUtil().getTokenCsrf() != null) {
                
                connection.setRequestProperty(this.mediatorUtils.getConnectionUtil().getTokenCsrf().getKey(), this.mediatorUtils.getConnectionUtil().getTokenCsrf().getValue());
            }
            
            this.mediatorUtils.getConnectionUtil().fixJcifsTimeout(connection);
            this.mediatorUtils.getConnectionUtil().setCustomUserAgent(connection);
            
            this.initializeHeader(isUsingIndex, dataInjection, connection, msgHeader);
            this.initializeRequest(isUsingIndex, dataInjection, connection, msgHeader);
            
            Map<String, String> headers = HeaderUtil.getHttpHeaders(connection);
            msgHeader.put(Header.RESPONSE, headers);
            
            // Calling connection.disconnect() is not required, further calls will follow
            pageSource = ConnectionUtil.getSource(connection);
            
            int sizeHeaders =
                headers
                .keySet()
                .stream()
                .map(key -> headers.get(key).length() + key.length())
                .mapToInt(Integer::intValue)
                .sum();
            
            String size = (pageSource.length() + sizeHeaders) / 1024 + "kb";
            msgHeader.put(Header.PAGE_SIZE, size);
            
            if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
                
                pageSource = StringUtil.fromHtml(pageSource);
            }
            
            msgHeader.put(Header.SOURCE, pageSource);
            msgHeader.put(Header.METADATA_INJECTION_PROCESS, metadataInjectionProcess);
            
            // Send data to Views
            Request request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.sendToViews(request);
            
        } catch (
            // Exception for General and Spnego Opening Connection
            IOException | LoginException | GSSException | PrivilegedActionException e
        ) {
            LOGGER.warn("Error during connection: "+ e.getMessage(), e);
        }

        // return the source code of the page
        return pageSource;
    }

    private URL initializeQueryString(boolean isUsingIndex, String urlInjection, String dataInjection, URL urlObject, Map<Header, Object> msgHeader) {
        
        String urlInjectionFixed = urlInjection;
        URL urlObjectFixed = urlObject;
        
        if (!this.mediatorUtils.getParameterUtil().getListQueryString().isEmpty()) {
            
            // URL without query string like Request and Header can receive
            // new params from <form> parsing, in that case add the '?' to URL
            if (!urlInjectionFixed.contains("?")) {
                
                urlInjectionFixed += "?";
            }

            urlInjectionFixed += this.buildQuery(this.mediatorMethod.getQuery(), this.mediatorUtils.getParameterUtil().getQueryStringFromEntries(), isUsingIndex, dataInjection);
            
            if (this.mediatorUtils.getConnectionUtil().getTokenCsrf() != null) {
                
                urlInjectionFixed += "&"+ this.mediatorUtils.getConnectionUtil().getTokenCsrf().getKey() +"="+ this.mediatorUtils.getConnectionUtil().getTokenCsrf().getValue();
            }
            
            try {
                urlObjectFixed = new URL(urlInjectionFixed);
                
            } catch (MalformedURLException e) {
                
                LOGGER.warn("Incorrect Url: "+ e.getMessage(), e);
            }
        } else {
            
            if (this.mediatorUtils.getConnectionUtil().getTokenCsrf() != null) {
                
                urlInjectionFixed += "?"+ this.mediatorUtils.getConnectionUtil().getTokenCsrf().getKey() +"="+ this.mediatorUtils.getConnectionUtil().getTokenCsrf().getValue();
            }
        }

        msgHeader.put(Header.URL, urlInjectionFixed);
        
        return urlObjectFixed;
    }

    private HttpURLConnection initializeConnection(URL urlObject) throws IOException, LoginException, GSSException, PrivilegedActionException {
        
        HttpURLConnection connection;
        
        // Block Opening Connection
        if (this.mediatorUtils.getAuthenticationUtil().isKerberos()) {
            
            String kerberosConfiguration =
                Pattern
                .compile("(?s)\\{.*")
                .matcher(
                    StringUtils.join(
                        Files.readAllLines(
                            Paths.get(this.mediatorUtils.getAuthenticationUtil().getPathKerberosLogin()),
                            Charset.defaultCharset()
                        ),
                        StringUtils.EMPTY
                    )
                )
                .replaceAll(StringUtils.EMPTY)
                .trim();
            
            SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(kerberosConfiguration);
            connection = spnego.connect(urlObject);
            
        } else {
            
            connection = (HttpURLConnection) urlObject.openConnection();
        }
        
        connection.setReadTimeout(this.mediatorUtils.getConnectionUtil().getTimeout());
        connection.setConnectTimeout(this.mediatorUtils.getConnectionUtil().getTimeout());
        connection.setDefaultUseCaches(false);
        
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Expires", "-1");
        connection.setRequestProperty(HeaderUtil.CONTENT_TYPE, "text/plain");
        
        return connection;
    }

    private void initializeHeader(boolean isUsingIndex, String dataInjection, HttpURLConnection connection, Map<Header, Object> msgHeader) {
        
        if (!this.mediatorUtils.getParameterUtil().getListHeader().isEmpty()) {
            
            Stream
            .of(this.buildQuery(this.mediatorMethod.getHeader(), this.mediatorUtils.getParameterUtil().getHeaderFromEntries(), isUsingIndex, dataInjection).split("\\\\r\\\\n"))
            .forEach(e -> {
                
                if (e.split(":").length == 2) {
                    
                    HeaderUtil.sanitizeHeaders(connection, new SimpleEntry<>(e.split(":")[0], e.split(":")[1]));
                }
            });
            
            msgHeader.put(Header.HEADER, this.buildQuery(this.mediatorMethod.getHeader(), this.mediatorUtils.getParameterUtil().getHeaderFromEntries(), isUsingIndex, dataInjection));
        }
    }

    private void initializeRequest(boolean isUsingIndex, String dataInjection, HttpURLConnection connection, Map<Header, Object> msgHeader) {
        
        if (
            this.mediatorUtils.getParameterUtil().getListRequest().isEmpty()
            && this.mediatorUtils.getConnectionUtil().getTokenCsrf() == null
        ) {
            return;
        }
            
        try {
            // Set connection method
            // Active for query string injection too, in that case inject query string still with altered method
            ConnectionUtil.fixCustomRequestMethod(connection, this.mediatorUtils.getConnectionUtil().getTypeRequest());
            
            connection.setDoOutput(true);
            
            if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
                
                connection.setRequestProperty(HeaderUtil.CONTENT_TYPE, "text/xml");
                
            } else {
                
                connection.setRequestProperty(HeaderUtil.CONTENT_TYPE, "application/x-www-form-urlencoded");
            }
   
            DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
            
            if (this.mediatorUtils.getConnectionUtil().getTokenCsrf() != null) {
                
                dataOut.writeBytes(this.mediatorUtils.getConnectionUtil().getTokenCsrf().getKey() +"="+ this.mediatorUtils.getConnectionUtil().getTokenCsrf().getValue() +"&");
            }
            
            if (this.mediatorUtils.getConnectionUtil().getTypeRequest().matches("PUT|POST")) {
                
                if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
                    
                    dataOut.writeBytes(this.buildQuery(this.mediatorMethod.getRequest(), this.mediatorUtils.getParameterUtil().getRawRequest(), isUsingIndex, dataInjection));
                    
                } else {
                    
                    dataOut.writeBytes(this.buildQuery(this.mediatorMethod.getRequest(), this.mediatorUtils.getParameterUtil().getRequestFromEntries(), isUsingIndex, dataInjection));
                }
            }
            
            dataOut.flush();
            dataOut.close();
            
            if (this.mediatorUtils.getParameterUtil().isRequestSoap()) {
                
                msgHeader.put(Header.POST, this.buildQuery(this.mediatorMethod.getRequest(), this.mediatorUtils.getParameterUtil().getRawRequest(), isUsingIndex, dataInjection));
                
            } else {
                
                msgHeader.put(Header.POST, this.buildQuery(this.mediatorMethod.getRequest(), this.mediatorUtils.getParameterUtil().getRequestFromEntries(), isUsingIndex, dataInjection));
            }
            
        } catch (IOException e) {
            LOGGER.warn("Error during Request connection: "+ e.getMessage(), e);
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
                /**
                 * Oracle column often contains $, which is reserved for regex.
                 * => need to be escape with quoteReplacement()
                 */
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
            
            query = paramLead.replace(InjectionModel.STAR, sqlTrail + this.mediatorVendor.getVendor().instance().endingComment());
            
        } else {
            
            // Replace injection point by indexes found for Normal strategy
            // and use visible Index for injection
            query = paramLead.replace(
                InjectionModel.STAR,
                this.indexesInUrl.replace(
                    // TODO 1337
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
        
        LOGGER.warn(message + ", response from site:");
        LOGGER.warn(">>>" + source);
    }
    
    public void displayVersion() {
        
        String versionJava = System.getProperty("java.version");
        String architecture = System.getProperty("os.arch");
        
        LOGGER.trace(
            "jSQL Injection v"
            + this.getVersionJsql()
            + " on Java "
            + versionJava
            + "-"
            + architecture
            + "-"
            + System.getProperty("user.language")
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

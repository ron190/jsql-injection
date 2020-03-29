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

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MediatorMethodInjection;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.MediatorStrategy;
import com.jsql.model.injection.vendor.MediatorVendor;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.HeaderUtil;
import com.jsql.util.JsonUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.PropertiesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.util.SoapUtil;
import com.jsql.util.ThreadUtil;
import com.jsql.util.tampering.TamperingUtil;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

/**
 * Model class of MVC pattern for processing SQL injection automatically.<br>
 * Different views can be attached to this observable, like Swing or command line, in order to separate
 * the functional job from the graphical processing.<br>
 * The Model has a specific database vendor and strategy which run an automatic injection to get name of
 * databases, tables, columns and values, and it can also retrieve resources like files and shell.<br>
 * Tasks are run in multi-threads in general to speed the process.
 */
public class InjectionModel extends AbstractModelObservable {
    
    private MediatorVendor mediatorVendor = new MediatorVendor(InjectionModel.this);
    private MediatorMethodInjection mediatorMethodInjection = new MediatorMethodInjection(this);
    private MediatorUtils mediatorUtils;
    private MediatorStrategy mediatorStrategy;
    
    private DataAccess dataAccess = new DataAccess(this);
    private RessourceAccess resourceAccess = new RessourceAccess(this);
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public static final String STAR = "*";
    
    /**
     * HTML body of page successfully responding to
     * multiple fields selection (select 1,2,3,..).
     */
    private String srcSuccess = "";
    
    /**
     * initialUrl transformed to a correct injection url.
     */
    private String indexesInUrl = "";

    /**
     * Current version of database.
     */
    private String versionDatabase;
    
    /**
     * Selected database.
     */
    private String nameDatabase;
    
    /**
     * User connected to database.
     */
    private String username;
    
    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    private boolean injectionAlreadyBuilt = false;
    
    private boolean isScanning = false;
    
    public static final boolean IS_NOT_INJECTION_POINT = true;
    public static final boolean IS_JSON = true;

    public InjectionModel() {
        this.mediatorUtils = new MediatorUtils();
        
        this.mediatorStrategy = new MediatorStrategy(this);

        this.mediatorUtils.setPropertiesUtil(new PropertiesUtil());
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
        this.mediatorUtils.setThreadUtil(new ThreadUtil(this));
        this.mediatorUtils.setTamperingUtil(new TamperingUtil());
    }

    /**
     * Reset each injection attributes: Database metadata, General Thread status, Strategy.
     */
    public void resetModel() {
        
        // TODO make injection pojo for all fields
        this.getMediatorStrategy().getNormal().setVisibleIndex(null);
        this.indexesInUrl = "";
        
        this.getMediatorUtils().getConnectionUtil().setTokenCsrf(null);
        
        this.versionDatabase = null;
        this.nameDatabase = null;
        this.username = null;
        
        this.setIsStoppedByUser(false);
        this.injectionAlreadyBuilt = false;
        
        this.getMediatorStrategy().setStrategy(null);
        
        this.resourceAccess.setReadingIsAllowed(false);
        
        this.getMediatorUtils().getThreadUtil().reset();
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     * Run by Scan, Standard and TU.
     */
    public void beginInjection() {
        
        this.resetModel();
        
        try {
            if (!this.getMediatorUtils().getProxyUtil().isLive(ShowOnConsole.YES)) {
                return;
            }
            
            LOGGER.info(I18n.valueByKey("LOG_START_INJECTION") +": "+ this.getMediatorUtils().getConnectionUtil().getUrlByUser());
            
            // Check general integrity if user's parameters
            this.getMediatorUtils().getParameterUtil().checkParametersFormat();
            
            // Check connection is working: define Cookie management, check HTTP status, parse <form> parameters, process CSRF
            LOGGER.trace(I18n.valueByKey("LOG_CONNECTION_TEST"));
            this.getMediatorUtils().getConnectionUtil().testConnection();
            
            boolean hasFoundInjection = false;
            
            hasFoundInjection = this.getMediatorMethodInjection().getQuery().testParameters();

            if (!hasFoundInjection) {
                hasFoundInjection = this.getMediatorUtils().getSoapUtil().testParameters();
            }
            
            if (!hasFoundInjection) {
                LOGGER.trace("Checking standard Request parameters");
                hasFoundInjection = this.getMediatorMethodInjection().getRequest().testParameters();
            }
            
            if (!hasFoundInjection) {
                hasFoundInjection = this.getMediatorMethodInjection().getHeader().testParameters();
            }
            
            if (!this.isScanning) {
                if (!this.getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata()) {
                    this.getDataAccess().getDatabaseInfos();
                }
                this.getDataAccess().listDatabases();
            }
            
            LOGGER.trace(I18n.valueByKey("LOG_DONE"));
            
            this.injectionAlreadyBuilt = true;
            
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
    public String inject(String newDataInjection, boolean isUsingIndex) {
        
        // Temporary url, we go from "select 1,2,3,4..." to "select 1,([complex query]),2...", but keep initial url
        String urlInjection = this.getMediatorUtils().getConnectionUtil().getUrlBase();
        
        String dataInjection = " "+ newDataInjection;
        
        urlInjection = this.buildURL(urlInjection, isUsingIndex, dataInjection);
        
        // TODO merge into function
        urlInjection = urlInjection
            .trim()
            // Remove comments
            .replaceAll("(?s)/\\*.*?\\*/", "")
            // Remove spaces after a word
            .replaceAll("([^\\s\\w])(\\s+)", "$1")
            // Remove spaces before a word
            .replaceAll("(\\s+)([^\\s\\w])", "$2")
            // Replace spaces
            .replaceAll("\\s+", "+");

        URL urlObject = null;
        try {
            urlObject = new URL(urlInjection);
        } catch (MalformedURLException e) {
            LOGGER.warn("Incorrect Query Url: "+ e, e);
            return "";
        }

        /**
         * Build the GET query string infos
         */
        // TODO Extract in method
        if (!this.getMediatorUtils().getParameterUtil().getListQueryString().isEmpty()) {
            
            // URL without querystring like Request and Header can receive
            // new params from <form> parsing, in that case add the '?' to URL
            if (!urlInjection.contains("?")) {
                urlInjection += "?";
            }

            urlInjection += this.buildQuery(this.getMediatorMethodInjection().getQuery(), this.getMediatorUtils().getParameterUtil().getQueryStringFromEntries(), isUsingIndex, dataInjection);
            
            if (this.getMediatorUtils().getConnectionUtil().getTokenCsrf() != null) {
                urlInjection += "&"+ this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getKey() +"="+ this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getValue();
            }
            
            try {
                urlObject = new URL(urlInjection);
            } catch (MalformedURLException e) {
                LOGGER.warn("Incorrect Url: "+ e, e);
            }
        } else {
            if (this.getMediatorUtils().getConnectionUtil().getTokenCsrf() != null) {
                urlInjection += "?"+ this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getKey() +"="+ this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getValue();
            }
        }
        
        HttpURLConnection connection;
        String pageSource = "";
        
        // Define the connection
        try {
            
            // TODO separate method
            // Block Opening Connection
            if (this.getMediatorUtils().getAuthenticationUtil().isKerberos()) {
                String kerberosConfiguration =
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(StringUtils.join(Files.readAllLines(Paths.get(this.getMediatorUtils().getAuthenticationUtil().getPathKerberosLogin()), Charset.defaultCharset()), ""))
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(kerberosConfiguration);
                connection = spnego.connect(urlObject);
            } else {
                connection = (HttpURLConnection) urlObject.openConnection();
            }
            
            connection.setReadTimeout(this.getMediatorUtils().getConnectionUtil().getTimeout());
            connection.setConnectTimeout(this.getMediatorUtils().getConnectionUtil().getTimeout());
            connection.setDefaultUseCaches(false);
            
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            connection.setRequestProperty("Content-Type", "text/plain");
            
            // Csrf
            
            if (this.getMediatorUtils().getConnectionUtil().getTokenCsrf() != null) {
                connection.setRequestProperty(this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getKey(), this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getValue());
            }
            
            this.getMediatorUtils().getConnectionUtil().fixJcifsTimeout(connection);

            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(Header.URL, urlInjection);
            
            /**
             * Build the HEADER and logs infos
             */
            // TODO Extract in method
            if (!this.getMediatorUtils().getParameterUtil().getListHeader().isEmpty()) {
                Stream.of(this.buildQuery(this.getMediatorMethodInjection().getHeader(), this.getMediatorUtils().getParameterUtil().getHeaderFromEntries(), isUsingIndex, dataInjection).split("\\\\r\\\\n"))
                .forEach(e -> {
                    if (e.split(":").length == 2) {
                        HeaderUtil.sanitizeHeaders(connection, new SimpleEntry<>(e.split(":")[0], e.split(":")[1]));
                    }
                });
                
                msgHeader.put(Header.HEADER, this.buildQuery(this.getMediatorMethodInjection().getHeader(), this.getMediatorUtils().getParameterUtil().getHeaderFromEntries(), isUsingIndex, dataInjection));
            }
    
            /**
             * Build the POST and logs infos
             */
            // TODO Extract in method
            if (!this.getMediatorUtils().getParameterUtil().getListRequest().isEmpty() || this.getMediatorUtils().getConnectionUtil().getTokenCsrf() != null) {
                try {
                    ConnectionUtil.fixCustomRequestMethod(connection, this.getMediatorUtils().getConnectionUtil().getTypeRequest());
                    
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    
                    DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                    if (this.getMediatorUtils().getConnectionUtil().getTokenCsrf() != null) {
                        dataOut.writeBytes(this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getKey() +"="+ this.getMediatorUtils().getConnectionUtil().getTokenCsrf().getValue() +"&");
                    }
                    if (this.getMediatorUtils().getConnectionUtil().getTypeRequest().matches("PUT|POST")) {
                        if (this.getMediatorUtils().getParameterUtil().isRequestSoap()) {
                            dataOut.writeBytes(this.buildQuery(this.getMediatorMethodInjection().getRequest(), this.getMediatorUtils().getParameterUtil().getRawRequest(), isUsingIndex, dataInjection));
                        } else {
                            dataOut.writeBytes(this.buildQuery(this.getMediatorMethodInjection().getRequest(), this.getMediatorUtils().getParameterUtil().getRequestFromEntries(), isUsingIndex, dataInjection));
                        }
                    }
                    dataOut.flush();
                    dataOut.close();
                    
                    if (this.getMediatorUtils().getParameterUtil().isRequestSoap()) {
                        msgHeader.put(Header.POST, this.buildQuery(this.getMediatorMethodInjection().getRequest(), this.getMediatorUtils().getParameterUtil().getRawRequest(), isUsingIndex, dataInjection));
                    } else {
                        msgHeader.put(Header.POST, this.buildQuery(this.getMediatorMethodInjection().getRequest(), this.getMediatorUtils().getParameterUtil().getRequestFromEntries(), isUsingIndex, dataInjection));
                    }
                } catch (IOException e) {
                    LOGGER.warn("Error during Request connection: "+ e, e);
                }
            }
            
            msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));
            
            try {
                pageSource = ConnectionUtil.getSource(connection);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
            
            // Calling connection.disconnect() is not required, further calls will follow
            
            msgHeader.put(Header.SOURCE, pageSource);
            
            // Inform the view about the log infos
            Request request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.sendToViews(request);
            
        } catch (
            // Exception for General and Spnego Opening Connection
            IOException | LoginException | GSSException | PrivilegedActionException e
        ) {
            LOGGER.warn("Error during connection: "+ e, e);
        }

        // return the source code of the page
        return pageSource;
    }
    
    /**
     * Build correct data for GET, POST, HEADER.
     * Each can be either raw data (no injection), SQL query without index requirement,
     * or SQL query with index requirement.
     * @param dataType Current method to build
     * @param urlBase Beginning of the request data
     * @param isUsingIndex False if request doesn't use indexes
     * @param sqlTrail SQL statement
     * @return Final data
     */
    private String buildURL(String urlBase, boolean isUsingIndex, String sqlTrail) {
        
        if (urlBase.contains(InjectionModel.STAR)) {
            
            if (!isUsingIndex) {
                return urlBase.replace(InjectionModel.STAR, sqlTrail);
            } else {
                return
                    urlBase.replace(
                        InjectionModel.STAR,
                        this.indexesInUrl.replaceAll(
                            "1337" + this.getMediatorStrategy().getNormal().getVisibleIndex() + "7331",
                            /**
                             * Oracle column often contains $, which is reserved for regex.
                             * => need to be escape with quoteReplacement()
                             */
                            Matcher.quoteReplacement(sqlTrail)
                        )
                    )
                ;
            }
        }
        
        return urlBase;
    }
    
    private String buildQuery(MethodInjection methodInjection, String paramLead, boolean isUsingIndex, String sqlTrail) {
        
        String query;
        
        paramLead = paramLead.replace("*", "SlQqLs*lSqQsL");
        
        // TODO simplify
        if (
            // No parameter transformation if method is not selected by user
            this.getMediatorUtils().getConnectionUtil().getMethodInjection() != methodInjection
            // No parameter transformation if injection point in URL
            || this.getMediatorUtils().getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)
        ) {
            
            // Just pass parameters without any transformation
            query = paramLead;
            
        } else if (
            // If method is selected by user and URL does not contains injection point
            // but parameters contain an injection point
            // then replace injection point by SQL expression in those parameter
            paramLead.contains(InjectionModel.STAR)
        ) {
            
            // Several SQL expressions does not use indexes in SELECT,
            // like Boolean, Error, Shell and search for Insertion character,
            // in that case replace injection point by SQL expression.
            // Injection point is always at the end?
            if (!isUsingIndex) {
                query = paramLead.replace(InjectionModel.STAR, sqlTrail + this.getMediatorVendor().getVendor().instance().endingComment());
                
            } else {
                
                // Replace injection point by indexes found for Normal strategy
                // and use visible Index for injection
                query = paramLead.replace(
                    InjectionModel.STAR,
                    this.indexesInUrl.replaceAll(
                        "1337" + this.getMediatorStrategy().getNormal().getVisibleIndex() + "7331",
                        /**
                         * Oracle column often contains $, which is reserved for regex.
                         * => need to be escape with quoteReplacement()
                         */
                        Matcher.quoteReplacement(sqlTrail)
                    ) + this.getMediatorVendor().getVendor().instance().endingComment()
                );
            }
            
        } else {
            
            // Method is selected by user and there's no injection point
            if (
                // Several SQL expressions does not use indexes in SELECT,
                // like Boolean, Error, Shell and search for Insertion character,
                // in that case concat SQL expression to the end of param.
                !isUsingIndex
            ) {
                
                query = paramLead + sqlTrail;
                
                // Add ending line comment by vendor
                query = query + this.getMediatorVendor().getVendor().instance().endingComment();
                
            } else {
                
                // Concat indexes found for Normal strategy to params
                // and use visible Index for injection
                query = paramLead + this.indexesInUrl.replaceAll(
                    "1337" + this.getMediatorStrategy().getNormal().getVisibleIndex() + "7331",
                    /**
                     * Oracle column often contains $, which is reserved for regex.
                     * => need to be escape with quoteReplacement()
                     */
                    Matcher.quoteReplacement(sqlTrail)
                );
                
                // Add ending line comment by vendor
                query = query + this.getMediatorVendor().getVendor().instance().endingComment();
            }
        }
        
        // TODO merge into function
        
        // Remove SQL comments
        query = query.replaceAll("(?s)/\\*.*?\\*/", "");
        
        if (
            methodInjection == this.getMediatorMethodInjection().getRequest()
            && this.getMediatorUtils().getParameterUtil().isRequestSoap()
        ) {
            
            query = query.replace("%2b", "+");
        } else {
            
            // Remove spaces after a word
            query = query.replaceAll("([^\\s\\w])(\\s+)", "$1");
            
            // Remove spaces before a word
            query = query.replaceAll("(\\s+)([^\\s\\w])", "$2");
            
            // Replace spaces
            query = query.replaceAll("\\s+", "+");
        }
        
        if (this.getMediatorUtils().getConnectionUtil().getMethodInjection() == methodInjection) {
            query = this.getMediatorUtils().getTamperingUtil().tamper(query);
        }
        
        if (methodInjection != this.getMediatorMethodInjection().getHeader()) {
            
            // URL encode each character because no query parameter context
            query = query.replace("\"", "%22");
            query = query.replace("'", "%27");
            query = query.replace("(", "%28");
            query = query.replace(")", "%29");
            query = query.replace("{", "%7B");
            query = query.replace("[", "%5B");
            query = query.replace("|", "%7C");
            query = query.replace("`", "%60");
            query = query.replace("]", "%5D");
            query = query.replace("}", "%7D");
            query = query.replace(">", "%3E");
            query = query.replace("<", "%3C");
            query = query.replace("?", "%3F");
            query = query.replace("_", "%5F");
            query = query.replace(" ", "+");
        } else {
            // For cookies in Spring
            // Replace spaces
            query = query.replace("+", "%20");
            query = query.replace(",", "%2C");
        }
        
        query = query.trim();
        
        return query;
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

    /**
     * Send each parameters from the GUI to the model in order to
     * start the preparation of injection, the injection process is
     * started in a new thread via model function inputValidation().
     */
    public void controlInput(
        String urlQuery,
        String dataRequest,
        String dataHeader,
        MethodInjection methodInjection,
        String typeRequest,
        boolean isScanning
    ) {
        try {
                
            if (!urlQuery.isEmpty() && !urlQuery.matches("(?i)^https?://.*")) {
                if (!urlQuery.matches("(?i)^\\w+://.*")) {
                    LOGGER.info("Undefined URL protocol, forcing to [http://]");
                    urlQuery = "http://"+ urlQuery;
                } else {
                    throw new MalformedURLException("unknown URL protocol");
                }
            }
                     
            this.getMediatorUtils().getParameterUtil().initializeQueryString(urlQuery);
            this.getMediatorUtils().getParameterUtil().initializeRequest(dataRequest);
            this.getMediatorUtils().getParameterUtil().initializeHeader(dataHeader);
            
            this.getMediatorUtils().getConnectionUtil().setMethodInjection(methodInjection);
            this.getMediatorUtils().getConnectionUtil().setTypeRequest(typeRequest);
            
            // TODO separate method
            if (isScanning) {
                this.beginInjection();
            } else {
                // Start the model injection process in a thread
                new Thread(InjectionModel.this::beginInjection, "ThreadBeginInjection").start();
            }
        } catch (MalformedURLException e) {
            
            LOGGER.warn("Incorrect Url: "+ e, e);
            
            // Incorrect URL, reset the start button
            Request request = new Request();
            request.setMessage(Interaction.END_PREPARATION);
            this.sendToViews(request);
        }
    }
    
    // TODO Util
    public void displayVersion() {
        
        String versionJava = System.getProperty("java.version");
        String nameSystemArchitecture = System.getProperty("os.arch");
        LOGGER.trace(
            "jSQL Injection v" + this.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("jsql.version")
            + " on Java "+ versionJava
            +"-"+ nameSystemArchitecture
            +"-"+ System.getProperty("user.language")
        );
    }
    
    public String getDatabaseInfos() {
        
        return
            "Database ["+ this.nameDatabase +"] "
            + "on "+ this.getMediatorVendor().getVendor() +" ["+ this.versionDatabase +"] "
            + "for user ["+ this.username +"]";
    }

    public void setDatabaseInfos(String versionDatabase, String nameDatabase, String username) {
        
        this.versionDatabase = versionDatabase;
        this.nameDatabase = nameDatabase;
        this.username = username;
    }
    
    // Getters and setters

    public String getSrcSuccess() {
        return this.srcSuccess;
    }

    public void setSrcSuccess(String srcSuccess) {
        this.srcSuccess = srcSuccess;
    }

    public String getIndexesInUrl() {
        return this.indexesInUrl;
    }

    public void setIndexesInUrl(String indexesInUrl) {
        this.indexesInUrl = indexesInUrl;
    }

    public boolean isInjectionAlreadyBuilt() {
        return this.injectionAlreadyBuilt;
    }

    public void setIsScanning(boolean isScanning) {
        this.isScanning = isScanning;
    }

    public String getVersionJsql() {
        return this.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("jsql.version");
    }

    public MediatorUtils getMediatorUtils() {
        return this.mediatorUtils;
    }

    public MediatorVendor getMediatorVendor() {
        return this.mediatorVendor;
    }

    public MediatorMethodInjection getMediatorMethodInjection() {
        return this.mediatorMethodInjection;
    }

    public DataAccess getDataAccess() {
        return this.dataAccess;
    }

    public RessourceAccess getResourceAccess() {
        return this.resourceAccess;
    }

    public MediatorStrategy getMediatorStrategy() {
        return this.mediatorStrategy;
    }

}

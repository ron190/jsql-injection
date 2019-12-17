/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;
import org.json.JSONException;

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.method.MediatorMethodInjection;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyInjectionBlind;
import com.jsql.model.injection.strategy.StrategyInjectionError;
import com.jsql.model.injection.strategy.StrategyInjectionNormal;
import com.jsql.model.injection.strategy.StrategyInjectionTime;
import com.jsql.model.injection.vendor.MediatorVendor;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.model.suspendable.SuspendableGetCharInsertion;
import com.jsql.model.suspendable.SuspendableGetVendor;
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
 * databases, tables, columns and values, and it can also retreive resources like files and shell.<br>
 * Tasks are run in multi-threads in general to speed the process.
 */
public class InjectionModel extends AbstractModelObservable {
    
    private MediatorVendor mediatorVendor = new MediatorVendor(InjectionModel.this);
    private MediatorMethodInjection mediatorMethodInjection = new MediatorMethodInjection(this);
    private MediatorUtils mediatorUtils;
    
    private DataAccess dataAccess = new DataAccess(this);
    private RessourceAccess resourceAccess = new RessourceAccess(this);

    public InjectionModel() {
        this.mediatorUtils = new MediatorUtils();

        this.mediatorUtils.setPropertiesUtil(new PropertiesUtil(this));
        this.mediatorUtils.setConnectionUtil(new ConnectionUtil(this));
        this.mediatorUtils.setAuthenticationUtil(new AuthenticationUtil(this));
        this.mediatorUtils.setGitUtil(new GitUtil(this));
        this.mediatorUtils.setHeaderUtil(new HeaderUtil(this));
        this.mediatorUtils.setParameterUtil(new ParameterUtil(this));
        this.mediatorUtils.setExceptionUtil(new ExceptionUtil(this));
        this.mediatorUtils.setSoapUtil(new SoapUtil(this));
        this.mediatorUtils.setJsonUtil(new JsonUtil(this));
        this.mediatorUtils.setPreferencesUtil(new PreferencesUtil(this));
        this.mediatorUtils.setProxyUtil(new ProxyUtil(this));
        this.mediatorUtils.setThreadUtil(new ThreadUtil(this));
        this.mediatorUtils.setTamperingUtil(new TamperingUtil(this));
    }

    public AbstractStrategy UNDEFINED = new AbstractStrategy(this) {

        @Override
        public void checkApplicability() throws JSqlException {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void allow() {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void unallow() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable)
                throws StoppedByUserSlidingException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void activateStrategy() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getPerformanceLength() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }
        
    };
    public AbstractStrategy TIME = new StrategyInjectionTime(this);
    public AbstractStrategy BLIND = new StrategyInjectionBlind(this);
    public AbstractStrategy ERROR = new StrategyInjectionError(this);
    public AbstractStrategy NORMAL = new StrategyInjectionNormal(this);
    
    public List<AbstractStrategy> strategies = Arrays.asList(this.UNDEFINED,this.TIME,this.BLIND,this.ERROR,this.NORMAL);
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public static final String STAR = "*";
    
    // TODO Pojo injection
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
    
//    /**
//     * Database vendor currently used.
//     * It can be switched to another vendor by automatic detection or manual selection.
//     */
//    private Vendor vendor = this.MYSQL;
//
//    /**
//     * Database vendor selected by user (default UNDEFINED).
//     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
//     */
//    private Vendor vendorByUser = this.AUTO;
    
    /**
     * Current injection strategy.
     */
//    private StrategyInjection strategy;
    private AbstractStrategy strategy;
    
    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    private boolean injectionAlreadyBuilt = false;
    
    private boolean isScanning = false;
    
    public final boolean IS_PARAM_BY_USER = true;
    public final boolean IS_JSON = true;

    /**
     * Reset each injection attributes: Database metadata, General Thread status, Strategy.
     */
    public void resetModel() {
        // TODO make injection pojo for all fields
        this.NORMAL.setVisibleIndex(null);
        this.indexesInUrl = "";
        
        this.getMediatorUtils().getConnectionUtil().setTokenCsrf(null);
        
        this.versionDatabase = null;
        this.nameDatabase = null;
        this.username = null;
        
        this.setIsStoppedByUser(false);
        this.injectionAlreadyBuilt = false;
        
        this.strategy = null;
        
        RessourceAccess.setReadingIsAllowed(false);
        
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
            
            hasFoundInjection = this.testParameters(this.getMediatorMethodInjection().getQuery());

            if (!hasFoundInjection) {
                hasFoundInjection = this.getMediatorUtils().getSoapUtil().testParameters();
            }
            
            if (!hasFoundInjection) {
                LOGGER.trace("Checking standard Request parameters");
                hasFoundInjection = this.testParameters(this.getMediatorMethodInjection().getRequest());
            }
            
            if (!hasFoundInjection) {
                hasFoundInjection = this.testParameters(this.getMediatorMethodInjection().getHeader());
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
     * Verify if injection works for specific Method using 3 modes: standard (last param), injection point
     * and full params injection. Special injections like JSON and SOAP are checked.
     * @param methodInjection currently tested (Query, Request or Header)
     * @param paramsAsString to verify if contains injection point
     * @param params from Query, Request or Header as a list of key/value to be tested for insertion character ;
     * Mode standard: last param, mode injection point: no test, mode full: every params.
     * @return true if injection didn't failed
     * @throws JSqlException when no params' integrity, process stopped by user, or injection failure
     */
    public boolean testParameters(MethodInjection methodInjection) throws JSqlException {
        boolean hasFoundInjection = false;
        
        // Injects URL, Request or Header params only if user tests every params
        // or method is selected by user.
        if (
            !this.getMediatorUtils().getPreferencesUtil().isCheckingAllParam()
            && this.getMediatorUtils().getConnectionUtil().getMethodInjection() != methodInjection
        ) {
            return hasFoundInjection;
        }
        
        // Force injection method of model to current running method
        this.getMediatorUtils().getConnectionUtil().setMethodInjection(methodInjection);
        
        // Injection by injection point
        if (methodInjection.getParamsAsString().contains(InjectionModel.STAR)) {
            LOGGER.info("Checking single "+ methodInjection.name() +" parameter with injection point at *");
            
            // Will keep param value as is,
            // Does not test for insertion character (param is null)
            hasFoundInjection = this.testStrategies(!this.IS_PARAM_BY_USER, !this.IS_JSON, null);
        
        // Default injection: last param tested only
        } else if (!methodInjection.isCheckingAllParam()) {
            // Injection point defined on last parameter
            methodInjection.getParams().stream().reduce((a, b) -> b).ifPresent(e -> e.setValue(e.getValue() + InjectionModel.STAR));

            // Will check param value by user.
            // Notice options 'Inject each URL params' and 'inject JSON' must be checked both
            // for JSON injection of last param
            hasFoundInjection = this.testStrategies(this.IS_PARAM_BY_USER, !this.IS_JSON, methodInjection.getParams().stream().reduce((a, b) -> b).orElseThrow());
            
        // Injection of every params: isCheckingAllParam() == true.
        // Params are tested one by one in two loops:
        //  - inner loop erases * from previous param
        //  - outer loop adds * to current param
        } else {
            
            // This param will be marked by * if injection is found,
            // inner loop will erase mark * otherwise
            injectionSuccessful:
            for (SimpleEntry<String, String> paramBase: methodInjection.getParams()) {

                // This param is the current tested one.
                // For JSON value attributes are traversed one by one to test every values.
                // For standard value mark * is simply added to the end of its value.
                for (SimpleEntry<String, String> paramStar: methodInjection.getParams()) {
                    
                    if (paramStar == paramBase) {
                        try {
                            // Will test if current value is a JSON entity
                            Object jsonEntity = JsonUtil.getJson(paramStar.getValue());
                            
                            // Define a tree of JSON attributes with path as the key: root.a => value of a
                            List<SimpleEntry<String, String>> attributesJson = JsonUtil.createEntries(jsonEntity, "root", null);
                            
                            // When option 'Inject JSON' is selected and there's a JSON entity to inject
                            // then loop through each paths to add * at the end of value and test each strategies.
                            // Marks * are erased between each tests.
                            if (this.getMediatorUtils().getPreferencesUtil().isCheckingAllJSONParam() && !attributesJson.isEmpty()) {
                                    hasFoundInjection = this.getMediatorUtils().getJsonUtil().testJsonParameter(methodInjection, paramStar);
                                
                            // Standard non JSON injection
                            } else {
                                hasFoundInjection = this.getMediatorUtils().getJsonUtil().testStandardParameter(methodInjection, paramStar);
                            }
                            
                            if (hasFoundInjection) {
                                break injectionSuccessful;
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Error parsing JSON parameters", e);
                        }
                        
                    }
                }
                
            }
        }
        
        return hasFoundInjection;
    }
    
    /**
     * Find the insertion character, test each strategy, inject metadata and list databases.
     * @param isParamByUser true if mode standard/JSON/full, false if injection point
     * @param isJson true if param contains JSON
     * @param parameter to be tested, null when injection point
     * @return true when successful injection
     * @throws JSqlException when no params' integrity, process stopped by user, or injection failure
     */
    // TODO Merge isParamByUser and parameter: isParamByUser = parameter != null
    public boolean testStrategies(boolean isParamByUser, boolean isJson, SimpleEntry<String, String> parameter) throws JSqlException {
        
        // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
        LOGGER.trace(I18n.valueByKey("LOG_GET_INSERTION_CHARACTER"));
        
        // Test for params integrity
        String characterInsertionByUser = this.getMediatorUtils().getParameterUtil().getCharacterInsertion(isParamByUser, parameter);
        
        // If not an injection point then find insertion character.
        // Force to 1 if no insertion char works and empty value from user,
        // Force to user's value if no insertion char works,
        // Force to insertion char otherwise.
        if (parameter != null) {
            String charInsertion = new SuspendableGetCharInsertion(this).run(characterInsertionByUser, parameter, isJson);
            LOGGER.info(I18n.valueByKey("LOG_USING_INSERTION_CHARACTER") +" ["+ charInsertion.replace(InjectionModel.STAR, "") +"]");
        }
        
        // Fingerprint database
        this.getMediatorVendor().vendor = new SuspendableGetVendor(this).run();

        // Test each injection strategies: time < blind < error < normal
        // Choose the most efficient strategy: normal > error > blind > time
        this.TIME.checkApplicability();
        this.BLIND.checkApplicability();
        this.ERROR.checkApplicability();
        this.NORMAL.checkApplicability();

        // Choose the most efficient strategy: normal > error > blind > time
        if (this.NORMAL.isApplicable()) {
            this.NORMAL.activateStrategy();
            
        } else if (this.ERROR.isApplicable()) {
            this.ERROR.activateStrategy();
            
        } else if (this.BLIND.isApplicable()) {
            this.BLIND.activateStrategy();
            
        } else if (this.TIME.isApplicable()) {
            this.TIME.activateStrategy();
            
        } else {
            throw new InjectionFailureException("No injection found");
        }
        
        return true;
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
            LOGGER.warn("Incorrect Query Url: "+ e.getMessage(), e);
            return "";
        }

        /**
         * Build the GET query string infos
         * TODO separate method
         */
        // TODO Extract in method
        if (!this.getMediatorUtils().getParameterUtil().getQueryString().isEmpty()) {
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
                LOGGER.warn("Incorrect Url: "+ e.getMessage(), e);
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
            if (!this.getMediatorUtils().getParameterUtil().getHeader().isEmpty()) {
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
             * TODO separate method
             */
            // TODO Extract in method
            if (!this.getMediatorUtils().getParameterUtil().getRequest().isEmpty() || this.getMediatorUtils().getConnectionUtil().getTokenCsrf() != null) {
                try {
                    ConnectionUtil.fixCustomRequestMethod(connection, this.getMediatorUtils().getConnectionUtil().getTypeRequest());
                    
                    connection.setDoOutput(true);
                    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    
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
                    LOGGER.warn("Error during Request connection: "+ e.getMessage(), e);
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
            LOGGER.warn("Error during connection: "+ e.getMessage(), e);
        }

        // return the source code of the page
        return pageSource;
    }
    
    /**
     * Build correct data for GET, POST, HEADER.<br>
     * Each can be:<br>
     *  - raw data (no injection)<br>
     *  - SQL query without index requirement<br>
     *  - SQL query with index requirement.
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
                            "1337" + this.NORMAL.getVisibleIndex() + "7331",
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
                query = paramLead.replace(InjectionModel.STAR, sqlTrail + this.getMediatorVendor().vendor.instance().endingComment());
                
            } else {
                
                // Replace injection point by indexes found for Normal strategy
                // and use visible Index for injection
                query = paramLead.replace(
                    InjectionModel.STAR,
                    this.indexesInUrl.replaceAll(
                        "1337" + this.NORMAL.getVisibleIndex() + "7331",
                        /**
                         * Oracle column often contains $, which is reserved for regex.
                         * => need to be escape with quoteReplacement()
                         */
                        Matcher.quoteReplacement(sqlTrail)
                    ) + this.getMediatorVendor().vendor.instance().endingComment()
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
                query = query + this.getMediatorVendor().vendor.instance().endingComment();
                
            } else {
                // Concat indexes found for Normal strategy to params
                // and use visible Index for injection
                query = paramLead + this.indexesInUrl.replaceAll(
                    "1337" + this.NORMAL.getVisibleIndex() + "7331",
                    /**
                     * Oracle column often contains $, which is reserved for regex.
                     * => need to be escape with quoteReplacement()
                     */
                    Matcher.quoteReplacement(sqlTrail)
                );
                
                // Add ending line comment by vendor
                query = query + this.getMediatorVendor().vendor.instance().endingComment();
            }
        }
        
        // TODO merge into function
        
        // Remove SQL comments
        query = query.replaceAll("(?s)/\\*.*?\\*/", "");
        
        if (methodInjection == this.getMediatorMethodInjection().getRequest()) {
            if (this.getMediatorUtils().getParameterUtil().isRequestSoap()) {
                query = query.replaceAll("%2b", "+");
            }
        } else {
            // Remove spaces after a word
            query = query.replaceAll("([^\\s\\w])(\\s+)", "$1");
            
            // Remove spaces before a word
            query = query.replaceAll("(\\s+)([^\\s\\w])", "$2");
            
            // Replace spaces
            query = query.replaceAll("\\s+", "+");
        }
        
        // TODO
        // Urlencode backtick and pipe (for Java only)
        query = query.replaceAll("(?s)`", "%60");
        query = query.replaceAll("(?s)\\|", "%7C");
        query = query.replaceAll("(?s)'", "%27");
        query = query.replaceAll("(?s)\\(", "%28");
        query = query.replaceAll("(?s)\\)", "%29");
        query = query.replaceAll("(?s)\\?", "%3F");
        query = query.replaceAll("(?s)>", "%3E");
//        query = query.replaceAll("(?s):", "%3A");
        // HTTP and Hibernate JPQL  purpose : => \:
        query = query.replaceAll("(?s):", "%5C%3A");
        query = query.replaceAll("(?s) ", "+");
        query = query.replaceAll("(?s)\"", "%22");
        
        if (this.getMediatorUtils().getConnectionUtil().getMethodInjection() == methodInjection) {
            query = this.getMediatorUtils().getTamperingUtil().tamper(query);
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
        Boolean isScanning
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
                     
            this.getMediatorUtils().getParameterUtil().initQueryString(urlQuery);
            this.getMediatorUtils().getParameterUtil().initRequest(dataRequest);
            this.getMediatorUtils().getParameterUtil().initHeader(dataHeader);
            
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
            LOGGER.warn("Incorrect Url: "+ e.getMessage(), e);
            
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
            + "on "+ this.getMediatorVendor().vendor +" ["+ this.versionDatabase +"] "
            + "for user ["+ this.username +"]";
    }

    public void setDatabaseInfos(String versionDatabase, String nameDatabase, String username) {
        this.versionDatabase = versionDatabase;
        this.nameDatabase = nameDatabase;
        this.username = username;
    }
    
    // Getters and setters
    
//    public Vendor getVendor() {
//        return this.vendor;
//    }
//
//    public Vendor getVendorByUser() {
//        return this.vendorByUser;
//    }
//
//    public void setVendorByUser(Vendor vendorByUser) {
//        this.vendorByUser = vendorByUser;
//    }
    
//    public StrategyInjection getStrategy() {
//    	return this.strategy;
//    }
//
//    public void setStrategy(StrategyInjection strategy) {
//        this.strategy = strategy;
//    }

    public String getSrcSuccess() {
        return this.srcSuccess;
    }

    public AbstractStrategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(AbstractStrategy strategy) {
        this.strategy = strategy;
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
        return mediatorVendor;
    }

    public MediatorMethodInjection getMediatorMethodInjection() {
        return mediatorMethodInjection;
    }

    public DataAccess getDataAccess() {
        return dataAccess;
    }

    public RessourceAccess getResourceAccess() {
        return resourceAccess;
    }

}

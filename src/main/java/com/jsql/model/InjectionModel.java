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
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyInjectionBlind;
import com.jsql.model.injection.strategy.StrategyInjectionError;
import com.jsql.model.injection.strategy.StrategyInjectionNormal;
import com.jsql.model.injection.strategy.StrategyInjectionTime;
import com.jsql.model.injection.vendor.model.AbstractVendor;
import com.jsql.model.injection.vendor.model.VendorXml;
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
    
    public Vendor AUTO = new Vendor("Database auto", null);
    public Vendor ACCESS = new Vendor("Access", new VendorXml("access.xml", InjectionModel.this));
    public Vendor COCKROACHDB = new Vendor("CockroachDB", new VendorXml("cockroachdb.xml", InjectionModel.this));
    public Vendor CUBRID = new Vendor("CUBRID", new VendorXml("cubrid.xml", InjectionModel.this));
    public Vendor DB2 = new Vendor("DB2", new VendorXml("db2.xml", InjectionModel.this));
    public Vendor DERBY = new Vendor("Derby", new VendorXml("derby.xml", InjectionModel.this));
    public Vendor FIREBIRD = new Vendor("Firebird", new VendorXml("firebird.xml", InjectionModel.this));
    public Vendor H2 = new Vendor("H2", new VendorXml("h2.xml", InjectionModel.this));
    public Vendor HANA = new Vendor("Hana", new VendorXml("hana.xml", InjectionModel.this));
    public Vendor HSQLDB = new Vendor("HSQLDB", new VendorXml("hsqldb.xml", InjectionModel.this));
    public Vendor INFORMIX = new Vendor("Informix", new VendorXml("informix.xml", InjectionModel.this));
    public Vendor INGRES = new Vendor("Ingres", new VendorXml("ingres.xml", InjectionModel.this));
    public Vendor MAXDB = new Vendor("MaxDB", new VendorXml("maxdb.xml", InjectionModel.this));
    public Vendor MCKOI = new Vendor("Mckoi", new VendorXml("mckoi.xml", InjectionModel.this));
    public Vendor MEMSQL = new Vendor("MemSQL", new VendorXml("memsql.xml", InjectionModel.this));
    public Vendor MYSQL = new Vendor("MySQL", new VendorXml("mysql.xml", InjectionModel.this));
    public Vendor NEO4J = new Vendor("Neo4j", new VendorXml("neo4j.xml", InjectionModel.this));
    public Vendor NUODB = new Vendor("NuoDB", new VendorXml("nuodb.xml", InjectionModel.this));
    public Vendor ORACLE = new Vendor("Oracle", new VendorXml("oracle.xml", InjectionModel.this));
    public Vendor POSTGRESQL = new Vendor("PostgreSQL", new VendorXml("postgresql.xml", InjectionModel.this));
    public Vendor SQLITE = new Vendor("SQLite", new VendorXml("sqlite.xml", InjectionModel.this)) {
         
         @Override
         public String transform(String resultToParse) {
             
             StringBuilder resultSQLite = new StringBuilder();
             String resultTmp = resultToParse.replaceFirst(".+?\\(", "").trim().replaceAll("\\)$", "");
             resultTmp = resultTmp.replaceAll("\\(.+?\\)", "");
             
             for (String columnNameAndType: resultTmp.split(",")) {
                 // Some recent SQLite use tabulation character as a separator => split() by any  white space \s
                 String columnName = columnNameAndType.trim().split("\\s")[0];
                 
                 // Some recent SQLite enclose names with ` => strip those `
                 columnName = StringUtils.strip(columnName, "`");
                 
                 if (!"CONSTRAINT".equals(columnName) && !"UNIQUE".equals(columnName)) {
                     resultSQLite.append((char) 4 + columnName + (char) 5 + "0" + (char) 4 + (char) 6);
                 }
             }
     
             return resultSQLite.toString();
             
         }
         
     };
    public Vendor SQLSERVER = new Vendor("SQL Server", new VendorXml("sqlserver.xml", InjectionModel.this));
    public Vendor SYBASE = new Vendor("Sybase", new VendorXml("sybase.xml", InjectionModel.this));
    public Vendor TERADATA = new Vendor("Teradata", new VendorXml("teradata.xml", InjectionModel.this));
    public Vendor VERTICA = new Vendor("Vertica", new VendorXml("vertica.xml", InjectionModel.this));
    
    public List<Vendor> vendors = Arrays.asList(this.ACCESS,this.COCKROACHDB,this.CUBRID,this.DB2,this.DERBY,this.FIREBIRD,this.H2,this.HANA,this.HSQLDB,this.INFORMIX,this.INGRES,this.MAXDB,this.MCKOI,this.MEMSQL,this.MYSQL,this.NEO4J,this.NUODB,this.ORACLE,this.POSTGRESQL,this.SQLITE,this.SQLSERVER,this.SYBASE,this.TERADATA,this.VERTICA);
    
    public class Vendor {
        
        private final String labelVendor;
        
        private final AbstractVendor instanceVendor;
        
        private Vendor(String labelVendor, AbstractVendor instanceVendor) {
            this.labelVendor = labelVendor;
            this.instanceVendor = instanceVendor;
        }
        
        public AbstractVendor instance() {
            return this.instanceVendor;
        }
        
        @Override
        public String toString() {
            return this.labelVendor;
        }
        
        public String transform(String resultToParse) {
            return "";
        }
        
    }
    
    public abstract class MethodInjection {
        
        public abstract boolean isCheckingAllParam();
        public abstract String getParamsAsString();
        public abstract List<SimpleEntry<String, String>> getParams();
        public abstract String name();
        
    }
    

    
    public MethodInjection QUERY = new MethodInjection() {
        
        @Override
        public boolean isCheckingAllParam() {
            return InjectionModel.this.preferencesUtil.isCheckingAllURLParam();
        }

        @Override
        public String getParamsAsString() {
            return InjectionModel.this.parameterUtil.getQueryStringFromEntries();
        }

        @Override
        public List<SimpleEntry<String, String>> getParams() {
            return InjectionModel.this.parameterUtil.getQueryString();
        }

        @Override
        public String name() {
            return "QUERY";
        }
        
    };
    
    public MethodInjection REQUEST = new MethodInjection() {
        
        @Override
        public boolean isCheckingAllParam() {
            return InjectionModel.this.preferencesUtil.isCheckingAllRequestParam();
        }

        @Override
        public String getParamsAsString() {
            return InjectionModel.this.parameterUtil.getRequestFromEntries();
        }

        @Override
        public List<SimpleEntry<String, String>> getParams() {
            return InjectionModel.this.parameterUtil.getRequest();
        }

        @Override
        public String name() {
            return "REQUEST";
        }
        
    };
    
    public MethodInjection HEADER = new MethodInjection() {
        
        @Override
        public boolean isCheckingAllParam() {
            return InjectionModel.this.preferencesUtil.isCheckingAllHeaderParam();
        }

        @Override
        public String getParamsAsString() {
            return InjectionModel.this.parameterUtil.getHeaderFromEntries();
        }

        @Override
        public List<SimpleEntry<String, String>> getParams() {
            return InjectionModel.this.parameterUtil.getHeader();
        }

        @Override
        public String name() {
            return "HEADER";
        }
        
    };
    
//    Mediator
    public PropertiesUtil propertiesUtil = new PropertiesUtil(this);
    public DataAccess dataAccess = new DataAccess(this);
    public RessourceAccess resourceAccess = new RessourceAccess(this);
    public ConnectionUtil connectionUtil = new ConnectionUtil(this);
    public AuthenticationUtil authenticationUtil = new AuthenticationUtil(this);
    public GitUtil gitUtil = new GitUtil(this);
    public HeaderUtil headerUtil = new HeaderUtil(this);
    public ParameterUtil parameterUtil = new ParameterUtil(this);
    public ExceptionUtil exceptionUtil = new ExceptionUtil(this);
    public SoapUtil soapUtil = new SoapUtil(this);
    public JsonUtil jsonUtil = new JsonUtil(this);
    public PreferencesUtil preferencesUtil = new PreferencesUtil(this);
    public ProxyUtil proxyUtil = new ProxyUtil(this);
    public ThreadUtil threadUtil = new ThreadUtil(this);
    
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
    
    public List<MethodInjection> methods = Arrays.asList(this.QUERY,this.REQUEST,this.HEADER);
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
    
    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    private Vendor vendor = this.MYSQL;

    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
     */
    private Vendor vendorByUser = this.AUTO;
    
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
        StrategyInjectionNormal.setVisibleIndex(null);
        this.indexesInUrl = "";
        
        this.connectionUtil.setTokenCsrf(null);
        
        this.versionDatabase = null;
        this.nameDatabase = null;
        this.username = null;
        
        this.setIsStoppedByUser(false);
        this.injectionAlreadyBuilt = false;
        
        this.strategy = null;
        
        RessourceAccess.setReadingIsAllowed(false);
        
        this.threadUtil.reset();
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via shouldStopAll).
     * Erase all attributes eventually defined in a previous injection.
     * Run by Scan, Standard and TU.
     */
    public void beginInjection() {
        this.resetModel();
        
        try {
            if (!this.proxyUtil.isLive(ShowOnConsole.YES)) {
                return;
            }
            
            LOGGER.info(I18n.valueByKey("LOG_START_INJECTION") +": "+ this.connectionUtil.getUrlByUser());
            
            // Check general integrity if user's parameters
            this.parameterUtil.checkParametersFormat();
            
            // Check connection is working: define Cookie management, check HTTP status, parse <form> parameters, process CSRF
            LOGGER.trace(I18n.valueByKey("LOG_CONNECTION_TEST"));
            this.connectionUtil.testConnection();
            
            boolean hasFoundInjection = false;
            
            hasFoundInjection = this.testParameters(this.QUERY);

            if (!hasFoundInjection) {
                hasFoundInjection = this.soapUtil.testParameters();
            }
            
            if (!hasFoundInjection) {
                LOGGER.trace("Checking standard Request parameters");
                hasFoundInjection = this.testParameters(this.REQUEST);
            }
            
            if (!hasFoundInjection) {
                hasFoundInjection = this.testParameters(this.HEADER);
            }
            
            if (!this.isScanning) {
                if (!this.preferencesUtil.isNotInjectingMetadata()) {
                    this.dataAccess.getDatabaseInfos();
                }
                this.dataAccess.listDatabases();
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
            !this.preferencesUtil.isCheckingAllParam()
            && this.connectionUtil.getMethodInjection() != methodInjection
        ) {
            return hasFoundInjection;
        }
        
        // Force injection method of model to current running method
        this.connectionUtil.setMethodInjection(methodInjection);
        
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
                            if (this.preferencesUtil.isCheckingAllJSONParam() && !attributesJson.isEmpty()) {
                                    hasFoundInjection = this.jsonUtil.testJsonParameter(methodInjection, paramStar);
                                
                            // Standard non JSON injection
                            } else {
                                hasFoundInjection = this.jsonUtil.testStandardParameter(methodInjection, paramStar);
                            }
                            
                            if (hasFoundInjection) {
                                break injectionSuccessful;
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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
        String characterInsertionByUser = this.parameterUtil.getCharacterInsertion(isParamByUser, parameter);
        
        // If not an injection point then find insertion character.
        // Force to 1 if no insertion char works and empty value from user,
        // Force to user's value if no insertion char works,
        // Force to insertion char otherwise.
        if (parameter != null) {
            String charInsertion = new SuspendableGetCharInsertion(this).run(characterInsertionByUser, parameter, isJson);
            LOGGER.info(I18n.valueByKey("LOG_USING_INSERTION_CHARACTER") +" ["+ charInsertion.replace(InjectionModel.STAR, "") +"]");
        }
        
        // Fingerprint database
        this.vendor = new SuspendableGetVendor(this).run();

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
        String urlInjection = this.connectionUtil.getUrlBase();
        
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
        if (!this.parameterUtil.getQueryString().isEmpty()) {
            // URL without querystring like Request and Header can receive
            // new params from <form> parsing, in that case add the '?' to URL
            if (!urlInjection.contains("?")) {
                urlInjection += "?";
            }

            urlInjection += this.buildQuery(this.QUERY, this.parameterUtil.getQueryStringFromEntries(), isUsingIndex, dataInjection);
            
            if (this.connectionUtil.getTokenCsrf() != null) {
                urlInjection += "&"+ this.connectionUtil.getTokenCsrf().getKey() +"="+ this.connectionUtil.getTokenCsrf().getValue();
            }
            
            try {
                urlObject = new URL(urlInjection);
            } catch (MalformedURLException e) {
                LOGGER.warn("Incorrect Url: "+ e.getMessage(), e);
            }
        } else {
            if (this.connectionUtil.getTokenCsrf() != null) {
                urlInjection += "?"+ this.connectionUtil.getTokenCsrf().getKey() +"="+ this.connectionUtil.getTokenCsrf().getValue();
            }
        }
        
        HttpURLConnection connection;
        String pageSource = "";
        
        // Define the connection
        try {
            
            // TODO separate method
            // Block Opening Connection
            if (this.authenticationUtil.isKerberos()) {
                String kerberosConfiguration =
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(StringUtils.join(Files.readAllLines(Paths.get(this.authenticationUtil.getPathKerberosLogin()), Charset.defaultCharset()), ""))
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(kerberosConfiguration);
                connection = spnego.connect(urlObject);
            } else {
                connection = (HttpURLConnection) urlObject.openConnection();
            }
            
            connection.setReadTimeout(this.connectionUtil.getTimeout());
            connection.setConnectTimeout(this.connectionUtil.getTimeout());
            connection.setDefaultUseCaches(false);
            
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            // Csrf
            
            if (this.connectionUtil.getTokenCsrf() != null) {
                connection.setRequestProperty(this.connectionUtil.getTokenCsrf().getKey(), this.connectionUtil.getTokenCsrf().getValue());
            }
            
            this.connectionUtil.fixJcifsTimeout(connection);

            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(Header.URL, urlInjection);
            
            /**
             * Build the HEADER and logs infos
             */
            // TODO Extract in method
            if (!this.parameterUtil.getHeader().isEmpty()) {
                Stream.of(this.buildQuery(this.HEADER, this.parameterUtil.getHeaderFromEntries(), isUsingIndex, dataInjection).split("\\\\r\\\\n"))
                .forEach(e -> {
                    if (e.split(":").length == 2) {
                        HeaderUtil.sanitizeHeaders(connection, new SimpleEntry<>(e.split(":")[0], e.split(":")[1]));
                    }
                });
                
                msgHeader.put(Header.HEADER, this.buildQuery(this.HEADER, this.parameterUtil.getHeaderFromEntries(), isUsingIndex, dataInjection));
            }
    
            /**
             * Build the POST and logs infos
             * TODO separate method
             */
            // TODO Extract in method
            if (!this.parameterUtil.getRequest().isEmpty() || this.connectionUtil.getTokenCsrf() != null) {
                try {
                    ConnectionUtil.fixCustomRequestMethod(connection, this.connectionUtil.getTypeRequest());
                    
                    connection.setDoOutput(true);
                    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    
                    DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                    if (this.connectionUtil.getTokenCsrf() != null) {
                        dataOut.writeBytes(this.connectionUtil.getTokenCsrf().getKey() +"="+ this.connectionUtil.getTokenCsrf().getValue() +"&");
                    }
                    if (this.connectionUtil.getTypeRequest().matches("PUT|POST")) {
                        if (this.parameterUtil.isRequestSoap()) {
                            dataOut.writeBytes(this.buildQuery(this.REQUEST, this.parameterUtil.getRawRequest(), isUsingIndex, dataInjection));
                        } else {
                            dataOut.writeBytes(this.buildQuery(this.REQUEST, this.parameterUtil.getRequestFromEntries(), isUsingIndex, dataInjection));
                        }
                    }
                    dataOut.flush();
                    dataOut.close();
                    
                    if (this.parameterUtil.isRequestSoap()) {
                        msgHeader.put(Header.POST, this.buildQuery(this.REQUEST, this.parameterUtil.getRawRequest(), isUsingIndex, dataInjection));
                    } else {
                        msgHeader.put(Header.POST, this.buildQuery(this.REQUEST, this.parameterUtil.getRequestFromEntries(), isUsingIndex, dataInjection));
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
                            "1337" + StrategyInjectionNormal.getVisibleIndex() + "7331",
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
            this.connectionUtil.getMethodInjection() != methodInjection
            // No parameter transformation if injection point in URL
            || this.connectionUtil.getUrlBase().contains(InjectionModel.STAR)
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
                query = paramLead.replace(InjectionModel.STAR, sqlTrail + this.vendor.instance().endingComment());
                
            } else {
                
                // Replace injection point by indexes found for Normal strategy
                // and use visible Index for injection
                query = paramLead.replace(
                    InjectionModel.STAR,
                    this.indexesInUrl.replaceAll(
                        "1337" + StrategyInjectionNormal.getVisibleIndex() + "7331",
                        /**
                         * Oracle column often contains $, which is reserved for regex.
                         * => need to be escape with quoteReplacement()
                         */
                        Matcher.quoteReplacement(sqlTrail)
                    ) + this.vendor.instance().endingComment()
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
                query = query + this.vendor.instance().endingComment();
                
            } else {
                // Concat indexes found for Normal strategy to params
                // and use visible Index for injection
                query = paramLead + this.indexesInUrl.replaceAll(
                    "1337" + StrategyInjectionNormal.getVisibleIndex() + "7331",
                    /**
                     * Oracle column often contains $, which is reserved for regex.
                     * => need to be escape with quoteReplacement()
                     */
                    Matcher.quoteReplacement(sqlTrail)
                );
                
                // Add ending line comment by vendor
                query = query + this.vendor.instance().endingComment();
            }
        }
        
        // TODO merge into function
        
        // Remove SQL comments
        query = query.replaceAll("(?s)/\\*.*?\\*/", "");
        
        if (methodInjection == this.REQUEST) {
            if (this.parameterUtil.isRequestSoap()) {
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
        
        if (this.connectionUtil.getMethodInjection() == methodInjection) {
            query = TamperingUtil.tamper(query);
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
                     
            this.parameterUtil.initQueryString(urlQuery);
            this.parameterUtil.initRequest(dataRequest);
            this.parameterUtil.initHeader(dataHeader);
            
            this.connectionUtil.setMethodInjection(methodInjection);
            this.connectionUtil.setTypeRequest(typeRequest);
            
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
            "jSQL Injection v" + this.propertiesUtil.getProperties().getProperty("jsql.version")
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
        return this.propertiesUtil.getProperties().getProperty("jsql.version");
    }

}

package com.jsql.model.injection.vendor;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;

public class MediatorVendor {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String LOG_VENDOR = "{} [{}]";

    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    private Vendor vendor;

    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
     */
    private Vendor vendorByUser;

    // TODO Replace with enum
    private Vendor auto;
    private Vendor access;
    private Vendor altibase;
    private Vendor ctreeACE;
    private Vendor cockroachDB;
    private Vendor cubrid;
    private Vendor db2;
    private Vendor derby;
    private Vendor exasol;
    private Vendor frontbase;
    private Vendor firebird;
    private Vendor h2;
    private Vendor hana;
    private Vendor hsqldb;
    private Vendor informix;
    private Vendor ingres;
    private Vendor iris;
    private Vendor maxDB;
    private Vendor mckoi;
    private Vendor memSQL;
    private Vendor mimerSQL;
    private Vendor monetDB;
    private Vendor mySQL;
    private Vendor neo4j;
    private Vendor netezza;
    private Vendor nuoDB;
    private Vendor oracle;
    private Vendor postgreSQL;
    private Vendor presto;
    private Vendor sqlite;
    private Vendor sqlServer;
    private Vendor sybase;
    private Vendor teradata;
    private Vendor vertica;
    
    private List<Vendor> vendors;
    
    private InjectionModel injectionModel;
    
    public MediatorVendor(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
        
        this.auto = new Vendor();
        this.access = new Vendor(new VendorYaml("access.yml", injectionModel));
        this.altibase = new Vendor(new VendorYaml("altibase.yml", injectionModel));
        this.ctreeACE = new Vendor(new VendorYaml("ctreeace.yml", injectionModel));
        this.cockroachDB = new Vendor(new VendorYaml("cockroachdb.yml", injectionModel));
        this.cubrid = new Vendor(new VendorYaml("cubrid.yml", injectionModel));
        this.db2 = new Vendor(new VendorYaml("db2.yml", injectionModel));
        this.derby = new Vendor(new VendorYaml("derby.yml", injectionModel));
        this.exasol = new Vendor(new VendorYaml("exasol.yml", injectionModel));
        this.frontbase = new Vendor(new VendorYaml("frontbase.yml", injectionModel));
        this.firebird = new Vendor(new VendorYaml("firebird.yml", injectionModel));
        this.h2 = new Vendor(new VendorYaml("h2.yml", injectionModel));
        this.hana = new Vendor(new VendorYaml("hana.yml", injectionModel));
        this.hsqldb = new Vendor(new VendorYaml("hsqldb.yml", injectionModel));
        this.informix = new Vendor(new VendorYaml("informix.yml", injectionModel));
        this.ingres = new Vendor(new VendorYaml("ingres.yml", injectionModel));
        this.iris = new Vendor(new VendorYaml("iris.yml", injectionModel));
        this.maxDB = new Vendor(new VendorYaml("maxdb.yml", injectionModel));
        this.mckoi = new Vendor(new VendorYaml("mckoi.yml", injectionModel));
        this.memSQL = new Vendor(new VendorYaml("memsql.yml", injectionModel));
        this.mimerSQL = new Vendor(new VendorYaml("mimersql.yml", injectionModel));
        this.monetDB = new Vendor(new VendorYaml("monetdb.yml", injectionModel));
        this.mySQL = new Vendor(new VendorYaml("mysql.yml", injectionModel));
        this.neo4j = new Vendor(new VendorYaml("neo4j.yml", injectionModel));
        this.netezza = new Vendor(new VendorYaml("netezza.yml", injectionModel));
        this.nuoDB = new Vendor(new VendorYaml("nuodb.yml", injectionModel));
        this.oracle = new Vendor(new VendorYaml("oracle.yml", injectionModel));
        this.postgreSQL = new Vendor(new VendorYaml("postgresql.yml", injectionModel));
        this.presto = new Vendor(new VendorYaml("presto.yml", injectionModel));
        this.sqlite = new Vendor(new VendorYaml("sqlite.yml", injectionModel)) {
             
            @Override
            public String transformSqlite(String resultToParse) {
                
                var resultSqlite = new StringBuilder();
                
                String resultTmp = resultToParse
                    .replaceFirst("[^(]+\\(", StringUtils.EMPTY)
                    .trim()
                    .replaceAll("\\)$", StringUtils.EMPTY);
                
                resultTmp = resultTmp.replaceAll("\\([^)]+\\)", StringUtils.EMPTY);
                
                for (String columnNameAndType: resultTmp.split(",")) {
                    
                    if (columnNameAndType.trim().startsWith("primary key")) {
                        continue;
                    }
                    
                    // Some recent SQLite use tabulation character as a separator => split() by any white space \s
                    String columnName = columnNameAndType.trim().split("\\s")[0];
                    
                    // Some recent SQLite enclose names with ` => strip those `
                    columnName = StringUtils.strip(columnName, "`");
                    
                    if (
                        !"CONSTRAINT".equals(columnName)
                        && !"UNIQUE".equals(columnName)
                    ) {
                        
                        // Generate pattern \4\5\4\6 for injection parsing
                        resultSqlite.append((char) 4 + columnName + (char) 5 + "0" + (char) 4 + (char) 6);
                    }
                }
         
                return resultSqlite.toString();
            }
        };
        this.sqlServer = new Vendor(new VendorYaml("sqlserver.yml", injectionModel));
        this.sybase = new Vendor(new VendorYaml("sybase.yml", injectionModel));
        this.teradata = new Vendor(new VendorYaml("teradata.yml", injectionModel));
        this.vertica = new Vendor(new VendorYaml("vertica.yml", injectionModel));
        
        this.vendors = Arrays.asList(
            this.auto,
            this.access,
            this.altibase,
            this.ctreeACE,
            this.cockroachDB,
            this.cubrid,
            this.db2,
            this.derby,
            this.exasol,
            this.firebird,
            this.frontbase,
            this.h2,
            this.hana,
            this.hsqldb,
            this.informix,
            this.ingres,
            this.iris,
            this.maxDB,
            this.mckoi,
            this.memSQL,
            this.mimerSQL,
            this.monetDB,
            this.mySQL,
            this.neo4j,
            this.netezza,
            this.nuoDB,
            this.oracle,
            this.postgreSQL,
            this.presto,
            this.sqlite,
            this.sqlServer,
            this.sybase,
            this.teradata,
            this.vertica
        );
        
        this.setVendor(this.mySQL);
        this.vendorByUser = this.auto;
    }
    
    public boolean isSqlite() {
        
        return this.getVendor() == this.getSqlite();
    }
    
    public Vendor fingerprintVendor() {
        
        Vendor vendorFound = null;
        
        if (this.injectionModel.getMediatorVendor().getVendorByUser() != this.injectionModel.getMediatorVendor().getAuto()) {
            
            vendorFound = this.injectionModel.getMediatorVendor().getVendorByUser();
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                MediatorVendor.LOG_VENDOR,
                () -> I18nUtil.valueByKey("LOG_DATABASE_TYPE_FORCED_BY_USER"),
                () -> this.injectionModel.getMediatorVendor().getVendorByUser()
            );
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, "Fingerprinting database...");
        
            var insertionCharacter = "'\"#-)'\"*";
            String pageSource = this.injectionModel.injectWithoutIndex(insertionCharacter, "test#vendor");
                
            var mediatorVendor = this.injectionModel.getMediatorVendor();
            Vendor[] vendorsWithoutAuto =
                mediatorVendor
                .getVendors()
                .stream()
                .filter(v -> v != mediatorVendor.getAuto())
                .toArray(Vendor[]::new);
            
            // Test each vendor
            for (Vendor vendorTest: vendorsWithoutAuto) {
                
                if (pageSource.matches("(?si)"+ vendorTest.instance().fingerprintErrorsAsRegex())) {
                    
                    vendorFound = vendorTest;
                    LOGGER.log(
                        LogLevel.CONSOLE_SUCCESS,
                        MediatorVendor.LOG_VENDOR,
                        () -> "Basic fingerprint matching vendor",
                        () -> vendorTest
                    );
                    break;
                }
            }
            
            vendorFound = this.initializeVendor(vendorFound);
        }
        
        var requestSetVendor = new Request();
        requestSetVendor.setMessage(Interaction.SET_VENDOR);
        requestSetVendor.setParameters(vendorFound);
        this.injectionModel.sendToViews(requestSetVendor);
        
        return vendorFound;
    }

    public Vendor initializeVendor(Vendor vendor) {
        
        var vendorFixed = vendor;
        
        if (vendorFixed == null) {
            
            vendorFixed = this.injectionModel.getMediatorVendor().getMySQL();
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                MediatorVendor.LOG_VENDOR,
                () -> I18nUtil.valueByKey("LOG_DATABASE_TYPE_NOT_FOUND"),
                () -> this.injectionModel.getMediatorVendor().getMySQL()
            );
            
        } else {
            
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                MediatorVendor.LOG_VENDOR,
                () -> I18nUtil.valueByKey("LOG_USING_DATABASE_TYPE"),
                () -> vendor
            );
            
            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(
                Header.URL,
                this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser()
            );
            msgHeader.put(Header.VENDOR, vendorFixed);
            
            var requestDatabaseIdentified = new Request();
            requestDatabaseIdentified.setMessage(Interaction.DATABASE_IDENTIFIED);
            requestDatabaseIdentified.setParameters(msgHeader);
            this.injectionModel.sendToViews(requestDatabaseIdentified);
        }
        
        return vendorFixed;
    }
    
    
    // Getter and setter
    
    public Vendor getAuto() {
        return this.auto;
    }

    public Vendor getCubrid() {
        return this.cubrid;
    }

    public Vendor getH2() {
        return this.h2;
    }

    public Vendor getPostgreSQL() {
        return this.postgreSQL;
    }

    public Vendor getMySQL() {
        return this.mySQL;
    }

    public Vendor getSqlite() {
        return this.sqlite;
    }

    public Vendor getSqlServer() {
        return this.sqlServer;
    }

    public Vendor getNeo4j() {
        return this.neo4j;
    }
    
    public Vendor getVendor() {
        return this.vendor;
    }

    public Vendor getVendorByUser() {
        return this.vendorByUser;
    }

    public void setVendorByUser(Vendor vendorByUser) {
        this.vendorByUser = vendorByUser;
    }

    public List<Vendor> getVendors() {
        return this.vendors;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Vendor getDb2() {
        return this.db2;
    }

    public Vendor getHsqldb() {
        return this.hsqldb;
    }

    public Vendor getDerby() {
        return this.derby;
    }

    public Vendor getOracle() {
        return this.oracle;
    }
}

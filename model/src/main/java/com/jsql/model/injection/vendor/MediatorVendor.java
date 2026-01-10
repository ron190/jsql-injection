package com.jsql.model.injection.vendor;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MediatorVendor {
    
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
    private final Vendor auto;
    private final Vendor cubrid;
    private final Vendor db2;
    private final Vendor derby;
    private final Vendor exasol;
    private final Vendor firebird;
    private final Vendor h2;
    private final Vendor hana;
    private final Vendor hsqldb;
    private final Vendor informix;
    private final Vendor mckoi;
    private final Vendor mimer;
    private final Vendor monetdb;
    private final Vendor mysql;
    private final Vendor neo4j;
    private final Vendor oracle;
    private final Vendor postgres;
    private final Vendor sqlite;
    private final Vendor sqlserver;
    private final Vendor sybase;
    private final Vendor vertica;

    private final List<Vendor> vendors;
    private final List<Vendor> vendorsForFingerprint;

    private final InjectionModel injectionModel;
    
    public MediatorVendor(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        Vendor access = new Vendor(new VendorYaml("access.yml", injectionModel));
        Vendor altibase = new Vendor(new VendorYaml("altibase.yml", injectionModel));
        Vendor ctreeace = new Vendor(new VendorYaml("ctreeace.yml", injectionModel));
        Vendor frontbase = new Vendor(new VendorYaml("frontbase.yml", injectionModel));
        Vendor ingres = new Vendor(new VendorYaml("ingres.yml", injectionModel));
        Vendor iris = new Vendor(new VendorYaml("iris.yml", injectionModel));
        Vendor maxdb = new Vendor(new VendorYaml("maxdb.yml", injectionModel));
        Vendor netezza = new Vendor(new VendorYaml("netezza.yml", injectionModel));
        Vendor nuodb = new Vendor(new VendorYaml("nuodb.yml", injectionModel));
        Vendor presto = new Vendor(new VendorYaml("presto.yml", injectionModel));
        Vendor teradata = new Vendor(new VendorYaml("teradata.yml", injectionModel));

        this.auto = new Vendor();
        this.cubrid = new Vendor(new VendorYaml("cubrid.yml", injectionModel));
        this.db2 = new Vendor(new VendorYaml("db2.yml", injectionModel));
        this.derby = new Vendor(new VendorYaml("derby.yml", injectionModel));
        this.exasol = new Vendor(new VendorYaml("exasol.yml", injectionModel));
        this.firebird = new Vendor(new VendorYaml("firebird.yml", injectionModel));
        this.h2 = new Vendor(new VendorYaml("h2.yml", injectionModel));
        this.hana = new Vendor(new VendorYaml("hana.yml", injectionModel));
        this.hsqldb = new Vendor(new VendorYaml("hsqldb.yml", injectionModel));
        this.informix = new Vendor(new VendorYaml("informix.yml", injectionModel));
        this.mckoi = new Vendor(new VendorYaml("mckoi.yml", injectionModel));
        this.mimer = new Vendor(new VendorYaml("mimersql.yml", injectionModel));
        this.monetdb = new Vendor(new VendorYaml("monetdb.yml", injectionModel));
        this.mysql = new Vendor(new VendorYaml("mysql.yml", injectionModel));
        this.neo4j = new Vendor(new VendorYaml("neo4j.yml", injectionModel));
        this.oracle = new Vendor(new VendorYaml("oracle.yml", injectionModel));
        this.postgres = new Vendor(new VendorYaml("postgres.yml", injectionModel));
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
                        resultSqlite.append((char) 4).append(columnName).append((char) 5).append("0").append((char) 4).append((char) 6);
                    }
                }
                return resultSqlite.toString();
            }
        };
        this.sqlserver = new Vendor(new VendorYaml("sqlserver.yml", injectionModel));
        this.sybase = new Vendor(new VendorYaml("sybase.yml", injectionModel));
        this.vertica = new Vendor(new VendorYaml("vertica.yml", injectionModel));

        this.vendors = Arrays.asList(
            this.auto, access, altibase, ctreeace, this.cubrid, this.db2, this.derby, exasol, this.firebird, frontbase, this.h2,
            hana, this.hsqldb, this.informix, ingres, iris, maxdb, this.mckoi, this.mimer, this.monetdb, this.mysql, this.neo4j,
            netezza, nuodb, this.oracle, this.postgres, presto, this.sqlite, this.sqlserver, this.sybase, teradata, this.vertica
        );
        this.vendorsForFingerprint = Arrays.asList(
            this.mysql, this.postgres, this.sqlite, this.h2, this.hsqldb, this.oracle, this.sqlserver, access, altibase, ctreeace,
            this.cubrid, this.db2, this.derby, exasol, this.firebird, frontbase, hana, this.informix, ingres, iris, maxdb, this.mckoi,
            this.mimer, this.monetdb, this.neo4j, netezza, nuodb, presto, this.sybase, teradata, this.vertica
        );

        this.setVendor(this.mysql);
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
                LogLevelUtil.CONSOLE_INFORM,
                MediatorVendor.LOG_VENDOR,
                () -> I18nUtil.valueByKey("LOG_DATABASE_TYPE_FORCED_BY_USER"),
                () -> this.injectionModel.getMediatorVendor().getVendorByUser()
            );
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database...");
            var insertionCharacter = URLEncoder.encode("'\"#-)'\"*", StandardCharsets.UTF_8);
            String pageSource = this.injectionModel.injectWithoutIndex(insertionCharacter, "test#vendor");
                
            var mediatorVendor = this.injectionModel.getMediatorVendor();
            Vendor[] vendorsWithoutAuto = mediatorVendor.getVendors()
                .stream()
                .filter(v -> v != mediatorVendor.getAuto())
                .toArray(Vendor[]::new);
            
            // Test each vendor
            for (Vendor vendorTest: vendorsWithoutAuto) {
                if (pageSource.matches(vendorTest.instance().fingerprintErrorsAsRegex())) {
                    vendorFound = vendorTest;
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_SUCCESS,
                        MediatorVendor.LOG_VENDOR,
                        () -> "Basic fingerprint matching vendor",
                        () -> vendorTest
                    );
                    break;
                }
            }
            vendorFound = this.initVendor(vendorFound);
        }

        var urlGitHub = this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperty("github.url");
        this.injectionModel.appendAnalysisReport(
            String.join(
                StringUtils.EMPTY,
                "# Date: ", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "<br>&#10;# Tested on: ", SystemUtils.OS_NAME, " (", SystemUtils.OS_VERSION, ")",
                "<br>&#10;# Tool: ", StringUtil.APP_NAME, " v", this.injectionModel.getPropertiesUtil().getVersionJsql(),
                " (<a href=", urlGitHub, ">", urlGitHub, "</a>)",
                "<br>&#10;# Database: ", vendorFound.toString(),
                "<br>&#10;<br>&#10;## Vulnerability summary</span>"
            ),
            true
        );

        var requestSetVendor = new Request();
        requestSetVendor.setMessage(Interaction.SET_VENDOR);
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser());
        msgHeader.put(Header.VENDOR, vendorFound);
        requestSetVendor.setParameters(msgHeader);
        this.injectionModel.sendToViews(requestSetVendor);
        
        return vendorFound;
    }

    public Vendor initVendor(Vendor vendor) {
        var vendorFixed = vendor;
        if (vendorFixed == null) {
            vendorFixed = this.injectionModel.getMediatorVendor().getMysql();
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                MediatorVendor.LOG_VENDOR,
                () -> I18nUtil.valueByKey("LOG_DATABASE_TYPE_NOT_FOUND"),
                () -> this.injectionModel.getMediatorVendor().getMysql()
            );
        } else {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
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

    public Vendor getPostgres() {
        return this.postgres;
    }

    public Vendor getMysql() {
        return this.mysql;
    }

    public Vendor getSqlite() {
        return this.sqlite;
    }

    public Vendor getSqlserver() {
        return this.sqlserver;
    }

    public Vendor getNeo4j() {
        return this.neo4j;
    }
    
    public Vendor getVendor() {
        return this.vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
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

    public List<Vendor> getVendorsForFingerprint() {
        return this.vendorsForFingerprint;
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

    public Vendor getFirebird() {
        return this.firebird;
    }

    public Vendor getMonetdb() {
        return this.monetdb;
    }

    public Vendor getMimer() {
        return this.mimer;
    }

    public Vendor getMckoi() {
        return this.mckoi;
    }

    public Vendor getInformix() {
        return this.informix;
    }

    public Vendor getSybase() {
        return this.sybase;
    }

    public Vendor getVertica() {
        return this.vertica;
    }

    public Vendor getExasol() {
        return this.exasol;
    }

    public Vendor getHana() {
        return this.hana;
    }
}

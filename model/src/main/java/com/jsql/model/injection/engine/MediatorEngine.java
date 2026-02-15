package com.jsql.model.injection.engine;

import com.jsql.model.InjectionModel;
import com.jsql.view.subscriber.Seal;
import com.jsql.model.injection.engine.model.Engine;
import com.jsql.model.injection.engine.model.EngineYaml;
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
import java.util.List;

public class MediatorEngine {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String LOG_ENGINE = "{} [{}]";

    /**
     * Database engine currently used.
     * It can be switched to another engine by automatic detection or manual selection.
     */
    private Engine engine;

    /**
     * Database engine selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected engine.
     */
    private Engine engineByUser;

    // TODO Replace with enum
    private final Engine auto;
    private final Engine access;
    private final Engine altibase;
    private final Engine clickhouse;
    private final Engine cubrid;
    private final Engine db2;
    private final Engine derby;
    private final Engine exasol;
    private final Engine firebird;
    private final Engine h2;
    private final Engine hana;
    private final Engine hsqldb;
    private final Engine informix;
    private final Engine mckoi;
    private final Engine mimer;
    private final Engine monetdb;
    private final Engine mysql;
    private final Engine neo4j;
    private final Engine oracle;
    private final Engine postgres;
    private final Engine presto;
    private final Engine sqlite;
    private final Engine sqlserver;
    private final Engine sybase;
    private final Engine vertica;
    private final Engine virtuoso;

    private final List<Engine> engines;
    private final List<Engine> enginesForFingerprint;

    private final InjectionModel injectionModel;

    public MediatorEngine(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        Engine ctreeace = new Engine(new EngineYaml("ctreeace.yml", injectionModel));
        Engine frontbase = new Engine(new EngineYaml("frontbase.yml", injectionModel));
        Engine ingres = new Engine(new EngineYaml("ingres.yml", injectionModel));
        Engine iris = new Engine(new EngineYaml("iris.yml", injectionModel));
        Engine maxdb = new Engine(new EngineYaml("maxdb.yml", injectionModel));
        Engine netezza = new Engine(new EngineYaml("netezza.yml", injectionModel));
        Engine nuodb = new Engine(new EngineYaml("nuodb.yml", injectionModel));
        Engine teradata = new Engine(new EngineYaml("teradata.yml", injectionModel));

        this.auto = new Engine();
        this.access = new Engine(new EngineYaml("access.yml", injectionModel));
        this.altibase = new Engine(new EngineYaml("altibase.yml", injectionModel));
        this.cubrid = new Engine(new EngineYaml("cubrid.yml", injectionModel));
        this.clickhouse = new Engine(new EngineYaml("clickhouse.yml", injectionModel));
        this.db2 = new Engine(new EngineYaml("db2.yml", injectionModel));
        this.derby = new Engine(new EngineYaml("derby.yml", injectionModel));
        this.exasol = new Engine(new EngineYaml("exasol.yml", injectionModel));
        this.firebird = new Engine(new EngineYaml("firebird.yml", injectionModel));
        this.h2 = new Engine(new EngineYaml("h2.yml", injectionModel));
        this.hana = new Engine(new EngineYaml("hana.yml", injectionModel));
        this.hsqldb = new Engine(new EngineYaml("hsqldb.yml", injectionModel));
        this.informix = new Engine(new EngineYaml("informix.yml", injectionModel));
        this.mckoi = new Engine(new EngineYaml("mckoi.yml", injectionModel));
        this.mimer = new Engine(new EngineYaml("mimersql.yml", injectionModel));
        this.monetdb = new Engine(new EngineYaml("monetdb.yml", injectionModel));
        this.mysql = new Engine(new EngineYaml("mysql.yml", injectionModel));
        this.neo4j = new Engine(new EngineYaml("neo4j.yml", injectionModel));
        this.oracle = new Engine(new EngineYaml("oracle.yml", injectionModel));
        this.postgres = new Engine(new EngineYaml("postgres.yml", injectionModel));
        this.presto = new Engine(new EngineYaml("presto.yml", injectionModel));
        this.sqlite = new Engine(new EngineYaml("sqlite.yml", injectionModel)) {
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
        this.sqlserver = new Engine(new EngineYaml("sqlserver.yml", injectionModel));
        this.sybase = new Engine(new EngineYaml("sybase.yml", injectionModel));
        this.vertica = new Engine(new EngineYaml("vertica.yml", injectionModel));
        this.virtuoso = new Engine(new EngineYaml("virtuoso.yml", injectionModel));

        this.engines = Arrays.asList(
            this.auto, access, this.altibase, this.clickhouse, ctreeace, this.cubrid, this.db2, this.derby, this.exasol, this.firebird,
            frontbase, this.h2, this.hana, this.hsqldb, this.informix, ingres, iris, maxdb, this.mckoi, this.mimer, this.monetdb,
            this.mysql, this.neo4j, netezza, nuodb, this.oracle, this.postgres, this.presto, this.sqlite, this.sqlserver, this.sybase,
            teradata, this.vertica, this.virtuoso
        );
        this.enginesForFingerprint = Arrays.asList(  // Add sortIndex
            this.mysql, this.postgres, this.sqlite, this.h2, this.hsqldb, this.oracle, this.sqlserver, access, this.altibase, ctreeace,
            this.cubrid, this.db2, this.derby, this.exasol, this.firebird, frontbase, this.hana, this.informix, ingres, iris, maxdb, this.mckoi,
            this.mimer, this.monetdb, this.neo4j, netezza, nuodb, this.presto, this.sybase, teradata, this.vertica, this.virtuoso, this.clickhouse
        );

        this.engine = this.mysql;
        this.engineByUser = this.auto;
    }
    
    public boolean isSqlite() {
        return this.getEngine() == this.getSqlite();
    }
    
    public Engine fingerprintEngine() {
        Engine engineFound = null;
        if (this.injectionModel.getMediatorEngine().getEngineByUser() != this.injectionModel.getMediatorEngine().getAuto()) {
            engineFound = this.injectionModel.getMediatorEngine().getEngineByUser();
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                MediatorEngine.LOG_ENGINE,
                () -> I18nUtil.valueByKey("LOG_DATABASE_TYPE_FORCED_BY_USER"),
                () -> this.injectionModel.getMediatorEngine().getEngineByUser()
            );
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database (step 1)...");
            var insertionCharacter = URLEncoder.encode("'\"#-)'\"*", StandardCharsets.UTF_8);
            String pageSource = this.injectionModel.injectWithoutIndex(insertionCharacter, "test#engine");
                
            var mediatorEngine = this.injectionModel.getMediatorEngine();
            Engine[] enginesWithoutAuto = mediatorEngine.getEngines()
                .stream()
                .filter(v -> v != mediatorEngine.getAuto())
                .toArray(Engine[]::new);
            
            // Test each engine
            for (Engine engineTest : enginesWithoutAuto) {
                if (pageSource.matches(engineTest.instance().fingerprintErrorsAsRegex())) {
                    engineFound = engineTest;
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_SUCCESS,
                        "Found [{}] using raw fingerprinting",
                        () -> engineTest
                    );
                    break;
                }
            }
            if (engineFound == null) {
                engineFound = this.injectionModel.getMediatorEngine().getMysql();
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    MediatorEngine.LOG_ENGINE,
                    () -> I18nUtil.valueByKey("LOG_DATABASE_TYPE_NOT_FOUND"),
                    () -> this.injectionModel.getMediatorEngine().getMysql()
                );
            }
        }

        var urlGitHub = this.injectionModel.getMediatorUtils().propertiesUtil().getProperty("github.url");
        this.injectionModel.appendAnalysisReport(
            String.join(
                StringUtils.EMPTY,
                "# Date: ", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "<br>&#10;# Tested on: ", SystemUtils.OS_NAME, " (", SystemUtils.OS_VERSION, ")",
                "<br>&#10;# Tool: ", StringUtil.APP_NAME, " v", this.injectionModel.getPropertiesUtil().getVersionJsql(),
                " (<a href=", urlGitHub, ">", urlGitHub, "</a>)",
                "<br>&#10;# Database: ", engineFound.toString(),
                "<br>&#10;<br>&#10;## Vulnerability summary</span>"
            ),
            true
        );

        this.injectionModel.sendToViews(new Seal.ActivateEngine(engineFound));
        return engineFound;
    }
    
    
    // Getter and setter

    public Engine getEngine() {
        return this.engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngineByUser() {
        return this.engineByUser;
    }

    public void setEngineByUser(Engine engineByUser) {
        this.engineByUser = engineByUser;
    }

    public List<Engine> getEngines() {
        return this.engines;
    }

    public List<Engine> getEnginesForFingerprint() {
        return this.enginesForFingerprint;
    }


    // engines

    public Engine getAuto() {
        return this.auto;
    }

    public Engine getAccess() {
        return this.access;
    }

    public Engine getAltibase() {
        return this.altibase;
    }

    public Engine getClickhouse() {
        return this.clickhouse;
    }

    public Engine getCubrid() {
        return this.cubrid;
    }

    public Engine getDb2() {
        return this.db2;
    }

    public Engine getDerby() {
        return this.derby;
    }

    public Engine getExasol() {
        return this.exasol;
    }

    public Engine getFirebird() {
        return this.firebird;
    }

    public Engine getH2() {
        return this.h2;
    }

    public Engine getHana() {
        return this.hana;
    }

    public Engine getHsqldb() {
        return this.hsqldb;
    }

    public Engine getInformix() {
        return this.informix;
    }

    public Engine getMckoi() {
        return this.mckoi;
    }

    public Engine getMimer() {
        return this.mimer;
    }

    public Engine getMonetdb() {
        return this.monetdb;
    }

    public Engine getMysql() {
        return this.mysql;
    }

    public Engine getNeo4j() {
        return this.neo4j;
    }

    public Engine getOracle() {
        return this.oracle;
    }

    public Engine getPostgres() {
        return this.postgres;
    }

    public Engine getPresto() {
        return this.presto;
    }

    public Engine getSqlite() {
        return this.sqlite;
    }

    public Engine getSqlserver() {
        return this.sqlserver;
    }

    public Engine getSybase() {
        return this.sybase;
    }

    public Engine getVertica() {
        return this.vertica;
    }

    public Engine getVirtuoso() {
        return this.virtuoso;
    }
}

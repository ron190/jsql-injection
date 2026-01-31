package spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.JdbcSettings;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.SpringApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
public class JdbcRestController {

    private static final String TEMPLATE = "Hello, s!";
    private static final Logger LOGGER = LogManager.getRootLogger();
    private final Driver driver = GraphDatabase.driver("bolt://jsql-neo4j:7687", AuthTokens.basic("neo4j", "test"));
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/clickhouse")
    public Greeting greetingClickhouse(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("clickhouse").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("clickhouse").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("clickhouse").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select schema_name from information_schema.schemata where '1' = '" + inject + "'"
        );
    }

    @RequestMapping("/exasol")
    public Greeting greetingExasol(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("exasol").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("exasol").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("exasol").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select COLUMN_SCHEMA from EXA_SYS_COLUMNS where '1' = '" + inject + "'"
        );
    }

    @RequestMapping("/hana")
    public Greeting greetingHana(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("hana").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("hana").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("hana").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select schema_name from sys.schemas where '1' = '" + inject + "'"
        );
    }

    @RequestMapping("/mckoi")  // no dialect
    public Greeting greetingMckoi(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("mckoi").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("mckoi").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("mckoi").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select name from SYS_INFO.sUSRSchemaInfo where 1 = "+ inject
        );
    }

    // Requires low traffic to avoid error: Operation not allowed. Configured number of users exceeded
    @RequestMapping("/mimer")  // MimerSQLDialect not working: SQLGrammarException Could not prepare statement, Sequence does not exist, or no privilege
    public Greeting greetingMimerSQL(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        Class.forName("com.mimer.jdbc.Driver");  // required
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("mimer").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("mimer").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("mimer").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select table_name from information_schema.tables where '1' = '"+ inject +"'"
        );
    }

    @RequestMapping("/monetdb")
    public Greeting greetingMonetDB(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("monetdb").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("monetdb").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("monetdb").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select name from schemas where '1' = '"+ inject +"'"
        );
    }

    @RequestMapping("/neo4j")
    public Greeting greetingNeo4j(@RequestParam(value="name", defaultValue="World") String name) {
        Greeting greeting;

        try (org.neo4j.driver.Session session = this.driver.session()) {
            Result result = session.run("MATCH (n:Person) where 1="+ name +" RETURN n.name, n.from, n.title, n.hobby");

            String collected = result.stream().map(driverRecord -> driverRecord.keys().stream()
                .map(key -> key + "=" + driverRecord.get(key))
                .collect(Collectors.joining(", ", "{", "}"))
            ).collect(Collectors.joining());

            greeting = new Greeting(JdbcRestController.TEMPLATE + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(collected)));
        } catch (Exception e) {
            greeting = this.initErrorMessage(e);
        }

        return greeting;
    }

    @RequestMapping("/presto")
    public Greeting greetingPresto(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("presto").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("presto").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("presto").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'"
        );
    }

    @RequestMapping("/vertica")
    public Greeting greetingVertica(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("vertica").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("vertica").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("vertica").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select table_schema from v_catalog.system_tables where 1 = "+ inject
        );
    }

    @RequestMapping("/virtuoso")
    public Greeting greetingVirtuoso(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        Class.forName("virtuoso.jdbc3.Driver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            SpringApp.get("virtuoso").getProperty(JdbcSettings.JAKARTA_JDBC_URL),
            SpringApp.get("virtuoso").getProperty(JdbcSettings.JAKARTA_JDBC_USER),
            SpringApp.get("virtuoso").getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD),
            "select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'"
        );
    }


    // Other
    
    @RequestMapping("/ctreeace")
    public Greeting greetingCTreeAce(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // no container
        // jdbc ctreeACE:ctreeace-connector-java:0.0 scope:system systemPath:${project.basedir}/src/test/resources/jdbc/ctreeJDBC.jar
        // c-treeACE-Express.windows.64bit.v11.5.1.64705.190310.ACE.msi
        // jdbc:ctree://localhost:6597/ctreeSQL
        // ADMIN ADMIN
        Class.forName("ctree.jdbc.ctreeDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:ctree://localhost:6597/ctreeSQL",
            "ADMIN",
            "ADMIN",
            "select tbl from systables where '1' = '"+ inject +"'"
        );
    }
    
    @RequestMapping("/ignite")
    public Greeting greetingIgnite(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // Fail: tables and system views cannot be used in the same query.
        // docker run -d -p 10800:10800 apacheignite/ignite
        // jdbc:ignite:thin://127.0.0.1
        // ignite ignite
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:ignite:thin://127.0.0.1",
            "ignite",
            "ignite",
            "select 'name' from PUBLIC.STUDENT where '1' = '"+ inject +"'"
        );
    }
    
    @RequestMapping("/frontbase")
    public Greeting greetingFrontbase(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // container issue
        // frontbase:frontbase-connector-java:2.5.9 scope:system systemPath:${project.basedir}/src/test/resources/jdbc/frontbasejdbc.jar
        // FrontBase-8.2.18-WinNT.zip
        // sql92.exe
        // create database firstdb
        // connect to firstdb user _system
        //  Auto committing is on: SET COMMIT TRUE
        //      create user test
        //      commit
        //  Service FBExec
        //  Service FrontBase firstdb
        //  jdbc:FrontBase://127.0.0.1/firstdb
        //  _system
        Class.forName("com.frontbase.jdbc.FBJDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:FrontBase://127.0.0.1/firstdb",
            "_system",
            StringUtils.EMPTY,
            "select \"SCHEMA_NAME\" from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'"
        );
    }
    
    @RequestMapping("/iris")
    public Greeting greetingIris(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // [FATAL] Unsupported CPU.  Please see InterSystems documentation for information on supported CPUs.
        // jdnc intersystems-iris:intersystems-iris-connector-java:3.1.0 scope:system systemPath:${project.basedir}/src/test/resources/jdbc/intersystems-jdbc-3.1.0.jar
        // docker run --name my-iris -d --publish 1972:1972 --publish 52773:52773 intersystems/iris-community:2020.3.0.221.0
        // [ERROR] Error - cannot write to /home/irisowner/irissys.
        // http://127.0.0.1:52773/csp/sys/UtilHome.csp
        // usr pwd: _SYSTEM SYS
        // Change pwd
        // jdbc:IRIS://127.0.0.1:1972/USER
        // _SYSTEM Mw7SUqLPFbZWUu4
        Class.forName("com.intersystems.jdbc.IRISDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:IRIS://127.0.0.1:1972/USER",
            "_SYSTEM",
            "Mw7SUqLPFbZWUu4",
            "select SCHEMA_NAME from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'"
        );
    }

    @RequestMapping("/netezza")
    public Greeting greetingNetezza(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // no container
        // jdbc netezza:netezza:3.40 scope:system systemPath:${project.basedir}/src/test/resources/jdbc/nzjdbc-1.0.jar
        // NetezzaSoftwareEmulator_7.2.1.ova
        // jdbc:netezza://127.0.0.1:5480/SYSTEM
        // admin password
        // no docker, custom dialect
        Class.forName("org.netezza.Driver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:netezza://127.0.0.1:5480/SYSTEM",
            "admin",
            "password",
            "select schema_name from schemata where '1' = '"+ inject +"'"
        );
    }

    @RequestMapping("/postgres")  // local testing, not used
    public Greeting greetingPostgres(@RequestParam(value="name", defaultValue="World") String name) {
        // pg_ctl.exe start -D "E:\Dev\pgsql\data\"
        AtomicReference<Greeting> greeting = new AtomicReference<>();
        StringBuilder result = new StringBuilder();

        Arrays.stream(("SELECT table_schema FROM information_schema.tables where '1' = '"+ name +"'").split(";")).map(String::trim).forEach(query -> {
            try (
                Connection con = DriverManager.getConnection("jdbc:postgresql://jsql-postgresql:5432/", "postgres", "my-secret-pw");
                PreparedStatement pstmt = con.prepareStatement(query)
            ) {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    result.append(rs.getString(1));
                }
                greeting.set(new Greeting(JdbcRestController.TEMPLATE + StringEscapeUtils.unescapeJava(result.toString())));
            } catch (Exception e) {
                greeting.set(this.initErrorMessage(e));
            }
        });

        return greeting.get();
    }

    @RequestMapping("/mysql")  // local testing, not used
    public Greeting greetingMysql(@RequestParam(value="name", defaultValue="World") String name) {
        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();

        try (
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?allowMultiQueries=true", "test193746285", "~Aa1");
            PreparedStatement pstmt = con.prepareStatement("select TABLE_SCHEMA from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(JdbcRestController.TEMPLATE + result);
        } catch (Exception e) {
            greeting = this.initErrorMessage(e);
        }

        return greeting;
    }


    private Greeting getGreeting(String url, String user, String password, String sql) {
        Greeting greeting;
        try (
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder result = new StringBuilder();
            while(resultSet.next()) {
                result.append(resultSet.getString(1));
            }
            greeting = new Greeting(JdbcRestController.TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));
        } catch (Exception e) {
            greeting = this.initErrorMessage(e);
        }
        return greeting;
    }

    private Greeting initErrorMessage(Exception e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        LOGGER.debug(stacktrace);
        return new Greeting(JdbcRestController.TEMPLATE + "#" + StringEscapeUtils.unescapeJava(stacktrace));
    }
}

// CockroachLegacyDialect: clone postgres
// GaussDBDialect: clone postgres
// MaxDBDialect: broken installation
// RDMSOS2200Dialect: no jdbc
// SingleStoreDialect: clone mysql
// TeradataDialect
// TiDBDialect: clone mysql
// TimesTenDialect: oracle login denied

// missing ingress: missing services
// jdbc:ingres://localhost:II7/demodb

// elasticsearch: no structure lookup
// elasticsearch-reset-password -u elastic
// POST https://localhost:9200/_license/start_trial?acknowledge=true
// browser => view cert => save pem => import as jks in kse
// jdbc:es://https://localhost:9200/?ssl.truststore.location=E:/tmp/certificate.jks

// localstack snowflake: wsl2
// docker run --rm -it -p 4566:4566 -e LOCALSTACK_AUTH_TOKEN=<token> localstack/snowflake
// export LOCALSTACK_AUTH_TOKEN=<token>
// IMAGE_NAME=localstack/snowflake /opt/code/localstack/.venv/bin/localstack start
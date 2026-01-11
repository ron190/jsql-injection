package spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // Integration tests on docker

    @RequestMapping("/exasol")
    public Greeting greetingExasol(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:exa:jsql-exasol/C48C3E80ECB8139DCEB043DA179068C640D6F029C7D85A20A837F24840068CB4:8563",
            "sys",
            "exasol",
            "select COLUMN_SCHEMA from EXA_SYS_COLUMNS where '1' = '" + inject + "'"
        );
    }

    @RequestMapping("/hana")
    public Greeting greetingHana(@RequestParam(value="name", defaultValue="World") String name) {
        // hdbuserstore LIST
        // hdbuserstore SET keytest localhost:30015@SYS SYSTEM Welcome1
        // SELECT DISTINCT(sql_port) FROM SYS.M_SERVICES WHERE SQL_PORT > 0
        // SELECT * FROM "SYS_DATABASES"."M_SERVICE_MEMORY";
        // ALTER SYSTEM ALTER CONFIGURATION ('global.ini', 'DATABASE', '') SET ('memorymanager', 'allocationlimit') = '8192' WITH RECONFIGURE;
        // SELECT SERVICE_NAME, PORT, SQL_PORT, (PORT + 2) HTTP_PORT FROM SYS.M_SERVICES
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:sap://jsql-hana:39017?encrypt=false&validateCertificate=false",
            "system",
            "1anaHEXH",
            "select schema_name from sys.schemas where '1' = '" + inject + "'"
        );
    }

    @RequestMapping("/mckoi")  // no dialect
    public Greeting greetingMckoi(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:mckoi://127.0.0.1",
            "user",
            "password",
            "select name from SYS_INFO.sUSRSchemaInfo where 1 = "+ inject
        );
    }

    @RequestMapping("/mimer")  // todo use MimerSQLDialect instead when docker container fixed
    public Greeting greetingMimerSQL(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        Class.forName("com.mimer.jdbc.Driver");  // required
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:mimer://jsql-mimer:1360/mimerdb",
            "SYSADM",
            "SYSADM",
            "select table_name from information_schema.tables where '1' = '"+ inject +"'"
        );
    }

    @RequestMapping("/monetdb")  // no dialect
    public Greeting greetingMonetDB(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:monetdb://jsql-monetdb:50000/db",
            "monetdb",
            "monetdb",
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

    @RequestMapping("/vertica")
    public Greeting greetingVertica(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:vertica://jsql-vertica:5433/",
            "dbadmin",
            "password",
            "select table_schema from v_catalog.system_tables where 1 = "+ inject
        );
    }


    // Other

    @RequestMapping("/altibase")
    public Greeting greetingAltibase(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // License required, Read-only file system
        // jdbc altibase:altibase-connector-java:7.1 scope:system systemPath:${project.basedir}/src/test/resources/jdbc/Altibase.jar
        // docker run -it altibase/altibase
        // jdbc:Altibase://localhost:20300/mydb
        // sys manager
        // isql -s 127.0.0.1 -u sys -p manager -sysdba
        // startup service
        // Connecting to the DB server...............................Startup Failure. Check
        // Your Environment.
        Class.forName("Altibase.jdbc.driver.AltibaseDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:Altibase://jsql-altibase:20300/mydb",
            "sys",
            "manager",
            "select db_name from SYSTEM_.SYS_DATABASE_ where '1' = '"+ inject +"'"
        );
    }
    
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
    
    @RequestMapping("/presto")
    public Greeting greetingPresto(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // jdbc com.facebook.presto:presto-jdbc:0.243.2
        // docker run -p 8080:8080 --name presto prestosql/presto
        // jdbc:presto://127.0.0.1:8078/system
        // test
        // 4Go
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        String inject = name.replace(":", "\\:");
        return this.getGreeting(
            "jdbc:presto://127.0.0.1:8078/system",
            "test",
            StringUtils.EMPTY,
            "select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'"
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
    public Greeting greetingPostgres(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        AtomicReference<Greeting> greeting = new AtomicReference<>();
        StringBuilder result = new StringBuilder();

        Arrays.stream(("SELECT table_schema FROM information_schema.tables where '1' = '"+ name +"'").split(";")).map(String::trim).forEach(query -> {
            System.out.println(query);
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
    public Greeting greetingMysql(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
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


    // AltibaseDialect: container issue
    // CockroachLegacyDialect
    // GaussDBDialect
    // MaxDBDialect: broken installation
    // RDMSOS2200Dialect
    // SingleStoreDialect
    // TeradataDialect
    // TiDBDialect
    // TimesTenDialect

    // missing ingress: missing services
    // jdbc:ingres://localhost:II7/demodb

    // missing maxdb: broken installation
    // no docker
    // jdbc:sapdb://127.0.0.1/MAXDB
    // DBADMIN TEST

    // nuodb
    // license
    // jdbc:com.nuodb://127.0.0.1/test
    // dba nuodb

    // teradata: config required
    // jdbc:teradata://127.0.0.1
    // dbc dbc
}
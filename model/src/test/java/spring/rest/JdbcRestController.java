package spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

@RestController
public class JdbcRestController {

    private static final String TEMPLATE = "Hello, s!";
    private static final Logger LOGGER = LogManager.getRootLogger();
    private final Driver driver = GraphDatabase.driver("bolt://jsql-neo4j:7687", AuthTokens.basic("neo4j", "test"));
    private final ObjectMapper objectMapper = new ObjectMapper();


    // Integration tests on docker

    @RequestMapping("/monetdb")
    public Greeting greetingMonetDB(@RequestParam(value="name", defaultValue="World") String name) {

        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();

        try (
            Connection con = DriverManager.getConnection("jdbc:monetdb://jsql-monetdb:50001/db", "monetdb", "monetdb");
            PreparedStatement pstmt = con.prepareStatement("select name from schemas where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }

    @RequestMapping("/neo4j")
    public Greeting greetingNeo4j(@RequestParam(value="name", defaultValue="World") String name) {

        Greeting greeting;

        try (org.neo4j.driver.Session session = this.driver.session()) {

            Result result = session.run("MATCH (n:Person) where 1="+ name +" RETURN n.name, n.from, n.title, n.hobby");

            String a = result.stream().map(driverRecord -> driverRecord
                    .keys()
                    .stream()
                    .map(key -> key + "=" + driverRecord.get(key))
                    .collect(Collectors.joining(", ", "{", "}"))
                ).collect(Collectors.joining());

            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(a)));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }

    @RequestMapping("/mimer")
    public Greeting greetingMimerSQL(@RequestParam(value="name", defaultValue="World") String name) {

        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();

        try (
            Connection con = DriverManager.getConnection("jdbc:mimer://jsql-mimer:1360/mimerdb", "SYSADM", "SYSADM");
            PreparedStatement pstmt = con.prepareStatement("select table_name from information_schema.tables where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }

    @RequestMapping("/mckoi")
    public Greeting greetingMckoi(@RequestParam(value="name", defaultValue="World") String name) {

        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();

        try (
            Connection con = DriverManager.getConnection("jdbc:mckoi://127.0.0.1", "user", "password");
            PreparedStatement pstmt = con.prepareStatement("select name from SYS_INFO.sUSRSchemaInfo where 1 = "+ inject)
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }

    @RequestMapping("/vertica")
    public Greeting greetingVertica(@RequestParam(value="name", defaultValue="World") String name) {

        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();

        try (
            Connection con = DriverManager.getConnection("jdbc:vertica://jsql-vertica:5433/", "dbadmin", "password");
            PreparedStatement pstmt = con.prepareStatement("select table_schema from v_catalog.tables where 1 = "+ inject)
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }


    // Other

    @RequestMapping("/altibase")
    public Greeting greetingAltibase(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // docker run -it altibase/altibase
        // jdbc:Altibase://localhost:20300/mydb
        // sys manager
        // isql -s 127.0.0.1 -u sys -p manager -sysdba
        // startup service
        // Connecting to the DB server...............................Startup Failure. Check
        // Your Environment.

        Class.forName("Altibase.jdbc.driver.AltibaseDriver");

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:Altibase://jsql-altibase:20300/mydb", "sys", "manager");
            PreparedStatement pstmt = con.prepareStatement("select db_name from SYSTEM_.SYS_DATABASE_ where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/ctreeace")
    public Greeting greetingCTreeAce(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // c-treeACE-Express.windows.64bit.v11.5.1.64705.190310.ACE.msi
        // jdbc:ctree://localhost:6597/ctreeSQL
        // ADMIN ADMIN
        Class.forName("ctree.jdbc.ctreeDriver");

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:ctree://localhost:6597/ctreeSQL", "ADMIN", "ADMIN");
            PreparedStatement pstmt = con.prepareStatement("select tbl from systables where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/exasol")
    public Greeting greetingExasol(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // docker run --name exasoldb -p 8563:8563 --detach --privileged --stop-timeout 120 exasol/docker-db
        // jdbc:exa:127.0.0.1:8563
        // sys exasol
        // 3.5Go
        Class.forName("com.exasol.jdbc.EXADriver");

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:exa:127.0.0.1:8563", "sys", "exasol");
            PreparedStatement pstmt = con.prepareStatement("select COLUMN_SCHEMA from EXA_SYS_COLUMNS where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/ignite")
    public Greeting greetingIgnite(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // Fail: Normal tables and system views cannot be used in the same query.
        // docker run -d -p 10800:10800 apacheignite/ignite
        // jdbc:ignite:thin://127.0.0.1
        // ignite ignite
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1", "ignite", "ignite");
            PreparedStatement pstmt = con.prepareStatement("select 'name' from PUBLIC.STUDENT where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/frontbase")
    public Greeting greetingFrontbase(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // FrontBase-8.2.18-WinNT.zip
        // sql92.exe
        // create database firstdb;
        // connect to firstdb user _system
        //  Auto committing is on: SET COMMIT TRUE
        //      create user test
        //      commit
        //  Service FBExec
        //  Service FrontBase firstdb
        //  jdbc:FrontBase://127.0.0.1/firstdb
        //  _system
        Class.forName("com.frontbase.jdbc.FBJDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:FrontBase://127.0.0.1/firstdb", "_system", "");
            PreparedStatement pstmt = con.prepareStatement("select \"SCHEMA_NAME\" from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/iris")
    public Greeting greetingIris(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // docker run --name my-iris -d --publish 1972:1972 --publish 52773:52773 intersystems/iris-community:2020.3.0.221.0
        // [ERROR] Error - cannot write to /home/irisowner/irissys.
        // http://127.0.0.1:52773/csp/sys/UtilHome.csp
        // usr pwd: _SYSTEM SYS
        // Change pwd
        // jdbc:IRIS://127.0.0.1:1972/USER
        // _SYSTEM Mw7SUqLPFbZWUu4
        Class.forName("com.intersystems.jdbc.IRISDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:IRIS://127.0.0.1:1972/USER", "_SYSTEM", "Mw7SUqLPFbZWUu4");
            PreparedStatement pstmt = con.prepareStatement("select SCHEMA_NAME from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/presto")
    public Greeting greetingPresto(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // docker run -p 8080:8080 --name presto prestosql/presto
        // jdbc:presto://127.0.0.1:8078/system
        // test
        // 4Go
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:presto://127.0.0.1:8078/system", "test", "");
            PreparedStatement pstmt = con.prepareStatement("select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }

        return greeting;
    }
    
    @RequestMapping("/netezza")
    public Greeting greetingNetezza(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // NetezzaSoftwareEmulator_7.2.1.ova
        // jdbc:netezza://127.0.0.1:5480/SYSTEM
        // admin password
        // no docker, custom dialect
        Class.forName("org.netezza.Driver");
        
        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:netezza://127.0.0.1:5480/SYSTEM", "admin", "password");
            PreparedStatement pstmt = con.prepareStatement("select schema_name from schemata where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/oracle")
    public Greeting greetingOracle(@RequestParam(value="name", defaultValue="World") String name) throws ClassNotFoundException {
        // 19.3.0/LINUX.X64_193000_db_home.zip
        // buildDockerImage.sh
        // docker run --name oracle19ee \
        // -p 1521:1521 -p 5500:5500 \
        // -e ORACLE_SID=ORCLCDB \
        // -e ORACLE_PDB=ORCLPDB1 \
        // -e ORACLE_PWD=Password1_One \
        // -e ORACLE_CHARACTERSET=AL32UTF8 \
        // oracle/database:19.3.0-ee
        // jdbc:oracle:thin:@localhost:11521:ORCLCDB
        // system Password1_One
        Class.forName("oracle.jdbc.OracleDriver");
        
        Greeting greeting;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:ORCLCDB", "system", "Password1_One");
            PreparedStatement pstmt = con.prepareStatement("select distinct owner from all_tables where '1' = '"+ inject +"'")
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            greeting = new Greeting(TEMPLATE + StringEscapeUtils.unescapeJava(result.toString()));

        } catch (Exception e) {
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    

    // missing hana: docker fails 13.9GB image
// jdbc:sap://127.0.0.1:30115
// XSA_ADMIN 1anaHEXH

    // missing ingress: missing services
// jdbc:ingres://localhost:II7/demodb

    // missing maxdb: broken installation
// no docker
// jdbc:sapdb://127.0.0.1/MAXDB
// DBADMIN TEST

    //nuodb
// license
// jdbc:com.nuodb://127.0.0.1/test
// dba nuodb

    // teradata
//# jdbc:teradata://127.0.0.1
//# dbc dbc

    // vertica
//# jdbc:vertica://127.0.0.1:5433/
//# dbadmin password

    private Greeting initializeErrorMessage(Exception e) {
        
        String stacktrace = ExceptionUtils.getStackTrace(e);
        LOGGER.debug(stacktrace);
        return new Greeting(TEMPLATE + "#" + StringEscapeUtils.unescapeJava(stacktrace));
    }
}
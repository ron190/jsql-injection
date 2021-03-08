package spring.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class JdbcRestController {

    private static final String template = "Hello, s!";
    private final AtomicLong counter = new AtomicLong();
    private static final Logger LOGGER = LogManager.getRootLogger();
    private Driver driver = GraphDatabase.driver("bolt://jsql-neo4j:7687", AuthTokens.basic("neo4j", "test"));
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @RequestMapping("/altibase")
    public Greeting greetingAltibase(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("Altibase.jdbc.driver.AltibaseDriver");
        // docker run -it altibase/altibase

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:Altibase://jsql-altibase:20300/mydb", "sys", "manager");
            PreparedStatement pstmt = con.prepareStatement("select db_name from SYSTEM_.SYS_DATABASE_ where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/ctreeace")
    public Greeting greetingCTreeAce(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        // c-treeACE-Express.windows.64bit.v11.5.1.64705.190310.ACE.msi
        Class.forName("ctree.jdbc.ctreeDriver");

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:ctree://localhost:6597/ctreeSQL", "ADMIN", "ADMIN");
            PreparedStatement pstmt = con.prepareStatement("select tbl from systables where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/exasol")
    public Greeting greetingExasol(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.exasol.jdbc.EXADriver");
        // docker run --name exasoldb -p 8563:8563 --detach --privileged --stop-timeout 120  exasol/docker-db
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:exa:127.0.0.1:8563", "sys", "exasol");
            PreparedStatement pstmt = con.prepareStatement("select COLUMN_SCHEMA from EXA_SYS_COLUMNS where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/ignite")
    public Greeting greetingIgnite(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
        // docker run -d -p 10800:10800 apacheignite/ignite
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1", "ignite", "ignite");
            PreparedStatement pstmt = con.prepareStatement("select 'name' from PUBLIC.STUDENT where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/frontbase")
    public Greeting greetingFrontbase(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        /* FrontBase-8.2.18-WinNT.zip
         * sql92.exe
         * sql92#1> create database firstdb;
            sql92#2>    connect to firstdb user _system;
            Auto committing is on: SET COMMIT TRUE;
            firstdb@localhost#3>    create user test;
            firstdb@localhost#4>    commit;
            jdbc:FrontBase://127.0.0.1/firstdb
            _system
            Service FBExec
            Service FrontBase firstdb
         */
        Class.forName("com.frontbase.jdbc.FBJDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:FrontBase://127.0.0.1/firstdb", "_system", "");
            PreparedStatement pstmt = con.prepareStatement("select \"SCHEMA_NAME\" from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/iris")
    public Greeting greetingIris(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.intersystems.jdbc.IRISDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:IRIS://127.0.0.1:1972/USER", "_SYSTEM", "Mw7SUqLPFbZWUu4");
            PreparedStatement pstmt = con.prepareStatement("select SCHEMA_NAME from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/monetdb")
    public Greeting greetingMonetDB(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:monetdb://127.0.0.1:50001/db", "monetdb", "monetdb");
            PreparedStatement pstmt = con.prepareStatement("select name from schemas where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/mimersql")
    public Greeting greetingMimerSQL(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.mimer.jdbc.Driver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:mimer://127.0.0.1:1360/mimerdb", "SYSADM", "SYSADM");
            PreparedStatement pstmt = con.prepareStatement("select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/presto")
    public Greeting greetingPresto(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:presto://127.0.0.1:8078/system", "test", "");
            PreparedStatement pstmt = con.prepareStatement("select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/firebird")
    public Greeting greetingFirebird(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        // Service Firebird Server - DefaultInstance
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:firebirdsql://127.0.0.1:3050/E:/Dev/Firebird/Firebird-2.5.9.27139-0_x64/examples/empbuild/EMPLOYEE.FDB", "sysdba", "masterkey");
            PreparedStatement pstmt = con.prepareStatement("select rdb$get_context('SYSTEM', 'DB_NAME') from rdb$database where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
            );
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/netezza")
    public Greeting greetingNetezza(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.netezza.Driver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:netezza://127.0.0.1:5480/SYSTEM", "admin", "password");
            PreparedStatement pstmt = con.prepareStatement("select schema_name from schemata where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
            );
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/oracle")
    public Greeting greetingOracle(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.OracleDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:ORCLCDB", "system", "Password1_One");
            PreparedStatement pstmt = con.prepareStatement("select distinct owner from all_tables where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
            );
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/neo4j")
    public Greeting greetingNeo4j(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        
        try (org.neo4j.driver.Session session = this.driver.session()) {
            Result result = session.run("MATCH (n:Person) where 1="+ name +" RETURN n.name, n.from, n.title, n.hobby");
            
            String a = result.stream().map(record ->
            
                record
                .keys()
                .stream()
                .map(key -> key + "=" + record.get(key))
                .collect(Collectors.joining(", ", "{", "}"))
                
            ).collect(Collectors.joining());
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(a))
            );
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    // TODO
    // missing derby
    // missing cockroachdb: docker fails
    // missing hana: docker fails 13.9GB image
    // missing informix: services but no connection
    // missing ingress: missing services
    // missing maxdb: broken installation
    // mckoi: no error on wrong order column
    // netezza

    private Greeting initializeErrorMessage(Exception e) {
        
        String stacktrace = ExceptionUtils.getStackTrace(e);
        
        LOGGER.debug(stacktrace);
        
        Greeting greeting = new Greeting(
            0,
            template+"#"
            + StringEscapeUtils.unescapeJava(stacktrace)
        );
        
        return greeting;
    }
}
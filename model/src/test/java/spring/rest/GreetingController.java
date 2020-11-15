package spring.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class GreetingController {

    private static final String template = "Hello, s!";
    private final AtomicLong counter = new AtomicLong();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = Logger.getRootLogger();
    private Driver driver = GraphDatabase.driver("bolt://jsql-neo4j:7687", AuthTokens.basic("neo4j", "test"));
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Bean
    public StrictHttpFirewall httpFirewall() {
        
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        
        List<String> httpMethods =
            Stream
            .concat(
                Stream.of("CUSTOM-JSQL"),
                Arrays.asList(RequestMethod.values()).stream().map(requestMethod -> requestMethod.name())
            )
            .collect(Collectors.toList());
        
        firewall.setAllowedHttpMethods(httpMethods);
        
        return firewall;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/greeting-csrf",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting greetingCsrf(HttpServletRequest request) throws IOException {
        
        Greeting greeting = null;
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            // Inside try because test connection do not send param
            String inject = request.getParameterMap().get("name")[0];
            inject = inject.replace(":", "\\:");
            
            Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '"+ inject +"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            // Required by multiple columns
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/greeting-post",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting greetingPost(HttpServletRequest request) throws IOException {
        
        Greeting greeting = null;
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            // Inside try because test connection do not send param
            String inject = request.getParameterMap().get("name")[0];
            inject = inject.replace(":", "\\:");
            
            Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '"+ inject +"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            // Required by multiple columns
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-cookie")
    public Greeting greetingCookie(HttpServletRequest request, @CookieValue("name") String name) throws IOException {

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6 from Student where '1' = '"+inject+"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            // Required by multiple columns
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-header")
    public Greeting greetingHeader(@RequestHeader Map<String, String> name) throws IOException {
        
        Greeting greeting = null;
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            String inject = name.get("name");
            
            if (StringUtils.isNotEmpty(inject)) {
                
                inject = inject.replace(":", "\\:");
    
                Query query = session.createNativeQuery("select 1,2,First_Name from Student where '1' = '"+ inject +"'");
                
                List<Object[]> results = query.getResultList();
                
                greeting = new Greeting(
                    this.counter.getAndIncrement(),
                    String.format(template, inject)
                    + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                );
            }
            
        } catch (Exception e) {
            
            // Required by multiple columns
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-json")
    public Greeting greetingJson(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replaceAll("\\\\:", ":");

        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            inject = new JSONObject(inject).getJSONObject("b").getJSONArray("b").getJSONObject(3).getJSONObject("a").getString("a");
            inject = inject.replaceAll(":", "\\\\:");
            inject = inject.replace(":", "\\:");
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-integer-insertion-char")
    public Greeting greetingIntegerInsertionChar(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where 1 = "+ inject);
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-multiple-index")
    public Greeting greetingMultipleIndex(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6 from Student where 1 = "+ inject);
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            // Required by multiple columns
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/basic/greeting")
    public Greeting greetingBasicAuth(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select 1,2,3,4,5,6,7,8,9,First_Name,10,11 from Student where 999 = "+ inject);
            
            query.getResultList();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/digest/greeting")
    public Greeting greetingDigestAuth(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select 1,2,3,4,5,6,7,8,9,First_Name,10,11 from Student where 999 = "+ inject);
            
            query.getResultList();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-insertion-char")
    public Greeting greetingInsertionChar(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where ((\"1\" = \""+ inject +"\"))");
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name, @RequestHeader Map<String, String> headers) throws IOException {
        
//        System.out.println(headers);
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
//    @SuppressWarnings("unchecked")
//    @RequestMapping("/greeting2")
//    public Greeting greeting2(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
//
//        Greeting greeting = null;
//        String inject = name.replace(":", "\\:");
//
//        try (Session session = this.sessionFactory.getCurrentSession()) {
//
////            Query query = session.createNativeQuery("select 1 where '1'='1'; declare @test nchar(100); SELECT @test = @@version; EXEC @test; --");
////            Query query = session.createNativeQuery("declare @test nchar(100); SELECT @test = @@version; EXEC @test; --");
//            Query query = session.createNativeQuery("select 1 where 1=@@version; declare @test nchar(100); SELECT @test = @@version; EXEC @test; --");
//
//            List<Object[]> results = query.getResultList();
//
//            greeting = new Greeting(
//                this.counter.getAndIncrement(),
//                String.format(template, inject)
//                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
//            );
//
//        } catch (Exception e) {
//
//            greeting = this.initializeErrorMessage(e);
//        }
//
//        return greeting;
//    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-custom")
    public Greeting greetingCustom(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        
        // TODO 1x GET and 12x POST instead of CUSTOM-JSQL
        if (!"CUSTOM-JSQL".equals(request.getMethod())) {
            
            return greeting;
        }
        
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/greeting/{name}/suffix")
    public Greeting greetingPathParam(@PathVariable("name") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
    @RequestMapping("/greeting-neo4j")
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
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
    @RequestMapping("/greeting-altibase")
    public Greeting greetingAltibase(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("Altibase.jdbc.driver.AltibaseDriver");
        // docker run -it altibase/altibase

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
            }
        
        Greeting greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-ctreeace")
    public Greeting greetingCTreeAce(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("ctree.jdbc.ctreeDriver");

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
            }
        
        Greeting greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-exasol")
    public Greeting greetingExasol(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.exasol.jdbc.EXADriver");
        // docker run --name exasoldb -p 8563:8563 --detach --privileged --stop-timeout 120  exasol/docker-db
        
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
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-ignite")
    public Greeting greetingIgnite(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
        // docker run -d -p 10800:10800 apacheignite/ignite
        
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
        }
        
        Greeting greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-frontbase")
    public Greeting greetingFrontbase(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        /*
         * sql92#1> create database firstdb;
sql92#2>    connect to firstdb user _system;
Auto committing is on: SET COMMIT TRUE;
firstdb@localhost#3>    create user test;
firstdb@localhost#4>    commit;
jdbc:FrontBase://127.0.0.1/firstdb
_system
         */
        Class.forName("com.frontbase.jdbc.FBJDriver");
        
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
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-iris")
    public Greeting greetingIris(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.intersystems.jdbc.IRISDriver");
        
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
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-monetdb")
    public Greeting greetingMonetDB(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
        
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
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-mimersql")
    public Greeting greetingMimerSQL(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.mimer.jdbc.Driver");
        
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
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-presto")
    public Greeting greetingPresto(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        
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
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-firebird")
    public Greeting greetingFirebird(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
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

    @RequestMapping("/greeting-error")
    public Greeting greetingError(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            query.getResultList();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-blind")
    public Greeting greetingBlind(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
        
            List<Object[]> results = query.getResultList();
            
            if (results.isEmpty()) {
                
                greeting = new Greeting(
                    this.counter.getAndIncrement(),
                    String.format(template+"#", inject)
                    + StringEscapeUtils.unescapeJava("PREFIX It's true SUFFIX")
                );
                
            } else {
                
                greeting = new Greeting(
                    this.counter.getAndIncrement(),
                    String.format(template+"#", inject)
                    + StringEscapeUtils.unescapeJava("PREFIX It's false SUFFIX")
                );
            }
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }

    @RequestMapping("/greeting-time")
    public Greeting greetingTime(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            query.getResultList();
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }

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
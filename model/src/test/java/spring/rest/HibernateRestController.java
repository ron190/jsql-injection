package spring.rest;

import java.io.IOException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
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
public class HibernateRestController {

    private static final String template = "Hello, s!";
    private final AtomicLong counter = new AtomicLong();
    private static final Logger LOGGER = LogManager.getRootLogger();
    private ObjectMapper objectMapper = new ObjectMapper();
    
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
        firewall.setUnsafeAllowAnyHttpMethod(true);
        
        return firewall;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/normal")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name, @RequestHeader Map<String, String> headers) throws IOException {
        
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

    @SuppressWarnings("unchecked")
    @RequestMapping("/blind")
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

    @RequestMapping("/time")
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

    @RequestMapping("/errors")
    public Greeting greetingError(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            // Do not display anything, error message is mandatory
            query.getResultList();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/inside")
    public Greeting greetingInside(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select '"+ inject +"'");
            
            // Do not display anything, error message is mandatory
            query.getResultList();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/delete")
    public Greeting greetingDelete(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            session.beginTransaction();
            Query query = session.createNativeQuery("delete from Student where 'not_found' = '"+ inject +"'");
            
            // Do not display anything, error message is mandatory
            query.executeUpdate();
            session.getTransaction().commit();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/insert")
    public Greeting greetingInsert(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            session.beginTransaction();
            Query query = session.createNativeQuery("insert into Student select * from Student where 'not_found' = '"+ inject +"'");
            
            // Do not display anything, error message is mandatory
            query.executeUpdate();
            session.getTransaction().commit();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/update")
    public Greeting greetingUpdate(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            session.beginTransaction();
            Query query = session.createNativeQuery("update Student set roll_no='' where 'not_found' = '"+ inject +"'");
            
            // Do not display anything, error message is mandatory
            query.executeUpdate();
            session.getTransaction().commit();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    
    // Special

    @SuppressWarnings("unchecked")
    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/csrf",
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/post",
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/cookie")
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/header")
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/json")
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/integer-insertion-char")
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/multiple-index")
    public Greeting greetingMultipleIndex(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            // Postgres union int on ()::text fails: PSQLException: ERROR: UNION types integer and text cannot be matched
            Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6 from Student where 1 = "+ inject);
        
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
    
    @RequestMapping("/basic")
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
    
    @RequestMapping("/digest")
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
    @RequestMapping("/insertion-char")
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
    @RequestMapping("/custom")
    public Greeting greetingCustom(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        
        // TODO 1x GET and 12x POST instead of CUSTOM-JSQL
        if (!"CUSTOM-JSQL".equals(request.getMethod())) {
            
            return new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, "")
                + StringEscapeUtils.unescapeJava("Missing method CUSTOM-JSQL: ")
                + request.getMethod()
                + request.getParameter("name")
            );
        }
        
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                + request.getMethod()
                + request.getParameter("name")
            );
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/user-agent")
    public Greeting greetingUserAgent(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        
        // TODO 1x GET and 12x POST instead of CUSTOM-JSQL
        if (
            !Arrays
            .asList("CUSTOM-USER-AGENT1", "CUSTOM-USER-AGENT2")
            .contains(request.getHeader("User-Agent"))
        ) {
            
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
    @GetMapping("/path/{name}/suffix")
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
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/order-by")
    public Greeting greetingOrderBy(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student order by 1, "+ inject);
            
            query.getResultList();
            
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
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
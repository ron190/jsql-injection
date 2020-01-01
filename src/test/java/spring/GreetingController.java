package spring;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
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
    protected static final Logger LOGGER = Logger.getRootLogger();
    
    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/greeting-post", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public Greeting greetingPost(HttpServletRequest a) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        Greeting greeting = null;
        try {
            String name = a.getParameterMap().get("name")[0];

            name = name.replace(":", "\\:");
            Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
            
            List<Object[]> results = q.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-cookie")
    public Greeting greetingCookie(HttpServletRequest request, @CookieValue("name") String name) throws IOException {

        Session session = this.sessionFactory.getCurrentSession();
        Greeting greeting = null;
        try {
            
            name = name.replace(":", "\\:");
            Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
            
            List<Object[]> results = q.getResultList();
            
            greeting = new Greeting(
                    this.counter.incrementAndGet(),
                    String.format(template, name)
                    + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                    );
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-header")
    public Greeting greetingHeader(@RequestHeader Map<String, String> a) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        Greeting greeting = null;
        try {
            String name = a.get("name");
            
            name = name.replace(":", "\\:");
            Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
            
            List<Object[]> results = q.getResultList();
            
            greeting = new Greeting(
                    this.counter.incrementAndGet(),
                    String.format(template, name)
                    + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                    );
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-json")
    public Greeting greetingJson(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        Greeting greeting = null;
        try {
            name = name.replaceAll("\\\\:", ":");
            String j = new JSONObject(name).getJSONObject("b").getJSONArray("b").getJSONObject(2).getJSONObject("a").getString("a");
            j = j.replaceAll(":", "\\\\:");
            
            String a = j;
            
            a = a.replace(":", "\\:");
            Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+ a +"'");
            
            List<Object[]> results = q.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        name = name.replace(":", "\\:");
        Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            List<Object[]> results = q.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }

    @RequestMapping("/greeting-error")
    public Greeting greetingError(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        name = name.replace(":", "\\:");
        Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            q.getResultList();
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            
            LOGGER.debug(stacktrace);
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template+"#", name)
                + StringEscapeUtils.unescapeJava(stacktrace)
            );
        } finally {
            session.close();
        }
        
        return greeting;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-blind")
    public Greeting greetingBlind(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        name = name.replace(":", "\\:");
        Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            List<Object[]> l = q.getResultList();
            
            if (l.isEmpty()) {
                greeting = new Greeting(
                    this.counter.incrementAndGet(),
                    String.format(template+"#", name)
                    + StringEscapeUtils.unescapeJava("PREFIX It's true SUFFIX")
                );
            } else {
                greeting = new Greeting(
                    this.counter.incrementAndGet(),
                    String.format(template+"#", name)
                    + StringEscapeUtils.unescapeJava("PREFIX It's false SUFFIX")
                );
            }
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }

    @RequestMapping("/greeting-time")
    public Greeting greetingTime(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
        name = name.replace(":", "\\:");
        Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            q.getResultList();
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            session.close();
        }
        
        return greeting;
    }

}
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
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-cookie")
    public Greeting greetingCookie(HttpServletRequest request, @CookieValue("name") String name) throws IOException {

        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+inject+"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting-header")
    public Greeting greetingHeader(@RequestHeader Map<String, String> name) throws IOException {
        
        Greeting greeting = null;
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            String inject = name.get("name");
            inject = inject.replace(":", "\\:");

            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
            
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
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
                this.counter.incrementAndGet(),
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
                this.counter.incrementAndGet(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
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
                this.counter.incrementAndGet(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            String stacktrace = ExceptionUtils.getStackTrace(e);
            
            LOGGER.debug(stacktrace);
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template+"#", inject)
                + StringEscapeUtils.unescapeJava(stacktrace)
            );
        }
        
        return greeting;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        
        try (Session session = this.sessionFactory.getCurrentSession()) {
            
            Query query = session.createNativeQuery("select First_Name from Student where '1' = '"+ inject +"'");
        
            List<Object[]> results = query.getResultList();
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template, inject)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            // Hide useless SQL error messages
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
            
            String stacktrace = ExceptionUtils.getStackTrace(e);
            
            LOGGER.debug(stacktrace);
            
            greeting = new Greeting(
                this.counter.incrementAndGet(),
                String.format(template+"#", inject)
                + StringEscapeUtils.unescapeJava(stacktrace)
            );
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
                    this.counter.incrementAndGet(),
                    String.format(template+"#", inject)
                    + StringEscapeUtils.unescapeJava("PREFIX It's true SUFFIX")
                );
                
            } else {
                
                greeting = new Greeting(
                    this.counter.incrementAndGet(),
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
}
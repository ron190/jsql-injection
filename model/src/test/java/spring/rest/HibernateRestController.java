package spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Transactional
@RestController
public class HibernateRestController {

    private static final String template = "Hello, s!";
    private final AtomicLong counter = new AtomicLong();
    private static final Logger LOGGER = LogManager.getRootLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SessionFactory sessionFactory;
    
    @RequestMapping("/normal")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name, @RequestHeader Map<String, String> headers) {
        return getGreeting(name, "select First_Name from Student where '1' = '%s'", false, false, true);
    }

    @RequestMapping("/blind")
    public Greeting greetingBlind(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select First_Name from Student where '1' = '%s'", false, false, true, false, true);
    }

    @RequestMapping("/time")
    public Greeting greetingTime(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select First_Name from Student where '1' = '%s'", false, false, false);
    }

    private Greeting getGreeting(String name, String sqlQuery, boolean isError, boolean isUpdate, boolean isVisible) {
        return getGreeting(name, sqlQuery, isError, isUpdate, isVisible, false, false);
    }

    private Greeting getGreeting(String name, String sqlQuery, boolean isError, boolean isUpdate, boolean isVisible, boolean isOracle, boolean isBoolean) {

        String inject = isOracle ? name : name.replace(":", "\\:");

        try {
            Session session = this.sessionFactory.getCurrentSession();
            Query<Object[]> query = session.createNativeQuery(
                String.format(sqlQuery, inject)
            );
            if (isUpdate) {
                query.executeUpdate();
            } else {
                List<Object[]> results = query.getResultList();
                if (isVisible) {
                    return new Greeting(
                        this.counter.getAndIncrement(),
                        isBoolean
                        ? results.isEmpty() ? "true" : "false"
                        : template + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                    );
                }
            }
        } catch (Exception e) {
            // Hide useless SQL error messages
            if (isError) {
                return this.initializeErrorMessage(e);
            }
        }

        return null;
    }

    @RequestMapping("/errors")
    public Greeting greetingError(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select First_Name from Student where '1' = '%s'", true, false, false);
    }
    
    @RequestMapping("/inside")
    public Greeting greetingInside(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select  '%s'", true, false, false);
    }
    
    @RequestMapping("/delete")
    public Greeting greetingDelete(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "delete from Student where 'not_found' = '%s'", true, true, false);
    }
    
    @RequestMapping("/insert")
    public Greeting greetingInsert(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "insert into Student select * from Student where 'not_found' = '%s'", true, true, false);
    }
    
    @RequestMapping("/update")
    public Greeting greetingUpdate(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "update Student set roll_no = '' where 'not_found' = '%s'", true, true, false);
    }

    @RequestMapping("/order-by")
    public Greeting greetingOrderBy(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select First_Name from Student order by 1, '%s'", true, false, false);
    }
    
    // Special

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/csrf",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting greetingCsrf(@RequestParam(value="name", defaultValue="World") String name) {
//    public Greeting greetingCsrf(HttpServletRequest request) {
//        String inject = request.getParameterMap().get("name")[0];
        return getGreeting(name, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
//        return getGreeting(inject, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
    }

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/post",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting greetingPost(HttpServletRequest request) {
        String inject = request.getParameterMap().get("name")[0];
        return getGreeting(inject, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
    }

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/multipart",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public Greeting greetingPostMultipart(HttpServletRequest request) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String name = String.join("", multipartRequest.getParameterValues("name"));

        return getGreeting(name, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
    }
    
    @RequestMapping("/cookie")
    public Greeting greetingCookie(HttpServletRequest request, @CookieValue("name") String name) {
        return getGreeting(name, "select 1,2,3,4,First_Name,5,6 from Student where '1' = '%s'", true, false, true);
    }
    
    @RequestMapping("/header")
    public Greeting greetingHeader(@RequestHeader Map<String, String> name) {
        return getGreeting(name.getOrDefault("name", "World"), "select 1,2,First_Name from Student where '1' = '%s'", true, false, true);
    }
    
    @RequestMapping("/json")
    public Greeting greetingJson(@RequestParam(value="name", defaultValue="World") String name) {
        String inject = name.replaceAll("\\\\:", ":");
        try {
            new JSONObject(inject);
        } catch (Exception e) {
            return null;
        }
        inject = new JSONObject(inject).getJSONObject("b").getJSONArray("b").getJSONObject(3).getJSONObject("a").getString("a");
        inject = inject.replaceAll(":", "\\\\:");
        inject = inject.replace(":", "\\:");
        return getGreeting(inject, "select First_Name from Student where '1' = '%s'", true, false, true, true, false);
    }
    
    @RequestMapping("/integer-insertion-char")
    public Greeting greetingIntegerInsertionChar(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select First_Name from Student where 1 = %s", true, false, true);
    }
    
    @RequestMapping("/multiple-index")
    public Greeting greetingMultipleIndex(@RequestParam(value="name", defaultValue="World") String name) {
        // Postgres union int on ()::text fails: PSQLException: ERROR: UNION types integer and text cannot be matched
        return getGreeting(name, "select 1,2,3,4,First_Name,5,6 from Student where 1 = %s", true, false, true);
    }
    
    @RequestMapping("/basic")
    public Greeting greetingBasicAuth(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select 1,2,3,4,5,6,7,8,9,First_Name,10,11 from Student where 999 = %s", true, false, false);
    }
    
    @RequestMapping("/digest")
    public Greeting greetingDigestAuth(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select 1,2,3,4,5,6,7,8,9,First_Name,10,11 from Student where 999 = %s", true, false, false);
    }
    
    @RequestMapping("/insertion-char")
    public Greeting greetingInsertionChar(@RequestParam(value="name", defaultValue="World") String name) {
        return getGreeting(name, "select First_Name from Student where ((\"1\" = \"%s\"))", true, false, true);
    }
    
    @RequestMapping("/custom")
    public Greeting greetingCustom(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) {
        
        // TODO 1x GET and 12x POST instead of CUSTOM-JSQL
        if (!"CUSTOM-JSQL".equals(request.getMethod())) {
            
            return new Greeting(
                this.counter.getAndIncrement(),
                template
                + StringEscapeUtils.unescapeJava("Missing method CUSTOM-JSQL: ")
                + request.getMethod()
                + request.getParameter("name")
            );
        }

        return getGreeting(name, "select First_Name from Student where '1' = '%s'", true, false, true);
    }
    
    @RequestMapping("/user-agent")
    public Greeting greetingUserAgent(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) {
        
        Greeting greeting = null;
        
        // TODO 1x GET and 12x POST instead of CUSTOM-JSQL
        if (
            !Arrays
            .asList("CUSTOM-USER-AGENT1", "CUSTOM-USER-AGENT2")
            .contains(request.getHeader("User-Agent"))
        ) {
            return greeting;
        }

        return getGreeting(name, "select First_Name from Student where '1' = '%s'", true, false, true);
    }
    
    @GetMapping("/path/{name}/suffix")
    public Greeting greetingPathParam(@PathVariable("name") String name) {
        return getGreeting(name, "select First_Name from Student where '1' = '%s'", true, false, true);
    }

    private Greeting initializeErrorMessage(Exception e) {
        
        String stacktrace = ExceptionUtils.getStackTrace(e);
        LOGGER.debug(stacktrace);
        return new Greeting(
            0,
            template+"#"
            + StringEscapeUtils.unescapeJava(stacktrace)
        );
    }
}
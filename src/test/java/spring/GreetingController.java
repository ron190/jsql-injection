package spring;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
    
//    public static class Customer {
//        private int custId;
//        private String firstname;
//        private String lastname;
//        private int age;
//        
//        
//        
//        public Customer() {
//            // TODO Auto-generated constructor stub
//        }
//        public Customer(int custId, String firstname, String lastname, int age) {
//            this.custId = custId;
//            this.firstname = firstname;
//            this.lastname = lastname;
//            this.age = age;
//        }
//        public int getCustId() {
//            return custId;
//        }
//        public void setCustId(int custId) {
//            this.custId = custId;
//        }
//        public String getFirstname() {
//            return firstname;
//        }
//        public void setFirstname(String firstname) {
//            this.firstname = firstname;
//        }
//        public String getLastname() {
//            return lastname;
//        }
//        public void setLastname(String lastname) {
//            this.lastname = lastname;
//        }
//        public int getAge() {
//            return age;
//        }
//        public void setAge(int age) {
//            this.age = age;
//        }
//        @Override
//        public String toString() {
//            return "Customer [custId=" + custId + ", firstname=" + firstname + ", lastname=" + lastname + ", age=" + age
//                    + "]";
//        }
//        
//
//    }
    
    


//private void printRequest(HttpServletRequest httpRequest) {
//    System.out.println(" \n\n Headers");
//
//    Enumeration headerNames = httpRequest.getHeaderNames();
//    while(headerNames.hasMoreElements()) {
//        String headerName = (String)headerNames.nextElement();
//        System.out.println(headerName + " = " + httpRequest.getHeader(headerName));
//    }
//
//    System.out.println("\n\nParameters");
//
//    Enumeration params = httpRequest.getParameterNames();
//    while(params.hasMoreElements()){
//        String paramName = (String)params.nextElement();
//        System.out.println(paramName + " = " + httpRequest.getParameter(paramName));
//    }
//
//    System.out.println("\n\n Row data");
//    System.out.println(extractPostRequestBody(httpRequest));
//}
//
//static String extractPostRequestBody(HttpServletRequest request) {
//    if ("POST".equalsIgnoreCase(request.getMethod())) {
//        Scanner s = null;
//        try {
//            s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return s.hasNext() ? s.next() : "";
//    }
//    return "";
//}


    @PostMapping(path = "/greeting-request7", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Greeting greetingRequest7(HttpServletRequest a) throws IOException {
        a.getParameterMap().forEach((key, value) -> System.out.println(key + Arrays.asList(value).stream().collect(Collectors.joining())));
//        System.out.println(IOUtils.toString(a.getReader()));
        
//        printRequest(a);
      return null;
    }
    
//    @PostMapping(path = "/greeting-request6", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public Greeting greetingRequest6(@RequestBody Map<String, String> body) throws IOException {
//        body.forEach((key, value) -> System.out.println(key + value));
//        return null;
//    }
//    
//    @PostMapping(path = "/greeting-request5", consumes = MediaType.ALL_VALUE)
//    public Greeting greetingRequest4(@RequestParam Map<String, String> body) throws IOException {
//        body.forEach((key, value) -> System.out.println(key + value));
//        return null;
//    }
//    
//    
//    @PostMapping(path = "/greeting-request4", consumes = MediaType.ALL_VALUE)
//    public Greeting greetingRequest4(@RequestBody String body, Writer writer) throws IOException {
////        fruits.forEach((key, value) -> System.out.println(key + value.stream().collect(Collectors.joining())));
//        System.out.println(body);
//        ObjectMapper mapper = new ObjectMapper();
//        Map list = mapper.readValue(body, Map.class);
//        System.out.println(list);
//        return null;
//    }
//    
//    @PostMapping(path = "/greeting-request3", consumes = MediaType.ALL_VALUE)
//    public Greeting greetingRequest3(@RequestParam String fruits) throws IOException {
////        fruits.forEach((key, value) -> System.out.println(key + value.stream().collect(Collectors.joining())));
//        System.out.println(fruits);
//        return null;
//    }
//    
//    @PostMapping(path = "/greeting-request2", consumes = MediaType.ALL_VALUE)
//    public Greeting greetingRequest2(@RequestBody Map<String,List<String>> fruits) throws IOException {
//        fruits.forEach((key, value) -> System.out.println(key + value.stream().collect(Collectors.joining())));
////      System.out.println(content);
//      return null;
//    }
//    
//    @PostMapping(path = "/greeting-request", consumes = MediaType.ALL_VALUE)
//    public Greeting greetingRequest(@RequestBody Customer customer) throws IOException {
////        request.getParameterMap().forEach((key, value) -> System.out.println(key + Arrays.asList(value).stream().collect(Collectors.joining())));
////        Session session = this.sessionFactory.getCurrentSession();
////        Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
////        
//        System.out.println(customer);
////        System.out.println(content);
//        return null;
////        name2.getParameterMap().forEach((key, value) -> System.out.println(key + Arrays.asList(value).stream().collect(Collectors.joining())));
////        body.forEach((key, value) -> System.out.println(key + value));
//        
////        Greeting greeting = null;
////        try {
////            List<Object[]> results = q.getResultList();
////            
////            greeting = new Greeting(
////                this.counter.incrementAndGet(),
////                String.format(template, name)
////                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
////            );
////        } catch (Exception e) {
////            // Hide useless SQL error messages
////        } finally {
////            session.close();
////        }
////        
////        return greeting;
//    }
//    
//    @RequestMapping("/greeting-header")
//    public Greeting greetingCookie(@RequestHeader(value="name", defaultValue="World") String name) throws IOException {
//        
//        Session session = this.sessionFactory.getCurrentSession();
//        Query q = session.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
//        
//        Greeting greeting = null;
//        try {
//            List<Object[]> results = q.getResultList();
//            
//            greeting = new Greeting(
//                this.counter.incrementAndGet(),
//                String.format(template, name)
//                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
//            );
//        } catch (Exception e) {
//            // Hide useless SQL error messages
//        } finally {
//            session.close();
//        }
//        
//        return greeting;
//    }
    
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
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

    @RequestMapping("/greeting-blind")
    public Greeting greetingBlind(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        Session session = this.sessionFactory.getCurrentSession();
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
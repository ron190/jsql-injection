package spring.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/greeting-custom/*")
public class CustomMethodServlet extends HttpServlet {

    private static final String template = "Hello, s!";
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private SessionFactory sessionFactory;
    
    public CustomMethodServlet(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        if (request.getMethod().equals("CUSTOM-JSQL")) {
            
            Session session;
            try {
                session = this.sessionFactory.getCurrentSession();
                
            } catch (HibernateException e) {
                
                session = this.sessionFactory.openSession();
            }
            
            try {
                
                // Inside try because test connection do not send param
                String inject = request.getParameterMap().get("name")[0];
                inject = inject.replace(":", "\\:");
                
                Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '"+ inject +"'");
                
                List<Object[]> results = query.getResultList();
                
                out.println(String.format(template, inject)
                        + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results)));
                
            } catch (Exception e) {
                
                String stacktrace = ExceptionUtils.getStackTrace(e);
                
                LOGGER.debug(stacktrace);
                
                out.println(
                    template+"#"
                    + StringEscapeUtils.unescapeJava(stacktrace)
                );
            }
            
        } else {
            
            super.service(request, response);
        }
    }
}
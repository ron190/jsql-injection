package spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.method.CustomMethodSuiteIT;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.Query;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Transactional("hibernateTransactionManager")
@RestController
public class HibernateRestController {

    private static final String TEMPLATE = "Hello, s!";
    private static final Logger LOGGER = LogManager.getRootLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SessionFactory sessionFactory;

    private Greeting getResponse(String name, String sqlQuery, boolean isError, boolean isUpdate, boolean isVisible) {
        return this.getResponse(name, sqlQuery, isError, isUpdate, isVisible, false, false, false);
    }

    private Greeting getResponse(
        String name,
        String sqlQuery,
        boolean isError,
        boolean isUpdate,
        boolean isVisible,
        boolean isOracle,
        boolean isBoolean,
        boolean isStack
    ) {
        if (name == null) {  // Empty when scanning
            return null;
        }

        String inject = isOracle ? name : name.replace(":", "\\:");

        try {
            Session session = this.sessionFactory.getCurrentSession();
            Query<Object> query = session.createNativeQuery(String.format(sqlQuery, inject), Object.class);
            if (isUpdate) {
                var transaction = session.beginTransaction();
                query.executeUpdate();
                transaction.commit();
            } else {
                if (isStack) {
                    try (Connection connection = ((SessionImpl) session).getJdbcConnectionAccess().obtainConnection()) {
                        Statement stmt = connection.createStatement();
                        boolean hasMoreResultSets = stmt.execute(String.format(sqlQuery, inject));
                        StringBuilder results = new StringBuilder();
                        while (hasMoreResultSets) {
                            ResultSet rs = stmt.getResultSet();
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();
                            while (rs.next()) {
                                for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
                                    results.append(rs.getString(columnNumber));
                                }
                            }
                            hasMoreResultSets = stmt.getMoreResults();
                        }
                        return new Greeting(
                            HibernateRestController.TEMPLATE + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                        );
                    }
                } else {
                    List<Object> results = query.getResultList();
                    if (isVisible) {
                        return new Greeting(
                            isBoolean
                            ? results.isEmpty() ? "true" : "false"
                            : HibernateRestController.TEMPLATE + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
                        );
                    }
                }
            }
        } catch (Exception e) {
            if (isError) {  // Hide useless SQL error messages except for error base injection
                return this.initErrorMessage(e);
            }
        }

        return null;
    }

    private Greeting initErrorMessage(Exception e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        LOGGER.debug(stacktrace);
        return new Greeting(HibernateRestController.TEMPLATE + "#" + StringEscapeUtils.unescapeJava(stacktrace));
    }


    // Visible injection

    @RequestMapping("/union")
    public Greeting endpointUnion(@RequestParam(value="name", defaultValue="World") String name, @RequestHeader Map<String, String> headers) {
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", false, false, true);
    }

    @RequestMapping("/stack")
    public Greeting endpointStack(@RequestParam(value="name", defaultValue="World") String name, @RequestHeader Map<String, String> headers) {
        if (name.toLowerCase().contains("union")) {
            return null;
        }
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", false, false, true, false, false, true);
    }

    @RequestMapping("/stack2")
    public Greeting endpointStack2(@RequestParam(value="name", defaultValue="World") String name, @RequestParam(value="semicolon", defaultValue="true") String semicolon, @RequestHeader Map<String, String> headers) {
        Greeting greeting = new Greeting(null);
        var result = new StringBuilder();
        Arrays.stream(("select First_Name from Student where '1' = '"+name+"'").split(";")).map(String::trim).forEach(query -> {
            if ("true".equals(semicolon)) {
                query = query +";";
            }
            Greeting g = this.getResponse(query, query, true, false, true, false, false, true);
            if (g != null) result.append(g.getContent());
        });
        greeting.setContent(result.toString());
        return greeting;
    }


    // Boolean based injection

    @RequestMapping("/blind")
    public Greeting endpointBlind(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", false, false, true, false, true, false);
    }

    @RequestMapping("/time")
    public Greeting endpointTime(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", false, false, false);
    }


    // Error based injection

    @RequestMapping("/errors")
    public Greeting endpointError(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", true, false, false);
    }

    @RequestMapping("/inside")
    public Greeting endpointInside(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select '%s'", true, false, false);
    }

    @RequestMapping("/select")
    public Greeting endpointSelect(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select %s", false, false, false);
    }

    @RequestMapping("/delete")
    public Greeting endpointDelete(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "delete from StudentForDelete where 'not_found' = '%s'", true, true, false);
    }
    
    @RequestMapping("/insert")
    public Greeting endpointInsert(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "insert into StudentForDelete select * from StudentForDelete where 'not_found' = '%s'", true, true, false);
    }
    
    @RequestMapping("/update")
    public Greeting endpointUpdate(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "update StudentForDelete set Class_Name = '' where 'not_found' = '%s'", true, true, false);
    }

    @RequestMapping("/order-by")
    public Greeting endpointOrderBy(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select First_Name from Student order by 1, '%s'", true, false, false);
    }


    // Specific injection

    @RequestMapping("/integer-insertion-char")
    public Greeting endpointIntegerInsertionChar(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select First_Name from Student where 1 = %s", true, false, true);
    }

    @RequestMapping("/multibit")
    public Greeting endpointMultibit(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select %s from (select 1)x where true or 1=", false, false, true);
    }

    @RequestMapping("/multiple-index")
    public Greeting endpointMultipleIndex(@RequestParam(value="name", defaultValue="World") String name) {
        // Postgres union int on ()::text fails: PSQLException: ERROR: UNION types integer and text cannot be matched
        return this.getResponse(name, "select 1,2,3,4,First_Name,5,6 from Student where 1 = %s", true, false, true);
    }

    @RequestMapping("/insertion-char")
    public Greeting endpointInsertionChar(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select First_Name from Student where ((\"1\" = \"%s\"))", true, false, true);
    }

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/json",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting endpointJson(HttpServletRequest request) {
        String inject = request.getParameterMap().get("name")[0];
        inject = inject.replaceAll("\\\\:", ":");
        try {
            new JSONObject(inject);
        } catch (Exception e) {
            return null;
        }
        inject = new JSONObject(inject).getJSONObject("b").getJSONArray("b").getJSONObject(3).getJSONObject("a").getString("a");
        inject = inject.replaceAll(":", "\\\\:");
        inject = inject.replace(":", "\\:");

        return this.getResponse(inject, "select First_Name from Student where '1' = '%s'", true, false, true, true, false, false);
    }

    // Special API endpoint

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/csrf",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting endpointCsrf(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
    }

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/post",
        consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.TEXT_PLAIN_VALUE }
    )
    public Greeting endpointPost(HttpServletRequest request) {
        if (request.getParameterMap().get("name") == null) {
            LOGGER.error("Parameter name not found with /post during IT");
            return null;
        }
        String inject = request.getParameterMap().get("name")[0];
        return this.getResponse(inject, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
    }

    @RequestMapping(
        method = { RequestMethod.GET, RequestMethod.POST },
        path = "/multipart",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public Greeting endpointPostMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String name = String.join(StringUtils.EMPTY, multipartRequest.getParameterValues("name"));
        return this.getResponse(name, "select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '%s'", true, false, true);
    }

    @GetMapping("/cookie")
    public Greeting endpointCookie(HttpServletRequest request, @CookieValue(name = "name", required = false, defaultValue = StringUtils.EMPTY) String name) {
        String nameUrlDecoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        return this.getResponse(nameUrlDecoded, "select 1,2,3,4,First_Name,5,6 from Student where '1' = '%s'", true, false, true);
    }

    @RequestMapping("/header")
    public Greeting endpointHeader(@RequestHeader Map<String, String> name) {
        return this.getResponse(name.getOrDefault("name", "World"), "select 1,2,First_Name from Student where '1' = '%s'", true, false, true);
    }

    @RequestMapping("/basic")
    public Greeting endpointBasicAuth(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select 1,2,3,4,5,6,7,8,9,First_Name,10,11 from Student where 999 = %s", true, false, false);
    }

    @RequestMapping("/digest")
    public Greeting endpointDigestAuth(@RequestParam(value="name", defaultValue="World") String name) {
        return this.getResponse(name, "select 1,2,3,4,5,6,7,8,9,First_Name,10,11 from Student where 999 = %s", true, false, false);
    }

    @RequestMapping("/custom")
    public Greeting endpointCustom(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) {
        if (!CustomMethodSuiteIT.CUSTOM_METHOD.equals(request.getMethod())) {
            return null;
        }
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", true, false, true);
    }
    
    @RequestMapping("/user-agent")
    public Greeting endpointUserAgent(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) {
        if (
            !Arrays.asList("CUSTOM-USER-AGENT1", "CUSTOM-USER-AGENT2")
            .contains(request.getHeader("User-Agent"))
        ) {
            return null;
        }
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", true, false, true);
    }

    @GetMapping("/path-quote/{name}/suffix")
    public Greeting endpointPathParamQuote(@PathVariable("name") String name) {
        return this.getResponse(name, "select First_Name from Student where '1' = '%s'", true, false, true);
    }

    @GetMapping("/path-integer/{name}/suffix")
    public Greeting endpointPathParamInteger(@PathVariable("name") String name) {
        return this.getResponse(name, "select First_Name from Student where '1' = %s", true, false, true);
    }

    @GetMapping("/path-select/{name}/suffix")  // for manual tests
    public Greeting endpointPathParamSelect(@PathVariable("name") String name) {
        return this.getResponse(name, "select %s", false, false, false);
    }
}
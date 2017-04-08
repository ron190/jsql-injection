package com.jsql.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

/**
 * Utility class in charge of connection to web resources and management
 * of source page and request and response headers.
 * In the same time it allows to fix different lack of functionality induced by
 * library bugs (jcifs) or core design lazyness (custom HTTP method).
 */
public class ConnectionUtil {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * URL entered by user
     */
    private static String urlByUser;

    /**
     * URL entered by user without the query string
     */
    private static String urlBase;

    /**
     * Method of injection: by query string, request or header.
     */
    private static MethodInjection methodInjection;

    /**
     * Default HTTP method. It can be changed to a custom method.
     */
    private static String typeRequest = "POST";

    /**
     * Query string built from the URL submitted by user.
     */
    private static String queryString = "";

    /**
     * Request submitted by user.
     */
    private static String request = "";

    /**
     * Header submitted by user.
     */
    private static String header = "";

    /**
     * Default timeout used by the jcifs fix. It's the default value used usually by the JVM.
     */
    public static final Integer TIMEOUT = 15000;
    
    // Utility class
    private ConnectionUtil() {
        // not used
    }
    
    /**
     * Check that the connection to the website is working correctly.
     * It uses authentication defined by user, with fixed timeout, and warn
     * user in case of authentication detected.
     * @throws InjectionFailureException when any error occurs during the connection
     */
    public static void testConnection() throws InjectionFailureException {
        // Test the HTTP connection
        HttpURLConnection connection = null;
        try {
            if (AuthenticationUtil.isKerberos()) {
                String loginKerberos =
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(
                            StringUtils.join(
                                Files.readAllLines(
                                    Paths.get(AuthenticationUtil.getPathKerberosLogin()),
                                    Charset.defaultCharset()
                                ),
                                ""
                            )
                        )
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(loginKerberos);
                connection = spnego.connect(new URL(ConnectionUtil.getUrlBase()));
            } else {
                connection = (HttpURLConnection) new URL(ConnectionUtil.getUrlBase()).openConnection();
            }
            
            connection.setReadTimeout(ConnectionUtil.TIMEOUT);
            connection.setConnectTimeout(ConnectionUtil.TIMEOUT);
            connection.setDefaultUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            ConnectionUtil.fixJcifsTimeout(connection);
            
            // Add headers if exists (Authorization:Basic, etc)
            for (String header: ConnectionUtil.getHeader().split("\\\\r\\\\n")) {
                ConnectionUtil.sanitizeHeaders(connection, header);
            }

            ConnectionUtil.checkResponseHeader(connection, ConnectionUtil.getUrlBase());
            
            // Disable caching of authentication like Kerberos
            // TODO worth the disconnection ?
//            connection.disconnect();
        } catch (Exception e) {
            throw new InjectionFailureException("Connection failed: "+ e.getMessage(), e);
        }
    }
    
    /**
     * Call an URL and return the source page.
     * @param url to call
     * @return the source page of the URL
     * @throws IOException when the reading of source page fails
     */
    public static String getSource(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(ConnectionUtil.TIMEOUT);
        connection.setConnectTimeout(ConnectionUtil.TIMEOUT);
        connection.setUseCaches(false);
        
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Expires", "-1");
        
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.URL, url);
        msgHeader.put(TypeHeader.RESPONSE, ConnectionUtil.getHttpHeaders(connection));
        
        StringBuilder pageSource = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            pageSource.append(line + "\n");
        }
        reader.close();

        msgHeader.put(TypeHeader.SOURCE, pageSource.toString());
        
        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage(TypeRequest.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
        
        // TODO optional
        return pageSource.toString().trim();
    }
    
    /**
     * Fix a wrong doing by Java core developers on design of HTTP method definition.
     * Compatible HTTP methods are stored in an array but it cannot be modified in order
     * to define your own method, whereas method should be customizable.
     * @param connection which HTTP method must be customized
     * @param customMethod to set on the connection
     * @throws ProtocolException if backup solution fails during reflectivity
     */
    public static void fixCustomRequestMethod(HttpURLConnection connection, String customMethod) throws ProtocolException {
        // Add a default or custom method : check whether we are running on a buggy JRE
        try {
            connection.setRequestMethod(customMethod);
        } catch (final ProtocolException pe) {
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(pe);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
            
            try {
                final Class<?> httpURLConnectionClass = connection.getClass();
                final Class<?> parentClass = httpURLConnectionClass.getSuperclass();
                final Field methodField;
                
                Field methods = parentClass.getDeclaredField("methods");
                methods.setAccessible(true);
                Array.set(methods.get(connection), 1, customMethod);
                
                // If the implementation class is an Https URL Connection, we
                // need to go up one level higher in the heirarchy to modify the
                // 'method' field.
                if (parentClass == HttpsURLConnection.class) {
                    methodField = parentClass.getSuperclass().getDeclaredField("method");
                } else {
                    methodField = parentClass.getDeclaredField("method");
                }
                methodField.setAccessible(true);
                methodField.set(connection, customMethod);
            } catch (Exception e) {
                LOGGER.warn("Custom Request method definition failed, forcing method GET", e);
                connection.setRequestMethod("GET");
            }
        }
    }
    
    /**
     * Fix a bug introduced by authentication library jcifs which ignore
     * default timeout of connection.
     * Use reflectivity to set connectTimeout and readTimeout attributs.
     * @param connection whose default timeout attributs will be set
     */
    public static void fixJcifsTimeout(HttpURLConnection connection) {
        Class<?> classConnection = connection.getClass();
        boolean connectionIsWrapped = true;
        
        Field privateFieldURLConnection = null;
        try {
            privateFieldURLConnection = classConnection.getDeclaredField("connection");
        } catch (Exception e) {
            // Ignore Fix
            connectionIsWrapped = false;

            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
        
        if (connectionIsWrapped) {
            try {
                privateFieldURLConnection.setAccessible(true);
                
                URLConnection privateURLConnection = (URLConnection) privateFieldURLConnection.get(connection);
                Class<?> classURLConnectionPrivate = privateURLConnection.getClass();
                
                final Class<?> parentClass = classURLConnectionPrivate.getSuperclass();
                if (parentClass == HttpsURLConnection.class) {
                    return;
                }
    
                Field privateFieldConnectTimeout = classURLConnectionPrivate.getDeclaredField("connectTimeout");
                privateFieldConnectTimeout.setAccessible(true);
                privateFieldConnectTimeout.setInt(privateURLConnection, ConnectionUtil.TIMEOUT);
                
                Field privateFieldReadTimeout = classURLConnectionPrivate.getDeclaredField("readTimeout");
                privateFieldReadTimeout.setAccessible(true);
                privateFieldReadTimeout.setInt(privateURLConnection, ConnectionUtil.TIMEOUT);
            } catch (Exception e) {
                LOGGER.warn("Fix jcifs timeout failed: "+ e.getMessage(), e);
            }
        }
    }
    
    /**
     * Parse the header component and decode any character of the form %xy
     * except for cookie
     * @param connection where decoded value will be set
     * @param header string to decode
     */
    public static void sanitizeHeaders(HttpURLConnection connection, String header) {
        Matcher regexSearch = Pattern.compile("(?s)(.*):(.*)").matcher(header);
        if (regexSearch.find()) {
            String keyHeader = regexSearch.group(1).trim();
            String valueHeader = regexSearch.group(2).trim();
            try {
                if ("Cookie".equalsIgnoreCase(keyHeader)) {
                    connection.addRequestProperty(keyHeader, valueHeader);
                } else {
                    connection.addRequestProperty(keyHeader, URLDecoder.decode(valueHeader, "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("Unsupported header encoding: "+ e.getMessage(), e);
            }
        }
    }

    /**
     * Verify the headers received after a request, detect authentication response and
     * send the headers to the view.
     * @param connection contains headers response
     * @param url the website to request
     * @throws IOException when an error occurs during connection
     */
    @SuppressWarnings("unchecked")
    public static void checkResponseHeader(HttpURLConnection connection, String url) throws IOException {
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.URL, url);
        msgHeader.put(TypeHeader.RESPONSE, ConnectionUtil.getHttpHeaders(connection));

        if (
            !PreferencesUtil.isFollowingRedirection()
            && Pattern.matches("3\\d\\d", Integer.toString(connection.getResponseCode()))
        ) {
            LOGGER.warn("HTTP 3XX Redirection detected. Please test again with option 'Follow HTTP redirection' enabled.");
        }
        
        Map<String, String> mapResponse = (Map<String, String>) msgHeader.get(TypeHeader.RESPONSE);
        if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && mapResponse.get("WWW-Authenticate") != null
            && mapResponse.get("WWW-Authenticate").startsWith("Basic ")
        ) {
            LOGGER.warn(
                "Basic Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences.\n"
                + "Or open Advanced panel, add 'Authorization: Basic b3N..3Jk' to the Header, replace b3N..3Jk with the string 'osUserName:osPassword' encoded in Base64. You can use the Coder in jSQL to encode the string."
            );
        
        } else if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && "NTLM".equals(mapResponse.get("WWW-Authenticate"))
        ) {
            LOGGER.warn(
                "NTLM Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences.\n"
                + "Or add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        
        } else if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && mapResponse.get("WWW-Authenticate") != null
            && mapResponse.get("WWW-Authenticate").startsWith("Digest ")
        ) {
            LOGGER.warn(
                "Digest Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences."
            );
        
        } else if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && "Negotiate".equals(mapResponse.get("WWW-Authenticate"))
        ) {
            LOGGER.warn(
                "Negotiate Authentication detected.\n"
                + "Please add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        }
        
        // Request the web page to the server
        StringBuilder pageSource = new StringBuilder();
        Exception exception = null;
        
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                pageSource.append(line + "\r\n");
            }
        } catch (IOException e) {
            exception = e;
        }

        msgHeader.put(TypeHeader.SOURCE, pageSource.toString());
        
        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage(TypeRequest.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
        
        if (exception != null) {
            throw new IOException(exception);
        }
    }
    
    /**
     * Extract HTTP headers from a connection.
     * @param connection Connection with HTTP headers
     * @return Map of HTTP headers <name, value>
     */
    public static Map<String, String> getHttpHeaders(URLConnection connection) {
        Map<String, String> mapHeaders = new HashMap<>();
        
        for (int i = 0 ; ; i++) {
            // Fix #6456: IllegalArgumentException on getHeaderFieldKey()
            // Implementation by sun.net.www.protocol.http.HttpURLConnection.getHeaderFieldKey()
            try {
                String headerName = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);
                if (headerName == null && headerValue == null) {
                    break;
                }
                mapHeaders.put(headerName == null ? "Method" : headerName, headerValue);
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return mapHeaders;
    }
    
    // Getters and setters
    
    public static String getUrlByUser() {
        return urlByUser;
    }

    public static void setUrlByUser(String urlByUser) {
        ConnectionUtil.urlByUser = urlByUser;
    }
    
    public static String getUrlBase() {
        return urlBase;
    }

    public static void setUrlBase(String urlBase) {
        ConnectionUtil.urlBase = urlBase;
    }
    
    public static MethodInjection getMethodInjection() {
        return methodInjection;
    }

    public static void setMethodInjection(MethodInjection methodInjection) {
        ConnectionUtil.methodInjection = methodInjection;
    }
    
    public static String getTypeRequest() {
        return typeRequest;
    }

    public static void setTypeRequest(String typeRequest) {
        ConnectionUtil.typeRequest = typeRequest;
    }
    
    public static String getQueryString() {
        return queryString;
    }

    public static void setQueryString(String queryString) {
        ConnectionUtil.queryString = queryString;
    }
    
    public static String getRequest() {
        return request;
    }

    public static void setRequest(String request) {
        ConnectionUtil.request = request;
    }
    
    public static String getHeader() {
        return header;
    }

    public static void setHeader(String header) {
        ConnectionUtil.header = header;
    }
    
}

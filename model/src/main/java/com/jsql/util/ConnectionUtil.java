package com.jsql.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.util.protocol.SessionCookieManager;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

/**
 * Utility class in charge of connection to web resources and management
 * of source page and request and response headers.
 * In the same time it allows to fix different lack of functionality induced by
 * library bugs (jcifs) or core design laziness (custom HTTP method).
 */
public class ConnectionUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * URL entered by user
     */
    private String urlByUser;

    /**
     * URL entered by user without the query string
     */
    private String urlBase;

    /**
     * Method of injection: by query string, request or header.
     */
    private AbstractMethodInjection methodInjection;

    /**
     * Default HTTP method. It can be changed to a custom method.
     */
    private String typeRequest = "POST";

    /**
     * Default timeout used by the jcifs fix. It's the default value used usually by the JVM.
     */
    private static final Integer TIMEOUT = 15000;
    private static final String NO_CACHE = "no-cache";
    
    private Random randomForUserAgent = new Random();
    
    private InjectionModel injectionModel;
    
    public ConnectionUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }
    
    /**
     * Check that the connection to the website is working correctly.
     * It uses authentication defined by user, with fixed timeout, and warn
     * user in case of authentication detected.
     * @throws InjectionFailureException when any error occurs during the connection
     */
    public void testConnection() throws InjectionFailureException {

        // Set multithreaded Cookie handler
        // Allows CSRF token processing during ITs, can be used for batch scan
        if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotProcessingCookies()) {
            
            CookieHandler.setDefault(SessionCookieManager.getInstance());
            
        } else {
            
            SessionCookieManager.getInstance().clear();
            CookieHandler.setDefault(null);
        }

        // Test the HTTP connection
        HttpURLConnection connection = null;
        try {
            if (this.injectionModel.getMediatorUtils().getAuthenticationUtil().isKerberos()) {
                
                String loginKerberos =
                    Pattern
                    .compile("(?s)\\{.*")
                    .matcher(
                        StringUtils.join(
                            Files.readAllLines(
                                Paths.get(this.injectionModel.getMediatorUtils().getAuthenticationUtil().getPathKerberosLogin()),
                                Charset.defaultCharset()
                            ),
                            StringUtils.EMPTY
                        )
                    )
                    .replaceAll(StringUtils.EMPTY)
                    .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(loginKerberos);
                connection = spnego.connect(new URL(this.getUrlByUser()));
                
            } else {
                
                connection =
                    (HttpURLConnection) new URL(
                        this.getUrlByUser()
                        // Ignore injection point during the test
                        .replace(InjectionModel.STAR, StringUtils.EMPTY)
                    )
                    .openConnection();
            }
            
            connection.setReadTimeout(this.getTimeout());
            connection.setConnectTimeout(this.getTimeout());
            connection.setDefaultUseCaches(false);
            connection.setRequestProperty("Pragma", NO_CACHE);
            connection.setRequestProperty("Cache-Control", NO_CACHE);
            connection.setRequestProperty("Expires", "-1");
            connection.setRequestProperty("Content-Type", "text/plain");
            
            this.fixJcifsTimeout(connection);
            
            // Add headers if exists (Authorization:Basic, etc)
            for (SimpleEntry<String, String> header: this.injectionModel.getMediatorUtils().getParameterUtil().getListHeader()) {
                
                HeaderUtil.sanitizeHeaders(connection, header);
            }

            this.injectionModel.getMediatorUtils().getHeaderUtil()
            .checkResponseHeader(
                connection,
                this.getUrlByUser().replace(InjectionModel.STAR, StringUtils.EMPTY)
            );
            
            // Calling connection.disconnect() is not required, more calls will happen
            
        } catch (Exception e) {
            
            String message = Optional.ofNullable(e.getMessage()).orElse(StringUtils.EMPTY);
            throw new InjectionFailureException("Connection failed: "+ message.replace(e.getClass().getName() +": ", StringUtils.EMPTY), e);
        }
    }
    
    public String getSource(String url, boolean lineFeed) throws IOException {
        
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(this.getTimeout());
        connection.setConnectTimeout(this.getTimeout());
        connection.setUseCaches(false);
        
        connection.setRequestProperty("Pragma", NO_CACHE);
        connection.setRequestProperty("Cache-Control", NO_CACHE);
        connection.setRequestProperty("Expires", "-1");
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, url);
        msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));
        
        String pageSource = null;
        try {
            if (lineFeed) {
                
                pageSource = ConnectionUtil.getSourceLineFeed(connection);
                
            } else {
                
                pageSource = ConnectionUtil.getSource(connection);
            }
            
        } catch (IOException e) {

            LOGGER.error(e, e);
            
        } finally {
            
            msgHeader.put(Header.SOURCE, pageSource);
            
            // Inform the view about the log infos
            Request request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.injectionModel.sendToViews(request);
        }
        
        return pageSource.trim();
    }
    
    public static String getSourceLineFeed(HttpURLConnection connection) throws IOException {
        
        StringBuilder pageSource = new StringBuilder();
    
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        
        while ((line = reader.readLine()) != null) {
            
            pageSource.append(line +"\n");
        }
        
        reader.close();
        
        return pageSource.toString();
    }
    
    public static String getSource(HttpURLConnection connection) throws IOException {
        
        ByteArrayOutputStream pageSource = new ByteArrayOutputStream();
        
        // Get connection content without null bytes %00
        try {
            byte[] buffer = new byte[1024];
            int length;
            
            while ((length = connection.getInputStream().read(buffer)) != -1) {
                
                pageSource.write(buffer, 0, length);
            }
            
        } catch (IOException errorInputStream) {
            
            InputStream errorStream = connection.getErrorStream();
            
            if (errorStream != null) {
                
                try {
                    byte[] buffer = new byte[1024];
                    int length;
                    
                    while ((length = connection.getInputStream().read(buffer)) != -1) {
                        
                        pageSource.write(buffer, 0, length);
                    }
                    
                } catch (IOException e) {
                    
                    // Ignore errors related to wrong URLs generated by injection
                    IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                    LOGGER.trace(exceptionIgnored, exceptionIgnored);
                }
            }
        }
        
        return pageSource.toString("UTF-8");
    }
    
    /**
     * Call an URL and return the source page.
     * @param url to call
     * @return the source page of the URL
     * @throws IOException when the reading of source page fails
     */
    public String getSourceLineFeed(String url) throws IOException {
        
        return this.getSource(url, true);
    }
    
    public String getSource(String url) throws IOException {
        
        return this.getSource(url, false);
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
        
        // Add a default or custom method: check whether we are running on a buggy JRE
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
                // need to go up one level higher in the hierarchy to modify the
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
     * Use reflectivity to set connectTimeout and readTimeout attributes.
     * @param connection whose default timeout attributes will be set
     */
    public void fixJcifsTimeout(HttpURLConnection connection) {
        
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
                privateFieldConnectTimeout.setInt(privateURLConnection, this.getTimeout());
                
                Field privateFieldReadTimeout = classURLConnectionPrivate.getDeclaredField("readTimeout");
                privateFieldReadTimeout.setAccessible(true);
                privateFieldReadTimeout.setInt(privateURLConnection, this.getTimeout());
                
            } catch (Exception e) {
                
                LOGGER.warn("Fix jcifs timeout failed: "+ e.getMessage(), e);
            }
        }
    }
    
    public void setCustomUserAgent(HttpURLConnection connection) {
        
        if (this.injectionModel.getMediatorUtils().getUserAgentUtil().isCustomUserAgent()) {
            
            String agents = this.injectionModel.getMediatorUtils().getUserAgentUtil().getCustomUserAgent();
            List<String> listAgents =
                Stream
                .of(agents.split("[\\r\\n]{1,}"))
                .filter(q -> !q.matches("^#.*"))
                .collect(Collectors.toList());
            
            String randomElement = listAgents.get(this.randomForUserAgent.nextInt(listAgents.size()));
            
            connection.addRequestProperty("User-Agent", randomElement);
        }
    }
    
    
    // Builder

    public ConnectionUtil withMethodInjection(AbstractMethodInjection methodInjection) {
        this.methodInjection = methodInjection;
        return this;
    }
    
    public ConnectionUtil withTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
        return this;
    }
    
    
    // Getters and setters
    
    public String getUrlByUser() {
        return this.urlByUser;
    }

    public void setUrlByUser(String urlByUser) {
        this.urlByUser = urlByUser;
    }
    
    public String getUrlBase() {
        return this.urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
    
    public AbstractMethodInjection getMethodInjection() {
        return this.methodInjection;
    }

    public void setMethodInjection(AbstractMethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }
    
    public String getTypeRequest() {
        return this.typeRequest;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }

    public Integer getTimeout() {
        return ConnectionUtil.TIMEOUT;
    }
}
package com.jsql.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.InjectionModel.MethodInjection;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.model.exception.InjectionFailureException;

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
    private String urlByUser;

    /**
     * URL entered by user without the query string
     */
    private String urlBase;

    /**
     * Method of injection: by query string, request or header.
     */
    private MethodInjection methodInjection;

    /**
     * Default HTTP method. It can be changed to a custom method.
     */
    private String typeRequest = "POST";

    /**
     * Default timeout used by the jcifs fix. It's the default value used usually by the JVM.
     */
    private final Integer TIMEOUT = 15000;
    
    private SimpleEntry<String, String> tokenCsrf = null;
    
    // Utility class
    private ConnectionUtil() {
        // not used
    }
    
    public ConnectionUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Check that the connection to the website is working correctly.
     * It uses authentication defined by user, with fixed timeout, and warn
     * user in case of authentication detected.
     * @throws InjectionFailureException when any error occurs during the connection
     */
    public void testConnection() throws InjectionFailureException {

        if (this.injectionModel.preferencesUtil.isProcessingCookies()) {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
        } else {
            CookieHandler.setDefault(null);
        }
        
        // Test the HTTP connection
        HttpURLConnection connection = null;
        try {
            if (this.injectionModel.authenticationUtil.isKerberos()) {
                String loginKerberos =
                    Pattern
                        .compile("(?s)\\{.*")
                        .matcher(
                            StringUtils.join(
                                Files.readAllLines(
                                    Paths.get(this.injectionModel.authenticationUtil.getPathKerberosLogin()),
                                    Charset.defaultCharset()
                                ),
                                ""
                            )
                        )
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(loginKerberos);
                connection = spnego.connect(new URL(this.getUrlByUser()));
            } else {
                connection = (HttpURLConnection) new URL(
                    this.getUrlByUser()
                    // Ignore injection point during the test
                    .replace(InjectionModel.STAR, "")
                ).openConnection();
            }
            
            connection.setReadTimeout(this.getTimeout());
            connection.setConnectTimeout(this.getTimeout());
            connection.setDefaultUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            this.fixJcifsTimeout(connection);
            
            // Add headers if exists (Authorization:Basic, etc)
            for (SimpleEntry<String, String> header: this.injectionModel.parameterUtil.getHeader()) {
                HeaderUtil.sanitizeHeaders(connection, header);
            }

            this.injectionModel.headerUtil.checkResponseHeader(connection, this.getUrlByUser().replace(InjectionModel.STAR, ""));
            
            // Calling connection.disconnect() is not required, more calls will happen
        } catch (Exception e) {
            String message = Optional.ofNullable(e.getMessage()).orElse("");
            throw new InjectionFailureException("Connection failed: "+ message.replace(e.getClass().getName() +": ", ""), e);
        }
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
    
    public String getSource(String url, boolean lineFeed) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(this.getTimeout());
        connection.setConnectTimeout(this.getTimeout());
        connection.setUseCaches(false);
        
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Expires", "-1");
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, url);
        msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));
        
        String pageSource = null;
        try {
            if (lineFeed) {
                pageSource = this.getSourceLineFeed(connection);
            } else {
                pageSource = this.getSource(connection);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            msgHeader.put(Header.SOURCE, pageSource);
            
            // Inform the view about the log infos
            Request request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.injectionModel.sendToViews(request);
        }
        
        // TODO optional
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
        StringBuilder pageSource = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            char[] buffer = new char[4096];
            while (reader.read(buffer) > 0) {
                pageSource.append(buffer);
            }
            reader.close();
        } catch (IOException errorInputStream) {
            InputStream errorStream = connection.getErrorStream();
            
            if (errorStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                    char[] buffer = new char[4096];
                    while (reader.read(buffer) > 0) {
                        pageSource.append(buffer);
                    }
                    reader.close();
                } catch (Exception errorErrorStream) {
                    throw errorErrorStream;
                }
            }
        }
        
        return pageSource.toString();
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
    
    public MethodInjection getMethodInjection() {
        return this.methodInjection;
    }

    public void setMethodInjection(MethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }
    
    public String getTypeRequest() {
        return this.typeRequest;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }

    public Integer getTimeout() {
        return this.TIMEOUT;
    }

    public SimpleEntry<String, String> getTokenCsrf() {
        return this.tokenCsrf;
    }

    public void setTokenCsrf(SimpleEntry<String, String> tokenCsrf) {
        this.tokenCsrf = tokenCsrf;
    }
    
    
    
}

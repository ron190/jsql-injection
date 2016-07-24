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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.MethodInjection;

public class ConnectionUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ConnectionUtil.class);
    
    private static String urlByUser;

    /**
     * Url entered by user.
     */
    private static String urlBase;

    /**
     * Http request type : GET, POST, HEADER...
     */
    private static MethodInjection methodInjection;

    private static String typeRequest = "POST";

    /**
     * Get data submitted by user.
     */
    private static String dataQuery = "";

    /**
     * Request data submitted by user.
     */
    private static String dataRequest = "";

    /**
     * Header data submitted by user.
     */
    private static String dataHeader = "";

    public static final Integer TIMEOUT = 15000;
    
    private ConnectionUtil() {
        // Utility class
    }
    
    public static void testConnection() throws InjectionFailureException {
        // Test the HTTP connection
        HttpURLConnection connection = null;
        try {
            if (AuthenticationUtil.isKerberos()) {
                String loginKerberos = 
                        Pattern
                            .compile("(?s)\\{.*")
                            .matcher(StringUtils.join(Files.readAllLines(Paths.get(AuthenticationUtil.getPathKerberosLogin()), Charset.defaultCharset()), ""))
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
            
            ConnectionUtil.fixJcifsTimeout(connection);
            
            // Add headers if exists (Authorization:Basic, etc)
            for (String header: ConnectionUtil.getDataHeader().split("\\\\r\\\\n")) {
                ConnectionUtil.sanitizeHeaders(connection, header);
            }

            StringUtil.sendMessageHeader(connection, ConnectionUtil.getUrlBase());
            
            // Disable caching of authentication like Kerberos
            connection.disconnect();
        } catch (Exception e) {
            throw new InjectionFailureException("Connection to "+ ConnectionUtil.getUrlBase() +" failed : "+ e, e);
        }
    }
    
    public static String getSource(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setReadTimeout(ConnectionUtil.TIMEOUT);
        connection.setConnectTimeout(ConnectionUtil.TIMEOUT);
        
        String pageSource = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            pageSource += line + "\n";
        }
        reader.close();
        
        return pageSource.trim();
    }
    
    public static void fixCustomRequestMethod(HttpURLConnection connection, String typeRequest) throws ProtocolException {
        // Add default or custom method : check whether we are running on a buggy JRE
        try {
            connection.setRequestMethod(typeRequest);
        } catch (final ProtocolException pe) {
            try {
                final Class<?> httpURLConnectionClass = connection.getClass();
                final Class<?> parentClass = httpURLConnectionClass.getSuperclass();
                final Field methodField;
                
                Field methods = parentClass.getDeclaredField("methods");
                methods.setAccessible(true);
                Array.set(methods.get(connection), 1, typeRequest);
                
                // If the implementation class is an Https URL Connection, we
                // need to go up one level higher in the heirarchy to modify the
                // 'method' field.
                if (parentClass == HttpsURLConnection.class) {
                    methodField = parentClass.getSuperclass().getDeclaredField("method");
                } else {
                    methodField = parentClass.getDeclaredField("method");
                }
                methodField.setAccessible(true);
                methodField.set(connection, typeRequest);
            } catch (Exception e) {
                LOGGER.warn("Custom Request method definition failed, forcing method GET", e);
                connection.setRequestMethod("GET");
            }
        }
    }
    
    public static void fixJcifsTimeout(HttpURLConnection connection) {
        Class<?> classConnection = connection.getClass();
        try {
            boolean isReflexive = true;
            
            Field privateFieldURLConnection = null;
            try {
                privateFieldURLConnection = classConnection.getDeclaredField("connection");
            } catch (Exception e) {
                isReflexive = false;
            }
            
            if (isReflexive) {
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
            }
        } catch (Exception e) {
            LOGGER.warn("Fix jcifs timeout failed: "+ e, e);
        }
    }
    
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
                LOGGER.warn("Unsupported header encoding "+ e, e);
            }
        }
    }
    
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
    
    public static String getDataQuery() {
        return dataQuery;
    }

    public static void setDataQuery(String dataQuery) {
        ConnectionUtil.dataQuery = dataQuery;
    }
    
    public static String getDataRequest() {
        return dataRequest;
    }

    public static void setDataRequest(String dataRequest) {
        ConnectionUtil.dataRequest = dataRequest;
    }
    
    public static String getDataHeader() {
        return dataHeader;
    }

    public static void setDataHeader(String dataHeader) {
        ConnectionUtil.dataHeader = dataHeader;
    }
}

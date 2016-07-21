package com.jsql.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    
    public static String urlByUser;
    
    /**
     * Url entered by user.
     */
    public static String urlBase;
    
    /**
     * Http request type : GET, POST, HEADER...
     */
    public static MethodInjection methodInjection;
    
    public static String typeRequest = "POST";
    
    /**
     * Get data submitted by user.
     */
    public static String dataQuery = "";
    
    /**
     * Request data submitted by user.
     */
    public static String dataRequest = "";
    
    /**
     * Header data submitted by user.
     */
    public static String dataHeader = "";
    
    public static final Integer timeOut = 15000;
    
    public static void testConnection() throws InjectionFailureException {
        // Test the HTTP connection
        HttpURLConnection connection = null;
        try {
            if (AuthenticationUtil.isKerberos) {
                String loginKerberos = 
                        Pattern
                            .compile("(?s)\\{.*")
                            .matcher(StringUtils.join(Files.readAllLines(Paths.get(AuthenticationUtil.pathKerberosLogin), Charset.defaultCharset()), ""))
                            .replaceAll("")
                            .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(loginKerberos);
                connection = spnego.connect(new URL(ConnectionUtil.urlBase));
            } else {
                connection = (HttpURLConnection) new URL(ConnectionUtil.urlBase).openConnection();
            }
            
            connection.setReadTimeout(ConnectionUtil.timeOut);
            connection.setConnectTimeout(ConnectionUtil.timeOut);
            connection.setDefaultUseCaches(false);
            
            ConnectionUtil.fixJcifsTimeout(connection);
            
            // Add headers if exists (Authorization:Basic, etc)
            for (String header: ConnectionUtil.dataHeader.split("\\\\r\\\\n")) {
                ConnectionUtil.sanitizeHeaders(connection, header);
            }

            StringUtil.sendMessageHeader(connection, ConnectionUtil.urlBase);
            
            // Disable caching of authentication like Kerberos
            connection.disconnect();
        } catch (Exception e) {
            throw new InjectionFailureException("Connection to "+ ConnectionUtil.urlBase +" failed : "+ e.getMessage());
        }
    }
    
    public static String getSource(String url) throws MalformedURLException, IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setReadTimeout(ConnectionUtil.timeOut);
        connection.setConnectTimeout(ConnectionUtil.timeOut);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        String pageSource = "";
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
            Field privateFieldURLConnection = classConnection.getDeclaredField("connection");
            privateFieldURLConnection.setAccessible(true);
            
            URLConnection privateURLConnection = (URLConnection) privateFieldURLConnection.get(connection);
            Class<?> classURLConnectionPrivate = privateURLConnection.getClass();
            
            Field privateFieldConnectTimeout = classURLConnectionPrivate.getDeclaredField("connectTimeout");
            privateFieldConnectTimeout.setAccessible(true);
            privateFieldConnectTimeout.setInt(privateURLConnection, ConnectionUtil.timeOut);
            
            Field privateFieldReadTimeout = classURLConnectionPrivate.getDeclaredField("readTimeout");
            privateFieldReadTimeout.setAccessible(true);
            privateFieldReadTimeout.setInt(privateURLConnection, ConnectionUtil.timeOut);
        } catch (Exception e) {
            LOGGER.warn("Fix jcifs timeout failed: "+ e.getMessage(), e);
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
                LOGGER.warn("Unsupported header encoding " + e.getMessage(), e);
            }
        }
    }
}

package com.jsql.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.spnego.SpnegoHttpURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.exception.PreparationException;
import com.jsql.model.injection.method.MethodInjection;

public class ConnectionUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ConnectionUtil.class);
    
    /**
     * Url entered by user.
     */
    public static String urlByUser;
    
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
    
    public static void testConnection() throws PreparationException {
        // Test the HTTP connection
        HttpURLConnection connection = null;
        try {
            if (AuthenticationUtil.isKerberos) {
                String a = 
                        Pattern
                        .compile("(?s)\\{.*")
                        .matcher(StringUtils.join(Files.readAllLines(Paths.get(AuthenticationUtil.pathKerberosLogin), Charset.defaultCharset()), ""))
                        .replaceAll("")
                        .trim();
                
                SpnegoHttpURLConnection spnego = new SpnegoHttpURLConnection(a);
                connection = spnego.connect(new URL(ConnectionUtil.urlByUser));
            } else {
                connection = (HttpURLConnection) new URL(ConnectionUtil.urlByUser).openConnection();
            }
            
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDefaultUseCaches(false);
            HttpURLConnection.setFollowRedirects(PreferencesUtil.isFollowingRedirection);
            
            // Add headers if exists (Authorization:Basic, etc)
            for (String header: ConnectionUtil.dataHeader.split("\\\\r\\\\n")) {
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

            StringUtil.sendMessageHeader(connection, ConnectionUtil.urlByUser);
            
            // Disable caching of authentication like Kerberos
            connection.disconnect();
        } catch (Exception e) {
            throw new PreparationException("Connection problem: " + e.getMessage());
        }
    }
    
    public static String getSource(String url) throws MalformedURLException, IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        String pageSource = "";
        while ((line = reader.readLine()) != null) {
            pageSource += line + "\n";
        }
        reader.close();
        return pageSource;
    }
}

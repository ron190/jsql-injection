package com.jsql.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;

public class HeaderUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    public static final String CONTENT_TYPE = "Content-Type";
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String REGEX_HTTP_STATUS = "4\\d\\d";
    private static final String FOUND_STATUS_HTTP = "Found status HTTP ";

    private InjectionModel injectionModel;
    
    public HeaderUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    /**
     * Parse the header component and decode any character of the form %xy
     * except for cookie
     * @param connection where decoded value will be set
     * @param header string to decode
     */
    public static void sanitizeHeaders(HttpURLConnection connection, SimpleEntry<String, String> header) {
        
        String keyHeader = header.getKey().trim();
        String valueHeader = header.getValue().trim();
        
        // Fix #2124: NullPointerException on addRequestProperty()
        try {
            if ("Cookie".equalsIgnoreCase(keyHeader)) {
                
                // TODO enclose value in "" => Cookie: a="a"; b="b"
                connection.addRequestProperty(keyHeader, valueHeader);
                
            } else {
                
                connection.addRequestProperty(
                    keyHeader,
                    URLDecoder.decode(
                        valueHeader,
                        StandardCharsets.UTF_8.name()
                    )
                );
            }
            
        } catch (NullPointerException | UnsupportedEncodingException e) {
            
            LOGGER.error(e, e);
        }
    }

    /**
     * Verify the headers received after a request, detect authentication response and
     * send the headers to the view.
     * @param connection contains headers response
     * @param urlByUser the website to request
     * @throws IOException when an error occurs during connection
     */
    public void checkResponseHeader(HttpURLConnection connection, String urlByUser) throws IOException {
        
        Map<String, String> headers = HeaderUtil.getHttpHeaders(connection);

        String responseCode = Integer.toString(connection.getResponseCode());
        
        this.checkResponse(responseCode, headers);
        
        // Request the web page to the server
        Exception exception = null;
        
        StringBuilder pageSource = new StringBuilder();
        
        exception = this.readSource(connection, pageSource);
        
        this.injectionModel.getMediatorUtils().getFormUtil().parseForms(connection, pageSource);
        
        this.injectionModel.getMediatorUtils().getCsrfUtil().parseForCsrfToken(pageSource, headers);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, urlByUser);
        msgHeader.put(Header.RESPONSE, headers);
        msgHeader.put(Header.SOURCE, pageSource.toString());
        
        // Inform the view about the log info
        Request request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
        
        if (exception != null) {
            
            throw new IOException(exception);
        }
    }

    private Exception readSource(HttpURLConnection connection, StringBuilder pageSource) throws IOException {
        
        Exception exception = null;
        
        ByteArrayOutputStream sourceByteArray = new ByteArrayOutputStream();
        
        // Get connection content without null bytes %00
        try {
            byte[] buffer = new byte[1024];
            int length;
            
            while ((length = connection.getInputStream().read(buffer)) != -1) {
                
                sourceByteArray.write(buffer, 0, length);
            }
            
        } catch (IOException errorInputStream) {
            
            exception = errorInputStream;
            InputStream errorStream = connection.getErrorStream();
            
            if (errorStream != null) {
                
                try {
                    byte[] buffer = new byte[1024];
                    int length;
                    
                    while ((length = errorStream.read(buffer)) != -1) {
                        
                        sourceByteArray.write(buffer, 0, length);
                    }
                    
                } catch (Exception errorErrorStream) {
                    
                    exception = new IOException("Exception reading Error Stream", errorErrorStream);
                }
            }
        }
        
        pageSource.append(sourceByteArray.toString(StandardCharsets.UTF_8.name()));
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotTestingConnection()) {
            
            if (exception != null) {
                
                LOGGER.debug("Connection test disabled, ignoring response HTTP {}...", connection.getResponseCode());
            }
            
            exception = null;
            
        } else if (exception != null) {
            
            LOGGER.info("Select option 'Disable connection test' and run again");
        }
        
        return exception;
    }

    private void checkResponse(String responseCode, Map<String, String> mapResponse) {
        
        if (this.isBasicAuth(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "Basic Authentication detected.\n"
                + "Define and enable authentication information in the panel Preferences.\n"
                + "Or open Advanced panel, add 'Authorization: Basic b3N..3Jk' to the Header, replace b3N..3Jk with "
                + "the string 'osUserName:osPassword' encoded in Base64. You can use the Coder in jSQL to encode the string."
            );
        
        } else if (this.isNtlm(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "NTLM Authentication detected.\n"
                + "Define and enable authentication information in the panel Preferences.\n"
                + "Or add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        
        } else if (this.isDigest(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "Digest Authentication detected.\n"
                + "Define and enable authentication information in the panel Preferences."
            );
        
        } else if (this.isNegotiate(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "Negotiate Authentication detected.\n"
                + "Add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
            
        } else if (Pattern.matches("1\\d\\d", responseCode)) {
            
            LOGGER.trace("{} {} Informational", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("2\\d\\d", responseCode)) {
            
            LOGGER.debug("{} {} Success", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("3\\d\\d", responseCode)) {
            
            LOGGER.warn("{} {} Redirection", FOUND_STATUS_HTTP, responseCode);
            
            if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isFollowingRedirection()) {
                
                LOGGER.warn("If injection fails retry with option 'Follow HTTP redirection' activated");
                
            } else {
                
                LOGGER.info("Redirecting to the next page...");
            }
            
        } else if (Pattern.matches(REGEX_HTTP_STATUS, responseCode)) {
            
            LOGGER.warn("{} {} Client Error", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("5\\d\\d", responseCode)) {
            
            LOGGER.warn("{} {} Server Error", FOUND_STATUS_HTTP, responseCode);
            
        } else {
            
            LOGGER.trace("{} {} Unknown", FOUND_STATUS_HTTP, responseCode);
        }
    }
    
    private boolean isNegotiate(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE)
            && "Negotiate".equals(mapResponse.get(WWW_AUTHENTICATE));
    }

    private boolean isDigest(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE)
            && mapResponse.get(WWW_AUTHENTICATE) != null
            && mapResponse.get(WWW_AUTHENTICATE).startsWith("Digest ");
    }

    private boolean isNtlm(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE)
            && "NTLM".equals(mapResponse.get(WWW_AUTHENTICATE));
    }

    private boolean isBasicAuth(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE)
            && mapResponse.get(WWW_AUTHENTICATE) != null
            && mapResponse.get(WWW_AUTHENTICATE).startsWith("Basic ");
    }
    
    /**
     * Extract HTTP headers from a connection.
     * @param connection Connection with HTTP headers
     * @return Map of HTTP headers <name, value>
     */
    public static Map<String, String> getHttpHeaders(URLConnection connection) {
        
        Map<String, String> mapHeaders = new HashMap<>();
        
        // Unhandled NoSuchElementException #91041 on getHeaderFields()
        try {
            for (Map.Entry<String, List<String>> entries: connection.getHeaderFields().entrySet()) {
                
                mapHeaders.put(
                    entries.getKey() == null
                    ? "Status code"
                    : entries.getKey(),
                    String.join(",", entries.getValue())
                );
            }
            
        } catch (NoSuchElementException e) {
            
            LOGGER.error(e, e);
        }
        
        return mapHeaders;
    }
}

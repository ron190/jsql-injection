package com.jsql.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    
    public static final String CONTENT_TYPE_REQUEST = "Content-Type";
    private static final String WWW_AUTHENTICATE_RESPONSE = "www-authenticate";
    private static final String REGEX_HTTP_STATUS = "4\\d\\d";
    private static final String FOUND_STATUS_HTTP = "Found status HTTP";

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
    public static void sanitizeHeaders(Builder httpRequest, SimpleEntry<String, String> header) {
        
        String keyHeader = header.getKey().trim();
        String valueHeader = header.getValue().trim();
        
        // Fix #2124: NullPointerException on addRequestProperty()
        try {
            if ("Cookie".equalsIgnoreCase(keyHeader)) {
                
                // TODO enclose value in "" => Cookie: a="a"; b="b"
                httpRequest.setHeader(keyHeader, valueHeader);
                
            } else {
                
                httpRequest.setHeader(
                    keyHeader,
                    URLDecoder.decode(
                        valueHeader,
                        StandardCharsets.UTF_8.name()
                    )
                );
            }
            
        } catch (NullPointerException | UnsupportedEncodingException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
    }

    /**
     * Verify the headers received after a request, detect authentication response and
     * send the headers to the view.
     * @param connection contains headers response
     * @param urlByUser the website to request
     * @throws IOException when an error occurs during connection
     */
    public void checkResponseHeader(Builder httpRequestBuilder, String replace) throws IOException, InterruptedException {
        
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse<String> response = this.injectionModel.getMediatorUtils().getConnectionUtil().getHttpClient().send(
            httpRequest, 
            BodyHandlers.ofString()
        );
        String pageSource = response.body();
        HttpHeaders httpHeaders = response.headers();
        
        Map<String, String> mapHeaders =
            httpHeaders
                .map()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Entry::getKey))
                .map(entrySet ->
                    new AbstractMap.SimpleEntry<>(
                        entrySet.getKey(),
                        String.join(", ", entrySet.getValue())
                    )
                )
                .collect(Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey,
                    AbstractMap.SimpleEntry::getValue
                ));
        
        String responseCode = Integer.toString(response.statusCode());
        
        this.checkResponse(responseCode, mapHeaders);
        
        // Request the web page to the server
        Exception exception = null;
        
        exception = this.readSource(response);
        
        this.injectionModel.getMediatorUtils().getFormUtil().parseForms(response.statusCode(), pageSource);
        
        this.injectionModel.getMediatorUtils().getCsrfUtil().parseForCsrfToken(pageSource, mapHeaders);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, httpRequest.uri().toURL().toString());
        msgHeader.put(Header.RESPONSE, mapHeaders);
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

    private Exception readSource(HttpResponse<String> response) throws IOException {
        
        Exception exception = null;
        
        if (response.statusCode() >= 400) {
            
            exception = new IOException(String.format("problem when calling %s", response.uri().toURL().toString()));
        }
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotTestingConnection()) {
            
            if (exception != null) {
                
                LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Connection test disabled, ignoring response HTTP {}...", response.statusCode());
            }
            
            exception = null;
            
        } else if (exception != null) {
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Select option 'Disable connection test' and run again");
        }
        
        return exception;
    }

    private void checkResponse(String responseCode, Map<String, String> mapResponse) {
        
        if (this.isBasicAuth(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR, 
                "Basic Authentication detected: "
                + "define and enable authentication information in the panel Preferences, "
                + "or open Advanced panel, add 'Authorization: Basic b3N..3Jk' to the Header, replace b3N..3Jk with "
                + "the string 'osUserName:osPassword' encoded in Base64. You can use the Coder in jSQL to encode the string."
            );
        
        } else if (this.isNtlm(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR, 
                "NTLM Authentication detected: "
                + "define and enable authentication information in the panel Preferences, "
                + "or add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        
        } else if (this.isDigest(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR, 
                "Digest Authentication detected: "
                + "define and enable authentication information in the panel Preferences."
            );
        
        } else if (this.isNegotiate(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevel.CONSOLE_ERROR, 
                "Negotiate Authentication detected: "
                + "add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
            
        } else if (Pattern.matches("1\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, "{} {} Informational", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("2\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevel.CONSOLE_SUCCESS, "{} {} Success", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("3\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "{} {} Redirection", FOUND_STATUS_HTTP, responseCode);
            
            if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isFollowingRedirection()) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "If injection fails retry with option 'Follow HTTP redirection' activated");
                
            } else {
                
                LOGGER.log(LogLevel.CONSOLE_INFORM, "Redirecting to the next page...");
            }
            
        } else if (Pattern.matches(REGEX_HTTP_STATUS, responseCode)) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "{} {} Client Error", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("5\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "{} {} Server Error", FOUND_STATUS_HTTP, responseCode);
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, "{} {} Unknown", FOUND_STATUS_HTTP, responseCode);
        }
    }
    
    private boolean isNegotiate(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE_RESPONSE)
            && "Negotiate".equals(mapResponse.get(WWW_AUTHENTICATE_RESPONSE));
    }

    private boolean isDigest(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE_RESPONSE)
            && mapResponse.get(WWW_AUTHENTICATE_RESPONSE) != null
            && mapResponse.get(WWW_AUTHENTICATE_RESPONSE).startsWith("Digest ");
    }

    private boolean isNtlm(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE_RESPONSE)
            && "NTLM".equals(mapResponse.get(WWW_AUTHENTICATE_RESPONSE));
    }

    private boolean isBasicAuth(String responseCode, Map<String, String> mapResponse) {
        
        return
            Pattern.matches(REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(WWW_AUTHENTICATE_RESPONSE)
            && mapResponse.get(WWW_AUTHENTICATE_RESPONSE) != null
            && mapResponse.get(WWW_AUTHENTICATE_RESPONSE).startsWith("Basic ");
    }
}

package com.jsql.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.Map;
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
     * @param httpRequest where decoded value will be set
     * @param header string to decode
     */
    public static void sanitizeHeaders(Builder httpRequest, SimpleEntry<String, String> header) {
        
        String keyHeader = header.getKey().trim();
        String valueHeader = header.getValue().trim();
        
        if ("Cookie".equalsIgnoreCase(keyHeader)) {
            
            // TODO enclose value in "" => Cookie: a="a"; b="b"
            httpRequest.setHeader(keyHeader, valueHeader);
            
        } else {
            
            httpRequest.setHeader(
                keyHeader,
                URLDecoder
                .decode(
                    valueHeader,
                    StandardCharsets.UTF_8
                )
                .replaceAll("[^\\p{ASCII}]", "")
            );
        }
    }

    /**
     * Verify the headers received after a request, detect authentication response and
     * send the headers to the view.
     * @param httpRequestBuilder calls URL
     * @param body 
     * @return httpResponse with response headers
     * @throws IOException when an error occurs during connection
     */
    public HttpResponse<String> checkResponseHeader(Builder httpRequestBuilder, String body) throws IOException, InterruptedException {
        
        var httpRequest = httpRequestBuilder.build();
        HttpResponse<String> httpResponse = this.injectionModel.getMediatorUtils().getConnectionUtil().getHttpClient().send(
            httpRequest,
            BodyHandlers.ofString()
        );
        String pageSource = httpResponse.body();
        
        Map<String, String> mapResponseHeaders = ConnectionUtil.getHeadersMap(httpResponse);
        
        var responseCode = Integer.toString(httpResponse.statusCode());
        
        this.checkResponse(responseCode, mapResponseHeaders);
        
        this.checkStatus(httpResponse);
        
        this.injectionModel.getMediatorUtils().getFormUtil().parseForms(httpResponse.statusCode(), pageSource);
        
        this.injectionModel.getMediatorUtils().getCsrfUtil().parseForCsrfToken(pageSource, mapResponseHeaders);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        
        int sizeHeaders = mapResponseHeaders
            .keySet()
            .stream()
            .map(key -> mapResponseHeaders.get(key).length() + key.length())
            .mapToInt(Integer::intValue)
            .sum();
        
        float size = (float) (pageSource.length() + sizeHeaders) / 1024;
        var decimalFormat = new DecimalFormat("0.000");
        msgHeader.put(Header.PAGE_SIZE, decimalFormat.format(size));
            
        msgHeader.put(Header.URL, httpRequest.uri().toURL().toString());
        msgHeader.put(Header.POST, body);
        msgHeader.put(Header.HEADER, ConnectionUtil.getHeadersMap(httpRequest.headers()));
        msgHeader.put(Header.RESPONSE, mapResponseHeaders);
        msgHeader.put(Header.SOURCE, pageSource);
        msgHeader.put(Header.METADATA_STRATEGY, "#none");
        msgHeader.put(Header.METADATA_PROCESS, "test#conn");
        
        // Inform the view about the log info
        var request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
        
        return httpResponse;
    }

    private Exception checkStatus(HttpResponse<String> response) throws IOException {
        
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
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Select option 'Disable connection test' if required");
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

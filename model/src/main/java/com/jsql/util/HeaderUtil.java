package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeaderUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    public static final String CONTENT_TYPE_REQUEST = "Content-Type";
    public static final String WWW_AUTHENTICATE_RESPONSE = "www-authenticate";
    private static final String REGEX_HTTP_STATUS = "4\\d\\d";
    private static final String FOUND_STATUS_HTTP = "Found status HTTP";

    private final InjectionModel injectionModel;
    
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

        if ("cookie".equalsIgnoreCase(keyHeader) && Pattern.compile(".+=.*").matcher(valueHeader).find()) {
            // Encode cookies to double quotes: Cookie: key="<value>"
            List<String> cookies = Stream.of(valueHeader.split(";"))
                .filter(value -> value != null && value.contains("="))
                .map(cookie -> cookie.split("=", 2))
                .map(arrayEntry -> arrayEntry[0].trim() + "=" + (arrayEntry[1] == null
                    ? "\"\""
                    // TODO Url encode: new cookie RFC restricts chars to non ()<>@,;:\"/[]?={} => server must url decode the request
                    // No url encode may work on legacy RFC
                    : "\"" + URLEncoder.encode(arrayEntry[1].trim().replaceAll("(^\\s*\")|(\"\\s*$)", "").replace("+", "%2B"), StandardCharsets.UTF_8) + "\""
                ))
                .collect(Collectors.toList());
            valueHeader = String.join("; ", cookies);
        }

        httpRequest.setHeader(
            keyHeader,
            valueHeader.replaceAll("[^\\p{ASCII}]", "")
        );
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

        List<HttpCookie> cookies = this.injectionModel.getMediatorUtils().getConnectionUtil().getCookieManager().getCookieStore().getCookies();
        if (!cookies.isEmpty()) {
            LOGGER.info("Cookies set by host: {}", cookies);
        }

        this.checkResponse(responseCode, mapResponseHeaders);
        
        this.checkStatus(httpResponse);
        
        this.injectionModel.getMediatorUtils().getFormUtil().parseForms(httpResponse.statusCode(), pageSource);

        this.injectionModel.getMediatorUtils().getCsrfUtil().parseForCsrfToken(pageSource, mapResponseHeaders);

        this.injectionModel.getMediatorUtils().getDigestUtil().parseWwwAuthenticate(mapResponseHeaders);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        
        int sizeHeaders = mapResponseHeaders.keySet()
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

    private void checkStatus(HttpResponse<String> response) {

        if (response.statusCode() >= 400) {

            if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotTestingConnection()) {

                LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Connection test disabled, skipping error {}...", response.statusCode());

            } else {

                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Try with option 'Disable connection test' to skip HTTP error {}", response.statusCode());
            }
        }
    }

    private void checkResponse(String responseCode, Map<String, String> mapResponse) {
        
        if (this.isBasicAuth(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "Basic Authentication detected: "
                + "set authentication in preferences, "
                + "or add header 'Authorization: Basic b3N..3Jk', with b3N..3Jk as "
                + "'osUserName:osPassword' encoded in Base64 (use the Coder in jSQL to encode the string)."
            );
        
        } else if (this.isNtlm(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "NTLM Authentication detected: "
                + "set authentication in preferences, "
                + "or add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        
        } else if (this.isDigest(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "Digest Authentication detected: set authentication in preferences."
            );
        
        } else if (this.isNegotiate(responseCode, mapResponse)) {
            
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "Negotiate Authentication detected: "
                + "add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
            
        } else if (Pattern.matches("1\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "{} {} Informational", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("2\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "{} {} Success", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("3\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{} {} Redirection", FOUND_STATUS_HTTP, responseCode);
            
            if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isFollowingRedirection()) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "If injection fails retry with option 'Follow HTTP redirection' activated");
                
            } else {
                
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Redirecting to the next page...");
            }
            
        } else if (Pattern.matches(REGEX_HTTP_STATUS, responseCode)) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{} {} Client Error", FOUND_STATUS_HTTP, responseCode);
            
        } else if (Pattern.matches("5\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{} {} Server Error", FOUND_STATUS_HTTP, responseCode);
            
        } else {
            
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "{} {} Unknown", FOUND_STATUS_HTTP, responseCode);
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

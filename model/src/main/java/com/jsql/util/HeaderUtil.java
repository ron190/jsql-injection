package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.view.subscriber.Seal;
import com.jsql.model.exception.JSqlException;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class HeaderUtil {
    
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
    public static void sanitizeHeaders(Builder httpRequest, SimpleEntry<String, String> header) throws JSqlException {
        String keyHeader = header.getKey().trim();
        String valueHeader = header.getValue().trim();

        if (CookiesUtil.COOKIE.equalsIgnoreCase(keyHeader) && Pattern.compile(".+=.*").matcher(valueHeader).find()) {
            // Encode cookies to double quotes: Cookie: key="<value>"
            List<String> cookies = Stream.of(valueHeader.split(";"))
                .filter(value -> value.contains("="))
                .map(cookie -> cookie.split("=", 2))
                .map(arrayEntry -> arrayEntry[0].trim() + "=" + (
                    arrayEntry[1] == null
                    ? StringUtils.EMPTY
                    // Url encode: new cookie RFC restricts chars to non ()<>@,;:\"/[]?={} => server must url decode the request
                    : URLEncoder.encode(arrayEntry[1].trim().replace("+", "%2B"), StandardCharsets.UTF_8)
                ))
                .toList();
            valueHeader = String.join("; ", cookies);
        }

        try {
            httpRequest.setHeader(
                keyHeader,
                valueHeader.replaceAll("[^\\p{ASCII}]", StringUtils.EMPTY)
            );
        } catch (IllegalArgumentException e) {
            throw new JSqlException(e);
        }
    }

    /**
     * Verify the headers received after a request, detect authentication response and
     * send the headers to the view.
     * @param httpRequestBuilder calls URL
     * @return httpResponse with response headers
     * @throws IOException when an error occurs during connection
     */
    public HttpResponse<String> checkResponseHeader(Builder httpRequestBuilder, String body) throws IOException, InterruptedException {
        var httpRequest = httpRequestBuilder.build();
        HttpResponse<String> httpResponse = this.injectionModel.getMediatorUtils().connectionUtil().getHttpClient().build().send(
            httpRequest,
            BodyHandlers.ofString()
        );
        String pageSource = httpResponse.body();
        
        List<HttpCookie> cookies = this.injectionModel.getMediatorUtils().connectionUtil().getCookieManager().getCookieStore().getCookies();
        if (!cookies.isEmpty()) {
            LOGGER.info("Cookies set by host: {}", cookies);
        }

        var responseCode = Integer.toString(httpResponse.statusCode());
        Map<String, String> mapResponseHeaders = ConnectionUtil.getHeadersMap(httpResponse);
        this.checkResponse(responseCode, mapResponseHeaders);
        this.checkStatus(httpResponse);
        
        this.injectionModel.getMediatorUtils().formUtil().parseForms(httpResponse.statusCode(), pageSource);
        this.injectionModel.getMediatorUtils().csrfUtil().parseForCsrfToken(pageSource, mapResponseHeaders);
        this.injectionModel.getMediatorUtils().digestUtil().parseWwwAuthenticate(mapResponseHeaders);

        int sizeHeaders = mapResponseHeaders.keySet()
            .stream()
            .map(key -> mapResponseHeaders.get(key).length() + key.length())
            .mapToInt(Integer::intValue)
            .sum();
        float size = (float) (pageSource.length() + sizeHeaders) / 1024;
        var decimalFormat = new DecimalFormat("0.000");

        // Inform the view about the log info
        this.injectionModel.sendToViews(new Seal.MessageHeader(
            httpRequest.uri().toURL().toString(),
            body,
            ConnectionUtil.getHeadersMap(httpRequest.headers()),
            mapResponseHeaders,
            pageSource,
            decimalFormat.format(size),
            "#none",
            "test#conn",
            null
        ));

        return httpResponse;
    }

    private void checkStatus(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            if (this.injectionModel.getMediatorUtils().preferencesUtil().isNotTestingConnection()) {
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
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "{} {} Informational", HeaderUtil.FOUND_STATUS_HTTP, responseCode);
        } else if (Pattern.matches("2\\d\\d", responseCode)) {
            LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "{} {} Success", HeaderUtil.FOUND_STATUS_HTTP, responseCode);
        } else if (Pattern.matches("3\\d\\d", responseCode)) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{} {} Redirection", HeaderUtil.FOUND_STATUS_HTTP, responseCode);
            
            if (!this.injectionModel.getMediatorUtils().preferencesUtil().isFollowingRedirection()) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "If injection fails retry with option 'Follow HTTP redirection' activated");
            } else {
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Redirecting to the next page...");
            }
        } else if (Pattern.matches(HeaderUtil.REGEX_HTTP_STATUS, responseCode)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{} {} Client Error", HeaderUtil.FOUND_STATUS_HTTP, responseCode);
        } else if (Pattern.matches("5\\d\\d", responseCode)) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "{} {} Server Error", HeaderUtil.FOUND_STATUS_HTTP, responseCode);
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "{} {} Unknown", HeaderUtil.FOUND_STATUS_HTTP, responseCode);
        }
    }
    
    private boolean isNegotiate(String responseCode, Map<String, String> mapResponse) {
        return Pattern.matches(HeaderUtil.REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(HeaderUtil.WWW_AUTHENTICATE_RESPONSE)
            && "Negotiate".equals(mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE));
    }

    private boolean isDigest(String responseCode, Map<String, String> mapResponse) {
        return Pattern.matches(HeaderUtil.REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(HeaderUtil.WWW_AUTHENTICATE_RESPONSE)
            && mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE) != null
            && mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE).startsWith("Digest ");
    }

    private boolean isNtlm(String responseCode, Map<String, String> mapResponse) {
        return Pattern.matches(HeaderUtil.REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(HeaderUtil.WWW_AUTHENTICATE_RESPONSE)
            && "NTLM".equals(mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE));
    }

    private boolean isBasicAuth(String responseCode, Map<String, String> mapResponse) {
        return Pattern.matches(HeaderUtil.REGEX_HTTP_STATUS, responseCode)
            && mapResponse.containsKey(HeaderUtil.WWW_AUTHENTICATE_RESPONSE)
            && mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE) != null
            && mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE).startsWith("Basic ");
    }
}

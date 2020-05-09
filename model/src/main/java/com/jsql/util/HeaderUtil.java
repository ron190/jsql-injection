package com.jsql.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;

public class HeaderUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
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
                
                connection.addRequestProperty(keyHeader, URLDecoder.decode(valueHeader, StandardCharsets.UTF_8.name()));
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
        
        Map<String, String> mapResponse = HeaderUtil.getHttpHeaders(connection);

        String responseCode = Integer.toString(connection.getResponseCode());
        
        this.checkResponse(responseCode, mapResponse);
        
        // Request the web page to the server
        Exception exception = null;
        
        StringBuilder pageSource = new StringBuilder();
        
        exception = this.readSource(connection, pageSource);
        
        this.parseForms(connection, pageSource);
        
        // TODO Extract CsrfUtil
        exception = this.parseCsrf(exception, pageSource);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, urlByUser);
        msgHeader.put(Header.RESPONSE, mapResponse);
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

    private Exception parseCsrf(Exception exception, StringBuilder pageSource) {
        
        Exception exceptionCsrf = exception;
        
        // TODO csrf in HTTP
        Optional<SimpleEntry<String, String>> optionalTokenCsrf = Jsoup
            .parse(pageSource.toString())
            .select("input")
            .select("[name=csrf_token], [name=csrfToken]")
            .stream()
            .findFirst()
            .map(input -> new SimpleEntry<>(input.attr("name"), input.attr("value")));
        
        if (optionalTokenCsrf.isPresent()) {
            
            SimpleEntry<String, String> tokenCsrfFound = optionalTokenCsrf.get();
            
            if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isProcessingCsrf()) {
                
                LOGGER.debug("Found Csrf token "+ tokenCsrfFound.getKey() +"="+ tokenCsrfFound.getValue() +" in HTML body, adding token to querystring, request and header");
                this.injectionModel.getMediatorUtils().getConnectionUtil().setTokenCsrf(tokenCsrfFound);
                
            } else {
                
                LOGGER.warn("Found Csrf token '"+ tokenCsrfFound.getKey() +"="+ tokenCsrfFound.getValue() +"' in HTML body");
                exceptionCsrf = new IOException("please activate Csrf processing in Preferences");
            }
        }
        
        return exceptionCsrf;
    }

    private void parseForms(HttpURLConnection connection, StringBuilder pageSource) throws IOException {
        
        Elements elementsForm = Jsoup.parse(pageSource.toString()).select("form");
        
        if (elementsForm.isEmpty()) {
            return;
        }
        
        StringBuilder result = new StringBuilder();
        
        Map<Element, List<Element>> mapForms = new HashMap<>();
        
        for (Element form: elementsForm) {
            
            mapForms.put(form, new ArrayList<>());
            
            result.append("\n<form action=\"");
            result.append(form.attr("action"));
            result.append("\" method=\"");
            result.append(form.attr("method"));
            result.append("\" />");
            
            for (Element input: form.select("input")) {
                
                result.append("\n    <input name=\"");
                result.append(input.attr("name"));
                result.append("\" value=\"");
                result.append(input.attr("value"));
                result.append("\" />");
                
                mapForms.get(form).add(input);
            }
            
            Collections.reverse(mapForms.get(form));
        }
            
        if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isParsingForm()) {
            
            this.logForms(connection, elementsForm, result);
            
        } else {
            
            this.addForms(elementsForm, result, mapForms);
        }
    }

    private void addForms(Elements elementsForm, StringBuilder result, Map<Element, List<Element>> mapForms) {
        
        LOGGER.debug("Found "+ elementsForm.size() +" <form> in HTML body, adding input(s) to requests:"+ result);
        
        for(Entry<Element, List<Element>> form: mapForms.entrySet()) {
            
            for (Element input: form.getValue()) {
                
                if ("get".equalsIgnoreCase(form.getKey().attr("method"))) {
                    
                    this.injectionModel.getMediatorUtils().getParameterUtil().getListQueryString().add(0, new SimpleEntry<>(input.attr("name"), input.attr("value")));
                    
                } else if ("post".equalsIgnoreCase(form.getKey().attr("method"))) {
                    
                    this.injectionModel.getMediatorUtils().getParameterUtil().getListRequest().add(0, new SimpleEntry<>(input.attr("name"), input.attr("value")));
                }
            }
        }
    }

    private void logForms(HttpURLConnection connection, Elements elementsForm, StringBuilder result) throws IOException {
        
        if (connection.getResponseCode() != 200) {
            
            LOGGER.trace("Found "+ elementsForm.size() +" ignored <form> in HTML body:"+ result);
            LOGGER.info("WAF can detect missing form parameters, you may enable 'Add <input> parameters' in Preferences and retry");
            
        } else {
            
            LOGGER.trace("Found "+ elementsForm.size() +" <form> in HTML body while status 200 Success:"+ result);
        }
    }

    private Exception readSource(HttpURLConnection connection, StringBuilder pageSource) throws IOException {
        
        Exception exception = null;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            
            char[] buffer = new char[4096];
            while (reader.read(buffer) > 0) {
                pageSource.append(buffer);
            }
            
        } catch (IOException errorInputStream) {
            
            exception = errorInputStream;
            
            InputStream errorStream = connection.getErrorStream();
            
            if (errorStream != null) {
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                    
                    char[] buffer = new char[4096];
                    while (reader.read(buffer) > 0) {
                        pageSource.append(buffer);
                    }
                    
                } catch (Exception errorErrorStream) {
                    exception = new IOException("Exception reading Error Stream", errorErrorStream);
                }
            }
        }
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotTestingConnection()) {
            
            if (exception != null) {
                LOGGER.debug("Connection test disabled, ignoring response HTTP "+ connection.getResponseCode() +"...");
            }
            
            exception = null;
            
        } else if (exception != null) {
            
            LOGGER.info("Please select option 'Disable connection test' and run again");
        }
        
        return exception;
    }

    private void checkResponse(String responseCode, Map<String, String> mapResponse) {
        
        if (this.isBasicAuth(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "Basic Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences.\n"
                + "Or open Advanced panel, add 'Authorization: Basic b3N..3Jk' to the Header, replace b3N..3Jk with the string 'osUserName:osPassword' encoded in Base64. You can use the Coder in jSQL to encode the string."
            );
        
        } else if (this.isNtlm(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "NTLM Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences.\n"
                + "Or add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        
        } else if (this.isDigest(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "Digest Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences."
            );
        
        } else if (this.isNegotiate(responseCode, mapResponse)) {
            
            LOGGER.warn(
                "Negotiate Authentication detected.\n"
                + "Please add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
            
        } else if (Pattern.matches("1\\d\\d", responseCode)) {
            
            LOGGER.trace(FOUND_STATUS_HTTP+ responseCode +" Informational");
            
        } else if (Pattern.matches("2\\d\\d", responseCode)) {
            
            LOGGER.debug(FOUND_STATUS_HTTP+ responseCode +" Success");
            
        } else if (Pattern.matches("3\\d\\d", responseCode)) {
            
            LOGGER.warn(FOUND_STATUS_HTTP+ responseCode +" Redirection");
            
            if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isFollowingRedirection()) {
                
                LOGGER.warn("If injection fails please test again with option 'Follow HTTP redirection' enabled.");
                
            } else {
                
                LOGGER.info("Redirecting to the next page...");
            }
            
        } else if (Pattern.matches(REGEX_HTTP_STATUS, responseCode)) {
            
            LOGGER.warn(FOUND_STATUS_HTTP+ responseCode +" Client Error");
            
        } else if (Pattern.matches("5\\d\\d", responseCode)) {
            
            LOGGER.warn(FOUND_STATUS_HTTP+ responseCode +" Server Error");
            
        } else {
            
            LOGGER.trace(FOUND_STATUS_HTTP+ responseCode +" Unknown");
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
        
        for (Map.Entry<String, List<String>> entries: connection.getHeaderFields().entrySet()) {
            
            mapHeaders.put(entries.getKey() == null ? "Status code" : entries.getKey(), String.join(",", entries.getValue()));
        }

        return mapHeaders;
    }
}

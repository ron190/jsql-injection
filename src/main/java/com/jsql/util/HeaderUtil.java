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

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;

public class HeaderUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    // Utility class
    private HeaderUtil() {
        // Nothing
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
    @SuppressWarnings("unchecked")
    public static void checkResponseHeader(HttpURLConnection connection, String urlByUser) throws IOException {
        // TODO Extract
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, urlByUser);
        msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));

        Map<String, String> mapResponse = (Map<String, String>) msgHeader.get(Header.RESPONSE);
        if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && mapResponse.get("WWW-Authenticate") != null
            && mapResponse.get("WWW-Authenticate").startsWith("Basic ")
        ) {
            LOGGER.warn(
                "Basic Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences.\n"
                + "Or open Advanced panel, add 'Authorization: Basic b3N..3Jk' to the Header, replace b3N..3Jk with the string 'osUserName:osPassword' encoded in Base64. You can use the Coder in jSQL to encode the string."
            );
        
        } else if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && "NTLM".equals(mapResponse.get("WWW-Authenticate"))
        ) {
            LOGGER.warn(
                "NTLM Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences.\n"
                + "Or add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
        
        } else if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && mapResponse.get("WWW-Authenticate") != null
            && mapResponse.get("WWW-Authenticate").startsWith("Digest ")
        ) {
            LOGGER.warn(
                "Digest Authentication detected.\n"
                + "Please define and enable authentication information in the panel Preferences."
            );
        
        } else if (
            Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))
            && mapResponse.containsKey("WWW-Authenticate")
            && "Negotiate".equals(mapResponse.get("WWW-Authenticate"))
        ) {
            LOGGER.warn(
                "Negotiate Authentication detected.\n"
                + "Please add username, password and domain information to the URL, e.g. http://domain\\user:password@127.0.0.1/[..]"
            );
            
        } else if (Pattern.matches("1\\d\\d", Integer.toString(connection.getResponseCode()))) {
            LOGGER.trace("Found status HTTP "+ connection.getResponseCode() +" Informational");
            
        } else if (Pattern.matches("2\\d\\d", Integer.toString(connection.getResponseCode()))) {
            LOGGER.debug("Found status HTTP "+ connection.getResponseCode() +" Success");
            
        } else if (Pattern.matches("3\\d\\d", Integer.toString(connection.getResponseCode()))) {
            LOGGER.warn("Found status HTTP "+ connection.getResponseCode() +" Redirection");
            
            if (!PreferencesUtil.isFollowingRedirection()) {
                LOGGER.warn("If injection fails please test again with option 'Follow HTTP redirection' enabled.");
            } else {
                LOGGER.info("Redirecting to the next page...");
            }
            
        } else if (Pattern.matches("4\\d\\d", Integer.toString(connection.getResponseCode()))) {
            LOGGER.warn("Found status HTTP "+ connection.getResponseCode() +" Client Error");
            
        } else if (Pattern.matches("5\\d\\d", Integer.toString(connection.getResponseCode()))) {
            LOGGER.warn("Found status HTTP "+ connection.getResponseCode() +" Server Error");
            
        } else {
            LOGGER.trace("Found status HTTP "+ connection.getResponseCode() +" Unknown");
            
        }
        
        // Request the web page to the server
        Exception exception = null;
        
        StringBuilder pageSource = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            char[] buffer = new char[4096];
            while (reader.read(buffer) > 0) {
                pageSource.append(buffer);
            }
            reader.close();
            
        } catch (IOException errorInputStream) {
            exception = errorInputStream;
            
            InputStream errorStream = connection.getErrorStream();
            
            if (errorStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                    char[] buffer = new char[4096];
                    while (reader.read(buffer) > 0) {
                        pageSource.append(buffer);
                    }
                    reader.close();
                    
                } catch (Exception errorErrorStream) {
                    exception = new IOException("Exception reading Error Stream", errorErrorStream);
                }
            }
        }
        
        // Connection test
        
        if (PreferencesUtil.isNotTestingConnection()) {
            if (exception != null) {
                LOGGER.debug("Connection test disabled, ignoring response HTTP "+ connection.getResponseCode() +"...");
            }
            exception = null;
        } else if (exception != null) {
            LOGGER.info("Please select option 'Disable connection test' and run again");
        }
        
        // Form parsing;
        // TODO Extract
        Elements elementsForm = Jsoup.parse(pageSource.toString()).select("form");
        
        StringBuilder result = new StringBuilder();
        
        Map<Element, List<Element>> p = new HashMap<>();
        for (Element form: elementsForm) {
            p.put(form, new ArrayList<>());
            
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
                
                p.get(form).add(input);
            }
            
            Collections.reverse(p.get(form));
            
        }
        
        if (!elementsForm.isEmpty()) {
            if (!PreferencesUtil.isParsingForm()) {
                if (connection.getResponseCode() != 200) {
                    LOGGER.trace("Found "+ elementsForm.size() +" ignored <form> in HTML body:"+ result);
                    LOGGER.info("WAF can detect missing form parameters, you may enable 'Add <input> parameters' in Preferences and retry");
                } else {
                    LOGGER.trace("Found "+ elementsForm.size() +" <form> in HTML body while status 200 Success:"+ result);
                }
            } else {
                LOGGER.debug("Found "+ elementsForm.size() +" <form> in HTML body, adding input(s) to requests:"+ result);
                
                for(Entry<Element, List<Element>> form: p.entrySet()) {
                    for (Element input: form.getValue()) {
                        if ("get".equalsIgnoreCase(form.getKey().attr("method"))) {
                            ParameterUtil.getQueryString().add(0, new SimpleEntry<String, String>(input.attr("name"), input.attr("value")));
                        } else if ("post".equalsIgnoreCase(form.getKey().attr("method"))) {
                            ParameterUtil.getRequest().add(0, new SimpleEntry<String, String>(input.attr("name"), input.attr("value")));
                        }
                    }
                }
            }
        }
        
        // Csrf
        // TODO Extract
        Optional<SimpleEntry<String, String>> optionalTokenCsrf = Jsoup
        .parse(pageSource.toString())
        .select("input")
        .select("[name=csrf_token], [name=csrfToken]")
        .stream()
        .findFirst()
        .map(input -> new SimpleEntry<String, String>(input.attr("name"), input.attr("value")));
        
        if (optionalTokenCsrf.isPresent()) {
            SimpleEntry<String, String> tokenCsrfFound = optionalTokenCsrf.get();
            
            if (PreferencesUtil.isProcessingCsrf()) {
                LOGGER.debug("Found Csrf token "+ tokenCsrfFound.getKey() +"="+ tokenCsrfFound.getValue() +" in HTML body, adding token to querystring, request and header");
                ConnectionUtil.setTokenCsrf(tokenCsrfFound);
            } else {
                LOGGER.warn("Found Csrf token '"+ tokenCsrfFound.getKey() +"="+ tokenCsrfFound.getValue() +"' in HTML body");
                exception = new IOException("please activate Csrf processing in Preferences");
            }
        }

        msgHeader.put(Header.SOURCE, pageSource.toString());
        
        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
        
        if (exception != null) {
            throw new IOException(exception);
        }
    }
    
    /**
     * Extract HTTP headers from a connection.
     * @param connection Connection with HTTP headers
     * @return Map of HTTP headers <name, value>
     */
    public static Map<String, String> getHttpHeaders(URLConnection connection) {
        Map<String, String> mapHeaders = new HashMap<>();
        
        for (int i = 0 ; ; i++) {
            // Fix #6456: IllegalArgumentException on getHeaderFieldKey()
            // Implementation by sun.net.www.protocol.http.HttpURLConnection.getHeaderFieldKey()
            try {
                String headerName = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);
                if (headerName == null && headerValue == null) {
                    break;
                }
                mapHeaders.put(headerName == null ? "Method" : headerName, headerValue);
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return mapHeaders;
    }

}

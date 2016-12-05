/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;

/**
 * String operations missing like join().
 */
public final class StringUtil {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Utility class.
     */
    private StringUtil() {
        //not called
    }
    
    /**
     * Concatenate strings with separator.
     * @param strings Array of strings to join
     * @param separator String that concatenate two values of the array
     * @return Joined string
     */
    public static String join(String[] strings, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < strings.length ; i++) {
            if (i != 0) {
                sb.append(separator);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    /**
     * Convert a hexadecimal String to String.
     * @param hex Hexadecimal String to convert
     * @return The string converted from hex
     */
    public static String hexstr(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0 ; i < bytes.length ; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return new String(bytes);
    }

    /**
     * Convert a String to a hexadecimal String.
     * @param arg The string to convert
     * @return Hexadecimal String conversion
     */
    public static String strhex(String arg) {
        return String.format("%x", new BigInteger(arg.getBytes()));
    }
    
    /**
     * Extract HTTP headers from a connection.
     * @param conn Connection with HTTP headers
     * @return Map of HTTP headers <name, value>
     */
    public static Map<String, String> getHttpHeaders(URLConnection conn) {
        Map<String, String> mapHeaders = new HashMap<>();
        
        for (int i = 0 ; ; i++) {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName == null && headerValue == null) {
                break;
            }
            mapHeaders.put(headerName == null ? "Method" : headerName, headerValue);
        }

        return mapHeaders;
    }

    @SuppressWarnings("unchecked")
    public static void sendMessageHeader(HttpURLConnection connection, String url) throws IOException {
        Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
        msgHeader.put(TypeHeader.URL, url);
        msgHeader.put(TypeHeader.RESPONSE, StringUtil.getHttpHeaders(connection));

        if (
            !PreferencesUtil.isFollowingRedirection()
            && Pattern.matches("3\\d\\d", Integer.toString(connection.getResponseCode()))
        ) {
            LOGGER.warn("HTTP 3XX Redirection detected. Please test again with option 'Follow HTTP redirection' enabled.");
        }
        
        Map<String, String> mapResponse = (Map<String, String>) msgHeader.get(TypeHeader.RESPONSE);
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
        }
        
        // Request the web page to the server
        String pageSource = "";
        Exception exception = null;
        
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\r\n";
            }
        } catch (IOException e) {
            exception = e;
        }

        msgHeader.put(TypeHeader.SOURCE, pageSource);
        
        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage(TypeRequest.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
        
        if (exception != null) {
            throw new IOException(exception);
        }
    }
}

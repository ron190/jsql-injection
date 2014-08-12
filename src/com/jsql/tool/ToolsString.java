/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.tool;

import java.math.BigInteger;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * String operations missing like join().
 */
public final class ToolsString {
    /**
     * Utility class.
     */
    private ToolsString() {
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
        for (int i = 0; i < strings.length; i++) {
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
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        String multi = new String(bytes);
        return multi;
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
    public static Map<String, String> getHTTPHeaders(URLConnection conn) {
        Map<String, String> msgResponse = new HashMap<String, String>();
        
        for (int i = 0;; i++) {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName == null && headerValue == null) {
                break;
            }
            msgResponse.put(headerName == null ? "Method" : headerName, headerValue);
        }

        return msgResponse;
    }
}

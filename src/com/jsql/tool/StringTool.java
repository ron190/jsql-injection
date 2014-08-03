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

/**
 * String operations lacking from java, like join()
 */
public class StringTool {
    /**
     * Concatenate strings with separator
     * @param strings Array of strings to join
     * @param separator String that concatenate two values of the array
     * @return
     */
    public static String join(String[] strings, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < strings.length; i++) {
            if (i != 0) sb.append(separator);
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    /**
     * Convert a hexadecimal String to String
     * @param arg Hexadecimal String to convert
     * @return The string converted from hex
     */
    public static String hexstr(String hex){
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        String multi = new String(bytes);
        return multi;
    }

    /**
     * Convert a String to a hexadecimal String
     * @param arg The string to convert
     * @return Hexadecimal String conversion
     */
    public static String strhex(String arg) {
        return String.format("%x", new BigInteger(arg.getBytes()));
    }
}

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

import java.nio.charset.StandardCharsets;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Utility class adding String operations like join() which are not
 * part of standard JVM.
 */
public final class StringUtil {
    
    /**
     * Define the schema of convertion to html entities.
     */
    private static final CharEncoder DECIMAL_HTML_ENCODER = new CharEncoder("&#", ";", 10);
    
    /**
     * This utility class defines a schema used to encode a text into a specialized
     * representation
     */
    private static class CharEncoder {
    	
        String prefix;
        String suffix;
        int radix;
        
        public CharEncoder(String prefix, String suffix, int radix) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.radix = radix;
        }
        
        void encode(char c, StringBuilder buff) {
            buff
            	.append(this.prefix)
            	.append(Integer.toString(c, this.radix))
            	.append(this.suffix);
        }
        
    }
	
    // Utility class.
    private StringUtil() {
        // not called
    }
    
    /**
     * Convert special characters like Chinese and Arabic letters to the corresponding html entities.
     * @param text string to encode
     * @return string encoded in html entities
     * TODO create specialized class
     */
    public static String decimalHtmlEncode(String text) {
        return StringUtil.encode(text, DECIMAL_HTML_ENCODER);
    }
    
    /**
     * Non trivial methods to convert unicode characters to html entities.
     * @param text string to encode
     * @param encoder schema of encoding
     * @return string representation using the encoder schema
     */
    private static String encode(String text, CharEncoder encoder) {
        StringBuilder buff = new StringBuilder();
        for ( int i = 0 ; i < text.length() ; i++) {
            if (text.charAt(i) > 128) {
                encoder.encode(text.charAt(i), buff);
            } else {
                buff.append(text.charAt(i));
            }
        }
        return ""+ buff;
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
    
    public static String detectUtf8Html(String text) {
        return StringUtil.detectUtf8Html(text, false);
    }
    
    public static String detectUtf8HtmlNoWrap(String text) {
        return StringUtil.detectUtf8Html(text, true);
    }
    
    public static String detectUtf8Html(String text, Boolean nowrap) {
        // Fix #35217: NullPointerException on getBytes()
        if (text == null) {
            return "";
        }
        
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(text.getBytes(), 0, text.length() - 1);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        
        String result = text;
        if (encoding != null) {
            result = "<html><span style=\"font-family:'Monospace';"+( nowrap ? "white-space:nowrap;" : "" )+"\">"+ new String(text.getBytes(), StandardCharsets.UTF_8) +"</span></html>";
        }
        
        return result;
    }
    
    public static String detectUtf8(String text) {
        if (text == null) {
            return "";
        }
        
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(text.getBytes(), 0, text.length() - 1);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        
        String result = text;
        if (encoding != null) {
            result = new String(text.getBytes(), StandardCharsets.UTF_8);
        }
        
        return result;
    }
    
}

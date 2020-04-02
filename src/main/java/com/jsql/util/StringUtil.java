/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.mozilla.universalchardet.UniversalDetector;

import com.jsql.view.swing.util.UiUtil;

/**
 * Utility class adding String operations like join() which are not
 * part of standard JVM.
 */
public final class StringUtil {
    
    /**
     * Define the schema of conversion to html entities.
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
     */
    public static String decimalHtmlEncode(String text) {
        
        return decimalHtmlEncode(text, false);
    }
    
    public static String decimalHtmlEncode(String text, boolean isRaw) {
        
        String result = StringUtil.encode(text, DECIMAL_HTML_ENCODER);
        
        if (isRaw) {
            return result.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
        } else {
            return result;
        }
    }
    
    /**
     * Non trivial methods to convert unicode characters to html entities.
     * @param text string to encode
     * @param encoder schema of encoding
     * @return string representation using the encoder schema
     */
    private static String encode(String text, CharEncoder encoder) {
        
        StringBuilder buff = new StringBuilder();
        for (int i = 0 ; i < text.length() ; i++) {
            if (text.charAt(i) > 128) {
                encoder.encode(text.charAt(i), buff);
            } else {
                buff.append(text.charAt(i));
            }
        }
        
        return ""+ buff;
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
    
    public static String detectUtf8Html(String text, boolean nowrap) {
        
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
            // TODO move to View, remove from model
            result = "<html><span style=\"font-family:'"+ UiUtil.FONT_NAME_UBUNTU_REGULAR +"';"+( nowrap ? "white-space:nowrap;" : "" )+"\">"+ new String(text.getBytes(), StandardCharsets.UTF_8) +"</span></html>";
        }
        
        return result;
    }
    
    public static boolean isUtf8(String text) {
        
        if (text == null) {
            return false;
        }
        
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(text.getBytes(), 0, text.length() - 1);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        
        return encoding != null;
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

    /**
     * Adapter method for base64 decode.
     * @param s base64 decode
     * @return Base64 decoded string
     */
    public static String base64Decode(String s) {
        
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    /**
     * Adapter method for base64 encode.
     * @param s String to base64 encode
     * @return Base64 encoded string
     */
    public static String base64Encode(String s) {
        
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    /**
     * Zip a string.
     * @param str Text to zip
     * @return Zipped string
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        
        return out.toString("ISO-8859-1");
    }

    /**
     * Unzip a String encoded from base64 or hexadecimal.
     * @param str String to unzip
     * @return String unzipped
     * @throws IOException
     */
    public static String decompress(String str) throws IOException {
        
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        
        final String encode = "ISO-8859-1";
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes(encode)));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, encode));

        char[] buff = new char[1024];
        int read;
        StringBuilder response = new StringBuilder();
        
        while ((read = bf.read(buff)) != -1) {
            response.append(buff, 0, read);
        }
        
        return response.toString();
    }
    
    public static String toHex(String text) throws UnsupportedEncodingException {
        
        return Hex.encodeHexString(text.getBytes(StandardCharsets.UTF_8.name())).trim();
    }
    
    public static String fromHex(String text) throws UnsupportedEncodingException, DecoderException {
        
        byte[] hex = Hex.decodeHex(text.toCharArray());
        
        return new String(hex, StandardCharsets.UTF_8.name());
    }
    
    public static String toHexZip(String text) throws IOException {
        
        byte[] zip = StringUtil.compress(text).getBytes(StandardCharsets.UTF_8.name());
        
        return Hex.encodeHexString(zip).trim();
    }
    
    public static String fromHexZip(String text) throws IOException, DecoderException {
        
        byte[] hex = Hex.decodeHex(text.toCharArray());
        String zip = new String(hex, StandardCharsets.UTF_8.name());
        
        return StringUtil.decompress(zip);
    }
    
    public static String toBase64Zip(String text) throws IOException {
        
        return StringUtil.base64Encode(StringUtil.compress(text));
    }
    
    public static String fromBase64Zip(String text) throws IOException {
        
        return StringUtil.decompress(StringUtil.base64Decode(text));
    }
    
    public static String toHtml(String text) {
        
        return StringEscapeUtils
            .escapeHtml4(text)
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("&", "&amp;");
    }
    
    public static String fromHtml(String text) {
        
        return StringEscapeUtils
            .unescapeHtml4(text)
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
    
    public static String toUrl(String text) throws UnsupportedEncodingException {
        
        return URLEncoder.encode(text, StandardCharsets.UTF_8.name());
    }
    
    public static String fromUrl(String text) throws UnsupportedEncodingException {
        
        return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
    }
}

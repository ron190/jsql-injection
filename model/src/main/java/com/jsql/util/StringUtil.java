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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import com.jsql.util.bruter.Base16;
import com.jsql.util.bruter.Base58;

/**
 * Utility class adding String operations like join() which are not
 * part of standard JVM.
 */
public final class StringUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    // Define the schema of conversion to html entities
    private static final CharEncoder DECIMAL_HTML_ENCODER = new CharEncoder("&#", ";", 10);
    
    /**
     * This utility class defines a schema used to encode a text into a specialized
     * representation
     */
    private static class CharEncoder {
        
        private String prefix;
        private String suffix;
        private int radix;
        
        public CharEncoder(String prefix, String suffix, int radix) {
            
            this.prefix = prefix;
            this.suffix = suffix;
            this.radix = radix;
        }
        
        protected void encode(char c, StringBuilder buff) {
            
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
        
        var result = StringUtil.encode(text, DECIMAL_HTML_ENCODER);
        
        if (isRaw) {
            
            return
                result
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("&", "&amp;");
            
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
        
        var buff = new StringBuilder();
        
        for (var i = 0 ; i < text.length() ; i++) {
            
            if (text.charAt(i) > 128) {
                
                encoder.encode(text.charAt(i), buff);
                
            } else {
                
                buff.append(text.charAt(i));
            }
        }
        
        return buff.toString();
    }

    /**
     * Convert a hexadecimal String to String.
     * @param hex Hexadecimal String to convert
     * @return The string converted from hex
     */
    public static String hexstr(String hex) {
        
        var bytes = new byte[hex.length() / 2];
        
        for (var i = 0 ; i < bytes.length ; i++) {
            
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        
        return new String(bytes);
    }
    
    public static boolean isUtf8(String text) {
        
        if (text == null) {
            
            return false;
        }
        
        var detector = new UniversalDetector(null);
        detector.handleData(text.getBytes(), 0, text.length() - 1);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        
        return encoding != null;
    }
    
    public static String detectUtf8(String text) {
        
        if (text == null) {
            
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        
        String encoding = null;
        
        // ArrayIndexOutOfBoundsException on handleData()
        try {
            var detector = new UniversalDetector(null);
            detector.handleData(text.getBytes(), 0, text.length() - 1);
            detector.dataEnd();
            encoding = detector.getDetectedCharset();
            
        } catch (ArrayIndexOutOfBoundsException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        String result = text;
        if (encoding != null) {
            
            result = new String(text.getBytes(), StandardCharsets.UTF_8);
        }
        
        return result;
    }
    
    public static String base32Encode(String s) {
        
        var base32 = new Base32();
        return base32.encodeToString(StringUtils.getBytesUtf8(s));
    }
    
    public static String base32Decode(String s) {
        
        var base32 = new Base32();
        return StringUtils.newStringUtf8(base32.decode(s));
    }
    
    public static String base58Encode(String s) {
        
        return Base58.encode(StringUtils.getBytesUtf8(s));
    }
    
    public static String base58Decode(String s) {
        
        return StringUtils.newStringUtf8(Base58.decode(s));
    }
    
    public static String base16Encode(String s) {
        
        var base16 = new Base16();
        return base16.encodeToString(StringUtils.getBytesUtf8(s));
    }
    
    public static String base16Decode(String s) {
        
        var base16 = new Base16();
        return StringUtils.newStringUtf8(base16.decode(s));
    }

    /**
     * Adapter method for base64 decode.
     * @param s base64 decode
     * @return Base64 decoded string
     */
    public static String base64Decode(String s) {
        
        // org.apache.commons.codec.binary.Base64 fails on RlRQIHVzZXI6IG
        // Use java.util.Base64 instead
        return StringUtils.newStringUtf8(Base64.getDecoder().decode(s));
    }

    /**
     * Adapter method for base64 encode.
     * @param s String to base64 encode
     * @return Base64 encoded string
     */
    public static String base64Encode(String s) {
        
        // org.apache.commons.codec.binary.Base64 fails on RlRQIHVzZXI6IG
        // Use java.util.Base64 instead
        return Base64.getEncoder().encodeToString(StringUtils.getBytesUtf8(s));
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
        
        var out = new ByteArrayOutputStream();
        var gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        
        return out.toString(StandardCharsets.ISO_8859_1.name());
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
        
        final var encode = "ISO-8859-1";
        var gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes(encode)));
        var bf = new BufferedReader(new InputStreamReader(gis, encode));

        var buff = new char[1024];
        int read;
        var response = new StringBuilder();
        
        while ((read = bf.read(buff)) != -1) {
            response.append(buff, 0, read);
        }
        
        return response.toString();
    }
    
    public static String toHex(String text) {
        
        return Hex.encodeHexString(text.getBytes(StandardCharsets.UTF_8)).trim();
    }
    
    public static String fromHex(String text) throws DecoderException {
        
        byte[] hex = Hex.decodeHex(text.toCharArray());
        
        return new String(hex, StandardCharsets.UTF_8);
    }
    
    public static String toHexZip(String text) throws IOException {
        
        byte[] zip = StringUtil.compress(text).getBytes(StandardCharsets.UTF_8);
        
        return Hex.encodeHexString(zip).trim();
    }
    
    public static String fromHexZip(String text) throws IOException, DecoderException {
        
        byte[] hex = Hex.decodeHex(text.toCharArray());
        var zip = new String(hex, StandardCharsets.UTF_8);
        
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
    
    public static String toUrl(String text) {
        
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
    
    public static String fromUrl(String text) {
        
        return URLDecoder.decode(text, StandardCharsets.UTF_8);
    }
    
    public static String clean(String query) {
        
        return
            query
            // Remove SQL comments except tamper /**/ /*!...*/
            // Negative lookahead: don't match tamper empty comment /**/ or version comment /*!...*/
            // JavaScript: (?!\/\*!.*\*\/|\/\*\*\/)\/\*.*\*\/
            .replaceAll("(?s)(?!/\\*\\*/|/\\*!.*\\*/)/\\*.*?\\*/", org.apache.commons.lang3.StringUtils.EMPTY)
            // Remove spaces after a word
            .replaceAll("(?s)([^\\s\\w])(\\s+)", "$1")
            // Remove spaces before a word
            .replaceAll("(?s)(\\s+)([^\\s\\w])", "$2")
            // Replace spaces
            .replaceAll("(?s)\\s+", " ")
        ;
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.util;

import com.jsql.util.bruter.Base16;
import com.jsql.util.bruter.Base58;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

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
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String INFORMATION_SCHEMA = "information_schema";
    public static final String APP_NAME = "jSQL Injection";

    /**
     * This utility class defines a schema used to encode a text into a specialized
     * representation
     */
    private static class CharEncoder {
        
        private final String prefix;
        private final String suffix;
        private final int radix;
        
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

    private StringUtil() {
        // Utility class
    }
    
    /**
     * Convert special characters like Chinese and Arabic letters to the corresponding html entities.
     * @param text string to encode
     * @return string encoded in html entities
     */
    public static String toHtmlDecimal(String text) {
        return StringUtil.encode(text);
    }
    
    /**
     * Non-trivial methods to convert unicode characters to html entities.
     * @param text string to encode
     * @return string representation using the encoder schema
     */
    private static String encode(String text) {
        var buff = new StringBuilder();
        for (var i = 0 ; i < text.length() ; i++) {
            if (text.charAt(i) > 128) {
                StringUtil.DECIMAL_HTML_ENCODER.encode(text.charAt(i), buff);
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
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    public static boolean isUtf8(String text) {
        if (text == null) {
            return false;
        }
        
        var detector = new UniversalDetector(null);
        detector.handleData(text.getBytes(StandardCharsets.UTF_8), 0, text.length() - 1);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        return encoding != null;
    }
    
    public static String detectUtf8(String text) {
        if (text == null) {
            return StringUtils.EMPTY;
        }
        
        String encoding = null;
        
        // ArrayIndexOutOfBoundsException on handleData()
        try {
            var detector = new UniversalDetector(null);
            detector.handleData(text.getBytes(StandardCharsets.UTF_8), 0, text.length() - 1);
            detector.dataEnd();
            encoding = detector.getDetectedCharset();
            
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        
        String result = text;
        if (encoding != null) {
            result = new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
        return result;
    }
    
    public static String base32Encode(String s) {
        var base32 = new Base32();
        return base32.encodeToString(StringUtil.getBytesUtf8(s));
    }
    
    public static String base32Decode(String s) {
        var base32 = new Base32();
        return StringUtil.newStringUtf8(base32.decode(s));
    }
    
    public static String base58Encode(String s) {
        return Base58.encode(StringUtil.getBytesUtf8(s));
    }
    
    public static String base58Decode(String s) {
        return StringUtil.newStringUtf8(Base58.decode(s));
    }
    
    public static String base16Encode(String s) {
        var base16 = new Base16();
        return base16.encodeToString(StringUtil.getBytesUtf8(s));
    }
    
    public static String base16Decode(String s) {
        var base16 = new Base16();
        return StringUtil.newStringUtf8(base16.decode(s));
    }

    public static String base64Decode(String s) {
        return StringUtil.newStringUtf8(Base64.getDecoder().decode(s));
    }

    public static String base64Encode(String s) {
        return Base64.getEncoder().encodeToString(StringUtil.getBytesUtf8(s));
    }

    public static String toHex(String text) {
        return StringUtil.encodeHexString(text.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String fromHex(String text) {
        byte[] hex = StringUtil.decodeHexString(text);
        return new String(hex, StandardCharsets.UTF_8);
    }
    
    public static String toHexZip(String text) throws IOException {
        byte[] zip = StringUtil.compress(text);
        return StringUtil.encodeHexString(zip);
    }

    public static String fromHexZip(String text) throws IOException {
        return new String(StringUtil.decompress(StringUtil.decodeHexString(text)), StandardCharsets.UTF_8);
    }
    
    public static String toBase64Zip(String text) throws IOException {
        return new String(Base64.getEncoder().encode(StringUtil.compress(text)));
    }
    
    public static String fromBase64Zip(String text) throws IOException {
        byte[] decompressedBArray = StringUtil.decompress(Base64.getDecoder().decode(text));
        return new String(decompressedBArray, StandardCharsets.UTF_8);
    }
    
    public static String toHtml(String text) {
        return StringEscapeUtils.escapeHtml4(text);
    }
    
    public static String fromHtml(String text) {
        return StringEscapeUtils.unescapeHtml4(text);
    }
    
    public static String toUrl(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
    
    public static String fromUrl(String text) {
        return URLDecoder.decode(text, StandardCharsets.UTF_8);
    }
    
    public static String cleanSql(String query) {
        return StringUtil.removeSqlComment(query)
            .replaceAll("(?s)([^\\s\\w])(\\s+)", "$1")  // Remove spaces after a word
            .replaceAll("(?s)(\\s+)([^\\s\\w])", "$2")  // Remove spaces before a word
            .replaceAll("(?s)\\s+", " ")  // Replace spaces
            .trim();
    }

    /**
     * Remove SQL comments except tamper /**\/ /*!...*\/
     * Negative lookahead: don't match tamper empty comment /**\/ or version comment /*!...*\/
     * JavaScript: (?!\/\*!.*\*\/|\/\*\*\/)\/\*.*\*\/
     */
    public static String removeSqlComment(String query) {
        return query.replaceAll(
            "(?s)(?!/\\*\\*/|/\\*!.*\\*/)/\\*.*?\\*/",
            StringUtils.EMPTY
        );
    }

    public static String formatReport(Color color, String text) {
        return String.format(
            "<span style=color:rgb(%s,%s,%s)>%s</span>",
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            text
        );
    }


    // Utils

    private static byte[] compress(String text) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
            dos.write(text.getBytes());
        }
        return os.toByteArray();
    }

    private static byte[] decompress(byte[] compressedTxt) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (OutputStream ios = new InflaterOutputStream(os)) {
            ios.write(compressedTxt);
        }
        return os.toByteArray();
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = StringUtil.toDigit(hexString.charAt(0));
        int secondDigit = StringUtil.toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private static String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuffer.append(StringUtil.byteToHex(b));
        }
        return hexStringBuffer.toString();
    }

    private static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException("Invalid hexadecimal String supplied.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = StringUtil.hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private static byte[] getBytesUtf8(String string) {
        return string == null ? null : string.getBytes(StandardCharsets.UTF_8);
    }

    private static String newStringUtf8(byte[] bytes) {
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }
}

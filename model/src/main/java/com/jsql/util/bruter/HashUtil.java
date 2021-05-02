package com.jsql.util.bruter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.jsql.util.StringUtil;

public class HashUtil {
    
    private HashUtil() {
        // Nothing
    }
    
    public static String toAdler32(String text) {

        byte[] bytes = text.getBytes();
        Checksum checksum = new java.util.zip.Adler32();
        checksum.update(bytes,0,bytes.length);
        
        return String.valueOf(checksum.getValue());
    }
    
    public static String toCrc16(String text) {
        
        return Crc16Helper.generateCRC16(text);
    }
    
    public static String toCrc64(String text) {
        
        return Crc64Helper.generateCRC64(text.getBytes());
    }
    
    public static String toMySql(String textInput) throws NoSuchAlgorithmException {
        
        var md = MessageDigest.getInstance("sha-1");
        
        var password = String.valueOf(textInput.toCharArray());
        
        byte[] passwordBytes = password.getBytes();
        md.update(passwordBytes, 0, passwordBytes.length);
        
        byte[] hashSHA1 = md.digest();
        var stringSHA1 = HashUtil.digestToHexString(hashSHA1);
        
        var passwordSHA1 = String.valueOf(StringUtil.hexstr(stringSHA1).toCharArray());
        byte[] passwordSHA1Bytes = passwordSHA1.getBytes();
        
        md.update(passwordSHA1Bytes, 0, passwordSHA1Bytes.length);
        byte[] hashSHA1SH1 = md.digest();
        
        return HashUtil.digestToHexString(hashSHA1SH1);
    }

    public static String toCrc32(String textInput) {
        
        byte[] bytes = textInput.getBytes();
        
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        
        long lngChecksum = checksum.getValue();
        
        return Long.toString(lngChecksum);
    }

    public static String toMd4(String textInput) {
        
        MessageDigest md = new DigestMD4();

        var passwordString = String.valueOf(textInput.toCharArray());
        byte[] passwordByte = passwordString.getBytes();
        
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();
        
        return HashUtil.digestToHexString(encodedPassword);
    }

    public static String toHash(String nameMethod, String textInput) throws NoSuchAlgorithmException {
        
        var md = MessageDigest.getInstance(nameMethod);
        
        var passwordString = String.valueOf(textInput.toCharArray());
        byte[] passwordByte = passwordString.getBytes();
        
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();
        
        return HashUtil.digestToHexString(encodedPassword);
    }
    
    /**
     * Convert a digest hash to a string representation.
     * @param block Digest array
     * @return Hash as a string
     */
    public static String digestToHexString(byte[] block) {
        
        var buf = new StringBuilder();
        int len = block.length;
        
        for (var i = 0; i < len; i++) {
            
            HashUtil.byte2hex(block[i], buf);
        }
        
        return buf.toString();
    }
    
    /**
     * Convert byte character to hexadecimal StringBuffer character.
     * @param b Byte character to convert
     * @param buf Hexadecimal converted character
     */
    private static void byte2hex(byte b, StringBuilder buf) {
        
        var hexChars = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = (b & 0xf0) >> 4;
        int low = b & 0x0f;
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
}

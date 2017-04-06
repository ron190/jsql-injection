package com.jsql.view.swing.bruteforce;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.jsql.util.StringUtil;

public class Hash {
    
    private Hash() {
        
    }
    
    public static String generateMySQL(char[] passwordChar) throws NoSuchAlgorithmException {
        MessageDigest digestPass1 = MessageDigest.getInstance("sha-1");
        
        String passwordStringPass1 = new String(passwordChar);
        byte[] passwordBytePass1 = passwordStringPass1.getBytes();
        
        digestPass1.update(passwordBytePass1, 0, passwordBytePass1.length);
        byte[] passwordPass1 = digestPass1.digest();
        String passwordHexPass1 = digestToHexString(passwordPass1);
        
        MessageDigest digestPass2 = MessageDigest.getInstance("sha-1");
        
        String passwordStringPass2 = new String(StringUtil.hexstr(passwordHexPass1).toCharArray());
        byte[] passwordBytePass2 = passwordStringPass2.getBytes();
        
        digestPass2.update(passwordBytePass2, 0, passwordBytePass2.length);
        byte[] passwordPass2 = digestPass2.digest();
        
        return digestToHexString(passwordPass2);
    }

    public static String generateHash(String type, char[] passwordChar) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(type);
        
        String passwordString = new String(passwordChar);
        byte[] passwordByte = passwordString.getBytes();
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();
        
        return digestToHexString(encodedPassword);
    }
    
    /**
     * Convert a digest hash to a string representation.
     * @param block Digest array
     * @return Hash as a string
     */
    public static String digestToHexString(byte[] block) {
        StringBuilder  buf = new StringBuilder();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            Hash.byte2hex(block[i], buf);
        }
        return buf.toString();
    }
    
    /**
     * Convert byte character to hexadecimal StringBuffer character.
     * @param b Byte character to convert
     * @param buf Hexadecimal converted character
     */
    private static void byte2hex(byte b, StringBuilder buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = (b & 0xf0) >> 4;
        int low = b & 0x0f;
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
    
}

package com.jsql.view.swing.bruteforce;

import java.security.MessageDigest;

public class MD4 {
    
    private MD4() {
        
    }

    public static String generateMd4(String passwordString) {
        MessageDigest md = new DigestMD4();

        byte[] passwordByte = passwordString.getBytes();
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();

        return Hash.digestToHexString(encodedPassword);
    }
    
}

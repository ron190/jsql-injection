package com.jsql.util.bruter;

import java.security.MessageDigest;

public class MD4 {
    
    private MD4() {
        // Nothing
    }

    public static String generateMd4(String passwordString) {
        MessageDigest md = new DigestMD4();

        byte[] passwordByte = passwordString.getBytes();
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();

        return HashUtil.digestToHexString(encodedPassword);
    }
}

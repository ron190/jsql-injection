package com.jsql.view.swing.bruteforce;

import java.security.MessageDigest;

import com.jsql.view.swing.manager.util.MD4;

public class Md4 {

    public static String generateMd4(String passwordString) {
        MessageDigest md = new MD4();

        byte[] passwordByte = passwordString.getBytes();
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();

        return Hash.digestToHexString(encodedPassword);
    }
    
}

package com.jsql.view.swing.bruteforce;

import java.util.zip.Checksum;

public class Adler32 {
    
    private Adler32() {
        
    }

    public static String generateAdler32(String baseString) {
        //Convert string to bytes
        byte[] bytes = baseString.getBytes();
       
        Checksum checksum = new java.util.zip.Adler32();
       
        /*
         * To compute the CRC32 checksum for byte array, use
         *
         * void update(bytes[] b, int start, int length)
         * method of CRC32 class.
         */
         
        checksum.update(bytes,0,bytes.length);
       
        /*
         * Get the generated checksum using
         * getValue method of CRC32 class.
         */
        return String.valueOf(checksum.getValue());
    }
    
}

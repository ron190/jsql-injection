package com.jsql.view.swing.bruteforce;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Crc32 {
    
    private Crc32() {
        
    }

    public static String generateCRC32(String baseString) {
        //Convert string to bytes
        byte[] bytes = baseString.getBytes();
       
        Checksum checksum = new CRC32();
       
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

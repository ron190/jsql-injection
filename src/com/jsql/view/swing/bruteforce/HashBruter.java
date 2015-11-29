package com.jsql.view.swing.bruteforce;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jsql.tool.ToolsString;

public class HashBruter extends Bruter {
    /*
     * public static void main(String[] args) {
     *
     * final HashBruter hb = new HashBruter();
     *
     * hb.setMaxLength(5); hb.setMinLength(1);
     *
     * hb.addSpecialCharacters(); hb.addUpperCaseLetters();
     * hb.addLowerCaseLetters(); hb.addDigits();
     *
     * hb.setType("sha-512");
     *
     * hb.setHash("282154720ABD4FA76AD7CD5F8806AA8A19AEFB6D10042B0D57A311B86087DE4DE3186A92019D6EE51035106EE088DC6007BEB7BE46994D1463999968FBE9760E");
     *
     * Thread thread = new Thread(new Runnable() {
     *
     * @Override public void run() { hb.tryBruteForce(); } });
     *
     * thread.start();
     *
     * while (!hb.isFound()) { System.out.println("Hash: " +
     * hb.getGeneratedHash()); System.out.println("Number of Possibilities: " +
     * hb.getNumberOfPossibilities()); System.out.println("Checked hashes: " +
     * hb.getCounter()); System.out.println("Estimated hashes left: " +
     * hb.getRemainder()); }
     *
     * System.out.println("Found " + hb.getType() + " hash collision: " +
     * hb.getGeneratedHash() + " password is: " + hb.getPassword());
     *
     * }
     */

    public String hash, generatedHash, password;
    public String type;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(HashBruter.class);

    public String getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }

    public void setHash(String p) {
        hash = p;
    }

    public void setType(String digestType) {
        type = digestType;
    }

    public String getGeneratedHash() {
        return generatedHash;
    }

    public void tryBruteForce() {
        starttime = System.nanoTime();
        for (int size = minLength; size <= maxLength; size++) {
            if (found || done) {
                break;
            } else {
                while (paused) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        LOGGER.error(e, e);
                    }
                }
                generateAllPossibleCombinations("", size);
            }
        }
        done = true;
    }

    private void generateAllPossibleCombinations(String baseString, int length) {
        while (paused) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            }
        }
        if (!found || !done) {
            if (baseString.length() == length) {
                if(type.equalsIgnoreCase("adler32")) {
                    generatedHash = generateAdler32(baseString);
                } else if(type.equalsIgnoreCase("crc16")) {
                    generatedHash = generateCRC16(baseString);
                } else if(type.equalsIgnoreCase("crc32")) {
                    generatedHash = generateCRC32(baseString);
                } else if(type.equalsIgnoreCase("crc64")) {
                    generatedHash = generateCRC64(baseString.getBytes());
                } else if(type.equalsIgnoreCase("mysql")) {
                    generatedHash = generateMySQL(baseString.toCharArray());
                } else {
                    generatedHash = generateHash(baseString.toCharArray());
                }
                    password = baseString;
                if (hash.equals(generatedHash)) {
                    password = baseString;
                    found = true;
                    done = true;
                }
                count++;
            } else if (baseString.length() < length) {
                for (int n = 0; n < characters.size(); n++) {
                    generateAllPossibleCombinations(baseString + characters.get(n), length);
                }
            }
        }
    }

    private String generateMySQL(char[] passwordChar) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("sha-1");
        } catch (NoSuchAlgorithmException e1) {
            JOptionPane.showMessageDialog(null, "No such algorithm for hashes exists", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String passwordString = new String(passwordChar);
        byte[] passwordByte = passwordString.getBytes();
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();
        String encodedPasswordInString = toHexString(encodedPassword);
        
        MessageDigest md2 = null;
        try {
            md2 = MessageDigest.getInstance("sha-1");
        } catch (NoSuchAlgorithmException e1) {
            JOptionPane.showMessageDialog(null, "No such algorithm for hashes exists", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String passwordString2 = new String(ToolsString.hexstr(encodedPasswordInString).toCharArray());
        byte[] passwordByte2 = passwordString2.getBytes();
        md2.update(passwordByte2, 0, passwordByte2.length);
        byte[] encodedPassword2 = md2.digest();
        String encodedPasswordInString2 = toHexString(encodedPassword2);
        
        return encodedPasswordInString2;
    }

    private String generateHash(char[] passwordChar) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(type);
        } catch (NoSuchAlgorithmException e1) {
            JOptionPane.showMessageDialog(null, "No such algorithm for hashes exists", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String passwordString = new String(passwordChar);
        byte[] passwordByte = passwordString.getBytes();
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();
        String encodedPasswordInString = toHexString(encodedPassword);
        return encodedPasswordInString;
    }

    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = (b & 0xf0) >> 4;
        int low = b & 0x0f;
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
        }
        return buf.toString();
    }

    private String generateCRC32(String baseString) {
               
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
    public static String generateAdler32(String baseString) {
               
                //Convert string to bytes
                byte[] bytes = baseString.getBytes();
               
                Checksum checksum = new Adler32();
               
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
/*************************************************************************
 *  Compilation:  javac CRC16.java
 *  Execution:    java CRC16 s
 *  
 *  Reads in a string s as a command-line argument, and prints out
 *  its 16-bit Cyclic Redundancy Check (CRC16). Uses a lookup table.
 *
 *  Reference:  http://www.gelato.unsw.edu.au/lxr/source/lib/crc16.c
 *
 *  % java CRC16 123456789
 *  CRC16 = bb3d
 *
 * Uses irreducible polynomial:  1 + x^2 + x^15 + x^16
 *
 *
 *************************************************************************/
    public static String generateCRC16(String baseString) {
                int[] table = {
            0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
            0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
            0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
            0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
            0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
            0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
            0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
            0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
            0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
            0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
            0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
            0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
            0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
            0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
            0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
            0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
            0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
            0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
            0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
            0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
            0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
            0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
            0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
            0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
            0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
            0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
            0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
            0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
            0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
            0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
            0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
            0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
        };


        byte[] bytes = baseString.getBytes();
        int crc = 0x0000;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
        }

        return Integer.toHexString(crc);
    }
    /*******************************************************************************
 * Copyright (c) 2009, 2012 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/

/**
 * CRC64 checksum calculator based on the polynom specified in ISO 3309. The
 * implementation is based on the following publications:
 * 
 * <ul>
 * <li>http://en.wikipedia.org/wiki/Cyclic_redundancy_check</li>
 * <li>http://www.geocities.com/SiliconValley/Pines/8659/crc.htm</li>
 * </ul>
 */
    private static final long POLY64REV = 0xd800000000000000L;

    private static final long[] LOOKUPTABLE;

    static {
        LOOKUPTABLE = new long[0x100];
        for (int i = 0; i < 0x100; i++) {
            long v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ POLY64REV;
                } else {
                    v = (v >>> 1);
                }
            }
            LOOKUPTABLE[i] = v;
        }
    }

    /**
     * Calculates the CRC64 checksum for the given data array.
     * 
     * @param data
     *            data to calculate checksum for
     * @return checksum value
     */
    public static String generateCRC64(final byte[] data) {
        long sum = 0;
        for (int i = 0; i < data.length; i++) {
            final int lookupidx = ((int) sum ^ data[i]) & 0xff;
            sum = (sum >>> 8) ^ LOOKUPTABLE[lookupidx];
        }
        return String.valueOf(sum);
    }
}

package com.jsql.view.swing.bruteforce;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import com.jsql.util.StringUtil;

public class HashBruter extends Bruter {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

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

    public String hash;
    public String generatedHash;
    public String password;
    public String type;

    public void tryBruteForce() {
        this.starttime = System.nanoTime();
        
        for (int size = this.minLength; size <= this.maxLength; size++) {
            if (this.found || this.done) {
                break;
            } else {
                
                while (paused) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        LOGGER.error("Interruption while sleeping for brute force", e);
                        Thread.currentThread().interrupt();
                    }
                }
                
                try {
                    this.generateAllPossibleCombinations("", size);
                } catch (NoSuchAlgorithmException e) {
                    LOGGER.error("Coding algorithm not found", e);
                } catch (InterruptedException e) {
                    LOGGER.error("Interruption while generating brute force combinations", e);
                    Thread.currentThread().interrupt();
                }
                
            }
        }
        
        this.done = true;
    }

    private void generateAllPossibleCombinations(String baseString, int length) throws NoSuchAlgorithmException, InterruptedException {
        while (paused) {
            Thread.sleep(500);
        }
        
        if (!this.found || !this.done) {
            if (baseString.length() == length) {
                if("adler32".equalsIgnoreCase(this.type)) {
                    this.generatedHash = Adler32.generateAdler32(baseString);
                } else if("crc16".equalsIgnoreCase(this.type)) {
                    this.generatedHash = Crc16.generateCRC16(baseString);
                } else if("crc32".equalsIgnoreCase(this.type)) {
                    this.generatedHash = Crc32.generateCRC32(baseString);
                } else if("crc64".equalsIgnoreCase(this.type)) {
                    this.generatedHash = Crc64.generateCRC64(baseString.getBytes());
                } else if("mysql".equalsIgnoreCase(this.type)) {
                    this.generatedHash = Hash.generateMySQL(baseString.toCharArray());
                } else if("md4".equalsIgnoreCase(this.type)) {
                    this.generatedHash = Md4.generateMd4(baseString);
                } else {
                    this.generatedHash = Hash.generateHash(this.type, baseString.toCharArray());
                }
                
                this.password = baseString;
                
                if (this.hash.equals(this.generatedHash)) {
                    this.password = baseString;
                    this.found = true;
                    this.done = true;
                }
                count++;
            } else if (baseString.length() < length) {
                for (int n = 0; n < characters.size(); n++) {
                    this.generateAllPossibleCombinations(baseString + characters.get(n), length);
                }
            }
        }
    }

//    private String generateMySQL(char[] passwordChar) throws NoSuchAlgorithmException {
//        MessageDigest digestPass1 = MessageDigest.getInstance("sha-1");
//        
//        String passwordStringPass1 = new String(passwordChar);
//        byte[] passwordBytePass1 = passwordStringPass1.getBytes();
//        
//        digestPass1.update(passwordBytePass1, 0, passwordBytePass1.length);
//        byte[] passwordPass1 = digestPass1.digest();
//        String passwordHexPass1 = digestToHexString(passwordPass1);
//        
//        MessageDigest digestPass2 = MessageDigest.getInstance("sha-1");
//        
//        String passwordStringPass2 = new String(StringUtil.hexstr(passwordHexPass1).toCharArray());
//        byte[] passwordBytePass2 = passwordStringPass2.getBytes();
//        
//        digestPass2.update(passwordBytePass2, 0, passwordBytePass2.length);
//        byte[] passwordPass2 = digestPass2.digest();
//        
//        return digestToHexString(passwordPass2);
//    }
//
//    private String generateHash(char[] passwordChar) throws NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance(this.type);
//        
//        String passwordString = new String(passwordChar);
//        byte[] passwordByte = passwordString.getBytes();
//        md.update(passwordByte, 0, passwordByte.length);
//        byte[] encodedPassword = md.digest();
//        
//        return digestToHexString(encodedPassword);
//    }
//    
//    /**
//     * Convert a digest hash to a string representation.
//     * @param block Digest array
//     * @return Hash as a string
//     */
//    public static String digestToHexString(byte[] block) {
//        StringBuilder  buf = new StringBuilder();
//        int len = block.length;
//        for (int i = 0; i < len; i++) {
//            HashBruter.byte2hex(block[i], buf);
//        }
//        return buf.toString();
//    }
//    
//    /**
//     * Convert byte character to hexadecimal StringBuffer character.
//     * @param b Byte character to convert
//     * @param buf Hexadecimal converted character
//     */
//    private static void byte2hex(byte b, StringBuilder buf) {
//        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
//                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//        int high = (b & 0xf0) >> 4;
//        int low = b & 0x0f;
//        buf.append(hexChars[high]);
//        buf.append(hexChars[low]);
//    }
    
    // Getter and setter

    public String getType() {
        return this.type;
    }

    public String getPassword() {
        return this.password;
    }

    public void setHash(String p) {
        this.hash = p;
    }

    public void setType(String digestType) {
        this.type = digestType;
    }

    public String getGeneratedHash() {
        return this.generatedHash;
    }
    
}

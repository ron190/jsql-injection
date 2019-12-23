package com.jsql.view.swing.bruteforce;

import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class HashBruter extends Bruter {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private String hash;
    
    private String generatedHash;
    
    private String password;
    
    private String type;

    public void tryBruteForce() {
        this.starttime = System.nanoTime();
        
        for (int size = this.minLength; size <= this.maxLength; size++) {
            if (this.found || this.done) {
                break;
            } else {
                
                while (this.paused) {
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
        while (this.paused) {
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
                    this.generatedHash = MD4.generateMd4(baseString);
                } else {
                    this.generatedHash = Hash.generateHash(this.type, baseString.toCharArray());
                }
                
                this.password = baseString;
                
                if (this.hash.equals(this.generatedHash)) {
                    this.found = true;
                    this.done = true;
                }
                this.count++;
            } else if (baseString.length() < length) {
                for (String element : this.characters) {
                    this.generateAllPossibleCombinations(baseString + element, length);
                }
            }
        }
    }
    
    // Getter and setter

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

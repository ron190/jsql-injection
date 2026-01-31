package com.jsql.util.bruter;

import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class HashBruter extends Bruter {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String hash;
    private String generatedHash;
    private String password;
    private String type;

    public void tryBruteForce() {
        this.starttime = System.nanoTime();
        for (int size = this.minLength; size <= this.maxLength; size++) {
            if (this.found || this.done) {
                break;
            }
            try {
                this.generateAllPossibleCombinations(StringUtils.EMPTY, size);
            } catch (NoSuchAlgorithmException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }
        this.done = true;
    }

    private void generateAllPossibleCombinations(String baseString, int length) throws NoSuchAlgorithmException {
        if (!this.found || !this.done) {
            if (baseString.length() == length) {
                this.generatedHash = switch (this.type.toLowerCase()) {
                    case "adler32" -> HashUtil.toAdler32(baseString);
                    case "crc16" -> Crc16Helper.generateCRC16(baseString);
                    case "crc32" -> HashUtil.toCrc32(baseString);
                    case "crc64" -> Crc64Helper.generateCRC64(baseString.getBytes(StandardCharsets.UTF_8));
                    case "mysql" -> HashUtil.toMySql(baseString);
                    case "md4" -> HashUtil.toMd4(baseString);
                    default -> HashUtil.toHash(this.type, baseString);
                };
                this.password = baseString;
                if (this.hash.equals(this.generatedHash)) {
                    this.found = true;
                    this.done = true;
                }
                this.count++;
                
            } else if (baseString.length() < length) {
                for (String element: this.characters) {
                    this.generateAllPossibleCombinations(baseString + element, length);
                }
            }
        }
    }
    
    
    // Getter and setter

    public String getPassword() {
        return this.password;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setType(String digestType) {
        this.type = digestType;
    }

    public String getGeneratedHash() {
        return this.generatedHash;
    }
}

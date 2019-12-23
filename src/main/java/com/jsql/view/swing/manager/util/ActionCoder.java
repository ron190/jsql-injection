/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.manager.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.bruteforce.Adler32;
import com.jsql.view.swing.bruteforce.Crc16;
import com.jsql.view.swing.bruteforce.Crc64;
import com.jsql.view.swing.bruteforce.DigestMD4;
import com.jsql.view.swing.manager.ManagerCoder;

/**
 * Action runned when this.coderManager.encoding.
 */
public class ActionCoder implements ActionListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private ManagerCoder coderManager;
    
    public ActionCoder(ManagerCoder coderManager) {
        this.coderManager = coderManager;
    }
    
    public void actionPerformed() {
        this.transform(this.coderManager.getMenuMethod().getText());
    }
    
    public void actionPerformed(String nameMethod) {
        this.transform(nameMethod);
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        this.transform(this.coderManager.getMenuMethod().getText());
    }
    
    private void transform(String labelMethodMenu) {

        String nameMethod = labelMethodMenu.replace("Hash to ", "");
        
        String result;
        String textInput = this.coderManager.getTextInput().getText();
        
        if (
            "".equals(textInput)
            && !Arrays.asList("Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql").contains(nameMethod)
        ) {
            result = "<span style=\"color:red;\">Empty string to convert</span>";
            
        } else if (Arrays.asList("Md2", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512").contains(nameMethod)) {
            try {
                MessageDigest md = MessageDigest.getInstance(nameMethod);
                
                String passwordString = new String(textInput.toCharArray());
                byte[] passwordByte = passwordString.getBytes();
                md.update(passwordByte, 0, passwordByte.length);
                byte[] encodedPassword = md.digest();
                String encodedPasswordInString = StringUtil.digestToHexString(encodedPassword);
                
                result = encodedPasswordInString;
            } catch (NoSuchAlgorithmException e) {
                result = String.format("<span style=\"color:red;\">Digest algorithm %s not found</span>", nameMethod);
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Md4".contains(nameMethod)) {
            MessageDigest md = new DigestMD4();

            String passwordString = new String(textInput.toCharArray());
            byte[] passwordByte = passwordString.getBytes();
            md.update(passwordByte, 0, passwordByte.length);
            byte[] encodedPassword = md.digest();
            String encodedPasswordInString = StringUtil.digestToHexString(encodedPassword);

            result = encodedPasswordInString;
            
        } else if ("Adler32".contains(nameMethod)) {
            result = Adler32.generateAdler32(textInput);
            
        } else if ("Crc16".contains(nameMethod)) {
            result = Crc16.generateCRC16(textInput);
            
        } else if ("Crc32".contains(nameMethod)) {
            byte[] bytes = textInput.getBytes();
            Checksum checksum = new CRC32();
            checksum.update(bytes,0,bytes.length);
            long lngChecksum = checksum.getValue();
            
            result = Long.toString(lngChecksum);
            
        } else if ("Crc64".contains(nameMethod)) {
            result = Crc64.generateCRC64(textInput.getBytes());
            
        } else if ("Mysql".equals(nameMethod)) {
            try {
                MessageDigest md = MessageDigest.getInstance("sha-1");
                
                String password = new String(textInput.toCharArray());
                byte[] passwordBytes = password.getBytes();
                md.update(passwordBytes, 0, passwordBytes.length);
                byte[] hashSHA1 = md.digest();
                String stringSHA1 = StringUtil.digestToHexString(hashSHA1);
                
                String passwordSHA1 = new String(StringUtil.hexstr(stringSHA1).toCharArray());
                byte[] passwordSHA1Bytes = passwordSHA1.getBytes();
                md.update(passwordSHA1Bytes, 0, passwordSHA1Bytes.length);
                byte[] hashSHA1SH1 = md.digest();
                String mysqlHash = StringUtil.digestToHexString(hashSHA1SH1);
                
                result = mysqlHash;
            } catch (NoSuchAlgorithmException e) {
                result = "<span style=\"color:red;\">Digest algorithm sha-1 not found</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Hex".equalsIgnoreCase(nameMethod)) {
            try {
                result = Hex.encodeHexString(textInput.getBytes(StandardCharsets.UTF_8.name())).trim();
            } catch (UnsupportedEncodingException e) {
                result = String.format("<span style=\"color:red;\">Encoding to Hex error: %s</span>", e.getMessage());

                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Hex".equalsIgnoreCase(nameMethod)) {
            try {
                result = new String(
                    Hex.decodeHex(textInput.toCharArray()),
                    StandardCharsets.UTF_8.name()
                );
            } catch (Exception e) {
                result = String.format("<span style=\"color:red;\">Decoding from Hex error: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Hex(zipped)".equalsIgnoreCase(nameMethod)) {
            try {
                result = Hex.encodeHexString(
                    StringUtil.compress(textInput).getBytes(StandardCharsets.UTF_8.name())
                ).trim();
            } catch (Exception e) {
                result = String.format("<span style=\"color:red;\">Encoding to Hex(zipped) error: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Hex(zipped)".equalsIgnoreCase(nameMethod)) {
            try {
                result = StringUtil.decompress(
                    new String(
                        Hex.decodeHex(textInput.toCharArray()),
                        StandardCharsets.UTF_8.name()
                    )
                );
            } catch (Exception e) {
                result = String.format("<span style=\"color:red;\">Decoding from Hex(zipped) error: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Base64(zipped)".equalsIgnoreCase(nameMethod)) {
            try {
                result = StringUtil.base64Encode(
                    StringUtil.compress(
                        textInput
                    )
                );
            } catch (IOException e) {
                result = String.format("<span style=\"color:red;\">Encoding to Base64(zipped) error: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Base64(zipped)".equalsIgnoreCase(nameMethod)) {
            try {
                result = StringUtil.decompress(
                    StringUtil.base64Decode(
                        textInput
                    )
                );
            } catch (IOException e) {
                result = String.format("<span style=\"color:red;\">Decoding from Base64(zipped) error: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Base64".equalsIgnoreCase(nameMethod)) {
            result = StringUtil.base64Encode(textInput);
            
        } else if ("Decode from Base64".equalsIgnoreCase(nameMethod)) {
            result = StringUtil.base64Decode(textInput);
            
        } else if ("Encode to Html".equalsIgnoreCase(nameMethod)) {
            result = StringEscapeUtils.escapeHtml4(textInput).replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
            
        } else if ("Encode to Html (decimal)".equalsIgnoreCase(nameMethod)) {
            result = StringUtil.decimalHtmlEncode(textInput).replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
            
        } else if ("Decode from Html".equalsIgnoreCase(nameMethod)) {
            result = StringEscapeUtils.unescapeHtml4(textInput).replace("<", "&lt;").replace(">", "&gt;");
            
        } else if ("Encode to Url".equalsIgnoreCase(nameMethod)) {
            try {
                result = URLEncoder.encode(textInput, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                result = String.format("<span style=\"color:red;\">Encoding to UTF-8 failed: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Url".equalsIgnoreCase(nameMethod)) {
            // Fix #16068: IllegalArgumentException on URLDecoder.decode() when input contains %
            try {
                result = URLDecoder.decode(textInput, StandardCharsets.UTF_8.name());
            } catch (IllegalArgumentException | UnsupportedEncodingException e) {
                result = String.format("<span style=\"color:red;\">Decoding failed: %s</span>", e.getMessage());
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else {
            result = "<span style=\"color:red;\">Unsupported encoding or decoding method</span>";
        }
        
        this.coderManager.getResult().setText(String.format("<html><span style=\"font-family:'Ubuntu Mono'\">%s</span></html>", result));
    }
    
}
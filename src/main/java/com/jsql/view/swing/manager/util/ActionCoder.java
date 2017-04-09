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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
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
        a(this.coderManager.getEncoding().getText().replace("Hash to ", ""));
    }
    
    public void actionPerformed(String text) {
        a(text.replace("Hash to ", ""));
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        a(this.coderManager.getEncoding().getText().replace("Hash to ", ""));
    }
    
    private void a(String text) {

        String choice = text.replace("Hash to ", "");
        
        String result;
        String textInput = this.coderManager.getTextInput().getText();
        
        if (
            "".equals(textInput)
            && !Arrays.asList(new String[]{"Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql"}).contains(choice)
        ) {
            result = "<span style=\"color:red;\">Empty string to convert</span>";
            
        } else if (Arrays.asList(new String[]{"Md2", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512"}).contains(choice)) {
            try {
                MessageDigest md = MessageDigest.getInstance(choice);
                
                String passwordString = new String(textInput.toCharArray());
                byte[] passwordByte = passwordString.getBytes();
                md.update(passwordByte, 0, passwordByte.length);
                byte[] encodedPassword = md.digest();
                String encodedPasswordInString = StringUtil.digestToHexString(encodedPassword);
                
                result = encodedPasswordInString;
            } catch (NoSuchAlgorithmException e) {
                result = "<span style=\"color:red;\">Digest algorithm "+ choice +" not found</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Md4".contains(choice)) {
            MessageDigest md = new DigestMD4();

            String passwordString = new String(textInput.toCharArray());
            byte[] passwordByte = passwordString.getBytes();
            md.update(passwordByte, 0, passwordByte.length);
            byte[] encodedPassword = md.digest();
            String encodedPasswordInString = StringUtil.digestToHexString(encodedPassword);

            result = encodedPasswordInString;
            
        } else if ("Adler32".contains(choice)) {
            result = Adler32.generateAdler32(textInput);
            
        } else if ("Crc16".contains(choice)) {
            result = Crc16.generateCRC16(textInput);
            
        } else if ("Crc32".contains(choice)) {
            byte[] bytes = textInput.getBytes();
            Checksum checksum = new CRC32();
            checksum.update(bytes,0,bytes.length);
            long lngChecksum = checksum.getValue();
            
            result = Long.toString(lngChecksum);
            
        } else if ("Crc64".contains(choice)) {
            result = Crc64.generateCRC64(textInput.getBytes());
            
        } else if ("Mysql".equals(choice)) {
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
            
        } else if ("Encode to Hex".equalsIgnoreCase(choice)) {
            try {
                result = Hex.encodeHexString(textInput.getBytes("UTF-8")).trim();
            } catch (UnsupportedEncodingException e) {
                result = "<span style=\"color:red;\">Encoding to Hex error: "+ e.getMessage() +"</span>";

                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Hex".equalsIgnoreCase(choice)) {
            try {
                result = new String(
                    Hex.decodeHex(textInput.toCharArray()),
                    "UTF-8"
                );
            } catch (Exception e) {
                result = "<span style=\"color:red;\">Decoding from Hex error: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Hex(zipped)".equalsIgnoreCase(choice)) {
            try {
                result = Hex.encodeHexString(
                    StringUtil.compress(textInput).getBytes("UTF-8")
                ).trim();
            } catch (Exception e) {
                result = "<span style=\"color:red;\">Encoding to Hex(zipped) error: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Hex(zipped)".equalsIgnoreCase(choice)) {
            try {
                result = StringUtil.decompress(
                    new String(
                        Hex.decodeHex(textInput.toCharArray()),
                        "UTF-8"
                    )
                );
            } catch (Exception e) {
                result = "<span style=\"color:red;\">Decoding from Hex(zipped) error: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Base64(zipped)".equalsIgnoreCase(choice)) {
            try {
                result = StringUtil.base64Encode(
                    StringUtil.compress(
                        textInput
                    )
                );
            } catch (IOException e) {
                result = "<span style=\"color:red;\">Encoding to Base64(zipped) error: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Base64(zipped)".equalsIgnoreCase(choice)) {
            try {
                result = StringUtil.decompress(
                    StringUtil.base64Decode(
                        textInput
                    )
                );
            } catch (IOException e) {
                result = "<span style=\"color:red;\">Decoding from Base64(zipped) error: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Encode to Base64".equalsIgnoreCase(choice)) {
            result = StringUtil.base64Encode(textInput);
            
        } else if ("Decode from Base64".equalsIgnoreCase(choice)) {
            result = StringUtil.base64Decode(textInput);
            
        } else if ("Encode to Html".equalsIgnoreCase(choice)) {
            result = StringEscapeUtils.escapeHtml4(textInput).replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
            
        } else if ("Encode to Html (decimal)".equalsIgnoreCase(choice)) {
            result = StringUtil.decimalHtmlEncode(textInput).replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
            
        } else if ("Decode from Html".equalsIgnoreCase(choice)) {
            result = StringEscapeUtils.unescapeHtml4(textInput).replace("<", "&lt;").replace(">", "&gt;");
            
        } else if ("Encode to Url".equalsIgnoreCase(choice)) {
            try {
                result = URLEncoder.encode(textInput, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                result = "<span style=\"color:red;\">Encoding to UTF-8 failed: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else if ("Decode from Url".equalsIgnoreCase(choice)) {
            // Fix #16068: IllegalArgumentException on URLDecoder.decode() when input contains %
            try {
                result = URLDecoder.decode(textInput, "UTF-8");
            } catch (IllegalArgumentException | UnsupportedEncodingException e) {
                result = "<span style=\"color:red;\">Decoding failed: "+ e.getMessage() +"</span>";
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
            
        } else {
            result = "<span style=\"color:red;\">Unsupported encoding or decoding method</span>";
        }
        
        this.coderManager.getResult().setText("<html><span style=\"font-family:'Ubuntu Mono'\">"+ result +"</span></html>");
    }
    
}
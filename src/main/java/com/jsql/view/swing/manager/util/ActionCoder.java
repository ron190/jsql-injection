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

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String choice = this.coderManager.getEncoding().getText().replace("Hash to ", "");
        
        if (
            "".equals(this.coderManager.getTextInput().getText())
            && !Arrays.asList(new String[]{"Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql"}).contains(choice)
        ) {
            LOGGER.warn("Empty string to convert");
            return;
        }
        
        if (Arrays.asList(new String[]{"Md2", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512"}).contains(choice)) {
            try {
                MessageDigest md = MessageDigest.getInstance(choice);
                
                String passwordString = new String(this.coderManager.getTextInput().getText().toCharArray());
                byte[] passwordByte = passwordString.getBytes();
                md.update(passwordByte, 0, passwordByte.length);
                byte[] encodedPassword = md.digest();
                String encodedPasswordInString = this.coderManager.digestToHexString(encodedPassword);
                
                this.coderManager.getResult().setText(encodedPasswordInString);
            } catch (NoSuchAlgorithmException e) {
                LOGGER.warn("Digest algorithm "+ choice +" not found", e);
            }
            
        } else if ("Md4".contains(choice)) {
            MessageDigest md = new DigestMD4();

            String passwordString = new String(this.coderManager.getTextInput().getText().toCharArray());
            byte[] passwordByte = passwordString.getBytes();
            md.update(passwordByte, 0, passwordByte.length);
            byte[] encodedPassword = md.digest();
            String encodedPasswordInString = this.coderManager.digestToHexString(encodedPassword);

            this.coderManager.getResult().setText(encodedPasswordInString);
            
        } else if ("Adler32".contains(choice)) {
            this.coderManager.getResult().setText(Adler32.generateAdler32(this.coderManager.getTextInput().getText()));
            
        } else if ("Crc16".contains(choice)) {
            this.coderManager.getResult().setText(Crc16.generateCRC16(this.coderManager.getTextInput().getText()));
            
        } else if ("Crc32".contains(choice)) {
            byte[] bytes = this.coderManager.getTextInput().getText().getBytes();
            Checksum checksum = new CRC32();
            checksum.update(bytes,0,bytes.length);
            long lngChecksum = checksum.getValue();
            this.coderManager.getResult().setText(Long.toString(lngChecksum));
            
        } else if ("Crc64".contains(choice)) {
            this.coderManager.getResult().setText(Crc64.generateCRC64(this.coderManager.getTextInput().getText().getBytes()));
            
        } else if ("Mysql".equals(choice)) {
            try {
                MessageDigest md = MessageDigest.getInstance("sha-1");
                
                String password = new String(this.coderManager.getTextInput().getText().toCharArray());
                byte[] passwordBytes = password.getBytes();
                md.update(passwordBytes, 0, passwordBytes.length);
                byte[] hashSHA1 = md.digest();
                String stringSHA1 = this.coderManager.digestToHexString(hashSHA1);
                
                String passwordSHA1 = new String(StringUtil.hexstr(stringSHA1).toCharArray());
                byte[] passwordSHA1Bytes = passwordSHA1.getBytes();
                md.update(passwordSHA1Bytes, 0, passwordSHA1Bytes.length);
                byte[] hashSHA1SH1 = md.digest();
                String mysqlHash = this.coderManager.digestToHexString(hashSHA1SH1);
                
                this.coderManager.getResult().setText(mysqlHash);
            } catch (NoSuchAlgorithmException e) {
                LOGGER.warn("Digest algorithm sha-1 not found", e);
            }
            
        } else if ("Encode to Hex".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    Hex.encodeHexString(
                        this.coderManager.getTextInput().getText().getBytes("UTF-8")
                    ).trim()
                );
            } catch (UnsupportedEncodingException e) {
                this.coderManager.getResult().setText("Encoding to Hex error: "+ e);
            }
            
        } else if ("Decode from Hex".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    new String(
                        Hex.decodeHex(
                            this.coderManager.getTextInput().getText().toCharArray()
                        ),
                        "UTF-8"
                    )
                );
            } catch (Exception e) {
                this.coderManager.getResult().setText("Decoding from Hex error: "+ e);
            }
            
        } else if ("Encode to Hex(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    Hex.encodeHexString(
                        this.coderManager.compress(
                            this.coderManager.getTextInput().getText()
                        ).getBytes("UTF-8")
                    ).trim()
                );
            } catch (Exception e) {
                this.coderManager.getResult().setText("Encoding to Hex(zipped) error: "+ e);
            }
            
        } else if ("Decode from Hex(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    this.coderManager.decompress(
                        new String(
                            Hex.decodeHex(
                                this.coderManager.getTextInput().getText().toCharArray()
                            ),
                            "UTF-8"
                        )
                    )
                );
            } catch (Exception e) {
                this.coderManager.getResult().setText("Decoding from Hex(zipped) error: "+ e);
            }
            
        } else if ("Encode to Base64(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    this.coderManager.base64Encode(
                        this.coderManager.compress(
                            this.coderManager.getTextInput().getText()
                        )
                    )
                );
            } catch (IOException e) {
                this.coderManager.getResult().setText("Encoding to Base64(zipped) error: "+ e);
            }
            
        } else if ("Decode from Base64(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    this.coderManager.decompress(
                        this.coderManager.base64Decode(
                            this.coderManager.getTextInput().getText()
                        )
                    )
                );
            } catch (IOException e) {
                this.coderManager.getResult().setText("Decoding from Base64(zipped) error: "+ e);
            }
            
        } else if ("Encode to Base64".equalsIgnoreCase(choice)) {
            this.coderManager.getResult().setText(this.coderManager.base64Encode(this.coderManager.getTextInput().getText()));
            
        } else if ("Decode from Base64".equalsIgnoreCase(choice)) {
            this.coderManager.getResult().setText(this.coderManager.base64Decode(this.coderManager.getTextInput().getText()));
            
        } else if ("Encode to Html".equalsIgnoreCase(choice)) {
            this.coderManager.getResult().setText(StringEscapeUtils.escapeHtml3(this.coderManager.getTextInput().getText()));
            
        } else if ("Decode from Html".equalsIgnoreCase(choice)) {
            this.coderManager.getResult().setText(StringEscapeUtils.unescapeHtml3(this.coderManager.getTextInput().getText()));
            
        } else if ("Encode to Url".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.getResult().setText(
                    URLEncoder.encode(this.coderManager.getTextInput().getText(), "UTF-8")
                );
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("Encoding to UTF-8 failed: "+ e.getMessage(), e);
            }
            
        } else if ("Decode from Url".equalsIgnoreCase(choice)) {
            // Fix #16068: IllegalArgumentException on URLDecoder.decode() when input contains %
            try {
                this.coderManager.getResult().setText(
                    URLDecoder.decode(this.coderManager.getTextInput().getText(), "UTF-8")
                );
            } catch (IllegalArgumentException | UnsupportedEncodingException e) {
                LOGGER.warn("Decoding failed: "+ e.getMessage(), e);
            }
            
        } else {
            this.coderManager.getResult().setText("Unsupported encoding or decoding method");
        }
    }
    
}
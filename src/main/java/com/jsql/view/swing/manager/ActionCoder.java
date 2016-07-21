/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.manager;

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
import com.jsql.view.swing.bruteforce.HashBruter;

/**
 * Action runned when this.coderManager.encoding.
 */
public class ActionCoder implements ActionListener {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ActionCoder.class);
    
    private ManagerCoder coderManager;
    
    public ActionCoder(ManagerCoder coderManager) {
        this.coderManager = coderManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if ("".equals(this.coderManager.entry.getText())) {
            LOGGER.warn("Empty String to convert");
            return;
        }
        
        String choice = this.coderManager.encoding.getText().replace("Hash to ", "");
        
        //TODO State pattern
        if (Arrays.asList(new String[]{ "Md2", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512" } ).contains(choice)) {
            try {
                MessageDigest md = MessageDigest.getInstance(choice);
                
                String passwordString = new String(this.coderManager.entry.getText().toCharArray());
                byte[] passwordByte = passwordString.getBytes();
                md.update(passwordByte, 0, passwordByte.length);
                byte[] encodedPassword = md.digest();
                String encodedPasswordInString = this.coderManager.digestToHexString(encodedPassword);
                
                this.coderManager.result.setText(encodedPasswordInString);
            } catch (NoSuchAlgorithmException e1) {
                LOGGER.warn("Digest algorithm "+ choice +" not found");
            }
            
        } else if ("Md4".contains(choice)) {
            MessageDigest md = new MD4();

            String passwordString = new String(this.coderManager.entry.getText().toCharArray());
            byte[] passwordByte = passwordString.getBytes();
            md.update(passwordByte, 0, passwordByte.length);
            byte[] encodedPassword = md.digest();
            String encodedPasswordInString = this.coderManager.digestToHexString(encodedPassword);

            this.coderManager.result.setText(encodedPasswordInString);
            
        } else if ("Adler32".contains(choice)) {
            this.coderManager.result.setText(HashBruter.generateAdler32(this.coderManager.entry.getText()));
            
        } else if ("Crc16".contains(choice)) {
            this.coderManager.result.setText(HashBruter.generateCRC16(this.coderManager.entry.getText()));
            
        } else if ("Crc32".contains(choice)) {
            byte[] bytes = this.coderManager.entry.getText().getBytes();
            Checksum checksum = new CRC32();
            checksum.update(bytes,0,bytes.length);
            long lngChecksum = checksum.getValue();
            this.coderManager.result.setText(lngChecksum+"");
            
        } else if ("Crc64".contains(choice)) {
            this.coderManager.result.setText(HashBruter.generateCRC64(this.coderManager.entry.getText().getBytes()));
            
        } else if ("Mysql".equals(choice)) {
            try {
                MessageDigest md = MessageDigest.getInstance("sha-1");
                
                String password = new String(this.coderManager.entry.getText().toCharArray());
                byte[] passwordBytes = password.getBytes();
                md.update(passwordBytes, 0, passwordBytes.length);
                byte[] hashSHA1 = md.digest();
                String stringSHA1 = this.coderManager.digestToHexString(hashSHA1);
                
                String passwordSHA1 = new String(StringUtil.hexstr(stringSHA1).toCharArray());
                byte[] passwordSHA1Bytes = passwordSHA1.getBytes();
                md.update(passwordSHA1Bytes, 0, passwordSHA1Bytes.length);
                byte[] hashSHA1SH1 = md.digest();
                String mysqlHash = this.coderManager.digestToHexString(hashSHA1SH1);
                
                this.coderManager.result.setText(mysqlHash);
            } catch (NoSuchAlgorithmException e1) {
                LOGGER.warn("Digest algorithm sha-1 not found");
            }
            
        } else if ("Encode to Hex".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    Hex.encodeHexString(
                        this.coderManager.entry.getText().getBytes("UTF-8")
                    ).trim()
                );
            } catch (UnsupportedEncodingException e) {
                this.coderManager.result.setText("Encoding to Hex error: " + e.getMessage());
            }
            
        } else if ("Decode from Hex".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    new String(
                        Hex.decodeHex(
                            this.coderManager.entry.getText().toCharArray()
                        ), 
                        "UTF-8"
                    )
                );
            } catch (Exception e) {
                this.coderManager.result.setText("Decoding from Hex error: " + e.getMessage());
            }
            
        } else if ("Encode to Hex(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    Hex.encodeHexString(
                        this.coderManager.compress(
                            this.coderManager.entry.getText()
                        ).getBytes("UTF-8")
                    ).trim()
                );
            } catch (Exception e) {
                this.coderManager.result.setText("Encoding to Hex(zipped) error: " + e.getMessage());
            }
            
        } else if ("Decode from Hex(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    this.coderManager.decompress(
                        new String(
                            Hex.decodeHex(
                                this.coderManager.entry.getText().toCharArray()
                            ), 
                            "UTF-8"
                        )
                    )
                );
            } catch (Exception e) {
                this.coderManager.result.setText("Decoding from Hex(zipped) error: " + e.getMessage());
            }
            
        } else if ("Encode to Base64(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    this.coderManager.base64Encode(
                        this.coderManager.compress(
                            this.coderManager.entry.getText()
                        )
                    )
                );
            } catch (IOException e) {
                this.coderManager.result.setText("Encoding to Base64(zipped) error: " + e.getMessage());
            }
            
        } else if ("Decode from Base64(zipped)".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    this.coderManager.decompress(
                        this.coderManager.base64Decode(
                            this.coderManager.entry.getText()
                        )
                    )
                );
            } catch (IOException e) {
                this.coderManager.result.setText("Decoding from Base64(zipped) error: " + e.getMessage());
            }
            
        } else if ("Encode to Base64".equalsIgnoreCase(choice)) {
            this.coderManager.result.setText(this.coderManager.base64Encode(this.coderManager.entry.getText()));
            
        } else if ("Decode from Base64".equalsIgnoreCase(choice)) {
            this.coderManager.result.setText(this.coderManager.base64Decode(this.coderManager.entry.getText()));
            
        } else if ("Encode to Html".equalsIgnoreCase(choice)) {
            this.coderManager.result.setText(StringEscapeUtils.escapeHtml3(this.coderManager.entry.getText()));
            
        } else if ("Decode from Html".equalsIgnoreCase(choice)) {
            this.coderManager.result.setText(StringEscapeUtils.unescapeHtml3(this.coderManager.entry.getText()));
            
        } else if ("Encode to Url".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    URLEncoder.encode(
                        this.coderManager.entry.getText(), 
                        "UTF-8"
                    )
                );
            } catch (UnsupportedEncodingException e) {
                this.coderManager.result.setText("Encoding to Url error: " + e.getMessage());
            }
            
        } else if ("Decode from Url".equalsIgnoreCase(choice)) {
            try {
                this.coderManager.result.setText(
                    URLDecoder.decode(
                        this.coderManager.entry.getText().replace("%", "%25"),
                        "UTF-8"
                    )
                );
            } catch (UnsupportedEncodingException e) {
                this.coderManager.result.setText("Decoding from Url error: " + e.getMessage());
            }
            
        } else {
            this.coderManager.result.setText("Unsupported encoding or decoding method");
        }
    }
}
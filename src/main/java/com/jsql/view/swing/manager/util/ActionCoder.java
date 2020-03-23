/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.log4j.Logger;

import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.manager.ManagerCoder;

/**
 * Action run when this.coderManager.encoding.
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
        
        try {
            if (
                "".equals(textInput)
                && !Arrays.asList("Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql").contains(nameMethod)
            ) {
                result = "<span style=\"color:red;\">Empty string to convert</span>";
                
            } else if (Arrays.asList("Md2", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512").contains(nameMethod)) {
                result = StringUtil.toHash(nameMethod, textInput);
                
            } else if ("Md4".contains(nameMethod)) {
                result = StringUtil.toMd4(textInput);
                
            } else if ("Adler32".contains(nameMethod)) {
                result = StringUtil.toAdler32(textInput);
                
            } else if ("Crc16".contains(nameMethod)) {
                result = StringUtil.toCrc16(textInput);
                
            } else if ("Crc32".contains(nameMethod)) {
                result = StringUtil.toCrc32(textInput);
                
            } else if ("Crc64".contains(nameMethod)) {
                result = StringUtil.toCrc64(textInput);
                
            } else if ("Mysql".equals(nameMethod)) {
                result = StringUtil.toMySql(textInput);
                
            } else if ("Encode to Hex".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.toHex(textInput);
                
            } else if ("Decode from Hex".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.fromHex(textInput);
                
            } else if ("Encode to Hex(zipped)".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.toHexZip(textInput);
                
            } else if ("Decode from Hex(zipped)".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.fromHexZip(textInput);
                
            } else if ("Encode to Base64(zipped)".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.toBase64Zip(textInput);
                
            } else if ("Decode from Base64(zipped)".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.fromBase64Zip(textInput);
                
            } else if ("Encode to Base64".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.base64Encode(textInput);
                
            } else if ("Decode from Base64".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.base64Decode(textInput);
                
            } else if ("Encode to Html".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.toHtml(textInput);
                
            } else if ("Encode to Html (decimal)".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.decimalHtmlEncode(textInput, true);
                
            } else if ("Decode from Html".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.fromHtml(textInput);
                
            } else if ("Encode to Url".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.toUrl(textInput);
                
            } else if ("Decode from Url".equalsIgnoreCase(nameMethod)) {
                result = StringUtil.fromUrl(textInput);
                
            } else {
                result = "<span style=\"color:red;\">Unsupported encoding or decoding method</span>";
            }
        } catch (IllegalArgumentException | NoSuchAlgorithmException | IOException | DecoderException e) {
            
            result = String.format("<span style=\"color:red;\">Decoding failed: %s</span>", e.getMessage());
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
        
        this.coderManager.getResult().setText(String.format("<html><span style=\"font-family:'Ubuntu Mono'\">%s</span></html>", result));
    }
}
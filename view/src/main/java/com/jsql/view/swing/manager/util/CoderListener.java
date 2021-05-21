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
import java.util.NoSuchElementException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.util.StringUtil;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.view.swing.manager.ManagerCoder;
import com.jsql.view.swing.util.UiUtil;

/**
 * Action run when this.coderManager.encoding.
 */
public class CoderListener implements ActionListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private ManagerCoder coderManager;
    
    public CoderListener(ManagerCoder coderManager) {
        
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

        String nameMethod = labelMethodMenu.replace("Hash to ", StringUtils.EMPTY);
        
        String result;
        String textInput = this.coderManager.getTextInput().getText();
        
        try {
            if (
                StringUtils.isEmpty(textInput)
                && !Arrays.asList("Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql").contains(nameMethod)
            ) {
                result = "<span style=\"color:red;\">Empty string to convert</span>";
                
            } else {
                
                result = ActionCoder
                    .forName(nameMethod)
                    .orElseThrow(() -> new NoSuchElementException("Unsupported encoding or decoding method"))
                    .run(textInput);
                
                // Prevent decoded HTML tags from result not rendered in Coder textPane
                result = StringUtil.fromHtml(result);
            }
            
        } catch (
            IllegalArgumentException
            | NoSuchAlgorithmException
            | IOException
            | DecoderException e
        ) {
            
            result = String.format("<span style=\"color:red;\">Decoding failed: %s</span>", e.getMessage());
            
            LOGGER.log(LogLevel.IGNORE, e);
        }
        
        this.coderManager.getResult().setText(
            String
            .format(
                "<html><span style=\"font-family:'%s'\">%s</span></html>",
                UiUtil.FONT_NAME_MONO_NON_ASIAN,
                result
            )
        );
    }
}
/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.manager.util;

import com.jsql.util.LogLevelUtil;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.view.swing.manager.ManagerCoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Action run when this.coderManager.encoding.
 */
public class CoderListener implements ActionListener {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final ManagerCoder coderManager;
    
    public CoderListener(ManagerCoder coderManager) {
        this.coderManager = coderManager;
    }
    
    public void actionPerformed() {  // type
        this.transform(this.coderManager.getMenuMethod().getText());
    }
    
    public void actionPerformed(String nameMethod) {  // hover
        this.transform(nameMethod);
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {  // click
        this.coderManager.getMenuMethod().setText(actionEvent.getActionCommand());
        this.transform(this.coderManager.getMenuMethod().getText());
    }
    
    private void transform(String labelMethodMenu) {
        String nameMethod = labelMethodMenu.replace("Hash to ", StringUtils.EMPTY);
        String result;
        String textInput = this.coderManager.getTextInput().getText();
        
        try {
            if (
                StringUtils.isEmpty(textInput)
                && !ActionCoder.getHashesEmpty().contains(nameMethod)
            ) {
                throw new IllegalArgumentException("text to convert not found");
            } else {
                result = ActionCoder.forName(nameMethod).orElseThrow().run(textInput);
            }
        } catch (
            IllegalArgumentException  // also thrown by Base64
            | IOException
            | NoSuchAlgorithmException e
        ) {
            result = "Coder failure: " + e.getMessage();
            LOGGER.log(LogLevelUtil.IGNORE, e);
        }

        this.coderManager.getResult().setText(result);
    }
}
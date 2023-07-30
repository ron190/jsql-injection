/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.action;

import com.jsql.MainApplication;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Open another jSQL instance in new process.
 */
public class ActionNewWindow extends AbstractAction {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String PATH = SystemUtils.JAVA_HOME + File.separator +"bin"+ File.separator +"java";
    
    private static final List<String> COMMANDS_DEFAULT = Arrays.asList(
        "-cp",
        SystemUtils.JAVA_CLASS_PATH,
        MainApplication.class.getName()
    );
    
    private final List<String> commands;
    
    public ActionNewWindow() {
        
        this(I18nUtil.valueByKey("NEW_WINDOW_MENU"));
        
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
    }

    public ActionNewWindow(String name, String... commands) {
        
        this.commands = new ArrayList<>(List.of(PATH));
        this.commands.addAll(Arrays.asList(commands));
        this.commands.addAll(COMMANDS_DEFAULT);
        
        this.putValue(Action.NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        
        LOGGER.log(LogLevelUtil.CONSOLE_INFORM, () -> I18nUtil.valueByKey("NEW_WINDOW_START"));
        
        var processBuilder = new ProcessBuilder(this.commands.toArray(new String[0]));
        
        try {
            processBuilder.start();
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
}

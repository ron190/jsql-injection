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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.MainApplication;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;

/**
 * Open another jSQL instance in new process.
 */
@SuppressWarnings("serial")
public class ActionNewWindow extends AbstractAction {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static final String CLASSPATH = System.getProperty("java.class.path");
    private static final String PATH = System.getProperty("java.home") + SEPARATOR +"bin"+ SEPARATOR +"java";
    
    private static final List<String> COMMANDS_DEFAULT = Arrays.asList(
        "-cp",
        CLASSPATH,
        MainApplication.class.getName()
    );
    
    private List<String> commands;
    
    public ActionNewWindow() {
        
        this(I18nUtil.valueByKey("NEW_WINDOW_MENU"), new String[0]);
        
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    }

    public ActionNewWindow(String name, String... commands) {
        
        this.commands = new ArrayList<>(Arrays.asList(PATH));
        this.commands.addAll(Arrays.asList(commands));
        this.commands.addAll(COMMANDS_DEFAULT);
        
        this.putValue(Action.NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        
        LOGGER.log(LogLevel.CONSOLE_INFORM, () -> I18nUtil.valueByKey("NEW_WINDOW_START"));
        
        var processBuilder = new ProcessBuilder(this.commands.toArray(new String[0]));
        
        try {
            processBuilder.start();
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}

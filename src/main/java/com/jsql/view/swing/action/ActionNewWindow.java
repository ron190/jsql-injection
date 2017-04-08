/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.jsql.MainApplication;
import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperUi;

/**
 * Open another jSQL instance in new process.
 */
@SuppressWarnings("serial")
public class ActionNewWindow extends AbstractAction {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    public ActionNewWindow() {
        this.putValue(Action.NAME, I18n.valueByKey("NEW_WINDOW_MENU"));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        this.putValue(Action.SMALL_ICON, HelperUi.ICON_EMPTY);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.info(I18n.valueByKey("NEW_WINDOW_START"));
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder =
            new ProcessBuilder(
                path,
                "-cp",
                classpath,
                MainApplication.class.getName()
            )
        ;
        try {
            processBuilder.start();
        } catch (IOException e) {
            LOGGER.error(I18n.valueByKey("NEW_WINDOW_ERROR"), e);
        }
    }
    
}

package com.jsql.view.swing.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.util.ConnectionUtil;

public class ActionCheckUpdate implements ActionListener, Runnable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ActionCheckUpdate.class);

    @Override
    public void run() {
        try {
            LOGGER.trace(I18n.valueByKey("UPDATE_LOADING"));
            
            String pageSource = ConnectionUtil.getSource("https://raw.githubusercontent.com/ron190/jsql-injection/master/.version");

            Float gitVersion = Float.parseFloat(pageSource);
            MediatorModel.model();
            if (gitVersion <= Float.parseFloat(InjectionModel.VERSION_JSQL)) {
                LOGGER.debug(I18n.valueByKey("UPDATE_UPTODATE"));
            } else {
                LOGGER.warn(I18n.valueByKey("UPDATE_NEW_VERSION_AVAILABLE"));
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn(I18n.valueByKey("UPDATE_EXCEPTION"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "Menubar - Check update").start();
    }
}

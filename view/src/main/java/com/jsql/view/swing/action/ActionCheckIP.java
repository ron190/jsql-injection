package com.jsql.view.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Action performing a IP localisation test.
 */
public class ActionCheckIP implements ActionListener, Runnable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "ThreadCheckIP").start();
    }

    @Override
    public void run() {
        
        if (!MediatorHelper.model().getMediatorUtils().getProxyUtil().isLive(ShowOnConsole.YES)) {
            
            return;
        }

        try {
            LOGGER.trace(I18nUtil.valueByKey("LOG_IP_ADDRESS_CHECK"));
            String addressIp = MediatorHelper.model().getMediatorUtils().getConnectionUtil().getSource("http://checkip.amazonaws.com");
            LOGGER.info(
                String.format(
                    "%s %s",
                    I18nUtil.valueByKey("LOG_IP_ADDRESS_IS"),
                    addressIp
                )
            );
            
        } catch (MalformedURLException e) {
            
            LOGGER.warn(
                String
                .format(
                    "Malformed URL: %s",
                    e.getMessage()
                ),
                e
            );
            
        } catch (IOException e) {
            
            LOGGER.warn(
                String
                .format(
                    "Error during AWS test: %s",
                    e.getMessage()
                ),
                e
            );
        }
    }
}
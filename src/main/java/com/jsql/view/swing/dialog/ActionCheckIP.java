package com.jsql.view.swing.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import com.jsql.util.ConnectionUtil;
import com.jsql.util.ProxyUtil;

/**
 * Action performing a IP localisation test.
 */
public class ActionCheckIP implements ActionListener, Runnable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ActionCheckIP.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "Prefs - Action check IP").start();
    }

    @Override
    public void run() {
        if (!ProxyUtil.proxyIsResponding()) {
            return;
        }

        try {
            LOGGER.trace("Checking ip address...");
            String addressIp = ConnectionUtil.getSource("http://checkip.amazonaws.com");
            LOGGER.info("Your ip address is " + addressIp);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL: "+ e, e);
        } catch (IOException e) {
            LOGGER.warn("Error during AWS test: "+ e, e);
        }
    }
}
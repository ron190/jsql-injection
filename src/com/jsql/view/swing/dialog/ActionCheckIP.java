package com.jsql.view.swing.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.apache.log4j.Logger;

import com.jsql.model.injection.MediatorModel;

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
        // Test if proxy is available then apply settings
        if (MediatorModel.model().isProxyfied && !"".equals(MediatorModel.model().proxyAddress) && !"".equals(MediatorModel.model().proxyPort)) {
            try {
                LOGGER.info("Testing proxy...");
                new Socket(MediatorModel.model().proxyAddress, Integer.parseInt(MediatorModel.model().proxyPort)).close();
            } catch (Exception e) {
                LOGGER.warn("Proxy connection failed: " 
                        + MediatorModel.model().proxyAddress + ":" + MediatorModel.model().proxyPort
                        + "\nVerify your proxy informations or disable proxy setting.", e);
                return;
            }
            LOGGER.trace("Proxy is responding.");
        }

        BufferedReader in = null;
        try {
            LOGGER.info("Checking IP...");

            URL amazonUrl = new URL("http://checkip.amazonaws.com");
            HttpURLConnection connection = (HttpURLConnection) amazonUrl.openConnection();
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String ip2 = in.readLine();
            LOGGER.trace("Your IP information (AWS): " + ip2);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL: " + e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("Error during AWS test: " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Error closing AWS test: " + e.getMessage(), e);
                }
            }
        }
    }
}
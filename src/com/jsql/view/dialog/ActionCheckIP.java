package com.jsql.view.dialog;
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

import com.jsql.view.MediatorGUI;

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
        if (MediatorGUI.model().isProxyfied && !"".equals(MediatorGUI.model().proxyAddress) && !"".equals(MediatorGUI.model().proxyPort)) {
            try {
                LOGGER.info("Testing proxy...");
                new Socket(MediatorGUI.model().proxyAddress, Integer.parseInt(MediatorGUI.model().proxyPort)).close();
            } catch (Exception e) {
                LOGGER.warn("Proxy connection failed: " 
                        + MediatorGUI.model().proxyAddress + ":" + MediatorGUI.model().proxyPort
                        + "\nVerify your proxy informations or disable proxy setting.", e);
                return;
            }
            LOGGER.info("Proxy is responding.");
        }

        BufferedReader in = null;
        try {
            LOGGER.info("Checking IP...");

            URL whatismyip = new URL("http://checkip.amazonaws.com");
            HttpURLConnection con = (HttpURLConnection) whatismyip.openConnection();
            con.setDefaultUseCaches(false);
            con.setUseCaches(false);
            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Expires", "-1");

            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String ip2 = in.readLine();
            LOGGER.info("Your IP information (AWS): " + ip2);

            whatismyip = new URL("http://freegeoip.net/csv/");
            con = (HttpURLConnection) whatismyip.openConnection();
            con.setDefaultUseCaches(false);
            con.setUseCaches(false);
            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Expires", "-1");

            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            ip2 = in.readLine();
            LOGGER.info("Your IP information (freegeoip): " + ip2);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL: " + e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("Error during proxy test: " + e.getMessage(), e);
            LOGGER.warn("Use your browser to verify your proxy is working.");
        } finally {
            if (in != null) {
                try {
                    LOGGER.info("Checking IP done.");
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Error during proxy test: " + e.getMessage(), e);
                    LOGGER.warn("Use your browser to verify your proxy is working.");
                }
            }
        }
    }
}
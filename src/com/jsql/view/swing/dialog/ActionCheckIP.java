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
import org.json.JSONObject;

import com.jsql.view.swing.MediatorGUI;

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
            LOGGER.info("Your IP information (AWS): " + ip2);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL: " + e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("Error during AWS proxy test: " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Error closing AWS proxy test: " + e.getMessage(), e);
                }
            }
        }
        
        in = null;
        try {
            URL amazonUrl = new URL("http://www.telize.com/geoip");
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
            
            JSONObject jsonObject = new JSONObject(ip2);
            String ip = jsonObject.getString("ip");
            String continent_code = jsonObject.getString("continent_code");
            String country = jsonObject.getString("country");
            String region = jsonObject.getString("region");
            String city = jsonObject.getString("city");
            String postal_code = jsonObject.getString("postal_code");
            
            LOGGER.info("Your IP information (Telize): " + ip + " [" + continent_code + "," + country + "," + region + "," + city + "," + postal_code + "]");
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL: " + e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn("Error during Telize proxy test: " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    LOGGER.info("Checking IP done.");
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Error closing Telize proxy test: " + e.getMessage(), e);
                }
            }
        }
    }
}
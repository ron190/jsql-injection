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

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;

/**
 * Action performing a IP localisation test.
 */
public class ActionCheckIP implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Test if proxy is available then apply settings
                if (GUIMediator.model().isProxyfied && !GUIMediator.model().proxyAddress.equals("") && !GUIMediator.model().proxyPort.equals("")) {
                    try {
                        InjectionModel.LOGGER.info("Testing proxy...");
                        new Socket(GUIMediator.model().proxyAddress, Integer.parseInt(GUIMediator.model().proxyPort)).close();
                    } catch (Exception e) {
                        InjectionModel.LOGGER.warn("Proxy connection failed: " 
                                + GUIMediator.model().proxyAddress + ":" + GUIMediator.model().proxyPort
                                + "\nVerify your proxy informations or disable proxy setting.");
                        return;
                    }
                    InjectionModel.LOGGER.info("Proxy is responding.");
                }

                BufferedReader in = null;
                try {
                    InjectionModel.LOGGER.info("Checking IP...");

                    URL whatismyip = new URL("http://checkip.amazonaws.com");
                    HttpURLConnection con = (HttpURLConnection) whatismyip.openConnection();
                    con.setDefaultUseCaches(false);
                    con.setUseCaches(false);
                    con.setRequestProperty("Pragma", "no-cache");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Expires", "-1");

                    in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String ip2 = in.readLine();
                    InjectionModel.LOGGER.info("Your IP information (AWS): " + ip2);

                    whatismyip = new URL("http://freegeoip.net/csv/");
                    con = (HttpURLConnection) whatismyip.openConnection();
                    con.setDefaultUseCaches(false);
                    con.setUseCaches(false);
                    con.setRequestProperty("Pragma", "no-cache");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Expires", "-1");

                    in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    ip2 = in.readLine();
                    InjectionModel.LOGGER.info("Your IP information (freegeoip): " + ip2);
                } catch (MalformedURLException e) {
                    InjectionModel.LOGGER.warn("Malformed URL: " + e.getMessage());
                } catch (IOException e) {
                    InjectionModel.LOGGER.warn("Error during proxy test: " + e.getMessage());
                    InjectionModel.LOGGER.warn("Use your browser to verify your proxy is working.");
                } finally {
                    if (in != null) {
                        try {
                            InjectionModel.LOGGER.info("Checking IP done.");
                            in.close();
                        } catch (IOException e) {
                            InjectionModel.LOGGER.warn("Error during proxy test: " + e.getMessage());
                            InjectionModel.LOGGER.warn("Use your browser to verify your proxy is working.");
                        }
                    }
                }
            }
        }, "Prefs - Action check IP").start();
    }
}
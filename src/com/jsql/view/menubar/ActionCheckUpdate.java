package com.jsql.view.menubar;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.view.MediatorGUI;

public class ActionCheckUpdate implements ActionListener, Runnable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ActionCheckUpdate.class);

    @Override
    public void run() {
        try {
            LOGGER.info("Checking updates...");
            URLConnection con = new URL("http://jsql-injection.googlecode.com/git/.version").openConnection();
            con.setReadTimeout(60000);
            con.setConnectTimeout(60000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();

            Float gitVersion = Float.parseFloat(pageSource);
            MediatorGUI.model();
            if (gitVersion <= Float.parseFloat(InjectionModel.JSQLVERSION)) {
                LOGGER.info("jSQL Injection is up to date.");
            } else {
                LOGGER.warn("A new version of jSQL Injection is available.");
                Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/downloads/list"));
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("An error occured while checking updates, download the latest version from official website :");
            LOGGER.warn("http://code.google.com/p/jsql-injection/downloads/list");
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.warn("An error occured while checking updates, download the latest version from official website :");
            LOGGER.warn("http://code.google.com/p/jsql-injection/downloads/list");
            LOGGER.error(e, e);
        } catch (URISyntaxException e) {
            LOGGER.warn("An error occured while checking updates, download the latest version from official website :");
            LOGGER.warn("http://code.google.com/p/jsql-injection/downloads/list");
            LOGGER.error(e, e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "Menubar - Check update").start();
    }
}

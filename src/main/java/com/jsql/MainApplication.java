package com.jsql;

import java.awt.HeadlessException;
import java.io.File;
import java.util.Locale;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.CertificateUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.view.swing.JFrameSoftware;
import com.jsql.view.swing.MediatorGui;

public class MainApplication {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(MainApplication.class);
    
    // Keep referenced class for Maven shade minimizeJar
    Appender build = new ConsoleAppender();
    
    private MainApplication() {
        // nothing
    }
    
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     */
    public static void main(String[] args) {
        
        CertificateUtil.ignoreCertificationChain();
        ExceptionUtil.setUncaughtExceptionHandler();
        ProxyUtil.setProxy();
        PreferencesUtil.loadSavedPreferences();
        AuthenticationUtil.setKerberosCifs();
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        
        try {
            JFrameSoftware frame = new JFrameSoftware();
            model.addObserver(frame);
            MediatorGui.register(frame);
        } catch (HeadlessException e) {
            LOGGER.error("HeadlessException: command line execution in jSQL not supported yet", e);
        }
        
        model.sendVersionToView();
        
        if (!ProxyUtil.proxyIsResponding()) {
            return;
        }
        
        if (PreferencesUtil.isCheckUpdateActivated()) {
            GitUtil.checkUpdate();
        }
        
        File f = new File("com.jsql.i18n.jsql_"+ Locale.getDefault().getLanguage() +".properties");
        if (!f.exists() && !"en".equals(Locale.getDefault().getLanguage())) { 
            String languageDisplayed = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
            LOGGER.debug(
                "Language "+ languageDisplayed +" is not supported. "
                + "Contribute and translate pieces of jSQL into "+ languageDisplayed +": "
                + "open menu [Community] using [Advanced] arrow on the right, choose [I help translate jSQL], "
                + "translate some text into "+ languageDisplayed +" then click on [Send], it's that simple."
            );
        }
        
        GitUtil.showNews();
    }
}

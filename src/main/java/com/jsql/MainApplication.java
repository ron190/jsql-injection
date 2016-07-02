package com.jsql;

import java.awt.HeadlessException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.CertificateUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.view.swing.FrameJSql;
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
        ProxyUtil.initialize();
        PreferencesUtil.initialize();
        AuthenticationUtil.initialize();
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        
        try {
            FrameJSql gui = new FrameJSql();
            model.addObserver(gui);
            MediatorGui.register(gui);
        } catch (HeadlessException e) {
            LOGGER.error("HeadlessException: command line execution in jSQL not supported yet.");
        }
        
        model.sendVersionToView();
        
        if (PreferencesUtil.isCheckingUpdate) {
            GitUtil.checkUpdate();
        }
    }
}

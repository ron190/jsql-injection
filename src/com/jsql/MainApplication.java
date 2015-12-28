package com.jsql;

import java.awt.HeadlessException;

import org.apache.log4j.Logger;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.AuthenticationTools;
import com.jsql.tool.CertTools;
import com.jsql.tool.ExceptionTools;
import com.jsql.tool.GitTools;
import com.jsql.tool.ProxyTools;
import com.jsql.view.swing.JFrameGUI;
import com.jsql.view.swing.MediatorGUI;

public class MainApplication {
    /**
     * Using default log4j.properties from root /
     */
    public static final Logger LOGGER = Logger.getLogger(MainApplication.class);
    
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     */
    public static void main(String[] args) {
        CertTools.ignoreCertificationChain();
        
        ExceptionTools.setUncaughtExceptionHandler();
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        
        ProxyTools.init();
        
        AuthenticationTools.init();
        
        try {
            MediatorGUI.register(new JFrameGUI());
        } catch (HeadlessException e) {
            LOGGER.error("HeadlessException: command line execution in jSQL not supported yet.");
        }
        
        model.instanciationDone();
        
        if (model.checkUpdateAtStartup) {
            GitTools.checkVersion();
        }
    }
}

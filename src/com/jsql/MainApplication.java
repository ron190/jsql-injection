package com.jsql;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.SwingUtilities;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
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

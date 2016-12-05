package com.jsql;

import java.awt.HeadlessException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.util.AuthenticationUtil;
import com.jsql.util.CertificateUtil;
import com.jsql.util.ExceptionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.MediatorGui;

/**
 * Main class of the application and called from the .jar.
 * This class set the general environment of execution and start the software.
 */
public class MainApplication {
	
	static {
		// TODO define in properties
    	PropertyConfigurator.configure(MainApplication.class.getResource("/log4j2.properties"));
	}
	
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
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
    	
    	// Configure global environnement settings
        CertificateUtil.ignoreCertificationChain();
        ExceptionUtil.setUncaughtExceptionHandler();
        ProxyUtil.setProxy();
        PreferencesUtil.loadSavedPreferences();
        AuthenticationUtil.setKerberosCifs();
        
        // Initialize MVC
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        
        try {
            JFrameView view = new JFrameView();
            MediatorGui.register(view);
            
            model.addObserver(view);
        } catch (HeadlessException e) {
            LOGGER.error("HeadlessException, command line execution in jSQL not supported yet: "+ e, e);
            return;
        }
        
        model.displayVersion();
        
        // Check application status
        if (!ProxyUtil.proxyIsResponding(ShowOnConsole.YES)) {
            return;
        }
        
        if (PreferencesUtil.isCheckUpdateActivated()) {
            GitUtil.checkUpdate(ShowOnConsole.NO);
        }
        
        I18n.checkCurrentLanguage();
        GitUtil.showNews();
        
    }
    
}

package com.jsql;

import java.awt.AWTError;
import java.awt.HeadlessException;
import java.net.URISyntaxException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

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
	
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    // Keep referenced class for Maven shade minimizeJar
    @SuppressWarnings("unused")
    private Appender build = new ConsoleAppender();
    
    private MainApplication() {
        // nothing
    }
    
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws URISyntaxException {
        
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
            
            model.addObserver(view.getObserver());
        } catch (HeadlessException e) {
            LOGGER.error("HeadlessException, command line execution in jSQL not supported yet: "+ e.getMessage(), e);
            return;
        } catch (AWTError e) {
            // Fix #22668: Assistive Technology not found
            LOGGER.error("Java Access Bridge missing or corrupt, check your access bridge definition in JDK_HOME/jre/lib/accessibility.properties: "+ e.getMessage(), e);
            return;
        }
        
        model.displayVersion();
        
        // Check application status
        if (!ProxyUtil.isChecked(ShowOnConsole.YES)) {
            return;
        }
        
        if (PreferencesUtil.isCheckUpdateActivated()) {
            GitUtil.checkUpdate(ShowOnConsole.NO);
        }
        
        I18n.checkCurrentLanguage();
        GitUtil.showNews();
        
    }
    
}

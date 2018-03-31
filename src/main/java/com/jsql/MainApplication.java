package com.jsql;

import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.net.URISyntaxException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.databind.ext.Java7Support;
import com.fasterxml.jackson.databind.ext.Java7SupportImpl;
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
        PreferencesUtil.loadSavedPreferences();
        
        if (PreferencesUtil.is4K()) {
            System.setProperty("sun.java2d.uiScale", "2.5");
        }
    }
	
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    // Keep referenced class for Maven shade minimizeJar
    @SuppressWarnings("unused")
    private Appender consoleAppender = new ConsoleAppender();
    @SuppressWarnings("unused")
    private WstxInputFactory wstxInputFactory = new WstxInputFactory();
    @SuppressWarnings("unused")
    private WstxOutputFactory wstxOutputFactory = new WstxOutputFactory();
    @SuppressWarnings("unused")
    private Java7Support java7SupportImpl = new Java7SupportImpl();
    
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
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        
        if (width >= 3840 && !PreferencesUtil.is4K()) {
            LOGGER.warn("Your screen seems compatible with 4K resolution, please activate high-definition mode in Preferences");
        }
        
    }
    
}

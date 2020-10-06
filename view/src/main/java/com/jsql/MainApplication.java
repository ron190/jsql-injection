package com.jsql;

import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.net.URISyntaxException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.util.CertificateUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Main class of the application and called from the .jar.
 * This class set the general environment of execution and start the software.
 */
public class MainApplication {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private static InjectionModel injectionModel;
    
    static {
        
        injectionModel = new InjectionModel();
        
        if (!"true".equals(System.getenv("FROM_CI_PIPELINE"))) {
            
            injectionModel.getMediatorUtils().getPreferencesUtil().loadSavedPreferences();
        }
        
        MainApplication.apply4K();
    }
    
    // Keep referenced class for Maven shade minimizeJar
    @SuppressWarnings("unused")
    private Appender consoleAppender = new ConsoleAppender();
    
    private MainApplication() {
        // nothing
    }
    
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     * @throws URISyntaxException
     */
    public static void main(String[] args) {
        
        // Initialize MVC
        MediatorHelper.register(injectionModel);
        
        // Configure global environment settings
        CertificateUtil.ignoreCertificationChain();
        injectionModel.getMediatorUtils().getExceptionUtil().setUncaughtExceptionHandler();
        injectionModel.getMediatorUtils().getProxyUtil().initializeProxy();
        injectionModel.getMediatorUtils().getAuthenticationUtil().setKerberosCifs();
        
        try {
            JFrameView view = new JFrameView();
            MediatorHelper.register(view);
            
            injectionModel.addObserver(view.getObserver());
            
        } catch (HeadlessException e) {
            
            LOGGER.error("HeadlessException, command line execution in jSQL not supported yet: "+ e.getMessage(), e);
            return;
            
        } catch (AWTError e) {
            
            // Fix #22668: Assistive Technology not found
            LOGGER.error("Java Access Bridge missing or corrupt, check your access bridge definition in JDK_HOME/jre/lib/accessibility.properties: "+ e.getMessage(), e);
            return;
        }
        
        injectionModel.displayVersion();
        
        // Check application status
        if (!injectionModel.getMediatorUtils().getProxyUtil().isLive(ShowOnConsole.YES)) {
            return;
        }
        
        if (injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingUpdate()) {
            
            injectionModel.getMediatorUtils().getGitUtil().checkUpdate(ShowOnConsole.NO);
        }
        
        I18nUtil.checkCurrentLanguage();
        injectionModel.getMediatorUtils().getGitUtil().showNews();
        
        MainApplication.check4K();
    }
    
    private static void apply4K() {
        
        if (injectionModel.getMediatorUtils().getPreferencesUtil().is4K()) {
            
            // jdk >= 9
            System.setProperty("sun.java2d.uiScale", "2.5");
        }
    }
    
    private static void check4K() {
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        
        if (width >= 3840 && !injectionModel.getMediatorUtils().getPreferencesUtil().is4K()) {
            
            LOGGER.warn("Your screen seems compatible with 4K resolution, please activate high-definition mode in Preferences");
        }
    }
}

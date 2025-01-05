package com.jsql;

import com.jsql.model.InjectionModel;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Main class of the application and called from the .jar.
 * This class set the general environment of execution and start the software.
 */
public class MainApplication {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final InjectionModel injectionModel;  // required to load preferences first
    
    static {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "connection,content-length,expect,host,upgrade");

        if (GraphicsEnvironment.isHeadless()) {
            LOGGER.log(Level.ERROR, "Headless runtime not supported, use default Java runtime instead");
            System.exit(1);
        }

        injectionModel = new InjectionModel();
        injectionModel.getMediatorUtils().getPreferencesUtil().loadSavedPreferences();

        var nameTheme = injectionModel.getMediatorUtils().getPreferencesUtil().getThemeFlatLafName();
        UiUtil.applyTheme(nameTheme);  // required init but not enough, reapplied next
        MainApplication.apply4K();
    }

    private MainApplication() {
        // nothing
    }
    
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     */
    public static void main(String[] args) {
        
        MediatorHelper.register(injectionModel);
        
        injectionModel.getMediatorUtils().getExceptionUtil().setUncaughtExceptionHandler();
        injectionModel.getMediatorUtils().getProxyUtil().initializeProxy();
        injectionModel.getMediatorUtils().getAuthenticationUtil().setKerberosCifs();
        
        try {
            var view = new JFrameView();
            MediatorHelper.register(view);
            injectionModel.subscribe(view.getSubscriber());
        } catch (HeadlessException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "HeadlessException, command line execution not supported: %s", e);
            return;
        } catch (AWTError e) {
            // Fix #22668: Assistive Technology not found
            LOGGER.log(
                LogLevelUtil.CONSOLE_JAVA,
                String.format(
                    "Java Access Bridge missing or corrupt, check your access bridge definition in JDK_HOME/jre/lib/accessibility.properties: %s",
                    e.getMessage()
                ),
                e
            );
            return;
        }
        
        I18nUtil.checkCurrentLanguage();
        MainApplication.check4K();
        SwingUtilities.invokeLater(() -> AppMenubar.applyTheme(
            injectionModel.getMediatorUtils().getPreferencesUtil().getThemeFlatLafName()  // refresh missing components
        ));

        if (injectionModel.getMediatorUtils().getProxyUtil().isNotLive(ShowOnConsole.YES)) {  // network access
            return;
        }
        if (injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingUpdate()) {
            injectionModel.getMediatorUtils().getGitUtil().checkUpdate(ShowOnConsole.NO);
        }
        injectionModel.getMediatorUtils().getGitUtil().showNews();
    }
    
    private static void apply4K() {  // required not in UiUtil before frame is set
        if (injectionModel.getMediatorUtils().getPreferencesUtil().is4K()) {
            System.setProperty("sun.java2d.uiScale", "2.5");  // jdk >= 9
        }
    }

    private static void check4K() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        if (width >= 3840 && !injectionModel.getMediatorUtils().getPreferencesUtil().is4K()) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Screen compatible with resolution 4K, enable high-definition in Preferences");
        }
    }
}

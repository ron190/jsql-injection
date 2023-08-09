package com.jsql.view.swing.action;

import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action performing a IP localization test.
 */
public class ActionCheckIP implements ActionListener, Runnable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "ThreadCheckIP").start();
    }

    @Override
    public void run() {
        
        if (!MediatorHelper.model().getMediatorUtils().getProxyUtil().isLive(ShowOnConsole.YES)) {
            
            return;
        }

        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("LOG_IP_ADDRESS_CHECK"));
        String addressIp = MediatorHelper.model().getMediatorUtils().getConnectionUtil().getSource("http://checkip.amazonaws.com");
        LOGGER.log(
            LogLevelUtil.CONSOLE_INFORM,
            "{} {}",
            () -> I18nUtil.valueByKey("LOG_IP_ADDRESS_IS"),
            () -> addressIp
        );
    }
}
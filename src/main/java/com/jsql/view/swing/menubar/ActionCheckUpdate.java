package com.jsql.view.swing.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.GitUtil;

public class ActionCheckUpdate implements ActionListener, Runnable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ActionCheckUpdate.class);

    @Override
    public void run() {
        try {
            LOGGER.trace(I18n.valueByKey("UPDATE_LOADING"));
            
            if (GitUtil.obj == null) {
                String json = ConnectionUtil.getSource("https://raw.githubusercontent.com/ron190/jsql-injection/master/web/services/jsql-injection.json");
                GitUtil.obj = new JSONObject(json);
            }
            
            Float versionGit = Float.parseFloat(GitUtil.obj.getString("version"));
            if (versionGit > Float.parseFloat(InjectionModel.VERSION_JSQL)) {
                LOGGER.warn(I18n.valueByKey("UPDATE_NEW_VERSION"));
            } else {
                LOGGER.debug(I18n.valueByKey("UPDATE_UPTODATE"));
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn(I18n.valueByKey("UPDATE_EXCEPTION"), e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "Menubar - Check update").start();
    }
}

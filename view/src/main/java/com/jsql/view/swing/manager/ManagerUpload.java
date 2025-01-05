/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.model.exception.JSqlException;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Manager to upload files to the host.
 */
public class ManagerUpload extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Build the manager panel.
     */
    public ManagerUpload() {
        super(UiUtil.PATH_WEB_FOLDERS);
        this.defaultText = "UPLOAD_RUN_BUTTON_LABEL";

        final JTextField shellURL = new JPopupTextField(I18nUtil.valueByKey("UPLOAD_URL_LABEL")).getProxy();
        String urlTooltip = I18nUtil.valueByKey("UPLOAD_URL_TOOLTIP");
        shellURL.setToolTipText(urlTooltip);
        this.initializeRunButton(shellURL);

        this.privilege = new JLabel(I18nUtil.valueByKey("PRIVILEGE_LABEL"), UiUtil.SQUARE.icon, SwingConstants.LEFT);
        I18nViewUtil.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setToolTipText(I18nUtil.valueByKey("PRIVILEGE_TOOLTIP"));
        this.progressBar.setVisible(false);
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        var southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    private void initializeRunButton(final JTextField shellURL) {
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey(this.defaultText, this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("UPLOAD_RUN_BUTTON_TOOLTIP"));
        this.run.setEnabled(false);
        this.run.addActionListener(actionEvent -> this.initializeRunAction(shellURL));
    }

    private void initializeRunAction(final JTextField shellURL) {
        
        if (ManagerUpload.this.listPaths.getSelectedValuesList().isEmpty()) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select directory(ies) to upload a file into");
            return;
        }

        final var filechooser = new JFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());
        filechooser.setDialogTitle(I18nUtil.valueByKey("UPLOAD_DIALOG_TEXT"));
        
        // Fix #2402: NullPointerException on showOpenDialog()
        // Fix #40547: ClassCastException on showOpenDialog()
        try {
            int returnVal = filechooser.showOpenDialog(MediatorHelper.frame());
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            this.uploadFiles(shellURL, filechooser);
        } catch (NullPointerException | ClassCastException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private void uploadFiles(final JTextField shellURL, final JFileChooser fileChooser) {
        for (final Object path: ManagerUpload.this.listPaths.getSelectedValuesList()) {
            new Thread(
                () -> {
                    try {
                        var file = fileChooser.getSelectedFile();
                        ManagerUpload.this.progressBar.setVisible(true);
                        MediatorHelper.model().getResourceAccess().uploadFile(path.toString(), shellURL.getText(), file);
                    } catch (JSqlException e) {
                        LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format("Payload creation error: %s", e.getMessage()));
                    } catch (IOException e) {
                        LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format("Posting file failed: %s", e.getMessage()), e);
                    } catch (InterruptedException e) {
                        LOGGER.log(LogLevelUtil.IGNORE, e, e);
                        Thread.currentThread().interrupt();
                    }
                },
                "ThreadUpload"
            ).start();
        }
    }
}

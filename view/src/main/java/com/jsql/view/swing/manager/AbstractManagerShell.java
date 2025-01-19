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
import com.jsql.view.swing.manager.util.ModelItemType;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manager for uploading PHP webshell to the host and send system commands.
 */
public abstract class AbstractManagerShell extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    protected final JTextField textfieldUrlShell;
    
    /**
     * Build the manager panel.
     */
    protected AbstractManagerShell() {
        super("swing/list/payload.txt");

        var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey("SHELL_URL_TOOLTIP")));
        var placeholderResult = new JTextFieldPlaceholder(I18nUtil.valueByKey("SHELL_URL_LABEL")) {
            @Override
            public JToolTip createToolTip() {
                return tooltip.get();
            }
        };
        this.textfieldUrlShell = new JPopupTextField(placeholderResult).getProxy();
        I18nViewUtil.addComponentForKey("SHELL_URL_LABEL", this.textfieldUrlShell);
        I18nViewUtil.addComponentForKey("SHELL_URL_TOOLTIP", tooltip.get());
        this.textfieldUrlShell.setToolTipText(I18nUtil.valueByKey("SHELL_URL_TOOLTIP"));

        this.buildRunButton("SHELL_RUN_BUTTON_LABEL", "SHELL_RUN_BUTTON_TOOLTIP");
        this.run.setEnabled(false);
        this.buildPrivilege();

        var southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(this.textfieldUrlShell);
        southPanel.add(this.lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    protected abstract void createPayload(String pathShell, String urlShell, File fileToUpload) throws JSqlException;

    protected class ActionExploit implements ActionListener {
        private final JComboBox<ModelItemType> comboBoxExploitTypes;

        public ActionExploit(JComboBox<ModelItemType> comboBoxExploitTypes) {
            this.comboBoxExploitTypes = comboBoxExploitTypes;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            var modelSelectItem = (ModelItemType) this.comboBoxExploitTypes.getSelectedItem();
            if (ManagerExploit.KEY_UDF_TAB.equals(Objects.requireNonNull(modelSelectItem).getKeyLabel())) {
                new SwingWorker<>() {
                    @Override
                    protected Object doInBackground() { Thread.currentThread().setName("SwingWorkerExploitUdf");
                        AbstractManagerShell.this.start(null, null, null);
                        return null;
                    }
                }.doInBackground();
                return;
            }
            if (AbstractManagerShell.this.listPaths.getSelectedValuesList().isEmpty()) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select at least one directory in the list");
                return;
            }
            String urlShell = AbstractManagerShell.this.textfieldUrlShell.getText();
            if (!urlShell.isEmpty() && !urlShell.matches("(?i)^https?://.*")) {
                if (!urlShell.matches("(?i)^\\w+://.*")) {
                    LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Undefined shell URL protocol, forcing to [https://]");
                    urlShell = "https://"+ urlShell;
                } else {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Unknown URL protocol");
                    return;
                }
            }
            if (StringUtils.isNotEmpty(urlShell)) {
                try {
                    new URI(urlShell);
                } catch (URISyntaxException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format("Incorrect URL: %s", e.getMessage()));
                    return;
                }
            }

            AtomicReference<File> fileToUpload = new AtomicReference<>();
            if (ManagerExploit.KEY_UPLOAD_TAB.equals(modelSelectItem.getKeyLabel())) {
                fileToUpload.set(AbstractManagerShell.chooseFile());
                if (fileToUpload.get() == null) {
                    return;
                }
            }

            String urlShellFinal = urlShell;
            new SwingWorker<>() {
                @Override
                protected Object doInBackground() { Thread.currentThread().setName("SwingWorkerExploitNonUdf");
                    AbstractManagerShell.this.horizontalGlue.setVisible(false);
                    AbstractManagerShell.this.progressBar.setVisible(true);
                    AbstractManagerShell.this.listPaths.getSelectedValuesList().forEach(remotePathFolder -> {
                        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, String.format("Checking path [%s]...", remotePathFolder));
                        AbstractManagerShell.this.start(remotePathFolder.toString(), urlShellFinal, fileToUpload.get());
                    });
                    AbstractManagerShell.this.endProcess();
                    return null;
                }
            }.doInBackground();
        }
    }

    private static File chooseFile() {
        var filechooser = new JFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());
        filechooser.setDialogTitle(I18nUtil.valueByKey("UPLOAD_DIALOG_TEXT"));
        int returnVal = filechooser.showOpenDialog(MediatorHelper.frame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return filechooser.getSelectedFile();
        }
        return null;
    }

    private void start(String remotePathFolder, String urlShellFinal, File fileToUpload) {
        try {
            this.createPayload(remotePathFolder, urlShellFinal, fileToUpload);
        } catch (JSqlException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format("Payload creation failure: %s", e.getMessage()));
        }
    }
}

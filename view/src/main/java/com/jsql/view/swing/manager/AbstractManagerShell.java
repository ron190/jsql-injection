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
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manager for uploading PHP webshell to the host and send system commands.
 */
public abstract class AbstractManagerShell extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final JTextField textfieldUrlShell = new JPopupTextField(I18nUtil.valueByKey("SHELL_URL_LABEL")).getProxy();
    
    /**
     * Build the manager panel.
     */
    protected AbstractManagerShell() {
        this.setLayout(new BorderLayout());
        
        List<ItemList> itemsList = new ArrayList<>();
        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(UiUtil.PATH_WEB_FOLDERS);
            var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                itemsList.add(new ItemList(line));
            }
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }

        this.listPaths = new DnDList(itemsList);
        this.add(new JScrollPane(this.listPaths), BorderLayout.CENTER);

        String urlTooltip = I18nUtil.valueByKey("SHELL_URL_TOOLTIP");
        this.textfieldUrlShell.setToolTipText(urlTooltip);

        JPanel lastLine = this.initializeRunButtonPanel();
        var southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(this.textfieldUrlShell);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }
    
    protected abstract void createPayload(String pathShell, String urlShell) throws JSqlException;

    private JPanel initializeRunButtonPanel() {
        this.defaultText = "SHELL_RUN_BUTTON_LABEL";
        
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey(this.defaultText, this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("SHELL_RUN_BUTTON_TOOLTIP"));
        this.run.setEnabled(false);
        this.run.addActionListener(new ActionCreationShell());

        this.privilege = new JLabel(I18nUtil.valueByKey("PRIVILEGE_LABEL"), UiUtil.SQUARE.icon, SwingConstants.LEFT);
        I18nViewUtil.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setToolTipText(I18nUtil.valueByKey("PRIVILEGE_TOOLTIP"));

        lastLine.add(Box.createHorizontalStrut(5));
        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalStrut(5));
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);
        return lastLine;
    }
    
    private class ActionCreationShell implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (AbstractManagerShell.this.listPaths.getSelectedValuesList().isEmpty()) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select at least one directory in the list");
                return;
            }

            String urlShell = AbstractManagerShell.this.textfieldUrlShell.getText();
            if (!urlShell.isEmpty() && !urlShell.matches("(?i)^https?://.*")) {
                if (!urlShell.matches("(?i)^\\w+://.*")) {
                    LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Undefined shell URL protocol, forcing to [http://]");
                    urlShell = "http://"+ urlShell;
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
            
            String urlShellFinal = urlShell;
            AbstractManagerShell.this.listPaths
                .getSelectedValuesList()
                .forEach(pathShell -> new Thread(
                    () -> {
                        try {
                            AbstractManagerShell.this.createPayload(pathShell.toString(), urlShellFinal);
                        } catch (JSqlException e) {
                            LOGGER.log(
                                LogLevelUtil.CONSOLE_ERROR,
                                String.format("Payload creation error: %s", e.getMessage())
                            );
                        }
                    },
                    "ThreadGetShell"
                ).start());
        }
    }
}

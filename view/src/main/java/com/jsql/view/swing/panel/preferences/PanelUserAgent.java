package com.jsql.view.swing.panel.preferences;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PanelUserAgent extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final JCheckBox checkboxIsCustomUserAgent = new JCheckBox("Randomize agent with the following list :", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUserAgentRandom());

    public PanelUserAgent(PanelPreferences panelPreferences) {
        var userAgents = new StringBuilder();
        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream("swing/list/user-agent.txt");
            var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                userAgents.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }

        checkboxIsCustomUserAgent.addActionListener(panelPreferences.getActionListenerSave());
        JTextArea textfieldCustomUserAgent = new JPopupTextArea(new JTextAreaPlaceholder("User agent list")).getProxy();
        textfieldCustomUserAgent.setMinimumSize(new Dimension(40000, 100));
        textfieldCustomUserAgent.getCaret().setBlinkRate(500);
        textfieldCustomUserAgent.setText(userAgents.toString());
        MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(userAgents.toString());
        textfieldCustomUserAgent.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(
                    textfieldCustomUserAgent.getText()
                );
            }
        });
        var scrollPane = new JScrollPane(textfieldCustomUserAgent);

        var labelOrigin = new JLabel("<html><b>Connection user agent</b></html>");
        labelOrigin.setBorder(PanelGeneral.MARGIN);

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);

        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(labelOrigin)
                .addComponent(checkboxIsCustomUserAgent)
                .addComponent(scrollPane)
            )
        );

        groupLayout.setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelOrigin)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(checkboxIsCustomUserAgent)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(scrollPane)
            )
        );
    }

    public JCheckBox getCheckboxIsCustomUserAgent() {
        return checkboxIsCustomUserAgent;
    }
}

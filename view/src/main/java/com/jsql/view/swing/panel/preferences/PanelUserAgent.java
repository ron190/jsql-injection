package com.jsql.view.swing.panel.preferences;

import com.jsql.util.StringUtil;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.*;

public class PanelUserAgent extends JPanel {
    
    private final JCheckBox checkboxIsCustomUserAgent = new JCheckBox("Randomize agent with the following list:", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUserAgentRandom());

    public PanelUserAgent(PanelPreferences panelPreferences) {
        var userAgents = StringUtil.getFile("swing/list/user-agent.txt");
        MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(userAgents);

        this.checkboxIsCustomUserAgent.addActionListener(panelPreferences.getActionListenerSave());

        JTextArea textfieldCustomUserAgent = new JPopupTextArea(new JTextAreaPlaceholder("User agent list")).getProxy();
        textfieldCustomUserAgent.setMinimumSize(new Dimension(40000, 100));
        textfieldCustomUserAgent.getCaret().setBlinkRate(500);
        textfieldCustomUserAgent.setText(userAgents);
        MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(userAgents);
        textfieldCustomUserAgent.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(
                    textfieldCustomUserAgent.getText()
                );
            }
        });
        textfieldCustomUserAgent.setCaretPosition(0);
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
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(labelOrigin)
                .addComponent(this.checkboxIsCustomUserAgent)
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
                .addComponent(this.checkboxIsCustomUserAgent)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(scrollPane)
            )
        );
    }

    public JCheckBox getCheckboxIsCustomUserAgent() {
        return this.checkboxIsCustomUserAgent;
    }
}

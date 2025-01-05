package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.panel.preferences.listener.SpinnerMouseWheelListener;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.stream.Stream;

public class PanelConnection extends JPanel {

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox("Follow redirection", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsHttp2Disabled = new JCheckBox("Disable HTTP/2", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isHttp2Disabled());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox("Disable connection test", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxIsNotProcessingCookies = new JCheckBox("Disable session cookies", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies());
    private final JCheckBox checkboxIsProcessingCsrf = new JCheckBox("Process CSRF token (search for XSRF-TOKEN/.../_csrf ; then set X-XSRF-TOKEN/.../_csrf)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());
    private final JCheckBox checkboxIsLimitingThreads = new JCheckBox("Limit processing threads :", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());
    private final JCheckBox checkboxIsConnectionTimeout = new JCheckBox("Set timeout :", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isConnectionTimeout());
    private final JCheckBox checkboxIsUnicodeDecodeDisabled = new JCheckBox("Disable Unicode decoding in response", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled());
    private final JCheckBox checkboxIsUrlDecodeDisabled = new JCheckBox("Disable Url decoding in response", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlDecodeDisabled());
    
    private final JSpinner spinnerLimitingThreads = new JSpinner();
    private final JSpinner spinnerConnectionTimeout = new JSpinner();
    
    private final JCheckBox checkboxIsCsrfUserTag = new JCheckBox("Custom CSRF :", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag());
    private final JTextField textfieldCustomCsrfInputToken = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTag());
    private final JTextField textfieldCustomCsrfOutputToken = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTagOutput());
    
    public PanelConnection(PanelPreferences panelPreferences) {
        this.checkboxIsFollowingRedirection.setToolTipText(
            "<html>HTTP 3XX response indicates page's location has changed.<br>" +
            "Redirect automatically to the new location.</html>"
        );
        this.checkboxIsHttp2Disabled.setToolTipText("<html>Some website works with HTTP/1.1 only.<br>Disable HTTP/2 in favor of HTTP/1.1.</html>");
        this.checkboxIsUnicodeDecodeDisabled.setToolTipText(
            "<html>Unicode entities \\uXXXX are decoded to raw characters by default.<br>" +
            "Check to disable this behavior.</html>"
        );
        this.checkboxIsUrlDecodeDisabled.setToolTipText(
            "<html>Url entities %XX are decoded to raw characters by default.<br>" +
            "Check to disable this behavior.</html>"
        );
        this.checkboxIsNotTestingConnection.setToolTipText(
            "<html>Connectivity to target is checked first to stop when target is dead, like with 404 Not Found.<br>"
            + "Check option to process with injection whatever problem exists.</html>"
        );
        this.checkboxIsNotProcessingCookies.setToolTipText(
            "<html>Cookies persist data between connections.<br>" +
            "Sometimes persisted data like user's session is messing with injection and have to be ignored.</html>"
        );
        this.checkboxIsLimitingThreads.setToolTipText(
            "<html>Various tasks are processed in parallel to save time.<br>"
            + "Target that detects too much calls during a period can close the connection,<br>"
            + "in that case it helps lowering threads or keeping a single thread.</html>"
        );
        this.checkboxIsConnectionTimeout.setToolTipText("End connection when target takes this long to answer, it can be lowered down to save time in some cases.");
        this.checkboxIsProcessingCsrf.setToolTipText(
            "<html>Search for common CSRF tokens in response header and body.<br>" +
            "Inject back the value in the query, header and request body.</html>"
        );
        
        var panelConnectionTimeout = new JPanel();
        panelConnectionTimeout.setLayout(new BoxLayout(panelConnectionTimeout, BoxLayout.X_AXIS));
        panelConnectionTimeout.add(new JLabel("Close connection after "));
        panelConnectionTimeout.add(this.spinnerConnectionTimeout);
        panelConnectionTimeout.add(new JLabel(" s ; default 15s"));
        panelConnectionTimeout.setMaximumSize(new Dimension(125, this.spinnerConnectionTimeout.getPreferredSize().height));
        int countConnectionTimeout = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countConnectionTimeout();
        var spinnerConnectionModel = new SpinnerNumberModel(
            countConnectionTimeout <= 0 ? 15 : countConnectionTimeout,
            1,
            30,
            1
        );
        this.spinnerConnectionTimeout.setModel(spinnerConnectionModel);
        this.spinnerConnectionTimeout.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.spinnerConnectionTimeout.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));

        var panelThreadCount = new JPanel();
        panelThreadCount.setLayout(new BoxLayout(panelThreadCount, BoxLayout.X_AXIS));
        panelThreadCount.add(new JLabel("Use "));
        panelThreadCount.add(this.spinnerLimitingThreads);
        panelThreadCount.add(new JLabel(" thread(s) ; default 5 threads"));
        panelThreadCount.setMaximumSize(new Dimension(125, this.spinnerLimitingThreads.getPreferredSize().height));
        int countLimitingThreads = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countLimitingThreads();
        var spinnerNumberModel = new SpinnerNumberModel(
            countLimitingThreads <= 0 ? 10 : countLimitingThreads,
            1,
            100,
            1
        );
        this.spinnerLimitingThreads.setModel(spinnerNumberModel);
        this.spinnerLimitingThreads.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.spinnerLimitingThreads.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));

        this.checkboxIsCsrfUserTag.setToolTipText(
            "<html>Process custom CSRF.<br>" +
            "Read value from input token and write value to output token.</html>"
        );

        var panelCsrfUserTagInput = new JPanel();
        panelCsrfUserTagInput.setLayout(new BoxLayout(panelCsrfUserTagInput, BoxLayout.X_AXIS));
        panelCsrfUserTagInput.add(new JLabel("Input token to find "));
        panelCsrfUserTagInput.add(this.textfieldCustomCsrfInputToken);
        panelCsrfUserTagInput.setMaximumSize(new Dimension(450, this.textfieldCustomCsrfInputToken.getPreferredSize().height));

        var panelCsrfUserTagOutput = new JPanel();
        panelCsrfUserTagOutput.setLayout(new BoxLayout(panelCsrfUserTagOutput, BoxLayout.X_AXIS));
        panelCsrfUserTagOutput.add(new JLabel("Output token to write "));
        panelCsrfUserTagOutput.add(this.textfieldCustomCsrfOutputToken);
        panelCsrfUserTagOutput.setMaximumSize(new Dimension(450, this.textfieldCustomCsrfInputToken.getPreferredSize().height));

        this.textfieldCustomCsrfInputToken.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        });
        this.textfieldCustomCsrfOutputToken.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        });
        
        ActionListener actionListenerNotProcessingCookies = actionEvent -> {
            this.checkboxIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.textfieldCustomCsrfInputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.textfieldCustomCsrfOutputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.checkboxIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        this.checkboxIsNotProcessingCookies.addActionListener(actionListenerNotProcessingCookies);
        
        this.textfieldCustomCsrfInputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.textfieldCustomCsrfOutputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.checkboxIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.checkboxIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());

        Stream.of(
            this.checkboxIsFollowingRedirection,
            this.checkboxIsHttp2Disabled,
            this.checkboxIsUnicodeDecodeDisabled,
            this.checkboxIsUrlDecodeDisabled,
            this.checkboxIsNotTestingConnection,
            this.checkboxIsProcessingCsrf,
            this.checkboxIsCsrfUserTag,
            this.checkboxIsNotProcessingCookies,
            this.checkboxIsLimitingThreads,
            this.checkboxIsConnectionTimeout
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        this.checkboxIsFollowingRedirection.setName("checkboxIsFollowingRedirection");
        this.checkboxIsHttp2Disabled.setName("checkboxIsHttp2Disabled");
        this.checkboxIsUnicodeDecodeDisabled.setName("checkboxIsUnicodeDecodeDisabled");
        this.checkboxIsUrlDecodeDisabled.setName("checkboxIsUrlDecodeDisabled");
        this.checkboxIsNotTestingConnection.setName("checkboxIsNotTestingConnection");
        this.checkboxIsProcessingCsrf.setName("checkboxIsProcessingCsrf");
        this.checkboxIsCsrfUserTag.setName("checkboxIsCsrfUserTag");
        this.checkboxIsNotProcessingCookies.setName("checkboxIsNotProcessingCookies");
        this.checkboxIsLimitingThreads.setName("checkboxIsLimitingThreads");
        this.checkboxIsConnectionTimeout.setName("checkboxIsConnectionTimeout");

        var labelOrigin = new JLabel("<html><b>Network settings</b></html>");
        var labelSessionManagement = new JLabel("<html><br /><b>Session and Cookie management</b></html>");
        Arrays.asList(labelOrigin, labelSessionManagement)
        .forEach(label -> label.setBorder(PanelGeneral.MARGIN));

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);

        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(labelOrigin)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(this.checkboxIsHttp2Disabled)
                .addComponent(this.checkboxIsUnicodeDecodeDisabled)
                .addComponent(this.checkboxIsUrlDecodeDisabled)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(this.checkboxIsLimitingThreads)
                .addComponent(panelThreadCount)
                .addComponent(this.checkboxIsConnectionTimeout)
                .addComponent(panelConnectionTimeout)
                .addComponent(labelSessionManagement)
                .addComponent(this.checkboxIsNotProcessingCookies)
                .addComponent(this.checkboxIsProcessingCsrf)
                .addComponent(this.checkboxIsCsrfUserTag)
                .addComponent(panelCsrfUserTagInput)
                .addComponent(panelCsrfUserTagOutput)
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
                .addComponent(this.checkboxIsFollowingRedirection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsHttp2Disabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUnicodeDecodeDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUrlDecodeDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotTestingConnection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsLimitingThreads)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelThreadCount)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsConnectionTimeout)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelConnectionTimeout)
            )

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelSessionManagement)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotProcessingCookies)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsProcessingCsrf)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCsrfUserTag)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelCsrfUserTagInput)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelCsrfUserTagOutput)
            )
        );
    }

    
    // Getter and setter
    
    public JCheckBox getCheckboxIsFollowingRedirection() {
        return this.checkboxIsFollowingRedirection;
    }
    
    public JCheckBox getCheckboxIsHttp2Disabled() {
        return this.checkboxIsHttp2Disabled;
    }
    
    public JCheckBox getCheckboxIsUnicodeDecodeDisabled() {
        return this.checkboxIsUnicodeDecodeDisabled;
    }
    
    public JCheckBox getCheckboxIsUrlDecodeDisabled() {
        return this.checkboxIsUrlDecodeDisabled;
    }
    
    public JCheckBox getCheckboxIsNotTestingConnection() {
        return this.checkboxIsNotTestingConnection;
    }
    
    public JCheckBox getCheckboxIsNotProcessingCookies() {
        return this.checkboxIsNotProcessingCookies;
    }
    
    public JCheckBox getCheckboxIsProcessingCsrf() {
        return this.checkboxIsProcessingCsrf;
    }
    
    public JCheckBox getCheckboxIsLimitingThreads() {
        return this.checkboxIsLimitingThreads;
    }
    
    public JSpinner getSpinnerLimitingThreads() {
        return this.spinnerLimitingThreads;
    }
    
    public JCheckBox getCheckboxIsConnectionTimeout() {
        return this.checkboxIsConnectionTimeout;
    }
    
    public JSpinner getSpinnerConnectionTimeout() {
        return this.spinnerConnectionTimeout;
    }
    
    public JCheckBox getCheckboxIsCsrfUserTag() {
        return this.checkboxIsCsrfUserTag;
    }
    
    public JTextField getTextfieldCsrfUserTag() {
        return this.textfieldCustomCsrfInputToken;
    }
    
    public JTextField getTextfieldCsrfUserTagOutput() {
        return this.textfieldCustomCsrfOutputToken;
    }
}

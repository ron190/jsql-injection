package com.jsql.view.swing.panel.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.ui.BasicColoredSpinnerUI;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class PanelConnection extends JPanel {

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxIsNotProcessingCookies = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies());
    private final JCheckBox checkboxIsProcessingCsrf = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());
    private final JCheckBox checkboxIsLimitingThreads = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());
    private final JCheckBox checkboxIsConnectionTimeout = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isConnectionTimeout());
    private final JCheckBox checkboxIsUnicodeDecodeDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled());
    
    private final JSpinner spinnerLimitingThreads = new JSpinner();
    private final JSpinner spinnerConnectionTimeout = new JSpinner();
    
    private final JCheckBox checkboxIsCsrfUserTag = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag());
    private final JTextField textfieldCustomCsrfInputToken = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTag());
    private final JTextField textfieldCustomCsrfOutputToken = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTagOutput());
    
    public PanelConnection(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        var tooltipIsFollowingRedirection = "<html>HTTP 3XX response indicates page's location has changed.<br>Redirect automatically to the new location.</html>";
        this.checkboxIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        this.checkboxIsFollowingRedirection.setFocusable(false);
        var labelIsFollowingRedirection = new JButton("Follow redirection");
        labelIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        labelIsFollowingRedirection.addActionListener(actionEvent -> {
            
            this.checkboxIsFollowingRedirection.setSelected(!this.checkboxIsFollowingRedirection.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsUnicodeDecodeDisabled = "<html>Unicode entities \\uXXXX are decoded to raw characters by default.<br>Check to disable this behavior.</html>";
        this.checkboxIsUnicodeDecodeDisabled.setToolTipText(tooltipIsUnicodeDecodeDisabled);
        this.checkboxIsUnicodeDecodeDisabled.setFocusable(false);
        var labelIsUnicodeDecodeDisabled = new JButton("Disable Unicode decoding in response");
        labelIsUnicodeDecodeDisabled.setToolTipText(tooltipIsUnicodeDecodeDisabled);
        labelIsUnicodeDecodeDisabled.addActionListener(actionEvent -> {
            
            this.checkboxIsUnicodeDecodeDisabled.setSelected(!this.checkboxIsUnicodeDecodeDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipTestConnection =
                "<html>Connectivity to target is checked first to stop when target is dead, like with 404 Not Found.<br>"
                + "Check option to process with injection whatever problem exists.</html>";
        this.checkboxIsNotTestingConnection.setToolTipText(tooltipTestConnection);
        this.checkboxIsNotTestingConnection.setFocusable(false);
        var labelIsNotTestingConnection = new JButton("Disable connection test");
        labelIsNotTestingConnection.setToolTipText(tooltipTestConnection);
        labelIsNotTestingConnection.addActionListener(actionEvent -> {
            
            this.checkboxIsNotTestingConnection.setSelected(!this.checkboxIsNotTestingConnection.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsNotProcessingCookies = "<html>Cookies persist data between connections.<br>Sometimes persisted data like user's session is messing with injection and have to be ignored.</html>";
        this.checkboxIsNotProcessingCookies.setToolTipText(tooltipIsNotProcessingCookies);
        this.checkboxIsNotProcessingCookies.setFocusable(false);
        var labelIsNotProcessingCookies = new JButton("Disable session cookies");
        labelIsNotProcessingCookies.setToolTipText(tooltipIsNotProcessingCookies);
        
        var tooltipIsLimitingThreads =
            "<html>Various tasks are processed in parallel to save time.<br>"
            + "Target that detects too much calls during a period can close the connection,<br>"
            + "in that case it helps lowering threads or keeping a single thread.</html>";
        this.checkboxIsLimitingThreads.setToolTipText(tooltipIsLimitingThreads);
        this.checkboxIsLimitingThreads.setFocusable(false);
        var labelIsLimitingThreads = new JButton("Limit processing to");
        labelIsLimitingThreads.setToolTipText(tooltipIsLimitingThreads);
        labelIsLimitingThreads.addActionListener(actionEvent -> {
            
            this.checkboxIsLimitingThreads.setSelected(!this.checkboxIsLimitingThreads.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsConnectionTimeout = "End connection when target takes this long to answer, it can be lowered down to save time in some cases.";
        this.checkboxIsConnectionTimeout.setToolTipText(tooltipIsConnectionTimeout);
        this.checkboxIsConnectionTimeout.setFocusable(false);
        var labelIsConnectionTimeout = new JButton("Timeout after");
        labelIsConnectionTimeout.setToolTipText(tooltipIsConnectionTimeout);
        labelIsConnectionTimeout.addActionListener(actionEvent -> {
            
            this.checkboxIsConnectionTimeout.setSelected(!this.checkboxIsConnectionTimeout.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipProcessCsrf = "Search for commons CSRF token in target's response and inject back the value on each requests";
        this.checkboxIsProcessingCsrf.setToolTipText(tooltipProcessCsrf);
        this.checkboxIsProcessingCsrf.setFocusable(false);
        var labelIsProcessingCsrf = new JButton("Process CSRF token (search for XSRF-TOKEN/.../_csrf ; then set X-XSRF-TOKEN/.../_csrf)");
        labelIsProcessingCsrf.setToolTipText(tooltipProcessCsrf);
        labelIsProcessingCsrf.addActionListener(actionEvent -> {
            
            this.checkboxIsProcessingCsrf.setSelected(!this.checkboxIsProcessingCsrf.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var panelConnectionTimeout = new JPanel(new BorderLayout());
        panelConnectionTimeout.add(labelIsConnectionTimeout, BorderLayout.WEST);
        panelConnectionTimeout.add(this.spinnerConnectionTimeout, BorderLayout.CENTER);
        panelConnectionTimeout.add(new JLabel(" s ; default 15s"), BorderLayout.EAST);
        panelConnectionTimeout.setMaximumSize(new Dimension(125, this.spinnerConnectionTimeout.getPreferredSize().height));
        this.spinnerConnectionTimeout.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));
        
        int countConnectionTimeout = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countConnectionTimeout();
        var spinnerConnectionModel = new SpinnerNumberModel(
            countConnectionTimeout <= 0
            ? 15
            : countConnectionTimeout,
            1,
            30,
            1
        );
        this.spinnerConnectionTimeout.setModel(spinnerConnectionModel);
        this.spinnerConnectionTimeout.setUI(new BasicColoredSpinnerUI());
        this.spinnerConnectionTimeout.addMouseWheelListener(new SpinnerMouseWheelListener());
        
        var panelThreadCount = new JPanel(new BorderLayout());
        panelThreadCount.add(labelIsLimitingThreads, BorderLayout.WEST);
        panelThreadCount.add(this.spinnerLimitingThreads, BorderLayout.CENTER);
        panelThreadCount.add(new JLabel(" thread(s) ; default 10 threads"), BorderLayout.EAST);
        panelThreadCount.setMaximumSize(new Dimension(125, this.spinnerLimitingThreads.getPreferredSize().height));
        this.spinnerLimitingThreads.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));
        
        int countLimitingThreads = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countLimitingThreads();
        var spinnerNumberModel = new SpinnerNumberModel(
            countLimitingThreads <= 0
            ? 10
            : countLimitingThreads,
            1,
            100,
            1
        );
        this.spinnerLimitingThreads.setModel(spinnerNumberModel);
        this.spinnerLimitingThreads.setUI(new BasicColoredSpinnerUI());
        this.spinnerLimitingThreads.addMouseWheelListener(new SpinnerMouseWheelListener());
        
        var tooltipIsCsrfUserTag = "<html>Process custom CSRF.<br>Read value from input token and write value to output token.</html>";
        this.checkboxIsCsrfUserTag.setToolTipText(tooltipIsCsrfUserTag);
        this.checkboxIsCsrfUserTag.setFocusable(false);
        var labelIsCsrfUserTag = new JButton("Custom CSRF ; Input token");
        var labelIsCsrfUserTagOutput = new JButton("; Output token");
        labelIsCsrfUserTag.setToolTipText(tooltipIsCsrfUserTag);
        labelIsCsrfUserTagOutput.setToolTipText(tooltipIsCsrfUserTag);
        labelIsCsrfUserTag.addActionListener(actionEvent -> {
            
            this.checkboxIsCsrfUserTag.setSelected(!this.checkboxIsCsrfUserTag.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsCsrfUserTagOutput.addActionListener(actionEvent -> {
            
            this.checkboxIsCsrfUserTag.setSelected(!this.checkboxIsCsrfUserTag.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var panelCsrfUserTag = new JPanel();
        panelCsrfUserTag.setLayout(new BoxLayout(panelCsrfUserTag, BoxLayout.X_AXIS));
        panelCsrfUserTag.add(labelIsCsrfUserTag);
        panelCsrfUserTag.add(this.textfieldCustomCsrfInputToken);
        panelCsrfUserTag.add(labelIsCsrfUserTagOutput);
        panelCsrfUserTag.add(this.textfieldCustomCsrfOutputToken);
        panelCsrfUserTag.setMaximumSize(new Dimension(450, this.textfieldCustomCsrfInputToken.getPreferredSize().height));
        this.textfieldCustomCsrfInputToken.setHorizontalAlignment(SwingConstants.RIGHT);
        this.textfieldCustomCsrfOutputToken.setHorizontalAlignment(SwingConstants.RIGHT);
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
            
            this.checkboxIsNotProcessingCookies.setSelected(!this.checkboxIsNotProcessingCookies.isSelected());
            
            this.checkboxIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            labelIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            
            this.textfieldCustomCsrfInputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.textfieldCustomCsrfOutputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.checkboxIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            labelIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            labelIsCsrfUserTagOutput.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        labelIsNotProcessingCookies.addActionListener(actionListenerNotProcessingCookies);
        
        labelIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        
        this.textfieldCustomCsrfInputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.textfieldCustomCsrfOutputToken.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.checkboxIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.checkboxIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        labelIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        labelIsCsrfUserTagOutput.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        
        var emptyLabelSessionManagement = new JLabel();
        var labelSessionManagement = new JLabel("<html><br /><b>Session and Cookie management</b></html>");
        
        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        Stream
        .of(
            this.checkboxIsFollowingRedirection,
            this.checkboxIsUnicodeDecodeDisabled,
            this.checkboxIsNotTestingConnection,
            this.checkboxIsProcessingCsrf,
            this.checkboxIsCsrfUserTag,
            this.checkboxIsNotProcessingCookies,
            this.checkboxIsLimitingThreads,
            this.checkboxIsConnectionTimeout
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        this.checkboxIsFollowingRedirection.setName("checkboxIsFollowingRedirection");
        this.checkboxIsUnicodeDecodeDisabled.setName("checkboxIsUnicodeDecodeDisabled");
        this.checkboxIsNotTestingConnection.setName("checkboxIsNotTestingConnection");
        this.checkboxIsProcessingCsrf.setName("checkboxIsProcessingCsrf");
        this.checkboxIsCsrfUserTag.setName("checkboxIsCsrfUserTag");
        this.checkboxIsNotProcessingCookies.setName("checkboxIsNotProcessingCookies");
        this.checkboxIsLimitingThreads.setName("checkboxIsLimitingThreads");
        this.checkboxIsConnectionTimeout.setName("checkboxIsConnectionTimeout");

        labelIsFollowingRedirection.setName("labelIsFollowingRedirection");
        labelIsUnicodeDecodeDisabled.setName("labelIsUnicodeDecodeDisabled");
        labelIsNotTestingConnection.setName("labelIsNotTestingConnection");
        labelIsProcessingCsrf.setName("labelIsProcessingCsrf");
        labelIsCsrfUserTag.setName("labelIsCsrfUserTag");
        labelIsCsrfUserTagOutput.setName("labelIsCsrfUserTagOutput");
        labelIsNotProcessingCookies.setName("labelIsNotProcessingCookies");
        labelIsLimitingThreads.setName("labelIsLimitingThreads");
        labelIsConnectionTimeout.setName("labelIsConnectionTimeout");
        
        Stream
        .of(
            labelIsFollowingRedirection,
            labelIsUnicodeDecodeDisabled,
            labelIsNotTestingConnection,
            labelIsProcessingCsrf,
            labelIsCsrfUserTag,
            labelIsCsrfUserTagOutput,
            labelIsNotProcessingCookies,
            labelIsLimitingThreads,
            labelIsConnectionTimeout
        )
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(this.checkboxIsUnicodeDecodeDisabled)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(this.checkboxIsLimitingThreads)
                .addComponent(this.checkboxIsConnectionTimeout)
                .addComponent(emptyLabelSessionManagement)
                .addComponent(this.checkboxIsNotProcessingCookies)
                .addComponent(this.checkboxIsProcessingCsrf)
                .addComponent(this.checkboxIsCsrfUserTag)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelIsFollowingRedirection)
                .addComponent(labelIsUnicodeDecodeDisabled)
                .addComponent(labelIsNotTestingConnection)
                .addComponent(panelThreadCount)
                .addComponent(panelConnectionTimeout)
                .addComponent(labelSessionManagement)
                .addComponent(labelIsNotProcessingCookies)
                .addComponent(labelIsProcessingCsrf)
                .addComponent(panelCsrfUserTag)
            )
        );

        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(labelIsFollowingRedirection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUnicodeDecodeDisabled)
                .addComponent(labelIsUnicodeDecodeDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(labelIsNotTestingConnection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsLimitingThreads)
                .addComponent(panelThreadCount)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsConnectionTimeout)
                .addComponent(panelConnectionTimeout)
            )

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelSessionManagement)
                .addComponent(labelSessionManagement)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotProcessingCookies)
                .addComponent(labelIsNotProcessingCookies)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsProcessingCsrf)
                .addComponent(labelIsProcessingCsrf)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCsrfUserTag)
                .addComponent(panelCsrfUserTag)
            )
        );
    }

    
    // Getter and setter
    
    public JCheckBox getCheckboxIsFollowingRedirection() {
        return this.checkboxIsFollowingRedirection;
    }
    
    public JCheckBox getCheckboxIsUnicodeDecodeDisabled() {
        return this.checkboxIsUnicodeDecodeDisabled;
    }
    
    public JCheckBox getCheckboxIsNotTestingConnection() {
        return this.checkboxIsNotTestingConnection;
    }
    
    public JCheckBox getCheckboxIsNotProcessingCookies() {
        return this.checkboxIsNotProcessingCookies;
    }
    
    public JCheckBox getCheckboxProcessCsrf() {
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

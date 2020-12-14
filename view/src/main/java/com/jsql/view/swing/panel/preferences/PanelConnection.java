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
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class PanelConnection extends JPanel {

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxIsNotProcessingCookies = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies());
    private final JCheckBox checkboxIsProcessingCsrf = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());
    private final JCheckBox checkboxIsLimitingThreads = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());
    private final JCheckBox checkboxIsUnicodeDecodeDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled());
    
    private final JSpinner spinnerLimitingThreads = new JSpinner();
    private final JCheckBox checkboxIsCsrfUserTag = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag());
    private final JTextField textfieldCsrfUserTag = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTag());
    private final JTextField textfieldCsrfUserTagOutput = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTagOutput());
    
    public PanelConnection(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String tooltipIsFollowingRedirection = "Follow redirection";
        this.checkboxIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        this.checkboxIsFollowingRedirection.setFocusable(false);
        JButton labelIsFollowingRedirection = new JButton("Follow redirection");
        labelIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        labelIsFollowingRedirection.addActionListener(actionEvent -> {
            
            this.checkboxIsFollowingRedirection.setSelected(!this.checkboxIsFollowingRedirection.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsUnicodeDecodeDisabled = "Disable Unicode decoding in response";
        this.checkboxIsUnicodeDecodeDisabled.setToolTipText(tooltipIsUnicodeDecodeDisabled);
        this.checkboxIsUnicodeDecodeDisabled.setFocusable(false);
        JButton labelIsUnicodeDecodeDisabled = new JButton("Disable Unicode decoding in response");
        labelIsUnicodeDecodeDisabled.setToolTipText(tooltipIsUnicodeDecodeDisabled);
        labelIsUnicodeDecodeDisabled.addActionListener(actionEvent -> {
            
            this.checkboxIsUnicodeDecodeDisabled.setSelected(!this.checkboxIsUnicodeDecodeDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipTestConnection = "Disable initial connection test";
        this.checkboxIsNotTestingConnection.setToolTipText(tooltipTestConnection);
        this.checkboxIsNotTestingConnection.setFocusable(false);
        JButton labelTestConnection = new JButton("Disable initial connection test");
        labelTestConnection.setToolTipText(tooltipTestConnection);
        labelTestConnection.addActionListener(actionEvent -> {
            
            this.checkboxIsNotTestingConnection.setSelected(!this.checkboxIsNotTestingConnection.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsNotProcessingCookies = "Disable session cookies";
        this.checkboxIsNotProcessingCookies.setToolTipText(tooltipIsNotProcessingCookies);
        this.checkboxIsNotProcessingCookies.setFocusable(false);
        JButton labelIsNotProcessingCookies = new JButton("Disable session cookies");
        labelIsNotProcessingCookies.setToolTipText(tooltipIsNotProcessingCookies);
        
        String tooltipIsLimitingThreads = "Limit threads";
        this.checkboxIsLimitingThreads.setToolTipText(tooltipIsLimitingThreads);
        this.checkboxIsLimitingThreads.setFocusable(false);
        JButton labelIsLimitingThreads = new JButton("Limit threads to");
        labelIsLimitingThreads.setToolTipText(tooltipIsLimitingThreads);
        labelIsLimitingThreads.addActionListener(actionEvent -> {
            
            this.checkboxIsLimitingThreads.setSelected(!this.checkboxIsLimitingThreads.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipProcessCsrf = "Process CSRF token";
        this.checkboxIsProcessingCsrf.setToolTipText(tooltipProcessCsrf);
        this.checkboxIsProcessingCsrf.setFocusable(false);
        JButton labelProcessingCsrf = new JButton("Process CSRF token (search for XSRF-TOKEN/.../_csrf ; then set X-XSRF-TOKEN/.../_csrf)");
        labelProcessingCsrf.setToolTipText(tooltipProcessCsrf);
        labelProcessingCsrf.addActionListener(actionEvent -> {
            
            this.checkboxIsProcessingCsrf.setSelected(!this.checkboxIsProcessingCsrf.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        JPanel panelThreadCount = new JPanel(new BorderLayout());
        panelThreadCount.add(labelIsLimitingThreads, BorderLayout.WEST);
        panelThreadCount.add(this.spinnerLimitingThreads, BorderLayout.CENTER);
        panelThreadCount.add(new JLabel(" (default 10)"), BorderLayout.EAST);
        panelThreadCount.setMaximumSize(new Dimension(125, this.spinnerLimitingThreads.getPreferredSize().height));
        this.spinnerLimitingThreads.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));
        
        int countLimitingThreads = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countLimitingThreads();
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(
            countLimitingThreads <= 0
            ? 10
            : countLimitingThreads,
            1,
            100,
            1
        );
        this.spinnerLimitingThreads.setModel(spinnerNumberModel);
        
        String tooltipIsCsrfUserTag = "CSRF tag name";
        this.checkboxIsCsrfUserTag.setToolTipText(tooltipIsCsrfUserTag);
        this.checkboxIsCsrfUserTag.setFocusable(false);
        JButton labelCsrfUserTag = new JButton("Custom CSRF processing ; Input tag");
        JButton labelCsrfUserTagOutput = new JButton(", Output tag");
        labelCsrfUserTag.setToolTipText(tooltipIsCsrfUserTag);
        labelCsrfUserTagOutput.setToolTipText(tooltipIsCsrfUserTag);
        labelCsrfUserTag.addActionListener(actionEvent -> {
            
            this.checkboxIsCsrfUserTag.setSelected(!this.checkboxIsCsrfUserTag.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelCsrfUserTagOutput.addActionListener(actionEvent -> {
            
            this.checkboxIsCsrfUserTag.setSelected(!this.checkboxIsCsrfUserTag.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        JPanel panelCsrfUserTag = new JPanel();
        panelCsrfUserTag.setLayout(new BoxLayout(panelCsrfUserTag, BoxLayout.X_AXIS));
        panelCsrfUserTag.add(labelCsrfUserTag);
        panelCsrfUserTag.add(this.textfieldCsrfUserTag);
        panelCsrfUserTag.add(labelCsrfUserTagOutput);
        panelCsrfUserTag.add(this.textfieldCsrfUserTagOutput);
        panelCsrfUserTag.setMaximumSize(new Dimension(450, this.textfieldCsrfUserTag.getPreferredSize().height));
        this.textfieldCsrfUserTag.getDocument().addDocumentListener(new DocumentListenerEditing() {
            
            @Override
            public void process() {
                
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        });
        this.textfieldCsrfUserTagOutput.getDocument().addDocumentListener(new DocumentListenerEditing() {
            
            @Override
            public void process() {
                
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        });
        
        ActionListener actionListenerNotProcessingCookies = actionEvent -> {
            
            this.checkboxIsNotProcessingCookies.setSelected(!this.checkboxIsNotProcessingCookies.isSelected());
            
            this.checkboxIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            labelProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            
            this.textfieldCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.textfieldCsrfUserTagOutput.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            this.checkboxIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            labelCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            labelCsrfUserTagOutput.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        labelIsNotProcessingCookies.addActionListener(actionListenerNotProcessingCookies);
        
        labelProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        
        this.textfieldCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.textfieldCsrfUserTagOutput.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.checkboxIsProcessingCsrf.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        this.checkboxIsCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        labelCsrfUserTag.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        labelCsrfUserTagOutput.setEnabled(!this.checkboxIsNotProcessingCookies.isSelected());
        
        JLabel emptyLabelSessionManagement = new JLabel();
        JLabel labelSessionManagement = new JLabel("<html><br /><b>Session and Cookie management</b></html>");
        
        GroupLayout groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        Stream
        .of(
            this.checkboxIsFollowingRedirection,
            this.checkboxIsUnicodeDecodeDisabled,
            this.checkboxIsNotTestingConnection,
            this.checkboxIsProcessingCsrf,
            this.checkboxIsCsrfUserTag,
            this.checkboxIsNotProcessingCookies,
            this.checkboxIsLimitingThreads
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream
        .of(
            labelIsFollowingRedirection,
            labelIsUnicodeDecodeDisabled,
            labelTestConnection,
            labelProcessingCsrf,
            labelCsrfUserTag,
            labelCsrfUserTagOutput,
            labelIsNotProcessingCookies,
            labelIsLimitingThreads
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
                .addComponent(labelTestConnection)
                .addComponent(panelThreadCount)
                .addComponent(labelSessionManagement)
                .addComponent(labelIsNotProcessingCookies)
                .addComponent(labelProcessingCsrf)
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
                .addComponent(labelTestConnection)
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
                .addComponent(labelProcessingCsrf)
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
    
    public JCheckBox getCheckboxIsCsrfUserTag() {
        return this.checkboxIsCsrfUserTag;
    }
    
    public JTextField getTextfieldCsrfUserTag() {
        return this.textfieldCsrfUserTag;
    }
    
    public JTextField getTextfieldCsrfUserTagOutput() {
        return this.textfieldCsrfUserTagOutput;
    }
}

package com.jsql.view.swing.panel.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
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
import com.jsql.view.swing.text.listener.DocumentListenerTyping;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class PanelConnection extends JPanel {

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxIsNotProcessingCookies = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies());
    private final JCheckBox checkboxIsProcessingCsrf = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());
    private final JCheckBox checkboxIsLimitingThreads = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());
    private final JCheckBox checkboxIsUnicodeDecodeDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled());
    
    private final JSpinner spinnerThreadCount = new JSpinner();
    private final JCheckBox checkboxIsCsrfUserTag = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag());
    private final JTextField textfieldCsrfUserTag = new JTextField(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().csrfUserTag());
    
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
        labelIsNotProcessingCookies.addActionListener(actionEvent -> {
            
            this.checkboxIsNotProcessingCookies.setSelected(!this.checkboxIsNotProcessingCookies.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsLimitingThreads = "Limit threads";
        this.checkboxIsLimitingThreads.setToolTipText(tooltipIsLimitingThreads);
        this.checkboxIsLimitingThreads.setFocusable(false);
        JButton labelIsLimitingThreads = new JButton("Limit threads to");
        labelIsLimitingThreads.setToolTipText(tooltipIsLimitingThreads);
        labelIsLimitingThreads.addActionListener(actionEvent -> {
            
            this.checkboxIsLimitingThreads.setSelected(!this.checkboxIsLimitingThreads.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        JPanel panelThreadCount = new JPanel(new BorderLayout());
        panelThreadCount.add(labelIsLimitingThreads, BorderLayout.WEST);
        panelThreadCount.add(this.spinnerThreadCount, BorderLayout.CENTER);
        panelThreadCount.setMaximumSize(new Dimension(150, this.spinnerThreadCount.getPreferredSize().height));
        this.spinnerThreadCount.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));
        
        int countLimitingThreads = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countLimitingThreads();
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(
            countLimitingThreads <= 0
            ? 10
            : countLimitingThreads,
            1,
            100,
            1
        );
        this.spinnerThreadCount.setModel(spinnerNumberModel);
        
        ActionListener actionListenerProcessCsrf = actionEvent -> {
            
            if (actionEvent.getSource() != this.checkboxIsProcessingCsrf) {
                
                this.checkboxIsProcessingCsrf.setSelected(!this.checkboxIsProcessingCsrf.isSelected());
            }
            
            if (this.checkboxIsProcessingCsrf.isSelected()) {
                
                this.checkboxIsNotProcessingCookies.setSelected(false);
            }
            
            this.checkboxIsNotProcessingCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
            labelIsNotProcessingCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        this.checkboxIsNotProcessingCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
        labelIsNotProcessingCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
        
        String tooltipProcessCsrf = "Process CSRF token";
        this.checkboxIsProcessingCsrf.setToolTipText(tooltipProcessCsrf);
        this.checkboxIsProcessingCsrf.setFocusable(false);
        JButton labelProcessCsrf = new JButton("Process CSRF token");
        labelProcessCsrf.setToolTipText(tooltipProcessCsrf);
        labelProcessCsrf.addActionListener(actionListenerProcessCsrf);
        this.checkboxIsProcessingCsrf.addActionListener(actionListenerProcessCsrf);
        
        String tooltipIsCsrfUserTag = "CSRF tag name";
        this.checkboxIsCsrfUserTag.setToolTipText(tooltipIsCsrfUserTag);
        this.checkboxIsCsrfUserTag.setFocusable(false);
        JButton labelIsCsrfUserTag = new JButton("CSRF tag name");
        labelIsCsrfUserTag.setToolTipText(tooltipIsCsrfUserTag);
        labelIsCsrfUserTag.addActionListener(actionEvent -> {
            
            this.checkboxIsCsrfUserTag.setSelected(!this.checkboxIsCsrfUserTag.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        JPanel panelCsrfUserTag = new JPanel(new BorderLayout());
        panelCsrfUserTag.add(labelIsCsrfUserTag, BorderLayout.WEST);
        panelCsrfUserTag.add(this.textfieldCsrfUserTag, BorderLayout.CENTER);
        panelCsrfUserTag.setMaximumSize(new Dimension(150, this.textfieldCsrfUserTag.getPreferredSize().height));
        this.textfieldCsrfUserTag.getDocument().addDocumentListener(new DocumentListenerTyping() {
            
            @Override
            public void process() {
                
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        });
        

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
            labelProcessCsrf,
            labelIsCsrfUserTag,
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
                .addComponent(labelProcessCsrf)
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
                .addComponent(labelProcessCsrf)
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
        return this.spinnerThreadCount;
    }
    
    public JCheckBox getCheckboxIsCsrfUserTag() {
        return this.checkboxIsCsrfUserTag;
    }
    
    public JTextField getTextfieldCsrfUserTag() {
        return this.textfieldCsrfUserTag;
    }
}

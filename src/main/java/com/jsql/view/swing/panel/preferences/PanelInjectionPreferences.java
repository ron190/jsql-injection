package com.jsql.view.swing.panel.preferences;

import java.awt.event.ActionListener;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jsql.model.MediatorModel;
import com.jsql.view.swing.panel.PanelPreferences;

@SuppressWarnings("serial")
public class PanelInjectionPreferences extends JPanel {

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsNotInjectingMetadata = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxIsParsingForm = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isParsingForm());
    
    private final JCheckBox checkboxIsCheckingAllParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllParam());
    private final JCheckBox checkboxIsCheckingAllURLParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam());
    private final JCheckBox checkboxIsCheckingAllRequestParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam());
    private final JCheckBox checkboxIsCheckingAllHeaderParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam());
    private final JCheckBox checkboxIsCheckingAllJSONParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJSONParam());
    private final JCheckBox checkboxIsCheckingAllCookieParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllCookieParam());
    private final JCheckBox checkboxIsCheckingAllSOAPParam = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSOAPParam());

    private final JCheckBox checkboxProcessCookies = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isProcessingCookies());
    private final JCheckBox checkboxProcessCsrf = new JCheckBox("", MediatorModel.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());
    
    public PanelInjectionPreferences(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String tooltipIsFollowingRedirection = "Force redirection when the page has moved (e.g. HTTP/1.1 302 Found).";
        this.checkboxIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        this.checkboxIsFollowingRedirection.setFocusable(false);
        JButton labelIsFollowingRedirection = new JButton("Follow HTTP redirection");
        labelIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        labelIsFollowingRedirection.addActionListener(actionEvent -> {
            this.checkboxIsFollowingRedirection.setSelected(!this.checkboxIsFollowingRedirection.isSelected());
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
        
        String tooltipProcessCookies = "Save session cookies";
        this.checkboxProcessCookies.setToolTipText(tooltipProcessCookies);
        this.checkboxProcessCookies.setFocusable(false);
        JButton labelProcessCookies = new JButton("Save session cookies");
        labelProcessCookies.setToolTipText(tooltipProcessCookies);
        labelProcessCookies.addActionListener(actionEvent -> {
            this.checkboxProcessCookies.setSelected(!this.checkboxProcessCookies.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        ActionListener actionListenerProcessCsrf = actionEvent -> {
            if (actionEvent.getSource() != this.checkboxProcessCsrf) {
                this.checkboxProcessCsrf.setSelected(!this.checkboxProcessCsrf.isSelected());
            }
            
            if (this.checkboxProcessCsrf.isSelected()) {
                this.checkboxProcessCookies.setSelected(this.checkboxProcessCsrf.isSelected());
            }
            this.checkboxProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
            labelProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        this.checkboxProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
        labelProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
        
        String tooltipProcessCsrf = "Process CSRF token";
        this.checkboxProcessCsrf.setToolTipText(tooltipProcessCsrf);
        this.checkboxProcessCsrf.setFocusable(false);
        JButton labelProcessCsrf = new JButton("Process CSRF token");
        labelProcessCsrf.setToolTipText(tooltipProcessCsrf);
        labelProcessCsrf.addActionListener(actionListenerProcessCsrf);
        this.checkboxProcessCsrf.addActionListener(actionListenerProcessCsrf);
        
        String tooltipParseForm = "Add <input> parameters found in HTML body to URL and Request";
        this.checkboxIsParsingForm.setToolTipText(tooltipParseForm);
        this.checkboxIsParsingForm.setFocusable(false);
        JButton labelParseForm = new JButton("Add <input> parameters found in HTML body to URL and Request");
        labelParseForm.setToolTipText(tooltipParseForm);
        labelParseForm.addActionListener(actionEvent -> {
            this.checkboxIsParsingForm.setSelected(!this.checkboxIsParsingForm.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsNotInjectingMetadata = "Disable database's metadata injection (e.g version, username).";
        this.checkboxIsNotInjectingMetadata.setToolTipText(tooltipIsNotInjectingMetadata);
        this.checkboxIsNotInjectingMetadata.setFocusable(false);
        JButton labelIsNotInjectingMetadata = new JButton("Disable database's metadata injection to speed-up boolean process");
        labelIsNotInjectingMetadata.setToolTipText(tooltipIsNotInjectingMetadata);
        labelIsNotInjectingMetadata.addActionListener(actionEvent -> {
            this.checkboxIsNotInjectingMetadata.setSelected(!this.checkboxIsNotInjectingMetadata.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        JButton labelIsCheckingAllParam = new JButton("Inject each parameter and ignore user's method");
        JButton labelIsCheckingAllURLParam = new JButton("Inject each URL parameter if method is GET");
        JButton labelIsCheckingAllRequestParam = new JButton("Inject each Request parameter if method is Request");
        JButton labelIsCheckingAllHeaderParam = new JButton("Inject each Header parameter if method is Header");
        JButton labelIsCheckingAllCookieParam = new JButton("Inject each Cookie parameter");
        JButton labelIsCheckingAllJSONParam = new JButton("Inject JSON parameters");
        JButton labelIsCheckingAllSOAPParam = new JButton("Inject SOAP parameters in Request body");
        
        JLabel emptyLabelGeneralInjection = new JLabel();
        JLabel labelGeneralInjection = new JLabel("<html><b>Connection definition</b></html>");
        JLabel emptyLabelParamsInjection = new JLabel();
        JLabel labelParamsInjection = new JLabel("<html><br /><b>Parameters injection</b></html>");
        JLabel emptyLabelSessionManagement = new JLabel();
        JLabel labelSessionManagement = new JLabel("<html><br /><b>Session and Cookie management</b></html>");
        JLabel emptyLabelValueInjection = new JLabel();
        JLabel labelValueInjection = new JLabel("<html><br /><b>Special parameters</b></html>");
        
        ActionListener actionListenerCheckingAllParam = actionEvent -> {
            if (actionEvent.getSource() != this.checkboxIsCheckingAllParam) {
                this.checkboxIsCheckingAllParam.setSelected(!this.checkboxIsCheckingAllParam.isSelected());
            }
            
            this.checkboxIsCheckingAllURLParam.setSelected(this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllRequestParam.setSelected(this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllHeaderParam.setSelected(this.checkboxIsCheckingAllParam.isSelected());
            
            this.checkboxIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            
            labelIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            labelIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            labelIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        this.checkboxIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        this.checkboxIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        this.checkboxIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        
        labelIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        labelIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        labelIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        
        labelIsCheckingAllParam.addActionListener(actionListenerCheckingAllParam);
        labelIsCheckingAllURLParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllURLParam.setSelected(!this.checkboxIsCheckingAllURLParam.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsCheckingAllRequestParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllRequestParam.setSelected(!this.checkboxIsCheckingAllRequestParam.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsCheckingAllHeaderParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllHeaderParam.setSelected(!this.checkboxIsCheckingAllHeaderParam.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsCheckingAllJSONParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllJSONParam.setSelected(!this.checkboxIsCheckingAllJSONParam.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsCheckingAllCookieParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllCookieParam.setSelected(!this.checkboxIsCheckingAllCookieParam.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsCheckingAllSOAPParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllSOAPParam.setSelected(!this.checkboxIsCheckingAllSOAPParam.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        this.checkboxIsCheckingAllParam.addActionListener(actionListenerCheckingAllParam);
        
        Stream.of(
            this.checkboxIsFollowingRedirection,
            this.checkboxIsNotInjectingMetadata,
            this.checkboxIsParsingForm,
            this.checkboxIsNotTestingConnection,
            this.checkboxProcessCookies,
            this.checkboxProcessCsrf,
            
            this.checkboxIsCheckingAllURLParam,
            this.checkboxIsCheckingAllRequestParam,
            this.checkboxIsCheckingAllHeaderParam,
            this.checkboxIsCheckingAllJSONParam,
            this.checkboxIsCheckingAllCookieParam,
            this.checkboxIsCheckingAllSOAPParam
            
        ).forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream.of(
            labelIsFollowingRedirection,
            labelTestConnection,
            labelParseForm,
            labelIsNotInjectingMetadata,
            labelIsCheckingAllParam,
            labelIsCheckingAllURLParam,
            labelIsCheckingAllRequestParam,
            labelIsCheckingAllHeaderParam,
            labelIsCheckingAllJSONParam,
            labelIsCheckingAllCookieParam,
            labelIsCheckingAllSOAPParam,
            labelProcessCookies,
            labelProcessCsrf
        )
        .forEach(label -> {
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        GroupLayout groupLayoutInjection = new GroupLayout(this);
        this.setLayout(groupLayoutInjection);

        groupLayoutInjection.setHorizontalGroup(
            groupLayoutInjection
            .createSequentialGroup()
            .addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(emptyLabelGeneralInjection)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(this.checkboxIsParsingForm)
                .addComponent(this.checkboxIsNotInjectingMetadata)
                
                .addComponent(emptyLabelParamsInjection)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)
                
                .addComponent(emptyLabelValueInjection)
                .addComponent(this.checkboxIsCheckingAllCookieParam)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
                
                .addComponent(emptyLabelSessionManagement)
                .addComponent(this.checkboxProcessCsrf)
                .addComponent(this.checkboxProcessCookies)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup()
                .addComponent(labelGeneralInjection)
                .addComponent(labelIsFollowingRedirection)
                .addComponent(labelTestConnection)
                .addComponent(labelParseForm)
                .addComponent(labelIsNotInjectingMetadata)
                
                .addComponent(labelParamsInjection)
                .addComponent(labelIsCheckingAllParam)
                .addComponent(labelIsCheckingAllURLParam)
                .addComponent(labelIsCheckingAllRequestParam)
                .addComponent(labelIsCheckingAllHeaderParam)
                
                .addComponent(labelValueInjection)
                .addComponent(labelIsCheckingAllJSONParam)
                .addComponent(labelIsCheckingAllSOAPParam)
                .addComponent(labelIsCheckingAllCookieParam)
                
                .addComponent(labelSessionManagement)
                .addComponent(labelProcessCsrf)
                .addComponent(labelProcessCookies)
            )
        );
        
        groupLayoutInjection.setVerticalGroup(
            groupLayoutInjection
            .createSequentialGroup()
            .addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelGeneralInjection)
                .addComponent(labelGeneralInjection)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(labelIsFollowingRedirection)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(labelTestConnection)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsParsingForm)
                .addComponent(labelParseForm)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotInjectingMetadata)
                .addComponent(labelIsNotInjectingMetadata)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelParamsInjection)
                .addComponent(labelParamsInjection)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(labelIsCheckingAllParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(labelIsCheckingAllURLParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(labelIsCheckingAllRequestParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)
                .addComponent(labelIsCheckingAllHeaderParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelValueInjection)
                .addComponent(labelValueInjection)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(labelIsCheckingAllJSONParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
                .addComponent(labelIsCheckingAllSOAPParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllCookieParam)
                .addComponent(labelIsCheckingAllCookieParam)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelSessionManagement)
                .addComponent(labelSessionManagement)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxProcessCsrf)
                .addComponent(labelProcessCsrf)
            ).addGroup(
                groupLayoutInjection
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxProcessCookies)
                .addComponent(labelProcessCookies)
            )
        );
    }

    
    // Getter and setter
    
    public JCheckBox getCheckboxIsFollowingRedirection() {
        return this.checkboxIsFollowingRedirection;
    }
    
    public JCheckBox getCheckboxIsNotInjectingMetadata() {
        return this.checkboxIsNotInjectingMetadata;
    }
    
    public JCheckBox getCheckboxIsCheckingAllParam() {
        return this.checkboxIsCheckingAllParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllURLParam() {
        return this.checkboxIsCheckingAllURLParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllRequestParam() {
        return this.checkboxIsCheckingAllRequestParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllHeaderParam() {
        return this.checkboxIsCheckingAllHeaderParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllJSONParam() {
        return this.checkboxIsCheckingAllJSONParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllCookieParam() {
        return this.checkboxIsCheckingAllCookieParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllSOAPParam() {
        return this.checkboxIsCheckingAllSOAPParam;
    }
    
    public JCheckBox getCheckboxIsParsingForm() {
        return this.checkboxIsParsingForm;
    }
    
    public JCheckBox getCheckboxIsNotTestingConnection() {
        return this.checkboxIsNotTestingConnection;
    }
    
    public JCheckBox getCheckboxProcessCookies() {
        return this.checkboxProcessCookies;
    }
    
    public JCheckBox getCheckboxProcessCsrf() {
        return this.checkboxProcessCsrf;
    }
}

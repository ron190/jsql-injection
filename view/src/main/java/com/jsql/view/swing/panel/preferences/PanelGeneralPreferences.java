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

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class PanelGeneralPreferences extends JPanel {

    private final JCheckBox checkboxIsCheckingUpdate = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingUpdate());
    private final JCheckBox checkboxIsReportingBugs = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isReportingBugs());
    private final JCheckBox checkboxIs4K = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().is4K());

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxProcessCookies = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCookies());
    private final JCheckBox checkboxProcessCsrf = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());

    public PanelGeneralPreferences(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        this.getCheckboxIsCheckingUpdate().setFocusable(false);
        JButton labelIsCheckingUpdate = new JButton("Check update at startup");
        labelIsCheckingUpdate.addActionListener(actionEvent -> {
            
            this.getCheckboxIsCheckingUpdate().setSelected(!this.getCheckboxIsCheckingUpdate().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsReportingBugs = "Send unhandled exception to developer in order to fix issues.";
        this.getCheckboxIsReportingBugs().setToolTipText(tooltipIsReportingBugs);
        this.getCheckboxIsReportingBugs().setFocusable(false);
        JButton labelIsReportingBugs = new JButton("Report unhandled exceptions");
        labelIsReportingBugs.setToolTipText(tooltipIsReportingBugs);
        labelIsReportingBugs.addActionListener(actionEvent -> {
            
            this.getCheckboxIsReportingBugs().setSelected(!this.getCheckboxIsReportingBugs().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIs4K = "Upscale GUI by factor 2.5 for compatibility with high-definition screens";
        this.getCheckboxIs4K().setToolTipText(tooltipIs4K);
        this.getCheckboxIs4K().setFocusable(false);
        JButton labelIs4K = new JButton("Activate high-definition mode for 4K screens (need a restart)");
        labelIs4K.setToolTipText(tooltipIs4K);
        labelIs4K.addActionListener(actionEvent -> {
            
            this.getCheckboxIs4K().setSelected(!this.getCheckboxIs4K().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        
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

        JLabel emptyLabelSessionManagement = new JLabel();
        JLabel labelSessionManagement = new JLabel("<html><br /><b>Session and Cookie management</b></html>");
        
        GroupLayout groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        Stream.of(
            this.checkboxIsCheckingUpdate,
            this.checkboxIsReportingBugs,
            this.checkboxIs4K,
            this.checkboxIsFollowingRedirection,
            this.checkboxIsNotTestingConnection,
            this.checkboxProcessCsrf,
            this.checkboxProcessCookies
        ).forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream.of(
            labelIsCheckingUpdate,
            labelIsReportingBugs,
            labelIs4K,
            labelIsFollowingRedirection,
            labelTestConnection,
            labelProcessCsrf,
            labelProcessCookies
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
                .addComponent(this.checkboxIsCheckingUpdate)
                .addComponent(this.checkboxIsReportingBugs)
                .addComponent(this.checkboxIs4K)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(emptyLabelSessionManagement)
                .addComponent(this.checkboxProcessCsrf)
                .addComponent(this.checkboxProcessCookies)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelIsCheckingUpdate)
                .addComponent(labelIsReportingBugs)
                .addComponent(labelIs4K)
                .addComponent(labelIsFollowingRedirection)
                .addComponent(labelTestConnection)
                .addComponent(labelSessionManagement)
                .addComponent(labelProcessCsrf)
                .addComponent(labelProcessCookies)
            )
        );

        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingUpdate)
                .addComponent(labelIsCheckingUpdate)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsReportingBugs)
                .addComponent(labelIsReportingBugs)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIs4K)
                .addComponent(labelIs4K)
            )

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(labelIsFollowingRedirection)
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
                .addComponent(emptyLabelSessionManagement)
                .addComponent(labelSessionManagement)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxProcessCsrf)
                .addComponent(labelProcessCsrf)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxProcessCookies)
                .addComponent(labelProcessCookies)
            )
        );
    }

    
    // Getter and setter
    
    public JCheckBox getCheckboxIsCheckingUpdate() {
        return this.checkboxIsCheckingUpdate;
    }

    public JCheckBox getCheckboxIsReportingBugs() {
        return this.checkboxIsReportingBugs;
    }

    public JCheckBox getCheckboxIs4K() {
        return this.checkboxIs4K;
    }
    
    public JCheckBox getCheckboxIsFollowingRedirection() {
        return this.checkboxIsFollowingRedirection;
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

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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class PanelGeneral extends JPanel {

    private final JCheckBox checkboxIsCheckingUpdate = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingUpdate());
    private final JCheckBox checkboxIsReportingBugs = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isReportingBugs());
    private final JCheckBox checkboxIs4K = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().is4K());

    private final JCheckBox checkboxIsFollowingRedirection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection());
    private final JCheckBox checkboxIsNotTestingConnection = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection());
    private final JCheckBox checkboxIsProcessingCookies = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCookies());
    private final JCheckBox checkboxIsProcessingCsrf = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf());
    private final JCheckBox checkboxIsLimitingThreads = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());
    private final JSpinner spinnerLimitingThreads = new JSpinner();
    
    public PanelGeneral(PanelPreferences panelPreferences) {
        
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
        this.checkboxIsProcessingCookies.setToolTipText(tooltipProcessCookies);
        this.checkboxIsProcessingCookies.setFocusable(false);
        JButton labelProcessCookies = new JButton("Save session cookies");
        labelProcessCookies.setToolTipText(tooltipProcessCookies);
        labelProcessCookies.addActionListener(actionEvent -> {
            
            this.checkboxIsProcessingCookies.setSelected(!this.checkboxIsProcessingCookies.isSelected());
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
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(labelIsLimitingThreads, BorderLayout.WEST);
        p.add(this.spinnerLimitingThreads, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(150, this.spinnerLimitingThreads.getPreferredSize().height));
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
        
        ActionListener actionListenerProcessCsrf = actionEvent -> {
            
            if (actionEvent.getSource() != this.checkboxIsProcessingCsrf) {
                
                this.checkboxIsProcessingCsrf.setSelected(!this.checkboxIsProcessingCsrf.isSelected());
            }
            
            if (this.checkboxIsProcessingCsrf.isSelected()) {
                
                this.checkboxIsProcessingCookies.setSelected(this.checkboxIsProcessingCsrf.isSelected());
            }
            
            this.checkboxIsProcessingCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
            labelProcessCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        this.checkboxIsProcessingCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
        labelProcessCookies.setEnabled(!this.checkboxIsProcessingCsrf.isSelected());
        
        String tooltipProcessCsrf = "Process CSRF token";
        this.checkboxIsProcessingCsrf.setToolTipText(tooltipProcessCsrf);
        this.checkboxIsProcessingCsrf.setFocusable(false);
        JButton labelProcessCsrf = new JButton("Process CSRF token");
        labelProcessCsrf.setToolTipText(tooltipProcessCsrf);
        labelProcessCsrf.addActionListener(actionListenerProcessCsrf);
        this.checkboxIsProcessingCsrf.addActionListener(actionListenerProcessCsrf);

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
            this.checkboxIsProcessingCsrf,
            this.checkboxIsProcessingCookies,
            this.checkboxIsLimitingThreads
        ).forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream.of(
            labelIsCheckingUpdate,
            labelIsReportingBugs,
            labelIs4K,
            labelIsFollowingRedirection,
            labelTestConnection,
            labelProcessCsrf,
            labelProcessCookies,
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
                .addComponent(this.checkboxIsCheckingUpdate)
                .addComponent(this.checkboxIsReportingBugs)
                .addComponent(this.checkboxIs4K)
                .addComponent(this.checkboxIsFollowingRedirection)
                .addComponent(this.checkboxIsNotTestingConnection)
                .addComponent(this.checkboxIsLimitingThreads)
                .addComponent(emptyLabelSessionManagement)
                .addComponent(this.checkboxIsProcessingCsrf)
                .addComponent(this.checkboxIsProcessingCookies)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelIsCheckingUpdate)
                .addComponent(labelIsReportingBugs)
                .addComponent(labelIs4K)
                .addComponent(labelIsFollowingRedirection)
                .addComponent(labelTestConnection)
                .addComponent(p)
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
                .addComponent(this.checkboxIsLimitingThreads)
                .addComponent(p)
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
                .addComponent(this.checkboxIsProcessingCsrf)
                .addComponent(labelProcessCsrf)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsProcessingCookies)
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
        return this.checkboxIsProcessingCookies;
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
}

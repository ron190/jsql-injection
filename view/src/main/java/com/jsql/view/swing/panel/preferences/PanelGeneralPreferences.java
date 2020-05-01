package com.jsql.view.swing.panel.preferences;

import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
        
        GroupLayout groupLayoutGeneral = new GroupLayout(this);
        this.setLayout(groupLayoutGeneral);
        
        Stream.of(
            this.getCheckboxIsCheckingUpdate(),
            this.getCheckboxIsReportingBugs(),
            this.getCheckboxIs4K()
        ).forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream.of(
            labelIsCheckingUpdate,
            labelIsReportingBugs,
            labelIs4K
        )
        .forEach(label -> {
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        groupLayoutGeneral
        .setHorizontalGroup(
            groupLayoutGeneral
            .createSequentialGroup()
            .addGroup(
                groupLayoutGeneral
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.getCheckboxIsCheckingUpdate())
                .addComponent(this.getCheckboxIsReportingBugs())
                .addComponent(this.getCheckboxIs4K())
            )
            .addGroup(
                groupLayoutGeneral
                .createParallelGroup()
                .addComponent(labelIsCheckingUpdate)
                .addComponent(labelIsReportingBugs)
                .addComponent(labelIs4K)
            )
        );

        groupLayoutGeneral
        .setVerticalGroup(
            groupLayoutGeneral
            .createSequentialGroup()
            .addGroup(
                groupLayoutGeneral
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIsCheckingUpdate())
                .addComponent(labelIsCheckingUpdate)
            )
            .addGroup(
                groupLayoutGeneral
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIsReportingBugs())
                .addComponent(labelIsReportingBugs)
            )
            .addGroup(
                groupLayoutGeneral
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIs4K())
                .addComponent(labelIs4K)
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
}

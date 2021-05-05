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
public class PanelGeneral extends JPanel {

    private final JCheckBox checkboxIsCheckingUpdate = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingUpdate());
    private final JCheckBox checkboxIsReportingBugs = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isReportingBugs());
    private final JCheckBox checkboxIs4K = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().is4K());
    
    public PanelGeneral(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        this.getCheckboxIsCheckingUpdate().setFocusable(false);
        var labelIsCheckingUpdate = new JButton("Check update at startup");
        labelIsCheckingUpdate.addActionListener(actionEvent -> {
            
            this.getCheckboxIsCheckingUpdate().setSelected(!this.getCheckboxIsCheckingUpdate().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsReportingBugs = "Send unhandled exception to developer in order to fix issues.";
        this.getCheckboxIsReportingBugs().setToolTipText(tooltipIsReportingBugs);
        this.getCheckboxIsReportingBugs().setFocusable(false);
        var labelIsReportingBugs = new JButton("Report unhandled exceptions");
        labelIsReportingBugs.setToolTipText(tooltipIsReportingBugs);
        labelIsReportingBugs.addActionListener(actionEvent -> {
            
            this.getCheckboxIsReportingBugs().setSelected(!this.getCheckboxIsReportingBugs().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIs4K = "Upscale GUI by factor 2.5 for compatibility with high-definition screens";
        this.getCheckboxIs4K().setToolTipText(tooltipIs4K);
        this.getCheckboxIs4K().setFocusable(false);
        var labelIs4K = new JButton("Enable high-definition mode for 4K screens (need a restart)");
        labelIs4K.setToolTipText(tooltipIs4K);
        labelIs4K.addActionListener(actionEvent -> {
            
            this.getCheckboxIs4K().setSelected(!this.getCheckboxIs4K().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        Stream
        .of(
            this.checkboxIsCheckingUpdate,
            this.checkboxIsReportingBugs,
            this.checkboxIs4K
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream
        .of(
            labelIsCheckingUpdate,
            labelIsReportingBugs,
            labelIs4K
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
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelIsCheckingUpdate)
                .addComponent(labelIsReportingBugs)
                .addComponent(labelIs4K)
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

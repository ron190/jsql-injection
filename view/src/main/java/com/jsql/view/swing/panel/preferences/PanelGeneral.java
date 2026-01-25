package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.stream.Stream;

public class PanelGeneral extends JPanel {

    public static final Border MARGIN = BorderFactory.createEmptyBorder(0,0,2,0);

    private final JCheckBox checkboxIsCheckingUpdate = new JCheckBox("Check update at startup", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingUpdate());
    private final JCheckBox checkboxIsReportingBugs = new JCheckBox("Report unhandled exceptions", MediatorHelper.model().getMediatorUtils().preferencesUtil().isReportingBugs());
    private final JCheckBox checkboxIs4K = new JCheckBox("Enable high-definition mode for 4K screens (need a restart)", MediatorHelper.model().getMediatorUtils().preferencesUtil().is4K());
    
    public PanelGeneral(PanelPreferences panelPreferences) {
        this.checkboxIsReportingBugs.setToolTipText("Send unhandled exception to developer in order to fix issues.");
        this.checkboxIs4K.setToolTipText("Upscale GUI by factor 2.5 for compatibility with high-definition screens");

        Stream.of(
            this.checkboxIsCheckingUpdate,
            this.checkboxIsReportingBugs,
            this.checkboxIs4K
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));

        var labelOrigin = new JLabel("<html><b>Settings and behaviors</b></html>");
        labelOrigin.setBorder(PanelGeneral.MARGIN);

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);

        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(labelOrigin)
                .addComponent(this.checkboxIsCheckingUpdate)
                .addComponent(this.checkboxIsReportingBugs)
                .addComponent(this.checkboxIs4K)
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
                .addComponent(this.checkboxIsCheckingUpdate)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsReportingBugs)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIs4K)
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

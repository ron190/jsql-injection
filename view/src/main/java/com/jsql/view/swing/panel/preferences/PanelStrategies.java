package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.util.stream.Stream;

public class PanelStrategies extends JPanel {

    private final JCheckBox checkboxIsStrategyTimeDisabled = new JCheckBox("Disable Time", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyTimeDisabled());
    private final JCheckBox checkboxIsStrategyBlindBitDisabled = new JCheckBox("Disable Blind bit", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyBlindBitDisabled());
    private final JCheckBox checkboxIsStrategyBlindBinDisabled = new JCheckBox("Disable Blind bin", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyBlindBinDisabled());
    private final JCheckBox checkboxIsStrategyMultibitDisabled = new JCheckBox("Disable Multibit", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyMultibitDisabled());
    private final JCheckBox checkboxIsStrategyDnsDisabled = new JCheckBox("[Advanced] Disable Dns (requires local setup or registrar)", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyDnsDisabled());
    private final JCheckBox checkboxIsStrategyErrorDisabled = new JCheckBox("Disable Error", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyErrorDisabled());
    private final JCheckBox checkboxIsStrategyStackDisabled = new JCheckBox("Disable Stack", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyStackDisabled());
    private final JCheckBox checkboxIsStrategyUnionDisabled = new JCheckBox("Disable Union", MediatorHelper.model().getMediatorUtils().preferencesUtil().isStrategyUnionDisabled());

    public PanelStrategies(PanelPreferences panelPreferences) {
        this.checkboxIsStrategyTimeDisabled.setToolTipText("Skip Time strategy processing");
        this.checkboxIsStrategyBlindBitDisabled.setToolTipText("Skip Blind bit strategy processing");
        this.checkboxIsStrategyBlindBinDisabled.setToolTipText("Skip Blind bin strategy processing");
        this.checkboxIsStrategyMultibitDisabled.setToolTipText("Skip Multibit strategy processing");
        this.checkboxIsStrategyDnsDisabled.setToolTipText("Skip Dns strategy processing");
        this.checkboxIsStrategyErrorDisabled.setToolTipText("Skip Error strategy processing");
        this.checkboxIsStrategyStackDisabled.setToolTipText("Skip Stack strategy processing");
        this.checkboxIsStrategyUnionDisabled.setToolTipText("Skip Union strategy processing");

        Stream.of(
            this.checkboxIsStrategyTimeDisabled,
            this.checkboxIsStrategyBlindBitDisabled,
            this.checkboxIsStrategyBlindBinDisabled,
            this.checkboxIsStrategyMultibitDisabled,
            this.checkboxIsStrategyDnsDisabled,
            this.checkboxIsStrategyErrorDisabled,
            this.checkboxIsStrategyStackDisabled,
            this.checkboxIsStrategyUnionDisabled
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));

        var labelOrigin = new JLabel("<html><b>Choose injection strategies to skip</b></html>");
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
                .addComponent(this.checkboxIsStrategyTimeDisabled)
                .addComponent(this.checkboxIsStrategyBlindBinDisabled)
                .addComponent(this.checkboxIsStrategyBlindBitDisabled)
                .addComponent(this.checkboxIsStrategyMultibitDisabled)
                .addComponent(this.checkboxIsStrategyDnsDisabled)
                .addComponent(this.checkboxIsStrategyErrorDisabled)
                .addComponent(this.checkboxIsStrategyStackDisabled)
                .addComponent(this.checkboxIsStrategyUnionDisabled)
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
                .addComponent(this.checkboxIsStrategyTimeDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyBlindBinDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyBlindBitDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyMultibitDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyDnsDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyErrorDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyStackDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyUnionDisabled)
            )
        );
    }
    
    
    // Getter and setter

    public JCheckBox getCheckboxIsStrategyTimeDisabled() {
        return this.checkboxIsStrategyTimeDisabled;
    }

    public JCheckBox getCheckboxIsStrategyBlindBinDisabled() {
        return this.checkboxIsStrategyBlindBinDisabled;
    }

    public JCheckBox getCheckboxIsStrategyBlindBitDisabled() {
        return this.checkboxIsStrategyBlindBitDisabled;
    }

    public JCheckBox getCheckboxIsStrategyMultibitDisabled() {
        return this.checkboxIsStrategyMultibitDisabled;
    }

    public JCheckBox getCheckboxIsStrategyStackDisabled() {
        return this.checkboxIsStrategyStackDisabled;
    }

    public JCheckBox getCheckboxIsStrategyDnsDisabled() {
        return this.checkboxIsStrategyDnsDisabled;
    }

    public JCheckBox getCheckboxIsStrategyErrorDisabled() {
        return this.checkboxIsStrategyErrorDisabled;
    }

    public JCheckBox getCheckboxIsStrategyUnionDisabled() {
        return this.checkboxIsStrategyUnionDisabled;
    }
}

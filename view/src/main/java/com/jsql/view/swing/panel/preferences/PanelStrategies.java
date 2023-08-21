package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.stream.Stream;

public class PanelStrategies extends JPanel {

    private final JCheckBox checkboxIsStrategyTimeDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isStrategyTimeDisabled());
    private final JCheckBox checkboxIsStrategyBlindDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isStrategyBlindDisabled());
    private final JCheckBox checkboxIsStrategyStackedDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isStrategyStackedDisabled());
    private final JCheckBox checkboxIsStrategyMultibitDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isStrategyMultibitDisabled());
    private final JCheckBox checkboxIsStrategyErrorDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isStrategyErrorDisabled());
    private final JCheckBox checkboxIsStrategyNormalDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isStrategyNormalDisabled());

    public PanelStrategies(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String tooltipIsStrategyTimeDisabled = "Skip Time strategy processing";
        this.checkboxIsStrategyTimeDisabled.setToolTipText(tooltipIsStrategyTimeDisabled);
        this.checkboxIsStrategyTimeDisabled.setFocusable(false);
        var labelIsStrategyTimeDisabled = new JButton("Disable Time");
        labelIsStrategyTimeDisabled.setToolTipText(tooltipIsStrategyTimeDisabled);
        labelIsStrategyTimeDisabled.addActionListener(actionEvent -> {
            
            this.checkboxIsStrategyTimeDisabled.setSelected(!this.checkboxIsStrategyTimeDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });

        String tooltipIsStrategyBlindDisabled = "Skip Blind strategy processing";
        this.checkboxIsStrategyBlindDisabled.setToolTipText(tooltipIsStrategyBlindDisabled);
        this.checkboxIsStrategyBlindDisabled.setFocusable(false);
        var labelIsStrategyBlindDisabled = new JButton("Disable Blind");
        labelIsStrategyBlindDisabled.setToolTipText(tooltipIsStrategyBlindDisabled);
        labelIsStrategyBlindDisabled.addActionListener(actionEvent -> {

            this.checkboxIsStrategyBlindDisabled.setSelected(!this.checkboxIsStrategyBlindDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });

        String tooltipIsStrategyMultibitDisabled = "Skip Multibit strategy processing";
        this.checkboxIsStrategyMultibitDisabled.setToolTipText(tooltipIsStrategyMultibitDisabled);
        this.checkboxIsStrategyMultibitDisabled.setFocusable(false);
        var labelIsStrategyMultibitDisabled = new JButton("Disable Multibit");
        labelIsStrategyMultibitDisabled.setToolTipText(tooltipIsStrategyMultibitDisabled);
        labelIsStrategyMultibitDisabled.addActionListener(actionEvent -> {

            this.checkboxIsStrategyMultibitDisabled.setSelected(!this.checkboxIsStrategyMultibitDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });

        String tooltipIsStrategyStackedDisabled = "Skip Stacked strategy processing";
        this.checkboxIsStrategyStackedDisabled.setToolTipText(tooltipIsStrategyStackedDisabled);
        this.checkboxIsStrategyStackedDisabled.setFocusable(false);
        var labelIsStrategyStackedDisabled = new JButton("Disable Stacked");
        labelIsStrategyStackedDisabled.setToolTipText(tooltipIsStrategyStackedDisabled);
        labelIsStrategyStackedDisabled.addActionListener(actionEvent -> {

            this.checkboxIsStrategyStackedDisabled.setSelected(!this.checkboxIsStrategyStackedDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });

        String tooltipIsStrategyErrorDisabled = "Skip Error strategy processing";
        this.checkboxIsStrategyErrorDisabled.setToolTipText(tooltipIsStrategyErrorDisabled);
        this.checkboxIsStrategyErrorDisabled.setFocusable(false);
        var labelIsStrategyErrorDisabled = new JButton("Disable Error");
        labelIsStrategyErrorDisabled.setToolTipText(tooltipIsStrategyErrorDisabled);
        labelIsStrategyErrorDisabled.addActionListener(actionEvent -> {

            this.checkboxIsStrategyErrorDisabled.setSelected(!this.checkboxIsStrategyErrorDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });

        String tooltipIsStrategyNormalDisabled = "Skip Normal strategy processing";
        this.checkboxIsStrategyNormalDisabled.setToolTipText(tooltipIsStrategyNormalDisabled);
        this.checkboxIsStrategyNormalDisabled.setFocusable(false);
        var labelIsStrategyNormalDisabled = new JButton("Disable Normal");
        labelIsStrategyNormalDisabled.setToolTipText(tooltipIsStrategyNormalDisabled);
        labelIsStrategyNormalDisabled.addActionListener(actionEvent -> {

            this.checkboxIsStrategyNormalDisabled.setSelected(!this.checkboxIsStrategyNormalDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        Stream.of(
            labelIsStrategyTimeDisabled,
            labelIsStrategyBlindDisabled,
            labelIsStrategyMultibitDisabled,
            labelIsStrategyErrorDisabled,
            labelIsStrategyStackedDisabled,
            labelIsStrategyNormalDisabled
        )
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.checkboxIsStrategyTimeDisabled)
                .addComponent(this.checkboxIsStrategyBlindDisabled)
                .addComponent(this.checkboxIsStrategyStackedDisabled)
                .addComponent(this.checkboxIsStrategyMultibitDisabled)
                .addComponent(this.checkboxIsStrategyErrorDisabled)
                .addComponent(this.checkboxIsStrategyNormalDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelIsStrategyTimeDisabled)
                .addComponent(labelIsStrategyBlindDisabled)
                .addComponent(labelIsStrategyStackedDisabled)
                .addComponent(labelIsStrategyMultibitDisabled)
                .addComponent(labelIsStrategyErrorDisabled)
                .addComponent(labelIsStrategyNormalDisabled)
            )
        );
        
        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyTimeDisabled)
                .addComponent(labelIsStrategyTimeDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyBlindDisabled)
                .addComponent(labelIsStrategyBlindDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyStackedDisabled)
                .addComponent(labelIsStrategyStackedDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyMultibitDisabled)
                .addComponent(labelIsStrategyMultibitDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyErrorDisabled)
                .addComponent(labelIsStrategyErrorDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsStrategyNormalDisabled)
                .addComponent(labelIsStrategyNormalDisabled)
            )
        );
        
        Stream
        .of(
            this.checkboxIsStrategyTimeDisabled,
            this.checkboxIsStrategyBlindDisabled,
            this.checkboxIsStrategyStackedDisabled,
            this.checkboxIsStrategyMultibitDisabled,
            this.checkboxIsStrategyErrorDisabled,
            this.checkboxIsStrategyNormalDisabled
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
    }
    
    
    // Getter and setter

    public JCheckBox getCheckboxIsStrategyTimeDisabled() {
        return checkboxIsStrategyTimeDisabled;
    }

    public JCheckBox getCheckboxIsStrategyBlindDisabled() {
        return checkboxIsStrategyBlindDisabled;
    }

    public JCheckBox getCheckboxIsStrategyStackedDisabled() {
        return checkboxIsStrategyStackedDisabled;
    }

    public JCheckBox getCheckboxIsStrategyMultibitDisabled() {
        return checkboxIsStrategyMultibitDisabled;
    }

    public JCheckBox getCheckboxIsStrategyErrorDisabled() {
        return checkboxIsStrategyErrorDisabled;
    }

    public JCheckBox getCheckboxIsStrategyNormalDisabled() {
        return checkboxIsStrategyNormalDisabled;
    }
}

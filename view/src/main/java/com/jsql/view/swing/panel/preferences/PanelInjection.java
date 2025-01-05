package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.panel.preferences.listener.SpinnerMouseWheelListener;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.stream.Stream;

public class PanelInjection extends JPanel {

    private final JCheckBox checkboxIsNotShowingVulnReport = new JCheckBox("Disable showing vulnerability report", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotShowingVulnReport());
    private final JCheckBox checkboxIsNotSearchingCharInsertion = new JCheckBox("Disable search for character insertion", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotSearchingCharInsertion());
    private final JCheckBox checkboxIsNotInjectingMetadata = new JCheckBox("Disable search of database name, version and user metadata", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata());
    private final JCheckBox checkboxIsParsingForm = new JCheckBox("Get HTML tags <input/> and add parameters to URL and Request", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm());
    
    private final JCheckBox checkboxIsCheckingAllParam = new JCheckBox("Inject every parameters (ignore user's selection)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllParam());
    private final JCheckBox checkboxIsCheckingAllURLParam = new JCheckBox("Inject every URL parameters when URL method is selected", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam());
    private final JCheckBox checkboxIsCheckingAllRequestParam = new JCheckBox("Inject every Request parameters when Request method is selected", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam());
    private final JCheckBox checkboxIsCheckingAllHeaderParam = new JCheckBox("Inject every Header parameters when Header method is selected", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam());
    private final JCheckBox checkboxIsCheckingAllBase64Param = new JCheckBox("Inject Base64 parameters", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllBase64Param());
    private final JCheckBox checkboxIsCheckingAllJSONParam = new JCheckBox("Inject every JSON parameters", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam());
    private final JCheckBox checkboxIsCheckingAllCookieParam = new JCheckBox("Inject every cookie parameters", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllCookieParam());
    private final JCheckBox checkboxIsCheckingAllSOAPParam = new JCheckBox("Inject SOAP parameters in Request body", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam());

    private final JCheckBox checkboxIsLimitingNormalIndex = new JCheckBox("Limit Normal UNION strategy :", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex());
    private final JSpinner spinnerNormalIndexCount = new JSpinner();
    private final JCheckBox checkboxIsLimitingSleepTimeStrategy = new JCheckBox("Delay Time strategy :", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingSleepTimeStrategy());
    private final JSpinner spinnerSleepTimeStrategyCount = new JSpinner();

    private final JCheckBox checkboxIsPerfIndexDisabled = new JCheckBox("Disable calibration (smaller SQL query during Normal index selection only)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled());
    private final JRadioButton radioIsDefaultStrategy = new JRadioButton("Use Default mode (keep unchanged ; URL and processing unchanged)", true);
    private final JRadioButton radioIsZipStrategy = new JRadioButton("Use Zip mode (smaller SQL queries ; reduce URL size but less efficient)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy());
    private final JRadioButton radioIsDiosStrategy = new JRadioButton("Use Dios mode (less queries ; do not use with Error strategies)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy());
    private final JCheckBox checkboxIsUrlEncodingDisabled = new JCheckBox("Disable URL encoding (smaller URL)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled());
    private final JCheckBox checkboxIsUrlRandomSuffixDisabled = new JCheckBox("Disable URL random suffix (strategy Time special use case)", MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlRandomSuffixDisabled());

    public PanelInjection(PanelPreferences panelPreferences) {
        this.checkboxIsNotInjectingMetadata.setName("checkboxIsNotInjectingMetadata");
        this.checkboxIsNotSearchingCharInsertion.setName("checkboxIsNotSearchingCharInsertion");
        this.checkboxIsNotShowingVulnReport.setName("checkboxIsNotShowingVulnReport");
        this.checkboxIsParsingForm.setName("checkboxIsParsingForm");
        this.checkboxIsCheckingAllURLParam.setName("checkboxIsCheckingAllURLParam");
        this.checkboxIsCheckingAllRequestParam.setName("checkboxIsCheckingAllRequestParam");
        this.checkboxIsCheckingAllHeaderParam.setName("checkboxIsCheckingAllHeaderParam");
        this.checkboxIsCheckingAllJSONParam.setName("checkboxIsCheckingAllJSONParam");
        this.checkboxIsCheckingAllBase64Param.setName("checkboxIsCheckingAllBase64Param");
        this.checkboxIsCheckingAllCookieParam.setName("checkboxIsCheckingAllCookieParam");
        this.checkboxIsCheckingAllSOAPParam.setName("checkboxIsCheckingAllSOAPParam");
        this.checkboxIsPerfIndexDisabled.setName("checkboxIsPerfIndexDisabled");
        this.radioIsZipStrategy.setName("radioIsZipStrategy");
        this.radioIsDefaultStrategy.setName("radioIsDefaultStrategy");
        this.radioIsDiosStrategy.setName("radioIsDiosStrategy");
        this.checkboxIsUrlEncodingDisabled.setName("checkboxIsUrlEncodingDisabled");
        this.checkboxIsUrlRandomSuffixDisabled.setName("checkboxIsUrlRandomSuffixDisabled");
        this.checkboxIsLimitingNormalIndex.setName("checkboxIsLimitingNormalIndex");
        this.checkboxIsLimitingSleepTimeStrategy.setName("checkboxIsLimitingSleepTimeStrategy");
        
        this.checkboxIsPerfIndexDisabled.setToolTipText(
            "<html>Reduce Normal calibration URL, useful when host rejects large URL."
            + "<br>Should be enabled when Zip mode is activated.</html>"
        );
        this.checkboxIsParsingForm.setToolTipText(
            "<html>Create name=value params from HTML forms' extracted data.<br>"
            + "Sometimes mandatory params are contained in forms.<br>"
            + "It makes easy adding such params to requests.</html>"
        );
        this.checkboxIsNotInjectingMetadata.setToolTipText("Not injecting metadata saves time, particularly for Blind and Time strategies");
        this.checkboxIsNotSearchingCharInsertion.setToolTipText(
            "<html>Injection query starts usually with prefix like <b>quote</b> or <b>parenthesis</b>:<br>" +
            "- ...&injectMe=' union select...<br>" +
            "- ...&injectMe=) union select...<br>" +
            "Default is searching for the prefix but can be disabled to save time when prefix is already set by the user.</html>"
        );
        this.checkboxIsLimitingSleepTimeStrategy.setToolTipText("<html>Time strategy waits an arbitrary number of seconds for a page to respond.<br>Amount of seconds can be lowered on a stable environment like local tests in order to save time.</html>");

        var panelSleepTimeStrategy = new JPanel();
        panelSleepTimeStrategy.setLayout(new BoxLayout(panelSleepTimeStrategy, BoxLayout.X_AXIS));
        panelSleepTimeStrategy.add(new JLabel("Adjust delay to "), BorderLayout.WEST);
        panelSleepTimeStrategy.add(this.spinnerSleepTimeStrategyCount, BorderLayout.CENTER);
        panelSleepTimeStrategy.add(new JLabel(" s ; default 5s"), BorderLayout.EAST);
        panelSleepTimeStrategy.setMaximumSize(new Dimension(125, this.spinnerSleepTimeStrategyCount.getPreferredSize().height));
        int countSleepTimeStrategy = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countSleepTimeStrategy();
        var spinnerSleepTimeStrategy = new SpinnerNumberModel(
            countSleepTimeStrategy <= 0 ? 15 : countSleepTimeStrategy,
            1,
            30,
            1
        );
        this.spinnerSleepTimeStrategyCount.setModel(spinnerSleepTimeStrategy);
        this.spinnerSleepTimeStrategyCount.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.spinnerSleepTimeStrategyCount.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));

        this.checkboxIsLimitingNormalIndex.setToolTipText("Maximum number of columns to check on UNION based queries");

        var panelIsLimitingNormalIndex = new JPanel();
        panelIsLimitingNormalIndex.setLayout(new BoxLayout(panelIsLimitingNormalIndex, BoxLayout.X_AXIS));
        panelIsLimitingNormalIndex.add(new JLabel("Search for up to "));
        panelIsLimitingNormalIndex.add(this.spinnerNormalIndexCount);
        panelIsLimitingNormalIndex.add(new JLabel(" column(s) ; default 50 columns"));
        panelIsLimitingNormalIndex.setMaximumSize(new Dimension(325, this.spinnerNormalIndexCount.getPreferredSize().height));
        int countNormalIndex = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countNormalIndex();
        var spinnerCountNormalIndex = new SpinnerNumberModel(
            countNormalIndex <= 0 ? 50 : countNormalIndex,
            1,
            200,
            1
        );
        this.spinnerNormalIndexCount.setModel(spinnerCountNormalIndex);
        this.spinnerNormalIndexCount.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.spinnerNormalIndexCount.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));

        this.radioIsDiosStrategy.setToolTipText(
            "<html>Mode Dump In One Shot injects a single query that gets all the data at once."
            + "<br>Faster than default mode for Normal and Error strats but requires volume of data to not be huge.</html>"
        );
        this.radioIsZipStrategy.setToolTipText(
            "<html>Zip mode injects small queries, useful when host rejects large URL."
            + "<br>Downside is metadata like table or row count is not fetched.</html>"
        );

        var labelGeneralInjection = new JLabel("<html><b>Processing</b></html>");
        var labelParamsInjection = new JLabel("<html><br /><b>URL parameters</b></html>");
        var labelSpecial = new JLabel("<html><br /><b>Special parameters</b></html>");
        var labelQuerySize = new JLabel("<html><br /><b>Reduce URL size (advanced)</b></html>");
        Arrays.asList(labelGeneralInjection, labelParamsInjection, labelSpecial, labelQuerySize).forEach(label -> label.setBorder(PanelGeneral.MARGIN));

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
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        };
        
        this.checkboxIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        this.checkboxIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        this.checkboxIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());

        this.checkboxIsCheckingAllParam.addActionListener(actionListenerCheckingAllParam);
        
        Stream.of(
            this.checkboxIsNotInjectingMetadata,
            this.checkboxIsNotSearchingCharInsertion,
            this.checkboxIsNotShowingVulnReport,
            this.checkboxIsParsingForm,
            this.checkboxIsCheckingAllURLParam,
            this.checkboxIsCheckingAllRequestParam,
            this.checkboxIsCheckingAllHeaderParam,
            this.checkboxIsCheckingAllJSONParam,
            this.checkboxIsCheckingAllBase64Param,
            this.checkboxIsCheckingAllCookieParam,
            this.checkboxIsCheckingAllSOAPParam,
            this.checkboxIsPerfIndexDisabled,
            this.radioIsZipStrategy,
            this.radioIsDiosStrategy,
            this.radioIsDefaultStrategy,
            this.checkboxIsUrlEncodingDisabled,
            this.checkboxIsUrlRandomSuffixDisabled,
            this.checkboxIsLimitingNormalIndex,
            this.checkboxIsLimitingSleepTimeStrategy
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        var groupSpaceToComment = new ButtonGroup();
        groupSpaceToComment.add(this.radioIsZipStrategy);
        groupSpaceToComment.add(this.radioIsDiosStrategy);
        groupSpaceToComment.add(this.radioIsDefaultStrategy);

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);

        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(labelGeneralInjection)
                .addComponent(this.checkboxIsParsingForm)
                .addComponent(this.checkboxIsNotInjectingMetadata)
                .addComponent(this.checkboxIsNotSearchingCharInsertion)
                .addComponent(this.checkboxIsNotShowingVulnReport)
                .addComponent(this.checkboxIsLimitingNormalIndex)
                .addComponent(panelIsLimitingNormalIndex)
                .addComponent(this.checkboxIsLimitingSleepTimeStrategy)
                .addComponent(panelSleepTimeStrategy)

                .addComponent(labelParamsInjection)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)

                .addComponent(labelSpecial)
//                .addComponent(this.checkboxIsCheckingAllBase64Param)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
                .addComponent(this.checkboxIsCheckingAllCookieParam)

                .addComponent(labelQuerySize)
                .addComponent(this.radioIsDefaultStrategy)
                .addComponent(this.radioIsDiosStrategy)
                .addComponent(this.radioIsZipStrategy)
                .addComponent(this.checkboxIsPerfIndexDisabled)
                .addComponent(this.checkboxIsUrlEncodingDisabled)
                .addComponent(this.checkboxIsUrlRandomSuffixDisabled)
            )
        );

        groupLayout.setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelGeneralInjection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsParsingForm)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotInjectingMetadata)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotSearchingCharInsertion)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotShowingVulnReport)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsLimitingNormalIndex)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelIsLimitingNormalIndex)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsLimitingSleepTimeStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelSleepTimeStrategy)
            )

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelParamsInjection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllURLParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)
            )

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelSpecial)
            )
//            .addGroup(
//                groupLayout
//                .createParallelGroup(GroupLayout.Alignment.BASELINE)
//                .addComponent(this.checkboxIsCheckingAllBase64Param)
//            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllCookieParam)
            )

            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelQuerySize)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsDefaultStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsDiosStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsZipStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsPerfIndexDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUrlEncodingDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUrlRandomSuffixDisabled)
            )
        );
    }

    
    // Getter and setter
    
    public JCheckBox getCheckboxIsNotInjectingMetadata() {
        return this.checkboxIsNotInjectingMetadata;
    }
    
    public JCheckBox getCheckboxIsNotSearchingCharInsertion() {
        return this.checkboxIsNotSearchingCharInsertion;
    }

    public JCheckBox getCheckboxIsNotShowingVulnReport() {
        return this.checkboxIsNotShowingVulnReport;
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
    
    public JCheckBox getCheckboxIsCheckingAllBase64Param() {
        return this.checkboxIsCheckingAllBase64Param;
    }
    
    public JCheckBox getCheckboxIsCheckingAllJsonParam() {
        return this.checkboxIsCheckingAllJSONParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllCookieParam() {
        return this.checkboxIsCheckingAllCookieParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllSoapParam() {
        return this.checkboxIsCheckingAllSOAPParam;
    }
    
    public JCheckBox getCheckboxIsParsingForm() {
        return this.checkboxIsParsingForm;
    }

    public JCheckBox getCheckboxIsPerfIndexDisabled() {
        return this.checkboxIsPerfIndexDisabled;
    }

    public JRadioButton getRadioIsZipStrategy() {
        return this.radioIsZipStrategy;
    }
    
    public JRadioButton getRadioIsDiosStrategy() {
        return this.radioIsDiosStrategy;
    }
    
    public JRadioButton getRadioIsDefaultStrategy() {
        return this.radioIsDefaultStrategy;
    }
    
    public JCheckBox getCheckboxIsUrlEncodingDisabled() {
        return this.checkboxIsUrlEncodingDisabled;
    }
    
    public JCheckBox getCheckboxIsUrlRandomSuffixDisabled() {
        return this.checkboxIsUrlRandomSuffixDisabled;
    }

    public JCheckBox getCheckboxIsLimitingNormalIndex() {
        return this.checkboxIsLimitingNormalIndex;
    }
    
    public JSpinner getSpinnerNormalIndexCount() {
        return this.spinnerNormalIndexCount;
    }
    
    public JCheckBox getCheckboxIsLimitingSleepTimeStrategy() {
        return this.checkboxIsLimitingSleepTimeStrategy;
    }
    
    public JSpinner getSpinnerSleepTimeStrategy() {
        return this.spinnerSleepTimeStrategyCount;
    }
}

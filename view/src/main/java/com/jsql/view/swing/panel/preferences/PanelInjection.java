package com.jsql.view.swing.panel.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class PanelInjection extends JPanel {

    private final JCheckBox checkboxIsNotInjectingMetadata = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata());
    private final JCheckBox checkboxIsParsingForm = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm());
    
    private final JCheckBox checkboxIsCheckingAllParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllParam());
    private final JCheckBox checkboxIsCheckingAllURLParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam());
    private final JCheckBox checkboxIsCheckingAllRequestParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam());
    private final JCheckBox checkboxIsCheckingAllHeaderParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam());
    private final JCheckBox checkboxIsCheckingAllBase64Param = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllBase64Param());
    private final JCheckBox checkboxIsCheckingAllJSONParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam());
    private final JCheckBox checkboxIsCheckingAllCookieParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllCookieParam());
    private final JCheckBox checkboxIsCheckingAllSOAPParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam());

    private final JCheckBox checkboxIsLimitingNormalIndex = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex());
    private final JSpinner spinnerNormalIndexCount = new JSpinner();

    private final JCheckBox checkboxIsPerfIndexDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled());
    private final JRadioButton checkboxIsZipStrategy = new JRadioButton(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy());
    private final JRadioButton checkboxIsDefaultStrategy = new JRadioButton(StringUtils.EMPTY, true);
    private final JRadioButton checkboxIsDiosStrategy = new JRadioButton(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy());
    private final JCheckBox checkboxIsUrlEncodingDisabled = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled());
    
    public PanelInjection(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String tooltipParseForm = "Add <input> params to Query string and Request";
        this.checkboxIsParsingForm.setToolTipText(tooltipParseForm);
        this.checkboxIsParsingForm.setFocusable(false);
        JButton labelParseForm = new JButton("Add <input> params to Query string and Request");
        labelParseForm.setToolTipText(tooltipParseForm);
        labelParseForm.addActionListener(actionEvent -> {
            
            this.checkboxIsParsingForm.setSelected(!this.checkboxIsParsingForm.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsNotInjectingMetadata = "Disable database's metadata injection (speed-up boolean process)";
        this.checkboxIsNotInjectingMetadata.setToolTipText(tooltipIsNotInjectingMetadata);
        this.checkboxIsNotInjectingMetadata.setFocusable(false);
        JButton labelIsNotInjectingMetadata = new JButton("Disable database's metadata injection (speed-up boolean process)");
        labelIsNotInjectingMetadata.setToolTipText(tooltipIsNotInjectingMetadata);
        labelIsNotInjectingMetadata.addActionListener(actionEvent -> {
            
            this.checkboxIsNotInjectingMetadata.setSelected(!this.checkboxIsNotInjectingMetadata.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsLimitingNormalIndex = "Limit Normal indexes to";
        this.checkboxIsLimitingNormalIndex.setToolTipText(tooltipIsLimitingNormalIndex);
        this.checkboxIsLimitingNormalIndex.setFocusable(false);
        JButton labelIsLimitingNormalIndex = new JButton("Limit Normal indexes to");
        labelIsLimitingNormalIndex.setToolTipText(tooltipIsLimitingNormalIndex);
        labelIsLimitingNormalIndex.addActionListener(actionEvent -> {
            
            this.checkboxIsLimitingNormalIndex.setSelected(!this.checkboxIsLimitingNormalIndex.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        JPanel panelIsLimitingNormalIndex = new JPanel(new BorderLayout());
        panelIsLimitingNormalIndex.add(labelIsLimitingNormalIndex, BorderLayout.WEST);
        panelIsLimitingNormalIndex.add(this.spinnerNormalIndexCount, BorderLayout.CENTER);
        panelIsLimitingNormalIndex.add(new JLabel(" (default 50)"), BorderLayout.EAST);
        panelIsLimitingNormalIndex.setMaximumSize(new Dimension(250, this.spinnerNormalIndexCount.getPreferredSize().height));
        this.spinnerNormalIndexCount.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));
        
        int countNormalIndex = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().countNormalIndex();
        SpinnerNumberModel spinnerCountNormalIndex = new SpinnerNumberModel(
            countNormalIndex <= 0
            ? 50
            : countNormalIndex,
            1,
            100,
            1
        );
        this.spinnerNormalIndexCount.setModel(spinnerCountNormalIndex);
        
        JButton labelIsCheckingAllParam = new JButton("Inject each parameter and ignore user's method");
        JButton labelIsCheckingAllURLParam = new JButton("Inject each URL parameter if method is GET");
        JButton labelIsCheckingAllRequestParam = new JButton("Inject each Request parameter if method is Request");
        JButton labelIsCheckingAllHeaderParam = new JButton("Inject each Header parameter if method is Header");
        JButton labelIsCheckingAllCookieParam = new JButton("Inject each Cookie parameter");
        JButton labelIsCheckingAllJSONParam = new JButton("Inject JSON parameters");
        JButton labelIsCheckingAllBase64Param = new JButton("Inject Base64 parameters");
        JButton labelIsCheckingAllSOAPParam = new JButton("Inject SOAP parameters in Request body");
        
        JButton labelIsDefaultStrategy = new JButton("Use Default mode (use this ; no change to URL or processing)");
        JButton labelIsDiosStrategy = new JButton("Use Dios mode (less queries ; do not use with Error strategies)");
        JButton labelIsZipStrategy = new JButton("Use Zip mode (smaller SQL queries ; reduce URL size but less efficient)");
        JButton labelIsUrlEncodingDisabled = new JButton("Disable URL encoding (smaller URL)");
        JButton labelIsPerfIndexDisabled = new JButton("Disable calibration (smaller SQL query during Normal index selection only)");
        
        JLabel emptyLabelGeneralInjection = new JLabel();
        JLabel labelGeneralInjection = new JLabel("<html><b>Content processing</b></html>");
        JLabel emptyLabelParamsInjection = new JLabel();
        JLabel labelParamsInjection = new JLabel("<html><br /><b>URL parameters</b></html>");
        JLabel emptyLabelSpecial = new JLabel();
        JLabel labelSpecial = new JLabel("<html><br /><b>Special parameters</b></html>");
        JLabel emptyLabelQuerySize = new JLabel();
        JLabel labelQuerySize = new JLabel("<html><br /><b>Reduce processing and URL size (advanced)</b></html>");
        
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
        labelIsCheckingAllBase64Param.addActionListener(actionEvent -> {
            
            this.checkboxIsCheckingAllBase64Param.setSelected(!this.checkboxIsCheckingAllBase64Param.isSelected());
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
        labelIsPerfIndexDisabled.addActionListener(actionEvent -> {
            
            this.checkboxIsPerfIndexDisabled.setSelected(!this.checkboxIsPerfIndexDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsZipStrategy.addActionListener(actionEvent -> {
            
            this.checkboxIsZipStrategy.setSelected(!this.checkboxIsZipStrategy.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsDiosStrategy.addActionListener(actionEvent -> {
            
            this.checkboxIsDiosStrategy.setSelected(!this.checkboxIsDiosStrategy.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsDefaultStrategy.addActionListener(actionEvent -> {
            
            this.checkboxIsDefaultStrategy.setSelected(!this.checkboxIsDefaultStrategy.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        labelIsUrlEncodingDisabled.addActionListener(actionEvent -> {
            
            this.checkboxIsUrlEncodingDisabled.setSelected(!this.checkboxIsUrlEncodingDisabled.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        this.checkboxIsCheckingAllParam.addActionListener(actionListenerCheckingAllParam);
        
        Stream
        .of(
            this.checkboxIsNotInjectingMetadata,
            this.checkboxIsParsingForm,
            this.checkboxIsCheckingAllURLParam,
            this.checkboxIsCheckingAllRequestParam,
            this.checkboxIsCheckingAllHeaderParam,
            this.checkboxIsCheckingAllJSONParam,
            this.checkboxIsCheckingAllBase64Param,
            this.checkboxIsCheckingAllCookieParam,
            this.checkboxIsCheckingAllSOAPParam,
            this.checkboxIsPerfIndexDisabled,
            this.checkboxIsZipStrategy,
            this.checkboxIsUrlEncodingDisabled,
            this.checkboxIsLimitingNormalIndex
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream
        .of(
            labelParseForm,
            labelIsNotInjectingMetadata,
            labelIsCheckingAllParam,
            labelIsCheckingAllURLParam,
            labelIsCheckingAllRequestParam,
            labelIsCheckingAllHeaderParam,
            labelIsCheckingAllJSONParam,
            labelIsCheckingAllBase64Param,
            labelIsCheckingAllCookieParam,
            labelIsCheckingAllSOAPParam,
            labelIsPerfIndexDisabled,
            labelIsZipStrategy,
            labelIsDiosStrategy,
            labelIsDefaultStrategy,
            labelIsUrlEncodingDisabled,
            labelIsLimitingNormalIndex
        )
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        ButtonGroup groupSpaceToComment = new ButtonGroup();
        groupSpaceToComment.add(this.checkboxIsZipStrategy);
        groupSpaceToComment.add(this.checkboxIsDiosStrategy);
        groupSpaceToComment.add(this.checkboxIsDefaultStrategy);
        
        GroupLayout groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);

        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(emptyLabelGeneralInjection)
                .addComponent(this.checkboxIsParsingForm)
                .addComponent(this.checkboxIsNotInjectingMetadata)
                .addComponent(this.checkboxIsLimitingNormalIndex)
                
                .addComponent(emptyLabelParamsInjection)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)
                
                .addComponent(emptyLabelSpecial)
                .addComponent(this.checkboxIsCheckingAllBase64Param)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
                .addComponent(this.checkboxIsCheckingAllCookieParam)
                
                .addComponent(emptyLabelQuerySize)
                .addComponent(this.checkboxIsDefaultStrategy)
                .addComponent(this.checkboxIsDiosStrategy)
                .addComponent(this.checkboxIsZipStrategy)
                .addComponent(this.checkboxIsUrlEncodingDisabled)
                .addComponent(this.checkboxIsPerfIndexDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelGeneralInjection)
                .addComponent(labelParseForm)
                .addComponent(labelIsNotInjectingMetadata)
                .addComponent(panelIsLimitingNormalIndex)
                
                .addComponent(labelParamsInjection)
                .addComponent(labelIsCheckingAllParam)
                .addComponent(labelIsCheckingAllURLParam)
                .addComponent(labelIsCheckingAllRequestParam)
                .addComponent(labelIsCheckingAllHeaderParam)
                
                .addComponent(labelSpecial)
                .addComponent(labelIsCheckingAllBase64Param)
                .addComponent(labelIsCheckingAllJSONParam)
                .addComponent(labelIsCheckingAllSOAPParam)
                .addComponent(labelIsCheckingAllCookieParam)

                .addComponent(labelQuerySize)
                .addComponent(labelIsDefaultStrategy)
                .addComponent(labelIsDiosStrategy)
                .addComponent(labelIsZipStrategy)
                .addComponent(labelIsUrlEncodingDisabled)
                .addComponent(labelIsPerfIndexDisabled)
            )
        );
        
        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelGeneralInjection)
                .addComponent(labelGeneralInjection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsParsingForm)
                .addComponent(labelParseForm)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsNotInjectingMetadata)
                .addComponent(labelIsNotInjectingMetadata)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsLimitingNormalIndex)
                .addComponent(panelIsLimitingNormalIndex)
            )
            
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelParamsInjection)
                .addComponent(labelParamsInjection)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(labelIsCheckingAllParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(labelIsCheckingAllURLParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(labelIsCheckingAllRequestParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)
                .addComponent(labelIsCheckingAllHeaderParam)
            )
            
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelSpecial)
                .addComponent(labelSpecial)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllBase64Param)
                .addComponent(labelIsCheckingAllBase64Param)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(labelIsCheckingAllJSONParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
                .addComponent(labelIsCheckingAllSOAPParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllCookieParam)
                .addComponent(labelIsCheckingAllCookieParam)
            )
            
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelQuerySize)
                .addComponent(labelQuerySize)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsDefaultStrategy)
                .addComponent(labelIsDefaultStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsDiosStrategy)
                .addComponent(labelIsDiosStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsZipStrategy)
                .addComponent(labelIsZipStrategy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUrlEncodingDisabled)
                .addComponent(labelIsUrlEncodingDisabled)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsPerfIndexDisabled)
                .addComponent(labelIsPerfIndexDisabled)
            )
        );
    }

    
    // Getter and setter
    
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

    public JRadioButton getCheckboxIsZipStrategy() {
        return this.checkboxIsZipStrategy;
    }
    
    public JRadioButton getCheckboxIsDiosStrategy() {
        return this.checkboxIsDiosStrategy;
    }
    
    public JCheckBox getCheckboxIsUrlEncodingDisabled() {
        return this.checkboxIsUrlEncodingDisabled;
    }
    
    public JCheckBox getCheckboxIsLimitingNormalIndex() {
        return this.checkboxIsLimitingNormalIndex;
    }
    
    public JSpinner getSpinnerNormalIndexCount() {
        return this.spinnerNormalIndexCount;
    }
}

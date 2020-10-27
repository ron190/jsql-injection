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
public class PanelInjectionPreferences extends JPanel {

    private final JCheckBox checkboxIsNotInjectingMetadata = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata());
    private final JCheckBox checkboxIsParsingForm = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm());
    
    private final JCheckBox checkboxIsCheckingAllParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllParam());
    private final JCheckBox checkboxIsCheckingAllURLParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam());
    private final JCheckBox checkboxIsCheckingAllRequestParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam());
    private final JCheckBox checkboxIsCheckingAllHeaderParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam());
    private final JCheckBox checkboxIsCheckingAllJSONParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam());
    private final JCheckBox checkboxIsCheckingAllCookieParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllCookieParam());
    private final JCheckBox checkboxIsCheckingAllSOAPParam = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSOAPParam());
    
    public PanelInjectionPreferences(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

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
        JLabel emptyLabelBooleanInjection = new JLabel();
        JLabel labelBooleanInjection = new JLabel("<html><br /><b>Boolean configuration for Blind/Time injection</b></html>");
        JLabel emptyLabelParamsInjection = new JLabel();
        JLabel labelParamsInjection = new JLabel("<html><br /><b>Parameters injection</b></html>");
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
            this.checkboxIsNotInjectingMetadata,
            this.checkboxIsParsingForm,
            this.checkboxIsCheckingAllURLParam,
            this.checkboxIsCheckingAllRequestParam,
            this.checkboxIsCheckingAllHeaderParam,
            this.checkboxIsCheckingAllJSONParam,
            this.checkboxIsCheckingAllCookieParam,
            this.checkboxIsCheckingAllSOAPParam
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        Stream.of(
            labelParseForm,
            labelIsNotInjectingMetadata,
            labelIsCheckingAllParam,
            labelIsCheckingAllURLParam,
            labelIsCheckingAllRequestParam,
            labelIsCheckingAllHeaderParam,
            labelIsCheckingAllJSONParam,
            labelIsCheckingAllCookieParam,
            labelIsCheckingAllSOAPParam
        )
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
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
                
                .addComponent(emptyLabelBooleanInjection)
                
                .addComponent(emptyLabelParamsInjection)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)
                
                .addComponent(emptyLabelValueInjection)
                .addComponent(this.checkboxIsCheckingAllCookieParam)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(this.checkboxIsCheckingAllSOAPParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelGeneralInjection)
                .addComponent(labelParseForm)
                .addComponent(labelIsNotInjectingMetadata)
                
                .addComponent(labelBooleanInjection)
                
                .addComponent(labelParamsInjection)
                .addComponent(labelIsCheckingAllParam)
                .addComponent(labelIsCheckingAllURLParam)
                .addComponent(labelIsCheckingAllRequestParam)
                .addComponent(labelIsCheckingAllHeaderParam)
                
                .addComponent(labelValueInjection)
                .addComponent(labelIsCheckingAllJSONParam)
                .addComponent(labelIsCheckingAllSOAPParam)
                .addComponent(labelIsCheckingAllCookieParam)
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
                .addComponent(emptyLabelBooleanInjection)
                .addComponent(labelBooleanInjection)
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
                .addComponent(emptyLabelValueInjection)
                .addComponent(labelValueInjection)
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
}

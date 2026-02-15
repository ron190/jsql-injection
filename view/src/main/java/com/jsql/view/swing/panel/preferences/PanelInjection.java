package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.panel.preferences.listener.SpinnerMouseWheelListener;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.stream.Stream;

public class PanelInjection extends JPanel {

    public static final String CHECKBOX_IS_PARSING_FORM = "checkboxIsParsingForm";
    public static final String RADIO_IS_ZIP_STRATEGY = "radioIsZipStrategy";
    public static final String RADIO_IS_DIOS_STRATEGY = "radioIsDiosStrategy";
    public static final String RADIO_IS_DEFAULT_STRATEGY = "radioIsDefaultStrategy";

    private final JCheckBox checkboxIsNotShowingVulnReport = new JCheckBox("Disable showing vulnerability report", MediatorHelper.model().getMediatorUtils().preferencesUtil().isNotShowingVulnReport());
    private final JCheckBox checkboxIsNotSearchingCharInsertion = new JCheckBox("Disable search for character insertion", MediatorHelper.model().getMediatorUtils().preferencesUtil().isNotSearchingCharInsertion());
    private final JCheckBox checkboxIsNotInjectingMetadata = new JCheckBox("Disable search of database name, version and user metadata", MediatorHelper.model().getMediatorUtils().preferencesUtil().isNotInjectingMetadata());
    private final JCheckBox checkboxIsParsingForm = new JCheckBox("Get HTML tags <input/> and add parameters to URL and Request", MediatorHelper.model().getMediatorUtils().preferencesUtil().isParsingForm());
    
    private final JCheckBox checkboxIsCheckingAllParam = new JCheckBox("Inject all params and ignore user's selection", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllParam());
    private final JCheckBox checkboxIsCheckingAllURLParam = new JCheckBox("Inject all URL params when URL method is selected", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllURLParam());
    private final JCheckBox checkboxIsCheckingAllRequestParam = new JCheckBox("Inject all Request params when Request method is selected", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllRequestParam());
    private final JCheckBox checkboxIsCheckingAllHeaderParam = new JCheckBox("Inject all Header params when Header method is selected", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllHeaderParam());
    private final JCheckBox checkboxIsCheckingAllJSONParam = new JCheckBox("Inject all JSON params", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllJsonParam());
    private final JCheckBox checkboxIsCheckingAllCookieParam = new JCheckBox("Inject all Cookie params in Request body", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllCookieParam());
    private final JCheckBox checkboxIsCheckingAllSoapParam = new JCheckBox("Inject all SOAP params in Request body", MediatorHelper.model().getMediatorUtils().preferencesUtil().isCheckingAllSoapParam());

    private final JCheckBox checkboxIsLimitingUnionIndex = new JCheckBox("Limit Union strategy:", MediatorHelper.model().getMediatorUtils().preferencesUtil().isLimitingUnionIndex());
    private final JSpinner spinnerUnionIndexCount = new JSpinner();
    private final JCheckBox checkboxIsLimitingSleepTimeStrategy = new JCheckBox("Delay Time strategy:", MediatorHelper.model().getMediatorUtils().preferencesUtil().isLimitingSleepTimeStrategy());
    private final JSpinner spinnerSleepTimeStrategyCount = new JSpinner();

    private final JCheckBox checkboxIsPerfIndexDisabled = new JCheckBox("Disable calibration (smaller SQL query during Union index selection only)", MediatorHelper.model().getMediatorUtils().preferencesUtil().isPerfIndexDisabled());
    private final JRadioButton radioIsDefaultStrategy = new JRadioButton("Use Default mode (keep unchanged ; URL and processing unchanged)", true);
    private final JRadioButton radioIsZipStrategy = new JRadioButton("Use Zip mode (smaller SQL queries ; reduce URL size but less efficient)", MediatorHelper.model().getMediatorUtils().preferencesUtil().isZipStrategy());
    private final JRadioButton radioIsDiosStrategy = new JRadioButton("Use Dios mode (less queries ; do not use with Error strategies)", MediatorHelper.model().getMediatorUtils().preferencesUtil().isDiosStrategy());
    private final JCheckBox checkboxIsUrlEncodingDisabled = new JCheckBox("Disable URL encoding (smaller URL)", MediatorHelper.model().getMediatorUtils().preferencesUtil().isUrlEncodingDisabled());
    private final JCheckBox checkboxIsUrlRandomSuffixDisabled = new JCheckBox("Disable URL random suffix", MediatorHelper.model().getMediatorUtils().preferencesUtil().isUrlRandomSuffixDisabled());

    private final JTextField textfieldDnsDomain = new JPopupTextField("e.g custom-domain.com", MediatorHelper.model().getMediatorUtils().preferencesUtil().getDnsDomain()).getProxy();
    private final JTextField textfieldDnsPort = new JPopupTextField("e.g 53", MediatorHelper.model().getMediatorUtils().preferencesUtil().getDnsPort()).getProxy();

    public PanelInjection(PanelPreferences panelPreferences) {
        this.checkboxIsNotInjectingMetadata.setName("checkboxIsNotInjectingMetadata");
        this.checkboxIsNotSearchingCharInsertion.setName("checkboxIsNotSearchingCharInsertion");
        this.checkboxIsNotShowingVulnReport.setName("checkboxIsNotShowingVulnReport");
        this.checkboxIsParsingForm.setName(PanelInjection.CHECKBOX_IS_PARSING_FORM);

        this.checkboxIsCheckingAllURLParam.setName("checkboxIsCheckingAllURLParam");
        this.checkboxIsCheckingAllRequestParam.setName("checkboxIsCheckingAllRequestParam");
        this.checkboxIsCheckingAllHeaderParam.setName("checkboxIsCheckingAllHeaderParam");
        this.checkboxIsCheckingAllJSONParam.setName("checkboxIsCheckingAllJSONParam");
        this.checkboxIsCheckingAllCookieParam.setName("checkboxIsCheckingAllCookieParam");
        this.checkboxIsCheckingAllSoapParam.setName("checkboxIsCheckingAllSOAPParam");

        this.checkboxIsPerfIndexDisabled.setName("checkboxIsPerfIndexDisabled");
        this.radioIsZipStrategy.setName(PanelInjection.RADIO_IS_ZIP_STRATEGY);
        this.radioIsDefaultStrategy.setName(PanelInjection.RADIO_IS_DEFAULT_STRATEGY);
        this.radioIsDiosStrategy.setName(PanelInjection.RADIO_IS_DIOS_STRATEGY);
        this.checkboxIsUrlEncodingDisabled.setName("checkboxIsUrlEncodingDisabled");
        this.checkboxIsUrlRandomSuffixDisabled.setName("checkboxIsUrlRandomSuffixDisabled");
        this.checkboxIsLimitingUnionIndex.setName("checkboxIsLimitingUnionIndex");
        this.checkboxIsLimitingSleepTimeStrategy.setName("checkboxIsLimitingSleepTimeStrategy");
        
        this.checkboxIsPerfIndexDisabled.setToolTipText(
            "<html>Reduce Union calibration URL, useful when host rejects large URL."
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
        this.checkboxIsLimitingSleepTimeStrategy.setToolTipText("<html>Time strategy waits a given number of seconds for a page to respond (fallback to default if unchecked).<br>Amount of seconds can be lowered on a stable environment to save time (e.g. local tests).</html>");

        var panelSleepTimeStrategy = new JPanel();
        panelSleepTimeStrategy.setLayout(new BoxLayout(panelSleepTimeStrategy, BoxLayout.X_AXIS));
        panelSleepTimeStrategy.add(new JLabel("Adjust delay to "), BorderLayout.WEST);
        panelSleepTimeStrategy.add(this.spinnerSleepTimeStrategyCount, BorderLayout.CENTER);
        panelSleepTimeStrategy.add(new JLabel(" s ; default 5s"), BorderLayout.EAST);
        panelSleepTimeStrategy.setMaximumSize(new Dimension(125, this.spinnerSleepTimeStrategyCount.getPreferredSize().height));
        int countSleepTimeStrategy = MediatorHelper.model().getMediatorUtils().preferencesUtil().countSleepTimeStrategy();
        var spinnerSleepTimeStrategy = new SpinnerNumberModel(
            countSleepTimeStrategy <= 0 ? 15 : countSleepTimeStrategy,
            1,
            30,
            1
        );
        this.spinnerSleepTimeStrategyCount.setModel(spinnerSleepTimeStrategy);
        this.spinnerSleepTimeStrategyCount.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.spinnerSleepTimeStrategyCount.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));

        this.checkboxIsLimitingUnionIndex.setToolTipText("Maximum number of columns to check on UNION based queries");

        var panelIsLimitingUnionIndex = new JPanel();
        panelIsLimitingUnionIndex.setLayout(new BoxLayout(panelIsLimitingUnionIndex, BoxLayout.X_AXIS));
        panelIsLimitingUnionIndex.add(new JLabel("Search for up to "));
        panelIsLimitingUnionIndex.add(this.spinnerUnionIndexCount);
        panelIsLimitingUnionIndex.add(new JLabel(" column(s) ; default 50 columns"));
        panelIsLimitingUnionIndex.setMaximumSize(new Dimension(325, this.spinnerUnionIndexCount.getPreferredSize().height));
        int countUnionIndex = MediatorHelper.model().getMediatorUtils().preferencesUtil().countUnionIndex();
        var spinnerCountUnionIndex = new SpinnerNumberModel(
            countUnionIndex <= 0 ? 50 : countUnionIndex,
            1,
            200,
            1
        );
        this.spinnerUnionIndexCount.setModel(spinnerCountUnionIndex);
        this.spinnerUnionIndexCount.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.spinnerUnionIndexCount.addChangeListener(e -> panelPreferences.getActionListenerSave().actionPerformed(null));

        this.radioIsDiosStrategy.setToolTipText(
            "<html>Mode Dump In One Shot injects a single query that gets all the data at once."
            + "<br>Faster than default mode for Union and Error strats but requires volume of data to not be huge.</html>"
        );
        this.radioIsZipStrategy.setToolTipText(
            "<html>Zip mode injects small queries, useful when host rejects large URL."
            + "<br>Downside is metadata like table or row count is not fetched.</html>"
        );

        var labelGeneralInjection = new JLabel("<html><b>Processing</b></html>");
        var labelParamsInjection = new JLabel("<html><br><b>URL parameters</b></html>");
        var labelSpecial = new JLabel("<html><br><b>Special parameters</b></html>");
        var labelQuerySize = new JLabel("<html><br><b>[Advanced] Reduce URL size</b></html>");
        var labelDns = new JLabel("<html><br><b>[Advanced] DNS exfiltration (also working on local database without registrar)</b></html>");
        Arrays.asList(
            labelGeneralInjection,
            labelParamsInjection,
            labelSpecial,
            labelQuerySize,
            labelDns
        ).forEach(label -> label.setBorder(PanelGeneral.MARGIN));

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
            this.checkboxIsCheckingAllCookieParam,
            this.checkboxIsCheckingAllSoapParam,

            this.checkboxIsPerfIndexDisabled,
            this.radioIsZipStrategy,
            this.radioIsDiosStrategy,
            this.radioIsDefaultStrategy,
            this.checkboxIsUrlEncodingDisabled,
            this.checkboxIsUrlRandomSuffixDisabled,
            this.checkboxIsLimitingUnionIndex,
            this.checkboxIsLimitingSleepTimeStrategy
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));

        var panelDnsDomain = new JPanel();
        panelDnsDomain.setLayout(new BoxLayout(panelDnsDomain, BoxLayout.X_AXIS));
        panelDnsDomain.add(new JLabel("Domain name "));
        panelDnsDomain.add(this.textfieldDnsDomain);
        panelDnsDomain.add(new JLabel(" ; default custom-domain.com"));
        panelDnsDomain.setMaximumSize(new Dimension(400, this.textfieldDnsDomain.getPreferredSize().height));
        var panelDnsPort = new JPanel();
        panelDnsPort.setLayout(new BoxLayout(panelDnsPort, BoxLayout.X_AXIS));
        panelDnsPort.add(new JLabel("DNS port "));
        panelDnsPort.add(this.textfieldDnsPort);
        panelDnsPort.add(new JLabel(" ; default 53"));
        panelDnsPort.setMaximumSize(new Dimension(125, this.textfieldDnsPort.getPreferredSize().height));

        DocumentListener documentListenerSave = new DocumentListenerEditing() {
            @Override
            public void process() {
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        };

        Stream.of(this.textfieldDnsDomain, this.textfieldDnsPort).forEach(
            textField -> textField.getDocument().addDocumentListener(documentListenerSave)
        );

        this.textfieldDnsDomain.setToolTipText(
            "<html>"
            + "<b>This domain must redirect to your IP address that runs jSQL</b><br>"
            + "You can run DNS exfiltration on a local database, replace your ISP box address<br>"
            + "by your system address in your OS preferred DNS.<br>"
            + "</html>"
        );
        this.textfieldDnsPort.setToolTipText("The DNS server port started by jSQL");

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
                .addComponent(this.checkboxIsLimitingUnionIndex)
                .addComponent(panelIsLimitingUnionIndex)
                .addComponent(this.checkboxIsLimitingSleepTimeStrategy)
                .addComponent(panelSleepTimeStrategy)

                .addComponent(labelParamsInjection)
                .addComponent(this.checkboxIsCheckingAllParam)
                .addComponent(this.checkboxIsCheckingAllURLParam)
                .addComponent(this.checkboxIsCheckingAllRequestParam)
                .addComponent(this.checkboxIsCheckingAllHeaderParam)

                .addComponent(labelSpecial)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
                .addComponent(this.checkboxIsCheckingAllSoapParam)
                .addComponent(this.checkboxIsCheckingAllCookieParam)

                .addComponent(labelQuerySize)
                .addComponent(this.radioIsDefaultStrategy)
                .addComponent(this.radioIsDiosStrategy)
                .addComponent(this.radioIsZipStrategy)
                .addComponent(this.checkboxIsPerfIndexDisabled)
                .addComponent(this.checkboxIsUrlEncodingDisabled)
                .addComponent(this.checkboxIsUrlRandomSuffixDisabled)

                .addComponent(labelDns)
                .addComponent(panelDnsDomain)
                .addComponent(panelDnsPort)
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
                .addComponent(this.checkboxIsLimitingUnionIndex)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelIsLimitingUnionIndex)
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
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllJSONParam)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCheckingAllSoapParam)
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
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelDns)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelDnsDomain)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelDnsPort)
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
    
    public JCheckBox getCheckboxIsCheckingAllJsonParam() {
        return this.checkboxIsCheckingAllJSONParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllCookieParam() {
        return this.checkboxIsCheckingAllCookieParam;
    }
    
    public JCheckBox getCheckboxIsCheckingAllSoapParam() {
        return this.checkboxIsCheckingAllSoapParam;
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

    public JCheckBox getCheckboxIsLimitingUnionIndex() {
        return this.checkboxIsLimitingUnionIndex;
    }
    
    public JSpinner getSpinnerUnionIndexCount() {
        return this.spinnerUnionIndexCount;
    }
    
    public JCheckBox getCheckboxIsLimitingSleepTimeStrategy() {
        return this.checkboxIsLimitingSleepTimeStrategy;
    }
    
    public JSpinner getSpinnerSleepTimeStrategy() {
        return this.spinnerSleepTimeStrategyCount;
    }

    public JTextField getTextfieldDnsDomain() {
        return this.textfieldDnsDomain;
    }

    public JTextField getTextfieldDnsPort() {
        return this.textfieldDnsPort;
    }
}

/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.util.I18nUtil;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.view.swing.manager.util.ActionBruteForce;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.ModelBrute;
import com.jsql.view.swing.manager.util.ModelSpinner;
import com.jsql.view.swing.panel.preferences.listener.SpinnerMouseWheelListener;
import com.jsql.view.swing.text.*;
import com.jsql.view.swing.util.I18nViewUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manager to brute force a hash of various types.
 */
public class ManagerBruteForce extends JPanel {

    public static final String BRUTEFORCE_RUN_BUTTON_TOOLTIP = "BRUTEFORCE_RUN_BUTTON_TOOLTIP";
    public static final String BRUTEFORCE_HASH_TOOLTIP = "BRUTEFORCE_HASH_TOOLTIP";
    public static final String BRUTEFORCE_EXCLUDE_TOOLTIP = "BRUTEFORCE_EXCLUDE_TOOLTIP";

    private JButtonStateful run;
    private JTextField hash;
    private JComboBox<String> hashTypes;
    private final AtomicReference<JCheckBox> lowerCaseCharacters = new AtomicReference<>();
    private final AtomicReference<JCheckBox> upperCaseCharacters = new AtomicReference<>();
    private final AtomicReference<JCheckBox> numericCharacters = new AtomicReference<>();
    private final AtomicReference<JCheckBox> specialCharacters = new AtomicReference<>();
    private JTextField exclude;
    private final AtomicReference<JSpinner> minimumLength = new AtomicReference<>();
    private final AtomicReference<JSpinner> maximumLength = new AtomicReference<>();
    private final JTextPane result;
    
    /**
     * Animated GIF displayed during attack.
     */
    private JProgressBar progressBar;
    private final Component horizontalGlue = Box.createHorizontalGlue();

    /**
     * Create a panel to run brute force attack.
     */
    public ManagerBruteForce() {
        super(new BorderLayout());

        JPanel panelOptions = this.initOptionsPanel();
        this.add(panelOptions, BorderLayout.NORTH);

        var placeholder = new JTextPanePlaceholder(I18nUtil.valueByKey("BRUTEFORCE_RESULT"));
        this.result = new JPopupTextPane(placeholder).getProxy();
        I18nViewUtil.addComponentForKey("BRUTEFORCE_RESULT", placeholder);
        this.result.setName("managerBruterResult");
        this.result.setEditable(false);
        this.add(new JScrollPane(this.result), BorderLayout.CENTER);

        JPanel panelButton = this.initPanelButton();
        this.add(panelButton, BorderLayout.SOUTH);
    }

    private JPanel initPanelButton() {
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));

        var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(ManagerBruteForce.BRUTEFORCE_RUN_BUTTON_TOOLTIP)));
        this.run = new JButtonStateful("BRUTEFORCE_RUN_BUTTON_LABEL") {
            @Override
            public JToolTip createToolTip() {
                return tooltip.get();
            }
        };
        I18nViewUtil.addComponentForKey("BRUTEFORCE_RUN_BUTTON_LABEL", this.run);
        I18nViewUtil.addComponentForKey(ManagerBruteForce.BRUTEFORCE_RUN_BUTTON_TOOLTIP, tooltip.get());
        this.run.setToolTipText(I18nUtil.valueByKey(ManagerBruteForce.BRUTEFORCE_RUN_BUTTON_TOOLTIP));

        this.run.setName("managerBruterRun");
        this.run.addActionListener(new ActionBruteForce(this));

        this.progressBar = new JProgressBar();
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisible(false);
        this.progressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        lastLine.add(this.horizontalGlue);
        lastLine.add(this.progressBar);
        lastLine.add(this.run);
        return lastLine;
    }

    public void showLoader(boolean isVisible) {
        this.progressBar.setVisible(isVisible);
        this.horizontalGlue.setVisible(!isVisible);
    }

    private JPanel initOptionsPanel() {
        var options = new JPanel(new BorderLayout());
        JPanel firstLine = this.initFirstLine();
        final JPanel secondLine = this.initSecondLine();
        JPanel thirdLine = this.initThirdLine();
        
        final var secondAndThirdLine = new JPanel(new BorderLayout());
        secondAndThirdLine.add(secondLine, BorderLayout.NORTH);
        secondAndThirdLine.add(thirdLine, BorderLayout.SOUTH);

        options.add(firstLine, BorderLayout.NORTH);
        options.add(secondAndThirdLine, BorderLayout.SOUTH);
        return options;
    }

    private JPanel initFirstLine() {
        var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(ManagerBruteForce.BRUTEFORCE_HASH_TOOLTIP)));
        var placeholder = new JTextFieldPlaceholder(I18nUtil.valueByKey("BRUTEFORCE_HASH_LABEL")) {
            @Override
            public JToolTip createToolTip() {
                return tooltip.get();
            }
        };
        this.hash = new JPopupTextField(placeholder).getProxy();
        I18nViewUtil.addComponentForKey(ManagerBruteForce.BRUTEFORCE_HASH_TOOLTIP, tooltip.get());
        I18nViewUtil.addComponentForKey("BRUTEFORCE_HASH_LABEL", this.hash);
        this.hash.setName("managerBruterHash");
        this.hash.setToolTipText(I18nUtil.valueByKey(ManagerBruteForce.BRUTEFORCE_HASH_TOOLTIP));

        var firstLine = new JPanel(new BorderLayout());
        firstLine.add(this.hash, BorderLayout.CENTER);
        return firstLine;
    }

    private JPanel initSecondLine() {
        final var secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));

        this.hashTypes = new JComboBox<>(ActionCoder.getHashes().toArray(String[]::new));
        this.hashTypes.setSelectedIndex(6);
        this.hashTypes.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_HASH_TYPE_TOOLTIP"));
        I18nViewUtil.addComponentForKey("BRUTEFORCE_HASH_TYPE_TOOLTIP", this.hashTypes);
        secondLine.add(this.hashTypes);

        Arrays.asList(
            new ModelBrute(this.lowerCaseCharacters, "a-z", "BRUTEFORCE_LCASE_TOOLTIP"),
            new ModelBrute(this.upperCaseCharacters, "A-Z", "BRUTEFORCE_UCASE_TOOLTIP"),
            new ModelBrute(this.numericCharacters, "0-9", "BRUTEFORCE_NUM_TOOLTIP"),
            new ModelBrute(this.specialCharacters, "Special", "BRUTEFORCE_SPEC_TOOLTIP")
        ).forEach(modelBrute -> {
            var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(modelBrute.i18nTooltip())));
            var checkbox = new JCheckBox(modelBrute.text(), true) {
                @Override
                public JToolTip createToolTip() {
                    return tooltip.get();
                }
            };
            checkbox.setName(modelBrute.text());
            checkbox.setToolTipText(I18nUtil.valueByKey(modelBrute.i18nTooltip()));
            I18nViewUtil.addComponentForKey(modelBrute.i18nTooltip(), tooltip.get());
            secondLine.add(Box.createHorizontalStrut(5));
            secondLine.add(checkbox);
            modelBrute.checkbox().set(checkbox);
        });

        return secondLine;
    }

    private JPanel initThirdLine() {
        var thirdLine = new JPanel();
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.X_AXIS));

        final var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(ManagerBruteForce.BRUTEFORCE_EXCLUDE_TOOLTIP)));
        var placeholderTooltip = new JTextFieldPlaceholder(I18nUtil.valueByKey("BRUTEFORCE_EXCLUDE_LABEL")) {
            @Override
            public JToolTip createToolTip() {
                return tooltip.get();
            }
        };
        this.exclude = new JPopupTextField(placeholderTooltip).getProxy();
        this.exclude.setToolTipText(I18nUtil.valueByKey(ManagerBruteForce.BRUTEFORCE_EXCLUDE_TOOLTIP));
        I18nViewUtil.addComponentForKey("BRUTEFORCE_EXCLUDE_LABEL", this.exclude);
        I18nViewUtil.addComponentForKey(ManagerBruteForce.BRUTEFORCE_EXCLUDE_TOOLTIP, tooltip.get());
        thirdLine.add(this.exclude);

        Arrays.asList(
            new ModelSpinner(1, this.minimumLength, "BRUTEFORCE_MIN_LABEL", "BRUTEFORCE_MIN_TOOLTIP"),
            new ModelSpinner(5, this.maximumLength, "BRUTEFORCE_MAX_LABEL", "BRUTEFORCE_MAX_TOOLTIP")
        ).forEach(model -> SwingUtilities.invokeLater(() -> {  // #96099: JSpinner not fully initialized
            final var tooltipMax = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(model.i18n())));
            var spinner = new JSpinner() {
                @Override
                public JToolTip createToolTip() {
                    return tooltipMax.get();
                }
            };
            spinner.setModel(new SpinnerNumberModel(model.value(), 1, 10000, 1));
            spinner.addMouseWheelListener(new SpinnerMouseWheelListener());
            spinner.setToolTipText(I18nUtil.valueByKey(model.i18n()));
            I18nViewUtil.addComponentForKey(model.i18n(), tooltipMax.get());
            spinner.setPreferredSize(new Dimension(
                (int) (spinner.getPreferredSize().width/1.8),
                spinner.getPreferredSize().height
            ));

            var label = new JLabel(StringUtils.SPACE + I18nUtil.valueByKey(model.label()), SwingConstants.RIGHT);
            label.setMaximumSize(new Dimension(label.getPreferredSize().width, label.getPreferredSize().height));
            thirdLine.add(Box.createHorizontalStrut(5));
            thirdLine.add(label);
            I18nViewUtil.addComponentForKey(model.label(), label);

            thirdLine.add(spinner);
            model.spinner().set(spinner);
        }));

        return thirdLine;
    }

    
    // Getter and setter

    public JButtonStateful getRun() {
        return this.run;
    }

    public JTextField getHash() {
        return this.hash;
    }

    public JComboBox<String> getHashTypes() {
        return this.hashTypes;
    }

    public JCheckBox getLowerCaseCharacters() {
        return this.lowerCaseCharacters.get();
    }

    public JCheckBox getUpperCaseCharacters() {
        return this.upperCaseCharacters.get();
    }

    public JCheckBox getNumericCharacters() {
        return this.numericCharacters.get();
    }

    public JCheckBox getSpecialCharacters() {
        return this.specialCharacters.get();
    }

    public JTextField getExclude() {
        return this.exclude;
    }

    public JSpinner getMinimumLength() {
        return this.minimumLength.get();
    }

    public JSpinner getMaximumLength() {
        return this.maximumLength.get();
    }

    public JTextPane getResult() {
        return this.result;
    }
}

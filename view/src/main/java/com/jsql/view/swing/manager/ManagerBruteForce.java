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
import com.jsql.view.swing.manager.util.ActionBruteForce;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.panel.preferences.listener.SpinnerMouseWheelListener;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.util.I18nViewUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Manager to brute force a hash of various types.
 */
public class ManagerBruteForce extends JPanel {
    
    /**
     * Button running the attack.
     */
    private JButtonStateful run;
    
    /**
     * Input for hash to brute force.
     */
    private JTextField hash;
    
    /**
     * Combobox of hashing methods.
     */
    private JComboBox<String> hashTypes;
    
    /**
     * Enable injection of lowercase characters.
     */
    private JCheckBox lowerCaseCharacters;
    
    /**
     * Enable injection of uppercase characters.
     */
    private JCheckBox upperCaseCharacters;
    
    /**
     * Enable injection of numeric characters.
     */
    private JCheckBox numericCharacters;
    
    /**
     * Enable injection of special characters.
     */
    private JCheckBox specialCharacters;
    
    /**
     * List of characters to exclude from the attack.
     */
    private JTextField exclude;
    
    /**
     * Minimum length of string to attack.
     */
    private JSpinner minimumLength;
    
    /**
     * Maximum length of string to attack.
     */
    private JSpinner maximumLength;
    
    /**
     * Textarea displaying result.
     */
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

        JPanel panelOptions = this.initializeOptionsPanel();
        this.add(panelOptions, BorderLayout.NORTH);

        this.result = new JPopupTextPane("Result of brute force processing").getProxy();
        this.result.setName("managerBruterResult");
        this.add(new JScrollPane(this.result), BorderLayout.CENTER);

        JPanel panelButton = this.initializePanelButton();
        this.add(panelButton, BorderLayout.SOUTH);
    }

    private JPanel initializePanelButton() {
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));

        this.run = new JButtonStateful("BRUTEFORCE_RUN_BUTTON_LABEL");
        I18nViewUtil.addComponentForKey("BRUTEFORCE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_RUN_BUTTON_TOOLTIP"));
        this.run.setName("managerBruterRun");
        this.run.addActionListener(new ActionBruteForce(this));

        this.progressBar = new JProgressBar();
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisible(false);

        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.horizontalGlue);
        lastLine.add(this.progressBar);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        return lastLine;
    }

    public void showLoader(boolean isVisible) {
        this.progressBar.setVisible(isVisible);
        this.horizontalGlue.setVisible(!isVisible);
    }

    private JPanel initializeOptionsPanel() {
        var options = new JPanel(new BorderLayout());
        JPanel firstLine = this.initializeFirstLine();
        final JPanel secondLine = this.initializeSecondLine();
        JPanel thirdLine = this.initializeThirdLine();
        
        final var secondAndThirdLine = new JPanel(new BorderLayout());
        secondAndThirdLine.add(secondLine, BorderLayout.NORTH);
        secondAndThirdLine.add(thirdLine, BorderLayout.SOUTH);

        options.add(firstLine, BorderLayout.NORTH);
        options.add(secondAndThirdLine, BorderLayout.SOUTH);
        return options;
    }

    private JPanel initializeFirstLine() {
        this.hash = new JPopupTextField(I18nUtil.valueByKey("BRUTEFORCE_HASH_LABEL")).getProxy();
        this.hash.setName("managerBruterHash");
        this.hash.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_HASH_TOOLTIP"));

        var firstLine = new JPanel(new BorderLayout());
        firstLine.add(this.hash, BorderLayout.CENTER);
        return firstLine;
    }

    private JPanel initializeSecondLine() {
        final var secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));

        this.lowerCaseCharacters = new JCheckBox("a-z", true);
        this.upperCaseCharacters = new JCheckBox("A-Z", true);
        this.numericCharacters = new JCheckBox("0-9", true);
        this.specialCharacters = new JCheckBox("Special", true);

        this.hashTypes = new JComboBox<>(ManagerCoder.HASHES);
        this.hashTypes.setSelectedIndex(6);
        this.hashTypes.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_HASH_TYPE_TOOLTIP"));

        secondLine.add(this.hashTypes);
        secondLine.add(Box.createHorizontalStrut(5));
        secondLine.add(this.lowerCaseCharacters);
        secondLine.add(Box.createHorizontalStrut(5));
        secondLine.add(this.upperCaseCharacters);
        secondLine.add(Box.createHorizontalStrut(5));
        secondLine.add(this.numericCharacters);
        secondLine.add(Box.createHorizontalStrut(5));
        secondLine.add(this.specialCharacters);

        this.lowerCaseCharacters.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_LCASE_TOOLTIP"));
        this.upperCaseCharacters.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_UCASE_TOOLTIP"));
        this.numericCharacters.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_NUM_TOOLTIP"));
        this.specialCharacters.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_SPEC_TOOLTIP"));
        
        return secondLine;
    }

    private JPanel initializeThirdLine() {
        var thirdLine = new JPanel();
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.X_AXIS));

        this.exclude = new JPopupTextField(I18nUtil.valueByKey("BRUTEFORCE_EXCLUDE_LABEL")).getProxy();
        this.exclude.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_EXCLUDE_TOOLTIP"));
        thirdLine.add(this.exclude);

        this.minimumLength = new JSpinner();
        this.minimumLength.setModel(new SpinnerNumberModel(1, 1, 10000, 1));
        this.minimumLength.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.minimumLength.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_MIN_TOOLTIP"));

        this.maximumLength = new JSpinner();
        this.maximumLength.setModel(new SpinnerNumberModel(5, 1, 10000, 1));
        this.maximumLength.addMouseWheelListener(new SpinnerMouseWheelListener());
        this.maximumLength.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_MAX_TOOLTIP"));

        var dimension = new Dimension(52, (int) this.minimumLength.getPreferredSize().getHeight());
        this.minimumLength.setPreferredSize(dimension);
        this.minimumLength.setMaximumSize(dimension);
        this.minimumLength.setMinimumSize(dimension);
        this.maximumLength.setPreferredSize(dimension);
        this.maximumLength.setMaximumSize(dimension);
        this.maximumLength.setMinimumSize(dimension);

        var labelMin = new JLabel(StringUtils.SPACE + I18nUtil.valueByKey("BRUTEFORCE_MIN_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(Box.createHorizontalStrut(5));
        thirdLine.add(labelMin);
        I18nViewUtil.addComponentForKey("BRUTEFORCE_MIN_LABEL", labelMin);
        thirdLine.add(this.minimumLength);

        var labelMax = new JLabel(StringUtils.SPACE + I18nUtil.valueByKey("BRUTEFORCE_MAX_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(Box.createHorizontalStrut(5));
        thirdLine.add(labelMax);
        I18nViewUtil.addComponentForKey("BRUTEFORCE_MAX_LABEL", labelMax);
        thirdLine.add(this.maximumLength);
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
        return this.lowerCaseCharacters;
    }

    public JCheckBox getUpperCaseCharacters() {
        return this.upperCaseCharacters;
    }

    public JCheckBox getNumericCharacters() {
        return this.numericCharacters;
    }

    public JCheckBox getSpecialCharacters() {
        return this.specialCharacters;
    }

    public JTextField getExclude() {
        return this.exclude;
    }

    public JSpinner getMinimumLength() {
        return this.minimumLength;
    }

    public JSpinner getMaximumLength() {
        return this.maximumLength;
    }

    public JTextPane getResult() {
        return this.result;
    }
}

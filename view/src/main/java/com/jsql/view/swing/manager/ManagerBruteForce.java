/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.manager.util.ActionBruteForce;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.UiUtil;

/**
 * Manager to brute force a hash of various types.
 */
@SuppressWarnings("serial")
public class ManagerBruteForce extends JPanel implements Manager {
    
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
    private JTextPane result;
    
    /**
     * Animated GIF displayed during attack.
     */
    private JLabel loader;

    /**
     * Create a panel to run brute force attack.
     */
    public ManagerBruteForce() {
        
        super(new BorderLayout());

        JPanel panelOptions = this.initializeOptionsPanel();
        this.add(panelOptions, BorderLayout.NORTH);

        this.result = new JPopupTextPane("Result of brute force processing").getProxy();
        this.add(new LightScrollPane(1, 0, 0, 0, this.result), BorderLayout.CENTER);
        
        this.result.setName("managerBruterResult");
        
        JPanel panelButton = this.initializePanelButton();
        this.add(panelButton, BorderLayout.SOUTH);
    }

    private JPanel initializePanelButton() {
        
        var lastLine = new JPanel();
        
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setPreferredSize(new Dimension(0, 26));

        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        this.run = new JButtonStateful("BRUTEFORCE_RUN_BUTTON_LABEL");
        I18nViewUtil.addComponentForKey("BRUTEFORCE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setName("managerBruterRun");
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(new ActionBruteForce(this));

        this.loader = new JLabel(UiUtil.ICON_LOADER_GIF);
        this.loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        
        return lastLine;
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
        
        var firstLine = new JPanel(new BorderLayout());

        this.hash = new JPopupTextField(I18nUtil.valueByKey("BRUTEFORCE_HASH_LABEL")).getProxy();
        
        this.hash.setName("managerBruterHash");
        
        this.hash.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_HASH_TOOLTIP"));
        
        firstLine.add(this.hash, BorderLayout.CENTER);
        
        this.hash.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, UiUtil.COLOR_DEFAULT_BACKGROUND),
                UiUtil.BORDER_BLU
            )
        );
        
        return firstLine;
    }

    private JPanel initializeSecondLine() {
        
        final var secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));
        secondLine.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UiUtil.COLOR_DEFAULT_BACKGROUND));
        
        this.lowerCaseCharacters = new JCheckBox("a-z", true);
        this.upperCaseCharacters = new JCheckBox("A-Z", true);
        this.numericCharacters = new JCheckBox("0-9", true);
        this.specialCharacters = new JCheckBox("Special", true);

        this.hashTypes = new JComboBox<>(
            new String[]{
                "Adler32", "Crc16", "Crc32", "Crc64", "Md2", "Md4",
                "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "mysql"
            }
        );
        
        this.hashTypes.setSelectedIndex(6);
        this.hashTypes.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_HASH_TYPE_TOOLTIP"));

        secondLine.add(this.hashTypes);
        secondLine.add(this.lowerCaseCharacters);
        secondLine.add(this.upperCaseCharacters);
        secondLine.add(this.numericCharacters);
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
        thirdLine.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UiUtil.COLOR_DEFAULT_BACKGROUND));
        
        this.exclude = new JPopupTextField(I18nUtil.valueByKey("BRUTEFORCE_EXCLUDE_LABEL")).getProxy();
        this.exclude.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_EXCLUDE_TOOLTIP"));
        this.exclude.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, UiUtil.COLOR_DEFAULT_BACKGROUND),
                UiUtil.BORDER_BLU
            )
        );
        thirdLine.add(this.exclude);

        this.minimumLength = new JSpinner();
        this.minimumLength.setModel(new SpinnerNumberModel(1, 1, 10000, 1));
        
        this.maximumLength = new JSpinner();
        this.maximumLength.setModel(new SpinnerNumberModel(5, 1, 10000, 1));
        
        this.minimumLength.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_MIN_TOOLTIP"));
        this.maximumLength.setToolTipText(I18nUtil.valueByKey("BRUTEFORCE_MAX_TOOLTIP"));
        
        this.minimumLength.setPreferredSize(new Dimension(38, (int) this.minimumLength.getPreferredSize().getHeight()));
        this.maximumLength.setPreferredSize(new Dimension(38, (int) this.maximumLength.getPreferredSize().getHeight()));
        this.minimumLength.setMaximumSize(new Dimension(38, (int) this.minimumLength.getPreferredSize().getHeight()));
        this.maximumLength.setMaximumSize(new Dimension(38, (int) this.maximumLength.getPreferredSize().getHeight()));
        this.minimumLength.setMinimumSize(new Dimension(38, (int) this.minimumLength.getPreferredSize().getHeight()));
        this.maximumLength.setMinimumSize(new Dimension(38, (int) this.maximumLength.getPreferredSize().getHeight()));

        var labelMin = new JLabel(StringUtils.SPACE + I18nUtil.valueByKey("BRUTEFORCE_MIN_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(labelMin);
        I18nViewUtil.addComponentForKey("BRUTEFORCE_MIN_LABEL", labelMin);
        thirdLine.add(this.minimumLength);
        var labelMax = new JLabel(StringUtils.SPACE + I18nUtil.valueByKey("BRUTEFORCE_MAX_LABEL"), SwingConstants.RIGHT);
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

    public JLabel getLoader() {
        return this.loader;
    }
}

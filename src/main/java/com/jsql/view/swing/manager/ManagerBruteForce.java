/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import com.jsql.i18n.I18n;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.manager.util.ActionBruteForce;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

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

        JPanel options = new JPanel(new BorderLayout());

        JPanel firstLine = new JPanel(new BorderLayout());

        this.hash = new JPopupTextField(I18n.valueByKey("BRUTEFORCE_HASH_LABEL")).getProxy();
        this.hash.setToolTipText(I18n.valueByKey("BRUTEFORCE_HASH_TOOLTIP"));
        firstLine.add(this.hash, BorderLayout.CENTER);
        this.hash.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, HelperUi.COLOR_DEFAULT_BACKGROUND),
                HelperUi.BORDER_BLU
            )
        );

        final JPanel secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));

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
        this.hashTypes.setToolTipText(I18n.valueByKey("BRUTEFORCE_HASH_TYPE_TOOLTIP"));

        secondLine.add(this.hashTypes);

        secondLine.add(this.lowerCaseCharacters);
        secondLine.add(this.upperCaseCharacters);
        secondLine.add(this.numericCharacters);
        secondLine.add(this.specialCharacters);

        this.lowerCaseCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_LCASE_TOOLTIP"));
        this.upperCaseCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_UCASE_TOOLTIP"));
        this.numericCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_NUM_TOOLTIP"));
        this.specialCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_SPEC_TOOLTIP"));

        JPanel thirdLine = new JPanel();
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.X_AXIS));
        
        this.exclude = new JPopupTextField(I18n.valueByKey("BRUTEFORCE_EXCLUDE_LABEL")).getProxy();
        this.exclude.setToolTipText(I18n.valueByKey("BRUTEFORCE_EXCLUDE_TOOLTIP"));
        this.exclude.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, HelperUi.COLOR_DEFAULT_BACKGROUND),
                HelperUi.BORDER_BLU
            )
        );
        thirdLine.add(this.exclude);

        this.minimumLength = new JSpinner(
            new SpinnerNumberModel(1, 1, 10000, 1)
        );
        this.maximumLength = new JSpinner(
            new SpinnerNumberModel(5, 1, 10000, 1)
        );
        
        this.minimumLength.setToolTipText(I18n.valueByKey("BRUTEFORCE_MIN_TOOLTIP"));
        this.maximumLength.setToolTipText(I18n.valueByKey("BRUTEFORCE_MAX_TOOLTIP"));
        
        this.minimumLength.setPreferredSize(new Dimension(38, (int) this.minimumLength.getPreferredSize().getHeight()));
        this.maximumLength.setPreferredSize(new Dimension(38, (int) this.maximumLength.getPreferredSize().getHeight()));
        this.minimumLength.setMaximumSize(new Dimension(38, (int) this.minimumLength.getPreferredSize().getHeight()));
        this.maximumLength.setMaximumSize(new Dimension(38, (int) this.maximumLength.getPreferredSize().getHeight()));
        this.minimumLength.setMinimumSize(new Dimension(38, (int) this.minimumLength.getPreferredSize().getHeight()));
        this.maximumLength.setMinimumSize(new Dimension(38, (int) this.maximumLength.getPreferredSize().getHeight()));

        JLabel labelMin = new JLabel(" "+I18n.valueByKey("BRUTEFORCE_MIN_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(labelMin);
        I18nView.addComponentForKey("BRUTEFORCE_MIN_LABEL", labelMin);
        thirdLine.add(this.minimumLength);
        JLabel labelMax = new JLabel(" "+I18n.valueByKey("BRUTEFORCE_MAX_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(labelMax);
        I18nView.addComponentForKey("BRUTEFORCE_MAX_LABEL", labelMax);
        thirdLine.add(this.maximumLength);
        
        final JPanel secondAndThirdLine = new JPanel(new BorderLayout());
        secondAndThirdLine.add(secondLine, BorderLayout.NORTH);
        secondAndThirdLine.add(thirdLine, BorderLayout.SOUTH);

        options.add(firstLine, BorderLayout.NORTH);
        options.add(secondAndThirdLine, BorderLayout.SOUTH);
        this.add(options, BorderLayout.NORTH);

        this.result = new JPopupTextPane("Result of brute force processing").getProxy();

        this.add(new LightScrollPane(1, 1, 0, 0, this.result), BorderLayout.CENTER);
        
        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        this.run = new JButtonStateful("BRUTEFORCE_RUN_BUTTON_LABEL");
        I18nView.addComponentForKey("BRUTEFORCE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18n.valueByKey("BRUTEFORCE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));

        this.loader = new JLabel(HelperUi.ICON_LOADER_GIF);
        this.loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);

        this.run.addActionListener(new ActionBruteForce(this));

        this.add(lastLine, BorderLayout.SOUTH);
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

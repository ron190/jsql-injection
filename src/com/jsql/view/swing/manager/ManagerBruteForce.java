/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * Manager to brute force a hash of various types.
 */
@SuppressWarnings("serial")
public class ManagerBruteForce extends JPanel {
    /**
     * Button running the attack.
     */
    public AbstractButton run;
    
    /**
     * Input for hash to brute force.
     */
    JTextField hash;
    
    /**
     * Combobox of hashing methods.
     */
    JComboBox<String> hashTypes;
    
    /**
     * Enable injection of lowercase characters.
     */
    JCheckBox lowerCaseCharacters;
    
    /**
     * Enable injection of uppercase characters.
     */
    JCheckBox upperCaseCharacters;
    
    /**
     * Enable injection of numeric characters.
     */
    JCheckBox numericCharacters;
    
    /**
     * Enable injection of special characters. 
     */
    JCheckBox specialCharacters;
    
    /**
     * List of characters to exclude from the attack.
     */
    JTextField exclude;
    
    /**
     * Minimum length of string to attack.
     */
    JTextField minimumLength;
    
    /**
     * Maximum length of string to attack.
     */
    JTextField maximumLength;
    
    /**
     * Textarea displaying result.
     */
    JTextArea result;
    
    /**
     * Animated GIF displayed during attack. 
     */
    JLabel loader;

    /**
     * Create a panel to run brute force attack. 
     */
    public ManagerBruteForce() {
        super(new BorderLayout());

        JPanel options = new JPanel(new BorderLayout());

        JPanel firstLine = new JPanel(new BorderLayout());

        hash = new JPopupTextField(I18n.BRUTEFORCE_HASH).getProxy();
        hash.setToolTipText(I18n.BRUTEFORCE_HASH_TOOLTIP);
        firstLine.add(hash, BorderLayout.CENTER);
        hash.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, HelperGUI.DEFAULT_BACKGROUND),
                HelperGUI.BLU_ROUND_BORDER));

        final JPanel secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));

        lowerCaseCharacters = new JCheckBox("a-z", true);
        upperCaseCharacters = new JCheckBox("A-Z", true);
        numericCharacters = new JCheckBox("0-9", true);
        specialCharacters = new JCheckBox("Special", true);

        hashTypes = new JComboBox<String>(new String[]{"md2", "md5", "sha-1", "sha-256", "sha-384",
                "sha-512", "mysql" /*,"crc16","crc32","crc64","adler32"*/});
        
        hashTypes.setSelectedIndex(1);
        hashTypes.setToolTipText(I18n.BRUTEFORCE_HASH_TYPE_TOOLTIP);

        secondLine.add(hashTypes);

        secondLine.add(lowerCaseCharacters);
        secondLine.add(upperCaseCharacters);
        secondLine.add(numericCharacters);
        secondLine.add(specialCharacters);

        lowerCaseCharacters.setToolTipText(I18n.BRUTEFORCE_LCASE_TOOLTIP);
        upperCaseCharacters.setToolTipText(I18n.BRUTEFORCE_UCASE_TOOLTIP);
        numericCharacters.setToolTipText(I18n.BRUTEFORCE_NUM_TOOLTIP);
        specialCharacters.setToolTipText(I18n.BRUTEFORCE_SPEC_TOOLTIP);

        JPanel thirdLine = new JPanel();
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.X_AXIS));
        
        exclude = new JPopupTextField(I18n.BRUTEFORCE_EXCLUDE_LABEL).getProxy();
        exclude.setToolTipText(I18n.BRUTEFORCE_EXCLUDE_TOOLTIP);
        thirdLine.add(exclude);

        minimumLength = new JPopupTextField("min", "1").getProxy();
        maximumLength = new JPopupTextField("max", "5").getProxy();
        
        minimumLength.setToolTipText(I18n.BRUTEFORCE_MIN_TOOLTIP);
        maximumLength.setToolTipText(I18n.BRUTEFORCE_MAX_TOOLTIP);
        
        minimumLength.setHorizontalAlignment(JTextField.RIGHT);
        maximumLength.setHorizontalAlignment(JTextField.RIGHT);
//
        minimumLength.setPreferredSize(new Dimension(30, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setPreferredSize(new Dimension(30, (int) maximumLength.getPreferredSize().getHeight()));
        minimumLength.setMaximumSize(new Dimension(30, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setMaximumSize(new Dimension(30, (int) maximumLength.getPreferredSize().getHeight()));
        minimumLength.setMinimumSize(new Dimension(30, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setMinimumSize(new Dimension(30, (int) maximumLength.getPreferredSize().getHeight()));

        thirdLine.add(new JLabel(I18n.BRUTEFORCE_MIN_LABEL, SwingConstants.RIGHT));
        thirdLine.add(minimumLength);
        thirdLine.add(new JLabel(I18n.BRUTEFORCE_MAX_LABEL, SwingConstants.RIGHT));
        thirdLine.add(maximumLength);
        
        final JPanel secondAndThirdLine = new JPanel(new BorderLayout());
        secondAndThirdLine.add(secondLine, BorderLayout.NORTH);
        secondAndThirdLine.add(thirdLine, BorderLayout.SOUTH);

        options.add(firstLine, BorderLayout.NORTH);
        options.add(secondAndThirdLine, BorderLayout.SOUTH);
        this.add(options, BorderLayout.NORTH);

        result = new JPopupTextArea().getProxy();
        result.setLineWrap(true);
        this.add(new JScrollPanePixelBorder(1, 1, 0, 0, result), BorderLayout.CENTER);
        
        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperGUI.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)));
        
        run = new JButton(I18n.BRUTEFORCE_RUN_BUTTON, new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/key.png")));
        run.setToolTipText(I18n.BRUTEFORCE_RUN_BUTTON_TOOLTIP);
        run.setBorder(HelperGUI.BLU_ROUND_BORDER);

        loader = new JLabel(HelperGUI.LOADER_GIF);
        loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(run);

        run.addActionListener(new ActionBruteForce(this));

        this.add(lastLine, BorderLayout.SOUTH);
    }
}

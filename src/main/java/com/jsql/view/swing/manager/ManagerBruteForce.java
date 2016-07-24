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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.manager.util.ActionBruteForce;
import com.jsql.view.swing.scrollpane.LightScrollPane;
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
    public JTextField hash;
    
    /**
     * Combobox of hashing methods.
     */
    public JComboBox<String> hashTypes;
    
    /**
     * Enable injection of lowercase characters.
     */
    public JCheckBox lowerCaseCharacters;
    
    /**
     * Enable injection of uppercase characters.
     */
    public JCheckBox upperCaseCharacters;
    
    /**
     * Enable injection of numeric characters.
     */
    public JCheckBox numericCharacters;
    
    /**
     * Enable injection of special characters. 
     */
    public JCheckBox specialCharacters;
    
    /**
     * List of characters to exclude from the attack.
     */
    public JTextField exclude;
    
    /**
     * Minimum length of string to attack.
     */
    public JSpinner minimumLength;
    
    /**
     * Maximum length of string to attack.
     */
    public JSpinner maximumLength;
    
    /**
     * Textarea displaying result.
     */
    public JTextArea result;
    
    /**
     * Animated GIF displayed during attack. 
     */
    public JLabel loader;

    /**
     * Create a panel to run brute force attack. 
     */
    public ManagerBruteForce() {
        super(new BorderLayout());

        JPanel options = new JPanel(new BorderLayout());

        JPanel firstLine = new JPanel(new BorderLayout());

        hash = new JPopupTextField(I18n.valueByKey("BRUTEFORCE_HASH")).getProxy();
        hash.setToolTipText(I18n.valueByKey("BRUTEFORCE_HASH_TOOLTIP"));
        firstLine.add(hash, BorderLayout.CENTER);
        hash.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, HelperUi.DEFAULT_BACKGROUND),
                HelperUi.BLU_BORDER
            )
        );

        final JPanel secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));

        lowerCaseCharacters = new JCheckBox("a-z", true);
        upperCaseCharacters = new JCheckBox("A-Z", true);
        numericCharacters = new JCheckBox("0-9", true);
        specialCharacters = new JCheckBox("Special", true);

        hashTypes = new JComboBox<>(
            new String[]{
                "Adler32", "Crc16", "Crc32", "Crc64", "Md2", "Md4", 
                "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "mysql"
            }
        );
        
        hashTypes.setSelectedIndex(6);
        hashTypes.setToolTipText(I18n.valueByKey("BRUTEFORCE_HASH_TYPE_TOOLTIP"));

        secondLine.add(hashTypes);

        secondLine.add(lowerCaseCharacters);
        secondLine.add(upperCaseCharacters);
        secondLine.add(numericCharacters);
        secondLine.add(specialCharacters);

        lowerCaseCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_LCASE_TOOLTIP"));
        upperCaseCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_UCASE_TOOLTIP"));
        numericCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_NUM_TOOLTIP"));
        specialCharacters.setToolTipText(I18n.valueByKey("BRUTEFORCE_SPEC_TOOLTIP"));

        JPanel thirdLine = new JPanel();
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.X_AXIS));
        
        exclude = new JPopupTextField(I18n.valueByKey("BRUTEFORCE_EXCLUDE_LABEL")).getProxy();
        exclude.setToolTipText(I18n.valueByKey("BRUTEFORCE_EXCLUDE_TOOLTIP"));
        exclude.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, HelperUi.DEFAULT_BACKGROUND),
                HelperUi.BLU_BORDER
            )
        );
        thirdLine.add(exclude);

        minimumLength = new JSpinner(
            new SpinnerNumberModel(
                new Integer(1),
                new Integer(1),
                new Integer(10000),
                new Integer(1)
            )
        );
        maximumLength = new JSpinner(
            new SpinnerNumberModel(
                new Integer(5),
                new Integer(1),
                new Integer(10000),
                new Integer(1)
            )
        );
        
        minimumLength.setToolTipText(I18n.valueByKey("BRUTEFORCE_MIN_TOOLTIP"));
        maximumLength.setToolTipText(I18n.valueByKey("BRUTEFORCE_MAX_TOOLTIP"));
        
        minimumLength.setPreferredSize(new Dimension(38, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setPreferredSize(new Dimension(38, (int) maximumLength.getPreferredSize().getHeight()));
        minimumLength.setMaximumSize(new Dimension(38, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setMaximumSize(new Dimension(38, (int) maximumLength.getPreferredSize().getHeight()));
        minimumLength.setMinimumSize(new Dimension(38, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setMinimumSize(new Dimension(38, (int) maximumLength.getPreferredSize().getHeight()));

        JLabel labelMin = new JLabel(" "+I18n.valueByKey("BRUTEFORCE_MIN_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(labelMin);
        I18n.addComponentForKey("BRUTEFORCE_MIN_LABEL", labelMin);
        thirdLine.add(minimumLength);
        JLabel labelMax = new JLabel(" "+I18n.valueByKey("BRUTEFORCE_MAX_LABEL"), SwingConstants.RIGHT);
        thirdLine.add(labelMax);
        I18n.addComponentForKey("BRUTEFORCE_MAX_LABEL", labelMax);
        thirdLine.add(maximumLength);
        
        final JPanel secondAndThirdLine = new JPanel(new BorderLayout());
        secondAndThirdLine.add(secondLine, BorderLayout.NORTH);
        secondAndThirdLine.add(thirdLine, BorderLayout.SOUTH);

        options.add(firstLine, BorderLayout.NORTH);
        options.add(secondAndThirdLine, BorderLayout.SOUTH);
        this.add(options, BorderLayout.NORTH);

        result = new JPopupTextArea().getProxy();
        result.setLineWrap(true);
        this.add(new LightScrollPane(1, 1, 0, 0, result), BorderLayout.CENTER);
        
        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        run = new JButton(
            I18n.valueByKey("BRUTEFORCE_RUN_BUTTON"), 
            new ImageIcon(ManagerBruteForce.class.getResource("/com/jsql/view/swing/resources/images/icons/key.png"))
        );
        I18n.addComponentForKey("BRUTEFORCE_RUN_BUTTON", run);
        run.setToolTipText(I18n.valueByKey("BRUTEFORCE_RUN_BUTTON_TOOLTIP"));
        
        run.setContentAreaFilled(false);
        run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        run.setBackground(new Color(200, 221, 242));
        
        run.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (run.isEnabled()) {
                    run.setContentAreaFilled(true);
                    run.setBorder(HelperUi.BLU_ROUND_BORDER);
                }
                
            }

            @Override public void mouseExited(MouseEvent e) {
                if (run.isEnabled()) {
                    run.setContentAreaFilled(false);
                    run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                }
            }
        });

        loader = new JLabel(HelperUi.LOADER_GIF);
        loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(run);

        run.addActionListener(new ActionBruteForce(this));

        this.add(lastLine, BorderLayout.SOUTH);
    }
}

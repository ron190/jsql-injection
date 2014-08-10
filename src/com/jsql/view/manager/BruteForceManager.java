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
package com.jsql.view.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import com.jsql.model.InjectionModel;
import com.jsql.view.GUITools;
import com.jsql.view.bruteforce.HashBruter;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.textcomponent.JPopupTextArea;
import com.jsql.view.textcomponent.JPopupTextField;

/**
 * Manager to brute force a hash of various types.
 */
@SuppressWarnings("serial")
public class BruteForceManager extends JPanel {
    /**
     * Button running the attack.
     */
    public AbstractButton run;
    
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
    private JTextField minimumLength;
    
    /**
     * Maximum length of string to attack.
     */
    private JTextField maximumLength;
    
    /**
     * Textarea displaying result.
     */
    private JTextArea result;
    
    /**
     * Animated GIF displayed during attack. 
     */
    private JLabel loader;

    /**
     * Create a panel to run brute force attack. 
     */
    public BruteForceManager() {
        super(new BorderLayout());

        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER));

        JPanel options = new JPanel(new BorderLayout());

        JPanel firstLine = new JPanel(new BorderLayout());
        firstLine.add(new JLabel(" Hash"), BorderLayout.WEST);

        hash = new JPopupTextField("Hash to find").getProxy();
        hash.setToolTipText("<html><b>Hash to brute force</b><br>"
                + "<i>Password for admin pages and for database users are<br>"
                + "usually hashed inside database.</i></html>");
        firstLine.add(hash, BorderLayout.CENTER);
        hash.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 2, 2, 0, GUITools.DEFAULT_BACKGROUND),
                GUITools.BLU_ROUND_BORDER));

        final JPanel secondLine = new JPanel();
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.X_AXIS));

        lowerCaseCharacters = new JCheckBox("a-z", true);
        upperCaseCharacters = new JCheckBox("A-Z", true);
        numericCharacters = new JCheckBox("0-9", true);
        specialCharacters = new JCheckBox("Special", true);

        secondLine.add(new JLabel(" Type ", SwingConstants.RIGHT));

        hashTypes = new JComboBox<String>(new String[]{"md2", "md5", "sha-1", "sha-256", "sha-384",
                "sha-512", "mysql" /*,"crc16","crc32","crc64","adler32"*/});
        
        hashTypes.setSelectedIndex(1);
        hashTypes.setMaximumSize(new Dimension((int) hashTypes.getPreferredSize().getWidth(), 22));
        hashTypes.setToolTipText("<html><b>Type of hash</b><br>"
                + "<i>MD5 is commonly used to hash password of admin pages. MySQL passwords are<br>"
                + "hashed differently (cf. Type mysql ; these are found into database 'mysql', table 'user').</i></html>");

        secondLine.add(hashTypes);

        secondLine.add(lowerCaseCharacters);
        secondLine.add(upperCaseCharacters);
        secondLine.add(numericCharacters);
        secondLine.add(specialCharacters);
        secondLine.add(Box.createGlue());

        lowerCaseCharacters.setToolTipText("<html><b>Lower case characters</b><br>"
                + "Check if searched string contains any of following characters:<br>"
                + "<span style=\"font-family:'Courier New';\">abcdefghijklmnopqrstuvwxyz</span></html>");
        upperCaseCharacters.setToolTipText("<html><b>Upper case characters</b><br>"
                + "Check if searched string contains any of following characters:<br>"
                + "<span style=\"font-family:'Courier New';\">ABCDEFGHIJKLMNOPQRSTUVWXYZ</span></html>");
        numericCharacters.setToolTipText("<html><b>Numeric characters</b><br>"
                + "Check if searched string contains any of following characters:<br>"
                + "<span style=\"font-family:'Courier New';\">0123456789</span></html>");
        specialCharacters.setToolTipText("<html><b>Special characters</b><br>"
                + "Check if searched string contains any of following characters:<br>"
                + "<span style=\"font-family:'Courier New';\">&nbsp;~`!@#$%^&*()_-+={}[]|\\;:'\"<.,>/?</span></html>");

        JPanel thirdLine = new JPanel();
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.X_AXIS));
        
        thirdLine.add(new JLabel(" Exclude ", SwingConstants.RIGHT));
        exclude = new JPopupTextField("Character(s) to exclude").getProxy();
        exclude.setToolTipText("<html><b>Exclude characters</b><br>"
                + "Speed up process by excluding characters from the search.</html>");
        thirdLine.add(exclude);

        minimumLength = new JPopupTextField("min", "1").getProxy();
        maximumLength = new JPopupTextField("max", "5").getProxy();
        
        thirdLine.add(new JLabel(" Length min. ", SwingConstants.RIGHT));
        thirdLine.add(minimumLength);
        thirdLine.add(new JLabel("max. ", SwingConstants.RIGHT));
        thirdLine.add(maximumLength);

        minimumLength.setToolTipText("<html><b>Minimum length of searched string</b><br>" 
                + "Speed up process by specifying the minimum length to search.</html>");
        maximumLength.setToolTipText("<html><b>Maximum length of searched string</b><br>" 
                + "Speed up process by specifying the maximum length to search.</html>");
        
        minimumLength.setHorizontalAlignment(JTextField.RIGHT);
        maximumLength.setHorizontalAlignment(JTextField.RIGHT);

        exclude.setMaximumSize(new Dimension(90, (int) exclude.getPreferredSize().getHeight()));
        exclude.setMinimumSize(new Dimension(90, (int) exclude.getPreferredSize().getHeight()));

        minimumLength.setMaximumSize(new Dimension(30, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setMaximumSize(new Dimension(30, (int) maximumLength.getPreferredSize().getHeight()));
        minimumLength.setMinimumSize(new Dimension(30, (int) minimumLength.getPreferredSize().getHeight()));
        maximumLength.setMinimumSize(new Dimension(30, (int) maximumLength.getPreferredSize().getHeight()));

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
                BorderFactory.createMatteBorder(0, 1, 0, 0, GUITools.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)));
        
        run = new JButton("Start", new ImageIcon(getClass().getResource("/com/jsql/view/images/key.png")));
        run.setToolTipText("<html><b>Begin brute forcing the hash</b><br>"
                + "<i>Such process calculates a hash for every possible combinations of characters, hoping<br>"
                + "a hash will be equal to the user's one. It always either fails or never ends. Use instead<br>"
                + "websites like md5decrypter.co.uk to search for precalculated pairs of hash and password,<br>"
                + "also you may try other brute force softwares like John the Ripper.</i></html>");
        run.setBorder(GUITools.BLU_ROUND_BORDER);

        loader = new JLabel(GUITools.LOADER_GIF);
        loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(run);

        run.addActionListener(new BruteForceAction());

        this.add(lastLine, BorderLayout.SOUTH);
    }
    
    /**
     * Run a brute force attack.
     */
    private class BruteForceAction implements ActionListener {
        private final Boolean[] doStop = {false};

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (run.getText().equals("Stop")) {
                run.setEnabled(false);
                doStop[0] = true;
            } else {
                try {
                    Integer.parseInt(maximumLength.getText());
                    Integer.parseInt(minimumLength.getText());
                } catch (NumberFormatException e) {
                    result.setText("*** Incorrect length");
                    return;
                }

                if (hash.getText().equals("")) {
                    result.setText("*** Empty hash");
                    return;
                } else if (
                        !specialCharacters.isSelected()
                        && !upperCaseCharacters.isSelected()
                        && !lowerCaseCharacters.isSelected()
                        && !numericCharacters.isSelected()) {
                    result.setText("*** Select a character range");
                    return;
                } else if (Integer.parseInt(maximumLength.getText()) < Integer.parseInt(minimumLength.getText())) {
                    result.setText("*** Incorrect minimum and maximum length");
                    return;
                }

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // Reset the panel
                        run.setText("Stop");
                        loader.setVisible(true);
                        result.setText(null);

                        // Configure the hasher
                        final HashBruter hashBruter = new HashBruter();

                        hashBruter.setMinLength(Integer.parseInt(minimumLength.getText()));
                        hashBruter.setMaxLength(Integer.parseInt(maximumLength.getText()));

                        if (specialCharacters.isSelected()) {
                            hashBruter.addSpecialCharacters();
                        }
                        if (upperCaseCharacters.isSelected()) {
                            hashBruter.addUpperCaseLetters();
                        }
                        if (lowerCaseCharacters.isSelected()) {
                            hashBruter.addLowerCaseLetters();
                        }
                        if (numericCharacters.isSelected()) {
                            hashBruter.addDigits();
                        }
                        if (!exclude.getText().equals("")) {
                            hashBruter.excludeChars(exclude.getText());
                        }

                        hashBruter.setType((String) hashTypes.getSelectedItem());
                        hashBruter.setHash(hash.getText().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").trim());

                        // Begin the unhashing process
                        Thread thread = new Thread(new Runnable() { @Override public void run() { hashBruter.tryBruteForce(); } }, "Display brute force results");
                        thread.start();

                        while (!hashBruter.isDone() && !hashBruter.isFound() && !doStop[0]) {
                            hashBruter.setEndtime(System.nanoTime());

                            try {
                                // delay to update result panel
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                InjectionModel.LOGGER.error(e, e);
                            }
                            
                            result.setText("Current string: " + hashBruter.getPassword() + "\n");
                            result.append("Current hash: " + hashBruter.getGeneratedHash() + "\n\n");
                            result.append("Number of possibilities: " + hashBruter.getNumberOfPossibilities() + "\n");
                            result.append("Checked hashes: " + hashBruter.getCounter() + "\n");
                            result.append("Estimated hashes left: " + hashBruter.getRemainder() + "\n");
                            result.append("Per second: " + hashBruter.getPerSecond() + "\n\n");
                            result.append( hashBruter.calculateTimeElapsed() + "\n");
                            
                            if (hashBruter.getPerSecond() != 0) {
                                result.append("Traversing remaining: " +
                                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f / 60.0f / 24f)) + "days " +
                                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f / 60f % 24)) + "h " +
                                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f % 60)) + "min " +
                                        Math.round((Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond()) % 60) + "s\n"); 
                            }
                            
                            result.append("Percent done: " + (100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities()) + "%");
                            
                            if (doStop[0]) {
                                hashBruter.setIsDone(true);
                                hashBruter.setFound(true);
                                break;
                            }
                        }

                        // Display the result
                        if (doStop[0]) {
                            result.append("\n\n*** Aborted\n");
                        } else if (hashBruter.isFound()) {
                            result.append("\n\nFound hash:\n"
                                    + hashBruter.getGeneratedHash() + "\n"
                                    + "String: " + hashBruter.getPassword());
                            
                            InjectionModel.LOGGER.info("Found hash:");
                            InjectionModel.LOGGER.info(hashBruter.getGeneratedHash());
                            InjectionModel.LOGGER.info("String: " + hashBruter.getPassword());
                        } else if (hashBruter.isDone()) {
                            result.append("\n\n*** Hash not found");
                        }
                        
                        doStop[0] = false;
                        loader.setVisible(false);
                        run.setText("Start");
                        run.setEnabled(true);
                    }
                }, "Start brute force").start();

            }
        }
    }
}

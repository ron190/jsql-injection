package com.jsql.view.manager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import com.jsql.view.bruteforce.HashBruter;

/**
 * Run a brute force attack.
 */
public class ActionBruteForce implements ActionListener {
    private BruteForceManager bruteForceManager;
    
    private Boolean doStop = false;
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(BruteForceManager.class);

    public ActionBruteForce(BruteForceManager bruteForceManager) {
        super();
        this.bruteForceManager = bruteForceManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if ("Stop".equals(bruteForceManager.run.getText())) {
            bruteForceManager.run.setEnabled(false);
            this.doStop = true;
        } else {
            try {
                Integer.parseInt(bruteForceManager.maximumLength.getText());
                Integer.parseInt(bruteForceManager.minimumLength.getText());
            } catch (NumberFormatException e) {
                bruteForceManager.result.setText("*** Incorrect length");
                return;
            }

            if ("".equals(bruteForceManager.hash.getText())) {
                bruteForceManager.result.setText("*** Empty hash");
                return;
            } else if (
                    !bruteForceManager.specialCharacters.isSelected()
                    && !bruteForceManager.upperCaseCharacters.isSelected()
                    && !bruteForceManager.lowerCaseCharacters.isSelected()
                    && !bruteForceManager.numericCharacters.isSelected()) {
                bruteForceManager.result.setText("*** Select a character range");
                return;
            } else if (Integer.parseInt(bruteForceManager.maximumLength.getText()) < Integer.parseInt(bruteForceManager.minimumLength.getText())) {
                bruteForceManager.result.setText("*** Incorrect minimum and maximum length");
                return;
            }

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // Reset the panel
                    bruteForceManager.run.setText("Stop");
                    bruteForceManager.loader.setVisible(true);
                    bruteForceManager.result.setText(null);

                    // Configure the hasher
                    final HashBruter hashBruter = new HashBruter();

                    hashBruter.setMinLength(Integer.parseInt(bruteForceManager.minimumLength.getText()));
                    hashBruter.setMaxLength(Integer.parseInt(bruteForceManager.maximumLength.getText()));

                    if (bruteForceManager.specialCharacters.isSelected()) {
                        hashBruter.addSpecialCharacters();
                    }
                    if (bruteForceManager.upperCaseCharacters.isSelected()) {
                        hashBruter.addUpperCaseLetters();
                    }
                    if (bruteForceManager.lowerCaseCharacters.isSelected()) {
                        hashBruter.addLowerCaseLetters();
                    }
                    if (bruteForceManager.numericCharacters.isSelected()) {
                        hashBruter.addDigits();
                    }
                    if (!"".equals(bruteForceManager.exclude.getText())) {
                        hashBruter.excludeChars(bruteForceManager.exclude.getText());
                    }

                    hashBruter.setType((String) bruteForceManager.hashTypes.getSelectedItem());
                    hashBruter.setHash(bruteForceManager.hash.getText().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").trim());

                    // Begin the unhashing process
                    Thread thread = new Thread(new Runnable() { 
                        @Override 
                        public void run() { 
                            hashBruter.tryBruteForce(); 
                        } 
                    }, "Display brute force results");
                    thread.start();

                    while (!hashBruter.isDone() && !hashBruter.isFound() && !ActionBruteForce.this.doStop) {
                        hashBruter.setEndtime(System.nanoTime());

                        try {
                            // delay to update result panel
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            LOGGER.error(e, e);
                        }

                        bruteForceManager.result.setText("Current string: " + hashBruter.getPassword() + "\n");
                        bruteForceManager.result.append("Current hash: " + hashBruter.getGeneratedHash() + "\n\n");
                        bruteForceManager.result.append("Number of possibilities: " + hashBruter.getNumberOfPossibilities() + "\n");
                        bruteForceManager.result.append("Checked hashes: " + hashBruter.getCounter() + "\n");
                        bruteForceManager.result.append("Estimated hashes left: " + hashBruter.getRemainder() + "\n");
                        bruteForceManager.result.append("Per second: " + hashBruter.getPerSecond() + "\n\n");
                        bruteForceManager.result.append( hashBruter.calculateTimeElapsed() + "\n");

                        if (hashBruter.getPerSecond() != 0) {
                            bruteForceManager.result.append("Traversing remaining: " +
                                    Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f / 60.0f / 24f)) + "days " +
                                    Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f / 60f % 24)) + "h " +
                                    Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f % 60)) + "min " +
                                    Math.round((Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond()) % 60) + "s\n"); 
                        }

                        bruteForceManager.result.append("Percent done: " + (100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities()) + "%");

                        if (ActionBruteForce.this.doStop) {
                            hashBruter.setIsDone(true);
                            hashBruter.setFound(true);
                            break;
                        }
                    }

                    // Display the result
                    if (ActionBruteForce.this.doStop) {
                        bruteForceManager.result.append("\n\n*** Aborted\n");
                    } else if (hashBruter.isFound()) {
                        bruteForceManager.result.append("\n\nFound hash:\n"
                                + hashBruter.getGeneratedHash() + "\n"
                                + "String: " + hashBruter.getPassword());

                        LOGGER.info("Found hash:");
                        LOGGER.info(hashBruter.getGeneratedHash());
                        LOGGER.info("String: " + hashBruter.getPassword());
                    } else if (hashBruter.isDone()) {
                        bruteForceManager.result.append("\n\n*** Hash not found");
                    }

                    ActionBruteForce.this.doStop = false;
                    bruteForceManager.loader.setVisible(false);
                    bruteForceManager.run.setText("Start");
                    bruteForceManager.run.setEnabled(true);
                }
            }, "Start brute force").start();

        }
    }
}
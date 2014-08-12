package com.jsql.view.manager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import com.jsql.view.bruteforce.HashBruter;

/**
 * Run a brute force attack.
 */
public class ActionBruteForce implements ActionListener, Runnable {
    private ManagerBruteForce bruteForceManager;
    
    private Boolean doStop = false;
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ManagerBruteForce.class);

    public ActionBruteForce(ManagerBruteForce bruteForceManager) {
        super();
        this.bruteForceManager = bruteForceManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if ("Stop".equals(bruteForceManager.run.getText())) {
            this.bruteForceManager.run.setEnabled(false);
            this.doStop = true;
        } else {
            try {
                Integer.parseInt(this.bruteForceManager.maximumLength.getText());
                Integer.parseInt(this.bruteForceManager.minimumLength.getText());
            } catch (NumberFormatException e) {
                this.bruteForceManager.result.setText("*** Incorrect length");
                return;
            }

            if ("".equals(this.bruteForceManager.hash.getText())) {
                this.bruteForceManager.result.setText("*** Empty hash");
                return;
            } else if (
                    !this.bruteForceManager.specialCharacters.isSelected()
                    && !this.bruteForceManager.upperCaseCharacters.isSelected()
                    && !this.bruteForceManager.lowerCaseCharacters.isSelected()
                    && !this.bruteForceManager.numericCharacters.isSelected()) {
                this.bruteForceManager.result.setText("*** Select a character range");
                return;
            } else if (Integer.parseInt(this.bruteForceManager.maximumLength.getText()) < Integer.parseInt(this.bruteForceManager.minimumLength.getText())) {
                this.bruteForceManager.result.setText("*** Incorrect minimum and maximum length");
                return;
            }

            new Thread(this, "Start brute force").start();

        }
    }

    @Override
    public void run() {
        // Reset the panel
        this.bruteForceManager.run.setText("Stop");
        this.bruteForceManager.loader.setVisible(true);
        this.bruteForceManager.result.setText(null);

        // Configure the hasher
        final HashBruter hashBruter = new HashBruter();

        hashBruter.setMinLength(Integer.parseInt(this.bruteForceManager.minimumLength.getText()));
        hashBruter.setMaxLength(Integer.parseInt(this.bruteForceManager.maximumLength.getText()));

        if (this.bruteForceManager.specialCharacters.isSelected()) {
            hashBruter.addSpecialCharacters();
        }
        if (this.bruteForceManager.upperCaseCharacters.isSelected()) {
            hashBruter.addUpperCaseLetters();
        }
        if (this.bruteForceManager.lowerCaseCharacters.isSelected()) {
            hashBruter.addLowerCaseLetters();
        }
        if (this.bruteForceManager.numericCharacters.isSelected()) {
            hashBruter.addDigits();
        }
        if (!"".equals(this.bruteForceManager.exclude.getText())) {
            hashBruter.excludeChars(this.bruteForceManager.exclude.getText());
        }

        hashBruter.setType((String) this.bruteForceManager.hashTypes.getSelectedItem());
        hashBruter.setHash(this.bruteForceManager.hash.getText().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").trim());

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

            this.bruteForceManager.result.setText("Current string: " + hashBruter.getPassword() + "\n");
            this.bruteForceManager.result.append("Current hash: " + hashBruter.getGeneratedHash() + "\n\n");
            this.bruteForceManager.result.append("Number of possibilities: " + hashBruter.getNumberOfPossibilities() + "\n");
            this.bruteForceManager.result.append("Checked hashes: " + hashBruter.getCounter() + "\n");
            this.bruteForceManager.result.append("Estimated hashes left: " + hashBruter.getRemainder() + "\n");
            this.bruteForceManager.result.append("Per second: " + hashBruter.getPerSecond() + "\n\n");
            this.bruteForceManager.result.append( hashBruter.calculateTimeElapsed() + "\n");

            if (hashBruter.getPerSecond() != 0) {
                this.bruteForceManager.result.append("Traversing remaining: " +
                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f / 60.0f / 24f)) + "days " +
                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f / 60f % 24)) + "h " +
                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond() / 60f % 60)) + "min " +
                        Math.round((Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond()) % 60) + "s\n"); 
            }

            this.bruteForceManager.result.append("Percent done: " + (100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities()) + "%");

            if (ActionBruteForce.this.doStop) {
                hashBruter.setIsDone(true);
                hashBruter.setFound(true);
                break;
            }
        }

        // Display the result
        if (ActionBruteForce.this.doStop) {
            this.bruteForceManager.result.append("\n\n*** Aborted\n");
        } else if (hashBruter.isFound()) {
            this.bruteForceManager.result.append("\n\nFound hash:\n"
                    + hashBruter.getGeneratedHash() + "\n"
                    + "String: " + hashBruter.getPassword());

            LOGGER.info("Found hash:");
            LOGGER.info(hashBruter.getGeneratedHash());
            LOGGER.info("String: " + hashBruter.getPassword());
        } else if (hashBruter.isDone()) {
            this.bruteForceManager.result.append("\n\n*** Hash not found");
        }

        ActionBruteForce.this.doStop = false;
        this.bruteForceManager.loader.setVisible(false);
        this.bruteForceManager.run.setText("Start");
        this.bruteForceManager.run.setEnabled(true);
    }
}
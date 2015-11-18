/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.bruteforce.HashBruter;

/**
 * Run a brute force attack.
 */
public class ActionBruteForce implements ActionListener, Runnable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ManagerBruteForce.class);

    private ManagerBruteForce bruteForceManager;
    
    private Boolean doStop = false;
    
    public ActionBruteForce(ManagerBruteForce bruteForceManager) {
        super();
        this.bruteForceManager = bruteForceManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (I18n.BRUTEFORCE_STOP.equals(bruteForceManager.run.getText())) {
            this.bruteForceManager.run.setEnabled(false);
            this.doStop = true;
        } else {
            try {
                Integer.parseInt(this.bruteForceManager.maximumLength.getValue().toString());
                Integer.parseInt(this.bruteForceManager.minimumLength.getValue().toString());
//                Integer.parseInt(this.bruteForceManager.maximumLength.getText());
//                Integer.parseInt(this.bruteForceManager.minimumLength.getText());
            } catch (NumberFormatException e) {
                this.bruteForceManager.result.setText("*** " + I18n.BRUTEFORCE_INCORRECT_LENGTH);
                return;
            }

            if ("".equals(this.bruteForceManager.hash.getText())) {
                this.bruteForceManager.result.setText("*** " + I18n.BRUTEFORCE_EMPTY_HASH);
                return;
            } else if (
                    !this.bruteForceManager.specialCharacters.isSelected()
                    && !this.bruteForceManager.upperCaseCharacters.isSelected()
                    && !this.bruteForceManager.lowerCaseCharacters.isSelected()
                    && !this.bruteForceManager.numericCharacters.isSelected()) {
                this.bruteForceManager.result.setText("*** " + I18n.BRUTEFORCE_CHARACTER_RANGE);
                return;
            } else if (Integer.parseInt(this.bruteForceManager.maximumLength.getValue().toString()) < Integer.parseInt(this.bruteForceManager.minimumLength.getValue().toString())) {
                this.bruteForceManager.result.setText("*** " + I18n.BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH);
                return;
            }

            new Thread(this, "Start brute force").start();

        }
    }

    @Override
    public void run() {
        // Reset the panel
        this.bruteForceManager.run.setText(I18n.BRUTEFORCE_STOP);
        this.bruteForceManager.loader.setVisible(true);
        this.bruteForceManager.result.setText(null);

        // Configure the hasher
        final HashBruter hashBruter = new HashBruter();

        hashBruter.setMinLength(Integer.parseInt(this.bruteForceManager.minimumLength.getValue().toString()));
        hashBruter.setMaxLength(Integer.parseInt(this.bruteForceManager.maximumLength.getValue().toString()));

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
            
            int selectionStart = this.bruteForceManager.result.getSelectionStart();
            int selectionEnd = this.bruteForceManager.result.getSelectionEnd();
            
            this.bruteForceManager.result.setText(I18n.BRUTEFORCE_CURRENT_STRING + ": " + hashBruter.getPassword() + "\n");
            this.bruteForceManager.result.append(I18n.BRUTEFORCE_CURRENT_HASH + ": " + hashBruter.getGeneratedHash() + "\n\n");
            this.bruteForceManager.result.append(I18n.BRUTEFORCE_POSSIBILITIES + ": " + hashBruter.getNumberOfPossibilities() + "\n");
            this.bruteForceManager.result.append(I18n.BRUTEFORCE_CHECKED_HASHES + ": " + hashBruter.getCounter() + "\n");
            this.bruteForceManager.result.append(I18n.BRUTEFORCE_ESTIMATED + ": " + hashBruter.getRemainder() + "\n");
            this.bruteForceManager.result.append(I18n.BRUTEFORCE_PERSECOND + ": " + hashBruter.getPerSecond() + "\n\n");
            this.bruteForceManager.result.append(hashBruter.calculateTimeElapsed() + "\n");

            if (hashBruter.getPerSecond() != 0) {
                Float remainingDuration = Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond();
                this.bruteForceManager.result.append(
                    I18n.BRUTEFORCE_TRAVERSING_REMAINING + ": " +
                    Math.round(Math.floor(remainingDuration / 60f / 60.0f / 24f))   + I18n.BRUTEFORCE_DAYS + " " +
                    Math.round(Math.floor(remainingDuration / 60f / 60f % 24))      + I18n.BRUTEFORCE_HOURS + " " +
                    Math.round(Math.floor(remainingDuration / 60f % 60))            + I18n.BRUTEFORCE_MINUTES + " " +
                    Math.round(remainingDuration % 60)                              + I18n.BRUTEFORCE_SECONDS + "\n"
                ); 
            }

            this.bruteForceManager.result.append(I18n.BRUTEFORCE_PERCENT_DONE
                    + ": " + (100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities()) + "%");

            this.bruteForceManager.result.setSelectionStart(selectionStart);
            this.bruteForceManager.result.setSelectionEnd(selectionEnd);
            
            if (ActionBruteForce.this.doStop) {
                hashBruter.setIsDone(true);
                hashBruter.setFound(true);
                break;
            }
        }

        // Display the result
        if (ActionBruteForce.this.doStop) {
            this.bruteForceManager.result.append("\n\n*** " + I18n.BRUTEFORCE_ABORTED + "\n");
        } else if (hashBruter.isFound()) {
            this.bruteForceManager.result.append("\n\n" + I18n.BRUTEFORCE_FOUND_HASH + ":\n" + hashBruter.getGeneratedHash() + " => " + hashBruter.getPassword());

            LOGGER.debug(I18n.BRUTEFORCE_FOUND_HASH + ": " + hashBruter.getGeneratedHash() + " => " + hashBruter.getPassword());
        } else if (hashBruter.isDone()) {
            this.bruteForceManager.result.append("\n\n*** " + I18n.BRUTEFORCE_HASH_NOT_FOUND);
        }

        ActionBruteForce.this.doStop = false;
        this.bruteForceManager.loader.setVisible(false);
        this.bruteForceManager.run.setText(I18n.BRUTEFORCE_START);
        this.bruteForceManager.run.setEnabled(true);
    }
}
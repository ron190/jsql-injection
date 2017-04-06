/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.manager.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.bruteforce.HashBruter;
import com.jsql.view.swing.manager.ManagerBruteForce;

/**
 * Run a brute force attack.
 */
public class ActionBruteForce implements ActionListener, Runnable {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private ManagerBruteForce bruteForceManager;
    
    private Boolean isStopped = false;
    
    public ActionBruteForce(ManagerBruteForce bruteForceManager) {
        this.bruteForceManager = bruteForceManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (bruteForceManager.run.getState() == StateButton.STOPPABLE) {
            this.bruteForceManager.run.setEnabled(false);
            this.isStopped = true;
        } else {
            try {
                Integer.parseInt(this.bruteForceManager.maximumLength.getValue().toString());
                Integer.parseInt(this.bruteForceManager.minimumLength.getValue().toString());
            } catch (NumberFormatException e) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_INCORRECT_LENGTH"));
                return;
            }

            if ("".equals(this.bruteForceManager.hash.getText())) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_EMPTY_HASH"));
                return;
            } else if (
                    !this.bruteForceManager.specialCharacters.isSelected()
                    && !this.bruteForceManager.upperCaseCharacters.isSelected()
                    && !this.bruteForceManager.lowerCaseCharacters.isSelected()
                    && !this.bruteForceManager.numericCharacters.isSelected()) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_CHARACTER_RANGE"));
                return;
            } else if (
                Integer.parseInt(this.bruteForceManager.maximumLength.getValue().toString()) < 
                Integer.parseInt(this.bruteForceManager.minimumLength.getValue().toString())
            ) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH"));
                return;
            }

            new Thread(this, "ThreadDisplayBruteForce").start();
        }
    }

    @Override
    public void run() {
        // Reset the panel
        this.bruteForceManager.run.setText(I18n.valueByKey("BRUTEFORCE_STOP"));
        this.bruteForceManager.run.setState(StateButton.STOPPABLE);
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
        new Thread(hashBruter::tryBruteForce, "ThreadRunBruteForce").start();

        while (!hashBruter.isDone() && !hashBruter.isFound() && !this.isStopped) {
            hashBruter.setEndtime(System.nanoTime());

            try {
                // delay to update result panel
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error("Interruption while sleeping for brute force", e);
                Thread.currentThread().interrupt();
            }
            
            int selectionStart = this.bruteForceManager.result.getSelectionStart();
            int selectionEnd = this.bruteForceManager.result.getSelectionEnd();
            
            this.bruteForceManager.result.setText(I18n.valueByKey("BRUTEFORCE_CURRENT_STRING") + ": " + hashBruter.getPassword() + "\n");
            this.bruteForceManager.result.append(I18n.valueByKey("BRUTEFORCE_CURRENT_HASH") + ": " + hashBruter.getGeneratedHash() + "\n\n");
            this.bruteForceManager.result.append(I18n.valueByKey("BRUTEFORCE_POSSIBILITIES") + ": " + hashBruter.getNumberOfPossibilities() + "\n");
            this.bruteForceManager.result.append(I18n.valueByKey("BRUTEFORCE_CHECKED_HASHES") + ": " + hashBruter.getCounter() + "\n");
            this.bruteForceManager.result.append(I18n.valueByKey("BRUTEFORCE_ESTIMATED") + ": " + hashBruter.getRemainder() + "\n");
            this.bruteForceManager.result.append(I18n.valueByKey("BRUTEFORCE_PERSECOND") + ": " + hashBruter.getPerSecond() + "\n\n");
            this.bruteForceManager.result.append(hashBruter.calculateTimeElapsed() + "\n");

            if (hashBruter.getPerSecond() != 0) {
                Float remainingDuration = Float.parseFloat(Long.toString(hashBruter.getRemainder())) / (float) hashBruter.getPerSecond();
                this.bruteForceManager.result.append(
                    I18n.valueByKey("BRUTEFORCE_TRAVERSING_REMAINING") + ": "
                    + Math.round(Math.floor(remainingDuration / 60f / 60.0f / 24f))   + I18n.valueByKey("BRUTEFORCE_DAYS") + " "
                    + Math.round(Math.floor(remainingDuration / 60f / 60f % 24))      + I18n.valueByKey("BRUTEFORCE_HOURS") + " "
                    + Math.round(Math.floor(remainingDuration / 60f % 60))            + I18n.valueByKey("BRUTEFORCE_MINUTES") + " "
                    + Math.round(remainingDuration % 60)                              + I18n.valueByKey("BRUTEFORCE_SECONDS") + "\n"
                ); 
            }

            this.bruteForceManager.result.append(
                I18n.valueByKey("BRUTEFORCE_PERCENT_DONE")
                + ": " 
                + (100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities()) 
                + "%"
            );

            this.bruteForceManager.result.setSelectionStart(selectionStart);
            this.bruteForceManager.result.setSelectionEnd(selectionEnd);
            
            if (this.isStopped) {
                hashBruter.setIsDone(true);
                hashBruter.setFound(true);
                break;
            }
        }

        // Display the result
        if (this.isStopped) {
            LOGGER.warn(I18n.valueByKey("BRUTEFORCE_ABORTED"));
        } else if (hashBruter.isFound()) {
            this.bruteForceManager.result.append(
                "\n\n"+ I18n.valueByKey("BRUTEFORCE_FOUND_HASH") +":\n"+ hashBruter.getGeneratedHash() +" => "+ hashBruter.getPassword()
            );

            LOGGER.debug(I18n.valueByKey("BRUTEFORCE_FOUND_HASH") +": "+ hashBruter.getGeneratedHash() +" => "+ hashBruter.getPassword());
        } else if (hashBruter.isDone()) {
            this.bruteForceManager.result.append(
                "\n\n"+ I18n.valueByKey("BRUTEFORCE_HASH_NOT_FOUND")
            );
            
            LOGGER.warn(I18n.valueByKey("BRUTEFORCE_HASH_NOT_FOUND"));
        }

        this.isStopped = false;
        this.bruteForceManager.loader.setVisible(false);
        this.bruteForceManager.run.setText(I18n.valueByKey("BRUTEFORCE_START"));
        this.bruteForceManager.run.setEnabled(true);
        this.bruteForceManager.run.setState(StateButton.STARTABLE);
    }
    
}
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

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.i18n.I18nView;
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
    
    private boolean isStopped = false;
    
    public ActionBruteForce(ManagerBruteForce bruteForceManager) {
        this.bruteForceManager = bruteForceManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (this.bruteForceManager.getRun().getState() == StateButton.STOPPABLE) {
            this.bruteForceManager.getRun().setEnabled(false);
            this.isStopped = true;
        } else {
            if (
                !this.bruteForceManager.getMaximumLength().getValue().toString().matches("^\\d+$")
                || !this.bruteForceManager.getMinimumLength().getValue().toString().matches("^\\d+$")
            ) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_INCORRECT_LENGTH"));
                return;
            }

            if ("".equals(this.bruteForceManager.getHash().getText())) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_EMPTY_HASH"));
                return;
            } else if (
                    !this.bruteForceManager.getSpecialCharacters().isSelected()
                    && !this.bruteForceManager.getUpperCaseCharacters().isSelected()
                    && !this.bruteForceManager.getLowerCaseCharacters().isSelected()
                    && !this.bruteForceManager.getNumericCharacters().isSelected()) {
                LOGGER.warn(I18n.valueByKey("BRUTEFORCE_CHARACTER_RANGE"));
                return;
            } else if (
                Integer.parseInt(this.bruteForceManager.getMaximumLength().getValue().toString()) <
                Integer.parseInt(this.bruteForceManager.getMinimumLength().getValue().toString())
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
        this.bruteForceManager.getRun().setText(I18nView.valueByKey("BRUTEFORCE_STOP"));
        this.bruteForceManager.getRun().setState(StateButton.STOPPABLE);
        this.bruteForceManager.getLoader().setVisible(true);
        this.bruteForceManager.getResult().setText(null);

        // Configure the hasher
        final HashBruter hashBruter = new HashBruter();

        hashBruter.setMinLength(Integer.parseInt(this.bruteForceManager.getMinimumLength().getValue().toString()));
        hashBruter.setMaxLength(Integer.parseInt(this.bruteForceManager.getMaximumLength().getValue().toString()));

        if (this.bruteForceManager.getSpecialCharacters().isSelected()) {
            hashBruter.addSpecialCharacters();
        }
        if (this.bruteForceManager.getUpperCaseCharacters().isSelected()) {
            hashBruter.addUpperCaseLetters();
        }
        if (this.bruteForceManager.getLowerCaseCharacters().isSelected()) {
            hashBruter.addLowerCaseLetters();
        }
        if (this.bruteForceManager.getNumericCharacters().isSelected()) {
            hashBruter.addDigits();
        }
        if (!"".equals(this.bruteForceManager.getExclude().getText())) {
            hashBruter.excludeChars(this.bruteForceManager.getExclude().getText());
        }

        hashBruter.setType((String) this.bruteForceManager.getHashTypes().getSelectedItem());
        hashBruter.setHash(this.bruteForceManager.getHash().getText().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").trim());

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
            
            int selectionStart = this.bruteForceManager.getResult().getSelectionStart();
            int selectionEnd = this.bruteForceManager.getResult().getSelectionEnd();
            
            this.bruteForceManager.getResult().setText(I18n.valueByKey("BRUTEFORCE_CURRENT_STRING") + ": " + hashBruter.getPassword());
            this.append(this.bruteForceManager.getResult(), I18n.valueByKey("BRUTEFORCE_CURRENT_HASH") + ": " + hashBruter.getGeneratedHash() + "\n");
            this.append(this.bruteForceManager.getResult(), I18n.valueByKey("BRUTEFORCE_POSSIBILITIES") + ": " + hashBruter.getNumberOfPossibilities());
            this.append(this.bruteForceManager.getResult(), I18n.valueByKey("BRUTEFORCE_CHECKED_HASHES") + ": " + hashBruter.getCounter());
            this.append(this.bruteForceManager.getResult(), I18n.valueByKey("BRUTEFORCE_ESTIMATED") + ": " + hashBruter.getRemainder());
            this.append(this.bruteForceManager.getResult(), I18n.valueByKey("BRUTEFORCE_PERSECOND") + ": " + hashBruter.getPerSecond() + "\n");
            this.append(this.bruteForceManager.getResult(), hashBruter.calculateTimeElapsed());

            if (hashBruter.getPerSecond() != 0) {
                Float remainingDuration = Float.parseFloat(Long.toString(hashBruter.getRemainder())) / hashBruter.getPerSecond();
                this.append(this.bruteForceManager.getResult(), (
                    I18n.valueByKey("BRUTEFORCE_TRAVERSING_REMAINING") + ": "
                    + Math.round(Math.floor(remainingDuration / 60f / 60.0f / 24f))   + I18n.valueByKey("BRUTEFORCE_DAYS") + " "
                    + Math.round(Math.floor(remainingDuration / 60f / 60f % 24))      + I18n.valueByKey("BRUTEFORCE_HOURS") + " "
                    + Math.round(Math.floor(remainingDuration / 60f % 60))            + I18n.valueByKey("BRUTEFORCE_MINUTES") + " "
                    + Math.round(remainingDuration % 60)                              + I18n.valueByKey("BRUTEFORCE_SECONDS")
                ));
            }

            this.append(
                this.bruteForceManager.getResult(),
                I18n.valueByKey("BRUTEFORCE_PERCENT_DONE")
                + ": "
                + (100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities())
                + "%"
            );

            this.bruteForceManager.getResult().setSelectionStart(selectionStart);
            this.bruteForceManager.getResult().setSelectionEnd(selectionEnd);
            
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
            this.append(
                this.bruteForceManager.getResult(),
                "\n"+ I18n.valueByKey("BRUTEFORCE_FOUND_HASH") +":\n"+ hashBruter.getGeneratedHash() +" => "+ hashBruter.getPassword()
            );

            LOGGER.debug(I18n.valueByKey("BRUTEFORCE_FOUND_HASH") +": "+ hashBruter.getGeneratedHash() +" => "+ hashBruter.getPassword());
        } else if (hashBruter.isDone()) {
            this.append(
                this.bruteForceManager.getResult(),
                "\n"+ I18n.valueByKey("BRUTEFORCE_HASH_NOT_FOUND")
            );
            
            LOGGER.warn(I18n.valueByKey("BRUTEFORCE_HASH_NOT_FOUND"));
        }

        this.isStopped = false;
        this.bruteForceManager.getLoader().setVisible(false);
        this.bruteForceManager.getRun().setText(I18nView.valueByKey("BRUTEFORCE_START"));
        this.bruteForceManager.getRun().setEnabled(true);
        this.bruteForceManager.getRun().setState(StateButton.STARTABLE);
    }
    
    public void append(JTextPane l, String text) {
        try {
            l.getDocument().insertString(
                l.getDocument().getLength(),
                (l.getDocument().getLength() == 0 ? "" : "\n") + text,
                null
            );
        } catch (BadLocationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
}
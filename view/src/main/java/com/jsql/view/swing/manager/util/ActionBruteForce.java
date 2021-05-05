/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import java.util.Locale;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.util.bruter.HashBruter;
import com.jsql.view.swing.manager.ManagerBruteForce;
import com.jsql.view.swing.util.I18nViewUtil;

/**
 * Run a brute force attack.
 */
public class ActionBruteForce implements ActionListener, Runnable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

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

            if (StringUtils.isEmpty(this.bruteForceManager.getHash().getText())) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, () -> I18nUtil.valueByKey("BRUTEFORCE_EMPTY_HASH"));
                return;
                
            } else if (this.isRangeNotSelected()) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, () -> I18nUtil.valueByKey("BRUTEFORCE_CHARACTER_RANGE"));
                return;
                
            } else if (this.isLengthNotValid()) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, () -> I18nUtil.valueByKey("BRUTEFORCE_INCORRECT_MIN_MAX_LENGTH"));
                return;
            }

            new Thread(this, "ThreadDisplayBruteForce").start();
        }
    }

    private boolean isLengthNotValid() {
        
        return
            Integer.parseInt(this.bruteForceManager.getMaximumLength().getValue().toString())
            < Integer.parseInt(this.bruteForceManager.getMinimumLength().getValue().toString());
    }

    private boolean isRangeNotSelected() {
        
        return
            !this.bruteForceManager.getSpecialCharacters().isSelected()
            && !this.bruteForceManager.getUpperCaseCharacters().isSelected()
            && !this.bruteForceManager.getLowerCaseCharacters().isSelected()
            && !this.bruteForceManager.getNumericCharacters().isSelected();
    }

    @Override
    public void run() {
        
        // Reset the panel
        this.bruteForceManager.getRun().setText(I18nViewUtil.valueByKey("BRUTEFORCE_STOP"));
        this.bruteForceManager.getRun().setState(StateButton.STOPPABLE);
        this.bruteForceManager.getLoader().setVisible(true);
        this.bruteForceManager.getResult().setText(null);

        // Configure the hasher
        final var hashBruter = new HashBruter();

        this.initializeBruter(hashBruter);

        // Begin the reverse hashing process
        new Thread(hashBruter::tryBruteForce, "ThreadRunBruteForce").start();

        while (!hashBruter.isDone() && !hashBruter.isFound() && !this.isStopped) {
            
            hashBruter.setEndtime(System.nanoTime());

            try {
                // delay to update result panel
                Thread.sleep(1000);
                
            } catch (InterruptedException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                Thread.currentThread().interrupt();
            }
            
            int selectionStart = this.bruteForceManager.getResult().getSelectionStart();
            int selectionEnd = this.bruteForceManager.getResult().getSelectionEnd();
            
            this.updateResult(hashBruter);

            this.bruteForceManager.getResult().setSelectionStart(selectionStart);
            this.bruteForceManager.getResult().setSelectionEnd(selectionEnd);
            
            if (this.isStopped) {
                
                hashBruter.setIsDone(true);
                hashBruter.setFound(true);
                
                break;
            }
        }

        this.displayResult(hashBruter);

        this.isStopped = false;
        this.bruteForceManager.getLoader().setVisible(false);
        this.bruteForceManager.getRun().setText(I18nViewUtil.valueByKey("BRUTEFORCE_START"));
        this.bruteForceManager.getRun().setEnabled(true);
        this.bruteForceManager.getRun().setState(StateButton.STARTABLE);
    }

    private void updateResult(final HashBruter hashBruter) {
        
        this.bruteForceManager.getResult().setText(I18nUtil.valueByKey("BRUTEFORCE_CURRENT_STRING") + ": " + hashBruter.getPassword());
        this.append(this.bruteForceManager.getResult(), I18nUtil.valueByKey("BRUTEFORCE_CURRENT_HASH") + ": " + hashBruter.getGeneratedHash() + "\n");
        this.append(this.bruteForceManager.getResult(), I18nUtil.valueByKey("BRUTEFORCE_POSSIBILITIES") + ": " + hashBruter.getNumberOfPossibilities());
        this.append(this.bruteForceManager.getResult(), I18nUtil.valueByKey("BRUTEFORCE_CHECKED_HASHES") + ": " + hashBruter.getCounter());
        this.append(this.bruteForceManager.getResult(), I18nUtil.valueByKey("BRUTEFORCE_ESTIMATED") + ": " + hashBruter.getRemainder());
        this.append(this.bruteForceManager.getResult(), I18nUtil.valueByKey("BRUTEFORCE_PERSECOND") + ": " + hashBruter.getPerSecond() + "\n");
        this.append(this.bruteForceManager.getResult(), hashBruter.calculateTimeElapsed());

        if (hashBruter.getPerSecond() != 0) {
            
            float remainingDuration = Float.parseFloat(Long.toString(hashBruter.getRemainder())) / hashBruter.getPerSecond();
            
            this.append(this.bruteForceManager.getResult(), (
                I18nUtil.valueByKey("BRUTEFORCE_TRAVERSING_REMAINING") + ": "
                + Math.round(Math.floor(remainingDuration / 60f / 60.0f / 24f)) + I18nUtil.valueByKey("BRUTEFORCE_DAYS") + StringUtils.SPACE
                + Math.round(Math.floor(remainingDuration / 60f / 60f % 24))    + I18nUtil.valueByKey("BRUTEFORCE_HOURS") + StringUtils.SPACE
                + Math.round(Math.floor(remainingDuration / 60f % 60))          + I18nUtil.valueByKey("BRUTEFORCE_MINUTES") + StringUtils.SPACE
                + Math.round(remainingDuration % 60)                            + I18nUtil.valueByKey("BRUTEFORCE_SECONDS")
            ));
        }

        this.append(
            this.bruteForceManager.getResult(),
            String.format(
                "%s: %s%%",
                I18nUtil.valueByKey("BRUTEFORCE_PERCENT_DONE"),
                100 * (float) hashBruter.getCounter() / hashBruter.getNumberOfPossibilities()
            )
        );
    }

    private void initializeBruter(final HashBruter hashBruter) {
        
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
        
        if (StringUtils.isNotEmpty(this.bruteForceManager.getExclude().getText())) {
            
            hashBruter.excludeChars(this.bruteForceManager.getExclude().getText());
        }

        hashBruter.setType((String) this.bruteForceManager.getHashTypes().getSelectedItem());
        hashBruter.setHash(
            this.bruteForceManager
            .getHash()
            .getText()
            .toUpperCase(Locale.ROOT)
            .replaceAll(
                "[^a-zA-Z0-9]",
                StringUtils.EMPTY
            )
            .trim()
        );
    }

    private void displayResult(final HashBruter hashBruter) {
        
        // Display the result
        if (this.isStopped) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, () -> I18nUtil.valueByKey("BRUTEFORCE_ABORTED"));
            
        } else if (hashBruter.isFound()) {
            
            this.append(
                this.bruteForceManager.getResult(),
                String.format(
                    "%n%s:%n%s => %s",
                    I18nUtil.valueByKey("BRUTEFORCE_FOUND_HASH"),
                    hashBruter.getGeneratedHash(),
                    hashBruter.getPassword()
                )
            );

            LOGGER.log(
                LogLevel.CONSOLE_SUCCESS,
                "{}: {} => {}",
                () -> I18nUtil.valueByKey("BRUTEFORCE_FOUND_HASH"),
                hashBruter::getGeneratedHash,
                hashBruter::getPassword
            );
            
        } else if (hashBruter.isDone()) {
            
            this.append(
                this.bruteForceManager.getResult(),
                "\n"+ I18nUtil.valueByKey("BRUTEFORCE_HASH_NOT_FOUND")
            );
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, () -> I18nUtil.valueByKey("BRUTEFORCE_HASH_NOT_FOUND"));
        }
    }
    
    public void append(JTextPane textPane, String text) {
        
        try {
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (textPane.getDocument().getLength() == 0 ? StringUtils.EMPTY : "\n") + text,
                null
            );
            
        } catch (BadLocationException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}